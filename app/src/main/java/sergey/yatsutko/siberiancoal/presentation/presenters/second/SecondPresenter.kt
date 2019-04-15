package sergey.yatsutko.siberiancoal.presentation.presenters.second

import android.content.Context
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import sergey.yatsutko.siberiancoal.R
import sergey.yatsutko.siberiancoal.commons.hasConnection
import sergey.yatsutko.siberiancoal.data.entity.CoalOrder

@InjectViewState
class SecondPresenter(private val coalOrder: CoalOrder,private val context: Context): MvpPresenter<SecondView>() {

    private var phoneNumberLength = -1
    val TAG = "MainPresenter"

    override fun onFirstViewAttach() {
        viewState.updateFields(coalOrder = coalOrder)
    }

    fun phoneNumberWasChanged(number: String) {
        var editPhone = number

        Log.d(TAG, "\n backLength: $phoneNumberLength\n numberLength: ${number.length}")

        if (phoneNumberLength < number.length) {
            phoneNumberLength = number.length
            Log.d(TAG, "In if")

            editPhone = when(number) {
                "+" -> "+7 ("

                "8" -> "+7 ("
                "7" -> "+7 ("
                "9" -> "+7 (9"
                "6" -> "+7 (6"
                "5" -> "+7 (5"
                "4" -> "+7 (4"
                "3" -> "+7 (3"
                "2" -> "+7 (2"
                "1" -> "+7 (1"
                "0" -> "+7 (0"

                "+9" -> "+7 (9"
                "+8" -> "+7 (8"
                "+7" -> "+7 ("
                "+6" -> "+7 (6"
                "+5" -> "+7 (5"
                "+4" -> "+7 (4"
                "+3" -> "+7 (3"
                "+2" -> "+7 (2"
                "+1" -> "+7 (1"
                "+0" -> "+7 (0"

                "+79" -> "+7 (9"
                "+78" -> "+7 (8"
                "+77" -> "+7 (7"
                "+76" -> "+7 (6"
                "+75" -> "+7 (5"
                "+74" -> "+7 (4"
                "+73" -> "+7 (3"
                "+72" -> "+7 (2"
                "+71" -> "+7 (1"
                "+70" -> "+7 (0"

                else -> editPhone
            }



            editPhone = when(editPhone.length) {
                7 ->  "$editPhone) "
                12 -> "$editPhone-"
                15 -> "$editPhone-"
                else -> editPhone
            }
            viewState.changePhoneNumber(editPhone)
        }
        phoneNumberLength = editPhone.length
    }

    fun doneButtonWasPressed() {
        if (!hasConnection(context = context)) {
                viewState.showError(titleRes = R.string.error, messageRes = R.string.networkConnectionError)
            return
        }


    }

    private fun generateMessage(): String =
        "Разрез: ${coalOrder.coalFirm}" +
                "\nМарка: ${coalOrder.coalMark}" +
                "\nМасса: ${coalOrder.weight} тонн" +
                "\nАдресс: ${coalOrder.address}" +
                "\nРасстояние: ${coalOrder.distance} км" +
                "\nЦена за тонну: ${coalOrder.pricePerTonn} рублей" +
                "\nЦена доставки: ${coalOrder.deliveryCost} рублей" +
                "\nОбщая цена ${coalOrder.overPrice} рублей" +
                "\nТелефон: ${coalOrder.phoneNumber}"

    fun cleearNumber(number: String) = number.replace("[^0-9+]".toRegex(), "")

}