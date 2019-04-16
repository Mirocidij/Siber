package sergey.yatsutko.siberiancoal.data.repository

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import sergey.yatsutko.siberiancoal.data.entity.HouseMeta
import sergey.yatsutko.siberiancoal.data.network.yandexGeocoder.GeocoderApiHolder
import sergey.yatsutko.siberiancoal.data.network.yandexGeocoder.GeocoeRequest

class GeocodeApiRepository(private val api: GeocoeRequest = GeocoderApiHolder.api) {

    fun getAddress(latitude: Double, longitude: Double): Single<HouseMeta> {
        return api.getHouseMeta("$latitude,$longitude")
            .subscribeOn(Schedulers.io())
    }

    fun getLatLng(address: String): Single<HouseMeta> {
        return api.getHouseMeta(address)
            .subscribeOn(Schedulers.io())
    }
}