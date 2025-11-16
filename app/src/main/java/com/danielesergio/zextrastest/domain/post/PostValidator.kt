package com.danielesergio.zextrastest.domain.post

import com.danielesergio.zextrastest.domain.ValidationResult

object PostValidator{
    fun validateTitle(title:String?): ValidationResult {
        return if(title?.isNotBlank()?: false) ValidationResult.VALID else ValidationResult.INVALID_EMPTY_FIELD
    }
}