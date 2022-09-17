package vn.ztech.software.foregroundservice

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Song(
    val name: String,
    val single: String,
    val uri: Int,
    val img: Int,
):Parcelable
