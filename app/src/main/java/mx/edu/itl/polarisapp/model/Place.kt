/***************************************************************************************************
                     Place.kt Última modificación: 18/Noviembre/2023
***************************************************************************************************/

package mx.edu.itl.polarisapp.model

import com.google.android.gms.maps.model.LatLng
import com.google.ar.sceneform.math.Vector3
import com.google.maps.android.ktx.utils.sphericalHeading
import kotlin.math.cos
import kotlin.math.sin
class Place ( val name  : String,
              val latLng : LatLng )
{

    //----------------------------------------------------------------------------------------------

    fun getPositionVector( azimuth: Float, latLng: LatLng ): Vector3{
        val heading = latLng.sphericalHeading( this.latLng )
        val r       = -2f
        val x       = r * sin( azimuth + heading ).toFloat()
        val y       = 0.5f
        val z       = r * cos( azimuth + heading ).toFloat()
        return Vector3( x, y, z )
    }

    //----------------------------------------------------------------------------------------------

}
