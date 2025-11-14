package com.danielesergio.zextrastest.android.ui

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.danielesergio.zextrastest.R
import com.danielesergio.zextrastest.android.Factory
import com.danielesergio.zextrastest.android.PostsViewModel
import com.danielesergio.zextrastest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var viewModel: PostsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Factory.dir = applicationContext.filesDir
        viewModel = getViewModel()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun getViewModel(): PostsViewModel{
        val viewModelStoreOwner: ViewModelStoreOwner = this
        val myViewModel: PostsViewModel = ViewModelProvider.create(
            viewModelStoreOwner,
            factory = PostsViewModel.Factory,
            extras = MutableCreationExtras().apply {
                set(PostsViewModel.MY_REPOSITORY_KEY, PostPagingSource(Factory.postRepository))
            },
        )[PostsViewModel::class]

        return myViewModel
    }
}