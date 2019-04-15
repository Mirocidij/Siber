package sergey.yatsutko.siberiancoal.presentation.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import org.jetbrains.anko.toast
import sergey.yatsutko.siberiancoal.R
import sergey.yatsutko.siberiancoal.presentation.presenters.maps.MapsPresenter
import sergey.yatsutko.siberiancoal.presentation.presenters.maps.MapsView


class MapsActivity : MvpAppCompatActivity(), MapsView, OnMapReadyCallback {

    @InjectPresenter
    lateinit var presenter: MapsPresenter

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

        // Add a marker in Abakan and move the camera
        val abakan = LatLng(53.717647, 91.429705)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(abakan, 14.5f))

        mMap.setOnMapClickListener { point ->
            presenter.pointWasChecked(point)
        }
    }

    fun accept(v: View) {
        presenter.acceptButtonWasPressed()
    }

    override fun refreshFlag(point: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(point))
    }

    override fun saveCoordinatesAndFinish(point: LatLng) {
        val intent = Intent(
            this@MapsActivity,
            MainActivity::class.java
        )
        intent.putExtra("longitude", point.longitude)
        intent.putExtra("latitude", point.latitude)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun showError(messageRes: Int) {
        toast(getText(messageRes))
    }
}
