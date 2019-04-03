package sergey.yatsutko.siberiancoal.presentation.UI

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.widget.Toast
import sergey.yatsutko.siberiancoal.R


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        // Add a marker in Sydney and move the camera
        val abakan = LatLng(53.717647, 91.429705)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(abakan, 14.5f))

        mMap.setOnMapClickListener { point ->
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(point))
            longitude = point.longitude
            latitude = point.latitude
        }


    }

    fun accept(v: View) {


        try {
            if (latitude == 0.0 && longitude == 0.0) {
                Toast.makeText(this, "Не выбрано место доставки", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(
                    this@MapsActivity,
                    MainActivity::class.java
                )
                intent.putExtra("longitude", longitude)
                intent.putExtra("latitude", latitude)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

    }
}
