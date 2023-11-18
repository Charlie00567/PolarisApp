/***************************************************************************************************
                  MainActivity.kt Última modificación: 18/Noviembre/2023
***************************************************************************************************/

package mx.edu.itl.polarisapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
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
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.ar.sceneform.AnchorNode
import mx.edu.itl.polarisapp.ar.PlaceNode
import mx.edu.itl.polarisapp.ar.PlacesArFragment
import mx.edu.itl.polarisapp.model.Edge
import mx.edu.itl.polarisapp.model.Nodo
import mx.edu.itl.polarisapp.model.Place
import mx.edu.itl.polarisapp.model.findShortestPath

class MainActivity : AppCompatActivity (), SensorEventListener {

    private lateinit var arFragment : PlacesArFragment
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private var map            : GoogleMap?  = null
    private var curretLocation : Location?   = null
    private var anchorNode     : AnchorNode? = null

    //----------------------------------------------------------------------------------------------

    // Variables para controlar las animaciones que tienen los botones
    private lateinit var fabPrincipal : FloatingActionButton
    private lateinit var fabSalir : FloatingActionButton
    private lateinit var fabCrearRuta : FloatingActionButton

    private lateinit var animAbrir : Animation
    private lateinit var animCerrar : Animation
    private lateinit var girarAdelante : Animation
    private lateinit var girarAtras : Animation

    private var isOpen = false

    //----------------------------------------------------------------------------------------------

    //Variables de los sensores
    private lateinit var  sensorManager : SensorManager
    private val accelerometerReading = FloatArray( 3 )
    private val magnometerReading    = FloatArray( 3 )
    private val rotatinMatrix        = FloatArray( 9 )
    private val orientationAngles    = FloatArray( 3 )

    // Declarar el AutoCompleteTextView como propiedad de la clase
    private lateinit var autotextview : AutoCompleteTextView

    val nodoEdificio19 = Nodo( "Edificio 19", "19", LatLng( 25.533261,-103.435979 ) )
    val nodoLabComputo = Nodo( "Lab. de Computo - AA","28", LatLng( 25.532719,-103.435974) )
    val nodoEntrada2= Nodo( "Entrada 2","Entrada2", LatLng( 25.533333821199893,-103.43444237040535) )
    val comedor = Nodo( "Comedor",  "comedor", LatLng( 25.533067,	-103.434961) )
    val nodoConexion1 = Nodo( "nodoConexion1", "1", LatLng( 25.533313082045854, -103.43614827619899) )
    val nodoConexion2 = Nodo( "nodoConexion2", "2", LatLng( 25.533042,-103.436103) )
    val nodoConexion3 = Nodo( "nodoConexion3", "3", LatLng( 25.5330,-103.4359) )
    val nodoConexion4 = Nodo( "nodoConexion4", "4", LatLng( 25.532879,-103.435926) )
    val nodoConexion5= Nodo( "nodoConexion5", "5", LatLng( 25.533235,-103.434958) )
    val nodoConexion6= Nodo( "nodoConexion6", "6", LatLng( 25.533273147908876,-103.43533422576532) )
    val nodoConexion7= Nodo( "nodoConexion7", "7", LatLng( 25.533063,-103.435279) )
    val nodoConexion8 = Nodo( "nodoConexion8", "8", LatLng( 25.53291979068679,	-103.43533154355852) )
    val nodoConexion9= Nodo( "nodoConexion9", "9", LatLng( 25.532879,	-103.435625) )


    val nodos = listOf (
         nodoEdificio19, nodoLabComputo, nodoEntrada2, comedor, nodoConexion1, nodoConexion2,
         nodoConexion3, nodoConexion4, nodoConexion5, nodoConexion6, nodoConexion7,
        nodoConexion8, nodoConexion9
    )

