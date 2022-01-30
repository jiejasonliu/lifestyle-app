package com.lifestyle.models

import com.lifestyle.interfaces.IUserProfile

data class EditProfileResult(
    val success: Boolean = false,
    val firstError: String = "<no error>",
    val userProfile: IUserProfile? = null
)
