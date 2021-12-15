package com.dboy.meusbagulhos.helpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.dboy.meusbagulhos.models.Task
import java.text.DateFormat
import java.util.*

class TaskDAO(context: Context) {
    private val dbHelper: DbHelper = DbHelper(context)
    private val write: SQLiteDatabase = dbHelper.writableDatabase
    private val read: SQLiteDatabase = dbHelper.readableDatabase
    private val countUndone
        get() = DatabaseUtils.longForQuery(
            read, "SELECT COUNT (*) FROM ${DbHelper.nameTaskTable} WHERE" +
                    " isFinalizado=?", arrayOf("0")
        ).toInt()
    private val countDone
        get() = DatabaseUtils.longForQuery(
            read, "SELECT COUNT (*) FROM ${DbHelper.nameTaskTable} WHERE" +
                    " isFinalizado=?", arrayOf("1")
        ).toInt()

    fun createTask(task: Task): Boolean {
        val cv = ContentValues()
        cv.put("texto", task.text)
        cv.put("dataCriacao", task.dateCreation)
        cv.put("isFinalizado", false)
        cv.put("positionUndone", countUndone)
        cv.put("dataFinalizacao", "")
        cv.put("dataEdicao", "")
        cv.put("positionDone", -1)

        try {
            write.insert(DbHelper.nameTaskTable, null, cv)
            Log.i((tagLogTaskDAO), "Sucesso ao salvar tarefa!")
            return true
        } catch (e: Exception) {
            Log.i(tagLogTaskDAO, "Erro ao salvar tarefa: $e")
        }
        return false
    }

    fun delete(task: Task): Boolean {
        removePositionFromList(task)

        try {
            val args = arrayOf(task.id.toString())
            write.delete(DbHelper.nameTaskTable, "id=?", args)
        } catch (e: Exception) {
            Log.i(tagLogTaskDAO, "Erro ao deletar tarefa: $e")
            return false
        }
        return true
    }

    private fun updateTaskInTable(task: Task, cv: ContentValues, motivo: String = "") {
        try {
            val args = arrayOf(task.id.toString())
            write.update(DbHelper.nameTaskTable, cv, "id=?", args)

            Log.i(tagLogTaskDAO, "Sucesso ao atualizar | $motivo")
        } catch (e: Exception) {
            Log.i(tagLogTaskDAO, "Erro ao atualizar | $motivo\nErro: $e")
        }
    }

    fun updateText(task: Task) { //It's only possible to update undone tasks.
        val cv = ContentValues()
        val dateEdition = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
        cv.put("texto", task.text)
        cv.put("dataEdicao", dateEdition)

        updateTaskInTable(task, cv, "Atualizar texto tarefa id ${task.id}")
    }

    fun finishTask(task: Task) {
        val cv = ContentValues()
        val dataFinalizacao = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)

        cv.put("dataFinalizacao", dataFinalizacao)
        cv.put("isFinalizado", true)
        cv.put("positionDone", countDone)
        cv.put("positionUndone", -1)

        removePositionFromList(task)
        updateTaskInTable(task, cv, "Finalizar tarefa id ${task.id}")
    }

    fun undoTask(task: Task) {
        val cv = ContentValues()

        cv.put("dataFinalizacao", "")
        cv.put("isFinalizado", false)
        cv.put("positionDone", -1)
        cv.put("positionUndone", countUndone)

        removePositionFromList(task)
        updateTaskInTable(task, cv, "Desfinalizar tarefa id ${task.id}")
    }

    private fun removePositionFromList(task: Task) {
        val list =
            getTaskList(task.isDone) as MutableList //receives undone or done list according to isDone property
        list.remove(task)
        val isListDone = task.isDone

        for ((index, taskItem) in list.withIndex()) {//update each index and then save it in db
            if (isListDone) taskItem.positionDone = index
            else taskItem.positionUndone = index

            val cv = ContentValues()
            cv.put("positionUndone", taskItem.positionUndone)
            cv.put("positionDone", taskItem.positionDone)
            updateTaskInTable(taskItem, cv, "Atualizar posição, id ${taskItem.id}")
        }
    }

    fun changePosition(
        task: Task,
        newPosition: Int
    ) { //troca de posição é feita apenas na lista undone
        val cv = ContentValues()
        cv.put("positionUndone", newPosition)

        updateTaskInTable(task, cv, "Troca de posição id ${task.id}, nova posição $newPosition")
    }

    /*fun below lists done or undone tasks according to the parameter value.*/
    fun getTaskList(isDone: Boolean): List<Task> {
        val isDoneInt = if (isDone) 1 else 0
        val positionType = if (isDone) "positionDone" else "positionUndone"

        val taskList = mutableListOf<Task>()
        val sql =
            "SELECT * FROM ${DbHelper.nameTaskTable} WHERE isFinalizado = $isDoneInt ORDER BY $positionType;"
        val cursor: Cursor = read.rawQuery(sql, null)

        while (cursor.moveToNext()) {
            val id: Int = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val text: String = cursor.getString(cursor.getColumnIndexOrThrow("texto"))
            val dateCreation: String = cursor.getString(cursor.getColumnIndexOrThrow("dataCriacao"))
            val dateDone: String = cursor.getString(cursor.getColumnIndexOrThrow("dataFinalizacao"))
            val dateEdition: String = cursor.getString(cursor.getColumnIndexOrThrow("dataEdicao"))
            val positionDone: Int = cursor.getInt(cursor.getColumnIndexOrThrow("positionDone"))
            val positionUndone: Int = cursor.getInt(cursor.getColumnIndexOrThrow("positionUndone"))

            val tarefa = Task(
                id, text, dateCreation, dateEdition = dateEdition, positionDone = positionDone,
                isDone = isDone, dateDone = dateDone, positionUndone = positionUndone
            )
            taskList.add(tarefa)
        }
        return taskList
    }
}