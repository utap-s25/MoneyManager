package com.example.moneymanager.api

import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

    @POST("users")
    suspend fun createUser(@Body body: CreateUserBody): CreateUserResponse

    @GET("institutions/{code}")
    suspend fun getInstitution(@Path("code") code: String): InstitutionResponse

    @POST("users/{user_guid}/members")
    suspend fun createTestMember(
        @Path("user_guid") userGuid: String,
        @Body body: CreateMemberBody
    ): MemberResponse

    @POST("users/{user_guid}/members/{member_guid}/aggregate")
    suspend fun aggregateMember(
        @Path("user_guid") userGuid: String,
        @Path("member_guid") memberGuid: String
    )

    // Data classes
    data class CreateUserBody(val user: Map<String, String>)
    data class CreateUserResponse(val user: MXUser)
    data class MXUser(val guid: String)

    data class InstitutionResponse(val institution: Institution)
    data class Institution(val code: String, val name: String, val credentials: List<Credential>)
    data class Credential(val guid: String, val displayName: String)

    data class CreateMemberBody(val member: Map<String, Any>)
    data class MemberResponse(val member: MXMember)
    data class MXMember(val guid: String)


    class TransactionResponse(val transactions: List<LocalTransaction>)

    companion object {
        private fun buildGsonConverterFactory(): GsonConverterFactory {
            val gsonBuilder = GsonBuilder()
            return GsonConverterFactory.create(gsonBuilder.create())
        }

        var httpurl: HttpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("int-api.mx.com")
            .build()

        fun create(): TransactionApi = create(httpurl)

        private fun create(httpUrl: HttpUrl): TransactionApi {
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
                .create(TransactionApi::class.java)
        }
    }
}
