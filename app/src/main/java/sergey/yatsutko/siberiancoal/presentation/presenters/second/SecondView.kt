package sergey.yatsutko.siberiancoal.presentation.presenters.second

import com.arellomobile.mvp.MvpView
import sergey.yatsutko.siberiancoal.data.entity.CoalOrder

interface SecondView: MvpView {

    fun updateFields(coalOrder: CoalOrder)
    fun changePhoneNumber(number: String)
    fun showCode(code: String)

    fun showAlert(titleRes: Int, messageRes: Int)
    fun showInformAlert(titleRes: Int, messageRes: Int)

    fun showCodeAlert(message: String, title: String, hint: String)


}
