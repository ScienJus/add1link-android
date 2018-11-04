package com.scienjus.add1link.client

import com.apollographql.apollo.ApolloClient
import okhttp3.OkHttpClient

object ApolloHttpClient {

    val instance: ApolloClient by lazy {
        val okHttp = OkHttpClient
                .Builder()
                .addInterceptor { chain ->
                    var request = chain.request()
                    AccountStorage.get()?.let { account ->
                        request = request.newBuilder()
                                .addHeader("authorization", account.token)
                                .build()
                    }
                    chain.proceed(request)
                }
                .build()
        ApolloClient.builder()
                .serverUrl("https://add1.link/graphql/")
                .okHttpClient(okHttp)
                .build()
    }
}