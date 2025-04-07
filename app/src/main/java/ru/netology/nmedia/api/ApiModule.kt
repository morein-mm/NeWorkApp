package ru.netology.nmedia.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {

    companion object {
        private const val BASE_URL = "${BuildConfig.BASE_URL}/api/"
    }

    @Singleton
    @Provides
    fun provideLogging(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
//                            level = HttpLoggingInterceptor.Level.NONE
        }
    }

    @Singleton
    @Provides
    fun provideOkHttp(
        logging: HttpLoggingInterceptor,
        appAuth: AppAuth,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.proceed(
                chain.run {
                    chain.request().newBuilder()
                        .addHeader("API-KEY", BuildConfig.SERVER_API_KEY)
                        .build()
                }
            )
        }
        .addInterceptor { chain ->
            chain.proceed(
                chain.run {
                    val token = appAuth.state.value?.token
                    if (token != null) {
                        this.request().newBuilder()
                            .addHeader("Authorization", token)
                            .build()
                    } else {
                        request()
                    }

                }
            )
        }
        .addInterceptor(logging)
        .build()

    @Singleton
    @Provides
    fun provideRetrofit(
        okhttp: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okhttp)
        .build()

    @Singleton
    @Provides
    fun provideApiService(
        retrofit: Retrofit
    ): ApiService = retrofit.create()
}