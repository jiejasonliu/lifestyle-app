package com.lifestyle.interfaces

interface IUserProfile {
    val username: String     // should be unique per user!
    val fullName: String     // no guarantees: culturally inclusive

    // rest are optional per the guidelines
    val age: Int?
    val city: String?
    val state: String?
    val country: String?
    val height: Int?         // in inches (but UI can calculate ft+in)
    val weight: Int?         // in lbs
    val sex: String?
    val pictureURI: String?
    val weightChange: Int?
}