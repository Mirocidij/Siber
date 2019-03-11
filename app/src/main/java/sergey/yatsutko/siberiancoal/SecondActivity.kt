package sergey.yatsutko.siberiancoal

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import kotlinx.android.synthetic.main.activity_second.*
import org.jetbrains.anko.toast

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val cuts = intent.getStringExtra("Cuts")
        etCutsInfo2.hint = "$cuts"
        val coalMark = intent.getStringExtra("CoalMark")
        etCoalInfo2.hint = (coalMark)

        val weight = intent.getStringExtra("Weight")
        if (Integer.parseInt(weight) < 5) {
            etWeight2.hint = "$weight тонны"
        } else {
            etWeight2.hint = "$weight тонн"
        }

        val price = intent.getIntExtra("price", 0)
        etPriceInfo2.hint = "$price рублей"
        val km = intent.getFloatExtra("km", 0f)
        etDistance2.hint = "$km km"
        etOverPrice2.hint = "${intent.getFloatExtra("overPrice", 0f)}"
        etDeliveryCost2.hint = "${intent.getFloatExtra("deliveryCost", 0f)}"


        etPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                when {
                    etPhoneNumber.text.toString() == "7" -> etPhoneNumber.setText("+7")
                    etPhoneNumber.text.toString() == "8" -> etPhoneNumber.setText("+7")
                    etPhoneNumber.text.toString() == "9" -> etPhoneNumber.setText("+79")
                    etPhoneNumber.text.toString() == "6" -> etPhoneNumber.setText("+76")
                    etPhoneNumber.text.toString() == "5" -> etPhoneNumber.setText("+75")
                    etPhoneNumber.text.toString() == "4" -> etPhoneNumber.setText("+74")
                    etPhoneNumber.text.toString() == "3" -> etPhoneNumber.setText("+73")
                    etPhoneNumber.text.toString() == "2" -> etPhoneNumber.setText("+72")
                    etPhoneNumber.text.toString() == "1" -> etPhoneNumber.setText("+71")
                    etPhoneNumber.text.toString() == "0" -> etPhoneNumber.setText("+70")

                    etPhoneNumber.text.toString() == "+6" -> etPhoneNumber.setText("+76")
                    etPhoneNumber.text.toString() == "+5" -> etPhoneNumber.setText("+75")
                    etPhoneNumber.text.toString() == "+4" -> etPhoneNumber.setText("+74")
                    etPhoneNumber.text.toString() == "+3" -> etPhoneNumber.setText("+73")
                    etPhoneNumber.text.toString() == "+2" -> etPhoneNumber.setText("+72")
                    etPhoneNumber.text.toString() == "+1" -> etPhoneNumber.setText("+71")
                    etPhoneNumber.text.toString() == "+0" -> etPhoneNumber.setText("+70")

                }
                etPhoneNumber.setSelection(etPhoneNumber.length())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })



    }

    fun Done(v: View) {
        val string = etPhoneNumber2.text.toString()
        if (string.length < 12 && string[0] != '+' && string[1] != '7') {
            toast("Некорректный номер телефона")
        }

    }
}
