package sergey.yatsutko.siberiancoal.presentation.presenters.second

import android.content.Context
import android.content.Intent
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import sergey.yatsutko.siberiancoal.R
import sergey.yatsutko.siberiancoal.commons.hasConnection
import sergey.yatsutko.siberiancoal.data.entity.CoalOrder
import sergey.yatsutko.siberiancoal.data.repository.SmsApiRepository
import kotlin.random.Random

@InjectViewState
class SecondPresenter(
    private val coalOrder: CoalOrder,
    private val context: Context,
    private val repository: SmsApiRepository = SmsApiRepository()
) : MvpPresenter<SecondView>() {

    val TAG = "SecondPresenter"
    private var code = "0"

    override fun onFirstViewAttach() {
        viewState.updateFields(coalOrder = coalOrder)
    }

    fun phoneNumberWasChanged(number: String) {
        coalOrder.phoneNumber = number
    }

    fun onSendOrder() {
        if (!hasConnection(context = context)) {
            viewState.showAlert(titleRes = R.string.error, messageRes = R.string.networkConnectionError)
            return
        }

        var count = 0
        for (i in 0 until coalOrder.phoneNumber.length) {
            if (coalOrder.phoneNumber[i] == '+') count++
        }

        Log.d(
            TAG, "\n" +
                    "Длина номера: ${coalOrder.phoneNumber.length}\n" +
                    "Первый символ: ${coalOrder.phoneNumber[0]}\n" +
                    "Второй символ: ${coalOrder.phoneNumber[1]}\n" +
                    "Количество плюсов: $count"
        )

        if (coalOrder.phoneNumber.length == 18
            && coalOrder.phoneNumber[0] == '+'
            && coalOrder.phoneNumber[1] == '7'
            /*&& coalOrder.phoneNumber.isNotEmpty()*/
            && count == 1
        ) {
            val phone = clearNumber(coalOrder.phoneNumber)
            code = generateCode()

            repository.sendSms(phoneNumber = phone, message = "Ваш код: $code")
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    viewState.showCodeAlert(message = "", title = "Введите код из SMS", hint = "")
                    viewState.showCode(code)
                }
                .subscribe()
        } else {
            viewState.showAlert(titleRes = R.string.error, messageRes = R.string.incorrectPhoneNumberError)
        }

    }

    fun onSendConfirmationCode(code: String) {
        if (code != this.code) {
            viewState.showCodeAlert(message = "", title = "Введите код из SMS", hint = "Неверный код")
            return
        } else {
            repository
                .sendSms(phoneNumber = "+79628003000", message = generateMessage())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    viewState.showInformAlert(
                        messageRes = R.string.userInformMessage, titleRes = R.string.userInformTitle
                    )
                }
                .subscribe()
        }
    }

    fun onSendConfirmationOrder(intent: Intent) {
        viewState.finishOrder(intent)
    }

    private fun generateMessage(): String {
        return "Разрез: ${coalOrder.coalFirm}" +
                "\nМарка: ${coalOrder.coalMark}" +
                "\nМасса: ${coalOrder.weight} тонн" +
                "\nАдресс: ${coalOrder.address}" +
                "\nРасстояние: ${coalOrder.distance} км" +
                "\nЦена за тонну: ${coalOrder.pricePerTonn} рублей" +
                "\nЦена доставки: ${coalOrder.deliveryCost} рублей" +
                "\nОбщая цена ${coalOrder.overPrice} рублей" +
                "\nТелефон: ${coalOrder.phoneNumber}"
    }

    private fun clearNumber(number: String) = number.replace("[^0-9+]".toRegex(), "")

    private fun generateCode(): String = Random.nextInt(1000, 9999).toString()
}
