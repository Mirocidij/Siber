package sergey.yatsutko.siberiancoal.presentation.presenters.maps

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.google.android.gms.maps.model.LatLng
import sergey.yatsutko.siberiancoal.R


@InjectViewState
class MapsPresenter: MvpPresenter<MapsView>() {

    private lateinit var point: LatLng

    fun pointWasChecked(point: LatLng) {
        this.point = point
        viewState.refreshFlag(this.point)
    }

    fun acceptButtonWasPressed() {
        if (point.latitude == 0.0 || point.longitude == 0.0) {
            viewState.showError(R.string.uncheckEndPoint)
            return
        }

        viewState.saveCoordinatesAndFinish(point)
    }

}