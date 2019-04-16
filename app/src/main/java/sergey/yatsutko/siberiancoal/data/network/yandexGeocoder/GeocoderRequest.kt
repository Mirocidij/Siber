package sergey.yatsutko.siberiancoal.data.network.yandexGeocoder

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import sergey.yatsutko.siberiancoal.data.entity.HouseMeta

interface GeocoderRequest {
    @GET("/1.x/?format=json&apikey=17757be8-4817-4365-886c-d89845ac6976&geocode=")
    fun getHouseMeta(
        @Query("geocode") geocode: String
    ): Single<HouseMeta>
}