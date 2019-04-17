package sergey.yatsutko.siberiancoal.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
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
import sergey.yatsutko.siberiancoal.App
import sergey.yatsutko.siberiancoal.R
import sergey.yatsutko.siberiancoal.commons.InputFilterMinMax
import sergey.yatsutko.siberiancoal.data.entity.CoalOrder
import sergey.yatsutko.siberiancoal.presentation.presenters.main.MainPresenter
import sergey.yatsutko.siberiancoal.presentation.presenters.main.MainView

class MainActivity : MvpAppCompatActivity(), MainView, SearchManager.SuggestListener,
    DrivingSession.DrivingRouteListener {

    @InjectPresenter
    lateinit var presenter: MainPresenter

    @ProvidePresenter
    fun provideMainPresenter(): MainPresenter {
        return MainPresenter(context = this@MainActivity)
    }

    private var suggestResult = mutableListOf<String>()
    private lateinit var resultAdapter: ArrayAdapter<*>
    private lateinit var searchManager: SearchManager
    private lateinit var drivingRouter: DrivingRouter
    private lateinit var drivingSession: DrivingSession

    var marker = true

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
        resultAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_2,
            android.R.id.text1,
            suggestResult
        )
        lvSearchResult.adapter = resultAdapter

        fabDone.setOnClickListener {
            presenter.nextActivityButtonWasPressed()
        }

        btnMap.setOnClickListener {
            presenter.goMapButtonWasPressed()
        }

        // Слушатель изменений в поисковой строке
        etSearchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                try {
                    if (marker) {
                        requestSuggest(editable.toString())
                    }
                    marker = true
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }
        })

        // Слушатель нажатий на эллементы ListView
        lvSearchResult.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            marker = false
            presenter.resultWasClicked(result = suggestResult[position])
        }

        //Inform Edit Text

        etWeight.filters = arrayOf(InputFilterMinMax(0, 40), InputFilter.LengthFilter(2))

        etWeight.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                presenter.weightWasChanged(s.toString().toIntOrNull() ?: 0)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        spinnerFirm.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, itemSelected: View, selectedItemPosition: Int, selectedId: Long
            ) {
                presenter.firmSpinnerWasChanged(
                    selectedItemPosition,
                    spinnerFirm.selectedItem.toString()
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerCoal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                itemSelected: View,
                selectedItemPosition: Int,
                selectedId: Long
            ) {
                presenter.coalSpinnerWasChanged(selectedItemPosition, spinnerCoal.selectedItem.toString())
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onMapPlaceSelected(place = data)
    }

    // Yandex SearchKit callback methods

    override fun onSuggestResponse(suggest: List<SuggestItem>) {
        presenter.onSuggestResponseDone(suggest)
    }

    override fun onSuggestError(error: Error) {
        presenter.onYandexError(error = error)
    }

    // Yandex DirectionKit callback methods

    override fun onDrivingRoutesError(error: Error) {
        presenter.onYandexError(error = error)
    }

    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
        presenter.onDrivingRoutesDone(routes)
    }

    // MainView methods

    override fun openNewActivityForResult() {
        startActivityForResult(Intent(this@MainActivity, MapsActivity::class.java), 1)
    }

    override fun requestSuggest(request: String) {
        try {
            lvSearchResult.visibility = View.INVISIBLE
            searchManager.suggest(request, App.BOUNDING_BOX, App.SEARCH_OPTIONS, this)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    override fun updateSearchBar(address: String) {
        marker = false
        etSearchBar.setText(address)
        lvSearchResult.visibility = View.INVISIBLE
    }

    override fun openNewActivity(coalOrder: CoalOrder) {
        startActivity(Intent(this@MainActivity, SecondActivity::class.java).putExtra("coalOrder", coalOrder))
    }

    override fun displaySearchResult(results: List<String>) {

        suggestResult.clear()
        suggestResult.addAll(results)
        resultAdapter.notifyDataSetChanged()
        lvSearchResult.visibility = View.VISIBLE
    }

    override fun submitRequest(requestPoints: List<RequestPoint>) {
        drivingSession = drivingRouter.requestRoutes(requestPoints, DrivingOptions(), this@MainActivity)
    }

    override fun changeCoalSpinnerEntries(adapter: ArrayAdapter<CharSequence>, i: Int) {
        spinnerCoal.adapter = adapter
    }

    override fun updateCost(pricePerTon: Int, distance: Int, deliveryCost: Int, overPrice: Int) {
        etCost.hint = "$pricePerTon руб/т"
        etDistance.hint = "$distance km"
        etCostForDelivery.hint = "$deliveryCost рублей"
        overPriceCost.hint = "$overPrice рублей"
    }

    // Errors

    override fun showValidationError(titleRes: Int, messageRes: Int) {
        alert(message = getString(messageRes), title = getString(titleRes)) {
            yesButton { }
        }.show()
    }

    override fun showYandexError(errorMessage: String) {
        toast(errorMessage)
    }
}