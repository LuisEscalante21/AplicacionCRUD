package luis.escalante.myapplication


import Modelo.ClaseConexion
import Modelo.DataClassTickets
import RecyclerViewHelpers.Adaptador
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class tickets : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tickets)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtTitulo = findViewById<EditText>(R.id.txtTitulo)
        val txtDescripcion = findViewById<EditText>(R.id.txtDescripcion)
        val txtAutor = findViewById<EditText>(R.id.txtAutor)
        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtFecha = findViewById<EditText>(R.id.txtFecha)
        val btnAgregar = findViewById<Button>(R.id.btnAgregar)
        val rcvtickets= findViewById<RecyclerView>(R.id.rcvTickets)

        rcvtickets.layoutManager= LinearLayoutManager(this)

        //////////////////// TODO: MOSTRAR DATOS ///////////////////////

        fun obtenerDatos(): List<DataClassTickets>{

            val objConexion = ClaseConexion().cadenaConexion()

            val statement = objConexion?.createStatement()
            val resulSet = statement?.executeQuery("select * from Ticket")!!
            val tickets = mutableListOf<DataClassTickets>()

            while (resulSet.next()){
                val UUID_Ticket = resulSet.getString("UUID_Ticket")
                val titulo = resulSet.getString("titulo")
                val descripcion = resulSet.getString("descripcion")
                val autor = resulSet.getString("autor")
                val email_autor = resulSet.getString("email_autor")
                val fecha_ticket = resulSet.getString("fecha_ticket")

                val ticket = DataClassTickets(UUID_Ticket, titulo, descripcion, autor, email_autor, fecha_ticket)
               tickets.add(ticket)
            }
            return tickets
        }

        CoroutineScope(Dispatchers.IO).launch{
            val ticketsDB = obtenerDatos()
            withContext(Dispatchers.Main){
                val adapter = Adaptador(ticketsDB)
                rcvtickets.adapter = adapter
            }
        }

        btnAgregar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{

                val objConexion = ClaseConexion().cadenaConexion()

                val addTickets = objConexion?.prepareStatement("insert into Tickets(UUID_ticket, titulo, descripcion, autor, email_autor, fecha_ticket) values(?, ?, ?, ?, ?, ?)")!!
                addTickets.setString(1, UUID.randomUUID().toString())
                addTickets.setString(2, txtTitulo.text.toString())
                addTickets.setString(3, txtDescripcion.text.toString())
                addTickets.setString(4, txtAutor.text.toString())
                addTickets.setString(5, txtEmail.text.toString())
                addTickets.setString(6, txtFecha.text.toString())
                addTickets.executeUpdate()

                val nuevosTickets = obtenerDatos()
                withContext(Dispatchers.Main){
                    (rcvtickets.adapter as? Adaptador)?.ActualizarLista(nuevosTickets)

                    txtTitulo.setText("")
                    txtDescripcion.setText("")
                    txtAutor.setText("")
                    txtEmail.setText("")
                    txtFecha.setText("")
                }
            }
        }
    }
}