    val graph = mutableListOf (
        Edge( nodoEdificio19, nodoConexion1, nodoEdificio19.calcularDistancia( nodoConexion1 ).toInt() ),
        Edge( nodoConexion1, nodoConexion2, nodoConexion1.calcularDistancia( nodoConexion2 ).toInt() ),
        Edge( nodoConexion2, nodoConexion3, nodoConexion2.calcularDistancia( nodoConexion3 ).toInt() ),
        Edge( nodoConexion3, nodoConexion4, nodoConexion3.calcularDistancia( nodoConexion4 ).toInt() ),
        Edge( nodoConexion4, nodoLabComputo, nodoConexion4.calcularDistancia( nodoLabComputo ).toInt() ),
        Edge( nodoEntrada2, nodoConexion5, nodoEntrada2.calcularDistancia( nodoConexion5 ).toInt() ),
        Edge( nodoConexion5, nodoConexion6, nodoConexion5.calcularDistancia( nodoConexion6 ).toInt() ),
        Edge( nodoConexion6, nodoConexion7, nodoConexion6.calcularDistancia( nodoConexion7 ).toInt() ),
        Edge( nodoConexion7, nodoConexion8, nodoConexion7.calcularDistancia( nodoConexion8 ).toInt() ),
        Edge( nodoConexion8, nodoConexion9, nodoConexion8.calcularDistancia( nodoConexion9 ).toInt() ),
        Edge( nodoConexion9, nodoConexion4, nodoConexion9.calcularDistancia( nodoConexion4 ).toInt() ),

        Edge( nodoConexion4, nodoConexion3, nodoConexion4.calcularDistancia( nodoConexion3 ).toInt() ),
        Edge( nodoConexion3, nodoConexion2, nodoConexion3.calcularDistancia( nodoConexion2 ).toInt() ),
        Edge( nodoConexion2, nodoConexion1, nodoConexion2.calcularDistancia( nodoConexion1 ).toInt() ),
        Edge( nodoConexion1, nodoEdificio19, nodoConexion1.calcularDistancia( nodoEdificio19 ).toInt() ),
    )
   // val result = findShortestPath(graph,nodoEntrada2,nodoEdificio19)

    private var textoSeleccionado : String? = null

    //----------------------------------------------------------------------------------------------

    override fun onCreate( savedInstanceState: Bundle? ) {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.activity_main )

        fabPrincipal = findViewById<FloatingActionButton>( R.id.fabPrincipal )
        fabSalir = findViewById<FloatingActionButton>( R.id.fabSalir )
        fabCrearRuta = findViewById<FloatingActionButton>( R.id.fabCrearRuta )

        // Especificamos que animaciones se van a cargar con cada variable
        animAbrir = AnimationUtils.loadAnimation( this, R.anim.fab_open )
        animCerrar = AnimationUtils.loadAnimation( this, R.anim.fab_close )
        girarAdelante = AnimationUtils.loadAnimation( this, R.anim.rotate_forward )
        girarAtras = AnimationUtils.loadAnimation( this, R.anim.rotate_backward )

        setUp()

        //AutoCompleteTextView
        autotextview = findViewById<AutoCompleteTextView>( R.id.actvBuscador )
        val edificios = resources.getStringArray( R.array.edificios )
        val adapter = ArrayAdapter( this, android.R.layout.simple_list_item_1, edificios )
        autotextview.setAdapter( adapter )
        //Toast.makeText(this,"result.shortesPath(): "+ result.shortestPath().get(1).toString(), Toast.LENGTH_SHORT).show()


