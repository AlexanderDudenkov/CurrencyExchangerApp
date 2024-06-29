package com.dudencov.currencyexchangerapp.data

import retrofit2.HttpException
import retrofit2.Response

suspend fun <T : Any> handleResponse(execute: suspend () -> Response<T>): NetworkResult<T> {
    return try {
        val response = execute()

        if (response.isSuccessful) {
            NetworkResult.Success(response.code(), response.body()!!)
        } else {
            NetworkResult.Error<String>(
                response.code(),
                response.errorBody()?.string()
            ) as NetworkResult<T>
        }
    } catch (e: HttpException) {
        NetworkResult.Error<String>(e.code(), e.message()) as NetworkResult<T>
    } catch (e: Throwable) {
        NetworkResult.Exception(e)
    }
}