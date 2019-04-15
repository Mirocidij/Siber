package sergey.yatsutko.siberiancoal.presentation.presenters.maps

import com.arellomobile.mvp.MvpView
import com.google.android.gms.maps.model.LatLng

interface MapsView : MvpView {

    fun refreshFlag(point: LatLng)
    fun saveCoordinatesAndFinish(point: LatLng)
    fun showError(messageRes: Int)

}