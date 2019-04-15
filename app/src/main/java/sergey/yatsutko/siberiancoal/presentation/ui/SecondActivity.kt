package sergey.yatsutko.siberiancoal.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_second.*
import org.jetbrains.anko.*
import sergey.yatsutko.siberiancoal.R
import sergey.yatsutko.siberiancoal.data.entity.CoalOrder
import sergey.yatsutko.siberiancoal.data.repository.SmsApiRepository
import sergey.yatsutko.siberiancoal.presentation.presenters.second.SecondPresenter
import sergey.yatsutko.siberiancoal.presentation.presenters.second.SecondView
import kotlin.random.Random

class SecondActivity : MvpAppCompatActivity(), SecondView {

    @InjectPresenter
    lateinit var presenter: SecondPresenter

    @ProvidePresenter
    fun provideSecondPresenter(): SecondPresenter {
        val coalOrder = intent.getSerializableExtra("coalOrder") as CoalOrder
        return SecondPresenter(coalOrder = coalOrder, context = this@SecondActivity)
    }

    val TAG = "SecondActivity"

    private val repository: SmsApiRepository = SmsApiRepository()

    var code = "0"

    private var phone = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        etPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, "Длинна новой строки ${s.toString()}")
                presenter.phoneNumberWasChanged(s.toString())
                etPhoneNumber.setSelection(etPhoneNumber.length())
            }
        })

        fabDone.setOnClickListener {
            presenter.doneButtonWasPressed()
        }
    }

    override fun onBackPressed() {
        try {
            super.onBackPressed()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    fun done(v: View) {
        presenter.doneButtonWasPressed()

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

    override fun changePhoneNumber(number: String) {
        etPhoneNumber.setText(number)
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

    override fun showError(titleRes: Int, messageRes: Int) {
        alert(message = getString(messageRes), title = getString(titleRes)) { yesButton {  } }.show()
    }
}
