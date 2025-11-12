package com.danielesergio.zextrastest.android.state

data class PostMasterDetailsState(val posts:List<PostState> = emptyList(),
                                  val selectedItem: PostState? = null,
                                  val isPending: Boolean = true,
                                  val syncError: Boolean = false)
