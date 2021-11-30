package com.dboy.meusbagulhos.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dboy.meusbagulhos.R
import com.dboy.meusbagulhos.models.Tarefa
import java.util.zip.Inflater

class RViewUndoneListAdapter(private val context: Context, private var listaTarefas: List<Tarefa>) :
    RecyclerView.Adapter<RViewUndoneListAdapter.MeuViewHolder>() {

    inner class MeuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun vincular(tarefa: Tarefa) {
            val textoTarefa = itemView.findViewById<TextView>(R.id.tarefaNao_aFazer_txt)
            val dataTarefa = itemView.findViewById<TextView>(R.id.tarefaNao_data_txt)

            textoTarefa.text = tarefa.texto
            dataTarefa.text = if (tarefa.isEditado){
                "${context.getText(R.string.tarefaEditadaEm)} ${tarefa.dataEdicao}"
            }else{
                "${context.getText(R.string.tarefaCriadaEm)} ${tarefa.dataCriacao}"
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeuViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.tarefa_nao_feita, parent, false)
        return MeuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MeuViewHolder, position: Int) {
        val tarefa = listaTarefas[position]
        holder.vincular(tarefa)
    }

    override fun getItemCount(): Int {
        return listaTarefas.size
    }
}