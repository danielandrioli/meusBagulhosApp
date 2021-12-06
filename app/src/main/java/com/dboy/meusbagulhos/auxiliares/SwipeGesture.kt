package com.dboy.meusbagulhos.auxiliares

import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.dboy.meusbagulhos.adapters.RViewUndoneListAdapter

class SwipeGesture(val tarefaDAO: TarefaDAO, val adapter: RViewUndoneListAdapter) :
    ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0){
    var myOrderChanged = false
    private var initialPosition = -1
    private var targetPosition = -1
    /*
    * //No momento em que o usuário clica no ítem, essa property abaixo segura a sua posição
    * */
    private var posicaoInicialFixa = -1

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        initialPosition = viewHolder.adapterPosition
        if (!myOrderChanged) posicaoInicialFixa = initialPosition
        targetPosition = target.adapterPosition
        Log.i("log swipe", "iPos: $initialPosition - tPos: $targetPosition")

        adapter.notifyItemMoved(initialPosition, targetPosition)
        myOrderChanged = true
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && myOrderChanged){
            val tarefa = adapter.listaTarefas[posicaoInicialFixa]
            Log.i("log posicao:", "tar: $tarefa")

            val mu = adapter.listaTarefas.toMutableList()
            mu.removeAt(posicaoInicialFixa) //removendo e adicionando da lista apenas para ela
            mu.add(targetPosition, tarefa) //organizar os índices automaticamente para mim. E após isso, reorganizo no db.

            mu.reverse()
            for ((indice, tarefa) in mu.withIndex()){
                tarefaDAO.trocarPosicao(tarefa, indice)
            }

            adapter.atualizarLista()
            myOrderChanged = false
        }
    }
}