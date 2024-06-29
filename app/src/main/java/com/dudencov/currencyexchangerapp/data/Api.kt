package com.dudencov.currencyexchangerapp.data

import retrofit2.Response
import retrofit2.http.GET

interface Api {

    @GET("/tasks/api/currency-exchange-rates")
    suspend fun getData(): Response<Data>
}