package sergey.yatsutko.siberiancoal.presentation.presenters.main

import android.widget.ArrayAdapter
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.yandex.mapkit.RequestPoint
import sergey.yatsutko.siberiancoal.data.entity.CoalOrder

@StateStrategyType(AddToEndSingleStrategy::class)
interface MainView : MvpView {
    fun updateCost(
        pricePerTon: Int,
        distance: Int,
        deliveryCost: Int,
        overPrice: Int
    )

    fun updateSearchBar(address: String)
    fun changeCoalSpinnerEntries(adapter: ArrayAdapter<CharSequence>, i: Int)
    fun submitRequest(requestPoints: List<RequestPoint>)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun openNewActivity(coalOrder: CoalOrder)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun openNewActivityForResult()

    fun requestSuggest(request: String)
    fun displaySearchResult(results: List<String>)

    // Errors

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showValidationError(titleRes: Int, messageRes: Int)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showYandexError(errorMessage: String)
}