package com.example.appmovil_ev2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ConexionDbHelper(
    context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Se añade la restricción UNIQUE a la columna EMAIL
    val sql = "CREATE TABLE USUARIOS (ID INTEGER PRIMARY KEY, NOMBRE TEXT, APELLIDO TEXT, EMAIL TEXT UNIQUE, CLAVE TEXT)"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE USUARIOS")
            onCreate(db)
        }
    }

    companion object {
        private const val DATABASE_NAME = "CRUD"
        private const val DATABASE_VERSION = 1
    }
}