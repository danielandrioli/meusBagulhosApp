package com.dboy.meusbagulhos.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dboy.meusbagulhos.R
import com.dboy.meusbagulhos.helpers.DoubleClickListener
import com.dboy.meusbagulhos.helpers.TaskDAO
import com.dboy.meusbagulhos.models.Task

class RViewUndoneListAdapter(private val context: Context, private val taskDAO: TaskDAO) :
    RecyclerView.Adapter<RViewUndoneListAdapter.MyViewHolder>() {
    var listTaskUndone = taskDAO.getTaskList(false).reversed()
        private set
    private lateinit var mListener: OnTarefaListener

    interface OnTarefaListener {
        fun onTaskClick(position: Int)
        fun onTaskDoubleClick(position: Int)
    }

    fun setOnTaskClickListener(listener: OnTarefaListener) {
        mListener = listener
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text = itemView.findViewById<TextView>(R.id.taskUnd_toDo_txt)
        val dateDone = itemView.findViewById<TextView>(R.id.taskUnd_date_txt)

        fun bindText(task: Task) {
            text.text = task.text
            dateDone.text = if (task.dateEdition.isNotBlank()) {
                "${context.getText(R.string.tarefaEditadaEm)} ${task.dateEdition}"
            } else {
                "${context.getText(R.string.tarefaCriadaEm)} ${task.dateCreation}"
            }
        }

        init {
            itemView.setOnClickListener(object : DoubleClickListener() {
                override fun onDoubleClick() {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onTaskDoubleClick(position)
                        Log.i("undoneAdapter", "Double click!")
                    }
                }

                override fun onSingleClick() {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onTaskClick(position)
                        Log.i("undoneAdapter", "Single click!")
                    }
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.task_undone, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val task = listTaskUndone[position]
        holder.bindText(task)
    }

    override fun getItemCount(): Int {
        return listTaskUndone.size
    }

    fun updateList() {
        listTaskUndone = taskDAO.getTaskList(false).reversed()
        notifyDataSetChanged()
    }
}