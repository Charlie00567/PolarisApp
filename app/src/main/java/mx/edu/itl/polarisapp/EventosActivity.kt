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

class EventosActivity : AppCompatActivity() {
    private lateinit var requestQueue : RequestQueue
    private lateinit var listView : ListView
    private lateinit var adapter : CustomListAdapter
    private lateinit var tituloEvento : String
    private  var posicionElementoSeleccionado : Int = 0

    val eventosLista: MutableList<ItemModel> = mutableListOf()
    val urlEliminar = "https://polarisappnavegator.000webhostapp.com/eliminarEvento.php"
    override fun onCreate ( savedInstanceState: Bundle? ) {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.activity_eventos )
        requestQueue = Volley.newRequestQueue( this )
        listView = findViewById( R.id.listEventos )
        //leerEventos()

    }

    fun regresarMenu ( view : View){

        val intent = Intent( this,MainActivity::class.java )
         startActivity ( intent )
    }
    fun btnEliminar ( view:View ){
        val stringRequest = object: StringRequest(
            Method.POST,
            urlEliminar,
            Response.Listener { response ->
                Toast.makeText(this, "Evento "+ tituloEvento+ " eliminado con éxito", Toast.LENGTH_SHORT).show()

                // Puedes realizar acciones adicionales después de eliminar el evento, si es necesario

            },
            Response.ErrorListener { error->
                Toast.makeText(this, "Error al intentar eliminar evento", Toast.LENGTH_SHORT).show()
            }
        ) {

            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["titulo"] = tituloEvento
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
        eventosLista.removeAt(posicionElementoSeleccionado)
        adapter.notifyDataSetChanged()


    }
    override fun onResume () {
        super.onResume()
        leerEventos()
    }
    fun btnAgregar ( view:View ){
        val intent = Intent ( this,AgregarEventos::class.java )
        startActivity ( intent )
//        val adapter = CustomListAdapter(this,eventosLista)
//        listView = findViewById( R.id.listEventos )
//        listView.adapter = adapter
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun leerEventos (){
        var ultimoElementoSeleccionado: View? = null
        val urlLeer = "https://polarisappnavegator.000webhostapp.com/fetch.php"
        val jsonObject= JsonArrayRequest(
            Request.Method.GET,
            urlLeer,null,
            Response.Listener { response->
                Toast.makeText(this,"Se recibio",Toast.LENGTH_LONG).show()

                for (i in 0 until response.length()){
                    val evento = response.getJSONObject(i)



                    val titulo = evento.getString("titulo")
                    val descripcion = evento.getString ( "descripcion" )
                    val lugar = evento.getString( "lugar" )
                    val fecha = evento.getString( "fecha" )
                    val horaA = evento.getString( "horaA" )
                    val horaC = evento.getString( "horaC" )
                    val imagen = evento.getString ( "imagen" )
                    //val imgEnBytes = Base64.decode(evento.getString("imagen"),0,evento.getString("imagen").length)
                    //val bitmap: Bitmap? = BitmapFactory.decodeByteArray(imgEnBytes, 0, imgEnBytes.size)

                    val eventoObjeto = ItemModel(imagen,titulo,fecha,horaA,horaC,lugar,descripcion)
                    eventosLista.add(eventoObjeto)
                    //mostrarAlertDialog("hola",eventosLista.get(0).getRecursoImg())

                }

                adapter = CustomListAdapter(this,eventosLista)

                listView.adapter = adapter

            },
            Response.ErrorListener {error->
                val errorMessage = "Error al recibir eventos: ${error.message}"
                mostrarAlertDialog("Error", errorMessage)
            }

        )
        requestQueue.add(jsonObject)
        listView.setOnItemClickListener { parent, view, position, id ->
            val element:ItemModel = adapter.getItem(position) as ItemModel // The item that was clicked
            // Restaurar el color de fondo del último elemento seleccionado
            ultimoElementoSeleccionado?.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))

            // Establecer el color de fondo del nuevo elemento seleccionado
            view.setBackgroundColor(ContextCompat.getColor(this, androidx.appcompat.R.color.material_grey_300))

            // Actualizar el último elemento seleccionado
            ultimoElementoSeleccionado = view

            posicionElementoSeleccionado = position
            tituloEvento = element.txtTitulo
            Toast.makeText(this, posicionElementoSeleccionado.toString()+" Evento seleccionado: "+ tituloEvento, Toast.LENGTH_SHORT).show()

        }
    }
    private fun mostrarAlertDialog(titulo: String, mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog = builder.create()
        dialog.show()
    }

}

