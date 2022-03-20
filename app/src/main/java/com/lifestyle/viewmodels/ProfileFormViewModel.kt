package com.lifestyle.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifestyle.interfaces.IUserProfile
import com.lifestyle.models.EditProfileResult
import com.lifestyle.models.PartialUserProfile
import com.lifestyle.models.PartialUserProfile.Companion.shouldIncludeInUpdate

class ProfileFormViewModel : ViewModel() {

    // fields data-binded with layout
    var username = MutableLiveData<String>()
    var fullName = MutableLiveData<String>()
    var age = MutableLiveData<String>()
    var city = MutableLiveData<String>()
    var state = MutableLiveData<String>()
    var country = MutableLiveData<String>()
    var sex = MutableLiveData<String>()
    var weight = MutableLiveData<String>()
    var heightFt = MutableLiveData<String>()
    var heightIn = MutableLiveData<String>()

    // fields not data-binded with layout
    var pfpUriString = MutableLiveData<String>()

    // modified after fields are validated (i.e. in validateFormFields())
    var validationResult = MutableLiveData<EditProfileResult>()

    /**
     * If we want to just update the user's profile picture URI, any not touch anything else:
     *
     *    // updates just the picture uri field and leaves all other fields alone
     *    updateFormPartial(PartialUserProfile(username).apply {
     *       this.pictureURI = ".../some/path/to/the/picture"
     *    });
     */
    fun updateFormPartial(profile: PartialUserProfile) {
        // only write to UI if the data was actually touched (updated)
        if (profile.username.shouldIncludeInUpdate()) username.value = profile.username
        if (profile.fullName.shouldIncludeInUpdate()) fullName.value = profile.fullName
        if (profile.age.shouldIncludeInUpdate()) age.value = profile.age.toString()
        if (profile.city.shouldIncludeInUpdate()) city.value = profile.city
        if (profile.state.shouldIncludeInUpdate()) state.value = profile.state
        if (profile.country.shouldIncludeInUpdate()) country.value = profile.country
        if (profile.sex.shouldIncludeInUpdate()) sex.value = profile.sex
        if (profile.weight.shouldIncludeInUpdate()) weight.value = profile.weight.toString()

        if (profile.height.shouldIncludeInUpdate()) {
            val feet = profile.height!! / 12
            val inches = profile.height!! % 12
            heightFt.value = feet.toString()
            heightIn.value = inches.toString()

        }

        if (profile.pictureURI.shouldIncludeInUpdate()) pfpUriString.value = profile.pictureURI
    }

    /**
     * Completely replace the form with data from the IUserProfile.
     * Useful for when the filling in the form with data from the logged in user.
     */
    fun updateFormFull(profile: IUserProfile) {
        clearForm()

        // write to UI regardless if the data was actually touched (updated)
        username.value = profile.username
        fullName.value = profile.fullName
        age.value = profile.age.toString()
        city.value = profile.city
        state.value = profile.state
        country.value = profile.country
        sex.value = profile.sex
        weight.value = profile.weight.toString()

        if (profile.height != null) {
            val feet = profile.height!! / 12
            val inches = profile.height!! % 12
            heightFt.value = feet.toString()
            heightIn.value = inches.toString()
        }
        else {
            heightFt.value = ""
            heightIn.value = ""
        }

        pfpUriString.value = profile.pictureURI
    }

    fun clearForm() {
        username.value = ""
        fullName.value = ""
        age.value = ""
        city.value = ""
        state.value = ""
        country.value = ""
        sex.value = ""
        weight.value = ""
        heightFt.value = ""
        heightIn.value = ""
        pfpUriString.value = ""
    }

    /**
     * Validate fields in the form, check `validationResult` for information regarding validation result.
     */
    fun validateFormFields() {
        // note: field lengths are limited via values in @strings/... (strings.xml)
        // the purpose here is to validate things that aren't possible via XML

        val textUsername = username.value.nullIfEmpty()
        val textFullName = fullName.value.nullIfEmpty()
        val textAge = age.value.nullIfEmpty()
        val textCity = city.value.nullIfEmpty()
        val textState = state.value.nullIfEmpty()
        val textCountry = country.value.nullIfEmpty()
        val textSex = sex.value.nullIfEmpty()
        val textWeight = weight.value.nullIfEmpty()
        val textHeightFt = heightFt.value.nullIfEmpty()
        val textHeightIn = heightIn.value.nullIfEmpty()
        val textPfpUriString = pfpUriString.value.nullIfEmpty()

        // username pass
        if (textUsername == null)
            return withErr("Username", "Must not be empty")
        else if (textUsername.length < 6)
            return withErr("Username", "Too short")

        // full name pass
        if (textFullName == null)
            return withErr("Full Name", "Must not be empty")
        else if (textFullName.length < 6)
            return withErr("Full Name", "Too short")

        // weight pass
        if (textWeight != null) {
            if (textWeight.toInt() < 1)
                return withErr("Weight", "Must be greater than 1")
        }

        // height pass
        // ft.
        if (textHeightFt != null) {    // validate only if it has contents
            if (textHeightFt.toInt() < 0 || textHeightFt.toInt() > 11)
                return withErr("Height (ft)", "Must be between 0-11")
        }

        // in.
        if (textHeightIn != null) {    // validate only if it has contents
            if (textHeightIn.toInt() < 0 || textHeightIn.toInt() > 11)
                return withErr("Height (in)", "Must be between 0-11")
        }

        // if one is filled, the other should be also filled
        if ((textHeightFt != null) xor (textHeightIn != null)) {
            return if (textHeightFt == null)
                withErr("Height (ft)", "Don't leave feet blank if inches is filled")
            else
                withErr("Height (in)", "Don't leave inches blank if feet is filled")
        }

        // create PartialUserProfile containing just the user profile data from the form to serialize
        val validatedProfile = PartialUserProfile(username.value!!).apply {
            fullName = textFullName
            age = textAge?.toIntOrNull()
            city = textCity
            state = textState
            country = textCountry
            sex = textSex
            weight = textWeight?.toIntOrNull()
            height = null
            val heightFtValue = textHeightFt?.toIntOrNull()
            val heightInValue = textHeightIn?.toIntOrNull()
            if (heightFtValue != null && heightInValue != null) {
                height = (12 * heightFtValue.toInt()) + heightInValue.toInt()
            }
            pictureURI = textPfpUriString
        }

        validationResult.value = EditProfileResult(
            success = true,
            partialUserProfile = validatedProfile
        )
    }

    /**
     * `String?` Extension function:
     *  Returns the string or null if it was empty.
     */
    private fun String?.nullIfEmpty(): String? {
        if (this.isNullOrBlank()) {
           return null
        }
        return this
    }


    private fun withErr(atViewName: String, errorDetails: String) {
        validationResult.value = EditProfileResult(
            success = false,
            firstError = "$atViewName: $errorDetails",
            partialUserProfile = null,
        )
    }
}