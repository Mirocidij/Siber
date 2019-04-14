package sergey.yatsutko.siberiancoal.presentation.presenters.main

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.ArrayAdapter
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
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
import org.json.JSONObject
import sergey.yatsutko.siberiancoal.App
import sergey.yatsutko.siberiancoal.R
import sergey.yatsutko.siberiancoal.commons.hasConnection
import sergey.yatsutko.siberiancoal.commons.selectEntries
import sergey.yatsutko.siberiancoal.data.entity.CoalOrder
import sergey.yatsutko.siberiancoal.presentation.ui.SecondActivity

@InjectViewState
class MainPresenter(private val context: Context) : MvpPresenter<MainView>() {

    private val TAG = "MainPresenter"
    private var firmBool = false
    private var marker = true
    private val coalOrder: CoalOrder = CoalOrder()
    val RESULT_NUMBER_OF_LIST_VIEW_LIMIT = 5

    override fun onFirstViewAttach() {
        if (!hasConnection(context)) {
            viewState.showNetworkConnectionError()
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

        submitRequest()

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
        marker = false

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
            viewState.showRoadNotFoundError()
        }

        coalOrder.distance = (Math.round(distance) / 1000).toInt()

        updateCost()

        Log.d(TAG, "Расстояние ${coalOrder.distance} км")

    }

    fun inYandexErrorCallback(error: Error) {
        var errorMessage = context.getString(R.string.unknown_error_message)
        if (error is RemoteError) {
            errorMessage = context.getString(R.string.remote_error_message)
        } else if (error is NetworkError) {
            errorMessage = context.getString(R.string.network_error_message)
        }
        viewState.showYandexErrorToast(errorMessage = errorMessage)
    }

    fun nextActivityButtonWasPressed() {

        if (coalOrder.weight.toString() == "0" || coalOrder.weight.toString() == "00" || coalOrder.weight.toString().isEmpty()) {
            viewState.showIncorrectWeightError()
            return
        }

        if (coalOrder.distance == 0) {
            viewState.showIncorrectAddressError()
            return
        }

        val nextIntent = Intent(context, SecondActivity::class.java)

        viewState.openNewActivity(coalOrder = coalOrder)
    }

    fun goMapButtonWasPressed() {
        if (!hasConnection(context = context)) {
            viewState.showNetworkConnectionError()
            return
        }

        viewState.openNewActivityForResult()
    }

    fun onMapPlaceSelected(place: Intent?) {
        if (place == null) {
            return
        }

        coalOrder.routeEndLocation = doubleArrayOf(
            place.extras.getDouble("latitude"),
            place.extras.getDouble("longitude")
        )

        getAddress(
            latitude = coalOrder.routeEndLocation[0],
            longitude = coalOrder.routeEndLocation[1]
        )

        updateCost()
    }

    fun onSuggestResponseDone(suggest: List<SuggestItem>) {
        val listItems  = ArrayList<String>(RESULT_NUMBER_OF_LIST_VIEW_LIMIT)

        try {
            for (i in 0..Math.min(RESULT_NUMBER_OF_LIST_VIEW_LIMIT - 1, suggest.size)) {
                listItems.add(suggest[i].displayText!!)
            }
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }

        listItems.forEach {
            Log.d(TAG, it)
        }

        viewState.displaySearchResult(listItems)
    }

    fun weightWasChanged(weight: Int) {
        coalOrder.weight = weight
        coalOrder.distanceCost = try {
            when (coalOrder.weight) {
                in 1..3 -> 10
                in 4..7 -> 15
                in 8..20 -> 35
                in 21..40 -> 80
                else -> 0
            }
        } catch (e: Throwable) {
            0
        }

        updateCost()
    }

    private fun getCoordinates(address: String) {

        val queue = Volley.newRequestQueue(context)
        val url =
            "https://geocode-maps.yandex.ru/1.x/?format=json&geocode=$address&apikey=17757be8-4817-4365-886c-d89845ac6976"

        var isHouse: String

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->

                val jsonObject =
                    JSONObject(response)

                isHouse = jsonObject.getJSONObject("response")
                    .getJSONObject("GeoObjectCollection")
                    .getJSONArray("featureMember")
                    .getJSONObject(0)
                    .getJSONObject("GeoObject")
                    .getJSONObject("metaDataProperty")
                    .getJSONObject("GeocoderMetaData")
                    .getString("kind")


                if (isHouse == "house") {
                    val coordinates = jsonObject.getJSONObject("response")
                        .getJSONObject("GeoObjectCollection")
                        .getJSONArray("featureMember")
                        .getJSONObject(0)
                        .getJSONObject("GeoObject")
                        .getJSONObject("Point")
                        .getString("pos").split(" ")

                    coalOrder.routeEndLocation =
                        doubleArrayOf(coordinates[1].toDouble(), coordinates[0].toDouble())


                    submitRequest()
                } else {
                    coalOrder.distance = 0
                    updateCost()
                    viewState.showHouseNotFoundError()
                }
            },

            Response.ErrorListener {

            })

        queue.add(stringRequest)
    }

    private fun getAddress(latitude: Double, longitude: Double) {

        val queue = Volley.newRequestQueue(context)
        val url =
            "https://geocode-maps.yandex.ru/1.x/?format=json&geocode=$longitude,$latitude&apikey=17757be8-4817-4365-886c-d89845ac6976"
        var address: String
        var isHouse: String

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->

                val jsonObject =
                    JSONObject(response)

                isHouse = jsonObject.getJSONObject("response")
                    .getJSONObject("GeoObjectCollection")
                    .getJSONArray("featureMember")
                    .getJSONObject(0)
                    .getJSONObject("GeoObject")
                    .getJSONObject("metaDataProperty")
                    .getJSONObject("GeocoderMetaData")
                    .getString("kind")

                address = jsonObject.getJSONObject("response")
                    .getJSONObject("GeoObjectCollection")
                    .getJSONArray("featureMember")
                    .getJSONObject(0)
                    .getJSONObject("GeoObject")
                    .getJSONObject("metaDataProperty")
                    .getJSONObject("GeocoderMetaData")
                    .getString("text")

                val streetName = address.split(", ")
                if (streetName.size >= 3) {
                    address =
                        streetName[streetName.size - 3] + ", " + streetName[streetName.size - 2] + ", " + streetName[streetName.size - 1]
                }

//                marker = false
                coalOrder.address = address
                viewState.updateSearchBar(address = coalOrder.address)

                if (isHouse == "house") {
                    coalOrder.routeEndLocation = doubleArrayOf(latitude, longitude)
                    submitRequest()
                } else {
                    coalOrder.distance = 0

                    updateCost()
                    viewState.showHouseNotFoundError()
                }
            },
            Response.ErrorListener {
                address = "That didn't work!"
            })

        queue.add(stringRequest)

    }

    private fun submitRequest() {
        if (coalOrder.routeEndLocation[0] == 0.0) {
            coalOrder.distance = 0
            updateCost()
            return
        }

        val requestPoints = java.util.ArrayList<RequestPoint>()
        requestPoints.add(
            RequestPoint(
                Point(
                    coalOrder.routeStartLocation[0],
                    coalOrder.routeStartLocation[1]
                ),
                RequestPointType.WAYPOINT,
                null
            )
        )
        requestPoints.add(
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