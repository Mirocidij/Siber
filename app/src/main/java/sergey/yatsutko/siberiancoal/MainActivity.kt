package sergey.yatsutko.siberiancoal

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*

import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*

import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError

import kotlinx.android.synthetic.main.activity_main.*
import sergey.yatsutko.siberiancoal.helpful.InputFilterMinMax
import sergey.yatsutko.siberiancoal.helpful.selectEntries


class MainActivity : AppCompatActivity(), SearchManager.SuggestListener{

    private var marker = true

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
                SearchType.TRANSIT.value)


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

    // For google map
    private var a = FloatArray(size = 3)
    // Users coordinates
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    // Cuts coordinates
    private val cuts = arrayOf(
        arrayOf(53.402971, 53.529799, 53.759367, 53.326586, 53.630114),
        arrayOf(91.083748, 91.410684, 01.061604, 91.361016, 91.436063)
    )
    // :D
    private var spinningCoal: Boolean = false

//    https://maps.googleapis.com/maps/api/directions/json?
//    origin=53.402971,91.083748&destination=53.717647,91.429705
//    &key=YOUR_API_KEY


    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(this@MainActivity)
        SearchFactory.initialize(this@MainActivity)


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        val queryEdit = findViewById(R.id.searchBar) as EditText
        suggestResultView = findViewById(R.id.suggest_result) as ListView
        suggestResult = ArrayList()
        resultAdapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_2,
            android.R.id.text1,
            suggestResult!!)
        suggestResultView!!.adapter = resultAdapter

        queryEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                scroll.elevation = 20f
                if (marker) {
                    requestSuggest(editable.toString())
                }
                marker = true
            }
        })



        suggestResultView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            marker = false
            scroll.elevation = 0f
            queryEdit.setText(suggestResult!![position])
            suggestResultView!!.visibility = View.INVISIBLE
            Toast.makeText(this@MainActivity, suggestResult!![position], Toast.LENGTH_LONG).show()
        }



        etDistance.hint = "0.0 km"
        etCoastForDelivery.hint = "0.0 рублей"

        //Inform Edit Text
        val etCoast = findViewById<EditText>(R.id.etCoast)
        val etCoast2 = findViewById<EditText>(R.id.etCoast2)

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
                deliveryCost = km * priceForWeight
                etCoastForDelivery.hint = "${deliveryCost} рублей"
                overPrice = km * priceForWeight + price * weigth
                etCoastFor.hint = "${overPrice} рублей"
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        firmSpiner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                itemSelected: View, selectedItemPosition: Int, selectedId: Long
            ) {
                val choose = resources.getStringArray(R.array.firms)

                when {
                    choose[selectedItemPosition] == "Кирбинский разрез" -> coalSpinner.adapter =
                        selectEntries(this@MainActivity, R.array.Kirbinsky)
                    choose[selectedItemPosition] == "Черногорский разрез" -> coalSpinner.adapter =
                        selectEntries(this@MainActivity, R.array.Chernogorsky)
                    choose[selectedItemPosition] == "Изыхский разрез" -> coalSpinner.adapter =
                        selectEntries(this@MainActivity, R.array.Izihsky)
                    choose[selectedItemPosition] == "Восточнобейский разрез" -> coalSpinner.adapter =
                        selectEntries(this@MainActivity, R.array.Vostochnobeysky)
                    choose[selectedItemPosition] == "Белоярский разрез" -> coalSpinner.adapter =
                        selectEntries(this@MainActivity, R.array.Beloyarsky)
                }
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
                etCoast2.hint = "${coalSpinner.selectedItem}:"
                etCoast.hint = "$price руб/т"

                overPrice = km * priceForWeight + price * weigth
                etCoastFor.hint = "${overPrice} рублей"


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

        Location.distanceBetween(latitude, longitude, cuts[0][0], cuts[0][1], a)
        km = a[0] / 50000

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

        etDistance.hint = "$km km"

        deliveryCost = km * priceForWeight
        etCoastForDelivery.hint = "$deliveryCost рублей"
        overPrice = km * priceForWeight + price * weigth
        etCoastFor.hint = "$overPrice рублей"

        Log.d("etWeight", "Correct Address")
    }

    fun goNextActivity(v: View) {

        val nextIntent = Intent(this@MainActivity, SecondActivity::class.java)

        if (etWeight.text.toString() == "0" || etWeight.text.toString() == "00" || etWeight.text.toString().isEmpty()) {
            Toast.makeText(this@MainActivity, "Некорректная масса", Toast.LENGTH_SHORT).show()
            Log.d("etWeight", "IsEmptyWeight")
            return
        } else {
            Log.d("etWeight", "NonEmptyWeight")

        }

        if (latitude == 0.0 && longitude == 0.0) {
            Toast.makeText(this@MainActivity, "Не выбрано место доставки", Toast.LENGTH_SHORT).show()
            Log.d("etWeight", "IncorrectAddress")
            return
        } else {
            Log.d("etWeight", "CorrectAddress")
        }

        nextIntent.putExtra("Cuts", firmSpiner.selectedItem.toString())
        nextIntent.putExtra("CoalMark", coalSpinner.selectedItem.toString())
        nextIntent.putExtra("Weight", etWeight.text.toString())
        nextIntent.putExtra("price", price)
        nextIntent.putExtra("km", km)
        nextIntent.putExtra("deliveryCost", deliveryCost)
        nextIntent.putExtra("overPrice", overPrice)
        nextIntent.putExtra("address", searchBar.text.toString())
        startActivity(nextIntent)
    }

    fun goMap(v: View) {
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
        suggestResult!!.clear()
        for (i in 0..Math.min(RESULT_NUMBER_LIMIT - 1, suggest.size)) {
            suggestResult!!.add(suggest[i].displayText!!)
        }
        resultAdapter!!.notifyDataSetChanged()


        suggestResultView!!.visibility = View.VISIBLE
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
        suggestResultView!!.visibility = View.INVISIBLE
        searchManager!!.suggest(query, BOUNDING_BOX, SEARCH_OPTIONS, this)
    }
}