package sergey.yatsutko.siberiancoal.commons

import android.content.Context
import android.widget.ArrayAdapter

fun selectEntries(
    context: Context,
    textArrayResId: Int
): ArrayAdapter<CharSequence> {
    val adapter =
        ArrayAdapter.createFromResource(
            context,
            textArrayResId,
            android.R.layout.simple_spinner_item
        )
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    return adapter
}