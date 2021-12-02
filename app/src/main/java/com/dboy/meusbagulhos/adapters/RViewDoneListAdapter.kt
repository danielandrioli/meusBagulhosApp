package com.dboy.meusbagulhos.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.TypedArrayUtils.getText
import androidx.recyclerview.widget.RecyclerView
import com.dboy.meusbagulhos.R
import com.dboy.meusbagulhos.auxiliares.TarefaDAO
import com.dboy.meusbagulhos.models.Tarefa

class RViewDoneListAdapter(
    private val context: Context,
    private val tarefaDAO: TarefaDAO
) : RecyclerView.Adapter<RViewDoneListAdapter.MyViewHolder>() {
    private var listaTarefasFeitas = tarefaDAO.listarDone()

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun vincular(tarefa: Tarefa) {
            val texto = itemView.findViewById<TextView>(R.id.tarefaFeitaTxt)
            val dataFinalizada = itemView.findViewById<TextView>(R.id.tarefaFeitaDataTxt)

            texto.text = tarefa.texto
            dataFinalizada.text = "${context.getText(R.string.tarefaProntaEm)} ${tarefa.dataFinalizacao}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.tarefa_feita, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val tarefa = listaTarefasFeitas[position]
        holder.vincular(tarefa)
    }

    override fun getItemCount(): Int {
        return listaTarefasFeitas.size
    }

    fun atualizaLista(){
        listaTarefasFeitas = tarefaDAO.listarDone()
        notifyDataSetChanged()
    }

}