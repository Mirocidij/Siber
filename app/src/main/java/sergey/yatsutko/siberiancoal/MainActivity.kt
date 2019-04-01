package sergey.yatsutko.siberiancoal

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geo
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*

import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError

import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import org.json.JSONObject
import sergey.yatsutko.siberiancoal.helpful.InputFilterMinMax
import sergey.yatsutko.siberiancoal.helpful.selectEntries


class MainActivity : AppCompatActivity(), SearchManager.SuggestListener, DrivingSession.DrivingRouteListener {

    private var marker = true
    private var firmBool = false
    var distance = 0.0

    private val MAPKIT_API_KEY = "a139146c-adfa-484c-abb6-5ce42284f64e"
    private val RESULT_NUMBER_LIMIT = 5

    private var searchManager: SearchManager? = null
    private var suggestResultView: ListView? = null
    private var resultAdapter: ArrayAdapter<*>? = null
    private var suggestResult: MutableList<String>? = null

    private val CENTER = Point(53.721254, 91.443417)
    private val BOX_SIZE = 0.2
    private val BOUNDING_BOX = BoundingBox(
        Point(CENTER.latitude - BOX_SIZE, CENTER.longitude - BOX_SIZE),
        Point(CENTER.latitude + BOX_SIZE, CENTER.longitude + BOX_SIZE)
    )
    private val SEARCH_OPTIONS = SearchOptions().setSearchTypes(
        SearchType.GEO.value or
                SearchType.BIZ.value or
                SearchType.TRANSIT.value
    )

    // Prices for coal
    private val prices = intArrayOf(1700, 1800, 1900, 2000)
    // Default price
    private var price = 0
    // Price depend on weight
    private var priceForWeight = 0
    // Coal weight
    private var weigth = 0
    // Distance between A and B
    private var km = 0f

    private var deliveryCost = 0f
    private var overPrice = 0f

    // Users coordinates
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    // Cuts coordinates
    private val cuts = arrayOf(
        arrayOf(53.402971, 53.529799, 53.759367, 53.326586, 53.630114),
        arrayOf(91.083748, 91.410684, 91.061604, 91.361016, 91.436063)
    )

    private var ROUTE_START_LOCATION = Point(cuts[0][0], cuts[1][0])
    private var ROUTE_END_LOCATION = Point(latitude, longitude)

