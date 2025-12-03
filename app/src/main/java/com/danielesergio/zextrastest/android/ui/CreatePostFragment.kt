package com.danielesergio.zextrastest.android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.danielesergio.zextrastest.R
import com.danielesergio.zextrastest.android.viewmodel.PostFormViewModel
import com.danielesergio.zextrastest.databinding.FragmentCreatePostBinding
import com.danielesergio.zextrastest.log.LoggerImpl
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: PostFormViewModel by activityViewModels{
        PostFormViewModel.Factory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LoggerImpl.startEndMethod(TAG, "onCreateView"){
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit =
        LoggerImpl.startEndMethod(TAG, "onViewCreated") {
            super.onViewCreated(view, savedInstanceState)
            binding.buttonCancel.setOnClickListener {
                findNavController().navigate(R.id.action_CreatePostFragment_to_ViewPostsFragment)
            }

            binding.buttonAdd.setOnClickListener {
                viewModel.submit()
            }
            binding.editTextBody.addTextChangedListener { text ->
                viewModel.onBodyChanged(text.toString())
            }

            //todo fix name
            binding.titleEditText.addTextChangedListener{ text ->
                viewModel.onTitleChanged(text.toString())
            }

            viewLifecycleOwner.lifecycleScope.launch {
                var snackbar: Snackbar? = null

                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiState.collect {state ->
                        LoggerImpl.d(TAG, state.toString())

                        binding.buttonAdd.isEnabled = state.submitButtonEnabled

                        binding.titleInputLayout.error = state.titleError?.run{resources.getString(this)} ?: ""
                        binding.bodyInputLayout.error = state.bodyError?.run{resources.getString(this)} ?: ""

                        if(state.title.isEmpty() && state.body.isEmpty()){
                            binding.titleEditText.setText("")
                            binding.editTextBody.setText("")
                        }

                        if(state.isPending){
                            binding.loadingSpinner.visibility = View.VISIBLE
                            binding.loadingOverlayBackground.visibility = View.VISIBLE
                        } else {
                            binding.loadingSpinner.visibility = View.GONE
                            binding.loadingOverlayBackground.visibility = View.GONE
                        }

                        if(state.storingError!= null){
                            snackbar =Snackbar.make(requireView(), getString(state.storingError), Snackbar.LENGTH_SHORT)
                                .also { it.show() }
                        } else {
                            snackbar?.dismiss()
                        }
                    }

                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.postCreateEvent.collect { newPostCreate ->
                        if(newPostCreate){
                            Snackbar.make(requireView(), "Post created", Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
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