package sergey.yatsutko.siberiancoal.presentation.presenters.main

import android.content.Context
import android.content.Intent
import android.widget.ArrayAdapter
import com.arellomobile.mvp.MvpView
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.geometry.Point

interface MainView : MvpView {
    fun updateCost(
        _pricePerTon: Int,
        _distance: Int,
        _deliveryCost: Int,
        _overPrice: Int
    )

    fun changeCoalSpinnerEntries(adapter: ArrayAdapter<CharSequence>, i: Int)
    fun submitRequest(requestPoints: ArrayList<RequestPoint>)
    fun openNewActivity(nextIntent: Intent)
    fun openNewActivityForResult(nextIntent: Intent)



    // Error

    fun showRoadNotFoundError()
    fun showHouseNotFoundError()
    fun showIncorrectWeightError()
    fun showIncorrectAddressError()
    fun showNetworkConnectionError()
}