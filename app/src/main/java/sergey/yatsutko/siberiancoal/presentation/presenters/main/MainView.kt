package sergey.yatsutko.siberiancoal.presentation.presenters.main

import com.arellomobile.mvp.MvpView

interface MainView : MvpView{
    fun updateCost()
    fun showNetworkErrorMessage()
    fun changeCoalSpinnerEntries(selectedItemPosition: Int)
    fun submitRequest()
}