package com.example.android3.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate
import java.util.*

interface APODAPI {
    @GET ("planetary/apod")

    fun getAPOD(
        @Query("api_key")
        api_key: String,
        @Query("date")
        date: LocalDate?
    ) : Call<APODDTO>
}