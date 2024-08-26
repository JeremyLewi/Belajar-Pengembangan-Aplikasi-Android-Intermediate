package com.example.storyapp.view.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.model.UserPreference
import com.example.storyapp.view.ViewModelFactory
import com.example.storyapp.view.adapter.ListStoryAdapter
import com.example.storyapp.view.detail.DetailActivity
import com.example.storyapp.view.login.LoginActivity
import com.example.storyapp.view.upload.UploadActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory(UserPreference.getInstance(dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.setTitle(R.string.app_name)

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStory.addItemDecoration(itemDecoration)

        mainViewModel.listStory.observe(this) { story ->
            setStoryData(story)
        }
        mainViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        binding.fabAddStory.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
            finish()
        }

        mainViewModel.getLoginSession().observe(this) { isLogin ->
            if (isLogin) {
                mainViewModel.getToken().observe(this) { token ->
                    mainViewModel.getAllStory(token)

                }
            } else {
                startActivity(Intent(this, LoginActivity::class.java))

            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logoutButton) {
            mainViewModel.logout()
            finish()
        } else if (item.itemId == R.id.settingsButton) {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }



        return super.onOptionsItemSelected(item)


    }


    private fun setStoryData(story: List<Story>) {
        val listStoryAdapter = ListStoryAdapter(ArrayList(story))
        listStoryAdapter.setOnItemClickCallback(object :
            ListStoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Story) {
                Intent(this@MainActivity, DetailActivity::class.java).also {
                    it.putExtra(DetailActivity.EXTRA_STORY, data)
                    startActivity(it)
                }
            }
        })
        binding.rvStory.adapter = listStoryAdapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


}




