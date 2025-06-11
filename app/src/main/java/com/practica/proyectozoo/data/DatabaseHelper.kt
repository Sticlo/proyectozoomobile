package com.practica.proyectozoo.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.practica.proyectozoo.data.Usuario
import com.practica.proyectozoo.data.Especie
import com.practica.proyectozoo.data.Zoo


class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE paises (
                id_pais INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre VARCHAR(100) NOT NULL UNIQUE
            );
        """)
        db.execSQL("""
            CREATE TABLE ciudades (
                id_ciudad INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre VARCHAR(100) NOT NULL,
                id_pais INTEGER NOT NULL REFERENCES paises(id_pais) ON UPDATE CASCADE ON DELETE RESTRICT,
                UNIQUE(nombre, id_pais)
            );
        """)
        db.execSQL("""
            CREATE TABLE zoos (
                id_zoo INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre VARCHAR(100) NOT NULL,
                id_ciudad INTEGER NOT NULL REFERENCES ciudades(id_ciudad) ON UPDATE CASCADE ON DELETE RESTRICT,
                tamano_m2 INTEGER,
                presupuesto_anual NUMERIC(12,2)
            );
        """)
        db.execSQL("""
            CREATE TABLE especies (
                id_especie INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_vulgar VARCHAR(100) NOT NULL,
                nombre_cientifico VARCHAR(150) NOT NULL UNIQUE,
                familia VARCHAR(100),
                en_peligro_extincion BOOLEAN DEFAULT FALSE
            );
        """)
        db.execSQL("""
            CREATE TABLE animales (
                id_animal INTEGER PRIMARY KEY AUTOINCREMENT,
                id_zoo INTEGER NOT NULL REFERENCES zoos(id_zoo) ON UPDATE CASCADE ON DELETE CASCADE,
                id_especie INTEGER NOT NULL REFERENCES especies(id_especie) ON UPDATE CASCADE ON DELETE RESTRICT,
                sexo CHAR(1) NOT NULL CHECK (sexo IN ('M','F')),
                anio_nacimiento INTEGER,
                pais_origen VARCHAR(100),
                continente VARCHAR(50)
            );
        """)
        db.execSQL("""
            CREATE TABLE perfiles (
                id_perfil INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_perfil VARCHAR(50) NOT NULL UNIQUE
            );
        """)
        db.execSQL("""
            CREATE TABLE usuarios (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                username VARCHAR(50) NOT NULL UNIQUE,
                email VARCHAR(100) NOT NULL UNIQUE,
                password_hash VARCHAR(255) NOT NULL,
                id_perfil INTEGER NOT NULL REFERENCES perfiles(id_perfil) ON UPDATE CASCADE ON DELETE RESTRICT,
                fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """)
        // Datos iniciales para prueba
        db.execSQL("INSERT INTO perfiles(nombre_perfil) VALUES('admin');")
        db.execSQL("INSERT INTO usuarios(username,email,password_hash,id_perfil) VALUES('admin','admin@example.com','admin',1);")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS perfiles")
        db.execSQL("DROP TABLE IF EXISTS animales")
        db.execSQL("DROP TABLE IF EXISTS especies")
        db.execSQL("DROP TABLE IF EXISTS zoos")
        db.execSQL("DROP TABLE IF EXISTS ciudades")
        db.execSQL("DROP TABLE IF EXISTS paises")
        onCreate(db)
    }

    fun validateUser(username: String, password: String): Boolean {
        readableDatabase.rawQuery(
            "SELECT id_usuario FROM usuarios WHERE username=? AND password_hash=?",
            arrayOf(username, password)
        ).use { cursor ->
            return cursor.moveToFirst()
        }
    }

    fun getUsuarios(): List<String> {
        val list = mutableListOf<String>()
        readableDatabase.rawQuery("SELECT username FROM usuarios", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursor.getString(0))
            }
        }
        return list
    }

    fun getZoos(): List<String> {
        val list = mutableListOf<String>()
        readableDatabase.rawQuery("SELECT nombre FROM zoos", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursor.getString(0))
            }
        }
        return list
    }

    fun getEspecies(): List<String> {
        val list = mutableListOf<String>()
        readableDatabase.rawQuery("SELECT nombre_vulgar FROM especies", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursor.getString(0))
            }
        }
        return list
    }

    fun getAnimales(): List<String> {
        val list = mutableListOf<String>()
        readableDatabase.rawQuery("SELECT id_animal || '-' || sexo FROM animales", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursor.getString(0))
            }
        }
        return list
    }
    fun getPasswordByEmail(email: String): String? {
        readableDatabase.rawQuery(
            "SELECT password_hash FROM usuarios WHERE email=?",
            arrayOf(email)
        ).use { cursor ->
            return if (cursor.moveToFirst()) cursor.getString(0) else null
        }
    }

    fun updatePassword(username: String, newPassword: String) {
        writableDatabase.execSQL(
            "UPDATE usuarios SET password_hash=? WHERE username=?",
            arrayOf(newPassword, username)
        )
    }

    fun insertUsuario(username: String, email: String, password: String, perfilId: Int) {
        writableDatabase.execSQL(
            "INSERT INTO usuarios(username,email,password_hash,id_perfil) VALUES(?,?,?,?)",
            arrayOf(username, email, password, perfilId)
        )
    }

    fun getAllUsuariosDetail(): List<Usuario> {
        val list = mutableListOf<Usuario>()
        readableDatabase.rawQuery(
            "SELECT id_usuario, username, email, password_hash, id_perfil FROM usuarios",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(
                    Usuario(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4)
                    )
                )
            }
        }
        return list
    }

    fun getUsuario(id: Int): Usuario? {
        readableDatabase.rawQuery(
            "SELECT id_usuario, username, email, password_hash, id_perfil FROM usuarios WHERE id_usuario=?",
            arrayOf(id.toString())
        ).use { c ->
            return if (c.moveToFirst()) {
                Usuario(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4))
            } else null
        }
    }

    fun updateUsuario(id: Int, username: String, email: String, password: String, perfilId: Int) {
        writableDatabase.execSQL(
            "UPDATE usuarios SET username=?, email=?, password_hash=?, id_perfil=? WHERE id_usuario=?",
            arrayOf(username, email, password, perfilId, id)
        )
    }

    fun deleteUsuario(id: Int) {
        writableDatabase.execSQL(
            "DELETE FROM usuarios WHERE id_usuario=?",
            arrayOf(id)
        )
    }

    fun insertEspecie(nombreVulgar: String, nombreCientifico: String, familia: String?, enPeligro: Boolean) {
        writableDatabase.execSQL(
            "INSERT INTO especies(nombre_vulgar,nombre_cientifico,familia,en_peligro_extincion) VALUES(?,?,?,?)",
            arrayOf(nombreVulgar, nombreCientifico, familia, if (enPeligro) 1 else 0)
        )
    }

    fun getAllEspeciesDetail(): List<Especie> {
        val list = mutableListOf<Especie>()
        readableDatabase.rawQuery(
            "SELECT id_especie, nombre_vulgar, nombre_cientifico, familia, en_peligro_extincion FROM especies",
            null
        ).use { c ->
            while (c.moveToNext()) {
                list.add(
                    Especie(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getInt(4) == 1
                    )
                )
            }
        }
        return list
    }

    fun getEspecie(id: Int): Especie? {
        readableDatabase.rawQuery(
            "SELECT id_especie, nombre_vulgar, nombre_cientifico, familia, en_peligro_extincion FROM especies WHERE id_especie=?",
            arrayOf(id.toString())
        ).use { c ->
            return if (c.moveToFirst()) {
                Especie(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4) == 1)
            } else null
        }
    }

    fun updateEspecie(id: Int, nombreVulgar: String, nombreCientifico: String, familia: String?, enPeligro: Boolean) {
        writableDatabase.execSQL(
            "UPDATE especies SET nombre_vulgar=?, nombre_cientifico=?, familia=?, en_peligro_extincion=? WHERE id_especie=?",
            arrayOf(nombreVulgar, nombreCientifico, familia, if (enPeligro) 1 else 0, id)
        )
    }

    fun deleteEspecie(id: Int) {
        writableDatabase.execSQL(
            "DELETE FROM especies WHERE id_especie=?",
            arrayOf(id)
        )
    }

    fun insertZoo(nombre: String, ciudadId: Int, tamano: Int?, presupuesto: Double?) {
        writableDatabase.execSQL(
            "INSERT INTO zoos(nombre,id_ciudad,tamano_m2,presupuesto_anual) VALUES(?,?,?,?)",
            arrayOf(nombre, ciudadId, tamano, presupuesto)
        )
    }

    fun getAllZoosDetail(): List<Zoo> {
        val list = mutableListOf<Zoo>()
        readableDatabase.rawQuery(
            "SELECT id_zoo, nombre, id_ciudad, tamano_m2, presupuesto_anual FROM zoos",
            null
        ).use { c ->
            while (c.moveToNext()) {
                val tam = if (c.isNull(3)) null else c.getInt(3)
                val pre = if (c.isNull(4)) null else c.getDouble(4)
                list.add(
                    Zoo(
                        c.getInt(0),
                        c.getString(1),
                        c.getInt(2),
                        tam,
                        pre
                    )
                )
            }
        }
        return list
    }

    fun getZoo(id: Int): Zoo? {
        readableDatabase.rawQuery(
            "SELECT id_zoo, nombre, id_ciudad, tamano_m2, presupuesto_anual FROM zoos WHERE id_zoo=?",
            arrayOf(id.toString())
        ).use { c ->
            return if (c.moveToFirst()) {
                val tam = if (c.isNull(3)) null else c.getInt(3)
                val pre = if (c.isNull(4)) null else c.getDouble(4)
                Zoo(
                    c.getInt(0),
                    c.getString(1),
                    c.getInt(2),
                    tam,
                    pre
                )
            } else null
        }
    }

    fun updateZoo(id: Int, nombre: String, ciudadId: Int, tamano: Int?, presupuesto: Double?) {
        writableDatabase.execSQL(
            "UPDATE zoos SET nombre=?, id_ciudad=?, tamano_m2=?, presupuesto_anual=? WHERE id_zoo=?",
            arrayOf(nombre, ciudadId, tamano, presupuesto, id)
        )
    }

    fun deleteZoo(id: Int) {
        writableDatabase.execSQL(
            "DELETE FROM zoos WHERE id_zoo=?",
            arrayOf(id)
        )
    }
    companion object {
        private const val DATABASE_NAME = "zoo_db.db"
        private const val DATABASE_VERSION = 1
    }
}
