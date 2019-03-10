package sergey.yatsutko.siberiancoal

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        etPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                when {
                    etPhoneNumber.text.toString() == "7" -> etPhoneNumber.setText("+7")
                    etPhoneNumber.text.toString() == "8" -> etPhoneNumber.setText("+7")
                    etPhoneNumber.text.toString() == "9" -> etPhoneNumber.setText("+79")
                    etPhoneNumber.text.toString() == "6" -> etPhoneNumber.setText("+7")
                    etPhoneNumber.text.toString() == "5" -> etPhoneNumber.setText("+7")
                    etPhoneNumber.text.toString() == "4" -> etPhoneNumber.setText("+7")
                    etPhoneNumber.text.toString() == "3" -> etPhoneNumber.setText("+7")
                    etPhoneNumber.text.toString() == "2" -> etPhoneNumber.setText("+7")
                    etPhoneNumber.text.toString() == "1" -> etPhoneNumber.setText("+7")
                    etPhoneNumber.text.toString() == "0" -> etPhoneNumber.setText("+7")

                }
                etPhoneNumber.setSelection(etPhoneNumber.length())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


    }

    fun Done(v: View) {

    }
}
