package sergey.yatsutko.siberiancoal.presentation.presenters.second

import android.content.Intent
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import sergey.yatsutko.siberiancoal.data.entity.CoalOrder

@StateStrategyType(AddToEndSingleStrategy::class)
interface SecondView: MvpView {

    fun updateFields(coalOrder: CoalOrder)
    fun changePhoneNumber(number: String)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun finishOrder(intent: Intent)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showCode(code: String)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showAlert(titleRes: Int, messageRes: Int)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showCodeAlert(message: String, title: String, hint: String)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showInformAlert(titleRes: Int, messageRes: Int)
}
