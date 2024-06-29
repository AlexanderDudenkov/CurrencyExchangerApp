package com.dudencov.currencyexchangerapp.data

interface RemoteDataSource {

    suspend fun getData(): NetworkResult<Data>
}