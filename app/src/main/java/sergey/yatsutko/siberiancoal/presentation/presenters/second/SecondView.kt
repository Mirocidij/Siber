package sergey.yatsutko.siberiancoal.presentation.presenters.second

import com.arellomobile.mvp.MvpView
import sergey.yatsutko.siberiancoal.data.entity.CoalOrder

interface SecondView: MvpView {

    fun updateFields(coalOrder: CoalOrder)
    fun changePhoneNumber(number: String)
    fun doneButtonWasPressed()

}
