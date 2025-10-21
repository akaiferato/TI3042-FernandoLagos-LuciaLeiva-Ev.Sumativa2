package com.example.appmovil_ev2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.Normalizer

class usuarioAdapter(private var usuarios: List<Usuario>, private val onItemClick: (Usuario) -> Unit) : RecyclerView.Adapter<usuarioAdapter.UsuarioViewHolder>(), Filterable {

    private var usuariosFiltrados: List<Usuario> = usuarios

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuariosFiltrados[position]
        holder.bind(usuario)
    }

    override fun getItemCount(): Int = usuariosFiltrados.size

    inner class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreCompleto: TextView = itemView.findViewById(R.id.tvNombreCompleto)
        private val email: TextView = itemView.findViewById(R.id.tvEmail)

        fun bind(usuario: Usuario) {
            val nombre = "${usuario.nombre} ${usuario.apellido}"
            nombreCompleto.text = nombre
            email.text = usuario.email
            itemView.setOnClickListener { onItemClick(usuario) }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val busqueda = constraint.toString().unaccent().lowercase().trim()
                usuariosFiltrados = if (busqueda.isEmpty()) {
                    usuarios
                } else {
                    usuarios.filter {
                        it.nombre.unaccent().lowercase().contains(busqueda) ||
                                it.apellido.unaccent().lowercase().contains(busqueda)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = usuariosFiltrados
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                usuariosFiltrados = results?.values as List<Usuario>
                notifyDataSetChanged()
            }
        }
    }

    private fun String.unaccent(): String {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return "\\p{InCombiningDiacriticalMarks}+".toRegex().replace(temp, "")
    }
}