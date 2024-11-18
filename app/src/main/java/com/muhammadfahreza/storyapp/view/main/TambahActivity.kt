package com.muhammadfahreza.storyapp.view.main

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.muhammadfahreza.storyapp.databinding.ActivityTambahBinding
import com.muhammadfahreza.storyapp.view.createCustomTempFile
import java.io.File

class TambahActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTambahBinding
    private var currentPhotoPath: String? = null

    companion object {
        private const val REQUEST_CODE_CAMERA = 100
        private const val REQUEST_CODE_GALLERY = 101
        private const val PERMISSION_REQUEST_CODE = 123
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
            requestPermissions(deniedPermissions.toTypedArray(), PERMISSION_REQUEST_CODE)
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
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            }
            if (cameraIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA)
            } else {
                Toast.makeText(this, "Kamera tidak tersedia.", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "Gagal membuat file sementara.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CAMERA -> {
                    currentPhotoPath?.let { path ->
                        val imageUri = Uri.fromFile(File(path))
                        binding.imagePlaceholder.setImageURI(imageUri)
                    }
                }
                REQUEST_CODE_GALLERY -> {
                    data?.data?.let { uri ->
                        binding.imagePlaceholder.setImageURI(uri)
                    }
                }
            }
        } else {
            Toast.makeText(this, "Operasi dibatalkan.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.any { it != PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Izin diperlukan untuk melanjutkan.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
