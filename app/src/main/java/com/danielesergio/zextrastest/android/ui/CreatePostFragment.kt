package com.danielesergio.zextrastest.android.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.danielesergio.zextrastest.R
import com.danielesergio.zextrastest.databinding.FragmentCreatePostBinding
import com.danielesergio.zextrastest.log.LoggerImpl

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LoggerImpl.startEndMethod(TAG, "onCreateView"){
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) =
        LoggerImpl.startEndMethod(TAG, "onViewCreated") {
            super.onViewCreated(view, savedInstanceState)
            binding.buttonCancel.setOnClickListener {
                findNavController().navigate(R.id.action_CreatePostFragment_to_ViewPostsFragment)
            }
        }

    override fun onDestroyView() =
        LoggerImpl.startEndMethod(TAG, "onDestroyView"){
            super.onDestroyView()
            _binding = null
        }

    companion object{
        private val TAG = CreatePostFragment::class.java.simpleName
    }
}