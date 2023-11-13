package mx.edu.itl.polarisapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.graphics.rotationMatrix
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.ar.sceneform.AnchorNode
import mx.edu.itl.polarisapp.ar.PlacesArFragment
import mx.edu.itl.polarisapp.ar.PlaceNode
import mx.edu.itl.polarisapp.model.Place

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var arFragment : PlacesArFragment
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var map            : GoogleMap?  = null
    private var curretLocation : Location?   = null
    private var anchorNode     : AnchorNode? = null

    //Variables de los sensores
    private lateinit var  sensorManager :SensorManager
    private val accelerometerReading = FloatArray( 3 )
    private val magnometerReading    = FloatArray( 3 )
    private val rotatinMatrix        = FloatArray( 9 )
    private val orientationAngles    = FloatArray( 3 )
    //----------------------------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUp()
    }
    //----------------------------------------------------------------------------------------------
    override fun onResume() {
        super.onResume()
        subscribeToSensors()
    }
    //----------------------------------------------------------------------------------------------
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener( this )
    }
    //----------------------------------------------------------------------------------------------
    private fun setUp(){
        setUpSensors()
        setUpMap()
        setUpAr()
    }
    //----------------------------------------------------------------------------------------------
    //Configuracion de los sensores
    //----------------------------------------------------------------------------------------------
    private fun setUpSensors(){
        sensorManager = getSystemService()!!

    }
    //----------------------------------------------------------------------------------------------
    //Lecturas de los sensores
    //----------------------------------------------------------------------------------------------
    private fun subscribeToSensors(){
        sensorManager.getDefaultSensor( Sensor.TYPE_MAGNETIC_FIELD )?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        sensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER )?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }
    //----------------------------------------------------------------------------------------------
    //Se obtienen las lecturas de los sensores
    //----------------------------------------------------------------------------------------------
    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        if( event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD ){
            System.arraycopy( event.values, 0,
                magnometerReading, 0, magnometerReading.size )
        } else if ( event.sensor.type == Sensor.TYPE_ACCELEROMETER )(
            System.arraycopy( event.values, 0,
                accelerometerReading, 0, accelerometerReading.size )
        )
        SensorManager.getRotationMatrix( rotatinMatrix,
            null,
            accelerometerReading,
            magnometerReading )
        SensorManager.getOrientation( rotatinMatrix, orientationAngles )

    }
    //----------------------------------------------------------------------------------------------
    //No se hace nada
    //----------------------------------------------------------------------------------------------
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d( "MainActivity", accuracy.toString() )
    }
    //----------------------------------------------------------------------------------------------
    //Se enfoca el mapa dependiendo de tu ubicacion, y se hace zoom alrededor de tu en 13m a la
    //redonda
    //----------------------------------------------------------------------------------------------
    private fun setUpMap(){
        mapFragment = supportFragmentManager.findFragmentById( R.id.mapFragment ) as SupportMapFragment
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( this )
        mapFragment.getMapAsync{ googleMap ->
            map = googleMap
            ifLocationIsGranted @SuppressLint( "MissingPermission" ) {
                googleMap.isMyLocationEnabled = true
                getCurrentLocation { location ->
                    curretLocation = location
                    val latLng   = LatLng( location.latitude, location.longitude )
                    val position = CameraPosition.fromLatLngZoom( latLng, 13f )
                    googleMap.moveCamera( CameraUpdateFactory.newCameraPosition( position ) )
                }

            }

        }
    }
    //Se obtiene la ubicacion actual
    //----------------------------------------------------------------------------------------------
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
                val lastLocation = locationResult?.lastLocation
                if( lastLocation != null && lastLocation.accuracy <= 15f ) {
                    onSuccess.invoke( lastLocation )
                    fusedLocationProviderClient.removeLocationUpdates( this )
                }
            }
        }, Looper.getMainLooper() )
    }
    //Se prepara el ARCore, y se añaden pines en el mapa que se podran ver
    //----------------------------------------------------------------------------------------------
    private fun setUpAr(){
        arFragment = supportFragmentManager.findFragmentById( R.id.arFragment ) as PlacesArFragment
        arFragment.setOnTapArPlaneListener{ hitResult, _, _ ->
            val anchor = hitResult.createAnchor()
            anchorNode = AnchorNode( anchor )
            anchorNode?.setParent( arFragment.arSceneView.scene )
            anchorNode?.let{  addPlaces( it ) }

        }
    }
    //Añade lugares que se ubicaran en el mapa y en el AR
    //----------------------------------------------------------------------------------------------
    private fun addPlaces( anchorNode: AnchorNode ){
        val places = listOf(
            Place( "Edificio 19"           , LatLng( 25.533261,-103.435979 ) ),
            Place( "Laboratorio de Computo", LatLng( 25.532719, -103.435974 ) )
        )
        places.forEach { place ->
            addPlaceToMap( place )
            addPlaceToAr( place, anchorNode )
        }
    }
    //Ubica el pin en el mapa
    //----------------------------------------------------------------------------------------------
    private fun addPlaceToMap( place: Place ){
        map?.let { googleMap ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position( place.latLng )
                    .title   ( place.name   )
            )
            marker?.apply {
                tag = place
            }
        }
    }
    //Ubica el pin en el AR
    //----------------------------------------------------------------------------------------------
    private fun addPlaceToAr( place: Place, anchorNode: AnchorNode ){
        val placeNode = PlaceNode( this, place )
        placeNode.setParent( anchorNode )
        curretLocation?.let{
            val latLng = LatLng( it.latitude, it.longitude )
            placeNode.localPosition = place.getPositionVector( orientationAngles[ 0 ], latLng )

        }
    }
    //----------------------------------------------------------------------------------------------
    //Verifica que esten activados los permisos de ubicacion
    //----------------------------------------------------------------------------------------------
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
    //----------------------------------------------------------------------------------------------


}