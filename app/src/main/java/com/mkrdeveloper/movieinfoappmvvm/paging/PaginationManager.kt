package com.mkrdeveloper.movieinfoappmvvm.paging

import retrofit2.Response

class PaginationManager<Key, Item>(
    private val initialPage: Key,
    private val onLoadUpdated: (Boolean) -> Unit,
    private val onRequest: suspend (nextPage: Key) -> Response<Item>,
    private val getNextKey: suspend (Item) -> Key,
    private val onError: suspend (Throwable?) -> Unit,
    private val onSuccess: suspend (items: Item, newPage: Key) -> Unit,

    ) : Pagination<Key, Item> {
    private var currentKey = initialPage
    private var isMakingRequest = false

    override suspend fun loadNextPage() {
        if (isMakingRequest) {
            return
        }
        isMakingRequest = true

        onLoadUpdated(true)
        try {
            val response = onRequest(currentKey)
            if (response.isSuccessful) {
                isMakingRequest = false
                val items = response.body()!!
                currentKey = getNextKey(items)!!
                onSuccess(items, currentKey)
                onLoadUpdated(false)
            }
        } catch (e: Exception) {
            onError(e)
            onLoadUpdated(false)
        }
    }

    override fun reset() {
        currentKey = initialPage
    }

}