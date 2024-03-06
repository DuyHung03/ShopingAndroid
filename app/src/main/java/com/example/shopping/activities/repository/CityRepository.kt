package com.example.shopping.activities.repository

import com.example.shopping.activities.entities.City
import com.example.shopping.activities.entities.District
import com.example.shopping.activities.entities.Ward
import com.example.shopping.activities.utils.Resources

interface CityRepository {
    suspend fun getCity(): Resources<City>
    suspend fun getDistrict(cityId: String): Resources<District>
    suspend fun getWard(districtId: String): Resources<Ward>
}