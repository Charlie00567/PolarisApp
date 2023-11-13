package mx.edu.itl.polarisapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.health.connect.datatypes.ExerciseRoute
import android.location.Location
import android.location.LocationRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import mx.edu.itl.polarisapp.ar.PLacesArFragment
import kotlin.math.truncate

class MainActivity : AppCompatActivity() {

    private lateinit var arFragment : PLacesArFragment
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var map : GoogleMap? = null
    private var curretLocation : Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUp()
    }
    private fun setUp(){

    }
    private fun setUpMap(){
        mapFragment = supportFragmentManager.findFragmentById( R.id.mapFragment ) as SupportMapFragment
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( this )
        mapFragment.getMapAsync{ googleMap ->
            map = googleMap
            ifLocationIsGranted @SuppressLint( "MissingPermission" ) {
                googleMap.isMyLocationEnabled = true
                getCurrentLocation { location ->
                    val latLng   = LatLng( location.latitude, location.longitude )
                    val position = CameraPosition.fromLatLngZoom( latLng, 13f )
                    googleMap.moveCamera( CameraUpdateFactory.newCameraPosition( position ) )
                }

            }

        }
    }

    @SuppressLint( "MissingPermission" )
    private fun getCurrentLocation( onSuccess: ( Location ) -> Unit ){
        val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
            interval        = 100
            fastestInterval = 50
            priority        = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime     = 100
        }

        fusedLocationProviderClient.requestLocationUpdates( locationRequest, object: LocationCallback() {
            override fun onLocationResult( locationResult : LocationResult ) {
                super.onLocationResult( locationResult )
                /*if( locationResult.lastLocation.accuracy <= 15f ) {
                    onSuccess.invoke( locationResult.lastLocation )
                    fusedLocationProviderClient.removeLocationUpdates( this )
                }*/
            }
        }, Looper.getMainLooper() )
    }

    private fun setUpAr(){
        arFragment = supportFragmentManager.findFragmentById( R.id.arFragment ) as PLacesArFragment
    }
    private fun ifLocationIsGranted( onIsGranted: () -> Unit ){
        val isCoarseGranted = ContextCompat.checkSelfPermission( this,
             Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED
        val isfFineGranted  = ContextCompat.checkSelfPermission( this,
            Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED

        if( isCoarseGranted && isfFineGranted ){
            onIsGranted.invoke()
            return
        }
        Toast.makeText( this, "La ubicacion no esta permitida", Toast.LENGTH_LONG ).show();
    }


}