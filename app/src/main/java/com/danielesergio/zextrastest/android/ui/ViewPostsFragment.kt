package com.danielesergio.zextrastest.android.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.danielesergio.zextrastest.R
import com.danielesergio.zextrastest.databinding.FragmentViewPostsBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ViewPostsFragment : Fragment() {

    private var _binding: FragmentViewPostsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentViewPostsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createNewPostFab.setOnClickListener { view ->
            findNavController().navigate(R.id.action_ViewFragment_to_CreatePostFragment)

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}