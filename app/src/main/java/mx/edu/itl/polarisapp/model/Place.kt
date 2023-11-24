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

    fun getPositionVector( distancia : Double, azimuth: Float, latLng: LatLng ): Vector3 {
        val heading = latLng.sphericalHeading(this.latLng)
        val x = distancia * sin(azimuth + heading).toFloat()
        val y = 0.5f
        val z = distancia * cos(azimuth + heading).toFloat()
        return Vector3(x.toFloat(), y, z.toFloat())
    }

    //----------------------------------------------------------------------------------------------

}
