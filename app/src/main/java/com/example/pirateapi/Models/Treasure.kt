package com.example.pirateapi.Models

import com.google.gson.annotations.SerializedName

data class Treasure(
// ANDROID CLUE 1: The map's legend is wrong! The C# JSON sends "coordinates" (lowercase).
// Fix the @SerializedName to match the JSON exactly.
    @SerializedName("coordinates")
    val coordinates: String,

    val secretMessage: String,

// ANDROID CLUE 2: Gold must be counted in whole numbers, not words!
// The C# API sends an integer. Change this data type or the app will crash when parsing.
    val goldCoins: Int
)