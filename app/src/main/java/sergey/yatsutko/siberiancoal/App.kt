package sergey.yatsutko.siberiancoal

import android.app.Application
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType

class App : Application() {


    companion object {
        // Цены за разные марки угля
        val prices = intArrayOf(1700, 1800, 1900, 2000)
        // Координаты разрезов
        val cuts = arrayOf(
            arrayOf(53.402971, 53.529799, 53.759367, 53.326586, 53.630114),
            arrayOf(91.083748, 91.410684, 91.061604, 91.361016, 91.436063)
        )

        const val MAPKIT_API_KEY = "a139146c-adfa-484c-abb6-5ce42284f64e"

        const val RESULT_NUMBER_LIMIT = 5

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