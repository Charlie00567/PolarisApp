package mx.edu.itl.polarisapp

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlin.random.Random

class AgregarEventos : AppCompatActivity() {
    private lateinit var btnSeleccionarImagen : Button
    private lateinit var imgView : ImageView
    private lateinit var edtTxtTitulo : EditText
    private lateinit var edtTxtFecha : EditText
    private lateinit var edtTxtHoraInicio : EditText
    private lateinit var edtTxtHoraFin : EditText
    private lateinit var edtTxtLugar : EditText
    private lateinit var edtTxtDescripcion : EditText
    private lateinit var requestQueue : RequestQueue

    val urlGuardar = "https://polarisappnavegator.000webhostapp.com/saveEvento.php"

    override fun onCreate ( savedInstanceState: Bundle? ) {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.activity_agregar_eventos )
        btnSeleccionarImagen = findViewById ( R.id.btnSeleccionarImg )
        imgView = findViewById ( R.id.imgViewEvento )
        edtTxtTitulo = findViewById ( R.id.edtTextTitulo )
        edtTxtFecha = findViewById ( R.id.edtTextFecha )
        edtTxtHoraInicio = findViewById ( R.id.edtTextHoraInicio )
        edtTxtHoraFin = findViewById ( R.id.edtTextHoraFin )
        edtTxtLugar = findViewById ( R.id.edtTextLugar )
        edtTxtDescripcion = findViewById ( R.id.edtTextDescripcion )
        requestQueue = Volley.newRequestQueue ( this )

    }
    fun btnAgregarEvento ( view : View ){
        val titulo = edtTxtTitulo.text.toString()
        val fecha = edtTxtFecha.text.toString()
        val horaInicio = edtTxtHoraInicio.text.toString()
        val horaFin = edtTxtHoraFin.text.toString()
        val lugar = edtTxtLugar.text.toString()
        val descripcion = edtTxtDescripcion.text.toString()
        val nombreImagen = imgView.getTag(imgView.id).toString()

        crearEvento(titulo,fecha,horaInicio,horaFin,lugar,descripcion,nombreImagen)
        val intent = Intent ( this, EventosActivity::class.java )
        startActivity ( intent)
    }

    private fun crearEvento(titulo: String, fecha: String, horaA: String, horaC: String, lugar: String, descripcion: String, imagen:String) {
        val stringRequest = object: StringRequest(
            Method.POST,
            urlGuardar,
            Response.Listener { response ->
                Toast.makeText( this,"Evento guardado con éxito", Toast.LENGTH_SHORT  ).show()

            },
            Response.ErrorListener { error->
                Toast.makeText( this,"Error al intentar guardar evento", Toast.LENGTH_SHORT  ).show()
            }
        ){


           override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()

                // Aquí se agregan los pares
                params.put("titulo",titulo)
                params.put("descripcion",descripcion)
                params.put("lugar",lugar)
                params.put("fecha",fecha)
                params.put("horaA",horaA)
                params.put("horaC",horaC)
                params.put("imagen",imagen)
                return params
            }
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/x-www-form-urlencoded"
                return params
            }
        }

        requestQueue.add(stringRequest)

    }

    fun seleccionarImg ( view : View ){
        requestPermission()
    }
    private val startForActivityGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result ->
        if( result.resultCode == Activity.RESULT_OK ){
            val data = result.data?.data
            imgView.setTag(imgView.id,"imagen"+ Random.nextLong(999))
            imgView.setImageURI ( data )
        }

    }
    private fun seleccionarImagen() {
        val intent = Intent ( Intent.ACTION_GET_CONTENT )
        intent.type = "image/*"
        startForActivityGallery.launch ( intent )
    }

    private fun requestPermission() {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
          when{

           ContextCompat.checkSelfPermission (
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                seleccionarImagen()
            }
              else -> requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
          }
        } else{
            seleccionarImagen()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){isGranted ->
        if ( isGranted ){
            seleccionarImagen()
        } else {
            Toast.makeText ( this, "Necesitas activar los permisos", Toast.LENGTH_SHORT ).show ()
        }

    }

//    fun agregarEvento ( view: View){
//
//        val intent = Intent ( this, EventosActivity::class.java )
//        startActivity ( intent)
//    }
}