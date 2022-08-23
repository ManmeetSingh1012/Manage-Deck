package com.example.firebase.activity.models

import android.os.Parcel
import android.os.Parcelable

data class Selected(
    var id:String="",
    var image:String=""
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Selected> {
        override fun createFromParcel(parcel: Parcel): Selected {
            return Selected(parcel)
        }

        override fun newArray(size: Int): Array<Selected?> {
            return arrayOfNulls(size)
        }
    }
}
