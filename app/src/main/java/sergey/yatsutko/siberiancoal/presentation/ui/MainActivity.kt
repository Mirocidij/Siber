package sergey.yatsutko.siberiancoal.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SuggestItem
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import org.json.JSONObject
import sergey.yatsutko.siberiancoal.App
import sergey.yatsutko.siberiancoal.R
import sergey.yatsutko.siberiancoal.commons.InputFilterMinMax
import sergey.yatsutko.siberiancoal.commons.hasConnection
import sergey.yatsutko.siberiancoal.presentation.presenters.main.MainPresenter
import sergey.yatsutko.siberiancoal.presentation.presenters.main.MainView


class MainActivity : MvpAppCompatActivity(), MainView, SearchManager.SuggestListener,
    DrivingSession.DrivingRouteListener {

    @InjectPresenter
    lateinit var presenter: MainPresenter

    private var marker = true

    private var distance = 0

    private var searchManager: SearchManager? = null
    private var suggestResultView: ListView? = null
    private var resultAdapter: ArrayAdapter<*>? = null
    private var suggestResult: MutableList<String>? = null

    // Users coordinates
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private var ROUTE_START_LOCATION = Point(App.cuts[0][0], App.cuts[1][0])
    private var ROUTE_END_LOCATION = Point(latitude, longitude)

    private lateinit var drivingRouter: DrivingRouter
    private lateinit var drivingSession: DrivingSession


    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey(App.MAPKIT_API_KEY)
        MapKitFactory.initialize(this@MainActivity)
        DirectionsFactory.initialize(this@MainActivity)
        SearchFactory.initialize(this@MainActivity)
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)

        // Проверка интернет подключения
        presenter.mainActivityWasCreated(this@MainActivity)


        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        suggestResultView = findViewById<ListView>(R.id.suggestResult)
        suggestResult = ArrayList()
        resultAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_2,
            android.R.id.text1,
            suggestResult!!
        )
        suggestResultView!!.adapter = resultAdapter

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {

                try {
                    scroll.elevation = 20f
                    if (marker) {
                        requestSuggest(editable.toString())
                    }
                    marker = true
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }

            }
        })


        suggestResultView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            marker = false
            scroll.elevation = 0f

            val streetName = suggestResult!![position].split(", ")
            val street = if (streetName.size >= 3) {
                streetName[streetName.size - 3] + ", " + streetName[streetName.size - 2] + ", " + streetName[streetName.size - 1]
            } else {
                suggestResult!![position]
            }

            searchBar.setText(street)
            suggestResultView!!.visibility = View.INVISIBLE

            getCoordinates(street)
        }


        //Inform Edit Text

        etWeight.filters = arrayOf(InputFilterMinMax(0, 40), InputFilter.LengthFilter(2))

        etWeight.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                try {
                    presenter.weightWasChanged(Integer.parseInt(s.toString()))
                } catch (e: NumberFormatException) {

                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        firmSpiner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, itemSelected: View, selectedItemPosition: Int, selectedId: Long
            ) {
                presenter.firmSpinnerWasChanged(
                    selectedItemPosition,
                    firmSpiner.selectedItem.toString(),
                    this@MainActivity
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        coalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                itemSelected: View,
                selectedItemPosition: Int,
                selectedId: Long
            ) {
                presenter.coalSpinnerWasChanged(selectedItemPosition, coalSpinner.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) {
            return
        }
        latitude = data.extras.getDouble("latitude")
        longitude = data.extras.getDouble("longitude")

        getAddress(latitude = latitude, longitude = longitude)

//        updateCost()

        Log.d("etWeight", "Correct Address")
    }

    fun goNextActivity(v: View) {

        presenter.nextActivityButtonWasPressed(context = this@MainActivity)

    }

    fun goMap(v: View) {

        if (!hasConnection(context =  this@MainActivity)) {
            alert("Отсутствует интернет соединение", "Операция невозможна") {
                yesButton { }
            }.show()
            return
        }

        val intent = Intent(
            this@MainActivity,
            MapsActivity::class.java
        )
        startActivityForResult(intent, 1)
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onSuggestResponse(suggest: List<SuggestItem>) {

        try {
            suggestResult!!.clear()
            for (i in 0..Math.min(App.RESULT_NUMBER_LIMIT - 1, suggest.size)) {
                suggestResult!!.add(suggest[i].displayText!!)
            }
            resultAdapter!!.notifyDataSetChanged()

            suggestResultView!!.visibility = View.VISIBLE
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }


    }

    override fun onSuggestError(error: Error) {
        var errorMessage = getString(R.string.unknown_error_message)
        if (error is RemoteError) {
            errorMessage = getString(R.string.remote_error_message)
        } else if (error is NetworkError) {
            errorMessage = getString(R.string.network_error_message)
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun requestSuggest(query: String) {
        try {
            suggestResultView!!.visibility = View.INVISIBLE
            searchManager!!.suggest(query, App.BOUNDING_BOX, App.SEARCH_OPTIONS, this)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    private fun getAddress(latitude: Double, longitude: Double) {

        val queue = Volley.newRequestQueue(this)
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
                searchBar.setText(address)

                if (isHouse == "house") {
                    ROUTE_END_LOCATION = Point(latitude, longitude)
                    submitRequest()
                } else {
                    distance = 0
                    etDistance.hint = "0.0 km"

                    alert("Выберите дом", "Ошибка") {
                        yesButton { }
                    }.show()
                }
            },
            Response.ErrorListener {
                address = "That didn't work!"
            })

        queue.add(stringRequest)

    }

    private fun getCoordinates(address: String) {

        val queue = Volley.newRequestQueue(this)
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

                    ROUTE_END_LOCATION = Point(coordinates[1].toDouble(), coordinates[0].toDouble())
                    latitude = coordinates[1].toDouble()
                    longitude = coordinates[0].toDouble()


                    submitRequest()
                } else {
                    distance = 0
                    etDistance.hint = "0.0 km"

                    showHouseNotFoundError()
                }
            },
            Response.ErrorListener {
                toast("That didn't work!")
            })

        queue.add(stringRequest)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDrivingRoutesError(error: Error) {
        var errorMessage = getString(R.string.unknown_error_message)
        if (error is RemoteError) {
            errorMessage = getString(R.string.remote_error_message)
        } else if (error is NetworkError) {
            errorMessage = getString(R.string.network_error_message)
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
        presenter.onDrivingRoutesDone(routes)
    }

    override fun submitRequest() {
        if (ROUTE_END_LOCATION.latitude == 0.0) {
            distance = 0
            etDistance.hint = "0.0 km"
            return
        }
        val options = DrivingOptions()
        val requestPoints = java.util.ArrayList<RequestPoint>()
        requestPoints.add(
            RequestPoint(
                ROUTE_START_LOCATION,
                RequestPointType.WAYPOINT,
                null
            )
        )
        requestPoints.add(
            RequestPoint(
                ROUTE_END_LOCATION,
                RequestPointType.WAYPOINT, null
            )
        )
        drivingSession = drivingRouter.requestRoutes(requestPoints, options, this)
    }

    override fun updateCost(_pricePerTon: Int, _distance: Int, _deliveryCost: Int, _overPrice: Int) {
        etCost.hint = "$_pricePerTon руб/т"
        etDistance.hint = "$_distance km"
        etCostForDelivery.hint = "$_deliveryCost рублей"
        overPriceCost.hint = "$_overPrice рублей"
    }

    override fun changeCoalSpinnerEntries(adapter: ArrayAdapter<CharSequence>, i: Int) {
        coalSpinner.adapter = adapter
        ROUTE_START_LOCATION = Point(App.cuts[0][i], App.cuts[1][i])
    }

    override fun openNewActivity(nextIntent: Intent) {
        startActivity(nextIntent)
    }

    // Errors
    override fun showNetworkConnectionError() {
        alert("Заказать уголь без интернет подключения невозможно", "Внимание") {
            yesButton { }
        }.show()
    }

    override fun showRoadNotFoundError() {
        alert("Дорога не найдена", "Ошибка") {
            yesButton { }
        }.show()
    }

    override fun showHouseNotFoundError() {
        alert("Выберите дом", "Ошибка") {
            yesButton { }
        }.show()
    }

    override fun showIncorrectWeightError() {
        alert(message = "Некорректая масса", title = "Ошибка") {
            yesButton { }
        }.show()
    }

    override fun showIncorrectAddressError() {
        alert(message = "Некорректный адрес доставки", title = "Ошибка") {
            yesButton { }
        }.show()
    }
}