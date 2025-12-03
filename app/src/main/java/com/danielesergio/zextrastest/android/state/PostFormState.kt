package com.danielesergio.zextrastest.android.state

data class PostFormState(
    val title: String = "",
    val body: String = "",
    val titleError: Int? = null ,
    val bodyError:Int? = null,
    val isPending: Boolean = false,
    val storingError: Int? = null
){
    val isFormValid: Boolean = titleError == null && bodyError == null
    val submitButtonEnabled: Boolean = isFormValid && !isPending
}