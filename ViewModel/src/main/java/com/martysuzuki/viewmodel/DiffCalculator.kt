package com.martysuzuki.viewmodel

import androidx.recyclerview.widget.DiffUtil
import com.martysuzuki.viewmodelinterface.DiffableItem

internal class DiffCalculator<Item : DiffableItem>(
    private val newItems: List<Item>,
    private val oldItems: List<Item>
) : DiffUtil.Callback() {
    override fun getNewListSize() = newItems.size

    override fun getOldListSize() = oldItems.size

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ) = oldItems[oldItemPosition].identifier == newItems[newItemPosition].identifier

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ) = oldItems[oldItemPosition] == newItems[newItemPosition]

    fun calculateDiff() = DiffUtil.calculateDiff(this)
}