    private lateinit var drivingRouter: DrivingRouter
    private lateinit var drivingSession: DrivingSession


    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(this@MainActivity)
        DirectionsFactory.initialize(this@MainActivity)
        SearchFactory.initialize(this@MainActivity)


        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)

        if (!hasConnection(this@MainActivity)) {
            alert("Заказать уголь без интернет подключения невозможно", "Внимание") { yesButton {  } }.show()

        }

        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        val queryEdit = findViewById(R.id.searchBar) as EditText
        suggestResultView = findViewById(R.id.suggest_result) as ListView
        suggestResult = ArrayList()
        resultAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_2,
            android.R.id.text1,
            suggestResult!!
        )
        suggestResultView!!.adapter = resultAdapter

        queryEdit.addTextChangedListener(object : TextWatcher {
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
            var street = ""
            if (streetName.size >= 3) {
                street =
                    streetName[streetName.size - 3] + ", " + streetName[streetName.size - 2] + ", "  + streetName[streetName.size - 1]
            } else {
              street = suggestResult!![position]
            }

            queryEdit.setText(street)
            suggestResultView!!.visibility = View.INVISIBLE

            getCoordinates(street)
        }



        etDistance.hint = "0.0 km"
        etCoastForDelivery.hint = "0.0 рублей"

        //Inform Edit Text
        val etCoast = findViewById<EditText>(R.id.etCoast)

        etWeight.filters = arrayOf(InputFilterMinMax(0, 40), InputFilter.LengthFilter(2))

        etWeight.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                try {
                    weigth = Integer.parseInt(etWeight.text.toString())
                } catch (e: NumberFormatException) {

                }

                priceForWeight = try {
                    when (weigth) {
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

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        firmSpiner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                itemSelected: View, selectedItemPosition: Int, selectedId: Long
            ) {
                if (firmBool) {
                    when (selectedItemPosition) {
                        0 -> coalSpinner.adapter =
                            selectEntries(this@MainActivity, R.array.Arshanovsky)
                        1 -> coalSpinner.adapter =
                            selectEntries(this@MainActivity, R.array.Beloyarsky)
                        2 -> coalSpinner.adapter =
                            selectEntries(this@MainActivity, R.array.Chernogorsky)
                        3 -> coalSpinner.adapter =
                            selectEntries(this@MainActivity, R.array.Vostochnobeysky)
                        4 -> coalSpinner.adapter =
                            selectEntries(this@MainActivity, R.array.Izihsky)
                        else -> {
                        }
                    }
                    ROUTE_START_LOCATION = Point(cuts[0][selectedItemPosition], cuts[1][selectedItemPosition])


                    submitRequest()
                }
                firmBool = true
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
                price = if (selectedItemPosition % 2 == 0) {
                    prices[selectedItemPosition] - 5 * selectedItemPosition
                } else {
                    prices[selectedItemPosition] + 5 * selectedItemPosition
                }

                updateCost()
                etCoast.hint = "$price руб/т"
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

        try {
            weigth = Integer.parseInt(etWeight.text.toString())
        } catch (e: NumberFormatException) {

        }

        priceForWeight = try {
            when (weigth) {
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

        Log.d("etWeight", "Correct Address")
    }

    fun goNextActivity(v: View) {

        if (etWeight.text.toString() == "0" || etWeight.text.toString() == "00" || etWeight.text.toString().isEmpty()) {
            alert(message = "Некорректая масса", title = "Ошибка") {
                yesButton { }
            }.show()
            return
        }

        if (distance == 0.0) {
            alert(message = "Некорректный адрес доставки", title = "Ошибка") {
                yesButton { }
            }.show()
            return
        }

        val nextIntent = Intent(this@MainActivity, SecondActivity::class.java)

        nextIntent.putExtra("Cuts", firmSpiner.selectedItem.toString())
        nextIntent.putExtra("CoalMark", coalSpinner.selectedItem.toString())
        nextIntent.putExtra("Weight", etWeight.text.toString())
        nextIntent.putExtra("price", price)
        nextIntent.putExtra("km", km.toInt())
        nextIntent.putExtra("deliveryCost", deliveryCost.toInt())
        nextIntent.putExtra("overPrice", overPrice.toInt())
        nextIntent.putExtra("address", searchBar.text.toString())
        startActivity(nextIntent)
    }

    fun goMap(v: View) {

        if (!hasConnection(this@MainActivity)) {
            alert("Отсутствует интернет соединение", "Операция невозможна") { yesButton {  } }.show()
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
            for (i in 0..Math.min(RESULT_NUMBER_LIMIT - 1, suggest.size)) {
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
            searchManager!!.suggest(query, BOUNDING_BOX, SEARCH_OPTIONS, this)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    private fun getAddress(latitude: Double, longitude: Double) {

        val queue = Volley.newRequestQueue(this)
        val url =
            "https://geocode-maps.yandex.ru/1.x/?format=json&geocode=$longitude,$latitude&apikey=17757be8-4817-4365-886c-d89845ac6976"
        var address = ""
        var isHouse = ""


        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->

                var jsonObject =
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
                        streetName[streetName.size - 3] + ", " + streetName[streetName.size - 2] + ", "  + streetName[streetName.size - 1]
                }

                marker = false
                searchBar.setText(address)

                if (isHouse == "house") {
                    ROUTE_END_LOCATION = Point(latitude, longitude)
                    submitRequest()
                } else {
                    distance = 0.0
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

        var isHouse = ""

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->

                var jsonObject =
                    JSONObject(response)

                isHouse = jsonObject.getJSONObject("response")
                    .getJSONObject("GeoObjectCollection")
                    .getJSONArray("featureMember")
                    .getJSONObject(0)
                    .getJSONObject("GeoObject")
                    .getJSONObject("metaDataProperty")
                    .getJSONObject("GeocoderMetaData")
                    .getString("kind")

                Log.d("Pisya", isHouse)


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

//                toast(coordinates[0] + coordinates[1])
                    submitRequest()
                } else {
                    distance = 0.0
                    etDistance.hint = "0.0 km"

                    alert("Выберите дом", "Ошибка") {
                        yesButton { }
                    }.show()
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

        val points = java.util.ArrayList<Point>()

        Log.d("Jopa", "Routes: " + routes.size.toString())

        distance = 0.0
        if (routes.size > 0) {
            points.addAll(routes.get(0).getGeometry().getPoints())

            for (i in 0 until points.size - 1) {
                distance += Geo.distance(points[i], points[i + 1])
                Log.d("Jopa", "Distance: $distance")
            }
        } else {
            alert("Дорога не найдена", "Ошибка") {
                yesButton { }
            }.show()
        }

        km = (Math.round(distance) / 1000).toFloat()
        updateCost()


        Log.d("Coordinates", Integer.toString(Math.round(distance / 1000).toInt()) + " km")
    }

    private fun submitRequest() {

        if (ROUTE_END_LOCATION.latitude == 0.0) {
            distance = 0.0
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

    fun updateCost() {
        deliveryCost = km * priceForWeight
        etCoastForDelivery.hint = "${deliveryCost} рублей"
        overPrice = km * priceForWeight + price * weigth
        etCoastFor.hint = "${overPrice} рублей"
        etDistance.hint = "$km km"
    }


}