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

    lateinit var nombre: EditText
    lateinit var apellido: EditText
    lateinit var email: EditText
    lateinit var clave: EditText
    lateinit var claveconf: EditText
    lateinit var btnreg: Button

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
        btnreg = findViewById(R.id.btnRegistrar)

        btnreg.setOnClickListener {
            val nombreText = nombre.text.toString().trim()
            val apellidoText = apellido.text.toString().trim()
            val emailText = email.text.toString().trim()
            val claveText = clave.text.toString().trim()
            val claveconfText = claveconf.text.toString().trim()

            if (nombreText.isEmpty() || apellidoText.isEmpty() || emailText.isEmpty() || claveText.isEmpty() || claveconfText.isEmpty()) {
                SweetAlertDialog(
                    this,
                    SweetAlertDialog.ERROR_TYPE
                ).setTitleText("Campos obligatorios")
                    .setContentText("Todos los campos son requeridos.").show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Formato Inválido")
                    .setContentText("Por favor, ingrese un email válido.").show()
                return@setOnClickListener
            }

            if (!validarClave(claveText)) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Contraseña Débil")
                    .setContentText("La contraseña debe tener al menos 8 caracteres, 1 mayúscula, 1 minúscula, 1 número y 1 carácter especial.")
                    .show()
                return@setOnClickListener
            }

            if (claveText != claveconfText) {
                SweetAlertDialog(
                    this,
                    SweetAlertDialog.ERROR_TYPE
                ).setTitleText("Contraseñas no coinciden")
                    .setContentText("Las contraseñas ingresadas no son iguales.").show()
                return@setOnClickListener
            }

            guardar(nombreText, apellidoText, emailText, claveText)
        }
    }

    fun guardar(nom: String, ape: String, mai: String, cla: String) {
        val helper = ConexionDbHelper(this)
        val db = helper.writableDatabase
        try {
            val datos = ContentValues().apply {
                put("Nombre", nom)
                put("Apellido", ape)
                put("Email", mai)
                put("Clave", cla)
            }

            // Ejecuta la inserción y verifica el resultado
            val resultado = db.insert("USUARIOS", null, datos)

            if (resultado > 0) {
                // ÉXITO: El registro se realizó correctamente
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("¡Registro Exitoso!")
                    .setContentText("Tu cuenta ha sido creada.")
                    .setConfirmText("Cerrar")
                    .show()
            } else {
                // FALLO: La inserción devolvió -1 (Error en BD)
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Registro Fallido")
                    .setContentText("La base de datos rechazó la inserción.")
                    .setConfirmText("Cerrar")
                    .setConfirmClickListener { dialog ->
                        dialog.dismissWithAnimation()
                    }
                    .show()
            }

        } catch (e: Exception) {
            // ERROR: Atrapa cualquier excepción de SQL o de otro tipo
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error Inesperado")
                .setContentText("Ha ocurrido un error. Detalle: ${e.message}") // Usa e.message para un mensaje más limpio
                .setConfirmText("Cerrar")
                .setConfirmClickListener { dialog ->
                    dialog.dismissWithAnimation()
                }
                .show()
        } finally {
            db.close()
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