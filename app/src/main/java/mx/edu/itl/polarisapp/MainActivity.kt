/***************************************************************************************************
                  MainActivity.kt Última modificación: 20/Noviembre/2023
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

    // Declarar los AutoCompleteTextView como propiedad de la clase
    private lateinit var autotextviewOrigen : AutoCompleteTextView
    private lateinit var autotextviewDestino : AutoCompleteTextView
    //Nodos de lugares
    val nodoEdificio19 = Nodo ( "Edificio-19", "Edificio-19", LatLng ( 25.533261,-103.435979 ) )
    val nodoLabComputo = Nodo ( "Lab. de Computo - AA","Lab. de Computo - AA", LatLng ( 25.532719,-103.435974 ) )
    val nodoEntrada2 = Nodo ( "Entrada 2","Entrada2", LatLng ( 25.533333821199893,-103.43444237040535 ) )
    val nodoComedor = Nodo ( "Comedor",  "comedor", LatLng ( 25.533219280160807,	-103.43511679921747 ) )
    val nodoBiblioteca = Nodo ( "Biblioteca", "Biblioteca", LatLng ( 25.529213,-103.436087 ) )
    val nodoGimnasio = Nodo ( "Gimnasio - 39", "Gimnasio - 39", LatLng ( 25.529929, -103.435744 ) )
    val nodoBeis = Nodo ( "Campo de béisbol", "Campo de béisbol", LatLng ( 25.53036002559007, -103.43689683545992 ) )
    val nodoAlberca = Nodo ( "Alberca", "Alberca", LatLng ( 25.530839, -103.435798 ) )
    val nodoBasket1 = Nodo ( "Cancha Basket principal", "Cancha basket 1", LatLng ( 25.531333990585452, -103.43580869675066 ) )
    val nodoCampoFutbol = Nodo ( "Campo de fútbol", "Campo de fútbol", LatLng ( 25.53160772053325, -103.43631554568248 ) )
    val nodoAtletismo = Nodo ( "Atletismo", "Atletismo", LatLng ( 25.531604578265473, -103.43621571903479 ) )
    val nodoKiosco = Nodo ( "Kiosco", "Kiosco", LatLng ( 25.531366813094554, -103.43556336348554 ) )
    val nodoAulas38 = Nodo ( "Aulas - 38", "Aulas - 38", LatLng ( 25.531574, -103.435154 ) )
    val nodoAulas37 = Nodo ( "Aulas - 37", "Aulas - 37", LatLng ( 25.531835539613358, -103.43545206846555 ) )
    val nodoRecMatsServ35 = Nodo ( "Recursos Materiales y Servicios - 35", "Recursos Materiales y Servicios - 35", LatLng ( 25.532071, -103.435046 ) )
    val nodoElecElectro36 = Nodo ( "Ingeniería Eléctrica - Electrónica - 36", "Ingeniería Eléctrica - Electrónica - 36", LatLng ( 25.532158, -103.435625 ) )
    val nodoAulas34 = Nodo ( "Aulas - 34", "Aulas - 34", LatLng ( 25.53249264394843, -103.43515166106118 ) )
    val nodoAulas33 = Nodo ( "Sala de Técnicos, Laboratorio de Físicas - 33", "Sala de Técnicos, Laboratorio de Físicas - 33", LatLng ( 25.532764923692493, -103.4351623898982 ) )
    val nodoBasket2 = Nodo ( "Cancha Basketball 2", "Cancha Basketball 2", LatLng ( 25.53230218369193, -103.4359887462145 ) )
    val nodoMantenimiento = Nodo ( "Mantenimiento - 24", "Mantenimiento - 24", LatLng ( 25.532999, -103.435695 ) )
    val nodoIdiomas = Nodo ( "Lab. de Idiomas - 29", "Lab. de Idiomas - 29", LatLng ( 25.532665331321986, -103.43569984990657 ) )
    val nodoCIM = Nodo ( "Laboratorio CIM", "Laboratorio CIM", LatLng ( 25.533211367347793, -103.43490233069835 ) )
    val nodoUsosMultiples = Nodo ( "Sala de usos Multiples", "Sala de usos Multiples", LatLng ( 25.533023112887967, -103.43492601302846 ) )
    val nodoLabIndustrial = Nodo ( "Lab. Ingeniería Industrial", "Lab. Ingeniería Industrial", LatLng ( 25.533009884185198, -103.43480196273423 ) )
    val nodoLabCompPos = Nodo ( "Lab. de Cómputo de Posgrado - 30", "Lab. de Cómputo de Posgrado - 30", LatLng ( 25.53282305695313, -103.43685159654821 ) )
    val nodoLabMaqElecIns = Nodo ( "Lab. de Máquinas Eléctricas e Instrumentación - 26", "Lab. de Máquinas Eléctricas e Instrumentación - 26", LatLng ( 25.532917, -103.436794 ) )
    val nodoAulas32 = Nodo ( "Aulas - 32", "Aulas - 32", LatLng ( 25.53264137433355, -103.43636339240476 ) )
    val nodoDivPosInv = Nodo ( "División de Estudios de Posgrado e Investigación - 31", "División de Estudios de Posgrado e Investigación - 31", LatLng ( 25.532762, -103.436446 ) )
    val nodoLabIngPot = Nodo ( "Lab. Ingeniería de Potencia - 25", "Lab. Ingeniería de Potencia - 25", LatLng ( 25.533185726319903, -103.43666866258673 ) )
    val nodoLabMecCon = Nodo ( "Lab. de Mecatrónica y Control - 27", "Lab. de Mecatrónica y Control - 27", LatLng ( 25.533083967168146, -103.43631568311318 ) )
    val nodoLabIngElect = Nodo ( "Lab. Ingeniería Electrónica - 20", "Lab. Ingeniería Electrónica - 20", LatLng ( 25.533390261959525, -103.43626719072111 ) )
    val nodoLabIngMec18 = Nodo ( "Lab. Ingeniería Mecánica 18 - A-B", "Lab. Ingeniería Mecánica 18 - A-B", LatLng ( 25.53371983967617, -103.43639137993927 ) )
    val nodoCenMejCon = Nodo ( "Sala Garcia Siler, Centro de mejora Continua - 17", "Sala Garcia Siler, Centro de mejora Continua - 17", LatLng ( 25.533774789335737, -103.43596509801925 ) )
    val nodoLabMultiMetal = Nodo ( "Lab. Multifuncional Metal - Mecánica - 21", "Lab. Multifuncional Metal - Mecánica - 21", LatLng ( 25.533561096062446, -103.43563128994904 ) )
    val nodoLabPotElectCien = Nodo ( "Lab. de Electrónica de Potencia, Lab. Electrónica Digital, Ciencias Básicas - 22", "Lab. de Electrónica de Potencia, Lab. Electrónica Digital, Ciencias Básicas - 22", LatLng ( 25.53364148547924, -103.43517681477319 ) )
    val nodoAulas15 = Nodo ( "nodoAulas15", "Aulas - 15", LatLng ( 25.53404546693482, -103.43487458314056 ) )
    val nodoLabIngElec11 = Nodo ( "Lab. Ingenieria Eléctrica - 11", "Lab. Ingenieria Eléctrica - 11", LatLng ( 25.534195809518884, -103.43631398787808 ) )
    val nodoLabQuiCual = Nodo ( "Lab. Quimica Cualitativa - 12", "Lab. Quimica Cualitativa - 12", LatLng ( 25.53425912897797, -103.43583553258219 ) )
    val nodoLabQuimCuan = Nodo ( "Lab. Química Cuantitativa - 16", "Lab. Química Cuantitativa - 16", LatLng ( 25.5340245589962, -103.43558514097732 ) )
    val nodoAulas14 = Nodo ( "Quimica - Bioquimica, Lab. Quimica Inorganica, Lab. Física - Química, Aulas - 14", "Quimica - Bioquimica, Lab. Quimica Inorganica, Lab. Física - Química, Aulas - 14", LatLng ( 25.534294227592245, -103.43485702567226 ) )
    val nodoCafeteria = Nodo ( "Cafeteria - 13", "Cafeteria - 13", LatLng ( 25.534303581597584, -103.43537614967529 ) )
    val nodoMetMecAulas10 = Nodo ( "Metal - Mecánica, Aulas - 10", "Metal - Mecánica, Aulas - 10", LatLng ( 25.534476358918322, -103.43614112927571 ) )
    val nodoEcoAdmin = Nodo ( "Económico Administrativas, Aulas - 9", "Económico Administrativas, Aulas - 9", LatLng ( 25.53453928480526, -103.43564626172316 ) )
    val nodoAulas8 = Nodo ( "Aulas - 8", "Aulas - 8", LatLng ( 25.53450661175366, -103.43523051932704 ) )
    val nodosAulas6 = Nodo ( "Aulas - 6", "Aulas - 6", LatLng ( 25.534759733692443, -103.43604541945557 ) )
    val nodoIngInd4 = Nodo ( "Ingenieria Industrial - 4", "Ingenieria Industrial - 4", LatLng ( 25.534836004501454, -103.43561161997779 ) )
    val nodoAulas7 = Nodo ( "Centro de Cómputo, Aulas - 7", "Centro de Cómputo, Aulas - 7", LatLng ( 25.534765489980863, -103.43497367958228 ) )
    val nodoAulas5 = Nodo ( "Aulas - 5", "Aulas - 5", LatLng ( 25.534951130152063, -103.43603744519685 ) )
    val nodoOfiSind = Nodo ( "Oficinas Sindicales - 3", "Oficinas Sindicales - 3", LatLng ( 25.534959764571383, -103.4352870678047 ) )
    val nodo1A = Nodo ( "Edificio Administrativo - 1-A", "Edificio Administrativo - 1-A", LatLng ( 25.535263407923832, -103.43493301088326 ) )
    val nodo1B = Nodo ( "Edificio Administrativo - 1-B", "Edificio Administrativo - 1-B", LatLng ( 25.53503171804417, -103.43492344177734 ) )
    val nodoGradas = Nodo ( "Gradas Deportivas - 40", "Gradas Deportivas - 40", LatLng ( 25.531071379189395, -103.436014455191 ) )
    val nodoEntrada1 = Nodo ( "Entrada 1", "Entrada 1", LatLng ( 25.534805062421828, -103.43446882713646 ) )
    //Nodos de conexiones
    val nodoConexion2 = Nodo ( "nodoConexion2", "2", LatLng ( 25.533042,-103.436103 ) )
    val nodoConexion1 = Nodo ( "nodoConexion1", "1", LatLng ( 25.533313082045854, -103.43614827619899 ) )
    val nodoConexion3 = Nodo ( "nodoConexion3", "3", LatLng ( 25.532858,-103.436145 ) )
    val nodoConexion4 = Nodo ( "nodoConexion4", "4", LatLng ( 25.532879,-103.435926 ) )
    val nodoConexion5 = Nodo ( "nodoConexion5", "5", LatLng ( 25.53326820923504, -103.43510085070761 ) )
    val nodoConexion6 = Nodo ( "nodoConexion6", "6", LatLng ( 25.533273147908876,-103.43533422576532 ) )
    val nodoConexion7 = Nodo ( "nodoConexion7", "7", LatLng ( 25.533063,-103.435279 ) )
    val nodoConexion8 = Nodo ( "nodoConexion8", "8", LatLng ( 25.53299176240932, -103.43522449872819 ) )
    val nodoConexion9 = Nodo ( "nodoConexion9", "9", LatLng ( 25.53301868929359,-103.43560792263749 ) )
    val nodoConexion10 = Nodo ( "nodoConexion10", "nodoConexion10", LatLng ( 25.529364, -103.436076 ) )
    val nodoConexion11 = Nodo ( "nodoConexion11", "nodoConexion11", LatLng ( 25.52970338081561, -103.43579585779854 ) )
    val nodoConexion12 = Nodo ( "nodoConexion12", "nodoConexion12", LatLng ( 25.530182, -103.435754 ) )
    val nodoConexion13 = Nodo ( "nodoConexion13", "nodoConexion13", LatLng ( 25.530356502457693, -103.43602323353907 ) )
    val nodoConexion14 = Nodo ( "nodoConexion14", "nodoConexion14", LatLng ( 25.530579, -103.435942 ) )
    val nodoConexion15 = Nodo ( "nodoConexion15", "nodoConexion15", LatLng ( 25.531048395563587, -103.43596499163036 ) )
    val nodoConexion16 = Nodo ( "nodoConexion16", "nodoConexion16", LatLng ( 25.53133643777295, -103.43597892093113 ) )
    val nodoConexion17 = Nodo ( "nodoConexion17", "nodoConexion17", LatLng ( 25.531147901134975, -103.43580132236023 ) )
    val nodoConexion18 = Nodo ( "nodoConexion18", "nodoConexion18", LatLng ( 25.531600388574354, -103.43580944778587 ) )
    val nodoConexion19 = Nodo ( "nodoConexion19", "nodoConexion19", LatLng ( 25.531594104038344, -103.43595686620745 ) )
    val nodoConexion20 = Nodo ( "nodoConexion20", "nodoConexion20", LatLng ( 25.531082, -103.435594 ) )
    val nodoConexion21 = Nodo ( "nodoConexion21", "nodoConexion21", LatLng ( 25.53161086279952, -103.43563068844261 ) )
    val nodoConexion22 = Nodo ( "nodoConexion22", "nodoConexion22", LatLng ( 25.53139112809017, -103.43539060868221 ) )
    val nodoConexion23 = Nodo ( "nodoConexion23", "nodoConexion23", LatLng ( 25.5315783816017, -103.43538285902066 ) )
    val nodoConexion24 = Nodo ( "nodoConexion24", "nodoConexion24", LatLng ( 25.531462, -103.435175 ) )
    val nodoConexion25 = Nodo ( "nodoConexion25", "nodoConexion25", LatLng ( 25.531679, -103.435580 ) )
    val nodoConexion26 = Nodo ( "nodoConexion26", "nodoConexion26", LatLng ( 25.531728943420333, -103.43545470103645 ) )
    val nodoConexion27 = Nodo ( "nodoConexion27", "nodoConexion27", LatLng ( 25.531716, -103.435202 ) )
    val nodoConexion28 = Nodo ( "nodoConexion28", "nodoConexion28", LatLng ( 25.531933, -103.435212 ) )
    val nodoConexion29 = Nodo ( "nodoConexion29", "nodoConexion29", LatLng ( 25.531914, -103.435486 ) )
    val nodoConexion30 = Nodo ( "nodoConexion30", "nodoConexion30", LatLng ( 25.531904, -103.435818 ) )
    val nodoConexion31 = Nodo ( "nodoConexion31", "nodoConexion31", LatLng ( 25.532134, -103.435226 ) )
    val nodoConexion32 = Nodo ( "nodoConexion32", "nodoConexion32", LatLng ( 25.532175, -103.435478 ) )
    val nodoConexion33 = Nodo ( "nodoConexion33", "nodoConexion33", LatLng ( 25.5324902236813, -103.435221398494 ) )
    val nodoConexion34 = Nodo ( "nodoConexion34", "nodoConexion34", LatLng ( 25.532760083169176, -103.43521737518176 ) )
    val nodoConexion35 = Nodo ( "nodoConexion35", "nodoConexion35", LatLng ( 25.532432, -103.435829 ) )
    val nodoConexion36 = Nodo ( "nodoConexion36", "nodoConexion36", LatLng ( 25.532399463628575, -103.43522005739196 ) )
    val nodoConexion37 = Nodo ( "nodoConexion37", "nodoConexion37", LatLng ( 25.532446, -103.436151 ) )
    val nodoConexion38 = Nodo ( "nodoConexion38", "nodoConexion38", LatLng ( 25.5327674048189, -103.43572603229606 ) )
    val nodoConexion39 = Nodo ( "nodoConexion39", "nodoConexion39", LatLng ( 25.532857742125074, -103.43582189041526 ) )
    val nodoConexion40 = Nodo ( "nodoConexion40", "nodoConexion40", LatLng ( 25.532981175309928, -103.43582390207227 ) )
    val nodoConexion41 = Nodo ( "nodoConexion41", "nodoConexion41", LatLng ( 25.53299327659652, -103.43561334866644 ) )
    val nodoConexion42 = Nodo ( "nodoConexion42", "nodoConexion42", LatLng ( 25.532974569454748, -103.43492749991721 ) )
    val nodoConexion43 = Nodo ( "nodoConexion43", "nodoConexion43", LatLng ( 25.532978199840773, -103.4347866839471 ) )
    val nodoConexion44 = Nodo ( "nodoConexion44", "nodoConexion44", LatLng ( 25.532516209996647, -103.43641752344223 ) )
    val nodoConexion45 = Nodo ( "nodoConexion45", "nodoConexion45", LatLng ( 25.532596, -103.436693 ) )
    val nodoConexion46 = Nodo ( "nodoConexion46", "nodoConexion46", LatLng ( 25.532856156144277, -103.43673357757521 ) )
    val nodoConexion47 = Nodo ( "nodoConexion47", "nodoConexion47", LatLng ( 25.532874616928915, -103.43680801536661 ) )
    val nodoConexion48 = Nodo ( "nodoConexion48", "nodoConexion48", LatLng ( 25.532858, -103.436564 ) )
    val nodoConexion49 = Nodo ( "nodoConexion49", "nodoConexion49", LatLng ( 25.532722633030343, -103.43622637795075 ) )
    val nodoConexion50 = Nodo ( "nodoConexion50", "nodoConexion50", LatLng ( 25.533008665340287, -103.43661678700914 ) )
    val nodoConexion51 = Nodo ( "nodoConexion51", "nodoConexion51", LatLng ( 25.533183691137694, -103.43663144749847 ) )
    val nodoConexion52 = Nodo ( "nodoConexion52", "nodoConexion52", LatLng ( 25.533190814275212, -103.43630102262387 ) )
    val nodoConexion53 = Nodo ( "nodoConexion53", "nodoConexion53", LatLng ( 25.533461493187126, -103.43658521057061 ) )
    val nodoConexion54 = Nodo ( "nodoConexion54", "nodoConexion54", LatLng ( 25.533199972594254, -103.43608788166381 ) )
    val nodoConexion55 = Nodo ( "nodoConexion55", "nodoConexion55", LatLng ( 25.533476757023102, -103.43612396901781 ) )
    val nodoConexion56 = Nodo ( "nodoConexion56", "nodoConexion56", LatLng ( 25.533469633902556, -103.43623899747246 ) )
    val nodoConexion57 = Nodo ( "nodoConexion57", "nodoConexion57", LatLng ( 25.533720857262704, -103.43612185248183 ) )
    val nodoConexion58 = Nodo ( "nodoConexion58", "nodoConexion58", LatLng ( 25.53356211365473, -103.43609704242309 ) )
    val nodoConexion59 = Nodo ( "nodoConexion59", "nodoConexion59", LatLng ( 25.53356516641839, -103.43593916023045 ) )
    val nodoConexion60 = Nodo ( "nodoConexion60", "nodoConexion60", LatLng ( 25.53373306829887, -103.43594930979997 ) )
    val nodoConexion61 = Nodo ( "nodoConexion61", "nodoConexion61", LatLng ( 25.533720857260928, -103.43578466122764 ) )
    val nodoConexion62 = Nodo ( "nodoConexion62", "nodoConexion62", LatLng ( 25.5335488850114, -103.43578578895759 ) )
    val nodoConexion63 = Nodo ( "nodoConexion63", "nodoConexion63", LatLng ( 25.533327050617398, -103.43579481079318 ) )
    val nodoConexion64 = Nodo ( "nodoConexion64", "nodoConexion64", LatLng ( 25.53331382194755, -103.43557490345167 ) )
    val nodoConexion65 = Nodo ( "nodoConexion65", "nodoConexion65", LatLng ( 25.533319927487383, -103.43532680286323 ) )
    val nodoConexion66 = Nodo ( "nodoConexion66", "nodoConexion66", LatLng ( 25.53356007847453, -103.4353358247028 ) )
    val nodoConexion67 = Nodo ( "nodoConexion67", "nodoConexion67", LatLng ( 25.533795141058373, -103.43532341965688 ) )
    val nodoConexion68 = Nodo ( "nodoConexion68", "nodoConexion68", LatLng ( 25.533794123472443, -103.43486556129822 ) )
    val nodoConexion69 = Nodo ( "nodoConexion69", "nodoConexion69", LatLng ( 25.533767666235498, -103.43457235151189 ) )
    val nodoConexion70 = Nodo ( "nodoConexion70", "nodoConexion70", LatLng ( 25.53421133298204, -103.43455994648079 ) )
    val nodoConexion71 = Nodo ( "nodoConexion71", "nodoConexion71", LatLng ( 25.53422049122314, -103.43485653945697 ) )
    val nodoConexion72 = Nodo ( "nodoConexion72", "nodoConexion72", LatLng ( 25.533519374952014, -103.4345622019362 ) )
    val nodoConexion73 = Nodo ( "nodoConexion73", "nodoConexion73", LatLng ( 25.534015957001234, -103.4353008650607 ) )
    val nodoConexion74 = Nodo ( "nodoConexion74", "nodoConexion74", LatLng ( 25.53398441189177, -103.4348824772502 ) )
    val nodoConexion75 = Nodo ( "nodoConexion75", "nodoConexion75", LatLng ( 25.53380124657617, -103.43512606691885 ) )
    val nodoConexion76 = Nodo ( "nodoConexion76", "nodoConexion76", LatLng ( 25.53406629233581, -103.43613058001442 ) )
    val nodoConexion77 = Nodo ( "nodoConexion77", "nodoConexion77", LatLng ( 25.534182857807238, -103.43613855426958 ) )
    val nodoConexion78 = Nodo ( "nodoConexion78", "nodoConexion78", LatLng ( 25.534182857807238, -103.43583553258219 ) )
    val nodoConexion79 = Nodo ( "nodoConexion79", "nodoConexion79", LatLng ( 25.534090756706224, -103.43559311523227 ) )
    val nodoConexion80 = Nodo ( "nodoConexion80", "nodoConexion80", LatLng ( 25.534218834780596, -103.43559630493422 ) )
    val nodoConexion81 = Nodo ( "nodoConexion81", "nodoConexion81", LatLng ( 25.534210041510484, -103.43550134547773 ) )
    val nodoConexion82 = Nodo ( "nodoConexion82", "nodoConexion82", LatLng ( 25.534110025490474, -103.43531155821034 ) )
    val nodoConexion83 = Nodo ( "nodoConexion83", "nodoConexion83", LatLng ( 25.53422874953373, -103.43524856159638 ) )
    val nodoConexion84 = Nodo ( "nodoConexion84", "nodoConexion84", LatLng ( 25.534295666669987, -103.43526132040427 ) )
    val nodoConexion85 = Nodo ( "nodoConexion85", "nodoConexion85", LatLng ( 25.534311007850842, -103.43550618030825 ) )
    val nodoConexion86 = Nodo ( "nodoConexion86", "nodoConexion86", LatLng ( 25.53437577712776, -103.43614234322919 ) )
    val nodoConexion87 = Nodo ( "nodoConexion87", "nodoConexion87", LatLng ( 25.534400942747816, -103.4359312343226 ) )
    val nodoConexion88 = Nodo ( "nodoConexion88", "nodoConexion88", LatLng ( 25.534423113910318, -103.43550678685753 ) )
    val nodoConexion89 = Nodo ( "nodoConexion89", "nodoConexion89", LatLng ( 25.534440055506167, -103.43540083960382 ) )
    val nodoConexion90 = Nodo ( "nodoConexion90", "nodoConexion90", LatLng ( 25.53442916448177, -103.43524527147629 ) )
    val nodoConexion91 = Nodo ( "nodoConexion91", "nodoConexion91", LatLng ( 25.53446909823637, -103.43563553288736 ) )
    val nodoConexion92 = Nodo ( "nodoConexion92", "nodoConexion92", LatLng ( 25.534689742358847, -103.43602827547458 ) )
    val nodoConexion93 = Nodo ( "nodoConexion93", "nodoConexion93", LatLng ( 25.534437381079588, -103.43497686928427 ) )
    val nodoConexion94 = Nodo ( "nodoConexion94", "nodoConexion94", LatLng ( 25.534490626883343, -103.43497048988033 ) )
    val nodoConexion95 = Nodo ( "nodoConexion95", "nodoConexion95", LatLng ( 25.534497822260416, -103.43472966738139 ) )
    val nodoConexion96 = Nodo ( "nodoConexion96", "nodoConexion96", LatLng ( 25.534697853570254, -103.43468182185178 ) )
    val nodoConexion97 = Nodo ( "nodoConexion97", "nodoConexion97", LatLng ( 25.534664754887363, -103.43497367958228 ) )
    val nodoConexion98 = Nodo ( "nodoConexion98", "nodoConexion98", LatLng ( 25.53472231781378, -103.4354010996476 ) )
    val nodoConexion99 = Nodo ( "nodoConexion99", "nodoConexion99", LatLng ( 25.534743903902005, -103.43556058474624 ) )
    val nodoConexion100 = Nodo ( "nodoConexion100", "nodoConexion100", LatLng ( 25.534748221119195, -103.43581895060603 ) )
    val nodoConexion101 = Nodo ( "nodoConexion101", "nodoConexion101", LatLng ( 25.534909397116763, -103.43581416605875 ) )
    val nodoConexion102 = Nodo ( "nodoConexion102", "nodoConexion102", LatLng ( 25.5349079580463, -103.43603186321839 ) )
    val nodoConexion103 = Nodo ( "nodoConexion103", "nodoConexion103", LatLng ( 25.534934580846695, -103.43575196687027 ) )
    val nodoConexion104 = Nodo ( "nodoConexion104", "nodoConexion104", LatLng ( 25.53491803153904, -103.43540189707876 ) )
    val nodoConexion105 = Nodo ( "nodoConexion105", "nodoConexion105", LatLng ( 25.534913714328027, -103.43529025750668 ) )
    val nodoConexion106 = Nodo ( "nodoConexion106", "nodoConexion106", LatLng ( 25.535188576458836, -103.43539073311635 ) )
    val nodoConexion107 = Nodo ( "nodoConexion107", "nodoConexion107", LatLng ( 25.535161234181125, -103.43491068296944 ) )
    val nodoConexion108 = Nodo ( "nodoConexion108", "nodoConexion108", LatLng ( 25.535149227453086, -103.43470468191262 )  )
    val nodoConexion109 = Nodo ( "nodoConexion109", "nodoConexion109", LatLng ( 25.53481836350876, -103.43469731145086 ) )

    val nodosLugares = listOf(
         nodoEdificio19, nodoLabComputo, nodoEntrada2, nodoComedor, nodoBiblioteca, nodoGimnasio,
        nodoBeis, nodoAlberca, nodoBasket1, nodoCampoFutbol, nodoAtletismo, nodoKiosco, nodoAulas38,
        nodoAulas37, nodoRecMatsServ35, nodoElecElectro36, nodoAulas34, nodoAulas33, nodoBasket2,
        nodoMantenimiento, nodoIdiomas, nodoCIM, nodoUsosMultiples, nodoLabIndustrial, nodoLabCompPos,
        nodoLabMaqElecIns, nodoAulas32, nodoDivPosInv, nodoLabIngPot, nodoLabMecCon, nodoLabIngElect,
        nodoLabIngMec18, nodoCenMejCon, nodoLabMultiMetal, nodoLabPotElectCien, nodoAulas15, nodoLabIngElec11,
        nodoLabQuiCual, nodoLabQuimCuan, nodoAulas14, nodoCafeteria, nodoMetMecAulas10, nodoEcoAdmin,
        nodoAulas8, nodosAulas6, nodoIngInd4, nodoAulas7, nodoAulas5, nodoOfiSind, nodo1A, nodo1B, nodoGradas, nodoEntrada1
    )
    val nodosConexiones = listOf(
        nodoConexion1, nodoConexion2,nodoConexion3,nodoConexion4,nodoConexion5,nodoConexion6,
        nodoConexion7,nodoConexion8,nodoConexion9,nodoConexion10, nodoConexion11, nodoConexion12,
        nodoConexion13, nodoConexion14, nodoConexion15, nodoConexion16, nodoConexion17, nodoConexion18,
        nodoConexion19, nodoConexion20, nodoConexion21, nodoConexion22, nodoConexion23, nodoConexion24,
        nodoConexion25, nodoConexion26, nodoConexion27, nodoConexion28, nodoConexion29, nodoConexion30,
        nodoConexion31, nodoConexion32, nodoConexion33, nodoConexion34, nodoConexion35, nodoConexion36,
        nodoConexion37, nodoConexion38, nodoConexion39, nodoConexion40, nodoConexion41, nodoConexion42,
        nodoConexion43, nodoConexion44, nodoConexion45, nodoConexion46, nodoConexion47, nodoConexion48,
        nodoConexion49, nodoConexion50, nodoConexion51, nodoConexion52, nodoConexion53, nodoConexion54,
        nodoConexion55, nodoConexion56, nodoConexion57, nodoConexion58, nodoConexion59, nodoConexion60,
        nodoConexion61, nodoConexion62, nodoConexion63, nodoConexion64, nodoConexion65, nodoConexion66,
        nodoConexion67, nodoConexion68, nodoConexion69, nodoConexion70, nodoConexion71, nodoConexion72,
        nodoConexion73, nodoConexion74, nodoConexion75, nodoConexion76, nodoConexion77, nodoConexion78,
        nodoConexion79, nodoConexion80, nodoConexion81, nodoConexion82, nodoConexion83, nodoConexion84,
        nodoConexion85, nodoConexion86, nodoConexion87, nodoConexion88, nodoConexion89, nodoConexion90,
        nodoConexion91, nodoConexion92, nodoConexion93, nodoConexion94, nodoConexion95, nodoConexion96,
        nodoConexion97, nodoConexion98, nodoConexion99, nodoConexion100, nodoConexion101, nodoConexion102,
        nodoConexion103, nodoConexion104, nodoConexion105, nodoConexion106, nodoConexion107, nodoConexion108, nodoConexion109
    )

    val graph = mutableListOf(
        Edge ( nodoEdificio19, nodoConexion1, nodoEdificio19.calcularDistancia ( nodoConexion1 ).toInt () ),
        Edge ( nodoConexion1, nodoEdificio19, nodoConexion1.calcularDistancia ( nodoEdificio19 ).toInt () ),
        Edge ( nodoConexion1, nodoConexion2, nodoConexion1.calcularDistancia ( nodoConexion2 ).toInt () ),
        Edge ( nodoConexion2, nodoConexion1, nodoConexion2.calcularDistancia ( nodoConexion1 ).toInt () ),
        Edge ( nodoConexion2, nodoConexion3, nodoConexion2.calcularDistancia ( nodoConexion3 ).toInt () ),
        Edge ( nodoConexion3, nodoConexion2, nodoConexion3.calcularDistancia ( nodoConexion2 ).toInt () ),
        Edge ( nodoConexion3, nodoConexion4, nodoConexion3.calcularDistancia ( nodoConexion4 ).toInt () ),
        Edge ( nodoConexion4, nodoConexion3, nodoConexion4.calcularDistancia ( nodoConexion3 ).toInt () ),
        Edge ( nodoConexion4, nodoLabComputo, nodoConexion4.calcularDistancia ( nodoLabComputo ).toInt () ),
        Edge ( nodoLabComputo, nodoConexion4, nodoLabComputo.calcularDistancia ( nodoConexion4 ).toInt () ),
        Edge ( nodoEntrada2, nodoConexion5, nodoEntrada2.calcularDistancia ( nodoConexion5 ).toInt () ),
        Edge ( nodoConexion5, nodoEntrada2, nodoConexion5.calcularDistancia ( nodoEntrada2 ).toInt () ),
        Edge ( nodoConexion40, nodoConexion4, nodoConexion40.calcularDistancia ( nodoConexion4 ).toInt () ),
        Edge ( nodoConexion4, nodoConexion40, nodoConexion4.calcularDistancia ( nodoConexion40 ).toInt () ),
        Edge ( nodoConexion40, nodoConexion41, nodoConexion40.calcularDistancia ( nodoConexion41 ).toInt () ),
        Edge ( nodoConexion41, nodoConexion40, nodoConexion41.calcularDistancia ( nodoConexion40 ).toInt () ),
        Edge ( nodoConexion5, nodoConexion6, nodoConexion5.calcularDistancia ( nodoConexion6 ).toInt () ),
        Edge ( nodoConexion6, nodoConexion7, nodoConexion6.calcularDistancia ( nodoConexion7 ).toInt () ),
        Edge ( nodoConexion7, nodoConexion8, nodoConexion7.calcularDistancia ( nodoConexion8 ).toInt () ),
        Edge ( nodoConexion8, nodoConexion9, nodoConexion8.calcularDistancia ( nodoConexion9 ).toInt () ),
        Edge ( nodoConexion9, nodoConexion41, nodoConexion9.calcularDistancia ( nodoConexion41 ).toInt () ),
        Edge ( nodoConexion41, nodoConexion9, nodoConexion41.calcularDistancia ( nodoConexion9 ).toInt () ),
        Edge ( nodoConexion6, nodoConexion5, nodoConexion6.calcularDistancia ( nodoConexion5 ).toInt () ),
        Edge ( nodoConexion7, nodoConexion6, nodoConexion7.calcularDistancia ( nodoConexion6 ).toInt () ),
        Edge ( nodoConexion8, nodoConexion7, nodoConexion8.calcularDistancia ( nodoConexion7 ).toInt () ),
        Edge ( nodoConexion9, nodoConexion8, nodoConexion9.calcularDistancia ( nodoConexion8 ).toInt () ),
        Edge ( nodoMantenimiento, nodoConexion9, nodoMantenimiento.calcularDistancia ( nodoConexion9 ).toInt () ),
        Edge ( nodoConexion9, nodoMantenimiento, nodoConexion9.calcularDistancia ( nodoMantenimiento ).toInt ()  ) ,

        Edge ( nodoBiblioteca, nodoConexion10, nodoBiblioteca.calcularDistancia ( nodoConexion10 ).toInt () ),
        Edge ( nodoConexion10, nodoBiblioteca, nodoConexion10.calcularDistancia ( nodoBiblioteca ).toInt () ),
        Edge ( nodoConexion10, nodoConexion11, nodoConexion10.calcularDistancia ( nodoConexion11 ).toInt () ),
        Edge ( nodoConexion11, nodoConexion10, nodoConexion11.calcularDistancia ( nodoConexion10 ).toInt () ),
        Edge ( nodoConexion11, nodoGimnasio, nodoConexion11.calcularDistancia ( nodoGimnasio ).toInt () ),
        Edge ( nodoGimnasio, nodoConexion11, nodoGimnasio.calcularDistancia ( nodoConexion11 ).toInt () ),
        Edge ( nodoGimnasio, nodoConexion12, nodoGimnasio.calcularDistancia ( nodoConexion12 ).toInt () ),
        Edge ( nodoConexion12, nodoGimnasio, nodoConexion12.calcularDistancia ( nodoGimnasio ).toInt () ),
        Edge ( nodoConexion12, nodoConexion13, nodoConexion12.calcularDistancia ( nodoConexion13 ).toInt () ),
        Edge ( nodoConexion13, nodoConexion12, nodoConexion13.calcularDistancia ( nodoConexion12 ).toInt () ),
        Edge ( nodoConexion13, nodoConexion10, nodoConexion13.calcularDistancia ( nodoConexion10 ).toInt () ),
        Edge ( nodoConexion10, nodoConexion13, nodoConexion10.calcularDistancia ( nodoConexion13 ).toInt () ),
        Edge ( nodoBeis, nodoConexion13, nodoBeis.calcularDistancia ( nodoConexion13 ).toInt () ),
        Edge ( nodoConexion13, nodoBeis, nodoConexion13.calcularDistancia ( nodoBeis ).toInt () ),
        Edge ( nodoConexion14, nodoConexion13, nodoConexion14.calcularDistancia ( nodoConexion13 ).toInt () ),
        Edge ( nodoConexion13, nodoConexion14, nodoConexion13.calcularDistancia ( nodoConexion14 ).toInt () ),
        Edge ( nodoConexion14, nodoConexion12, nodoConexion14.calcularDistancia ( nodoConexion12 ).toInt () ),
        Edge ( nodoConexion12, nodoConexion14, nodoConexion12.calcularDistancia ( nodoConexion14 ).toInt () ),
        Edge ( nodoConexion15, nodoConexion14, nodoConexion15.calcularDistancia ( nodoConexion14 ).toInt () ),
        Edge ( nodoConexion14, nodoConexion15, nodoConexion14.calcularDistancia ( nodoConexion15 ).toInt () ),
        Edge ( nodoConexion15, nodoAlberca, nodoConexion15.calcularDistancia ( nodoAlberca ).toInt () ),
        Edge ( nodoAlberca, nodoConexion15, nodoAlberca.calcularDistancia ( nodoConexion15 ).toInt () ),
        Edge ( nodoConexion16, nodoConexion15, nodoConexion16.calcularDistancia ( nodoConexion15 ).toInt () ),
        Edge ( nodoConexion15, nodoConexion16, nodoConexion15.calcularDistancia ( nodoConexion16 ).toInt () ),
        Edge ( nodoConexion16, nodoConexion19, nodoConexion16.calcularDistancia ( nodoConexion19 ).toInt () ),
        Edge ( nodoConexion19, nodoConexion16, nodoConexion19.calcularDistancia ( nodoConexion16 ).toInt () ),
        Edge ( nodoConexion16, nodoBasket1, nodoConexion16.calcularDistancia ( nodoBasket1 ).toInt () ),
        Edge ( nodoBasket1, nodoConexion16, nodoBasket1.calcularDistancia ( nodoConexion16 ).toInt () ),
        Edge ( nodoConexion17, nodoConexion15, nodoConexion17.calcularDistancia ( nodoConexion15 ).toInt () ),
        Edge ( nodoConexion15, nodoConexion17, nodoConexion15.calcularDistancia ( nodoConexion17 ).toInt () ),
        Edge ( nodoConexion17, nodoBasket1, nodoConexion17.calcularDistancia ( nodoBasket1 ).toInt () ),
        Edge ( nodoBasket1, nodoConexion17, nodoBasket1.calcularDistancia ( nodoConexion17 ).toInt () ),
        Edge ( nodoConexion17, nodoConexion20, nodoConexion17.calcularDistancia ( nodoConexion20 ).toInt () ),
        Edge ( nodoConexion20, nodoConexion17, nodoConexion20.calcularDistancia ( nodoConexion17 ).toInt () ),
        Edge ( nodoBasket1, nodoConexion18, nodoBasket1.calcularDistancia ( nodoConexion18 ).toInt () ),
        Edge ( nodoConexion18, nodoBasket1, nodoConexion18.calcularDistancia ( nodoBasket1 ).toInt () ),
        Edge ( nodoBasket1, nodoKiosco, nodoBasket1.calcularDistancia( nodoKiosco ).toInt () ),
        Edge ( nodoKiosco, nodoBasket1, nodoKiosco.calcularDistancia ( nodoBasket1 ).toInt () ),
        Edge ( nodoConexion18, nodoConexion19, nodoConexion18.calcularDistancia ( nodoConexion19 ).toInt () ),
        Edge ( nodoConexion19, nodoConexion18, nodoConexion19.calcularDistancia ( nodoConexion18 ).toInt () ),
        Edge ( nodoConexion19, nodoAtletismo, nodoConexion19.calcularDistancia ( nodoAtletismo ).toInt () ),
        Edge ( nodoAtletismo, nodoConexion19, nodoAtletismo.calcularDistancia ( nodoConexion19 ).toInt () ),
        Edge ( nodoConexion19, nodoCampoFutbol, nodoConexion19.calcularDistancia ( nodoCampoFutbol ).toInt () ),
        Edge ( nodoCampoFutbol, nodoConexion19, nodoCampoFutbol.calcularDistancia ( nodoConexion19 ).toInt () ),
        Edge ( nodoConexion20, nodoKiosco, nodoConexion20.calcularDistancia ( nodoKiosco) .toInt () ),
        Edge ( nodoKiosco, nodoConexion20, nodoKiosco.calcularDistancia ( nodoConexion20 ).toInt () ),
        Edge ( nodoKiosco, nodoConexion21, nodoKiosco.calcularDistancia ( nodoConexion21 ).toInt () ),
        Edge ( nodoConexion21, nodoKiosco, nodoConexion21.calcularDistancia ( nodoKiosco ).toInt () ),
        Edge ( nodoKiosco, nodoConexion22, nodoKiosco.calcularDistancia ( nodoConexion22 ).toInt () ),
        Edge ( nodoConexion22, nodoKiosco, nodoConexion22.calcularDistancia( nodoKiosco ).toInt () ),
        Edge ( nodoConexion21, nodoConexion18, nodoConexion21.calcularDistancia ( nodoConexion18 ).toInt () ),
        Edge ( nodoConexion18, nodoConexion21, nodoConexion18.calcularDistancia ( nodoConexion21 ).toInt () ),
        Edge ( nodoConexion21, nodoConexion23, nodoConexion21.calcularDistancia ( nodoConexion23 ).toInt () ),
        Edge ( nodoConexion23, nodoConexion21, nodoConexion23.calcularDistancia ( nodoConexion21 ).toInt () ),
        Edge ( nodoConexion22, nodoConexion24, nodoConexion22.calcularDistancia ( nodoConexion24 ).toInt () ),
        Edge ( nodoConexion24, nodoConexion22, nodoConexion24.calcularDistancia ( nodoConexion22 ).toInt () ),
        Edge ( nodoConexion22, nodoConexion23, nodoConexion22.calcularDistancia ( nodoConexion23 ).toInt () ),
        Edge ( nodoConexion23, nodoConexion22, nodoConexion23.calcularDistancia ( nodoConexion22 ).toInt () ),
        Edge ( nodoConexion23, nodoConexion26, nodoConexion23.calcularDistancia ( nodoConexion26 ).toInt () ),
        Edge ( nodoConexion26, nodoConexion23, nodoConexion26.calcularDistancia ( nodoConexion23 ).toInt () ),
        Edge ( nodoConexion23, nodoConexion24, nodoConexion23.calcularDistancia ( nodoConexion24 ).toInt () ),
        Edge ( nodoConexion24, nodoConexion23, nodoConexion24.calcularDistancia ( nodoConexion23 ).toInt () ),
        Edge ( nodoConexion24, nodoAulas38, nodoConexion24.calcularDistancia ( nodoAulas38 ).toInt () ),
        Edge ( nodoAulas38, nodoConexion24, nodoAulas38.calcularDistancia ( nodoConexion24 ).toInt () ),
        Edge ( nodoAulas38, nodoConexion24, nodoAulas38.calcularDistancia ( nodoConexion24 ).toInt () ),
        Edge ( nodoConexion24, nodoAulas38, nodoConexion24.calcularDistancia ( nodoAulas38 ).toInt () ),
        Edge ( nodoConexion25, nodoConexion21, nodoConexion25.calcularDistancia ( nodoConexion21 ).toInt () ),
        Edge ( nodoConexion21, nodoConexion25, nodoConexion21.calcularDistancia ( nodoConexion25 ).toInt () ),
        Edge ( nodoConexion25, nodoConexion26, nodoConexion25.calcularDistancia ( nodoConexion26 ).toInt () ),
        Edge ( nodoConexion26, nodoConexion25, nodoConexion26.calcularDistancia ( nodoConexion25 ).toInt () ),
        Edge ( nodoConexion26, nodoAulas37, nodoConexion26.calcularDistancia ( nodoAulas37 ).toInt () ),
        Edge ( nodoAulas37, nodoConexion26, nodoAulas37.calcularDistancia ( nodoConexion26 ).toInt () ),
        Edge ( nodoConexion26, nodoConexion27, nodoConexion26.calcularDistancia ( nodoConexion27 ).toInt () ),
        Edge ( nodoConexion27, nodoConexion26, nodoConexion27.calcularDistancia ( nodoConexion26 ).toInt () ),
        Edge ( nodoConexion27, nodoConexion28, nodoConexion27.calcularDistancia ( nodoConexion28 ).toInt () ),
        Edge ( nodoConexion28, nodoConexion27, nodoConexion28.calcularDistancia ( nodoConexion27 ).toInt () ),
        Edge ( nodoConexion28, nodoConexion29, nodoConexion28.calcularDistancia ( nodoConexion29 ).toInt () ),
        Edge ( nodoConexion29, nodoConexion28, nodoConexion29.calcularDistancia ( nodoConexion28 ).toInt () ),
        Edge ( nodoConexion28, nodoConexion31, nodoConexion28.calcularDistancia ( nodoConexion31 ).toInt () ),
        Edge ( nodoConexion31, nodoConexion28, nodoConexion31.calcularDistancia ( nodoConexion28 ).toInt () ),
        Edge ( nodoConexion29, nodoConexion30, nodoConexion29.calcularDistancia ( nodoConexion30 ).toInt () ),
        Edge ( nodoConexion30, nodoConexion29, nodoConexion30.calcularDistancia ( nodoConexion29 ).toInt () ),
        Edge ( nodoConexion30, nodoConexion18, nodoConexion30.calcularDistancia ( nodoConexion18 ).toInt () ),
        Edge ( nodoConexion18, nodoConexion30, nodoConexion18.calcularDistancia ( nodoConexion30 ).toInt () ),
        Edge ( nodoConexion31, nodoRecMatsServ35, nodoConexion31.calcularDistancia ( nodoRecMatsServ35 ).toInt () ),
        Edge ( nodoRecMatsServ35, nodoConexion31, nodoRecMatsServ35.calcularDistancia ( nodoConexion31 ).toInt () ),
        Edge ( nodoConexion32, nodoConexion29, nodoConexion32.calcularDistancia ( nodoConexion29 ).toInt () ),
        Edge ( nodoConexion29, nodoConexion32, nodoConexion29.calcularDistancia ( nodoConexion32 ).toInt () ),
        Edge ( nodoElecElectro36, nodoConexion32, nodoElecElectro36.calcularDistancia ( nodoConexion32 ).toInt () ),
        Edge ( nodoConexion32, nodoElecElectro36, nodoConexion32.calcularDistancia ( nodoElecElectro36 ).toInt () ),
        Edge ( nodoConexion36, nodoConexion31, nodoConexion36.calcularDistancia ( nodoConexion31 ).toInt () ),
        Edge ( nodoConexion31, nodoConexion36, nodoConexion31.calcularDistancia ( nodoConexion36 ).toInt () ),
        Edge ( nodoConexion36, nodoConexion32, nodoConexion36.calcularDistancia ( nodoConexion32 ).toInt () ),
        Edge ( nodoConexion32, nodoConexion36, nodoConexion32.calcularDistancia ( nodoConexion36 ).toInt () ),
        Edge ( nodoConexion33, nodoConexion36, nodoConexion33.calcularDistancia ( nodoConexion36 ).toInt () ),
        Edge ( nodoConexion36, nodoConexion33, nodoConexion36.calcularDistancia ( nodoConexion33 ).toInt () ),
        Edge ( nodoAulas34, nodoConexion33, nodoAulas34.calcularDistancia ( nodoConexion33 ).toInt () ),
        Edge ( nodoConexion33, nodoAulas34, nodoConexion33.calcularDistancia ( nodoAulas34 ).toInt () ),
        Edge ( nodoConexion34, nodoConexion33, nodoConexion34.calcularDistancia ( nodoConexion33 ).toInt () ),
        Edge ( nodoConexion33, nodoConexion34, nodoConexion33.calcularDistancia ( nodoConexion34 ).toInt () ),
        Edge ( nodoConexion35, nodoConexion33, nodoConexion35.calcularDistancia ( nodoConexion33 ).toInt () ),
        Edge ( nodoConexion33, nodoConexion35, nodoConexion33.calcularDistancia ( nodoConexion35 ).toInt () ),
        Edge ( nodoAulas33, nodoConexion34, nodoAulas33.calcularDistancia ( nodoConexion34 ).toInt () ),
        Edge ( nodoConexion34, nodoAulas33, nodoConexion34.calcularDistancia ( nodoAulas33 ).toInt () ),
        Edge ( nodoConexion35, nodoConexion30, nodoConexion35.calcularDistancia ( nodoConexion30 ).toInt () ),
        Edge ( nodoConexion30, nodoConexion35, nodoConexion30.calcularDistancia ( nodoConexion35 ).toInt () ),
        Edge ( nodoBasket2, nodoConexion30, nodoBasket2.calcularDistancia ( nodoConexion30 ).toInt () ),
        Edge ( nodoConexion30, nodoBasket2, nodoConexion30.calcularDistancia ( nodoBasket2 ).toInt () ),
        Edge ( nodoBasket2, nodoConexion35, nodoBasket2.calcularDistancia ( nodoConexion35 ).toInt () ),
        Edge ( nodoConexion35, nodoBasket2, nodoConexion35.calcularDistancia ( nodoBasket2 ).toInt () ),
        Edge ( nodoBasket2, nodoConexion37, nodoBasket2.calcularDistancia ( nodoConexion37 ).toInt () ),
        Edge ( nodoConexion37, nodoBasket2, nodoConexion37.calcularDistancia ( nodoBasket2 ).toInt () ),
        Edge ( nodoConexion38, nodoConexion34, nodoConexion38.calcularDistancia ( nodoConexion34 ).toInt () ),
        Edge ( nodoConexion34, nodoConexion38, nodoConexion34.calcularDistancia ( nodoConexion38 ).toInt () ),
        Edge ( nodoConexion34, nodoConexion8, nodoConexion34.calcularDistancia ( nodoConexion8 ).toInt () ),
        Edge ( nodoConexion8, nodoConexion34, nodoConexion8.calcularDistancia ( nodoConexion34).toInt () ),
        Edge ( nodoConexion38, nodoConexion39, nodoConexion38.calcularDistancia ( nodoConexion39 ).toInt () ),
        Edge ( nodoConexion39, nodoConexion38, nodoConexion39.calcularDistancia ( nodoConexion38 ).toInt () ),
        Edge ( nodoConexion39, nodoConexion4, nodoConexion39.calcularDistancia ( nodoConexion4 ).toInt () ),
        Edge ( nodoConexion4, nodoConexion39, nodoConexion4.calcularDistancia ( nodoConexion39 ).toInt () ),
        Edge ( nodoIdiomas, nodoConexion38, nodoIdiomas.calcularDistancia ( nodoConexion38 ).toInt () ),
        Edge ( nodoConexion38, nodoIdiomas, nodoConexion38.calcularDistancia ( nodoIdiomas ).toInt () ),
        Edge ( nodoConexion37, nodoConexion35, nodoConexion37.calcularDistancia ( nodoConexion35 ).toInt () ),
        Edge ( nodoConexion35, nodoConexion37, nodoConexion35.calcularDistancia ( nodoConexion37 ).toInt () ),
        Edge ( nodoConexion39, nodoConexion35, nodoConexion39.calcularDistancia ( nodoConexion35 ).toInt () ),
        Edge ( nodoConexion35, nodoConexion39, nodoConexion35.calcularDistancia ( nodoConexion39 ).toInt () ),
        Edge ( nodoCIM, nodoEntrada2, nodoCIM.calcularDistancia ( nodoEntrada2 ).toInt () ),
        Edge ( nodoEntrada2, nodoCIM, nodoEntrada2.calcularDistancia ( nodoCIM ).toInt () ),
        Edge ( nodoCIM, nodoConexion5, nodoCIM.calcularDistancia (nodoConexion5 ).toInt () ),
        Edge ( nodoConexion5, nodoCIM, nodoConexion5.calcularDistancia ( nodoCIM ).toInt () ),
        Edge ( nodoConexion42, nodoConexion8, nodoConexion42.calcularDistancia ( nodoConexion8 ).toInt () ),
        Edge ( nodoConexion8, nodoConexion42, nodoConexion8.calcularDistancia ( nodoConexion42 ).toInt () ),
        Edge ( nodoUsosMultiples, nodoConexion42, nodoUsosMultiples.calcularDistancia ( nodoConexion42 ).toInt () ),
        Edge ( nodoConexion42, nodoUsosMultiples, nodoConexion42.calcularDistancia ( nodoUsosMultiples ).toInt () ),
        Edge ( nodoConexion43, nodoConexion42, nodoConexion43.calcularDistancia ( nodoConexion42 ).toInt () ),
        Edge ( nodoConexion42, nodoConexion43, nodoConexion42.calcularDistancia ( nodoConexion43 ).toInt () ),
        Edge ( nodoLabIndustrial, nodoConexion43, nodoLabIndustrial.calcularDistancia ( nodoConexion43 ).toInt () ),
        Edge ( nodoConexion43, nodoLabIndustrial, nodoConexion43.calcularDistancia ( nodoLabIndustrial ).toInt () ),
        Edge ( nodoConexion37, nodoConexion3, nodoConexion37.calcularDistancia ( nodoConexion3 ).toInt () ),
        Edge ( nodoConexion3, nodoConexion37, nodoConexion3.calcularDistancia ( nodoConexion37 ).toInt () ),
        Edge ( nodoConexion44, nodoConexion37, nodoConexion44.calcularDistancia ( nodoConexion37 ).toInt () ),
        Edge ( nodoConexion37, nodoConexion44, nodoConexion37.calcularDistancia ( nodoConexion44 ).toInt () ),
        Edge ( nodoConexion45, nodoConexion44, nodoConexion45.calcularDistancia ( nodoConexion44 ).toInt () ),
        Edge ( nodoConexion44, nodoConexion45, nodoConexion44.calcularDistancia ( nodoConexion45 ).toInt () ),
        Edge ( nodoConexion46, nodoConexion45, nodoConexion46.calcularDistancia ( nodoConexion45 ).toInt () ),
        Edge ( nodoConexion45, nodoConexion46, nodoConexion45.calcularDistancia ( nodoConexion46 ).toInt () ),
        Edge ( nodoConexion47, nodoConexion46, nodoConexion47.calcularDistancia ( nodoConexion46 ).toInt () ),
        Edge ( nodoConexion46, nodoConexion47, nodoConexion46.calcularDistancia ( nodoConexion47 ).toInt () ),
        Edge ( nodoConexion48, nodoConexion46, nodoConexion48.calcularDistancia ( nodoConexion46 ).toInt () ),
        Edge ( nodoConexion46, nodoConexion48, nodoConexion46.calcularDistancia ( nodoConexion48 ).toInt () ),
        Edge ( nodoLabCompPos, nodoConexion47, nodoLabCompPos.calcularDistancia ( nodoConexion47 ).toInt () ),
        Edge ( nodoConexion47, nodoLabCompPos, nodoConexion47.calcularDistancia ( nodoLabCompPos ).toInt () ),
        Edge ( nodoLabMaqElecIns, nodoConexion47, nodoLabMaqElecIns.calcularDistancia ( nodoConexion47 ).toInt () ),
        Edge ( nodoConexion47, nodoLabMaqElecIns, nodoConexion47.calcularDistancia ( nodoLabMaqElecIns ).toInt () ),
        Edge ( nodoConexion49, nodoConexion37, nodoConexion49.calcularDistancia ( nodoConexion37 ).toInt () ),
        Edge ( nodoConexion37, nodoConexion49, nodoConexion37.calcularDistancia ( nodoConexion49 ).toInt () ),
        Edge ( nodoConexion49, nodoConexion3, nodoConexion49.calcularDistancia ( nodoConexion3 ).toInt () ),
        Edge ( nodoConexion3, nodoConexion49, nodoConexion3.calcularDistancia ( nodoConexion49 ).toInt () ),
        Edge ( nodoAulas32, nodoConexion49, nodoAulas32.calcularDistancia ( nodoConexion49 ).toInt () ),
        Edge ( nodoConexion49, nodoAulas32, nodoConexion49.calcularDistancia ( nodoAulas32 ).toInt () ),
        Edge ( nodoDivPosInv, nodoConexion48, nodoDivPosInv.calcularDistancia ( nodoConexion48 ).toInt () ),
        Edge ( nodoConexion48, nodoDivPosInv, nodoConexion48.calcularDistancia ( nodoDivPosInv ).toInt () ),
        Edge ( nodoConexion48, nodoConexion3, nodoConexion48.calcularDistancia ( nodoConexion3 ).toInt () ),
        Edge ( nodoConexion3, nodoConexion48, nodoConexion3.calcularDistancia ( nodoConexion48 ).toInt () ),
        Edge ( nodoConexion50, nodoConexion48, nodoConexion50.calcularDistancia ( nodoConexion48 ).toInt () ),
        Edge ( nodoConexion48, nodoConexion50, nodoConexion48.calcularDistancia ( nodoConexion50 ).toInt () ),
        Edge ( nodoConexion51, nodoConexion50, nodoConexion51.calcularDistancia ( nodoConexion50 ).toInt () ),
        Edge ( nodoConexion50, nodoConexion51, nodoConexion50.calcularDistancia ( nodoConexion51 ).toInt () ),
        Edge ( nodoConexion51, nodoConexion52, nodoConexion51.calcularDistancia ( nodoConexion52 ).toInt () ),
        Edge ( nodoConexion52, nodoConexion51, nodoConexion52.calcularDistancia ( nodoConexion51 ).toInt () ),
        Edge ( nodoConexion51, nodoConexion53, nodoConexion51.calcularDistancia ( nodoConexion53 ).toInt () ),
        Edge ( nodoConexion53, nodoConexion51, nodoConexion53.calcularDistancia ( nodoConexion51 ).toInt () ),
        Edge ( nodoLabIngPot, nodoConexion51, nodoLabIngPot.calcularDistancia ( nodoConexion51 ).toInt () ),
        Edge ( nodoConexion51, nodoLabIngPot, nodoConexion51.calcularDistancia ( nodoLabIngPot ).toInt () ),
        Edge ( nodoConexion52, nodoConexion54, nodoConexion52.calcularDistancia ( nodoConexion54 ).toInt () ),
        Edge ( nodoConexion54, nodoConexion52, nodoConexion54.calcularDistancia ( nodoConexion52 ).toInt () ),
        Edge ( nodoLabMecCon, nodoConexion52, nodoLabMecCon.calcularDistancia ( nodoConexion52 ).toInt () ),
        Edge ( nodoConexion52, nodoLabMecCon, nodoConexion52.calcularDistancia ( nodoLabMecCon ).toInt () ),
        Edge ( nodoConexion54, nodoConexion1, nodoConexion54.calcularDistancia ( nodoConexion1 ).toInt () ),
        Edge ( nodoConexion1, nodoConexion54, nodoConexion1.calcularDistancia ( nodoConexion54 ).toInt () ),
        Edge ( nodoConexion54, nodoConexion2, nodoConexion54.calcularDistancia ( nodoConexion2 ).toInt () ),
        Edge ( nodoConexion2, nodoConexion54, nodoConexion2.calcularDistancia ( nodoConexion54 ).toInt () ),
        Edge ( nodoConexion53, nodoConexion56, nodoConexion53.calcularDistancia ( nodoConexion56 ).toInt () ),
        Edge ( nodoConexion56, nodoConexion53, nodoConexion56.calcularDistancia ( nodoConexion53 ).toInt () ),
        Edge ( nodoConexion56, nodoConexion55, nodoConexion56.calcularDistancia ( nodoConexion55 ).toInt () ),
        Edge ( nodoConexion55, nodoConexion56, nodoConexion55.calcularDistancia ( nodoConexion56 ).toInt () ),
        Edge ( nodoConexion55, nodoConexion1, nodoConexion55.calcularDistancia ( nodoConexion1 ).toInt () ),
        Edge ( nodoConexion1, nodoConexion55, nodoConexion1.calcularDistancia ( nodoConexion55 ).toInt () ),
        Edge ( nodoLabIngElect, nodoConexion56, nodoLabIngElect.calcularDistancia ( nodoConexion56 ).toInt () ),
        Edge ( nodoConexion56, nodoLabIngElect, nodoConexion56.calcularDistancia ( nodoLabIngElect ).toInt () ),
        Edge ( nodoConexion57, nodoConexion55, nodoConexion57.calcularDistancia ( nodoConexion55 ).toInt () ),
        Edge ( nodoConexion55, nodoConexion57, nodoConexion55.calcularDistancia ( nodoConexion57 ).toInt () ),
        Edge ( nodoConexion58, nodoConexion55, nodoConexion58.calcularDistancia ( nodoConexion55 ).toInt () ),
        Edge ( nodoConexion55, nodoConexion58, nodoConexion55.calcularDistancia ( nodoConexion58 ).toInt () ),
        Edge ( nodoLabIngMec18, nodoConexion57, nodoLabIngMec18.calcularDistancia ( nodoConexion57 ).toInt () ),
        Edge ( nodoConexion57, nodoLabIngMec18, nodoConexion57.calcularDistancia ( nodoLabIngMec18 ).toInt () ),
        Edge ( nodoConexion58, nodoConexion57, nodoConexion58.calcularDistancia ( nodoConexion57 ).toInt () ),
        Edge ( nodoConexion57, nodoConexion58, nodoConexion57.calcularDistancia ( nodoConexion58 ).toInt () ),
        Edge ( nodoConexion59, nodoConexion58, nodoConexion59.calcularDistancia ( nodoConexion58 ).toInt () ),
        Edge ( nodoConexion58, nodoConexion59, nodoConexion58.calcularDistancia ( nodoConexion59 ).toInt () ),
        Edge ( nodoConexion59, nodoConexion60, nodoConexion59.calcularDistancia ( nodoConexion60 ).toInt () ),
        Edge ( nodoConexion60, nodoConexion59, nodoConexion60.calcularDistancia ( nodoConexion59 ).toInt () ),
        Edge ( nodoConexion59, nodoConexion62, nodoConexion59.calcularDistancia ( nodoConexion62 ).toInt () ),
        Edge ( nodoConexion62, nodoConexion59, nodoConexion62.calcularDistancia ( nodoConexion59 ).toInt () ),
        Edge ( nodoConexion60, nodoConexion57, nodoConexion60.calcularDistancia ( nodoConexion57 ).toInt () ),
        Edge ( nodoConexion57, nodoConexion60, nodoConexion57.calcularDistancia ( nodoConexion60 ).toInt () ),
        Edge ( nodoConexion61, nodoConexion60, nodoConexion61.calcularDistancia ( nodoConexion60 ).toInt () ),
        Edge ( nodoConexion60, nodoConexion61, nodoConexion60.calcularDistancia ( nodoConexion61 ).toInt () ),
        Edge ( nodoCenMejCon, nodoConexion60, nodoCenMejCon.calcularDistancia ( nodoConexion60 ).toInt () ),
        Edge ( nodoConexion60, nodoCenMejCon, nodoConexion60.calcularDistancia ( nodoCenMejCon ).toInt () ),
        Edge ( nodoConexion62, nodoConexion61, nodoConexion62.calcularDistancia ( nodoConexion61 ).toInt () ),
        Edge ( nodoConexion61, nodoConexion62, nodoConexion61.calcularDistancia ( nodoConexion62 ).toInt () ),
        Edge ( nodoConexion63, nodoConexion62, nodoConexion63.calcularDistancia ( nodoConexion62 ).toInt () ),
        Edge ( nodoConexion62, nodoConexion63, nodoConexion62.calcularDistancia ( nodoConexion63 ).toInt () ),
        Edge ( nodoConexion64, nodoConexion63, nodoConexion64.calcularDistancia ( nodoConexion63 ).toInt () ),
        Edge ( nodoConexion63, nodoConexion64, nodoConexion63.calcularDistancia ( nodoConexion64 ).toInt () ),
        Edge ( nodoConexion65, nodoConexion64, nodoConexion65.calcularDistancia ( nodoConexion64 ).toInt () ),
        Edge ( nodoConexion64, nodoConexion65, nodoConexion64.calcularDistancia ( nodoConexion65 ).toInt () ),
        Edge ( nodoLabMultiMetal, nodoConexion64, nodoLabMultiMetal.calcularDistancia ( nodoConexion64 ).toInt () ),
        Edge ( nodoConexion64, nodoLabMultiMetal, nodoConexion64.calcularDistancia ( nodoLabMultiMetal ).toInt () ),
        Edge ( nodoConexion65, nodoConexion66, nodoConexion65.calcularDistancia ( nodoConexion66 ).toInt () ),
        Edge ( nodoConexion66, nodoConexion65, nodoConexion66.calcularDistancia ( nodoConexion65 ).toInt () ),
        Edge ( nodoConexion65, nodoConexion6, nodoConexion65.calcularDistancia ( nodoConexion6 ).toInt () ),
        Edge ( nodoConexion6, nodoConexion65, nodoConexion6.calcularDistancia ( nodoConexion65 ).toInt () ),
        Edge ( nodoLabMultiMetal, nodoConexion66, nodoLabMultiMetal.calcularDistancia ( nodoConexion66 ).toInt () ),
        Edge ( nodoConexion66, nodoLabMultiMetal, nodoConexion66.calcularDistancia ( nodoLabMultiMetal ).toInt () ),
        Edge ( nodoLabPotElectCien, nodoConexion5, nodoLabPotElectCien.calcularDistancia ( nodoConexion5 ).toInt () ),
        Edge ( nodoConexion5, nodoLabPotElectCien, nodoConexion5.calcularDistancia ( nodoLabPotElectCien ).toInt () ),
        Edge ( nodoConexion67, nodoConexion66, nodoConexion67.calcularDistancia ( nodoConexion66 ).toInt () ),
        Edge ( nodoConexion66, nodoConexion67, nodoConexion66.calcularDistancia ( nodoConexion67 ).toInt () ),
        Edge ( nodoConexion72, nodoEntrada2, nodoConexion72.calcularDistancia ( nodoEntrada2 ).toInt () ),
        Edge ( nodoEntrada2, nodoConexion72, nodoEntrada2.calcularDistancia ( nodoConexion72 ).toInt () ),
        Edge ( nodoConexion69, nodoConexion72, nodoConexion69.calcularDistancia ( nodoConexion72 ).toInt () ),
        Edge ( nodoConexion72, nodoConexion69, nodoConexion72.calcularDistancia ( nodoConexion69 ).toInt () ),
        Edge ( nodoConexion68, nodoConexion69, nodoConexion68.calcularDistancia ( nodoConexion69 ).toInt () ),
        Edge ( nodoConexion69, nodoConexion68, nodoConexion69.calcularDistancia ( nodoConexion68 ).toInt () ),
        Edge ( nodoConexion70, nodoConexion69, nodoConexion70.calcularDistancia ( nodoConexion69 ).toInt () ),
        Edge ( nodoConexion69, nodoConexion70, nodoConexion69.calcularDistancia ( nodoConexion70 ).toInt () ),
        Edge ( nodoConexion74, nodoConexion68, nodoConexion74.calcularDistancia ( nodoConexion68 ).toInt () ),
        Edge ( nodoConexion68, nodoConexion74, nodoConexion68.calcularDistancia ( nodoConexion74 ).toInt () ),
        Edge ( nodoConexion75, nodoConexion68, nodoConexion75.calcularDistancia ( nodoConexion68 ).toInt () ),
        Edge ( nodoConexion68, nodoConexion75, nodoConexion68.calcularDistancia ( nodoConexion75 ).toInt () ),
        Edge ( nodoConexion75, nodoConexion67, nodoConexion75.calcularDistancia ( nodoConexion67 ).toInt () ),
        Edge ( nodoConexion67, nodoConexion75, nodoConexion67.calcularDistancia ( nodoConexion75 ).toInt () ),
        Edge ( nodoConexion75, nodoLabPotElectCien, nodoConexion75.calcularDistancia ( nodoLabPotElectCien ).toInt () ),
        Edge ( nodoLabPotElectCien, nodoConexion75, nodoLabPotElectCien.calcularDistancia ( nodoConexion75 ).toInt () ),
        Edge ( nodoConexion73, nodoConexion67, nodoConexion73.calcularDistancia ( nodoConexion67 ).toInt () ),
        Edge ( nodoConexion67, nodoConexion73, nodoConexion67.calcularDistancia ( nodoConexion73 ).toInt () ),
        Edge ( nodoConexion61, nodoConexion67, nodoConexion61.calcularDistancia ( nodoConexion67 ).toInt () ),
        Edge ( nodoConexion67, nodoConexion61, nodoConexion67.calcularDistancia ( nodoConexion61 ).toInt () ),
        Edge ( nodoAulas15, nodoConexion74, nodoAulas15.calcularDistancia ( nodoConexion74 ).toInt () ),
        Edge ( nodoConexion74, nodoAulas15, nodoConexion74.calcularDistancia ( nodoAulas15 ).toInt () ),
        Edge ( nodoConexion73, nodoConexion74, nodoConexion73.calcularDistancia ( nodoConexion74 ).toInt () ),
        Edge ( nodoConexion74, nodoConexion73, nodoConexion74.calcularDistancia ( nodoConexion73 ).toInt () ),
        Edge ( nodoConexion76, nodoConexion57, nodoConexion76.calcularDistancia ( nodoConexion57 ).toInt () ),
        Edge ( nodoConexion57, nodoConexion76, nodoConexion57.calcularDistancia ( nodoConexion76 ).toInt () ),
        Edge ( nodoConexion77, nodoConexion76, nodoConexion77.calcularDistancia ( nodoConexion76 ).toInt () ),
        Edge ( nodoConexion76, nodoConexion77, nodoConexion76.calcularDistancia ( nodoConexion77 ).toInt () ),
        Edge ( nodoConexion78, nodoConexion77, nodoConexion78.calcularDistancia ( nodoConexion77 ).toInt () ),
        Edge ( nodoConexion77, nodoConexion78, nodoConexion77.calcularDistancia ( nodoConexion78 ).toInt () ),
        Edge ( nodoLabIngElec11, nodoConexion77, nodoLabIngElec11.calcularDistancia ( nodoConexion77 ).toInt () ),
        Edge ( nodoConexion77, nodoLabIngElec11, nodoConexion77.calcularDistancia ( nodoLabIngElec11 ).toInt () ),
        Edge ( nodoConexion79, nodoConexion76, nodoConexion79.calcularDistancia ( nodoConexion76 ).toInt () ),
        Edge ( nodoConexion76, nodoConexion79, nodoConexion76.calcularDistancia ( nodoConexion79 ).toInt () ),
        Edge ( nodoLabQuiCual, nodoConexion78, nodoLabQuiCual.calcularDistancia ( nodoConexion78 ).toInt () ),
        Edge ( nodoConexion78, nodoLabQuiCual, nodoConexion78.calcularDistancia ( nodoLabQuiCual ).toInt () ),
        Edge ( nodoConexion80, nodoConexion78, nodoConexion80.calcularDistancia ( nodoConexion78 ).toInt () ),
        Edge ( nodoConexion78, nodoConexion80, nodoConexion78.calcularDistancia ( nodoConexion80 ).toInt () ),
        Edge ( nodoConexion80, nodoConexion79, nodoConexion80.calcularDistancia ( nodoConexion79 ).toInt () ),
        Edge ( nodoConexion79, nodoConexion80, nodoConexion79.calcularDistancia ( nodoConexion80 ).toInt () ),
        Edge ( nodoLabQuimCuan, nodoConexion79, nodoLabQuimCuan.calcularDistancia ( nodoConexion79 ).toInt () ),
        Edge ( nodoConexion79, nodoLabQuimCuan, nodoConexion79.calcularDistancia ( nodoLabQuimCuan ).toInt () ),
        Edge ( nodoConexion81, nodoConexion80, nodoConexion81.calcularDistancia ( nodoConexion80 ).toInt () ),
        Edge ( nodoConexion80, nodoConexion81, nodoConexion80.calcularDistancia ( nodoConexion81 ).toInt () ),
        Edge ( nodoConexion82, nodoConexion81, nodoConexion82.calcularDistancia ( nodoConexion81 ).toInt () ),
        Edge ( nodoConexion81, nodoConexion82, nodoConexion81.calcularDistancia ( nodoConexion82 ).toInt () ),
        Edge ( nodoConexion82, nodoConexion73, nodoConexion82.calcularDistancia ( nodoConexion73 ).toInt () ),
        Edge ( nodoConexion73, nodoConexion82, nodoConexion73.calcularDistancia ( nodoConexion82 ).toInt () ),
        Edge ( nodoConexion83, nodoConexion82, nodoConexion83.calcularDistancia ( nodoConexion82 ).toInt () ),
        Edge ( nodoConexion82, nodoConexion83, nodoConexion82.calcularDistancia ( nodoConexion83 ).toInt () ),
        Edge ( nodoConexion71, nodoConexion70, nodoConexion71.calcularDistancia ( nodoConexion70 ).toInt () ),
        Edge ( nodoConexion70, nodoConexion71, nodoConexion70.calcularDistancia ( nodoConexion71 ).toInt () ),
        Edge ( nodoConexion71, nodoConexion83, nodoConexion71.calcularDistancia ( nodoConexion83 ).toInt () ),
        Edge ( nodoConexion83, nodoConexion71, nodoConexion83.calcularDistancia ( nodoConexion71 ).toInt () ),
        Edge ( nodoAulas14, nodoConexion71, nodoAulas14.calcularDistancia ( nodoConexion71 ).toInt () ),
        Edge ( nodoConexion71, nodoAulas14, nodoConexion71.calcularDistancia ( nodoAulas14 ).toInt () ),
        Edge ( nodoConexion84, nodoConexion83, nodoConexion84.calcularDistancia ( nodoConexion83 ).toInt () ),
        Edge ( nodoConexion83, nodoConexion84, nodoConexion83.calcularDistancia ( nodoConexion84 ).toInt () ),
        Edge ( nodoConexion85, nodoConexion81, nodoConexion85.calcularDistancia ( nodoConexion81 ).toInt () ),
        Edge ( nodoConexion81, nodoConexion85, nodoConexion81.calcularDistancia ( nodoConexion85 ).toInt () ),
        Edge ( nodoCafeteria, nodoConexion85, nodoCafeteria.calcularDistancia ( nodoConexion85 ).toInt () ),
        Edge ( nodoConexion85, nodoCafeteria, nodoConexion85.calcularDistancia ( nodoCafeteria ).toInt () ),
        Edge ( nodoCafeteria, nodoConexion84, nodoCafeteria.calcularDistancia ( nodoConexion84 ).toInt () ),
        Edge ( nodoConexion84, nodoCafeteria, nodoConexion84.calcularDistancia ( nodoCafeteria ).toInt () ),
        Edge ( nodoConexion77, nodoConexion86, nodoConexion77.calcularDistancia ( nodoConexion86 ).toInt () ),
        Edge ( nodoConexion86, nodoConexion77, nodoConexion86.calcularDistancia ( nodoConexion77 ).toInt () ),
        Edge ( nodoConexion86, nodoMetMecAulas10, nodoConexion86.calcularDistancia ( nodoMetMecAulas10 ).toInt () ),
        Edge ( nodoMetMecAulas10, nodoConexion86, nodoMetMecAulas10.calcularDistancia ( nodoConexion86) .toInt () ),
        Edge ( nodoConexion86, nodoConexion87, nodoConexion86.calcularDistancia ( nodoConexion87 ).toInt () ),
        Edge ( nodoConexion87, nodoConexion86, nodoConexion87.calcularDistancia ( nodoConexion86 ).toInt () ),
        Edge ( nodoConexion87, nodoConexion91, nodoConexion87.calcularDistancia ( nodoConexion91 ).toInt () ),
        Edge ( nodoConexion91, nodoConexion87, nodoConexion91.calcularDistancia ( nodoConexion87 ).toInt () ),
        Edge ( nodoConexion87, nodoConexion88, nodoConexion87.calcularDistancia ( nodoConexion88 ).toInt () ),
        Edge ( nodoConexion88, nodoConexion87, nodoConexion88.calcularDistancia ( nodoConexion87 ).toInt () ),
        Edge ( nodoConexion91, nodoConexion88, nodoConexion91.calcularDistancia ( nodoConexion88 ).toInt () ),
        Edge ( nodoConexion88, nodoConexion91, nodoConexion88.calcularDistancia ( nodoConexion91 ).toInt () ),
        Edge ( nodoConexion85, nodoConexion88, nodoConexion85.calcularDistancia ( nodoConexion88 ).toInt () ),
        Edge ( nodoConexion88, nodoConexion85, nodoConexion88.calcularDistancia ( nodoConexion85 ).toInt () ),
        Edge ( nodoConexion91, nodoEcoAdmin, nodoConexion91.calcularDistancia ( nodoEcoAdmin ).toInt () ),
        Edge ( nodoEcoAdmin, nodoConexion91, nodoEcoAdmin.calcularDistancia ( nodoConexion91 ).toInt () ),
        Edge ( nodoConexion88, nodoConexion89, nodoConexion88.calcularDistancia ( nodoConexion89 ).toInt () ),
        Edge ( nodoConexion89, nodoConexion88, nodoConexion89.calcularDistancia ( nodoConexion88 ).toInt () ),
        Edge ( nodoConexion89, nodoConexion90, nodoConexion89.calcularDistancia ( nodoConexion90 ).toInt () ),
        Edge ( nodoConexion90, nodoConexion89, nodoConexion90.calcularDistancia ( nodoConexion89 ).toInt () ),
        Edge ( nodoConexion84, nodoConexion90, nodoConexion84.calcularDistancia ( nodoConexion90 ).toInt () ),
        Edge ( nodoConexion90, nodoConexion84, nodoConexion90.calcularDistancia ( nodoConexion84 ).toInt () ),
        Edge ( nodoConexion90, nodoAulas8, nodoConexion90.calcularDistancia ( nodoAulas8 ).toInt () ),
        Edge ( nodoAulas8, nodoConexion90, nodoAulas8.calcularDistancia ( nodoConexion90 ).toInt () ),
        Edge ( nodoConexion87, nodoConexion92, nodoConexion87.calcularDistancia ( nodoConexion92 ).toInt () ),
        Edge ( nodoConexion92, nodoConexion87, nodoConexion92.calcularDistancia ( nodoConexion87 ).toInt () ),
        Edge ( nodoConexion92, nodosAulas6, nodoConexion92.calcularDistancia ( nodosAulas6 ).toInt () ),
        Edge ( nodosAulas6, nodoConexion92, nodosAulas6.calcularDistancia ( nodoConexion92 ).toInt () ),
        Edge ( nodoConexion92, nodoConexion100, nodoConexion92.calcularDistancia ( nodoConexion100 ).toInt () ),
        Edge ( nodoConexion100, nodoConexion92, nodoConexion100.calcularDistancia ( nodoConexion92 ).toInt () ),
        Edge ( nodoConexion100, nodoConexion99, nodoConexion100.calcularDistancia ( nodoConexion99 ).toInt () ),
        Edge ( nodoConexion99, nodoConexion100, nodoConexion99.calcularDistancia ( nodoConexion100 ).toInt () ),
        Edge ( nodoConexion99, nodoIngInd4, nodoConexion99.calcularDistancia ( nodoIngInd4 ).toInt () ),
        Edge ( nodoIngInd4, nodoConexion99, nodoIngInd4.calcularDistancia ( nodoConexion99 ).toInt () ),
        Edge ( nodoConexion99, nodoConexion98, nodoConexion99.calcularDistancia ( nodoConexion98 ).toInt () ),
        Edge ( nodoConexion98, nodoConexion99, nodoConexion98.calcularDistancia ( nodoConexion99 ).toInt () ),
        Edge ( nodoConexion89, nodoConexion98, nodoConexion89.calcularDistancia ( nodoConexion98 ).toInt () ),
        Edge ( nodoConexion98, nodoConexion89, nodoConexion98.calcularDistancia ( nodoConexion89 ).toInt () ),
        Edge ( nodoConexion98, nodoConexion97, nodoConexion98.calcularDistancia ( nodoConexion97 ).toInt () ),
        Edge ( nodoConexion97, nodoConexion98, nodoConexion97.calcularDistancia ( nodoConexion98 ).toInt () ),
        Edge ( nodoConexion97, nodoAulas7, nodoConexion97.calcularDistancia ( nodoAulas7 ).toInt () ),
        Edge ( nodoAulas7, nodoConexion97, nodoAulas7.calcularDistancia ( nodoConexion97 ).toInt () ),
        Edge ( nodoConexion97, nodoConexion96, nodoConexion97.calcularDistancia ( nodoConexion96 ).toInt () ),
        Edge ( nodoConexion96, nodoConexion97, nodoConexion96.calcularDistancia ( nodoConexion97 ).toInt () ),
        Edge ( nodoConexion96, nodoConexion95, nodoConexion96.calcularDistancia ( nodoConexion95 ).toInt () ),
        Edge ( nodoConexion95, nodoConexion96, nodoConexion95.calcularDistancia ( nodoConexion96 ).toInt () ),
        Edge ( nodoConexion95, nodoConexion94, nodoConexion95.calcularDistancia ( nodoConexion94 ).toInt () ),
        Edge ( nodoConexion94, nodoConexion95, nodoConexion94.calcularDistancia ( nodoConexion95 ).toInt () ),
        Edge ( nodoConexion94, nodoConexion93, nodoConexion94.calcularDistancia ( nodoConexion93 ).toInt () ),
        Edge ( nodoConexion93, nodoConexion94, nodoConexion93.calcularDistancia ( nodoConexion94 ).toInt () ),
        Edge ( nodoConexion90, nodoConexion93, nodoConexion90.calcularDistancia ( nodoConexion93 ).toInt () ),
        Edge ( nodoConexion93, nodoConexion90, nodoConexion93.calcularDistancia ( nodoConexion90 ).toInt () ),
        Edge ( nodoConexion100, nodoConexion101, nodoConexion100.calcularDistancia ( nodoConexion101 ).toInt () ),
        Edge ( nodoConexion101, nodoConexion100, nodoConexion101.calcularDistancia ( nodoConexion100 ).toInt () ),
        Edge ( nodoConexion101, nodoConexion102, nodoConexion101.calcularDistancia ( nodoConexion102 ).toInt () ),
        Edge ( nodoConexion102, nodoConexion101, nodoConexion102.calcularDistancia ( nodoConexion101 ).toInt () ),
        Edge ( nodoConexion102, nodoAulas5, nodoConexion102.calcularDistancia ( nodoAulas5 ).toInt () ),
        Edge ( nodoAulas5, nodoConexion102, nodoAulas5.calcularDistancia ( nodoConexion102 ).toInt () ),
        Edge ( nodoConexion101, nodoConexion103, nodoConexion101.calcularDistancia ( nodoConexion103 ).toInt () ),
        Edge ( nodoConexion103, nodoConexion101, nodoConexion103.calcularDistancia ( nodoConexion101 ).toInt () ),
        Edge ( nodoConexion103, nodoConexion104, nodoConexion103.calcularDistancia ( nodoConexion104 ).toInt () ),
        Edge ( nodoConexion104, nodoConexion103, nodoConexion104.calcularDistancia ( nodoConexion103 ).toInt () ),
        Edge ( nodoConexion98, nodoConexion104, nodoConexion98.calcularDistancia ( nodoConexion104 ).toInt () ),
        Edge ( nodoConexion104, nodoConexion98, nodoConexion104.calcularDistancia ( nodoConexion98 ).toInt () ),
        Edge ( nodoConexion104, nodoConexion105, nodoConexion104.calcularDistancia ( nodoConexion105 ).toInt () ),
        Edge ( nodoConexion105, nodoConexion104, nodoConexion105.calcularDistancia ( nodoConexion104 ).toInt () ),
        Edge ( nodoConexion104, nodoConexion106, nodoConexion104.calcularDistancia ( nodoConexion106 ).toInt () ),
        Edge ( nodoConexion106, nodoConexion104, nodoConexion106.calcularDistancia ( nodoConexion104 ).toInt () ),
        Edge ( nodoConexion105, nodoOfiSind, nodoConexion105.calcularDistancia ( nodoOfiSind ).toInt () ),
        Edge ( nodoOfiSind, nodoConexion105, nodoOfiSind.calcularDistancia ( nodoConexion105 ).toInt () ),
        Edge ( nodoConexion106, nodoConexion107, nodoConexion106.calcularDistancia ( nodoConexion107 ).toInt () ),
        Edge ( nodoConexion107, nodoConexion106, nodoConexion107.calcularDistancia ( nodoConexion106 ).toInt () ),
        Edge ( nodoConexion107, nodo1A, nodoConexion107.calcularDistancia ( nodo1A ).toInt () ),
        Edge ( nodo1A, nodoConexion107, nodo1A.calcularDistancia ( nodoConexion107 ).toInt () ),
        Edge ( nodoConexion107, nodo1B, nodoConexion107.calcularDistancia ( nodo1B ).toInt () ),
        Edge ( nodo1B, nodoConexion107, nodo1B.calcularDistancia ( nodoConexion107 ).toInt () ),
        Edge ( nodoConexion15, nodoGradas, nodoConexion15.calcularDistancia ( nodoGradas ).toInt () ),
        Edge ( nodoGradas, nodoConexion15, nodoGradas.calcularDistancia ( nodoConexion15 ).toInt () ),
        Edge ( nodoConexion107, nodoConexion108, nodoConexion107.calcularDistancia ( nodoConexion108 ).toInt () ),
        Edge ( nodoConexion108, nodoConexion107, nodoConexion108.calcularDistancia ( nodoConexion107 ).toInt () ),
        Edge ( nodoConexion108, nodoConexion109, nodoConexion108.calcularDistancia ( nodoConexion109 ).toInt () ),
        Edge ( nodoConexion109, nodoConexion108, nodoConexion109.calcularDistancia ( nodoConexion108 ).toInt () ),
        Edge ( nodoConexion98, nodoConexion109, nodoConexion98.calcularDistancia ( nodoConexion109 ).toInt () ),
        Edge ( nodoConexion109, nodoConexion98, nodoConexion109.calcularDistancia ( nodoConexion98 ).toInt () ),
        Edge ( nodoConexion109, nodoEntrada1, nodoConexion109.calcularDistancia ( nodoEntrada1 ).toInt () ),
        Edge ( nodoEntrada1, nodoConexion109, nodoEntrada1.calcularDistancia ( nodoConexion109 ).toInt () ),
        )



   // val result = findShortestPath(graph,nodoEntrada2,nodoEdificio19)

    private var textoSeleccionadoOrigen : String? = null
    private var textoSeleccionadoDestino : String? = null
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
        autotextviewOrigen = findViewById<AutoCompleteTextView> ( R.id.actvBuscador )
        autotextviewDestino = findViewById<AutoCompleteTextView> ( R.id.actvBuscador2 )
        val edificiosOrigen = resources.getStringArray ( R.array.edificiosOrigen )
        val edificiosDestino = resources.getStringArray ( R.array.edificios )
        val adapterOrigen = ArrayAdapter ( this, android.R.layout.simple_list_item_1, edificiosOrigen )
        val adapterDestino = ArrayAdapter ( this, android.R.layout.simple_list_item_1, edificiosDestino )
        autotextviewOrigen.setAdapter ( adapterOrigen )
        autotextviewDestino.setAdapter ( adapterDestino )


        //Texto del textView de donde parte el usuario
        autotextviewOrigen.setOnItemClickListener { _, _, position, _ ->
            // Obtener el texto seleccionado de origen

            textoSeleccionadoOrigen = adapterOrigen.getItem( position ).toString()
            Toast.makeText( applicationContext, "Partes de: $textoSeleccionadoOrigen", Toast.LENGTH_SHORT ).show()

            //Si el usuario no selecciono origen, automaticamente tomara la ubicacion
            //actual como origen
            if( textoSeleccionadoOrigen.equals( "" ) ) {
                textoSeleccionadoOrigen= "Ubicacion actual"
            }

        }

        //Texto del textView de a donde se dirige el usuario
        autotextviewDestino.setOnItemClickListener { _, _, position, _ ->
            // Obtener el texto seleccionado de Destino
            textoSeleccionadoDestino = adapterDestino.getItem( position ).toString()
            Toast.makeText( applicationContext, "Te diriges a: $textoSeleccionadoDestino", Toast.LENGTH_SHORT ).show()


        }

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
            Place ( nodoEdificio19.nombre, LatLng ( nodoEdificio19.getLat (), nodoEdificio19.getLong () ) ),
            Place ( nodoLabComputo.nombre, LatLng ( nodoLabComputo.getLat (), nodoLabComputo.getLong () ) ),
            Place ( nodoEntrada2.nombre, LatLng ( nodoEntrada2.getLat (), nodoEntrada2.getLong () ) ),
            Place ( nodoComedor.nombre, LatLng ( nodoComedor.getLat (), nodoComedor.getLong () ) ),
            Place ( nodoBiblioteca.nombre, LatLng ( nodoBiblioteca.getLat (), nodoBiblioteca.getLong () ) ),
            Place ( nodoGimnasio.nombre, LatLng ( nodoGimnasio.getLat (), nodoGimnasio.getLong () ) ),
            Place ( nodoBeis.nombre, LatLng ( nodoBeis.getLat (), nodoBeis.getLong () ) ),
            Place ( nodoAlberca.nombre, LatLng ( nodoAlberca.getLat (), nodoAlberca.getLong () ) ),
            Place ( nodoBasket1.nombre, LatLng ( nodoBasket1.getLat (), nodoBasket1.getLong () ) ),
            Place ( nodoCampoFutbol.nombre, LatLng ( nodoCampoFutbol.getLat (), nodoCampoFutbol.getLong () ) ),
            Place ( nodoAtletismo.nombre, LatLng ( nodoAtletismo.getLat (), nodoAtletismo.getLong () ) ),
            Place ( nodoKiosco.nombre, LatLng ( nodoKiosco.getLat (), nodoKiosco.getLong () ) ),
            Place ( nodoAulas38.nombre, LatLng ( nodoAulas38.getLat (), nodoAulas38.getLong () ) ),
            Place ( nodoAulas37.nombre, LatLng ( nodoAulas37.getLat (), nodoAulas37.getLong () ) ),
            Place ( nodoRecMatsServ35.nombre, LatLng ( nodoRecMatsServ35.getLat (), nodoRecMatsServ35.getLong () ) ),
            Place ( nodoElecElectro36.nombre, LatLng ( nodoElecElectro36.getLat (), nodoElecElectro36.getLong () ) ),
            Place ( nodoAulas34.nombre, LatLng ( nodoAulas34.getLat (), nodoAulas34.getLong () ) ),
            Place ( nodoAulas33.nombre, LatLng ( nodoAulas33.getLat (), nodoAulas33.getLong () ) ),
            Place ( nodoBasket2.nombre, LatLng ( nodoBasket2.getLat (), nodoBasket2.getLong () ) ),
            Place ( nodoMantenimiento.nombre, LatLng ( nodoMantenimiento.getLat (), nodoMantenimiento.getLong () ) ),
            Place ( nodoIdiomas.nombre, LatLng ( nodoIdiomas.getLat (), nodoIdiomas.getLong () ) ),
            Place ( nodoCIM.nombre, LatLng ( nodoCIM.getLat (), nodoCIM.getLong () ) ),
            Place ( nodoUsosMultiples.nombre, LatLng ( nodoUsosMultiples.getLat (), nodoUsosMultiples.getLong () ) ),
            Place ( nodoLabIndustrial.nombre, LatLng ( nodoLabIndustrial.getLat (), nodoLabIndustrial.getLong () ) ),
            Place ( nodoLabCompPos.nombre, LatLng ( nodoLabCompPos.getLat (), nodoLabCompPos.getLong () ) ),
            Place ( nodoLabMaqElecIns.nombre, LatLng ( nodoLabMaqElecIns.getLat (), nodoLabMaqElecIns.getLong () ) ),
            Place ( nodoAulas32.nombre, LatLng ( nodoAulas32.getLat (), nodoAulas32.getLong () ) ),
            Place ( nodoDivPosInv.nombre, LatLng ( nodoDivPosInv.getLat (), nodoDivPosInv.getLong () ) ),
            Place ( nodoLabIngPot.nombre, LatLng ( nodoLabIngPot.getLat (), nodoLabIngPot.getLong () ) ),
            Place ( nodoLabMecCon.nombre, LatLng ( nodoLabMecCon.getLat (), nodoLabMecCon.getLong () ) ),
            Place ( nodoLabIngElect.nombre, LatLng ( nodoLabIngElect.getLat (), nodoLabIngElect.getLong () ) ),
            Place ( nodoLabIngMec18.nombre, LatLng ( nodoLabIngMec18.getLat (), nodoLabIngMec18.getLong () ) ),
            Place ( nodoCenMejCon.nombre, LatLng ( nodoCenMejCon.getLat (), nodoCenMejCon.getLong () ) ),
            Place ( nodoLabMultiMetal.nombre, LatLng ( nodoLabMultiMetal.getLat (), nodoLabMultiMetal.getLong () ) ),
            Place ( nodoLabPotElectCien.nombre, LatLng ( nodoLabPotElectCien.getLat (), nodoLabPotElectCien.getLong () ) ),
            Place ( nodoAulas15.nombre, LatLng ( nodoAulas15.getLat (), nodoAulas15.getLong () ) ),
            Place ( nodoLabIngElec11.nombre, LatLng ( nodoLabIngElec11.getLat (), nodoLabIngElec11.getLong () ) ),
            Place ( nodoLabQuiCual.nombre, LatLng ( nodoLabQuiCual.getLat (), nodoLabQuiCual.getLong () ) ),
            Place ( nodoLabQuimCuan.nombre, LatLng ( nodoLabQuimCuan.getLat (), nodoLabQuimCuan.getLong () ) ),
            Place ( nodoAulas14.nombre, LatLng ( nodoAulas14.getLat (), nodoAulas14.getLong () ) ),
            Place ( nodoCafeteria.nombre, LatLng ( nodoCafeteria.getLat (), nodoCafeteria.getLong () ) ),
            Place ( nodoMetMecAulas10.nombre, LatLng ( nodoMetMecAulas10.getLat (), nodoMetMecAulas10.getLong () ) ),
            Place ( nodoEcoAdmin.nombre, LatLng ( nodoEcoAdmin.getLat (), nodoEcoAdmin.getLong () ) ),
            Place ( nodoAulas8.nombre, LatLng ( nodoAulas8.getLat (), nodoAulas8.getLong () ) ),
            Place ( nodosAulas6.nombre, LatLng ( nodosAulas6.getLat (), nodosAulas6.getLong () ) ),
            Place ( nodoIngInd4.nombre, LatLng ( nodoIngInd4.getLat (), nodoIngInd4.getLong () ) ),
            Place ( nodoAulas7.nombre, LatLng ( nodoAulas7.getLat (), nodoAulas7.getLong () ) ),
            Place ( nodoAulas5.nombre, LatLng ( nodoAulas5.getLat (), nodoAulas5.getLong () ) ),
            Place ( nodoOfiSind.nombre, LatLng ( nodoOfiSind.getLat (), nodoOfiSind.getLong () ) ),
            Place ( nodo1A.nombre, LatLng ( nodo1A.getLat (), nodo1A.getLong () ) ),
            Place ( nodo1B.nombre, LatLng ( nodo1B.getLat (), nodo1B.getLong () ) ),
            Place ( nodoGradas.nombre, LatLng ( nodoGradas.getLat (), nodoGradas.getLong () ) ),
            Place ( nodoEntrada1.nombre, LatLng ( nodoEntrada1.getLat (), nodoEntrada1.getLong () ) ),
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
        //Validación para que el usuario seleccione un Destino forzosamente
        if ( textoSeleccionadoDestino.equals ("" ) ){
            Toast.makeText ( this, "No has seleccionado un destino", Toast.LENGTH_LONG ).show ()
            return
        }
        // Origen diferente a la ubicacion actual o a una cadena vacia, por lo tanto el usuario
        //parte desde un edificio
        if( !textoSeleccionadoOrigen.equals ( "Ubicacion actual" ) || !textoSeleccionadoOrigen.equals ( "") ){

            val result = findShortestPath(graph,origen ( textoSeleccionadoOrigen!! )!!,destino ( textoSeleccionadoDestino!! )!! )
            createPolylines( result.shortestPath() )
        } else {
            // Origen es igual a la ubicacion actual, por lo tanto se crea un nuevo nodo
            // Para la ubicacion
            val nodoUbicacionActual= Nodo ( "Ubicacion actual", "ubi",LatLng ( ubicacionActualLat,ubicacionActualLong ) )
            for( nodo in nodosConexiones ){
                i++
                //Toast.makeText( this,"tam: "+nodosConexiones.size,Toast.LENGTH_SHORT).show()
                val distancia= nodoUbicacionActual.calcularDistancia ( nodo );
                //Toast.makeText (this,"vuelta:"+i+"distancia = " + distancia, Toast.LENGTH_SHORT ).show ()
                if ( distancia<5 ) {
                    graph.add ( Edge ( nodoUbicacionActual,nodo,nodoUbicacionActual.calcularDistancia ( nodo ).toInt () ) )
                }
            }

            val result = findShortestPath ( graph, nodoUbicacionActual ,
                destino( textoSeleccionadoDestino!! ) !!
            )
            createPolylines( result.shortestPath() )
        }

    }

    //----------------------------------------------------------------------------------------------

    //Metodo para seleccionar el nodo destino

    private fun destino(destino:String): Nodo ? {
        for (nodo in nodosLugares)
            if (destino.equals(nodo.nombre)) {
                return nodo
            }
        return null
    }
    //Metodo para seleccionar el nodo origen
    private fun origen(origen:String): Nodo ? {
        for (nodo in nodosLugares)
            if (origen.equals(nodo.nombre)) {
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
