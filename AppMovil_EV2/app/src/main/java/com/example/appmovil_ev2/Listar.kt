package com.example.appmovil_ev2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Listar : AppCompatActivity() {

    private lateinit var rvUsuarios: RecyclerView
    private lateinit var svSearchUsuarios: SearchView
    private lateinit var btnVolver: Button
    private lateinit var adapter: usuarioAdapter
    private val listaUsuarios = mutableListOf<Usuario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar)

        rvUsuarios = findViewById(R.id.rvUsuarios)
        svSearchUsuarios = findViewById(R.id.svUsuarios)
        rvUsuarios.layoutManager = LinearLayoutManager(this)
        adapter = usuarioAdapter(listaUsuarios) { usuario ->
            val intent = Intent(this, ModificarUsuario::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }
        rvUsuarios.adapter = adapter

        svSearchUsuarios.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        btnVolver.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarUsuarios()
    }

    private fun cargarUsuarios() {
        val helper = ConexionDbHelper(this)
        val db = helper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM USUARIOS", null)

        listaUsuarios.clear()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
                // Se corrige el nombre de las columnas a mayúsculas para que coincida con la BD
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("NOMBRE"))
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow("APELLIDO"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("EMAIL"))
                listaUsuarios.add(Usuario(id, nombre, apellido, email))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        // Se notifica al adapter en el hilo principal para evitar problemas
        runOnUiThread {
            adapter.notifyDataSetChanged()
        }
    }
}