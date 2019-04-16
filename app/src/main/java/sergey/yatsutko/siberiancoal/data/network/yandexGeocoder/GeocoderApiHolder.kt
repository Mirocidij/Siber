package sergey.yatsutko.siberiancoal.data.network.yandexGeocoder

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object GeocoderApiHolder {

    private const val BASE_URL = "https://geocode-maps.yandex.ru"

    val api: GeocoderRequest = initApi()

    private fun initApi() : GeocoderRequest {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocoderRequest::class.java)
    }
}