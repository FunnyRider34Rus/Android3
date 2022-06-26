package com.example.android3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android3.BuildConfig
import com.example.android3.model.APODAPImpl
import com.example.android3.model.APODDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class MainViewModel(
    private val liveData: MutableLiveData<APODState> = MutableLiveData(),
    private val apodImpl: APODAPImpl = APODAPImpl()
) : ViewModel() {

    fun getLiveData() : LiveData<APODState>{
        return liveData
    }

    fun sendServerRequest(date: LocalDate?){
        liveData.postValue(APODState.Loading(null))
        apodImpl.getAPODImpl().getAPOD(BuildConfig.NASA_API_KEY, date).enqueue(
            object : Callback<APODDTO> {
                override fun onResponse(call: Call<APODDTO>, response: Response<APODDTO>) {
                    if (response.isSuccessful&&response.body()!=null){
                        response.body()?.let {
                            liveData.postValue(APODState.Success(it))
                        }
                    }
                }

                override fun onFailure(call: Call<APODDTO>, t: Throwable) {
                    throw Exception("Ошибка получения данных")
                }
            }
        )
    }
}