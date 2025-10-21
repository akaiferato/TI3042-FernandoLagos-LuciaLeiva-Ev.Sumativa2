package com.example.appmovil_ev2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


lateinit var emailLogin: EditText
lateinit var claveLogin: EditText
lateinit var btnIngresar: Button
lateinit var registro: TextView
lateinit var olvide: TextView

val lHandler = Handler(Looper.getMainLooper())

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        emailLogin = findViewById(R.id.usuarioLogin)
        claveLogin = findViewById(R.id.claveLogin)
        btnIngresar = findViewById(R.id.btnIngreso)
        registro = findViewById(R.id.loginRegistrar)
        olvide = findViewById(R.id.loginOlvidaste)

        emailLogin.text.clear()
        claveLogin.text.clear()

        btnIngresar.setOnClickListener {
            val email = emailLogin.text.toString().trim()
            val clave = claveLogin.text.toString().trim()

            if (email.isEmpty() || clave.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Campos obligatorios")
                    .setContentText("Por favor, ingrese su email y contraseña.")
                    .show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Formato Inválido")
                    .setContentText("Por favor, ingrese un email con formato válido.")
                    .show()
                return@setOnClickListener
            }

            val helper = ConexionDbHelper(this)
            val db = helper.readableDatabase
            val cursor = db.rawQuery(
                "SELECT * FROM USUARIOS WHERE EMAIL = ? AND CLAVE = ?",
                arrayOf(email, clave)
            )
            if (cursor.moveToFirst()) {
                cursor.close()
                db.close()
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Credenciales Correctas!")
                    .setContentText("Inicio de sesión exitoso.")
                    .setConfirmText("Continuar")
                    .setConfirmClickListener {
                        val intent = Intent(this, principal::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .show()
            } else {
                cursor.close()
                db.close()
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Credenciales Incorrectas")
                    .setContentText("Por favor, verifique su email y contraseña.")
                    .show()
            }
        }

        registro.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }

        olvide.setOnClickListener {
            val intent = Intent(this, recuperarContrasena::class.java)
            startActivity(intent)
        }
    }
}