package com.vp.coreui

import com.vp.coreui.ListState.ERROR
import com.vp.coreui.ListState.IN_PROGRESS
import com.vp.coreui.ListState.LOADED
import com.vp.coreui.model.ListItem
import java.util.Objects

class SearchResult private constructor(
    @JvmField val items: List<ListItem>,
    @JvmField val totalResult: Int,
    @JvmField val listState: ListState
) {

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as SearchResult
        return totalResult == that.totalResult && items == that.items && listState == that.listState
    }

    override fun hashCode(): Int {
        return Objects.hash(items, totalResult, listState)
    }

    companion object {
        @JvmStatic fun error(): SearchResult {
            return SearchResult(emptyList(), 0, ERROR)
        }

        @JvmStatic fun success(
            items: List<ListItem>,
            totalResult: Int
        ): SearchResult {
            return SearchResult(items, totalResult, LOADED)
        }

        @JvmStatic fun inProgress(): SearchResult {
            return SearchResult(emptyList(), 0, IN_PROGRESS)
        }
    }
}