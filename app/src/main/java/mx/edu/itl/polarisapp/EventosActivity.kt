/***************************************************************************************************
            EventosActivity.kt Última modificación: 22/Noviembre/2023
 ***************************************************************************************************/
package mx.edu.itl.polarisapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import mx.edu.itl.polarisapp.lista.CustomListAdapter
import mx.edu.itl.polarisapp.lista.ItemModel
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class EventosActivity : AppCompatActivity () {
    //----------------------------------------------------------------------------------------------
    //Inicializacion de variables
    private lateinit var requestQueue : RequestQueue
    private lateinit var listView : ListView
    private lateinit var adapter : CustomListAdapter
    private lateinit var tituloEvento : String
    private  var posicionElementoSeleccionado : Int = 0

    val eventosLista: MutableList<ItemModel> = mutableListOf ()
    val urlEliminar = "https://polarisappnavegator.000webhostapp.com/eliminarEvento.php"
    //----------------------------------------------------------------------------------------------
    override fun onCreate ( savedInstanceState: Bundle? ) {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.activity_eventos )
        requestQueue = Volley.newRequestQueue( this )
        listView = findViewById( R.id.listEventos )
        //leerEventos()

    }
    //----------------------------------------------------------------------------------------------
    //Regresa al modulo anterior
    fun regresarMenu ( view : View){

        val intent = Intent( this,MainActivity::class.java )
         startActivity ( intent )
    }
    //----------------------------------------------------------------------------------------------
    //Elimina el evento seleccionado
    fun btnEliminar ( view:View ){
        val stringRequest = object: StringRequest(
            Method.POST,
            urlEliminar,
            Response.Listener { response ->
                Toast.makeText ( this, "Evento "+ tituloEvento+ " eliminado con éxito", Toast.LENGTH_SHORT ).show ()



            },
            Response.ErrorListener { error->
                Toast.makeText ( this, "Error al intentar eliminar evento", Toast.LENGTH_SHORT ).show ()
            }
        ) {
            //Verifica que se haya eliminado el evento con el mismo titulo

            override fun getParams (): Map< String, String > {
                val params: MutableMap< String, String > = HashMap()
                params["titulo"] = tituloEvento
                return params
            }

            @Throws ( AuthFailureError::class )
            override fun getHeaders (): Map<String, String> {
                val params: MutableMap<String, String> = HashMap ()
                params["Content-Type"] = "application/x-www-form-urlencoded"
                return params
            }
        }

        requestQueue.add ( stringRequest )
        eventosLista.removeAt ( posicionElementoSeleccionado )
        adapter.notifyDataSetChanged ()


    }
    //----------------------------------------------------------------------------------------------
    override fun onResume () {
        super.onResume ()
        leerEventos ()
    }
    //----------------------------------------------------------------------------------------------
    //Boton que manda a llamar el formulario donde se agregaran los eventos
    fun btnAgregar ( view:View ){
        val intent = Intent ( this,AgregarEventos::class.java )
        startActivity ( intent )

    }
    //----------------------------------------------------------------------------------------------
    //Metodo el cual extrae la informacion de la base de datos y la muestra en el listView
    @OptIn(ExperimentalEncodingApi::class)
    fun leerEventos (){
        var ultimoElementoSeleccionado: View? = null
        val urlLeer = "https://polarisappnavegator.000webhostapp.com/fetch.php"
        val jsonObject= JsonArrayRequest(
            Request.Method.GET,
            urlLeer,null,
            Response.Listener { response->


                for ( i in 0 until response.length () ){
                    val evento = response.getJSONObject ( i )



                    val titulo = evento.getString("titulo")
                    val descripcion = evento.getString ( "descripcion" )
                    val lugar = evento.getString( "lugar" )
                    val fecha = evento.getString( "fecha" )
                    val horaA = evento.getString( "horaA" )
                    val horaC = evento.getString( "horaC" )
                    val imagen = evento.getString ( "imagen" )


                    val eventoObjeto = ItemModel( imagen, titulo, fecha, horaA, horaC, lugar, descripcion )
                    eventosLista.add( eventoObjeto )


                }

                adapter = CustomListAdapter ( this, eventosLista )

                listView.adapter = adapter

            },
            Response.ErrorListener { error->
                mostrarAlertDialog ( "Aviso", "No hay eventos disponibles" )
            }

        )
        requestQueue.add ( jsonObject )
        //----------------------------------------------------------------------------------------------
        //ClickListener para la seleccion de eventos
        listView.setOnItemClickListener { parent, view, position, id ->
            val element:ItemModel = adapter.getItem ( position ) as ItemModel // The item that was clicked
            // Restaurar el color de fondo del último elemento seleccionado
            ultimoElementoSeleccionado?.setBackgroundColor ( ContextCompat.getColor( this, android.R.color.transparent ) )

            // Establecer el color de fondo del nuevo elemento seleccionado
            view.setBackgroundColor( ContextCompat.getColor ( this, androidx.appcompat.R.color.material_grey_300 ) )

            // Actualizar el último elemento seleccionado
            ultimoElementoSeleccionado = view

            posicionElementoSeleccionado = position
            tituloEvento = element.txtTitulo
            Toast.makeText( this, posicionElementoSeleccionado.toString () + " Evento seleccionado: " + tituloEvento, Toast.LENGTH_SHORT ).show ()

        }
    }
    //----------------------------------------------------------------------------------------------
    //AlertDialog que muestra posibles errores
    private fun mostrarAlertDialog ( titulo: String, mensaje: String ) {
        val builder = AlertDialog.Builder ( this )
        builder.setTitle ( titulo )
        builder.setMessage ( mensaje )
        builder.setPositiveButton ( "Aceptar", null )
        val dialog = builder.create ()
        dialog.show ()
    }

}

