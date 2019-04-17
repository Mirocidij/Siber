package sergey.yatsutko.siberiancoal.presentation.presenters.main

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.ArrayAdapter
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.geometry.Geo
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.SuggestItem
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import io.reactivex.android.schedulers.AndroidSchedulers
import sergey.yatsutko.siberiancoal.App
import sergey.yatsutko.siberiancoal.R
import sergey.yatsutko.siberiancoal.commons.hasConnection
import sergey.yatsutko.siberiancoal.commons.selectEntries
import sergey.yatsutko.siberiancoal.data.entity.CoalOrder
import sergey.yatsutko.siberiancoal.data.repository.GeocoderApiRepository

@InjectViewState
class MainPresenter(
    private val context: Context
) : MvpPresenter<MainView>() {

    private val repository: GeocoderApiRepository = GeocoderApiRepository()

    private val TAG = "MainPresenter"
    private var firmBool = false
    private val coalOrder: CoalOrder = CoalOrder()
    val RESULT_NUMBER_OF_LIST_VIEW_LIMIT = 5

    override fun onFirstViewAttach() {
        if (!hasConnection(context)) {
            viewState.showValidationError(R.string.error, R.string.networkConnectionError)
        }
    }

    fun firmSpinnerWasChanged(i: Int, selectedItem: String) {

        coalOrder.coalFirm = selectedItem

        if (firmBool) {
            val adapter: ArrayAdapter<CharSequence> = when (i) {
                0 -> selectEntries(context, R.array.Arshanovsky)
                1 -> selectEntries(context, R.array.Beloyarsky)
                2 -> selectEntries(context, R.array.Chernogorsky)
                3 -> selectEntries(context, R.array.Vostochnobeysky)
                4 -> selectEntries(context, R.array.Izihsky)
                else -> selectEntries(context, R.array.Arshanovsky)
            }

            coalOrder.routeStartLocation = doubleArrayOf(App.cuts[0][i], App.cuts[1][i])
            viewState.changeCoalSpinnerEntries(adapter, i)
        }
        firmBool = true

        rebuildRoute()

        updateCost()

        Log.d(TAG, "Выбрана фирма: ${coalOrder.coalFirm}")
    }

    fun coalSpinnerWasChanged(i: Int, selectedItem: String) {
        coalOrder.coalMark = selectedItem
        coalOrder.pricePerTonn = App.prices[i]

        updateCost()

        Log.d(TAG, "Выбрана марка: ${coalOrder.coalMark}")
        Log.d(TAG, "Цена за тонну: ${coalOrder.pricePerTonn}")
    }

    fun resultWasClicked(result: String) {
        val streetName = result.split(", ")
        val street = if (streetName.size >= 3) {
            streetName[streetName.size - 3] + ", " + streetName[streetName.size - 2] + ", " + streetName[streetName.size - 1]
        } else {
            result
        }

        viewState.updateSearchBar(street)
        getCoordinates(street)
    }

    fun onDrivingRoutesDone(routes: MutableList<DrivingRoute>) {

        val points = java.util.ArrayList<Point>()
        var distance = 0.0
        if (routes.size > 0) {
            points.addAll(routes.get(0).getGeometry().getPoints())

            for (i in 0 until points.size - 1) {
                distance += Geo.distance(points[i], points[i + 1])
            }
        } else {
            viewState.showValidationError(R.string.error, R.string.roadNotFoundError)
        }

        coalOrder.distance = (Math.round(distance) / 1000).toInt()

        updateCost()

        Log.d(TAG, "Расстояние ${coalOrder.distance} км")

    }

    fun onYandexError(error: Error) {
        val errorMessage = when (error) {
            is RemoteError -> context.getString(R.string.remote_error_message)
            is NetworkError -> context.getString(R.string.network_error_message)
            else -> context.getString(R.string.unknown_error_message)
        }
        viewState.showYandexError(errorMessage = errorMessage)
    }

    fun nextActivityButtonWasPressed() {

        if (coalOrder.weight.toString() == "0" || coalOrder.weight.toString() == "00" || coalOrder.weight.toString().isEmpty()) {
            viewState.showValidationError(R.string.error, R.string.incorrectWeightError)
            return
        }

        if (coalOrder.distance == 0) {
            viewState.showValidationError(R.string.error, R.string.incorrectAddressError)
            return
        }

        viewState.openNewActivity(coalOrder = coalOrder)
    }

    fun goMapButtonWasPressed() {
        if (!hasConnection(context = context)) {
            viewState.showValidationError(R.string.error, R.string.networkConnectionError)
            return
        }

        viewState.openNewActivityForResult()
    }

    fun onMapPlaceSelected(place: Intent) {

        coalOrder.routeEndLocation = doubleArrayOf(
            place.extras.getDouble("latitude", 0.0),
            place.extras.getDouble("longitude")
        )

        getAddress(
            latitude = coalOrder.routeEndLocation[0],
            longitude = coalOrder.routeEndLocation[1]
        )


        updateCost()
    }

    fun onSuggestResponseDone(suggest: List<SuggestItem>) {
        val listItems = suggest.take(RESULT_NUMBER_OF_LIST_VIEW_LIMIT)
            .filter { it.displayText != null }
            .map { it.displayText!! }

        listItems.forEach {
            Log.d(TAG, it)
        }

        viewState.displaySearchResult(listItems)
    }

    fun weightWasChanged(weight: Int) {
        coalOrder.weight = weight
        coalOrder.distanceCost = when (coalOrder.weight) {
            in 1..3 -> 10
            in 4..7 -> 15
            in 8..20 -> 35
            in 21..40 -> 80
            else -> 0
        }


        updateCost()
    }

    private fun getCoordinates(address: String) {

        repository.getLatLng(address)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.d(TAG, it.kind)

                val isHouse = "house" == it.kind

                Log.d(TAG, it.point)

                val point = it.point.split(", ")

                if (isHouse) {
                    coalOrder.routeEndLocation = doubleArrayOf(point[0].toDouble(), point[1].toDouble())
                    rebuildRoute()
                } else {
                    coalOrder.distance = 0
                    updateCost()
                    viewState.showValidationError(R.string.error, R.string.choseHouseError)
                }
            }, {
                it.printStackTrace()
            })


    }

    private fun getAddress(latitude: Double, longitude: Double) {

        repository.getAddress(latitude = latitude, longitude = longitude)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.d(TAG, it.kind)

                val isHouse = "house" == it.kind

                Log.d(TAG, it.address)

                val address = it.address

                val streetName = address.split(", ")

                if (streetName.size >= 3) {
                    coalOrder.address =
                        streetName[streetName.size - 3] + ", " + streetName[streetName.size - 2] + ", " + streetName[streetName.size - 1]
                } else {
                    coalOrder.address = address
                }

                viewState.updateSearchBar(coalOrder.address)

                Log.d(TAG, "CoalOrder address: ${coalOrder.address}")

                if (isHouse) {
                    coalOrder.routeEndLocation = doubleArrayOf(latitude, longitude)
                    rebuildRoute()
                } else {
                    coalOrder.distance = 0

                    updateCost()
                    viewState.showValidationError(R.string.error, R.string.choseHouseError)
                }

            }, {
                it.printStackTrace()
            })
    }

    private fun rebuildRoute() {
        if (coalOrder.routeEndLocation[0] == 0.0) {
            coalOrder.distance = 0
            updateCost()
            return
        }

        val requestPoints = listOf(
            RequestPoint(
                Point(
                    coalOrder.routeStartLocation[0],
                    coalOrder.routeStartLocation[1]
                ),
                RequestPointType.WAYPOINT,
                null
            ),
            RequestPoint(
                Point(
                    coalOrder.routeEndLocation[0],
                    coalOrder.routeEndLocation[1]
                ),
                RequestPointType.WAYPOINT, null
            )
        )

        viewState.submitRequest(requestPoints = requestPoints)
    }

    private fun updateCost() {
        coalOrder.deliveryCost = coalOrder.distanceCost * coalOrder.distance
        coalOrder.overPrice = coalOrder.deliveryCost + coalOrder.pricePerTonn * coalOrder.weight

        viewState.updateCost(
            pricePerTon = coalOrder.pricePerTonn,
            overPrice = coalOrder.overPrice,
            deliveryCost = coalOrder.deliveryCost,
            distance = coalOrder.distance
        )

        Log.d(TAG, "Стоимость доставки: ${coalOrder.deliveryCost}")
        Log.d(TAG, "Полная стоимость: ${coalOrder.overPrice}")
    }
}