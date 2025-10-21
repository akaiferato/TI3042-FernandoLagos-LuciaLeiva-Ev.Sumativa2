package com.example.appmovil_ev2

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import cn.pedant.SweetAlert.SweetAlertDialog

lateinit var fecha: TextView
lateinit var temp: TextView
lateinit var hum: TextView
lateinit var imagenTemp: ImageView
lateinit var datos: RequestQueue

lateinit var ampolleta: ImageView
lateinit var linterna: ImageView
val mHandler = Handler(Looper.getMainLooper())

class sensores : AppCompatActivity() {

    private var ampolletaEncendida = false
    private var linternaEncendida = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sensores)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fecha=findViewById(R.id.fechaHora)
        temp=findViewById(R.id.temperatura)
        hum=findViewById(R.id.humedad)
        imagenTemp=findViewById(R.id.imgTemperatura)
        ampolleta = findViewById(R.id.imgAmpolleta)
        linterna = findViewById(R.id.imgLinterna)
        datos = Volley.newRequestQueue(this)
        mHandler.post(refrescar)
        ampolleta.setOnClickListener { alternarAmpolleta() }
        linterna.setOnClickListener { alternarLinterna() }

    }

    fun fechahora(): String {
        val c: Calendar = Calendar.getInstance()
        val sdf: SimpleDateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm:ss a")
        val strDate: String = sdf.format(c.time)
        return strDate
    }

    private fun obtenerDatos() {
        val url = "https://www.pnk.cl/muestra_datos.php"

        val request = JsonObjectRequest(
            Request.Method.GET,url, null,
            { response: JSONObject ->
                try {
                    val temperatura = response.getString("temperatura")
                    temp?.text = "$temperatura C"
                    hum?.text = "${response.getString("humedad")} %"

                    val valor = temperatura.toFloat()
                    cambiarImagen(valor)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error: VolleyError ->
                error.printStackTrace()
            }
        )
        datos.add(request)
    }

    private fun cambiarImagen(valor: Float) {
        if (valor >= 20) {
            imagenTemp.setImageResource(R.drawable.tempalta)
        } else {
            imagenTemp.setImageResource(R.drawable.tempbaja)
        }
    }

    private val refrescar = object : Runnable {
        override fun run() {
            fecha?.text = fechahora()
            obtenerDatos()
            mHandler.postDelayed(this, 1000)
        }
    }

    private fun alternarAmpolleta() {
        ampolletaEncendida = !ampolletaEncendida
        val mensaje = if (ampolletaEncendida) "Ampolleta encendida" else "Ampolleta apagada"

        val icono = if (ampolletaEncendida) R.drawable.ampolletaprendida else R.drawable.ampolletaapagada

        ampolleta.setImageResource(icono)

        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Estado")
            .setContentText(mensaje)
            .setConfirmText("OK")
            .show()
    }

    private fun alternarLinterna() {
        val cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = cameraManager.cameraIdList[0]
            linternaEncendida = !linternaEncendida
            cameraManager.setTorchMode(cameraId, linternaEncendida)

            val icono = if (linternaEncendida) R.drawable.linternaprendida else R.drawable.linternaapagada
            linterna.setImageResource(icono)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}