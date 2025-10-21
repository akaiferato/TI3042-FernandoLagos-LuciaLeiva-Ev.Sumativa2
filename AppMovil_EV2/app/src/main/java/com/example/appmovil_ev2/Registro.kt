package com.example.appmovil_ev2

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import java.util.regex.Pattern

class Registro : AppCompatActivity() {

    private lateinit var nombre: EditText
    private lateinit var apellido: EditText
    private lateinit var email: EditText
    private lateinit var clave: EditText
    private lateinit var claveconf: EditText
    private lateinit var btn_reg: Button
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nombre = findViewById(R.id.ingreseNombres)
        apellido = findViewById(R.id.ingreseApellidos)
        email = findViewById(R.id.ingreseEmail)
        clave = findViewById(R.id.ingreseClave)
        claveconf = findViewById(R.id.ingreseClaveconf)
        btn_reg = findViewById(R.id.btnRegistrar)
        btnVolver = findViewById(R.id.btnVolver)

        btn_reg.setOnClickListener {
            val nombreText = nombre.text.toString().trim()
            val apellidoText = apellido.text.toString().trim()
            val emailText = email.text.toString().trim()
            val claveText = clave.text.toString().trim()
            val claveconfText = claveconf.text.toString().trim()

            if (nombreText.isEmpty() || apellidoText.isEmpty() || emailText.isEmpty() || claveText.isEmpty() || claveconfText.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Campos obligatorios").setContentText("Todos los campos son requeridos.").show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Formato Inválido").setContentText("Por favor, ingrese un email válido.").show()
                return@setOnClickListener
            }

            if (!validarClave(claveText)) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Contraseña Débil").setContentText("La contraseña debe tener al menos 8 caracteres, 1 mayúscula, 1 minúscula, 1 número y 1 carácter especial.").show()
                return@setOnClickListener
            }

            if (claveText != claveconfText) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Contraseñas no coinciden").setContentText("Las contraseñas ingresadas no son iguales.").show()
                return@setOnClickListener
            }

            val helper = ConexionDbHelper(this)
            val db = helper.writableDatabase

            try {
                val cursor = db.rawQuery("SELECT * FROM USUARIOS WHERE EMAIL = ?", arrayOf(emailText))
                if (cursor.moveToFirst()) {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Email ya registrado").setContentText("La dirección de email ya se encuentra en uso.").show()
                    cursor.close()
                    return@setOnClickListener
                }
                cursor.close()

                val datos = ContentValues().apply {
                    put("Nombre", nombreText)
                    put("Apellido", apellidoText)
                    put("Email", emailText)
                    put("Clave", claveText)
                    put("ESTADO", 1)
                }
                db.insert("USUARIOS", null, datos)

                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("¡Registro Exitoso!")
                    .setContentText("Tu cuenta ha sido creada.")
                    .setConfirmClickListener {
                        val intent = Intent(this, Login::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    .show()

            } catch (e: Exception) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Error de Servidor").setContentText("Ocurrió un error al registrar el usuario: ${e.message}").show()
            } finally {
                db.close()
            }
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun validarClave(password: String): Boolean {
        val passwordPattern = Pattern.compile(
            "^" +
                    "(?=.*[0-9])" +
                    "(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    "(?=.*[@#$%^&+=!?.])" +
                    "(?=\\S+$)" +
                    ".{8,}" +
                    "$"
        )
        return passwordPattern.matcher(password).matches()
    }
}