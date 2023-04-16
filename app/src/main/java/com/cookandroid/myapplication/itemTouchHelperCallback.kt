package com.cookandroid.myapplication

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

interface ItemTouchhelperlistener{
    fun onItemMove(fromPosition: Int, toposition: Int): Boolean
    fun onItemSwipe(position: Int)
}

class itemTouchHelperCallback(val listener: ItemTouchhelperlistener): ItemTouchHelper.Callback(){
    private var itemTouchhelperlistener: ItemTouchhelperlistener = listener

    override fun isLongPressDragEnabled(): Boolean {
        return super.isLongPressDragEnabled()
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return super.isItemViewSwipeEnabled()
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int{
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }
    override fun onMove(
        recyclerView: RecyclerView, viewHolder: ViewHolder, target: RecyclerView.ViewHolder): Boolean{
        itemTouchhelperlistener.onItemMove(
            viewHolder.adapterPosition, target.adapterPosition
        )
        return true
    }

    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
        itemTouchhelperlistener.onItemSwipe(viewHolder.adapterPosition)
    }
}