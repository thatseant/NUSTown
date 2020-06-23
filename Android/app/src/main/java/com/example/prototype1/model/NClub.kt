package com.example.prototype1.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize //Parcelize necessary for safe args passing in Navigation Component
data class NClub constructor(var branchId: Int = 0, var catID: String = "", var catName: String = "", var id: String = "", var imgUrl: String = "", var info: String = "", var name: String = "",
                             var url: String = "", var websiteKey: String = "") : Parcelable

