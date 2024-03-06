package com.example.shopping.activities.repository

import com.example.shopping.activities.entities.City
import com.example.shopping.activities.entities.District
import com.example.shopping.activities.entities.Ward
import com.example.shopping.activities.network.CityService
import com.example.shopping.activities.utils.Resources
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class CityRepositoryImp @Inject constructor(
    private val cityService: CityService
) : CityRepository {
    override suspend fun getCity(): Resources<City> {
        return coroutineScope {
            async {
                try {
                    val result = cityService.getCities()
                    if (result.isSuccessful) {
                        val province = result.body()
                        if (province != null) {
                            Resources.Success(province)
                        } else
                            Resources.Failure(Exception("Empty"))
                    } else
                        Resources.Failure(Exception(result.message()))
                } catch (e: Exception) {
                    e.printStackTrace()
                    Resources.Failure(e)
                }
            }.await()
        }
    }

    override suspend fun getDistrict(cityId: String): Resources<District> {
        return coroutineScope {
            async {
                try {
                    val result = cityService.getDistrict(cityId)
                    if (result.isSuccessful) {
                        val district = result.body()
                        if (district != null) {
                            Resources.Success(district)
                        } else
                            Resources.Failure(Exception("Empty"))
                    } else
                        Resources.Failure(Exception(result.message()))
                } catch (e: Exception) {
                    e.printStackTrace()
                    Resources.Failure(e)
                }
            }.await()
        }
    }

    override suspend fun getWard(districtId: String): Resources<Ward> {
        return coroutineScope {
            async {
                try {
                    val result = cityService.getWard(districtId)
                    if (result.isSuccessful) {
                        val ward = result.body()
                        if (ward != null) {
                            Resources.Success(ward)
                        } else
                            Resources.Failure(Exception("Empty"))
                    } else
                        Resources.Failure(Exception(result.message()))
                } catch (e: Exception) {
                    e.printStackTrace()
                    Resources.Failure(e)
                }
            }.await()
        }
    }
}