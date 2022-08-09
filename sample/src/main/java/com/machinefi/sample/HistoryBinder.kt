package com.machinefi.sample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewBinder
import com.yuyh.jsonviewer.library.JsonRecyclerView

class HistoryBinder: ItemViewBinder<String, HistoryBinder.VH>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): VH {
        val view = inflater.inflate(R.layout.item_history, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, item: String) {
        holder.mJsonViewer.bindJson(item)
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val mJsonViewer = view.findViewById<JsonRecyclerView>(R.id.mJsonViewer)
    }
}