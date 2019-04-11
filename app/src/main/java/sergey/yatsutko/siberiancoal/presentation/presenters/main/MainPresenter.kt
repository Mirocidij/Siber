package sergey.yatsutko.siberiancoal.presentation.presenters.main

import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.annotations.NonNull
import sergey.yatsutko.siberiancoal.App
import sergey.yatsutko.siberiancoal.R
import sergey.yatsutko.siberiancoal.commons.hasConnection
import sergey.yatsutko.siberiancoal.commons.selectEntries
import sergey.yatsutko.siberiancoal.data.entity.Form

@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {

    val TAG = "MainPresenter"
    private var firmBool = false
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
        Log.d(TAG, "Выбрана фирма: ${form.coalFirm}")

        if (firmBool) {
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
        }
        firmBool = true
        viewState.updateCost()
    }

    fun coalSPinnerWasChanged(selectedItemPosition: Int, selectedItem: String) {
        form.coalMark = selectedItem
        form.pricePerTonn = App.prices[selectedItemPosition]

        Log.d(TAG, "Выбрана марка: ${form.coalMark}")
        Log.d(TAG, "Цена за тонну: ${form.pricePerTonn}")


        viewState.updateCost()
    }


}