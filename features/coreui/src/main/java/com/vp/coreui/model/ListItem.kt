package com.vp.coreui.model

import com.google.gson.annotations.SerializedName

data class ListItem (
    @SerializedName("Title") val title: String? = null,
    @SerializedName("Year") val year: String? = null,
    @JvmField val imdbID: String? = null,
    @JvmField @SerializedName("Poster") val poster: String? = null,
)