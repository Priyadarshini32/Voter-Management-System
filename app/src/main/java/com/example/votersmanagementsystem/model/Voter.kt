package com.example.votersmanagementsystem.model

import com.google.gson.annotations.SerializedName

data class Voter(
    val id: Int? = null,
    @SerializedName("name") val name: String,
    @SerializedName("age") val age: Int,
    @SerializedName("registered") val registered: Boolean  // Add registered field
)


data class FilterCriteria(
    val minAge: Int? = null,
    val maxAge: Int? = null,
    val registered: String? = null  // Use String ("true"/"false") instead of Boolean for Retrofit
)
