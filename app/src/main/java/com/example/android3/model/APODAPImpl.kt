package com.example.android3.model

import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class APODAPImpl : APODAPI  {
    private val baseURL = "https://api.nasa.gov/"

    fun getAPODImpl(): APODAPI{
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
        return retrofit.create(APODAPI::class.java)
    }

    override fun getAPOD(api_key: String): Call<APODDTO> {
        TODO("Not yet implemented")
    }
}