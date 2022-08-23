package com.example.firebase.activity.models

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter.writeLong
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter.writeString
import com.google.firestore.v1.Write

data class Users(
    val id:String="",
    val name:String="",
    val email:String="",
    val phone:Long=0,
    val image:String="",
    val fcmToken:String="",
    var slected:Boolean=false

)
    // parcebale is same as serialization but the main diff is that you can give custom serializtion methods in parc but not in serialization
    // in seriliazation implement methods serilaized oper automatically parc is better than serial
    : Parcelable {
    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readLong(),
        source.readString()!!,
        source.readString()!!,
        source.readBoolean()

    )

    override fun describeContents() = 0

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeString(email)
        writeString(image)
        writeLong(phone)
        writeString(fcmToken)
        writeBoolean(slected)

    }

    companion object CREATOR : Parcelable.Creator<Users> {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun createFromParcel(parcel: Parcel): Users {
            return Users(parcel)
        }

        override fun newArray(size: Int): Array<Users?> {
            return arrayOfNulls(size)
        }
    }
}

