package com.dudencov.currencyexchangerapp.di

import com.dudencov.currencyexchangerapp.data.Api
import com.dudencov.currencyexchangerapp.utils.Constants.API_URL
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiNetworkModule {
    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Singleton
    @Provides
    fun provideSerializationFactory(json: Json): Converter.Factory {
        val contentType = "application/json".toMediaType()
        return json.asConverterFactory(contentType)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder().apply {
            retryOnConnectionFailure(true)
            networkInterceptors().add(interceptor)
        }.build()

    @Singleton
    @Provides
    fun provideRetrofit(jsonFactory: Converter.Factory, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(jsonFactory)
            .client(client)
            .build()

    @Singleton
    @Provides
    fun provideApi(retrofit: Retrofit): Api = retrofit.create(Api::class.java)
}