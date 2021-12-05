package com.dboy.meusbagulhos.auxiliares

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class SwipeGesture : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN
    or ItemTouchHelper.START or ItemTouchHelper.END, 0){
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        var initialPosition = viewHolder.adapterPosition
        var targetPosition = target.adapterPosition


//        Collections.swap(recyclerView.adapter)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
}