        autotextview.setOnItemClickListener { _, _, position, _ ->
            // Obtener el texto seleccionado
            textoSeleccionado = adapter.getItem( position ).toString()
            Toast.makeText( applicationContext, "Texto seleccionado: $textoSeleccionado", Toast.LENGTH_SHORT ).show()
            if( textoSeleccionado.equals( "" ) ) {
                textoSeleccionado= autotextview.text.toString()
            }
            //origenDestino(textoSeleccionado!!)
        }
       // Toast.makeText(this,"result.shortestDistance(): "+ result.shortestDistance(),Toast.LENGTH_LONG)
    }

    //----------------------------------------------------------------------------------------------

    override fun onResume () {
        super.onResume()
        subscribeToSensors()
    }

    //----------------------------------------------------------------------------------------------

    override fun onPause () {
        super.onPause()
        sensorManager.unregisterListener( this )
    }

    //----------------------------------------------------------------------------------------------

    private fun setUp (){
        setUpSensors()
        setUpMap()
        setUpAr()
    }

    //----------------------------------------------------------------------------------------------

    //Configuracion de los sensores
    private fun setUpSensors (){
        sensorManager = getSystemService()!!

    }

    //----------------------------------------------------------------------------------------------

    //Lecturas de los sensores
    private fun subscribeToSensors (){
        sensorManager.getDefaultSensor( Sensor.TYPE_MAGNETIC_FIELD )?.also {
            sensorManager.registerListener (
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
    override fun onSensorChanged( event: SensorEvent? ) {
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
    override fun onAccuracyChanged ( sensor: Sensor?, accuracy: Int ) {
        Log.d( "MainActivity", accuracy.toString() )
    }

    //----------------------------------------------------------------------------------------------

    //Se enfoca el mapa dependiendo de tu ubicacion, y se hace zoom alrededor de tu en 13m a la
    //redonda
    private fun setUpMap () {
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

    //----------------------------------------------------------------------------------------------

    //Se obtiene la ubicacion actual
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

    //----------------------------------------------------------------------------------------------

    //Se prepara el ARCore, y se añaden pines en el mapa que se podran ver
    private fun setUpAr () {
        arFragment = supportFragmentManager.findFragmentById( R.id.arFragment ) as PlacesArFragment
        arFragment.setOnTapArPlaneListener{ hitResult, _, _ ->
            val anchor = hitResult.createAnchor()
            anchorNode = AnchorNode( anchor )
            anchorNode?.setParent( arFragment.arSceneView.scene )
            anchorNode?.let{  addPlaces( it ) }

        }
    }

    //----------------------------------------------------------------------------------------------

    //Añade lugares que se ubicaran en el mapa y en el AR
    private fun addPlaces( anchorNode: AnchorNode ){
        val places = listOf(
            Place( "Edificio 19"           , LatLng( 25.533261,-103.435979 ) ),
            Place( "Laboratorio de Computo", LatLng( 25.532719, -103.435974 ) ),
            Place("Entrada 2", LatLng(nodoEntrada2.getLat(),nodoEntrada2.getLong())),
//            Place ("Edificio Administrativo - 1-A", LatLng(25.535215, -103.434899)),
//            Place ("Edificio Administrativo - 1-B", LatLng(25.534961, -103.434885)),
//            Place ("Metal - Mecánica, Aulas - 10", LatLng(25.534443, -103.436175)),
//            Place ("Lab. Ingenieria Eléctrica - 11", LatLng(25.534129, -103.436312)),
//            Place ("Lab. Quimica Cualitativa - 12", LatLng(25.53416, -103.435864)),
//            Place ("Cafeteria - 13", LatLng(25.534213, -103.435373)),
//            Place ("Quimica - Bioquimica, Lab. Quimica Inorganica, Lab. Física - Química, Aulas - 14", LatLng(25.534257, -103.434907)),
//            Place ("Aulas - 15", LatLng(25.533996, -103.434923)),
//            Place ("Lab. Química Cuantitativa - 16", LatLng(25.533943, -103.435631)),
//            Place ("Sala 'Garcia Siler', Centro de mejora Continua - 17", LatLng(25.533729, -103.436041)),
//            Place ("Lab. Ingenería Mecánica - A-B", LatLng(25.5337, -103.436355)),
//            Place ("Ingeniería en Sistemas Computacionalesm Aulas - 19", LatLng(25.533261, -103.435979)),
//            Place ("Gestion Tecnológica y vinculacion - 2", LatLng(25.535152, -103.433967)),
//            Place ("Lab. Ingeniría Electrónica - 20", LatLng(25.53329, -103.436317)),
//            Place ("Lab. Multifuncional Metal - Mecánica - 21", LatLng(25.533522, -103.435507)),
//            Place ("Lab. de Electrónica de Potencia, Lab. Electrónica Digital, Ciencias Básicas - 22", LatLng(25.533657, -103.434987)),
//            Place ("Sala de usos Multiples, Laboratorio CIM, Lab. Ingeniería Industrial, Comedor - CIM", LatLng(25.533067, -103.434961)),
//            Place ("Mantenimiento - 24", LatLng(25.532999, -103.435695)),
//            Place ("Lab. Ingeniería de Potencia - 25", LatLng(25.53313, -103.436682)),
//            Place ("Lab. de Máquinas Eléctricas e Instrumentación - 26", LatLng(25.532917, -103.436794)),
//            Place ("Lab. de Mecatrónica y Control - 27", LatLng(25.533024, -103.436328)),
//            Place ("Lab. de Computo - AA", LatLng(25.532719, -103.435974)),
//            Place ("Lab. de Idiomas - 29", LatLng(25.532583, -103.435620)),
//            Place ("Oficinas Sindicales - 3", LatLng(25.534922, -103.435229)),
//            Place ("Lab. de Cómputo de Posgrado - 30", LatLng(25.532738, -103.436821)),
//            Place ("División de Estudios de Posgrado e Investigación - 31", LatLng(25.532762, -103.436446)),
//            Place ("Aulas - 32", LatLng(25.532579, -103.436285)),
//            Place ("Sala de Técnicos, Laboratorio de Físicas - 33", LatLng(25.532713, -103.435127)),
//            Place ("Aulas - 34", LatLng(25.532496, -103.435132)),
//            Place ("Recuros Materiales y Servicios - 35", LatLng(25.532071, -103.435046)),
//            Place ("Ingeniria Eléctrica - Electrónia - 36", LatLng(25.532158, -103.435625)),
//            Place ("Aulas - 37", LatLng(25.531816, -103.435433)),
//            Place ("Aulas - 38", LatLng(25.531574, -103.435154)),
//            Place ("Gimnasio - 39", LatLng(25.529929, -103.435744)),
//            Place ("Ingenieria Industrial - 4", LatLng(25.534847, -103.435607)),
//            Place ("Gradas Deportivas - 40", LatLng(25.531212, -103.436178)),
//            Place ("Vestidores, Oficina Arte, Cultura, Gimnasio Pesas - 41", LatLng(25.530834, -103.435663)),
//            Place ("Gimnasio Auditorio, Actividades Extra Escolares - 42", LatLng(25.529929, -103.435744)),
//            Place ("Centro de Información - CI", LatLng(25.529213, -103.436087)),
//            Place ("Aulas - 5", LatLng(25.534919, -103.436025)),
//            Place ("Aulas - 6", LatLng(25.534728, -103.436014)),
//            Place ("Centro de Cómputo, Aulas - 7", LatLng(25.534675, -103.434901)),
//            Place ("Aulas - 8", LatLng(25.534465, -103.435170)),
//            Place ("Económico Administrativas, Aulas - 9", LatLng(25.534503, -103.435671))
        )
        places.forEach { place ->
            addPlaceToMap( place )
            addPlaceToAr( place, anchorNode )
        }
        //createPolylines(result.shortestPath())

    }

    //----------------------------------------------------------------------------------------------

    //Método que comienza la ruta
    private fun comenzarRuta () {
        var i = 0
        val ubicacionActualLat = curretLocation!!.latitude
        val ubicacionActualLong = curretLocation!!.longitude
        val nodoUbicacionActual= Nodo( "ubicacionActual",
                                       "ubi", LatLng( ubicacionActualLat, ubicacionActualLong ) )
        for ( nodo in nodos ) {
            i++
            Toast.makeText( this, "tam: " + nodos.size, Toast.LENGTH_SHORT )
            val distancia= nodoUbicacionActual.calcularDistancia( nodo );
            Toast.makeText( this, "vuelta:" + i + "distancia = " + distancia, Toast.LENGTH_SHORT ).show()
            if ( distancia < 150 ){
                graph.add( Edge( nodoUbicacionActual, nodo,
                                 nodoUbicacionActual.calcularDistancia( nodo ).toInt() ) )
            }
        }
        //Origen es la ubicacion actual
        val result = findShortestPath ( graph, nodoUbicacionActual ,
            origenDestino( textoSeleccionado!! ) !!
        )
//        if(!textoSeleccionado.equals("mi ubicacion")){
            //Origen diferente a la ubicacion actual
            //val result = findShortestPath(graph,nodoUbicacionActual,origenDestino(textoSeleccionado!!)!!)
        //}




        createPolylines( result.shortestPath() )


    }

    //----------------------------------------------------------------------------------------------

    //Metodo para seleccionar el nodo destino desde el orgien
    private fun origenDestino ( destino : String ) : Nodo ? {
        for ( nodo in nodos )
            if ( destino.equals( nodo.nombre ) ) {
                return nodo
            }
        return null

    }

    //----------------------------------------------------------------------------------------------

    //Crea las líneas para trazar una ruta
    private fun createPolylines ( nodos : List<Nodo> ){
        val polylineOptions = PolylineOptions()
        nodos.forEach { nodo ->
            polylineOptions.add( nodo.coords )
        }
        val polyline = map?.addPolyline( polylineOptions )
    }

    //----------------------------------------------------------------------------------------------

    //Ubica el pin en el mapa
    private fun addPlaceToMap ( place : Place ) {
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

    //----------------------------------------------------------------------------------------------

    //Ubica el pin en el AR
    private fun addPlaceToAr ( place : Place, anchorNode : AnchorNode ) {
        val placeNode = PlaceNode( this, place )
        placeNode.setParent( anchorNode )
        curretLocation?.let{
            val latLng = LatLng( it.latitude, it.longitude )
            placeNode.localPosition = place.getPositionVector( orientationAngles[ 0 ], latLng )

        }
    }

    //----------------------------------------------------------------------------------------------

    //Verifica que esten activados los permisos de ubicacion
    private fun ifLocationIsGranted ( onIsGranted : () -> Unit ) {
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

    // Funcion que maneja las animaciones para los botones
    private fun animarBoton () {
        if ( isOpen ) {
            fabPrincipal.startAnimation( girarAtras )
            fabSalir.startAnimation( animCerrar )
            fabCrearRuta.startAnimation( animCerrar )
            fabSalir.setClickable( false )
            fabCrearRuta.setClickable( false )
            isOpen = false
        } else {
            fabPrincipal.startAnimation( girarAdelante )
            fabSalir.startAnimation( animAbrir )
            fabCrearRuta.startAnimation( animAbrir )
            fabSalir.setClickable( true )
            fabCrearRuta.setClickable( true )
            isOpen = true
        }
    }

    //----------------------------------------------------------------------------------------------

    // Aqui solo se necesita llamar a las animaciones ya que este es el boton principal
    fun fabPrincipalClick ( view : View ) {
        animarBoton()
    }

    //----------------------------------------------------------------------------------------------

    // Se llama a las animaciones y aparte al método para crear la ruta, la animacion cierra
    // los botones secundarios
    fun fabCrearRuta ( view : View ) {
        animarBoton()
        comenzarRuta()
    }

    //----------------------------------------------------------------------------------------------

    // El metodo finish se usa para cerrar la app
    fun fabSalirClick ( view : View ) {
        animarBoton()
        finish()
    }

    //----------------------------------------------------------------------------------------------

}