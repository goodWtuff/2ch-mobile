package com.dvach_2ch.a2ch.models.util

import com.google.gson.annotations.SerializedName

class MakePostResult(
    @SerializedName("Error")
    val error: String?,
    @SerializedName("Reason")
    val reason: String?
)