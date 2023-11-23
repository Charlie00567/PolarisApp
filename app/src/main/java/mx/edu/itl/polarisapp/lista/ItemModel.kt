/***************************************************************************************************
                    ItemModel.kt Última modificación: 21/Noviembre/2023
 ***************************************************************************************************/
package mx.edu.itl.polarisapp.lista

class ItemModel {

    var imageRscId : String =""
    var txtTitulo : String = ""
    var txtFecha : String = ""
    var txtHoraA : String = ""
    var txtHoraC : String = ""
    var txtLugar : String = ""
    var txtDescripcion : String = ""
    //----------------------------------------------------------------------------------------------
    //Constructor
    constructor(
        imageRscId: String,
        txtTitulo: String,
        txtFecha: String,
        txtHoraA: String,
        txtHoraC : String,
        txtLugar: String,
        txtDescripcion: String
    ) {
        this.imageRscId = imageRscId
        this.txtTitulo = txtTitulo
        this.txtFecha = txtFecha
        this.txtHoraA = txtHoraA
        this.txtHoraC = txtHoraC
        this.txtLugar = txtLugar
        this.txtDescripcion = txtDescripcion
    }

    //----------------------------------------------------------------------------------------------

    fun getRecursoImg() : String {
        return imageRscId
    }
    //----------------------------------------------------------------------------------------------
    fun getTitulo() : String {
        return txtTitulo
    }
    //----------------------------------------------------------------------------------------------
    fun getFecha() : String {
        return txtFecha
    }
    //----------------------------------------------------------------------------------------------
    fun getHoraA() : String {
        return txtHoraA
    }
    //----------------------------------------------------------------------------------------------
    fun getLugar() : String {
        return  txtLugar
    }
    //----------------------------------------------------------------------------------------------
    fun getDescripcion() : String {
        return txtDescripcion
    }
    fun getHoraC() : String {
        return txtHoraC
    }
}