package mx.edu.itl.polarisapp.lista

class ItemModel {

    var imageRscId : Int = 0
    var txtTitulo : String = ""
    var txtFecha : String = ""
    var txtHorario : String = ""
    var txtLugar : String = ""
    var txtDescripcion : String = ""

    constructor(
        imageRscId: Int,
        txtTitulo: String,
        txtFecha: String,
        txtHorario: String,
        txtLugar: String,
        txtDescripcion: String
    ) {
        this.imageRscId = imageRscId
        this.txtTitulo = txtTitulo
        this.txtFecha = txtFecha
        this.txtHorario = txtHorario
        this.txtLugar = txtLugar
        this.txtDescripcion = txtDescripcion
    }

    //----------------------------------------------------------------------------------------------

    fun getRecursoImg() : Int {
        return imageRscId
    }

    fun getTitulo() : String {
        return txtTitulo
    }

    fun getFecha() : String {
        return txtFecha
    }

    fun getHorario() : String {
        return txtHorario
    }

    fun getLugar() : String {
        return  txtLugar
    }

    fun getDescripcion() : String {
        return txtDescripcion
    }
}