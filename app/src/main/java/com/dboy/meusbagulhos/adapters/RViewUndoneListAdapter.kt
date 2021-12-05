package com.dboy.meusbagulhos.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dboy.meusbagulhos.R
import com.dboy.meusbagulhos.auxiliares.DoubleClickListener
import com.dboy.meusbagulhos.auxiliares.TarefaDAO
import com.dboy.meusbagulhos.models.Tarefa

class RViewUndoneListAdapter(private val context: Context, private val tarefaDAO: TarefaDAO) :
    RecyclerView.Adapter<RViewUndoneListAdapter.MeuViewHolder>() {
    var listaTarefas = tarefaDAO.listarUndone().reversed()
        private set
    private lateinit var mListener: OnTarefaListener

    interface OnTarefaListener{
        fun onTarefaClick(posicao: Int)
        fun onTarefaLongClick(posicao: Int)
        fun onTarefaDoubleClick(posicao: Int)
    }

    fun setOnTarefaClickListener(listener: OnTarefaListener){
        mListener = listener
    }

    inner class MeuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textoTarefa = itemView.findViewById<TextView>(R.id.tarefaNao_aFazer_txt)
        val dataTarefa = itemView.findViewById<TextView>(R.id.tarefaNao_data_txt)
        val dragButton = itemView.findViewById<ImageView>(R.id.tarefaNao_drag_img)

        fun vincularTexto(tarefa: Tarefa) {
            textoTarefa.text = tarefa.texto
            dataTarefa.text = if (tarefa.dataEdicao != null){
                "${context.getText(R.string.tarefaEditadaEm)} ${tarefa.dataEdicao}"
            }else{
                "${context.getText(R.string.tarefaCriadaEm)} ${tarefa.dataCriacao}"
            }
        }

        init {//DA PARA COLOCAR O CLICKLISTENER NO ITEMVIEW. AÍ PEGA T0DO ELE, COM EXCEÇÃO DO DRAG Q VAI TER SEU PROPRIO LISTENER
            textoTarefa.setOnClickListener(object : DoubleClickListener(){
                override fun onDoubleClick() {
                    val posicao = adapterPosition
                    if(posicao != RecyclerView.NO_POSITION){
                        mListener.onTarefaDoubleClick(posicao)
                        Log.i("undoneAdapter", "Double click!")
                    }
                }

                override fun onSingleClick() {
                    val posicao = adapterPosition
                    if(posicao != RecyclerView.NO_POSITION){
                        mListener.onTarefaClick(posicao)
                        Log.i("undoneAdapter", "Single click!")
                    }
                }
            })
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeuViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.tarefa_nao_feita, parent, false)
        return MeuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MeuViewHolder, position: Int) {
        val tarefa = listaTarefas[position]
        holder.vincularTexto(tarefa)
    }

    override fun getItemCount(): Int {
        return listaTarefas.size
    }

    fun atualizarLista(){
        listaTarefas = tarefaDAO.listarUndone().reversed()
        notifyDataSetChanged()
    }

}