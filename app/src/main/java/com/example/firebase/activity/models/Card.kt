package com.example.firebase.activity.models

import android.os.Parcel
import android.os.Parcelable

data class Card(
    var title:String="",
    var createdby:String="",
    var assignedto:ArrayList<String> = ArrayList(),
    var mslectedColor:String="",
    var DueDate:String=""
):Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.readString()!!,
    ) {
    }



    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) = with(dest) {
        this!!.writeString(title)
        writeString(createdby)
        writeStringList(assignedto)
        writeString(mslectedColor)
        writeString(DueDate)
    }

    companion object CREATOR : Parcelable.Creator<Card> {
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }

        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }
}




