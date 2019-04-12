package sergey.yatsutko.siberiancoal.presentation.presenters.main

import android.content.Intent
import android.widget.ArrayAdapter
import com.arellomobile.mvp.MvpView
import com.yandex.mapkit.RequestPoint

interface MainView : MvpView {
    fun updateCost(
        _pricePerTon: Int,
        _distance: Int,
        _deliveryCost: Int,
        _overPrice: Int
    )
    fun updateSearchBar(address: String)
    fun changeCoalSpinnerEntries(adapter: ArrayAdapter<CharSequence>, i: Int)
    fun submitRequest(requestPoints: ArrayList<RequestPoint>)
    fun openNewActivity(nextIntent: Intent)
    fun openNewActivityForResult(nextIntent: Intent)
    fun getAddress(latitude: Double, longitude: Double)
    fun getCoordinates(address: String)
    fun requestSuggest(request: String)
    fun displaySearchResult(results: ArrayList<String>?)


    // Error

    fun showRoadNotFoundError()
    fun showHouseNotFoundError()
    fun showIncorrectWeightError()
    fun showIncorrectAddressError()
    fun showNetworkConnectionError()
    fun showYandexErrorToast(errorMessage: String)
}