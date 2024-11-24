package com.muhammadfahreza.storyapp.view.main

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.muhammadfahreza.storyapp.databinding.ActivityTambahBinding
import com.muhammadfahreza.storyapp.view.ViewModelFactory
import com.muhammadfahreza.storyapp.view.createCustomTempFile
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class TambahActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTambahBinding
    private var currentPhotoPath: String? = null
    private var selectedImageFile: File? = null

    private val viewModel by lazy {
        ViewModelFactory.getInstance(this).create(StoryViewModel::class.java)
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }

    // Camera launcher
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentPhotoPath?.let { path ->
                selectedImageFile = File(path)
                val imageUri = Uri.fromFile(selectedImageFile)
                binding.imagePlaceholder.setImageURI(imageUri)
            }
        } else {
            Toast.makeText(this, "Operasi kamera dibatalkan.", Toast.LENGTH_SHORT).show()
        }
    }

    // Gallery launcher
    private val pickGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val filePath = uriToFile(uri, this)
            selectedImageFile = filePath
            binding.imagePlaceholder.setImageURI(uri)
        } else {
            Toast.makeText(this, "Tidak ada gambar yang dipilih.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()

        binding.btnCamera.setOnClickListener {
            openCamera()
        }

        binding.btnGallery.setOnClickListener {
            openGallery()
        }

        binding.btnUpload.setOnClickListener {
            uploadStory()
        }
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val deniedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (deniedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, deniedPermissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCamera() {
        val photoFile: File? = createCustomTempFile(this)
        photoFile?.let {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                it
            )
            currentPhotoPath = it.absolutePath
            takePictureLauncher.launch(photoURI)
        } ?: run {
            Toast.makeText(this, "Gagal membuat file sementara.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        pickGalleryLauncher.launch("image/*")
    }

    fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val myFile = createCustomTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg)!!
        val outputStream = FileOutputStream(myFile)
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private fun compressImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var quality = 80 // Mulai dengan kualitas 80%
        val outputStream = ByteArrayOutputStream()

        do {
            outputStream.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            quality -= 10 // Kurangi kualitas hingga ukuran file kecil
        } while (outputStream.toByteArray().size > 1024 * 500) // Pastikan ukuran < 500KB

        val compressedFile = File(file.parent, "compressed_${file.name}")
        FileOutputStream(compressedFile).use {
            it.write(outputStream.toByteArray())
        }
        return compressedFile
    }

    private fun uploadStory() {
        val file = selectedImageFile
        val descriptionText = binding.editDescription.text.toString()

        if (file != null && descriptionText.isNotEmpty()) {
            val compressedFile = compressImage(file) // Kompres file gambar
            val description = descriptionText.toRequestBody("text/plain".toMediaType())
            val imageFile = compressedFile.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart = MultipartBody.Part.createFormData(
                "photo",
                compressedFile.name,
                imageFile
            )

            lifecycleScope.launch {
                viewModel.getSession().collect { user ->
                    if (user.token.isNotEmpty()) {
                        val token = "Bearer ${user.token}"

                        binding.progressBar.visibility = View.VISIBLE
                        binding.btnUpload.isEnabled = false

                        viewModel.uploadStory(token, description, imageMultipart)
                            .observe(this@TambahActivity) { result ->
                                // Sembunyikan ProgressBar setelah selesai upload
                                binding.progressBar.visibility = View.GONE
                                binding.btnUpload.isEnabled = true

                                result.onSuccess {
                                    Toast.makeText(this@TambahActivity, "Upload berhasil!", Toast.LENGTH_SHORT).show()
                                    finish()
                                }.onFailure {
                                    Toast.makeText(this@TambahActivity, "Upload gagal: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this@TambahActivity, "Token tidak ditemukan. Harap login ulang.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Isi deskripsi dan pilih gambar terlebih dahulu!", Toast.LENGTH_SHORT).show()
        }
    }
}
