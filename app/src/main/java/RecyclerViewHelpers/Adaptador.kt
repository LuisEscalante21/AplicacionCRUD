package RecyclerViewHelpers

import Modelo.ClaseConexion
import Modelo.DataClassTickets
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import luis.escalante.myapplication.R

class Adaptador(private var Datos: List<DataClassTickets>) : RecyclerView.Adapter<ViewHolder>() {

    fun ActualizarLista(nuevaLista: List<DataClassTickets>){
        Datos = nuevaLista
        notifyDataSetChanged()
    }

    fun actualicePantalla(UUID_ticket: String, nuevoTicket: String){
        val index = Datos.indexOfFirst { it.UUID_ticket == UUID_ticket}
        Datos[index].titulo = nuevoTicket
        notifyDataSetChanged()
    }

    /////////////////// TODO: Eliminar datos

    fun eliminarDatos(titulo: String,UUID_ticket: String, posicion: Int){

        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        GlobalScope.launch(Dispatchers.IO){
            val objConexion = ClaseConexion().cadenaConexion()

            val deleteTickets= objConexion?.prepareStatement("delete from Tickets where titulo = ? AND UUID_ticket = ?")!!
            deleteTickets.setString(1, titulo)
            deleteTickets.setString(2, UUID_ticket)
            deleteTickets.executeUpdate()

            val commit =objConexion.prepareStatement("commit")!!
            commit.executeUpdate()
        }

        Datos = listaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card, parent, false)

        return ViewHolder(vista)
    }
    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tickets = Datos[position]
        holder.textView.text = tickets.titulo

        //todo: click al icon de eliminar
        holder.imgBorrar.setOnClickListener {
            val context = holder.itemView.context
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Eliminar")
            builder.setMessage("¿Desea eliminar el ticket?")

            builder.setPositiveButton("Si") { dialog, which ->
                eliminarDatos(tickets.titulo, tickets.UUID_ticket, position)
            }

            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        //todo: actualizar  datos
        fun actualizarDato(UUID_ticket: String, titulo: String, descripcion: String, autor: String, email_autor: String, fecha_ticket: String) {
            GlobalScope.launch(Dispatchers.IO) {
                val objConexion = ClaseConexion().cadenaConexion()

                val updateTickets = objConexion?.prepareStatement("UPDATE Tickets SET titulo = ?, descripcion = ?, autor = ?, email_autor = ?, fecha_ticket = ? WHERE UUID_ticket = ?")!!
                updateTickets.setString(1, titulo)
                updateTickets.setString(2, descripcion)
                updateTickets.setString(3, autor)
                updateTickets.setString(4, email_autor)
                updateTickets.setString(5, fecha_ticket)
                updateTickets.setString(6, UUID_ticket)
                updateTickets.executeUpdate()

                withContext(Dispatchers.Main) {
                }
            }
        }

        //todo: click al icon de editar
        holder.imgEditar.setOnClickListener {
            val context = holder.itemView.context
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Editar")
            builder.setMessage("¿Desea editar los datos del ticket?")

            val cuadroTexto = EditText(context)
            cuadroTexto.setHint("Datos del ticket:")

            val datosTickets = "Nombre: ${tickets.titulo}\n" +
                    "Descripción: ${tickets.descripcion}\n" +
                    "Autor: ${tickets.autor}\n" +
                    "Email del autor: ${tickets.email_autor}\n" +
                    "Fecha del ticket: ${tickets.fecha_ticket}"

            cuadroTexto.setText(datosTickets)
            builder.setView(cuadroTexto)

            builder.setPositiveButton("Actualizar") { dialog, which ->
                val datosActualizados = cuadroTexto.text.toString().split("\n")
                val titulo = datosActualizados[0].substringAfter("Nombre: ")
                val descripcion = datosActualizados[1].substringAfter("Descripción: ")
                val autor = datosActualizados[2].substringAfter("Autor: ")
                val email_autor = datosActualizados[3].substringAfter("Email del autor: ")
                val fecha_ticket = datosActualizados[4].substringAfter("Fecha del ticket: ")

                actualizarDato(tickets.UUID_ticket, titulo, descripcion, autor, email_autor, fecha_ticket)
            }

            builder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }
}