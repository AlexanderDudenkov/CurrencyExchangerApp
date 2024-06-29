package com.dudencov.currencyexchangerapp.data

import android.util.Log
import com.dudencov.currencyexchangerapp.di.IO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    @IO
    private val ioDispatcher: CoroutineDispatcher,
    private val api: Api
) : RemoteDataSource {
    override suspend fun getData(): NetworkResult<Data> = withContext(ioDispatcher) {
        handleResponse { api.getData().also { Log.d(this::class.java.simpleName, "getData=$it") } }
    }
}