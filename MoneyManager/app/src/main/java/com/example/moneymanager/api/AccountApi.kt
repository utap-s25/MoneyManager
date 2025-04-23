package com.example.moneymanager.api

import com.example.moneymanager.database.LocalAccount
import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface AccountApi {

    // Fetch accounts for a user
    @GET("users/{user_guid}/accounts")
    suspend fun fetchAccounts(
        @Path("user_guid") userGuid: String
    ): AccountResponse

    // Data class for account response
    data class AccountResponse(val accounts: List<LocalAccount>) // Ensure this is a List

    companion object {
        private fun buildGsonConverterFactory(): GsonConverterFactory {
            val gsonBuilder = GsonBuilder()
            return GsonConverterFactory.create(gsonBuilder.create())
        }

        var httpurl: HttpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("int-api.mx.com")
            .build()

        fun create(): AccountApi = create(httpurl)

        private fun create(httpUrl: HttpUrl): AccountApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BODY
                })
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Accept", "application/vnd.mx.api.v1+json")
                        .addHeader("Authorization", "Basic MWUwY2ZkZjYtMWE5Ny00NmVlLTlhMzctNzcwMTM1MWNkMjEzOmJjMmNhYjA0YjdiNDUyMDNjZGEwN2Q4ZGE3YjM5YjVlZmUxNGM5YmY=")
                        .build()
                    chain.proceed(request)
                }
                .build()

            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(buildGsonConverterFactory())
                .build()
                .create(AccountApi::class.java)
        }
    }
}
