package com.danielesergio.zextrastest.android.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.danielesergio.zextrastest.R
import com.danielesergio.zextrastest.android.PostsViewModel
import com.danielesergio.zextrastest.databinding.FragmentViewPostsBinding
import com.danielesergio.zextrastest.log.LoggerImpl
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ViewPostsFragment : Fragment() {

    private val viewModel: PostsViewModel by activityViewModels{
        PostsViewModel.Factory
    }
    private var _binding: FragmentViewPostsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = LoggerImpl.startEndMethod(TAG, "onCreateView"){
        _binding = FragmentViewPostsBinding.inflate(inflater, container, false)
        binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit =
        LoggerImpl.startEndMethod(TAG, "onViewCreated"){
            super.onViewCreated(view, savedInstanceState)

            binding.createNewPostFab.setOnClickListener { view ->
                findNavController().navigate(R.id.action_ViewFragment_to_CreatePostFragment)
            }

            val items = viewModel.items
            val postAdapter = PostAdapter()

            binding.bindAdapter(postAdapter = postAdapter)

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    items.collectLatest {
                        postAdapter.submitData(it)
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    postAdapter.loadStateFlow.collect { state ->
                        binding.createNewPostFab.isVisible = true
                        binding.prependProgress.isVisible = state.source.prepend is LoadState.Loading
                        binding.appendProgress.isVisible = state.source.append is LoadState.Loading


                        val wasRefreshing = binding.swipeRefresh.isRefreshing
                        val isRefreshing =  state.refresh is LoadState.Loading

                        binding.swipeRefresh.isRefreshing = isRefreshing
                        if(wasRefreshing && !isRefreshing){
                            binding.list.smoothScrollToPosition(0)
                        }
                    }
                }
            }

            postAdapter.addLoadStateListener { loadState ->

                if (loadState.hasError) {
                    binding.createNewPostFab.isVisible = false

                    snackbar = Snackbar.make(requireView(), "Connection error", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry") {
                            postAdapter.retry()
                        }.also { it.show() }
                }
            }

            binding.swipeRefresh.setOnRefreshListener {
                postAdapter.refresh()
                snackbar?.dismiss()
                snackbar = null
            }

        }
    private var snackbar: Snackbar? = null

    override fun onDestroyView():Unit = LoggerImpl.startEndMethod(TAG, "onDestroyView"){
        super.onDestroyView()
        _binding = null
    }

    private fun FragmentViewPostsBinding.bindAdapter(postAdapter: PostAdapter) {
        list.adapter = postAdapter
        list.layoutManager = LinearLayoutManager(list.context)
        val decoration = DividerItemDecoration(list.context, DividerItemDecoration.VERTICAL)
        list.addItemDecoration(decoration)
    }

    companion object{
        private val TAG = ViewPostsFragment::class.java.simpleName
    }
}