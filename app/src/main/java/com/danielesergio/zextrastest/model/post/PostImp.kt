package com.danielesergio.zextrastest.model.post

import kotlinx.serialization.Serializable

@Serializable
data class PostImp(override val id:Long? = null, override val userId:Long, override val title:String, override val body:String):Post