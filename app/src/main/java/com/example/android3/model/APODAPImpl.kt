package com.example.android3.model

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APODAPImpl {
    private val baseURL = "https://api.nasa.gov/"

    fun getAPODImpl(): APODAPI{
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
        return retrofit.create(APODAPI::class.java)
    }
}