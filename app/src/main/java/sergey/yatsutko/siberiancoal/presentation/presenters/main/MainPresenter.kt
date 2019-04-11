package sergey.yatsutko.siberiancoal.presentation.presenters.main

import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.geometry.Geo
import com.yandex.mapkit.geometry.Point
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
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

    fun firmSpinnerWasChanged(i: Int, selectedItem: String, context: Context) {

        form.coalFirm = selectedItem
        Log.d(TAG, "Выбрана фирма: ${form.coalFirm}")

        if (firmBool) {
            val adapter: ArrayAdapter<CharSequence> = when (i) {
                0 -> selectEntries(context, R.array.Arshanovsky)
                1 -> selectEntries(context, R.array.Beloyarsky)
                2 -> selectEntries(context, R.array.Chernogorsky)
                3 -> selectEntries(context, R.array.Vostochnobeysky)
                4 -> selectEntries(context, R.array.Izihsky)
                else -> selectEntries(context, R.array.Arshanovsky)
            }
            viewState.changeCoalSpinnerEntries(adapter, i)
        }
        firmBool = true

        viewState.submitRequest()

        updateCost()
    }

    fun coalSpinnerWasChanged(i: Int, selectedItem: String) {
        form.coalMark = selectedItem
        form.pricePerTonn = App.prices[i]

        Log.d(TAG, "Выбрана марка: ${form.coalMark}")
        Log.d(TAG, "Цена за тонну: ${form.pricePerTonn}")

        updateCost()
    }



    fun onDrivingRoutesDone(routes: MutableList<DrivingRoute>) {

        val points = java.util.ArrayList<Point>()
        var distance = 0.0
        if (routes.size > 0) {
            points.addAll(routes.get(0).getGeometry().getPoints())

            for (i in 0 until points.size - 1) {
                distance += Geo.distance(points[i], points[i + 1])
            }
        } else {
            viewState.showErrorRoadNotFound()
        }

        form.distance = (Math.round(distance) / 1000).toInt()

        updateCost()

    }

    private fun updateCost() {
        viewState.updateCost(
            _pricePerTon = form.pricePerTonn!!,
            _overPrice = form.overPrice!!,
            _deliveryCost = form.deliveryCost!!,
            _distance = form.distance!!
        )
    }

}