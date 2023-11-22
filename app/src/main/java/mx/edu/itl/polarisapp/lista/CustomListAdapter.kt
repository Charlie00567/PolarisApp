package mx.edu.itl.polarisapp.lista

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.shape.RoundedCornerShape
import com.bumptech.glide.Glide
import mx.edu.itl.polarisapp.EventosActivity
import mx.edu.itl.polarisapp.R

class CustomListAdapter ( contexto : Context, elementos : List<ItemModel> ) : BaseAdapter() {

    var context : Context = contexto
    var items : List<ItemModel> = elementos

     var inflater : LayoutInflater

    init {
        inflater = LayoutInflater.from( context )
    }

    override fun getCount (): Int {
        return items.size
    }

    override fun getItem (position: Int): Any {
        return items.get( position )
    }

    override fun getItemId (position: Int): Long {
        return position.toLong()
    }

    override fun getView ( position: Int, convertView: View?, parent: ViewGroup ): View {
        var view = convertView

        if (view == null) {
            view = inflater.inflate(R.layout.elementos_eventos, parent, false)
        }


        var imagenEvento : ImageView = view!!.findViewById( R.id.imgEvento )
        var txtTitulo : TextView = view.findViewById( R.id.txtTitulo )
        var txtFecha : TextView = view.findViewById( R.id.txtFecha )
        var txtHorario : TextView = view.findViewById( R.id.txtHoraInicFin )
        var txtLugar : TextView = view.findViewById( R.id.txtLugar )
        var txtDescripcion : TextView = view.findViewById( R.id.txtDescripcion )

        imagenEvento.setImageResource( R.drawable.eventosdefault )

        txtTitulo.setText( items.get( position ).getTitulo() )
        txtFecha.setText( items.get( position ).getFecha() )
        txtHorario.setText( items.get( position ).getHoraA() + "-"+items.get( position ).getHoraC() )
        txtLugar.setText( items.get( position ).getLugar() )
        txtDescripcion.setText( items.get( position ).getDescripcion() )

        return view
    }
}