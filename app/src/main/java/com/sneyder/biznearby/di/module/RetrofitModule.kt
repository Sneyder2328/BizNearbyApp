@file:Suppress("unused")

package com.sneyder.biznearby.di.module

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sneyder.biznearby.data.api.BizNearbyApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class RetrofitModule {

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)// TODO add interceptor po provide for more time in case of a long request like aptos/noAptos request
            .addNetworkInterceptor(StethoInterceptor())
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun gson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl(BizNearbyApi.END_POINT)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun provideBizNearbyApi(retrofit: Retrofit): BizNearbyApi =
        retrofit.create(BizNearbyApi::class.java)
}