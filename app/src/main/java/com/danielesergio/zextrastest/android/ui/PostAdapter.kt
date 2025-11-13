package com.danielesergio.zextrastest.android.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.danielesergio.zextrastest.android.state.PostState
import com.danielesergio.zextrastest.databinding.PostViewHolderLayoutBinding


/**
 * Adapter for an [PostState] [List].
 */
class PostAdapter : PagingDataAdapter<PostState, PostViewHolder>(POST_DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder =
        PostViewHolder(
            PostViewHolderLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        )

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        if (post != null) {
            holder.bind(post)
        }
    }

    companion object {
        private val POST_DIFF_CALLBACK = object : DiffUtil.ItemCallback<PostState>() {
            override fun areItemsTheSame(oldItem: PostState, newItem: PostState): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PostState, newItem: PostState): Boolean =
                oldItem == newItem
        }
    }
}