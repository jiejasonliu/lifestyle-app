package com.lifestyle.models

import com.lifestyle.interfaces.IUserProfile

class PartialUserProfile constructor(_username: String) : IUserProfile {

    companion object {
        // if a field is equal to one of these values, they should be ignored when doing updates
        // (i.e.) ProfileFormViewModel::updateFormPartial, UserViewModel::updateUserProfilePartial
        private const val DEFAULT_IGNORE_STRING = "#IGNORE#"
        private const val DEFAULT_IGNORE_INT = -1   // currently no ints should be negative

        fun String?.shouldIncludeInUpdate(): Boolean {
            return (this != DEFAULT_IGNORE_STRING)
        }

        fun Int?.shouldIncludeInUpdate(): Boolean {
            return (this != DEFAULT_IGNORE_INT)
        }
    }

    // must already exist and cannot be modified
    override val username: String = _username

    override var fullName: String? = DEFAULT_IGNORE_STRING
    override var age: Int? = DEFAULT_IGNORE_INT
    override var city: String? = DEFAULT_IGNORE_STRING
    override var state: String? = DEFAULT_IGNORE_STRING
    override var country: String? = DEFAULT_IGNORE_STRING
    override var height: Int? = DEFAULT_IGNORE_INT
    override var weight: Int? = DEFAULT_IGNORE_INT
    override var sex: String? = DEFAULT_IGNORE_STRING
    override var pictureURI: String? = DEFAULT_IGNORE_STRING
    override var weightChange: Int? = DEFAULT_IGNORE_INT
    override var stepGoal: Int? = DEFAULT_IGNORE_INT
    override var totalSteps: Int? = DEFAULT_IGNORE_INT
    override var todaysSteps: Int? = DEFAULT_IGNORE_INT
    override var dateOfTodaysSteps: Int? = DEFAULT_IGNORE_INT

}