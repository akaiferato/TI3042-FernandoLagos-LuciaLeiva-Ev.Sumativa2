package com.example.appmovil_ev2

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Listar : AppCompatActivity() {

    private lateinit var lista: ListView
    private lateinit var listausuario: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lista= findViewById(R.id.listaUsuario);
        CargarLista()

        lista.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view,
                                              position, id ->
                val item = listausuario[position]
                val datos = item.split(" ")
                if (datos.size >= 4) {
                    val idusu = datos[0].toIntOrNull() ?: 0
                    val nombre = datos[1]
                    val apellido = datos[2]
                    val email = datos[3]
                    val intent = Intent(this@Listar,
                        modificarEliminar::class.java).apply {
                        putExtra("Id", idusu)
                        putExtra("Nombre", nombre)
                        putExtra("Apellido", apellido)
                        putExtra("Email", email)
                    }
                    startActivity(intent)
                }
            }
    }

    private fun listaUsuario(): ArrayList<String> {
        val datos = ArrayList<String>()
        val helper = ConexionDbHelper(this)
        val db = helper.readableDatabase
        val sql = "SELECT * FROM USUARIOS"
        val c = db.rawQuery(sql, null)
        if (c.moveToFirst()) {
            do {
                val linea = "${c.getInt(0)}" +
                " ${c.getString(1)}" +
                " ${c.getString(2)}" +
                " ${c.getString(3)}"
                datos.add(linea)
            } while (c.moveToNext())
        }
        c.close()
        db.close()
        return datos
    }

    private fun CargarLista() {
        listausuario = listaUsuario()
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_list_item_1,
            listausuario
        )
        lista.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        CargarLista()
    }
}