package mx.edu.itl.polarisapp.model

import com.google.android.gms.maps.model.LatLng

class Nodo(val latLong: LatLng) {
    var nodoNorte: Nodo? = null
    var nodoSur: Nodo? = null
    var nodoEste: Nodo? = null
    var nodoOeste: Nodo? = null

    fun asignarNodoNorte(nodo: Nodo) {
        this.nodoNorte = nodo
        nodo.nodoSur=this
    }

    fun asignarNodoSur(nodo: Nodo) {
        this.nodoSur = nodo
        nodo.nodoNorte=this
    }

    fun asignarNodoEste(nodo: Nodo) {
        this.nodoEste = nodo
        nodo.nodoOeste=this
    }

    fun asignarNodoOeste(nodo: Nodo) {
        this.nodoOeste = nodo
        nodo.nodoEste=this
    }
}
