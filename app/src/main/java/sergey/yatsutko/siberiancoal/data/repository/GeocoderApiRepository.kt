package sergey.yatsutko.siberiancoal.data.repository

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import sergey.yatsutko.siberiancoal.data.entity.Address
import sergey.yatsutko.siberiancoal.data.entity.Position
import sergey.yatsutko.siberiancoal.data.network.yandexGeocoder.GeocoderApiHolder
import sergey.yatsutko.siberiancoal.data.network.yandexGeocoder.GeocoderRequest

class GeocoderApiRepository(private val api: GeocoderRequest = GeocoderApiHolder.api) {

    fun getAddress(latitude: Double, longitude: Double): Single<Address> {
        return api.getAddress("$longitude,$latitude")
            .subscribeOn(Schedulers.io())
    }

    fun getLatLng(address: String): Single<Position> {
        return api.getPosition(address)
            .subscribeOn(Schedulers.io())
    }
}