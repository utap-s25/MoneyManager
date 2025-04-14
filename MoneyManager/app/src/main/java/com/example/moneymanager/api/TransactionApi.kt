package edu.cs371m.budget.api

import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.example.moneymanager.database.LocalTransaction as LocalTransaction

interface TransactionApi {

    @GET("users/{user_guid}/members/{member_guid}/transactions")
    suspend fun getTransactionsByMember(
        @Path("user_guid") userGuid: String,
        @Path("member_guid") memberGuid: String,
        @Query("from_date") fromDate: String? = null,
        @Query("to_date") toDate: String? = null,
        @Query("page") page: Int? = null,
        @Query("records_per_page") recordsPerPage: Int? = null
    ): TransactionResponse

    class TransactionResponse(val transactions: List<LocalTransaction>)

    companion object {
        private fun buildGsonConverterFactory(): GsonConverterFactory {
            val gsonBuilder = GsonBuilder()
            return GsonConverterFactory.create(gsonBuilder.create())
        }

        var httpurl: HttpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("api.mx.com")
            .build()

        fun create(): TransactionApi = create(httpurl)

        private fun create(httpUrl: HttpUrl): TransactionApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()

            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(buildGsonConverterFactory())
                .build()
                .create(TransactionApi::class.java)
        }
    }
}
