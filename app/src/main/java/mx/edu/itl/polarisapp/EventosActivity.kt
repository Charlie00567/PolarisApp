package mx.edu.itl.polarisapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class EventosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos)
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