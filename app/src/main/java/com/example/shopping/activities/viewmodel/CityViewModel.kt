package com.example.shopping.activities.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopping.activities.entities.City
import com.example.shopping.activities.entities.District
import com.example.shopping.activities.entities.Ward
import com.example.shopping.activities.repository.CityRepository
import com.example.shopping.activities.utils.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val cityRepository: CityRepository
) : ViewModel() {

    private val _cities: MutableLiveData<City?> = MutableLiveData()
    val cities: LiveData<City?> get() = _cities

    private val _district: MutableLiveData<District?> = MutableLiveData()
    val district: LiveData<District?> get() = _district

    private val _ward: MutableLiveData<Ward?> = MutableLiveData()
    val ward: LiveData<Ward?> get() = _ward

    fun getCity() {
        viewModelScope.launch {
            when (val res = cityRepository.getCity()) {
                is Resources.Success -> _cities.value = res.result
                else -> {} // Handle other cases if needed
            }
        }
    }

    fun getDistrict(provinceId: String) {
        viewModelScope.launch {
            when (val res = cityRepository.getDistrict(provinceId)) {
                is Resources.Success -> {
                    Log.d("TAG", "getDistrict: ${res.result.results.get(0).district_name}")
                    _district.value = res.result
                }
                else -> {} // Handle other cases if needed
            }
        }
    }

    fun getWard(districtId: String) {
        viewModelScope.launch {
            when (val res = cityRepository.getWard(districtId)) {
                is Resources.Success -> _ward.value = res.result
                else -> {} // Handle other cases if needed
            }
        }
    }

}