//public class MainActivity extends AppCompatActivity {
//    FloatingActionButton fabPrincipal, fabAcercaDe, fabSalir;
//    Animation fabAbrir, fabCerrar, girarAdelante, girarAtras;
//    boolean isOpen = false;
//
//    ListView lista;
//    String [] listaElementos = { "Ejercicios sin instrumentos", "Ejercicios con ligas", "Ejercicios con pesas" };
//    int [] listaImagenes = { R.drawable.zapatillas, R.drawable.ligas, R.drawable.pesaslista };
//
//    List<ItemModel> items = new ArrayList<>();
//    List<String> listaString;
//    ArrayAdapter<String> arrayAdapter;
//
//    //----------------------------------------------------------------------------------------------
//
//    @Override
//    protected void onCreate( Bundle savedInstanceState ) {
//        super.onCreate( savedInstanceState );
//        setContentView( R.layout.activity_main );
//
//        fabPrincipal = ( FloatingActionButton ) findViewById( R.id.fabAcciones );
//        fabAcercaDe = ( FloatingActionButton ) findViewById( R.id.fabAcercaDe );
//        fabSalir = ( FloatingActionButton ) findViewById( R.id.fabSalir );
//
//        // Se cargan las animaciones hechas para el floatinButton
//        fabAbrir = AnimationUtils.loadAnimation( this, R.anim.fab_open );
//        fabCerrar = AnimationUtils.loadAnimation( this, R.anim.fab_close );
//        girarAdelante = AnimationUtils.loadAnimation( this, R.anim.rotate_forward );
//        girarAtras = AnimationUtils.loadAnimation( this, R.anim.rotate_backward );
//
//        //------------------------------------------------------------------------------------------
//
//        // Evento del botón principal para mostrar los otros dos botones
//        fabPrincipal.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick( View view ) {
//                animarBoton();
//            }
//        });
//
//        //------------------------------------------------------------------------------------------
//
//        // Evento para iniciar el activity del acerca de
//        fabAcercaDe.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick( View view )  {
//                animarBoton ();
//
//                Intent intent = new Intent( MainActivity.this, AcercaDeActivity.class );
//                startActivity( intent );
//            }
//        });
//
//        //------------------------------------------------------------------------------------------
//
//        // Evento del botón para salir de la aplicación
//        fabSalir.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick( View view ) {
//                animarBoton ();
//                finish ();
//            }
//        });
//
//        //------------------------------------------------------------------------------------------
//
//        // Se asignan la imagen y texto para cada elemento de la lista
//        ItemModel item1 = new ItemModel( listaImagenes[0], listaElementos[0] );
//        ItemModel item2 = new ItemModel( listaImagenes[1], listaElementos[1] );
//        ItemModel item3 = new ItemModel( listaImagenes[2], listaElementos[2] );
//
//        // Se agregan a la lista
//        items.add( item1 );
//        items.add( item2 );
//        items.add( item3 );
//
//        // Aqui creamos la lista usando el adaptador personalizado
//        CustomListAdapter adapter = new CustomListAdapter( MainActivity.this, items );
//        lista = findViewById( R.id.lista );
//        lista.setAdapter( adapter );
//
//        //------------------------------------------------------------------------------------------
//
//        // En este método hacemos que mande a diferentes activitys según el elemnto que el
//        // usuario presione
//        lista.setOnItemClickListener( new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
//                ItemModel item = ( ItemModel ) parent.getItemAtPosition( position );
//                Intent intent;
//
//                switch ( position ) {
//                    case 0:
//                    intent = new Intent(MainActivity.this, SinInstrumentosActivity.class );
//                    startActivity( intent );
//                    break;
//                    case 1:
//                    intent = new Intent( MainActivity.this, ConLigasActivity.class );
//                    startActivity( intent );
//                    break;
//                    case 2:
//                    intent = new Intent( MainActivity.this, ConPesasActivity.class );
//                    startActivity( intent );
//                    break;
//                }
//            }
//        });
//
//        //------------------------------------------------------------------------------------------
//
//    }