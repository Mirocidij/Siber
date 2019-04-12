package sergey.yatsutko.siberiancoal.presentation.presenters.main

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.ArrayAdapter
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.geometry.Geo
import com.yandex.mapkit.geometry.Point
import kotlinx.android.synthetic.main.activity_main.*
import sergey.yatsutko.siberiancoal.App
import sergey.yatsutko.siberiancoal.R
import sergey.yatsutko.siberiancoal.commons.hasConnection
import sergey.yatsutko.siberiancoal.commons.selectEntries
import sergey.yatsutko.siberiancoal.data.entity.Form
import sergey.yatsutko.siberiancoal.presentation.ui.MapsActivity
import sergey.yatsutko.siberiancoal.presentation.ui.SecondActivity

@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {

    val TAG = "MainPresenter"
    private var firmBool = false
    val form: Form = Form()

    init {

    }

    fun mainActivityWasCreated(context: Context) {

        if (!hasConnection(context)) {
            viewState.showNetworkConnectionError()
        }

    }

    fun firmSpinnerWasChanged(i: Int, selectedItem: String, context: Context) {

        form.coalFirm = selectedItem

        if (firmBool) {
            val adapter: ArrayAdapter<CharSequence> = when (i) {
                0 -> selectEntries(context, R.array.Arshanovsky)
                1 -> selectEntries(context, R.array.Beloyarsky)
                2 -> selectEntries(context, R.array.Chernogorsky)
                3 -> selectEntries(context, R.array.Vostochnobeysky)
                4 -> selectEntries(context, R.array.Izihsky)
                else -> selectEntries(context, R.array.Arshanovsky)
            }

            form.routeStartLocation = Point(App.cuts[0][i], App.cuts[1][i])
            viewState.changeCoalSpinnerEntries(adapter, i)
        }
        firmBool = true

        submitRequest()

        updateCost()

        Log.d(TAG, "Выбрана фирма: ${form.coalFirm}")
    }

    fun coalSpinnerWasChanged(i: Int, selectedItem: String) {
        form.coalMark = selectedItem
        form.pricePerTonn = App.prices[i]

        updateCost()

        Log.d(TAG, "Выбрана марка: ${form.coalMark}")
        Log.d(TAG, "Цена за тонну: ${form.pricePerTonn}")
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
            viewState.showRoadNotFoundError()
        }

        form.distance = (Math.round(distance) / 1000).toInt()

        updateCost()

        Log.d(TAG, "Расстояние ${form.distance} км")

    }

    fun weightWasChanged(weight: Int) {
        form.weight = weight
        form.distanceCost = try {
            when (form.weight) {
                in 1..3 -> 10
                in 4..7 -> 15
                in 8..20 -> 35
                in 21..40 -> 80
                else -> 0
            }
        } catch (e: Throwable) {
            0
        }

        updateCost()
    }

    fun inOnActivityResult(data: Intent?) {
        if (data == null) {
            return
        }

        form.routeEndLocation = Point(
            data.extras.getDouble("latitude"),
            data.extras.getDouble("longitude")
        )

        viewState.getAddress(
            latitude = form.routeEndLocation.latitude,
            longitude = form.routeEndLocation.longitude
        )

        updateCost()
    }

    fun nextActivityButtonWasPressed(context: Context) {

        if (form.weight.toString() == "0" || form.weight.toString() == "00" || form.weight.toString().isEmpty()) {
            viewState.showIncorrectWeightError()
            return
        }

        if (form.distance == 0) {
            viewState.showIncorrectAddressError()
            return
        }

        val nextIntent = Intent(context, SecondActivity::class.java)
        nextIntent.putExtra("form", form)

        viewState.openNewActivity(nextIntent = nextIntent)
    }

    fun goMapButtonWasPresed(context: Context) {
        if (!hasConnection(context =  context)) {
            viewState.showNetworkConnectionError()
            return
        }

        val intent = Intent(
            context,
            MapsActivity::class.java
        )

        viewState.openNewActivityForResult(nextIntent = intent)
    }

    fun submitRequest() {
        if (form.routeEndLocation.latitude == 0.0) {
            form.distance = 0
            updateCost()
            return
        }

        val requestPoints = java.util.ArrayList<RequestPoint>()
        requestPoints.add(
            RequestPoint(
                form.routeStartLocation,
                RequestPointType.WAYPOINT,
                null
            )
        )
        requestPoints.add(
            RequestPoint(
                form.routeEndLocation,
                RequestPointType.WAYPOINT, null
            )
        )

        viewState.submitRequest(requestPoints = requestPoints)
    }

    fun updateCost() {
        form.deliveryCost = form.distanceCost * form.distance
        form.overPrice = form.deliveryCost + form.pricePerTonn * form.weight

        viewState.updateCost(
            _pricePerTon = form.pricePerTonn,
            _overPrice = form.overPrice,
            _deliveryCost = form.deliveryCost,
            _distance = form.distance
        )

        Log.d(TAG, "Стоимость доставки: ${form.deliveryCost}")
        Log.d(TAG, "Полная стоимость: ${form.overPrice}")
    }
}