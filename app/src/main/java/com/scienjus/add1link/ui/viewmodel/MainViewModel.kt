package com.scienjus.add1link.ui.viewmodel

import FeedQuery
import SaveLinkMutation
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.scienjus.add1link.client.ApolloHttpClient

class MainViewModel : ViewModel() {

    val links = MutableLiveData<List<FeedQuery.Link>>()

    val initializing = MutableLiveData<Boolean>()

    val loadingMore = MutableLiveData<Boolean>()

    val refreshing = MutableLiveData<Boolean>()

    fun refresh(init: Boolean) {
        val loading = if (init) initializing else refreshing
        loading.postValue(true)
        ApolloHttpClient.instance.query(FeedQuery.builder().limit(20).build())
                .enqueue(object : ApolloCall.Callback<FeedQuery.Data>() {
                    override fun onFailure(e: ApolloException) {
                        loading.postValue(false)
                        Log.v(this.javaClass.simpleName, "error is: ${e.message.toString()}")

                    }
                    override fun onResponse(response: Response<FeedQuery.Data>) {
                        loading.postValue(false)
                        response.data()?.feed()?.links()?.let {
                            links.postValue(it)
                        }
                    }
                })
    }

    fun addUrl(url: String) {
        initializing.postValue(true)
        ApolloHttpClient.instance.mutate(
                SaveLinkMutation.builder()
                        .url(url)
                        .build())
                .enqueue(object : ApolloCall.Callback<SaveLinkMutation.Data>() {
                    override fun onFailure(e: ApolloException) {
                        initializing.postValue(false)
                        Log.v(this.javaClass.simpleName, "error is: ${e.message.toString()}")
                    }

                    override fun onResponse(response: Response<SaveLinkMutation.Data>) {
                        initializing.postValue(false)
                        response.data()?.saveLink()?.apply {
                            links.postValue(listOf(FeedQuery.Link(__typename(), id(), url(), title(), createdAt())) + links.value!!)
                        }
                    }
                })

    }

    fun loadMore() {
        loadingMore.postValue(true)
        links.value?.lastOrNull()?.let {
            ApolloHttpClient.instance.query(FeedQuery.builder().limit(20).beforeTimestamp(it.createdAt()).build())
                    .enqueue(object : ApolloCall.Callback<FeedQuery.Data>() {
                        override fun onFailure(e: ApolloException) {
                            loadingMore.postValue(false)
                            Log.v(this.javaClass.simpleName, "error is: ${e.message.toString()}")

                        }
                        override fun onResponse(response: Response<FeedQuery.Data>) {
                            loadingMore.postValue(false)
                            response.data()?.feed()?.links()?.let {
                                links.postValue(links.value!! + it)
                            }
                        }
                    })
        }
    }
}