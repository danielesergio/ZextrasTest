package com.danielesergio.zextrastest.domain.post

import com.danielesergio.zextrastest.domain.ValidationResult

object PostValidator{
    fun validateTitle(title:String?): ValidationResult {
        return notEmptyField(title)
    }

    fun validateBody(body:String?): ValidationResult {
        return notEmptyField(body)
    }

    private fun notEmptyField(field:String?): ValidationResult{
        return if(field?.isNotBlank()?: false) ValidationResult.VALID else ValidationResult.INVALID_EMPTY_FIELD
    }

}