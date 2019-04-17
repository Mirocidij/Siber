package sergey.yatsutko.siberiancoal.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_second.*
import org.jetbrains.anko.*
import sergey.yatsutko.siberiancoal.R
import sergey.yatsutko.siberiancoal.data.entity.CoalOrder
import sergey.yatsutko.siberiancoal.presentation.presenters.second.SecondPresenter
import sergey.yatsutko.siberiancoal.presentation.presenters.second.SecondView

class SecondActivity : MvpAppCompatActivity(), SecondView {

    @InjectPresenter
    lateinit var presenter: SecondPresenter

    @ProvidePresenter
    fun provideSecondPresenter(): SecondPresenter {
        val coalOrder = intent.getSerializableExtra("coalOrder") as CoalOrder
        return SecondPresenter(coalOrder = coalOrder, context = this@SecondActivity)
    }

    private var phoneNumberLength = -1
    private val TAG = "SecondActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        etPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                var editPhone = s.toString()
                if (phoneNumberLength < s.toString().length) {
                    phoneNumberLength = s.toString().length
                    Log.d(TAG, "In if")

                    editPhone = when (s.toString()) {
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
                    editPhone = when (editPhone.length) {
                        7 -> "$editPhone) "
                        12 -> "$editPhone-"
                        15 -> "$editPhone-"
                        else -> editPhone
                    }
                    etPhoneNumber.setText(editPhone)
                }
                phoneNumberLength = editPhone.length
                etPhoneNumber.setSelection(etPhoneNumber.length())
            }
        })

        fabDone.setOnClickListener {
            presenter.onSendOrder(etPhoneNumber.text.toString())
        }
    }

    override fun onBackPressed() {
        try {
            super.onBackPressed()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    override fun showCodeAlert(message: String, title: String, hint: String) {
        alert(message = message, title = title) {
            customView {
                val a = editText()
                a.setSelection(0)
                a.hint = hint
                a.inputType = InputType.TYPE_CLASS_NUMBER
                a.gravity = Gravity.CENTER
                a.filters = arrayOf(InputFilter.LengthFilter(4))

                yesButton {
                    presenter.onSendConfirmationCode(code = a.text.toString())
                }
            }
            noButton { toast("No") }
        }.show()
    }

    override fun showInformAlert(titleRes: Int, messageRes: Int) {
        alert(
            message = getString(R.string.userInformMessage),
            title = getString(R.string.userInformTitle)
        )
        {
            yesButton {
                presenter.onSendConfirmationOrder()
            }
        }.show()
    }

    override fun finishOrder() {
        startActivity(Intent(this@SecondActivity, MainActivity::class.java))
    }

    override fun showCode(code: String) {
        toast(code)
    }

    override fun updateFields(coalOrder: CoalOrder) {
        etCutsInfo2.hint = coalOrder.coalFirm
        etCoalInfo2.hint = coalOrder.coalMark
        if (coalOrder.weight < 5) {
            etWeight2.hint = "${coalOrder.weight} тонны"
        } else {
            etWeight2.hint = "${coalOrder.weight} тонн"
        }
        etPriceInfo2.hint = "${coalOrder.pricePerTonn} рублей"
        etDistance2.hint = "${coalOrder.distance} km"
        etOverPrice2.hint = "${coalOrder.overPrice} рублей"
        etDeliveryCost2.hint = "${coalOrder.deliveryCost} рублей"
        etAddress2.setText(coalOrder.address)
    }

    override fun showAlert(titleRes: Int, messageRes: Int) {
        alert(
            message = getString(messageRes),
            title = getString(titleRes)
        ) {
            yesButton { }
        }.show()
    }

}
