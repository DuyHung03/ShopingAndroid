package com.example.shopping.activities.network

import com.example.shopping.activities.entities.City
import com.example.shopping.activities.entities.District
import com.example.shopping.activities.entities.Ward
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CityService {
    @GET("province")
    suspend fun getCities(): Response<City>

    @GET("province/district/{province_id}")
    suspend fun getDistrict(
        @Path("province_id") id: String
    ): Response<District>

    @GET("province/ward/{district_id}")
    suspend fun getWard(
        @Path("district_id") id: String
    ): Response<Ward>

}