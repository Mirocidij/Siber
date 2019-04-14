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

    private var suggestResult: MutableList<String> = ArrayList()
    private var resultAdapter: ArrayAdapter<*> = ArrayAdapter(
        this,
        android.R.layout.simple_list_item_2,
        android.R.id.text1,
        suggestResult
    )
    private lateinit var searchManager: SearchManager
    private lateinit var suggestResultView: ListView
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
        suggestResultView.adapter = resultAdapter

        float_btn.setOnClickListener {
            presenter.nextActivityButtonWasPressed()
        }

        map_btn.setOnClickListener {
            presenter.goMapButtonWasPressed()
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
                    firmSpiner.selectedItem.toString()
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
        presenter.onSuggestResponseDone(suggest)
    }

    override fun onSuggestError(error: Error) {
        presenter.inYandexErrorCallback(error = error)
    }

    // Yandex DirectionKit callback methods

    override fun onDrivingRoutesError(error: Error) {
        presenter.inYandexErrorCallback(error = error)
    }

    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
        presenter.onDrivingRoutesDone(routes)
    }

    // MainView methods

    override fun requestSuggest(request: String) {
        try {
            suggestResultView!!.visibility = View.INVISIBLE
            searchManager.suggest(request, App.BOUNDING_BOX, App.SEARCH_OPTIONS, this)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    override fun updateSearchBar(address: String) {
        searchBar.setText(address)
        suggestResultView.visibility = View.INVISIBLE
    }

    override fun openNewActivity(coalOrder: CoalOrder) {
        startActivity(Intent(this@MainActivity, SecondActivity::class.java).putExtra("coalOrder", coalOrder))
    }

    override fun openNewActivityForResult() {
        startActivityForResult(Intent(this@MainActivity, MapsActivity::class.java), 1)
    }

    override fun displaySearchResult(results: ArrayList<String>?) {
        suggestResult!!.clear()
        results!!.forEach {
            suggestResult!!.add(it)
        }
        resultAdapter!!.notifyDataSetChanged()
        suggestResultView!!.visibility = View.VISIBLE
    }

    override fun submitRequest(requestPoints: ArrayList<RequestPoint>) {
        drivingSession = drivingRouter.requestRoutes(requestPoints, DrivingOptions(), this@MainActivity)
    }

    override fun changeCoalSpinnerEntries(adapter: ArrayAdapter<CharSequence>, i: Int) {
        coalSpinner.adapter = adapter
    }

    override fun updateCost(pricePerTon: Int, distance: Int, deliveryCost: Int, overPrice: Int) {
        etCost.hint = "$pricePerTon руб/т"
        etDistance.hint = "$distance km"
        etCostForDelivery.hint = "$deliveryCost рублей"
        overPriceCost.hint = "$overPrice рублей"
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