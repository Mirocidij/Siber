package sergey.yatsutko.siberiancoal

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import kotlinx.android.synthetic.main.activity_second.*
import org.jetbrains.anko.*
import sergey.yatsutko.siberiancoal.Sms.SmsService
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

    fun Done(v: View) {

        if (etPhoneNumber.text.length == 18 && etPhoneNumber.text.toString()[0] == '+' && etPhoneNumber.text.toString()[1] == '7' && etPhoneNumber.text.isNotEmpty()) {
            val string = etPhoneNumber.text.toString()
            phone = string.replace("[^0-9+]".toRegex(), "")
            code = Random.nextInt(1000, 9999).toString()
            toast(code)

            SmsService.instance.SendSms(code, phone)
            showAlert(message = "", title = "Введите код из SMS", hint = "")

        } else {

            toast("Некорректный номер телефона")

        }
    }


    fun showAlert(message: String, title: String, hint: String) {
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
                        showAlert(message = "", title = "", hint = "Неверный код")
                    } else {
                        val mes = "Разрез: $cuts" +
                                "\nМарка: $coalMark" +
                                "\nМасса: $weight ${
                                if (weight.toInt() < 5) {
                                    "тонны"
                                } else "тонн"
                                }" +
                                "\nАдресс: $address" +
                                "\nРасстояние: ${distance.toInt()} км" +
                                "\nЦена за тонну: ${price} рублей" +
                                "\nЦена доставки: ${deliveryCost.toInt()} рублей" +
                                "\nОбщая цена ${overPrice.toInt()} рублей" +
                                "\nТелефон: $phone"
                        SmsService.instance.SendSms(mes, "+79628003000")

                        alert(message = "В ближашее время оператор с вами свяжется", title = "Спасибо за заказ") {
                            yesButton { startActivity(Intent(this@SecondActivity, MainActivity::class.java)) }
                        }.show()
                    }
                }
            }

            noButton { toast("No") }
        }.show()
    }

}
