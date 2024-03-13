package com.example.shopping.activities.view.activities

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.shopping.R
import com.example.shopping.activities.adapter.CityAdapter
import com.example.shopping.activities.adapter.DistrictAdapter
import com.example.shopping.activities.adapter.WardAdapter
import com.example.shopping.activities.entities.Address
import com.example.shopping.activities.entities.City
import com.example.shopping.activities.entities.District
import com.example.shopping.activities.entities.Ward
import com.example.shopping.activities.helper.InputValidationHelper
import com.example.shopping.activities.helper.ValidationTextWatcher
import com.example.shopping.activities.utils.Resources
import com.example.shopping.activities.utils.Toast.toast
import com.example.shopping.activities.viewmodel.AuthViewModel
import com.example.shopping.activities.viewmodel.CityViewModel
import com.example.shopping.activities.viewmodel.DataViewModel
import com.example.shopping.databinding.ActivityAddNewAddressBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddNewAddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNewAddressBinding
    private val cityViewModel by viewModels<CityViewModel>()
    private var city: City.Result? = null
    private var district: District.Result? = null
    private var ward: Ward.Result? = null
    private lateinit var address: Address
    private val inputValidator = InputValidationHelper()
    private val dataViewModel by viewModels<DataViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_address)

        binding = ActivityAddNewAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent != null) {
            address = intent
                .getParcelableExtra<Address>("address") as Address
            val nameEditable = Editable.Factory.getInstance().newEditable(address.name)
            val phoneEditable = Editable.Factory.getInstance().newEditable(address.phoneNumber)
            val houseEditable = Editable.Factory.getInstance().newEditable(address.houseAddress)
            binding.etName.text = nameEditable
            binding.etPhoneNumber.text = phoneEditable
            binding.etHouseAddress.text = houseEditable
        }


        initializeListener()

    }


    private fun initializeListener() {
        //get city/province
        cityViewModel.getCity()

        binding.backButton.setOnClickListener {
            this.finish()
        }

        //assign to spinner city
        cityViewModel.cities.observe(this) { cities ->
            if (cities != null) {
                val list = mutableListOf<City.Result>()
                for (city in cities.results) {
                    list.add(city)
                }
                val adapter = CityAdapter(this, android.R.layout.simple_spinner_item, list)
                binding.spinnerCity.adapter = adapter
            }
        }


        //handle select item in spinner city
        binding.spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                //get city selected
                city = (adapterView?.adapter as? CityAdapter)?.getItem(position)
                city?.province_id?.let { cityId ->
                    cityViewModel.getDistrict(cityId.toString())
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        //observe changes in districts
        cityViewModel.district.observe(this) { districts ->
            districts?.let {
                val list = mutableListOf<District.Result>()
                for (district in districts.results) {
                    list.add(district)
                }
                val adapter = DistrictAdapter(
                    this@AddNewAddressActivity, android.R.layout.simple_spinner_item, list
                )
                binding.spinnerDistrict.adapter = adapter
            }
        }

        //handle select item in spinner district
        binding.spinnerDistrict.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    //get district selected
                    district = (adapterView?.adapter as? DistrictAdapter)?.getItem(position)
                    district?.district_id?.let { districtId ->
                        cityViewModel.getWard(districtId)
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }

        //observe changes in wards
        cityViewModel.ward.observe(this) { wards ->
            wards?.let {
                val list = mutableListOf<Ward.Result>()
                for (ward in wards.results) {
                    list.add(ward)
                }
                val adapter = WardAdapter(
                    this@AddNewAddressActivity, android.R.layout.simple_spinner_item, list
                )
                binding.spinnerWard.adapter = adapter
            }
        }

        //handle select item in spinner ward
        binding.spinnerWard.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                //get ward selected
                ward = (adapterView?.adapter as? WardAdapter)?.getItem(position)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        binding.saveButton.setOnClickListener {
            if (inputValidation()) {
                saveAddressToDb()
            }
        }

        //observe save address flow
        dataViewModel.saveAddressFlow.observe(this) { state ->
            when (state) {
                is Resources.Failure -> {
                    toast(state.e.message.toString())
                    binding.saveButton.revertAnimation()
                }

                Resources.Loading -> {
                    binding.saveButton.startAnimation()
                }

                is Resources.Success -> {
                    toast(state.result)
                    this.finish()
                }
            }
        }
    }

    private fun saveAddressToDb() {
        val address = Address(
            binding.etName.text.trim().toString(),
            binding.etPhoneNumber.text.toString().trim(),
            city?.province_name.toString(),
            district?.district_name.toString(),
            ward?.ward_name.toString(),
            binding.etHouseAddress.text.toString().trim(),
            false
        )

        val userId = authViewModel.currentUser?.uid!!

        dataViewModel.saveAddress(address, userId)

    }

    private fun inputValidation(): Boolean {
        //validation name
        return (inputValidator.isValidName(binding.etName.text.toString().trim()) {
            binding.nameLayout.error = it
            //validation phone number
        } && inputValidator.isVietnamesePhoneNumber(binding.etPhoneNumber.text.toString().trim()) {
            binding.phoneLayout.error = it
            //validation house address
        } && inputValidator.isValidAddress(binding.etHouseAddress.text.toString().trim()) {
            binding.houseAddressLayout.error = it
        })
    }

    override fun onResume() {
        super.onResume()

        val validationTextWatcher = ValidationTextWatcher(
            listOf(
                binding.nameLayout, binding.houseAddressLayout, binding.phoneLayout
            )
        )
        binding.etHouseAddress.addTextChangedListener(validationTextWatcher)
        binding.etName.addTextChangedListener(validationTextWatcher)
        binding.etHouseAddress.addTextChangedListener(validationTextWatcher)
    }

}