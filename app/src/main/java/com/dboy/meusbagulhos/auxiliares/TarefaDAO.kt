package com.dboy.meusbagulhos.auxiliares

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.dboy.meusbagulhos.models.Tarefa
import java.lang.Exception
import java.text.DateFormat
import java.util.*

class TarefaDAO(context: Context) {
    private val dbHelper: DbHelper = DbHelper(context)
    private val escreve: SQLiteDatabase = dbHelper.writableDatabase
    private val le: SQLiteDatabase = dbHelper.readableDatabase

    fun criarTarefa(tarefa: Tarefa): Boolean{
        val cv = ContentValues()
        cv.put("texto", tarefa.texto)
        cv.put("dataCriacao", tarefa.dataCriacao)
        cv.put("isFinalizado", false)
        //da pra colocar o contadorUndone e done como property e manipular seu get()
        val contadorUndone = DatabaseUtils.longForQuery(le, "SELECT COUNT (*) FROM ${DbHelper.nomeTabelaTarefas} WHERE" +
                " isFinalizado=?", arrayOf("0")).toInt()
        cv.put("positionUndone", contadorUndone + 1)

        try{
            escreve.insert(DbHelper.nomeTabelaTarefas, null, cv)
            Log.i((tagLogTarefaDAO), "Sucesso ao salvar tarefa!")
            return true
        }catch (e: Exception){
            Log.i(tagLogTarefaDAO, "Erro ao salvar tarefa: $e")
        }
        return false
    }

    fun atualizarTexto(tarefa: Tarefa): Boolean{ //SÓ PODE ATUALIZAR TEXTO DE TAREFAS UNDONE
        val cv = ContentValues()
        val dataEdicao = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
        cv.put("texto", tarefa.texto)
        cv.put("dataEdicao", dataEdicao)

        try {
            val args = arrayOf(tarefa.id.toString())
            escreve.update(DbHelper.nomeTabelaTarefas, cv, "id=?", args)
        }catch (e: Exception){
            Log.i(tagLogTarefaDAO, "Erro ao atualizar tarefa: $e")
            return false
        }
        return true
    }

    fun deletar(tarefa: Tarefa): Boolean{
        try {
            val args = arrayOf(tarefa.id.toString())
            escreve.delete(DbHelper.nomeTabelaTarefas, "id=?", args)
        }catch (e: Exception){
            Log.i(tagLogTarefaDAO, "Erro ao deletar tarefa: $e")
            return false
        }
        return true
    }

    fun finalizarTarefa(tarefa: Tarefa): Boolean{
        val cv = ContentValues()
        val dataFinalizacao = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
        val contadorDone = DatabaseUtils.longForQuery(le, "SELECT COUNT (*) FROM ${DbHelper.nomeTabelaTarefas} WHERE" +
                " isFinalizado=?", arrayOf("1")).toInt()

        cv.put("texto", tarefa.texto)//nem precisa...
        cv.put("dataFinalizacao", dataFinalizacao)
        cv.put("isFinalizado", true)
        cv.put("positionDone", contadorDone + 1)
        cv.put("positionUndone", -1)
//        val mContador = le.rawQuery("SELECT COUNT(id) FROM ${DbHelper.nomeTabelaTarefas} WHERE isFinalizado = 1", null)
//        val totalFinalizados = mContador.count
        Log.i(tagLogTarefaDAO, "Resultado do count done: $contadorDone")

        try {
            val args = arrayOf(tarefa.id.toString())
            escreve.update(DbHelper.nomeTabelaTarefas, cv, "id=?", args)
            Log.i(tagLogTarefaDAO, "Tarefa de id ${tarefa.id} finalizada!")
        }catch (e: Exception){
            Log.i(tagLogTarefaDAO, "Erro ao atualizar finalização da tarefa: $e")
            return false
        }
        return true
    }

