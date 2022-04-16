package com.lifestyle.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lifestyle.interfaces.IUserProfile

@Entity(tableName="users")
data class UserProfileEntity(
    @PrimaryKey
    @ColumnInfo(name="username") override val username: String,

    @ColumnInfo(name="fullName") override val fullName: String?,

    @ColumnInfo(name="age") override val age: Int?,

    @ColumnInfo(name="city") override val city: String?,

    @ColumnInfo(name="state") override val state: String?,

    @ColumnInfo(name="country") override val country: String?,

    @ColumnInfo(name="height") override val height: Int?,

    @ColumnInfo(name="weight") override val weight: Int?,

    @ColumnInfo(name="sex") override val sex: String?,

    @ColumnInfo(name="pictureURI") override val pictureURI: String? ,

    @ColumnInfo(name="weightChange") override val weightChange: Int?,

    @ColumnInfo(name="stepGoal") override val stepGoal: Int?,

    @ColumnInfo(name="totalSteps") override val totalSteps: Int?,

    @ColumnInfo(name = "todaysSteps") override val todaysSteps: Int?,

    @ColumnInfo(name = "dateOfTodaysSteps") override val dateOfTodaysSteps: Int?

    // extras
    // @ColumnInfo(name="loggedIn", defaultValue="false") val loggedIn: Boolean = false,
) : IUserProfile