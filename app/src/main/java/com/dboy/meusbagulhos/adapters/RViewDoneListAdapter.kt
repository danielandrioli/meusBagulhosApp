package com.dboy.meusbagulhos.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dboy.meusbagulhos.R
import com.dboy.meusbagulhos.auxiliares.DoubleClickListener
import com.dboy.meusbagulhos.auxiliares.TarefaDAO
import com.dboy.meusbagulhos.models.Tarefa

class RViewDoneListAdapter(private val context: Context, private val tarefaDAO: TarefaDAO) :
    RecyclerView.Adapter<RViewDoneListAdapter.MyViewHolder>() {
    var listaTarefasFeitas = tarefaDAO.listar(true).reversed()
        private set
    private lateinit var mListener: OnTarefaListener
    var isSelectedMode = false
    val listSelectedTasks = mutableListOf<Tarefa>()

    interface OnTarefaListener{
        fun onTarefaClick(posicao: Int)
        fun onTarefaLongClick(posicao: Int)
        fun onTarefaDoubleClick(posicao: Int)
        fun hideButtons()
    }

    fun setOnTarefaClickListener(listener: OnTarefaListener){
        mListener = listener
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val texto = itemView.findViewById<TextView>(R.id.tarefaFeitaTxt)
        val dataFinalizada = itemView.findViewById<TextView>(R.id.tarefaFeitaDataTxt)

        fun vincularTexto(tarefa: Tarefa) {
            texto.text = tarefa.texto
//            texto.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            dataFinalizada.text = "${context.getText(R.string.tarefaProntaEm)} ${tarefa.dataFinalizacao}"
            itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        fun selectItem(position: Int){
            val tarefa = listaTarefasFeitas[position]
            if(listSelectedTasks.contains(tarefa)){
                itemView.setBackgroundColor(Color.TRANSPARENT)
                listSelectedTasks.remove(tarefa)
            }else{
                itemView.setBackgroundResource(R.color.selected_background)
                listSelectedTasks.add(tarefa)
            }
            if (listSelectedTasks.size == 0) isSelectedMode = false
            Log.i("done adapter", "Lista selected tasks: $listSelectedTasks")
        }

        init {
//            mListener.hideButtons()
            itemView.setOnClickListener(object : DoubleClickListener(){
                override fun onDoubleClick() {
                    val posicao = adapterPosition
                    if (posicao != RecyclerView.NO_POSITION && !isSelectedMode) {
                        mListener.onTarefaDoubleClick(posicao)
                        Log.i("doneAdapter", "Double click!")
                    }
                }
                override fun onSingleClick() {
                    val posicao = adapterPosition
                    if(posicao != RecyclerView.NO_POSITION){
                        if(isSelectedMode){
                            selectItem(posicao)
                            if(!isSelectedMode) mListener.hideButtons()
                        } else mListener.onTarefaClick(posicao)
                        Log.i("doneAdapter", "Single click!")
                    }
                }
            })

            itemView.setOnLongClickListener {
                val posicao = adapterPosition
                if(posicao != RecyclerView.NO_POSITION){
                    isSelectedMode = true
                    selectItem(posicao)

                    mListener.onTarefaLongClick(posicao)
                    Log.i("doneAdapter", "Long click!")
                }
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.tarefa_feita, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val tarefa = listaTarefasFeitas[position]
        holder.vincularTexto(tarefa)
    }

    override fun getItemCount(): Int {
        return listaTarefasFeitas.size
    }

    fun atualizaLista(){
        listaTarefasFeitas = tarefaDAO.listar(true).reversed()
        notifyDataSetChanged()
    }
}