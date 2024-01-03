package com.vp.list.model

import com.google.gson.annotations.SerializedName
import com.vp.coreui.model.ListItem

private const val POSITIVE_RESPONSE = "True"

open class SearchResponse (
    @SerializedName("Response")
    val response: String,
    @SerializedName("Search")
    private var search: List<ListItem>? = emptyList(),
    val totalResults: Int = 0,
) {
    fun hasResponse(): Boolean {
        return POSITIVE_RESPONSE == response
    }

    fun getSearch() : List<ListItem> {
        return search ?: emptyList();
    }

}