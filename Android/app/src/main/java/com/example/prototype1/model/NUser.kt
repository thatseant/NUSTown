package com.example.prototype1.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize //Parcelize necessary for safe args passing in Navigation Component
data class NUser constructor(var displayName: String = "", var email: String = "", var eventAttending: List<String> = emptyList()) : Parcelable
