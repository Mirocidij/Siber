package sergey.yatsutko.siberiancoal.helpful

import android.text.Spanned
import android.text.InputFilter


class InputFilterMinMax(private val minimumValue: Int, private val maximumValue: Int) : InputFilter {

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val input =
                Integer.parseInt(dest.subSequence(0, dstart).toString() + source + dest.subSequence(dend, dest.length))
            if (isInRange(minimumValue, maximumValue, input))
                return null
        } catch (nfe: NumberFormatException) {
        }

        return ""
    }

    private fun isInRange(a: Int, b: Int, c: Int): Boolean {
        return if (b > a) c >= a && c <= b else c >= b && c <= a
    }

}