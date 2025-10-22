package com.example.appmovil_ev2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class modificarEliminar : AppCompatActivity() {

    lateinit var id: EditText
    lateinit var nom: EditText
    lateinit var ape: EditText
    lateinit var ema: EditText
    lateinit var mod: Button
    lateinit var elim: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_modificar_eliminar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        id = findViewById(R.id.inputID)
        nom = findViewById(R.id.inputNombre)
        ape = findViewById(R.id.inputApellidos)
        ema = findViewById(R.id.inputEmail)
        mod = findViewById(R.id.btnModificar)
        elim = findViewById(R.id.btnEliminar)

        val idusu = intent.getIntExtra("Id", 0)
        val nombre = intent.getStringExtra("Nombre") ?: ""
        val apellido = intent.getStringExtra("Apellido") ?: ""
        val email = intent.getStringExtra("Email") ?: ""

        id.setText(idusu.toString())
        nom.setText(nombre)
        ape.setText(apellido)
        ema.setText(email)

        mod.setOnClickListener {
            modificar(idusu,nom.text.toString(), ape.text.toString(),
                ema.text.toString())
            onBackPressedDispatcher.onBackPressed()
        }

        elim.setOnClickListener {
            eliminar(idusu)
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun modificar(id: Int, nombre: String,
                          apellido: String, email: String) {
        val helper = ConexionDbHelper(this)
        val db = helper.writableDatabase
        val sql = "UPDATE USUARIOS SET NOMBRE='$nombre', APELLIDO='$apellido', EMAIL='$email' WHERE ID=$id"
        db.execSQL(sql)
        db.close()
    }

    private fun eliminar(id: Int) {
        val helper = ConexionDbHelper(this)
        val db = helper.writableDatabase
        val sql = "DELETE FROM USUARIOS WHERE ID=$id"
        db.execSQL(sql)
        db.close()
    }
}