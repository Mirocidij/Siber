package sergey.yatsutko.siberiancoal

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import sergey.yatsutko.siberiancoal.data.entity.Address
import sergey.yatsutko.siberiancoal.data.entity.Position
import sergey.yatsutko.siberiancoal.data.network.yandexGeocoder.DeserializeAddress
import sergey.yatsutko.siberiancoal.data.network.yandexGeocoder.DeserializePosition

class App : Application() {

    companion object {

        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(Address::class.java, DeserializeAddress.route)
            .registerTypeAdapter(Position::class.java, DeserializePosition.route)
            .create()

        // Цены за разные марки угля
        val prices = intArrayOf(1700, 1800, 1900, 2000)
        // Координаты разрезов
        val cuts = arrayOf(
            arrayOf(53.402971, 53.529799, 53.759367, 53.326586, 53.630114),
            arrayOf(91.083748, 91.410684, 91.061604, 91.361016, 91.436063)
        )

        const val MAPKIT_API_KEY = "a139146c-adfa-484c-abb6-5ce42284f64e"

        private val CENTER = Point(53.721254, 91.443417)
        private const val BOX_SIZE = 0.2
        val BOUNDING_BOX = BoundingBox(
            Point(CENTER.latitude - BOX_SIZE, CENTER.longitude - BOX_SIZE),
            Point(CENTER.latitude + BOX_SIZE, CENTER.longitude + BOX_SIZE)
        )
        val SEARCH_OPTIONS = SearchOptions().setSearchTypes(
            SearchType.GEO.value or
                    SearchType.BIZ.value or
                    SearchType.TRANSIT.value
        )!!
    }



}