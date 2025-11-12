package com.danielesergio.zextrastest.model.post

import com.danielesergio.zextrastest.model.ValidationResult

object PostValidator{
    fun validateTitle(title:String?): ValidationResult {
        return if(title?.isNotBlank()?: false) ValidationResult.VALID else ValidationResult.INVALID_EMPTY_FIELD
    }
}