package com.example.appmovil_ev2

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.widget.Button

class Desarrolladores : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_desarrolladores)


        val githubFernanflo: TextView = findViewById(R.id.githubFernanflo)
        val githubLucia: TextView = findViewById(R.id.githubLucia)

        githubFernanflo.setOnClickListener {
            val url = "https://github.com/FelipeDev02"
            abrirUrl(url)
        }

        githubLucia.setOnClickListener {
            val url = "https://github.com/Ditoplz"
            abrirUrl(url)
        }
    }

    private fun abrirUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}