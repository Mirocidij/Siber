package sergey.yatsutko.siberiancoal.commons

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.ArrayAdapter

fun selectEntries(context: Context, textArrayResId: Int): ArrayAdapter<CharSequence> {
    val adapter =
        ArrayAdapter.createFromResource(
            context,
            textArrayResId,
            android.R.layout.simple_spinner_item
        )
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    return adapter
}

fun hasConnection(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var wifiInfo: NetworkInfo? = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
    if (wifiInfo != null && wifiInfo.isConnected) {
        return true
    }
    wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
    if (wifiInfo != null && wifiInfo.isConnected) {
        return true
    }
    wifiInfo = cm.activeNetworkInfo
    return wifiInfo != null && wifiInfo.isConnected
}