package com.dudencov.currencyexchangerapp.data

sealed class NetworkResult<out T : Any> {
    data class Success<out T : Any>(val code: Int, val data: T) : NetworkResult<T>()
    data class Error<out T : Any>(val code: Int, val errorMsg: String?) : NetworkResult<String>()
    data class Exception(val e: Throwable) : NetworkResult<Nothing>()
}

fun <T : Any> NetworkResult<T>.getOrNull(): T? =
    if (this is NetworkResult.Success) this.data else null
