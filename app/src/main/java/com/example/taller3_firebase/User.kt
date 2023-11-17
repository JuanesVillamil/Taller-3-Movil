package com.example.taller3_firebase

import android.os.Parcel
import android.os.Parcelable

data class User(
    val email: String,
    val firstName: String,
    val lastName: String,
    var latitude: String,
    var longitude: String,
    val state: String,
    val id: String
): Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(email)
        dest.writeString(firstName)
        dest.writeString(lastName)
        dest.writeString(latitude)
        dest.writeString(longitude)
        dest.writeString(state)
        dest.writeString(id)
    }

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "0.0",
        parcel.readString() ?: "0.0",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
