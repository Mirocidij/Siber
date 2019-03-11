package sergey.yatsutko.siberiancoal

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import sergey.yatsutko.siberiancoal.helpful.InputFilterMinMax
import sergey.yatsutko.siberiancoal.helpful.selectEntries

class MainActivity : AppCompatActivity() {

    // Prices for coal
    private val prices = intArrayOf(1700, 1800, 1900, 2000)
    // Default price
    private var price = 0
    // Price depend on weight
    private var priceForWeight = 0
    // Coal weight
    private var weigth = 0
    // Distance between A and B
    private var km = 0f
    // For google map
    private var a = FloatArray(size = 3)
    // Users coordinates
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    // Cuts coordinates
    private val cuts = arrayOf(
        arrayOf(53.402971, 53.529799, 53.759367, 53.326586, 53.630114),
        arrayOf(91.083748, 91.410684, 01.061604, 91.361016, 91.436063)
    )
    // :D
    private var spinningCoal: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etDistance.hint = "0.0 km"
        etCoastForDelivery.hint = "0.0 рублей"

        //Inform Edit Text
        val etCoast = findViewById<EditText>(R.id.etCoast)
        val etCoast2 = findViewById<EditText>(R.id.etCoast2)

        etWeight.filters = arrayOf(InputFilterMinMax(0, 40), InputFilter.LengthFilter(2))

        etWeight.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                try {
                    weigth = Integer.parseInt(etWeight.text.toString())
                } catch (e: NumberFormatException) {

                }

                priceForWeight = try {
                    when (weigth) {
                        in 1..3 -> 10
                        in 4..7 -> 15
                        in 8..20 -> 35
                        in 21..40 -> 80
                        else -> 0
                    }
                } catch (e: Throwable) {
                    0
                }

                etCoastForDelivery.hint = "${km * priceForWeight} рублей"
                etCoastFor.hint = "${km * priceForWeight + price * weigth} рублей"
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        firmSpiner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                itemSelected: View, selectedItemPosition: Int, selectedId: Long
            ) {
                val choose = resources.getStringArray(R.array.firms)

                when {
                    choose[selectedItemPosition] == "Кирбинский разрез" -> coalSpinner.adapter =
                        selectEntries(this@MainActivity, R.array.Kirbinsky)
                    choose[selectedItemPosition] == "Черногорский разрез" -> coalSpinner.adapter =
                        selectEntries(this@MainActivity, R.array.Chernogorsky)
                    choose[selectedItemPosition] == "Изыхский разрез" -> coalSpinner.adapter =
                        selectEntries(this@MainActivity, R.array.Izihsky)
                    choose[selectedItemPosition] == "Восточнобейский разрез" -> coalSpinner.adapter =
                        selectEntries(this@MainActivity, R.array.Vostochnobeysky)
                    choose[selectedItemPosition] == "Белоярский разрез" -> coalSpinner.adapter =
                        selectEntries(this@MainActivity, R.array.Beloyarsky)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        coalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                itemSelected: View,
                selectedItemPosition: Int,
                selectedId: Long
            ) {
                price = if (selectedItemPosition % 2 == 0) {
                    prices[selectedItemPosition] - 5 * selectedItemPosition
                } else {
                    prices[selectedItemPosition] + 5 * selectedItemPosition
                }
                etCoast2.hint = "${coalSpinner.selectedItem}:"
                etCoast.hint = "$price руб/т"
                etCoastFor.hint = "${km * priceForWeight + price * weigth} рублей"


            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) {
            return
        }

        latitude = data.extras.getDouble("latitude")
        longitude = data.extras.getDouble("longitude")

        Location.distanceBetween(latitude, longitude, cuts[0][0], cuts[0][1], a)
        km = a[0] / 50000

        try {
            weigth = Integer.parseInt(etWeight.text.toString())
        } catch (e: NumberFormatException) {

        }

        priceForWeight = try {
            when (weigth) {
                in 1..3 -> 10
                in 4..7 -> 15
                in 8..20 -> 35
                in 21..40 -> 80
                else -> 0
            }
        } catch (e: Throwable) {
            0
        }

        etDistance.hint = "$km km"

        etCoastFor.hint = "${km * priceForWeight + price * weigth} рублей"

        etCoastForDelivery.hint = "${km * priceForWeight} рублей"

        Log.d("etWeight", "Correct Address")
    }

    fun goNextActivity(v: View) {

        val nextIntent = Intent(this@MainActivity, SecondActivity::class.java)

        if (etWeight.text.toString() == "0" || etWeight.text.toString() == "00" || etWeight.text.toString().isEmpty()) {
            Toast.makeText(this@MainActivity, "Некорректная масса", Toast.LENGTH_SHORT).show()
            Log.d("etWeight", "IsEmptyWeight")
            return
        } else {
            Log.d("etWeight", "NonEmptyWeight")

        }

        if (latitude == 0.0 && longitude == 0.0) {
            Toast.makeText(this@MainActivity, "Не выбрано место доставки", Toast.LENGTH_SHORT).show()
            Log.d("etWeight", "IncorrectAddress")
            return
        } else {
            Log.d("etWeight", "CorrectAddress")
        }

        nextIntent.putExtra("Cuts", firmSpiner.selectedItem.toString())
        nextIntent.putExtra("CoalMark", coalSpinner.selectedItem.toString())
        nextIntent.putExtra("Weight", etWeight.text.toString())
        nextIntent.putExtra("price", price)
        nextIntent.putExtra("km", km)


        startActivity(nextIntent)
    }

    fun coalRotate(v: View) {
        val coal = findViewById<ImageView>(R.id.coalPic)

        if (!spinningCoal) {
            val pointWidth = (coal.width / 2).toFloat()
            val pointHeight = (coal.height / 2).toFloat()
            val rotation = RotateAnimation(0f, 360f, pointWidth, pointHeight)

            rotation.duration = 400
            rotation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    spinningCoal = true
                }

                override fun onAnimationEnd(animation: Animation) {
                    spinningCoal = false
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
            coal.startAnimation(rotation)
        }


    }

    fun goMap(v: View) {
        val intent = Intent(
            this@MainActivity,
            MapsActivity::class.java
        )
        startActivityForResult(intent, 1)
    }

}