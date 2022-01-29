package com.lifestyle.interfaces

interface IUserProfile {
    val fullName: String    // no guarantees: culturally inclusive
    val age: Int
    val city: String
    val state: String
    val country: String
    val height: Int         // in inches (but UI can calculate ft+in)
    val weight: Int         // in lbs
    val sex: String
    val pictureURI: String
}