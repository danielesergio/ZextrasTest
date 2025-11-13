package com.danielesergio.zextrastest.android.ui

import androidx.recyclerview.widget.RecyclerView
import com.danielesergio.zextrastest.android.state.PostState
import com.danielesergio.zextrastest.databinding.PostViewHolderLayoutBinding


/**
 * View Holder for a [PostState] RecyclerView list item.
 */
class PostViewHolder(
    private val binding: PostViewHolderLayoutBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: PostState) {
        binding.apply {
            binding.title.text = post.title
            binding.description.text = post.body
        }
    }
}