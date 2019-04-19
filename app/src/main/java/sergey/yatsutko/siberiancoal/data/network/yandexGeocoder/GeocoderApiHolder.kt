package sergey.yatsutko.siberiancoal.data.network.yandexGeocoder

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import sergey.yatsutko.siberiancoal.App

object GeocoderApiHolder {

    private val gson = App.gson

    private const val BASE_URL = "https://geocode-maps.yandex.ru"

    val api: GeocoderRequest = initApi()

    private fun initApi() : GeocoderRequest {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(GeocoderRequest::class.java)
    }
}