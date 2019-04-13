package sergey.yatsutko.siberiancoal.presentation.presenters.main

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
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
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import org.json.JSONObject
import sergey.yatsutko.siberiancoal.App
import sergey.yatsutko.siberiancoal.R
import sergey.yatsutko.siberiancoal.commons.hasConnection
import sergey.yatsutko.siberiancoal.commons.selectEntries
import sergey.yatsutko.siberiancoal.data.entity.Form
import sergey.yatsutko.siberiancoal.presentation.ui.MapsActivity
import sergey.yatsutko.siberiancoal.presentation.ui.SecondActivity

@InjectViewState
class MainPresenter(private val context: Context) : MvpPresenter<MainView>() {

    private val TAG = "MainPresenter"
    private var firmBool = false
    private var marker = true
    private val form: Form = Form()

    init {

    }

    fun firmSpinnerWasChanged(i: Int, selectedItem: String) {

        form.coalFirm = selectedItem

        if (firmBool) {
            val adapter: ArrayAdapter<CharSequence> = when (i) {
                0 -> selectEntries(context, R.array.Arshanovsky)
                1 -> selectEntries(context, R.array.Beloyarsky)
                2 -> selectEntries(context, R.array.Chernogorsky)
                3 -> selectEntries(context, R.array.Vostochnobeysky)
                4 -> selectEntries(context, R.array.Izihsky)
                else -> selectEntries(context, R.array.Arshanovsky)
            }

            form.routeStartLocation = doubleArrayOf(App.cuts[0][i], App.cuts[1][i])
            viewState.changeCoalSpinnerEntries(adapter, i)
        }
        firmBool = true

        submitRequest()

        updateCost()

        Log.d(TAG, "Выбрана фирма: ${form.coalFirm}")
    }

    fun coalSpinnerWasChanged(i: Int, selectedItem: String) {
        form.coalMark = selectedItem
        form.pricePerTonn = App.prices[i]

        updateCost()

        Log.d(TAG, "Выбрана марка: ${form.coalMark}")
        Log.d(TAG, "Цена за тонну: ${form.pricePerTonn}")
    }

    fun resultWasClicked(position: Int, suggestResult: MutableList<String>) {
        marker = false

        val streetName = suggestResult[position].split(", ")
        val street = if (streetName.size >= 3) {
            streetName[streetName.size - 3] + ", " + streetName[streetName.size - 2] + ", " + streetName[streetName.size - 1]
        } else {
            suggestResult[position]
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

        form.distance = (Math.round(distance) / 1000).toInt()

        updateCost()

        Log.d(TAG, "Расстояние ${form.distance} км")

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

        if (form.weight.toString() == "0" || form.weight.toString() == "00" || form.weight.toString().isEmpty()) {
            viewState.showIncorrectWeightError()
            return
        }

        if (form.distance == 0) {
            viewState.showIncorrectAddressError()
            return
        }

        val nextIntent = Intent(context, SecondActivity::class.java)
        nextIntent.putExtra("form", form)

        viewState.openNewActivity(nextIntent = nextIntent)
    }

    fun mainActivityWasCreated() {


        if (!hasConnection(context)) {
            viewState.showNetworkConnectionError()
        }

    }

    fun goMapButtonWasPressed() {
        if (!hasConnection(context =  context)) {
            viewState.showNetworkConnectionError()
            return
        }

        val intent = Intent(
            context,
            MapsActivity::class.java
        )

        viewState.openNewActivityForResult(nextIntent = intent)
    }

    fun inOnActivityResult(data: Intent?) {
        if (data == null) {
            return
        }

        form.routeEndLocation = doubleArrayOf(
            data.extras.getDouble("latitude"),
            data.extras.getDouble("longitude")
        )

        getAddress(
            latitude = form.routeEndLocation[0],
            longitude = form.routeEndLocation[1]
        )

        updateCost()
    }

    fun onSuggestResponseDone(suggest: List<SuggestItem>) {
        val listItems  = ArrayList<String>(App.RESULT_NUMBER_LIMIT)

        try {
            for (i in 0..Math.min(App.RESULT_NUMBER_LIMIT - 1, suggest.size)) {
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

    fun searchBarWasChanged(text: String) {
        try {
            if (marker) {
                viewState.requestSuggest(text)
            }
            marker = true
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    fun weightWasChanged(weight: Int) {
        form.weight = weight
        form.distanceCost = try {
            when (form.weight) {
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

    // Private Methods

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

                    form.routeEndLocation =
                        doubleArrayOf(coordinates[1].toDouble(), coordinates[0].toDouble())


                    submitRequest()
                } else {
                    form.distance = 0
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

                marker = false

                viewState.updateSearchBar(address = address)

                if (isHouse == "house") {
                    form.routeEndLocation = doubleArrayOf(latitude, longitude)
                    submitRequest()
                } else {
                    form.distance = 0

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
        if (form.routeEndLocation[0] == 0.0) {
            form.distance = 0
            updateCost()
            return
        }

        val requestPoints = java.util.ArrayList<RequestPoint>()
        requestPoints.add(
            RequestPoint(
                Point(
                    form.routeStartLocation[0],
                    form.routeStartLocation[1]
                ),
                RequestPointType.WAYPOINT,
                null
            )
        )
        requestPoints.add(
            RequestPoint(
                Point(
                    form.routeEndLocation[0],
                    form.routeEndLocation[1]
                ),
                RequestPointType.WAYPOINT, null
            )
        )

        viewState.submitRequest(requestPoints = requestPoints)
    }

    private fun updateCost() {
        form.deliveryCost = form.distanceCost * form.distance
        form.overPrice = form.deliveryCost + form.pricePerTonn * form.weight

        viewState.updateCost(
            _pricePerTon = form.pricePerTonn,
            _overPrice = form.overPrice,
            _deliveryCost = form.deliveryCost,
            _distance = form.distance
        )

        Log.d(TAG, "Стоимость доставки: ${form.deliveryCost}")
        Log.d(TAG, "Полная стоимость: ${form.overPrice}")
    }
}