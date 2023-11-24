/***************************************************************************************************
                        Nodo.kt Última modificación: 18/Noviembre/2023
 ***************************************************************************************************/

package mx.edu.itl.polarisapp.model

import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

class Nodo ( var nombre : String, var id : String, var coords : LatLng ) {



    //----------------------------------------------------------------------------------------------

    fun getLat () : Double {
        return coords.latitude
    }

    //----------------------------------------------------------------------------------------------

    override fun toString () : String {
        return "El nodo es: "+ this.nombre
    }

    //----------------------------------------------------------------------------------------------

    fun getLong () : Double {
        return coords.longitude
    }

    //----------------------------------------------------------------------------------------------

    fun calcularDistancia ( nodo1 : Nodo ) : Double {
        val radioTierra= 6371

        val latNodo1 = this.getLat ()
        val longNodo1 = this.getLong ()
        val latNodo2 = nodo1.getLat ()
        val longNodo2 = nodo1.getLong ()

        //Convertir grados a radianes
        val latNodo1Rad = Math.toRadians ( latNodo1 )
        val longNodo1Rad = Math.toRadians ( longNodo1 )
        val latNodo2Rad = Math.toRadians ( latNodo2 )
        val longNodo2Rad = Math.toRadians ( longNodo2 )

        //Diferencias entre coordenadas
        val dlat = latNodo2Rad - latNodo1Rad
        val dlong = longNodo2Rad - longNodo1Rad

        //Fórmula Haversina
        val a = sin ( dlat / 2 ).pow ( 2 ) + cos ( latNodo1Rad ) * cos ( latNodo2Rad ) * sin ( dlong / 2 ).pow ( 2 )
        val c = 2 * atan2 ( sqrt ( a ), sqrt ( 1 - a ) )

        val distancia = ( radioTierra * c ) * 100




        return distancia
    }

    //----------------------------------------------------------------------------------------------

    fun calcularRuta () : Nodo {
        return this
    }

    //----------------------------------------------------------------------------------------------

}