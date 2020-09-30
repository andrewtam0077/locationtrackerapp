package com.WorkSummer2020.projectdembeone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.WorkSummer2020.projectdembeone.models.Satellite
import com.WorkSummer2020.projectdembeone.models.UserMap

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

private const val TAG = "DisplayMapActivity"
class DisplayMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var userMap: UserMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_map)

        userMap = intent.getSerializableExtra(EXTRA_USER_MAP) as UserMap
        supportActionBar?.title = userMap.title
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     *
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.i(TAG, "user map to render: ${userMap.title}")

        val boundsBuilder = LatLngBounds.Builder()
        //for loop to put out the location of the "satellites" onto markers
        for (satellite in userMap.places) {
            val latLng = LatLng(satellite.latitude, satellite.longitude)
            boundsBuilder.include(latLng)
            mMap.addMarker(MarkerOptions().position(latLng).title(satellite.title).snippet(satellite.description))
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(),1000,1000,0))
    }
}