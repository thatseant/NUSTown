package com.example.prototype1.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize //Parcelize necessary for safe args passing in Navigation Component
data class NEvent constructor(var ID: String = "", var name: String = "", var time: String = "No Date", var category: String = "", var place: String = "", var rating: Int = 0,
                              var numberAttending: Int = 0, var url: String = "", var image: String = "", var info: String = ""): Parcelable

