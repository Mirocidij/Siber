package sergey.yatsutko.siberiancoal.data.repository

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import sergey.yatsutko.siberiancoal.data.entity.geocoderPojo.GeocoderPojo
import sergey.yatsutko.siberiancoal.data.network.yandexGeocoder.GeocoderApiHolder
import sergey.yatsutko.siberiancoal.data.network.yandexGeocoder.GeocoderRequest

class GeocoderApiRepository(private val api: GeocoderRequest = GeocoderApiHolder.api) {

    fun getAddress(latitude: Double, longitude: Double): Single<GeocoderPojo> {
        return api.getHouseMeta("$latitude,$longitude")
            .subscribeOn(Schedulers.io())
    }

    fun getLatLng(address: String): Single<GeocoderPojo> {
        return api.getHouseMeta(address)
            .subscribeOn(Schedulers.io())
    }
}