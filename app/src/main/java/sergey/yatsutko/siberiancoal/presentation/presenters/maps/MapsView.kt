package sergey.yatsutko.siberiancoal.presentation.presenters.maps

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.google.android.gms.maps.model.LatLng

@StateStrategyType(AddToEndSingleStrategy::class)
interface MapsView : MvpView {

    fun refreshFlag(point: LatLng)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun saveCoordinatesAndFinish(point: LatLng)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showError(messageRes: Int)

}