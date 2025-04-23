package com.example.moneymanager.api

import com.example.moneymanager.api.TransactionApi.CreateMemberBody
import com.example.moneymanager.api.TransactionApi.MemberResponse
import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BudgetsApi {

    // Get budgets for a specific user
    @GET("users/{user_guid}/budgets")
    suspend fun getBudgetsByUser(
        @Path("user_guid") userGuid: String,
        @Query("page") page: Int? = null,
        @Query("records_per_page") recordsPerPage: Int? = null
    ): BudgetsResponse

    data class CreateBudgetWrapper(
        val budget: CreateBudgetBody
    )
    @POST("users/{user_guid}/budgets")
    suspend fun createNewBudget(
        @Path("user_guid") userGuid: String,
        @Body body: CreateBudgetWrapper
    ): BudgetResponse

    @DELETE("users/{user_guid}/budgets/{budget_guid}")
    suspend fun deleteBudget(
        @Path("user_guid") userGuid: String,
        @Path("budget_guid") budgetGuid: String,
    )

    // Data class for handling budget response
    data class Budget(
        val amount: Double,
        val category_guid: String,
        val created_at: String,
        val guid: String,
        val is_exceeded: Boolean,
        val is_off_track: Boolean,
        val metadata: String?,
        val name: String,
        val parent_guid: String?,
        val percent_spent: Double,
        val projected_spending: Double?,
        val revision: Int,
        val transaction_total: Double,
        val updated_at: String,
        val user_guid: String
    )

    data class Pagination(
        val current_page: Int,
        val per_page: Int,
        val total_entries: Int,
        val total_pages: Int
    )

    data class CreateBudgetBody(
        val amount: Double,
        val category_guid: String
    )

    data class BudgetResponse(
        val budget: Budget
    )

    data class BudgetsResponse(
        val budgets: List<Budget>,
        val pagination: Pagination
    )

    companion object {
        private fun buildGsonConverterFactory(): GsonConverterFactory {
            val gsonBuilder = GsonBuilder()
            return GsonConverterFactory.create(gsonBuilder.create())
        }

        var httpurl: HttpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("int-api.mx.com")
            .build()

        fun create(): BudgetsApi = create(httpurl)

        private fun create(httpUrl: HttpUrl): BudgetsApi {
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
                .create(BudgetsApi::class.java)
        }
    }
}