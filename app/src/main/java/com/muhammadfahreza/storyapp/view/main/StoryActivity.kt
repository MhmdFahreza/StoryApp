package com.muhammadfahreza.storyapp.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.muhammadfahreza.storyapp.R
import com.muhammadfahreza.storyapp.data.response.ListStoryItem
import com.muhammadfahreza.storyapp.databinding.ActivityStoryBinding
import com.muhammadfahreza.storyapp.view.ViewModelFactory
import com.muhammadfahreza.storyapp.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class StoryActivity : AppCompatActivity() {
    private val viewModel by viewModels<StoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityStoryBinding
    private lateinit var storyAdapter: StoryAdapter

    private val tambahActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            lifecycleScope.launch {
                viewModel.getSession().collect { user ->
                    if (user.token.isNotEmpty()) {
                        refreshStories(user.token, skipCache = true)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            viewModel.getSession().collect { user ->
                if (!user.isLogin) {
                    startActivity(Intent(this@StoryActivity, WelcomeActivity::class.java))
                    finish()
                } else {
                    if (user.token.isNotEmpty()) {
                        refreshStories(user.token)
                    }
                }
            }
        }

        setupView()
        setupRecyclerView()

        viewModel.stories.observe(this) { storyList ->
            storyAdapter.submitList(storyList)
            updateRecyclerViewVisibility(storyList)
        }

        viewModel.loadStoriesFromDataStore()

        binding.menuIcon.setOnClickListener { showPopupMenu() }

        binding.addFab.setOnClickListener {
            val intent = Intent(this, TambahActivity::class.java)
            tambahActivityLauncher.launch(intent)
        }
    }

    private fun showPopupMenu() {
        val popup = PopupMenu(this, binding.menuIcon)
        popup.menuInflater.inflate(R.menu.menu_main, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    logout()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter { story ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_TITLE, story.name)
                putExtra(DetailActivity.EXTRA_DESCRIPTION, story.description)
                putExtra(DetailActivity.EXTRA_IMAGE_URL, story.photoUrl)
            }
            startActivity(intent)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@StoryActivity)
            adapter = storyAdapter
        }
    }

    private fun refreshStories(token: String, skipCache: Boolean = false) {
        lifecycleScope.launch {
            if (skipCache) {
                viewModel.fetchStories(token)
            } else {
                viewModel.loadStoriesFromDataStore(skipCache)
            }
        }
    }

    private fun updateRecyclerViewVisibility(storyList: List<ListStoryItem>) {
        if (storyList.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.emptyView.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyView.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            viewModel.clearLoginStatus()
        }
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }

}
