package com.dboy.meusbagulhos.helpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class DbHelper(context: Context) : SQLiteOpenHelper(context, nameDB, null, version) {

    override fun onCreate(p0: SQLiteDatabase?) {
        val sql = "CREATE TABLE IF NOT EXISTS $nameTaskTable" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " texto TEXT NOT NULL," +
                " dataCriacao VARCHAR NOT NULL," +
                " dataEdicao VARCHAR," +
                " dataFinalizacao VARCHAR," +
                " isFinalizado BOOLEAN NOT NULL," +
                " positionUndone INT," +
                " positionDone INT)"

        try {
            if (p0 != null) {
                p0.execSQL(sql)
                Log.i(tagLogDbHelper, "Comando de criação executado com sucesso!")
            } else Log.i(tagLogDbHelper, "db null")
        } catch (exception: Exception) {
            Log.i(tagLogDbHelper, "Erro ao criar tabela: $exception")
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    companion object {
        var version = 1
        val nameDB = "DB_TAREFAS"
        val nameTaskTable = "Tarefas"
    }
}