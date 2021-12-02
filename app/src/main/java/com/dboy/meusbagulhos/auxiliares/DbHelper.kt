package com.dboy.meusbagulhos.auxiliares

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import java.lang.Exception


class DbHelper(context: Context) : SQLiteOpenHelper(context, nomeDB, null, versao) {

    override fun onCreate(p0: SQLiteDatabase?) {
        val sql = "CREATE TABLE IF NOT EXISTS $nomeTabelaTarefas" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " texto TEXT NOT NULL," +
                " dataCriacao VARCHAR NOT NULL," +
                " dataEdicao VARCHAR," +
                " dataFinalizacao VARCHAR," +
                " isFinalizado BOOLEAN NOT NULL," +
                " positionUndone INT," +
                " positionDone INT)"

        try{
            if (p0 != null){
                p0.execSQL(sql)
                Log.i(tagLogDbHelper, "Comando de criação executado com sucesso!")
            } else Log.i(tagLogDbHelper, "db null")
        }catch (exception: Exception){
            Log.i(tagLogDbHelper, "Erro ao criar tabela: $exception")
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    companion object{
        var versao = 1
        val nomeDB = "DB_TAREFAS"
        val nomeTabelaTarefas = "Tarefas"
    }
}