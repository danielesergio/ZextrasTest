package com.danielesergio.zextrastest.android.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.danielesergio.zextrastest.R
import com.danielesergio.zextrastest.android.Factory
import com.danielesergio.zextrastest.android.PostsViewModel
import com.danielesergio.zextrastest.databinding.ActivityMainBinding
import com.danielesergio.zextrastest.log.LoggerImpl

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val viewModel: PostsViewModel by viewModels { PostsViewModel.Factory }


    override val defaultViewModelCreationExtras: CreationExtras
        get() = MutableCreationExtras(super.defaultViewModelCreationExtras).apply {
            this[RepositoryCreationExtrasKey] = Factory.postRepository
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        LoggerImpl.startEndMethod(TAG, "onCreate"){
            super.onCreate(savedInstanceState)
            Factory.dir = applicationContext.filesDir
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            setSupportActionBar(binding.toolbar)
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            appBarConfiguration = AppBarConfiguration(navController.graph)
            setupActionBarWithNavController(navController, appBarConfiguration)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    companion object{
        private val TAG = MainActivity::class.java.simpleName
    }
}