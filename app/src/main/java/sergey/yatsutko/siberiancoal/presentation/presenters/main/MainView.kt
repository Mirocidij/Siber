package sergey.yatsutko.siberiancoal.presentation.presenters.main

import android.content.Intent
import android.widget.ArrayAdapter
import com.arellomobile.mvp.MvpView

interface MainView : MvpView {
    fun updateCost(
        _pricePerTon: Int,
        _distance: Int,
        _deliveryCost: Int,
        _overPrice: Int
    )

    fun changeCoalSpinnerEntries(adapter: ArrayAdapter<CharSequence>, i: Int)
    fun submitRequest()
    fun openNewActivity(nextIntent: Intent)

    // Error

    fun showNetworkConnectionError()
    fun showRoadNotFoundError()
    fun showHouseNotFoundError()
    fun incorrectWeightError()
    fun incorrectAddressError()
}