package sergey.yatsutko.siberiancoal.presentation.UI

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_second.*
import org.jetbrains.anko.*
import sergey.yatsutko.siberiancoal.R
import sergey.yatsutko.siberiancoal.commons.hasConnection
import sergey.yatsutko.siberiancoal.data.network.Sms.SmsService
import kotlin.random.Random

class SecondActivity : AppCompatActivity() {

    var phoneNumberLength = -1
    var code = "0"


    private var cuts = ""
    private var coalMark = ""
    private var weight = ""
    private var price = 0
    private var distance = 0
    private var phone = ""
    private var overPrice = 0
    private var deliveryCost = 0
    private var address = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        cuts = intent.getStringExtra("Cuts")
        coalMark = intent.getStringExtra("CoalMark")
        weight = intent.getStringExtra("Weight")
        price = intent.getIntExtra("price", 0)
        distance = intent.getIntExtra("km", 0)
        overPrice = intent.getIntExtra("overPrice", 0)
        deliveryCost = intent.getIntExtra("deliveryCost", 0)
        address = intent.getStringExtra("address")

        etCutsInfo2.hint = "$cuts"
        etCoalInfo2.hint = (coalMark)

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

                if (phoneNumberLength < etPhoneNumber.text.length) {
                    when {
                        etPhoneNumber.text.toString() == "+" -> etPhoneNumber.setText("+7 (")
                        etPhoneNumber.text.toString() == "7" -> etPhoneNumber.setText("+7 (")
                        etPhoneNumber.text.toString() == "8" -> etPhoneNumber.setText("+7 (")
                        etPhoneNumber.text.toString() == "9" -> etPhoneNumber.setText("+7 (9")
                        etPhoneNumber.text.toString() == "6" -> etPhoneNumber.setText("+7 (6")
                        etPhoneNumber.text.toString() == "5" -> etPhoneNumber.setText("+7 (5")
                        etPhoneNumber.text.toString() == "4" -> etPhoneNumber.setText("+7 (4")
                        etPhoneNumber.text.toString() == "3" -> etPhoneNumber.setText("+7 (3")
                        etPhoneNumber.text.toString() == "2" -> etPhoneNumber.setText("+7 (2")
                        etPhoneNumber.text.toString() == "1" -> etPhoneNumber.setText("+7 (1")
                        etPhoneNumber.text.toString() == "0" -> etPhoneNumber.setText("+7 (0")

                        etPhoneNumber.text.toString() == "+9" -> etPhoneNumber.setText("+7 (9")
                        etPhoneNumber.text.toString() == "+8" -> etPhoneNumber.setText("+7 (8")
                        etPhoneNumber.text.toString() == "+7" -> etPhoneNumber.setText("+7 (")
                        etPhoneNumber.text.toString() == "+6" -> etPhoneNumber.setText("+7 (6")
                        etPhoneNumber.text.toString() == "+5" -> etPhoneNumber.setText("+7 (5")
                        etPhoneNumber.text.toString() == "+4" -> etPhoneNumber.setText("+7 (4")
                        etPhoneNumber.text.toString() == "+3" -> etPhoneNumber.setText("+7 (3")
                        etPhoneNumber.text.toString() == "+2" -> etPhoneNumber.setText("+7 (2")
                        etPhoneNumber.text.toString() == "+1" -> etPhoneNumber.setText("+7 (1")
                        etPhoneNumber.text.toString() == "+0" -> etPhoneNumber.setText("+7 (0")

                        etPhoneNumber.text.toString() == "+79" -> etPhoneNumber.setText("+7 (9")
                        etPhoneNumber.text.toString() == "+78" -> etPhoneNumber.setText("+7 (8")
                        etPhoneNumber.text.toString() == "+77" -> etPhoneNumber.setText("+7 (7")
                        etPhoneNumber.text.toString() == "+76" -> etPhoneNumber.setText("+7 (6")
                        etPhoneNumber.text.toString() == "+75" -> etPhoneNumber.setText("+7 (5")
                        etPhoneNumber.text.toString() == "+74" -> etPhoneNumber.setText("+7 (4")
                        etPhoneNumber.text.toString() == "+73" -> etPhoneNumber.setText("+7 (3")
                        etPhoneNumber.text.toString() == "+72" -> etPhoneNumber.setText("+7 (2")
                        etPhoneNumber.text.toString() == "+71" -> etPhoneNumber.setText("+7 (1")
                        etPhoneNumber.text.toString() == "+70" -> etPhoneNumber.setText("+7 (0")


                        etPhoneNumber.text.length == 7 -> etPhoneNumber.setText("${etPhoneNumber.text}) ")
                        etPhoneNumber.text.length == 12 -> etPhoneNumber.setText("${etPhoneNumber.text}-")
                        etPhoneNumber.text.length == 15 -> etPhoneNumber.setText("${etPhoneNumber.text}-")


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

            SmsService
                .instance
                .jsonApi
                .sendSms(phone, code)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {

                }
                .doOnComplete {

                    showAlert(message = "", title = "Введите код из SMS", hint = "")
                    toast(code)

                }
                .subscribeOn(Schedulers.io())
                .subscribe()


        } else {

            toast("Некорректный номер телефона")

        }
    }

    private fun showAlert(message: String, title: String, hint: String) {
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
                        showAlert(message = "", title = "Введите код из SMS", hint = "Неверный код")
                    } else {
                        val mes = "Разрез: $cuts" +
                                "\nМарка: $coalMark" +
                                "\nМасса: $weight тонн" +
                                "\nАдресс: $address" +
                                "\nРасстояние: $distance км" +
                                "\nЦена за тонну: $price рублей" +
                                "\nЦена доставки: $deliveryCost рублей" +
                                "\nОбщая цена $overPrice рублей" +
                                "\nТелефон: $phone"

                        SmsService
                            .instance
                            .jsonApi
                            .sendSms("+79628003000", mes)
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe {

                            }.doOnComplete {
                                alert(
                                    message = "В ближашее время оператор с вами свяжется",
                                    title = "Спасибо за заказ"
                                ) {
                                    yesButton { startActivity(Intent(this@SecondActivity, MainActivity::class.java)) }
                                }.show()
                            }.subscribeOn(Schedulers.io())
                            .subscribe()
                    }
                }
            }
            noButton { toast("No") }
        }.show()
    }
}
