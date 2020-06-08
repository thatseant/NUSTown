package com.example.prototype1.model;

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize //Parcelize necessary for safe args passing in Navigation Component
data class NEvent constructor(var ID: String = "", var category: String = "", var name: String = "", val place: String = "", val rating: Int = 0, val image: String = ""): Parcelable {
}

