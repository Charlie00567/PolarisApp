/***************************************************************************************************
                SplashActivity.kt Última modificación: 19/Noviembre/2023
 ***************************************************************************************************/
package mx.edu.itl.polarisapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView

class SplashActivity : AppCompatActivity() {

    private lateinit var gifSplash : GifImageView
    private lateinit var movGif : GifDrawable

    override fun onCreate ( savedInstanceState: Bundle? ) {
        super.onCreate ( savedInstanceState )
        setContentView ( R.layout.activity_splash )

        gifSplash = findViewById ( R.id.gifCarga )

        Handler().postDelayed( {
            val intent = Intent ( this@SplashActivity, MainActivity::class.java )
            startActivity ( intent )
            finish ()
        }, 3000)

        // Aqui se hace el intento de cargar la animación del gif
        try {
            movGif = GifDrawable ( resources, R.drawable.logo_carga )
            gifSplash.setImageDrawable ( movGif )
            movGif.start ()
        } catch ( e: Exception ) {
            Toast.makeText(
                this,
                "Gif no pudo ser cargado, porfavor reinicie la app",
                Toast.LENGTH_LONG
            )
        }
    }
}