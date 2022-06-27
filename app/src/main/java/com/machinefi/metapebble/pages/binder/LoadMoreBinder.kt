package com.machinefi.metapebble.pages.binder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewBinder
import com.machinefi.metapebble.R

class LoadMoreBinder: ItemViewBinder<LoadMoreEntry, LoadMoreBinder.VH>()  {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): VH {
        val view = inflater.inflate(R.layout.item_see_more, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, item: LoadMoreEntry) {

    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view)
}

class LoadMoreEntry