package com.dboy.meusbagulhos.adapters

import android.content.Context
import android.graphics.Color
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

class RViewDoneListAdapter(private val context: Context, private val taskDAO: TaskDAO) :
    RecyclerView.Adapter<RViewDoneListAdapter.MyViewHolder>() {
    var listTaskDone = taskDAO.getTaskList(true).reversed()
        private set
    private lateinit var mListener: OnTaskListener
    var isSelectedMode = false
    val listSelectedTasks = mutableListOf<Task>()

    interface OnTaskListener {
        fun onTaskClick(position: Int)
        fun onTaskLongClick(position: Int)
        fun onTaskDoubleClick(position: Int)
        fun hideButtons()
    }

    fun setOnTaskClickListener(listener: OnTaskListener) {
        mListener = listener
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text = itemView.findViewById<TextView>(R.id.taskDoneTxt)
        val dateDone = itemView.findViewById<TextView>(R.id.taskDoneDateTxt)

        fun bindText(task: Task) {
            text.text = task.text
//            texto.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            dateDone.text = "${context.getText(R.string.tarefaProntaEm)} ${task.dateDone}"
            itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        fun selectItem(position: Int) {
            val tarefa = listTaskDone[position]
            if (listSelectedTasks.contains(tarefa)) {
                itemView.setBackgroundColor(Color.TRANSPARENT)
                listSelectedTasks.remove(tarefa)
            } else {
                itemView.setBackgroundResource(R.color.selected_background)
                listSelectedTasks.add(tarefa)
            }
            if (listSelectedTasks.size == 0) isSelectedMode = false
            Log.i("done adapter", "Lista selected tasks: $listSelectedTasks")
        }

        init {
            itemView.setOnClickListener(object : DoubleClickListener() {
                override fun onDoubleClick() {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION && !isSelectedMode) {
                        mListener.onTaskDoubleClick(position)
                        Log.i("doneAdapter", "Double click!")
                    }
                }

                override fun onSingleClick() {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        if (isSelectedMode) {
                            selectItem(position)
                            if (!isSelectedMode) mListener.hideButtons()
                        } else mListener.onTaskClick(position)
                        Log.i("doneAdapter", "Single click!")
                    }
                }
            })

            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    isSelectedMode = true
                    selectItem(position)

                    mListener.onTaskLongClick(position)
                    Log.i("doneAdapter", "Long click!")
                }
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.task_done, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val task = listTaskDone[position]
        holder.bindText(task)
    }

    override fun getItemCount(): Int {
        return listTaskDone.size
    }

    fun updateList() {
        listTaskDone = taskDAO.getTaskList(true).reversed()
        notifyDataSetChanged()
    }
}