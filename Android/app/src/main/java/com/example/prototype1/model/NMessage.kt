package com.example.prototype1.model

import java.util.*

data class NMessage(
        val text: String = "",
        val user: String = "",
        val timestamp: Date = Date()
)