    fun desfinalizarTarefa(tarefa: Tarefa): Boolean{
        val cv = ContentValues()
        val contadorUndone = DatabaseUtils.longForQuery(le, "SELECT COUNT (*) FROM ${DbHelper.nomeTabelaTarefas} WHERE" +
                " isFinalizado=?", arrayOf("0")).toInt()

        cv.put("dataFinalizacao", "")
        cv.put("isFinalizado", false)
        cv.put("positionDone", -1)
        cv.put("positionUndone", contadorUndone + 1)

        Log.i(tagLogTarefaDAO, "Resultado do count undone: $contadorUndone")

        try {
            val args = arrayOf(tarefa.id.toString())
            escreve.update(DbHelper.nomeTabelaTarefas, cv, "id=?", args)
            Log.i(tagLogTarefaDAO, "Tarefa de id ${tarefa.id} desfinalizada!")
        }catch (e: Exception){
            Log.i(tagLogTarefaDAO, "Erro ao atualizar desfinalização da tarefa: $e")
            return false
        }
        return true
    }

//DÁ PARA ARRUMAR BEM ESSE CÓDIGO DESSA CLASSE TAREFADAO. LISTAR DONE E UNDONE PODEM TER UM METODO EM COMUM, ASSIM COMO FINALIZAR E DESFINALIZAR

    fun listarUndone(): List<Tarefa>{
        Log.i(tagLogTarefaDAO, "Listando undone")
        val listaTarefa = mutableListOf<Tarefa>()
        val sql = "SELECT * FROM ${DbHelper.nomeTabelaTarefas} WHERE isFinalizado = 0 ORDER BY positionUndone;"
        val cursor: Cursor = le.rawQuery(sql, null)

        while(cursor.moveToNext()){
            val id: Int = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val texto: String = cursor.getString(cursor.getColumnIndexOrThrow("texto"))
            val dataCriacao: String = cursor.getString(cursor.getColumnIndexOrThrow("dataCriacao"))
            val dataEdicao: String? = cursor.getString(cursor.getColumnIndexOrThrow("dataEdicao"))
            val positionUndone: Int = cursor.getInt(cursor.getColumnIndexOrThrow("positionUndone"))

            val tarefa = Tarefa(id, texto, dataCriacao, dataEdicao = dataEdicao, positionUndone = positionUndone)
            listaTarefa.add(tarefa)
        }
        Log.i(tagLogTarefaDAO, "Lista tarefa undone: ${listaTarefa.reversed()}")
        return listaTarefa.reversed()
    }

    fun listarDone(): List<Tarefa>{
        Log.i(tagLogTarefaDAO, "Listando done")
        val listaTarefa = mutableListOf<Tarefa>()
        val sql = "SELECT * FROM ${DbHelper.nomeTabelaTarefas} WHERE isFinalizado = 1 ORDER BY positionDone;"
        val cursor: Cursor = le.rawQuery(sql, null)

        while(cursor.moveToNext()){
            val id: Int = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val texto: String = cursor.getString(cursor.getColumnIndexOrThrow("texto"))
            val dataCriacao: String = cursor.getString(cursor.getColumnIndexOrThrow("dataCriacao"))
            val dataFinalizacao: String = cursor.getString(cursor.getColumnIndexOrThrow("dataFinalizacao"))
            val dataEdicao: String? = cursor.getString(cursor.getColumnIndexOrThrow("dataEdicao"))
            val positionDone: Int = cursor.getInt(cursor.getColumnIndexOrThrow("positionDone"))

            val tarefa = Tarefa(id, texto, dataCriacao, dataEdicao = dataEdicao, positionDone = positionDone,
            isFinalizado = true, dataFinalizacao = dataFinalizacao)
            listaTarefa.add(tarefa)
        }
        Log.i(tagLogTarefaDAO, "Lista tarefa done: ${listaTarefa.reversed()}")
        return listaTarefa.reversed()
    }

    //fun atualizaPosicaoListaUndone()
    //private fun listarQtdadeUndone()
}