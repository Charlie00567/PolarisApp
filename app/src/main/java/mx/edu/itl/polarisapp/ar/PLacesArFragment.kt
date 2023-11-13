package mx.edu.itl.polarisapp.ar


import com.google.ar.sceneform.ux.ArFragment

class PLacesArFragment: ArFragment(){
    override fun getAdditionalPermissions(): Array<String> {
        return arrayOf( android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION )
    }
}