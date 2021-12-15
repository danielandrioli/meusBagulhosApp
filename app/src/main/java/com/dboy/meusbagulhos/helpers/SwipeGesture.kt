package com.dboy.meusbagulhos.helpers

import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.dboy.meusbagulhos.adapters.RViewUndoneListAdapter

class SwipeGesture(private val taskDAO: TaskDAO, private val adapter: RViewUndoneListAdapter) :
    ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
    private var myOrderChanged = false
    private var initialPosition = -1
    private var targetPosition = -1

    /*
    * After clicking and holding the item, this property below holds the item position
    * */
    private var initialPositionFixed = -1

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        initialPosition = viewHolder.adapterPosition
        if (!myOrderChanged) initialPositionFixed = initialPosition
        targetPosition = target.adapterPosition
        Log.i("log swipe", "iPos: $initialPosition - tPos: $targetPosition")

        adapter.notifyItemMoved(initialPosition, targetPosition)
        myOrderChanged = true
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && myOrderChanged) {
            val task = adapter.listTaskUndone[initialPositionFixed]
            Log.i("log posicao:", "tar: $task")

            val mu = adapter.listTaskUndone.toMutableList()
            mu.removeAt(initialPositionFixed) //removing and adding from this list cause the list automatically organizes the indexes
            mu.add(targetPosition, task) //for me. After that, I just update them in the database
            mu.reverse()

            for ((index, task) in mu.withIndex()) {
                taskDAO.changePosition(task, index)
            }

            adapter.updateList()
            myOrderChanged = false
        }
    }
}