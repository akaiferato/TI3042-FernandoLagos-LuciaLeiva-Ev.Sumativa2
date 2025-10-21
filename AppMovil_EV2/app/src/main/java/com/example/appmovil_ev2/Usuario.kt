package com.example.appmovil_ev2

import java.io.Serializable

data class Usuario(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val email: String
) : Serializable