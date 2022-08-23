package com.example.firebase.activity.models

import android.os.Parcel
import android.os.Parcelable

data class Board(
    var boardname:String="",
    var createdby:String="",
    var image:String="",

    var assignedto: ArrayList<String> =ArrayList(),
   var documentedId:String="",
    var taskslist:ArrayList<Task> = ArrayList()
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Task.CREATOR)!!


    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(boardname)
        parcel.writeString(createdby)
        parcel.writeString(image)
        parcel.writeStringList(assignedto)
        parcel.writeString(documentedId)
        parcel.writeTypedList(taskslist)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }
}
