
package com.example.votersmanagementsystem.api

import com.example.votersmanagementsystem.model.FilterCriteria
import com.example.votersmanagementsystem.model.Voter
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("register_voter")
    fun registerVoter(@Body voter: Voter): Call<ResponseBody>


    @GET("get_voters")
    fun getVoters(): Call<List<Voter>>

    @GET("/filter_voters")
    fun filterVoters(
        @Query("min_age") minAge: Int?,
        @Query("max_age") maxAge: Int?,
        @Query("registered") registered: String?
    ): Call<List<Voter>>
}
