package com.example.appmovil_ev2

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import java.util.regex.Pattern

class ModificarUsuario : AppCompatActivity() {

    private lateinit var modNombre: EditText
    private lateinit var modApellido: EditText
    private lateinit var modEmail: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnEliminar: Button
    private var usuario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar_usuario)

        modNombre = findViewById(R.id.modificarNombres)
        modApellido = findViewById(R.id.modificarApellidos)
        modEmail = findViewById(R.id.modificarEmail)
        btnGuardar = findViewById(R.id.btnModificar)
        btnEliminar = findViewById(R.id.btnEliminar)

        usuario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("usuario", Usuario::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("usuario") as? Usuario
        }

        if (usuario == null) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText("No se pudieron cargar los datos del usuario.")
                .setConfirmClickListener { finish() }
                .show()
            return
        }

        modNombre.setText(usuario!!.nombre)
        modApellido.setText(usuario!!.apellido)
        modEmail.setText(usuario!!.email)

        btnGuardar.setOnClickListener { guardarCambios() }
        btnEliminar.setOnClickListener { confirmarEliminacion() }
    }

    private fun guardarCambios() {
        val nombre = modNombre.text.toString().trim()
        val apellido = modApellido.text.toString().trim()
        val email = modEmail.text.toString().trim()

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty()) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Campos vacíos").setContentText("Todos los campos son obligatorios.").show()
            return
        }

        val nombrePattern = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")
        if (!nombrePattern.matcher(nombre).matches() || !nombrePattern.matcher(apellido).matches()) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Formato incorrecto").setContentText("Nombres y apellidos solo deben contener letras y espacios.").show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Formato inválido").setContentText("Por favor, ingrese un email válido.").show()
            return
        }

        val helper = ConexionDbHelper(this)
        val db = helper.writableDatabase

        try {
            val cursor = db.rawQuery("SELECT * FROM USUARIOS WHERE EMAIL = ? AND ID != ?", arrayOf(email, usuario!!.id.toString()))
            if (cursor.moveToFirst()) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Email duplicado").setContentText("El email ingresado ya pertenece a otro usuario.").show()
                cursor.close()
                return
            }
            cursor.close()

            val values = ContentValues().apply {
                put("Nombre", nombre)
                put("Apellido", apellido)
                put("Email", email)
            }

            db.update("USUARIOS", values, "ID = ?", arrayOf(usuario!!.id.toString()))
            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("¡Éxito!")
                .setContentText("Usuario actualizado correctamente.")
                .setConfirmClickListener { finish() }
                .show()

        } catch (e: Exception) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Error de Servidor").setContentText(e.message).show()
        } finally {
            db.close()
        }
    }

    private fun confirmarEliminacion() {
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("¿Estás seguro?")
            .setContentText("Esta acción no se puede deshacer.")
            .setConfirmText("Sí, eliminar")
            .setCancelText("Cancelar")
            .setConfirmClickListener { sDialog ->
                eliminarUsuario()
                sDialog.dismissWithAnimation()
            }
            .show()
    }

    private fun eliminarUsuario() {
        val helper = ConexionDbHelper(this)
        val db = helper.writableDatabase
        try {
            db.delete("USUARIOS", "ID = ?", arrayOf(usuario!!.id.toString()))
            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Eliminado")
                .setContentText("El usuario ha sido eliminado.")
                .setConfirmClickListener { finish() }
                .show()
        } catch (e: Exception) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Error de Servidor").setContentText(e.message).show()
        } finally {
            db.close()
        }
    }
}