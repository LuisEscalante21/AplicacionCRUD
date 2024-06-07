package luis.escalante.myapplication

import Modelo.ClaseConexion
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtNombre = findViewById<EditText>(R.id.txtNombre)
        val txtContrasena = findViewById<EditText>(R.id.txtContrasena)
        val btnIngresar: Button = findViewById(R.id.btnIngresar)

        btnIngresar.setOnClickListener {

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

            }
        }

        btnIngresar.setOnClickListener{
            val pantallaPrincipal = Intent(this, tickets::class.java)
            CoroutineScope(Dispatchers.IO).launch{

                val objConexion = ClaseConexion().cadenaConexion()

                val comprobarUsuario = objConexion?.prepareStatement("SELECT * FROM Usuarios WHERE nombre_usuario = ? AND contrasena_usuario = ?")!!
                comprobarUsuario.setString(1, txtNombre.text.toString())
                comprobarUsuario.setString(2, txtContrasena.text.toString())

                val resultado = comprobarUsuario.executeQuery()
                if (resultado.next()) {
                    startActivity(pantallaPrincipal)
                } else {
                    println("Usuario no encontrado, verifique las credenciales")
                }

                btnIngresar.setOnClickListener {
                    val pantallaLogin = Intent(this@login, tickets::class.java)
                    startActivity(pantallaLogin)
                }
             }
        }
    }
}