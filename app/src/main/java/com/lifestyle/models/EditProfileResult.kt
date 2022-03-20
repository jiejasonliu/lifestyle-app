package com.lifestyle.models

data class EditProfileResult(
    val success: Boolean = false,
    val firstError: String = "<no error>",
    val partialUserProfile: PartialUserProfile? = null
)
