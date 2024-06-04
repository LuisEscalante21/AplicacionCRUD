package luis.escalante.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtNombre = findViewById<EditText>(R.id.txtNombre)
        val txtContrasena = findViewById<EditText>(R.id.txtContrasena)


        val btnRegistrar: Button = findViewById(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener {

            val nombre = txtNombre.text.toString()
            val contrasena = txtContrasena.text.toString()

            if (nombre.isEmpty() || contrasena.isEmpty()) {

                Toast.makeText(
                    this,
                    "Error, para acceder debes llenar todas las casillas.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                Log.i("Test de credenciales", "Correo: $nombre y Contraseña: $contrasena")
                val intent: Intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

            }
        }
    }
}