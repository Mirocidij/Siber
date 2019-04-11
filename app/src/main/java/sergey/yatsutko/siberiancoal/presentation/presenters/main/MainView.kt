package sergey.yatsutko.siberiancoal.presentation.presenters.main

import android.widget.ArrayAdapter
import com.arellomobile.mvp.MvpView

interface MainView : MvpView {
    fun updateCost(
        _pricePerTon: Int,
        _distance: Int,
        _deliveryCost: Int,
        _overPrice: Int
    )
    fun showNetworkErrorMessage()
    fun changeCoalSpinnerEntries(adapter: ArrayAdapter<CharSequence>, i: Int)
    fun submitRequest()
    fun showErrorRoadNotFound()
}