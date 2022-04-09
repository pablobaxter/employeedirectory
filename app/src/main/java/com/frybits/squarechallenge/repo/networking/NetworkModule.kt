package com.frybits.squarechallenge.repo.networking

import androidx.annotation.VisibleForTesting
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

// Ensures if the network response json has 'null' literals, we use the data class default values instead.
@VisibleForTesting
val DEFAULT_JSON_FORMAT = Json {
    coerceInputValues = true // A simple boolean, but quite powerful!
}

@Module
@InstallIn(SingletonComponent::class)
class OkHttpProvider {

    // Use a singleton OkHttpClient for everything
    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient()
    }
}

@Module
@InstallIn(SingletonComponent::class)
class RetrofitProvider {

    @OptIn(ExperimentalSerializationApi::class)
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient) // Reuse those resources!
            .baseUrl("https://s3.amazonaws.com/sq-mobile-interview/")
            .addConverterFactory(DEFAULT_JSON_FORMAT.asConverterFactory(MediaType.get("application/json")))
            .build()
    }

    @Singleton
    @Provides
    fun provideEmployeeApi(retrofit: Retrofit): EmployeeApi {
        return retrofit.create()
    }
}
