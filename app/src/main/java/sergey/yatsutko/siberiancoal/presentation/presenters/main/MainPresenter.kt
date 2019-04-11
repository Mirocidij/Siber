package sergey.yatsutko.siberiancoal.presentation.presenters.main

import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import kotlinx.android.synthetic.main.activity_main.*
import sergey.yatsutko.siberiancoal.R
import sergey.yatsutko.siberiancoal.commons.hasConnection
import sergey.yatsutko.siberiancoal.commons.selectEntries
import sergey.yatsutko.siberiancoal.data.entity.Form

@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {

    val TAG = "MainPresenter"

    private val form: Form = Form()

    init {

    }


    fun mainActivityWasCreated(context: Context) {

        if (!hasConnection(context)) {
            viewState.showNetworkErrorMessage()
        }

    }

    fun firmSpinnerWasChanged(selectedItemPosition: Int, selectedItem: String, context: Context) {

        form.coalFirm = selectedItem
        Log.d(TAG, form.coalFirm)

        val adapter: ArrayAdapter<CharSequence> = when (selectedItemPosition) {
            0 -> selectEntries(context, R.array.Arshanovsky)
            1 -> selectEntries(context, R.array.Beloyarsky)
            2 -> selectEntries(context, R.array.Chernogorsky)
            3 -> selectEntries(context, R.array.Vostochnobeysky)
            4 -> selectEntries(context, R.array.Izihsky)
            else -> selectEntries(context, R.array.Arshanovsky)
        }


        viewState.changeCoalSpinnerEntries(adapter, selectedItemPosition)
        viewState.submitRequest()
        viewState.updateCost()
    }


}