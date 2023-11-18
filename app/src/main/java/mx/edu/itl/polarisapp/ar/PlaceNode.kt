/***************************************************************************************************
                  PlaceNode.kt Última modificación: 18/Noviembre/2023
***************************************************************************************************/

package mx.edu.itl.polarisapp.ar

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ViewRenderable
import mx.edu.itl.polarisapp.R
import mx.edu.itl.polarisapp.model.Place

class PlaceNode( val context : Context,
                 val place   : Place ) : Node ()
{

    private var placeRenderable: ViewRenderable?    = null
    private var tvPlaceName    : AppCompatTextView? = null

    //----------------------------------------------------------------------------------------------

    override fun onActivate () {
        super.onActivate()
        scene ?: return
        if( placeRenderable != null ){ return }

        ViewRenderable.builder()
            .setView( context, R.layout.place_view )
            .build()
            .thenAccept { renderable ->
                setRenderable( renderable )
                placeRenderable   = renderable
                tvPlaceName       = renderable.view.findViewById( R.id.tvPlaceName )
                tvPlaceName?.text = place.name
            }
    }

    //----------------------------------------------------------------------------------------------

}