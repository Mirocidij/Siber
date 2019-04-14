package sergey.yatsutko.siberiancoal.presentation.presenters.main

import android.widget.ArrayAdapter
import com.arellomobile.mvp.MvpView
import com.yandex.mapkit.RequestPoint
import sergey.yatsutko.siberiancoal.data.entity.CoalOrder

interface MainView : MvpView {
    fun updateCost(
        pricePerTon: Int,
        distance: Int,
        deliveryCost: Int,
        overPrice: Int
    )
    fun updateSearchBar(address: String)
    fun changeCoalSpinnerEntries(adapter: ArrayAdapter<CharSequence>, i: Int)
    fun submitRequest(requestPoints: List<RequestPoint>)
    fun openNewActivity(coalOrder: CoalOrder)
    fun openNewActivityForResult()
    fun requestSuggest(request: String)
    fun displaySearchResult(results: List<String?>)

    // Errors

    fun showValidationError(titleRes: Int, messageRes: Int)
    fun showYandexError(errorMessage: String)
}