package com.example.prototype1.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize //Parcelize necessary for safe args passing in Navigation Component
data class NEvent constructor(@DocumentId var ID: String = "", var name: String = "", var time: Date = Date(), var category: String = "", var place: String = "", var rating: Int = 0,
                              var numberAttending: Int = 0, var url: String = "", var image: String = "", var info: String = "", var imgUrl: String = "", var org: String = "", var updates: Map<String, ArrayList<String>> = emptyMap(), var maxAttending: Int = 0, var usersAttending: List<String> = emptyList(), var orgUser: String = "") : Parcelable

