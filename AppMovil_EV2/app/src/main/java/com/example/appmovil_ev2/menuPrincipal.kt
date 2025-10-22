package com.example.appmovil_ev2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog

class menuPrincipal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtcrudusuarios = findViewById<TextView>(R.id.crudUsuario)
        val txtsensores = findViewById<TextView>(R.id.sensores)
        val txtdesarrollo = findViewById<TextView>(R.id.desarrollador)

        txtcrudusuarios.setOnClickListener {
            val intent = Intent(this, crudUsuario::class.java)
            startActivity(intent)
        }

        txtsensores.setOnClickListener {
            val intent = Intent(this, Sensores::class.java)
            startActivity(intent)
        }

        txtdesarrollo.setOnClickListener {
            val intent = Intent(this, Desarrolladores::class.java)
            startActivity(intent)
        }
    }
}