package sergey.yatsutko.siberiancoal.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_second.*
import org.jetbrains.anko.*
import sergey.yatsutko.siberiancoal.R

import sergey.yatsutko.siberiancoal.commons.hasConnection
import sergey.yatsutko.siberiancoal.data.repository.SmsApiRepository
import sergey.yatsutko.siberiancoal.presentation.presenters.second.SecondPresenter
import sergey.yatsutko.siberiancoal.presentation.presenters.second.SecondView
import kotlin.random.Random

class SecondActivity : MvpAppCompatActivity(), SecondView {

    @InjectPresenter
    lateinit var presenter: SecondPresenter

    private val repository: SmsApiRepository = SmsApiRepository()

    var phoneNumberLength = -1
    var code = "0"

    private var cuts = "0"
    private var coalMark = "0"
    private var weight = "0"
    private var price = 0
    private var distance = 0
    private var phone = "0"
    private var overPrice = 0
    private var deliveryCost = 0
    private var address = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        etCutsInfo2.hint = cuts
        etCoalInfo2.hint = coalMark

        if (Integer.parseInt(weight) < 5) {
            etWeight2.hint = "$weight тонны"
        } else {
            etWeight2.hint = "$weight тонн"
        }

        etPriceInfo2.hint = "$price рублей"

        etDistance2.hint = "$distance km"

        etOverPrice2.hint = "$overPrice рублей"

        etDeliveryCost2.hint = "$deliveryCost рублей"

        etAddress2.setText(address)


        etPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (phoneNumberLength < s.toString().length) {
                    when {
                        s.toString() == "+" -> etPhoneNumber.setText("+7 (")
                        s.toString() == "8" -> etPhoneNumber.setText("+7 (")
                        s.toString() == "7" -> etPhoneNumber.setText("+7 (")
                        s.toString() == "9" -> etPhoneNumber.setText("+7 (9")
                        s.toString() == "6" -> etPhoneNumber.setText("+7 (6")
                        s.toString() == "5" -> etPhoneNumber.setText("+7 (5")
                        s.toString() == "4" -> etPhoneNumber.setText("+7 (4")
                        s.toString() == "3" -> etPhoneNumber.setText("+7 (3")
                        s.toString() == "2" -> etPhoneNumber.setText("+7 (2")
                        s.toString() == "1" -> etPhoneNumber.setText("+7 (1")
                        s.toString() == "0" -> etPhoneNumber.setText("+7 (0")

                        s.toString() == "+9" -> etPhoneNumber.setText("+7 (9")
                        s.toString() == "+8" -> etPhoneNumber.setText("+7 (8")
                        s.toString() == "+7" -> etPhoneNumber.setText("+7 (")
                        s.toString() == "+6" -> etPhoneNumber.setText("+7 (6")
                        s.toString() == "+5" -> etPhoneNumber.setText("+7 (5")
                        s.toString() == "+4" -> etPhoneNumber.setText("+7 (4")
                        s.toString() == "+3" -> etPhoneNumber.setText("+7 (3")
                        s.toString() == "+2" -> etPhoneNumber.setText("+7 (2")
                        s.toString() == "+1" -> etPhoneNumber.setText("+7 (1")
                        s.toString() == "+0" -> etPhoneNumber.setText("+7 (0")

                        s.toString() == "+79" -> etPhoneNumber.setText("+7 (9")
                        s.toString() == "+78" -> etPhoneNumber.setText("+7 (8")
                        s.toString() == "+77" -> etPhoneNumber.setText("+7 (7")
                        s.toString() == "+76" -> etPhoneNumber.setText("+7 (6")
                        s.toString() == "+75" -> etPhoneNumber.setText("+7 (5")
                        s.toString() == "+74" -> etPhoneNumber.setText("+7 (4")
                        s.toString() == "+73" -> etPhoneNumber.setText("+7 (3")
                        s.toString() == "+72" -> etPhoneNumber.setText("+7 (2")
                        s.toString() == "+71" -> etPhoneNumber.setText("+7 (1")
                        s.toString() == "+70" -> etPhoneNumber.setText("+7 (0")

                        s.toString().length == 7 -> etPhoneNumber.setText("${etPhoneNumber.text}) ")
                        s.toString().length == 12 -> etPhoneNumber.setText("${etPhoneNumber.text}-")
                        s.toString().length == 15 -> etPhoneNumber.setText("${etPhoneNumber.text}-")


                    }
                }
                phoneNumberLength = etPhoneNumber.text.length
                etPhoneNumber.setSelection(etPhoneNumber.length())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


    }

    override fun onBackPressed() {
        try {
            super.onBackPressed()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    fun done(v: View) {

        if (!hasConnection(this@SecondActivity)) {
            alert("Отсутствует интернет соединение", "Операция невозможна") { yesButton { } }.show()
            return
        }

        val a = etPhoneNumber.text
        var count = 0

        for (i in 0 until a.length) {
            if (a[i] == '+') count++
        }

        if (etPhoneNumber.text.length == 18
            && etPhoneNumber.text.toString()[0] == '+'
            && etPhoneNumber.text.toString()[1] == '7'
            && etPhoneNumber.text.isNotEmpty()
            && count == 1
        ) {

            val string = etPhoneNumber.text.toString()
            phone = string.replace("[^0-9+]".toRegex(), "")
            code = Random.nextInt(1000, 9999).toString()

            repository.sendSms(phoneNumber = phone, message = code)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    showCodeAlert(message = "", title = "Введите код из SMS", hint = "")
                    toast(code)
                }
                .subscribe()


        } else {

            toast("Некорректный номер телефона")

        }
    }

    private fun showCodeAlert(message: String, title: String, hint: String) {
        alert(message = message, title = title) {
            customView {
                val a = editText()
                a.setSelection(0)
                a.hint = hint
                a.inputType = InputType.TYPE_CLASS_NUMBER
                a.gravity = Gravity.CENTER
                a.filters = arrayOf(InputFilter.LengthFilter(4))

                yesButton {
                    if (a.text.toString() != code) {
                        showCodeAlert(message = "", title = "Введите код из SMS", hint = "Неверный код")
                    } else {
                        repository
                            .sendSms(phoneNumber = "+79628003000", message = generateMessage())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnComplete {
                                alert(
                                    message = "В ближашее время оператор с вами свяжется",
                                    title = "Спасибо за заказ"
                                ) {
                                    yesButton { startActivity(Intent(this@SecondActivity, MainActivity::class.java)) }
                                }.show()
                            }
                            .subscribe()
                    }
                }
            }
            noButton { toast("No") }
        }.show()
    }

    private fun generateMessage(): String =
        "Разрез: $cuts" +
                "\nМарка: $coalMark" +
                "\nМасса: $weight тонн" +
                "\nАдресс: $address" +
                "\nРасстояние: $distance км" +
                "\nЦена за тонну: $price рублей" +
                "\nЦена доставки: $deliveryCost рублей" +
                "\nОбщая цена $overPrice рублей" +
                "\nТелефон: $phone"
}
