package sergey.yatsutko.siberiancoal.presentation.presenters.main

import android.widget.ArrayAdapter
import com.arellomobile.mvp.MvpView

interface MainView : MvpView {
    fun updateCost()
    fun showNetworkErrorMessage()
    fun changeCoalSpinnerEntries(adapter: ArrayAdapter<CharSequence>, i: Int)
    fun submitRequest()
}