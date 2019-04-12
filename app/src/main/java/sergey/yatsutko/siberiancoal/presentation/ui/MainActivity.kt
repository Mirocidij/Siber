package sergey.yatsutko.siberiancoal.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SuggestItem
import com.yandex.runtime.Error
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import org.json.JSONObject
import sergey.yatsutko.siberiancoal.App
import sergey.yatsutko.siberiancoal.R
import sergey.yatsutko.siberiancoal.commons.InputFilterMinMax
import sergey.yatsutko.siberiancoal.presentation.presenters.main.MainPresenter
import sergey.yatsutko.siberiancoal.presentation.presenters.main.MainView


class MainActivity : MvpAppCompatActivity(), MainView, SearchManager.SuggestListener,
    DrivingSession.DrivingRouteListener {

    @InjectPresenter
    lateinit var presenter: MainPresenter


    private var marker = true
    private var searchManager: SearchManager? = null
    private var suggestResultView: ListView? = null
    private var resultAdapter: ArrayAdapter<*>? = null
    private var suggestResult: MutableList<String>? = null

    private lateinit var drivingRouter: DrivingRouter
    private lateinit var drivingSession: DrivingSession

    // Lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {

        MapKitFactory.setApiKey(App.MAPKIT_API_KEY)
        MapKitFactory.initialize(this@MainActivity)
        DirectionsFactory.initialize(this@MainActivity)
        SearchFactory.initialize(this@MainActivity)
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)


        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        suggestResultView = findViewById(R.id.suggestResult)
        suggestResult = ArrayList()
        resultAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_2,
            android.R.id.text1,
            suggestResult!!
        )
        suggestResultView!!.adapter = resultAdapter

        // Проверка интернет подключения
        presenter.mainActivityWasCreated(this@MainActivity)


        float_btn.setOnClickListener {
            presenter.nextActivityButtonWasPressed(context = this@MainActivity)
        }

        map_btn.setOnClickListener {
            presenter.goMapButtonWasPressed(context = this@MainActivity)
        }

        // Слушатель изменений в поисковой строке
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                presenter.searchBarWasChanged(editable.toString())
            }
        })

        // Слушатель нажатий на эллементы ListView
        suggestResultView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            presenter.resultWasClicked(position, suggestResult!!)
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

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    //

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.inOnActivityResult(data = data)
    }


    // Yandex SearchKit callback methods

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
        presenter.inYandexErrorCallback(error = error, context = this@MainActivity)
    }


    // Yandex DirectionKit callback methods

    override fun onDrivingRoutesError(error: Error) {
        presenter.inYandexErrorCallback(error = error, context = this@MainActivity)
    }

    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
        presenter.onDrivingRoutesDone(routes)
    }


    // MainView methods

    override fun requestSuggest(request: String) {
        try {
            suggestResultView!!.visibility = View.INVISIBLE
            searchManager!!.suggest(request, App.BOUNDING_BOX, App.SEARCH_OPTIONS, this)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    override fun getCoordinates(address: String) {

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

                    presenter.form.routeEndLocation =
                        doubleArrayOf(coordinates[1].toDouble(), coordinates[0].toDouble())


                    presenter.submitRequest()
                } else {
                    presenter.form.distance = 0
                    etDistance.hint = "0 км"

                    showHouseNotFoundError()
                }
            },
            Response.ErrorListener {
                toast("That didn't work!")
            })

        queue.add(stringRequest)
    }

    override fun updateSearchBar(address: String) {
        searchBar.setText(address)
        suggestResultView!!.visibility = View.INVISIBLE
    }

    override fun openNewActivity(nextIntent: Intent) {
        startActivity(nextIntent)
    }

    override fun openNewActivityForResult(nextIntent: Intent) {
        startActivityForResult(nextIntent, 1)
    }

    override fun getAddress(latitude: Double, longitude: Double) {

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
                    presenter.form.routeEndLocation = doubleArrayOf(latitude, longitude)
                    presenter.submitRequest()
                } else {
                    presenter.form.distance = 0

                    presenter.updateCost()
                    showHouseNotFoundError()
                }
            },
            Response.ErrorListener {
                address = "That didn't work!"
            })

        queue.add(stringRequest)

    }

    override fun submitRequest(requestPoints: ArrayList<RequestPoint>) {
        drivingSession = drivingRouter.requestRoutes(requestPoints, DrivingOptions(), this@MainActivity)
    }

    override fun changeCoalSpinnerEntries(adapter: ArrayAdapter<CharSequence>, i: Int) {
        coalSpinner.adapter = adapter
    }

    override fun updateCost(_pricePerTon: Int, _distance: Int, _deliveryCost: Int, _overPrice: Int) {
        etCost.hint = "$_pricePerTon руб/т"
        etDistance.hint = "$_distance km"
        etCostForDelivery.hint = "$_deliveryCost рублей"
        overPriceCost.hint = "$_overPrice рублей"
    }


    // Errors

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

    override fun showNetworkConnectionError() {
        alert("Заказать уголь без интернет подключения невозможно", "Внимание") {
            yesButton { }
        }.show()
    }

    override fun showYandexErrorToast(errorMessage: String) {
        toast(errorMessage)
    }
}