package com.dzhucinski.seattleplaces.network

import com.dzhucinski.seattleplaces.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Denis Zhuchinski on 4/10/19.
 */
interface FoursquareService {

    @GET("/v2/venues/search")
    fun search(
        @Query("query") query: String,
        @Query("near") near: String,
        @Query("limit") limit: Int,
        @Query("client_id") clientId: String = BuildConfig.FOURSQUARE_CLIENT_ID,
        @Query("client_secret") clientSecret: String = BuildConfig.FOURSQUARE_CLIENT_SECRET,
        @Query("v") version: String = API_CLIENT_VERSION
    ): Call<SearchResponse>

    @GET("/v2/venues/{id}")
    fun details(
        @Path("id") id: String,
        @Query("client_id") clientId: String = BuildConfig.FOURSQUARE_CLIENT_ID,
        @Query("client_secret") clientSecret: String = BuildConfig.FOURSQUARE_CLIENT_SECRET,
        @Query("v") version: String = API_CLIENT_VERSION
    ): Call<DetailsApiResponse>

    companion object {
        const val API_CLIENT_URL = "https://api.foursquare.com"
        const val API_CLIENT_VERSION = "20180401"
    }
}