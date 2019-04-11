package sergey.yatsutko.siberiancoal.presentation.presenters.main

import android.content.Context
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import sergey.yatsutko.siberiancoal.commons.hasConnection
import sergey.yatsutko.siberiancoal.data.entity.Form

@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {

    val form : Form = Form()

    init {

    }

    fun mainActivityWasCreated(context: Context) {
        if (!hasConnection(context)) {
            viewState.showNetworkErrorMessage()
        }
    }

    fun firmSpinnerWasRechanged(selectedItemPosition: Int) {
        viewState.changeCoalSpinnerEntries(selectedItemPosition)
        viewState.submitRequest()
        viewState.updateCost()

    }



}