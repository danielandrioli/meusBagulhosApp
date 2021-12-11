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
    private val contadorUndone get() = DatabaseUtils.longForQuery(le, "SELECT COUNT (*) FROM ${DbHelper.nomeTabelaTarefas} WHERE" +
            " isFinalizado=?", arrayOf("0")).toInt()
    private val contadorDone get() = DatabaseUtils.longForQuery(le, "SELECT COUNT (*) FROM ${DbHelper.nomeTabelaTarefas} WHERE" +
            " isFinalizado=?", arrayOf("1")).toInt()

    fun criarTarefa(tarefa: Tarefa): Boolean{
        val cv = ContentValues()
        cv.put("texto", tarefa.texto)
        cv.put("dataCriacao", tarefa.dataCriacao)
        cv.put("isFinalizado", false)
        cv.put("positionUndone", contadorUndone)
        cv.put("dataFinalizacao", "")
        cv.put("dataEdicao", "")
        cv.put("positionDone", -1)

        try{
            escreve.insert(DbHelper.nomeTabelaTarefas, null, cv)
            Log.i((tagLogTarefaDAO), "Sucesso ao salvar tarefa!")
            return true
        }catch (e: Exception){
            Log.i(tagLogTarefaDAO, "Erro ao salvar tarefa: $e")
        }
        return false
    }

    fun deletar(tarefa: Tarefa): Boolean{
        removePosicaoDaLista(tarefa)

        try {
            val args = arrayOf(tarefa.id.toString())
            escreve.delete(DbHelper.nomeTabelaTarefas, "id=?", args)
        }catch (e: Exception){
            Log.i(tagLogTarefaDAO, "Erro ao deletar tarefa: $e")
            return false
        }
        return true
    }

    private fun atualizaTarefaNaTabela(tarefa: Tarefa, cv: ContentValues, motivo: String = ""){
        try {
            val args = arrayOf(tarefa.id.toString())
            escreve.update(DbHelper.nomeTabelaTarefas, cv, "id=?", args)

            Log.i(tagLogTarefaDAO, "Sucesso ao atualizar | $motivo")
        }catch (e: Exception){
            Log.i(tagLogTarefaDAO, "Erro ao atualizar | $motivo\nErro: $e")
        }
    }

    fun atualizarTexto(tarefa: Tarefa){ //SÓ PODE ATUALIZAR TEXTO DE TAREFAS UNDONE
        val cv = ContentValues()
        val dataEdicao = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
        cv.put("texto", tarefa.texto)
        cv.put("dataEdicao", dataEdicao)

        atualizaTarefaNaTabela(tarefa, cv, "Atualizar texto tarefa id ${tarefa.id}")
    }

    fun finalizarTarefa(tarefa: Tarefa){
        val cv = ContentValues()
        val dataFinalizacao = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)

        cv.put("dataFinalizacao", dataFinalizacao)
        cv.put("isFinalizado", true)
        cv.put("positionDone", contadorDone)
        cv.put("positionUndone", -1)

        removePosicaoDaLista(tarefa)
        atualizaTarefaNaTabela(tarefa, cv, "Finalizar tarefa id ${tarefa.id}")
    }

    fun desfinalizarTarefa(tarefa: Tarefa){
        val cv = ContentValues()

        cv.put("dataFinalizacao", "")
        cv.put("isFinalizado", false)
        cv.put("positionDone", -1)
        cv.put("positionUndone", contadorUndone)

        removePosicaoDaLista(tarefa)
        atualizaTarefaNaTabela(tarefa, cv, "Desfinalizar tarefa id ${tarefa.id}")
    }

    private fun removePosicaoDaLista(tarefa: Tarefa){
        val lista = listar(tarefa.isFinalizado) as MutableList //recebe a lista done OU undone de acordo com isFinalizado
        lista.remove(tarefa)
        val isListaDone = tarefa.isFinalizado

        for((indice, tarefaItem) in lista.withIndex()){//atualiza cada índice para depois salvar no db
            if (isListaDone) tarefaItem.positionDone = indice
            else tarefaItem.positionUndone = indice

            val cv = ContentValues()
            cv.put("positionUndone", tarefaItem.positionUndone)
            cv.put("positionDone", tarefaItem.positionDone)
            atualizaTarefaNaTabela(tarefaItem, cv, "Atualizar posição, id ${tarefaItem.id}")
        }
    }

    fun trocarPosicao(tarefa: Tarefa, posicaoNova: Int){ //troca de posição é feita apenas na lista undone
        val cv = ContentValues()
        cv.put("positionUndone", posicaoNova)

        atualizaTarefaNaTabela(tarefa, cv, "Troca de posição id ${tarefa.id}, nova posição $posicaoNova")
    }

    /*Método abaixo lista as tarefas prontas ou não prontas de acordo com o valor booleano passado.*/
    fun listar(isDone: Boolean): List<Tarefa>{
        val isFinalizado = if(isDone) 1 else 0
        val position = if(isDone) "positionDone" else "positionUndone"

        val listaTarefa = mutableListOf<Tarefa>()
        val sql = "SELECT * FROM ${DbHelper.nomeTabelaTarefas} WHERE isFinalizado = $isFinalizado ORDER BY $position;"
        val cursor: Cursor = le.rawQuery(sql, null)

        while(cursor.moveToNext()){
            val id: Int = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val texto: String = cursor.getString(cursor.getColumnIndexOrThrow("texto"))
            val dataCriacao: String = cursor.getString(cursor.getColumnIndexOrThrow("dataCriacao"))
            val dataFinalizacao: String = cursor.getString(cursor.getColumnIndexOrThrow("dataFinalizacao"))
            val dataEdicao: String = cursor.getString(cursor.getColumnIndexOrThrow("dataEdicao"))
            val positionDone: Int = cursor.getInt(cursor.getColumnIndexOrThrow("positionDone"))
            val positionUndone: Int = cursor.getInt(cursor.getColumnIndexOrThrow("positionUndone"))


            val tarefa = Tarefa(id, texto, dataCriacao, dataEdicao = dataEdicao, positionDone = positionDone,
                isFinalizado = isDone, dataFinalizacao = dataFinalizacao, positionUndone = positionUndone)
            listaTarefa.add(tarefa)
        }
        return listaTarefa
    }
}