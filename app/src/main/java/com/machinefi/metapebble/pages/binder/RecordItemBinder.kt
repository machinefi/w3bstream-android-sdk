package com.machinefi.metapebble.pages.binder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.TimeUtils
import com.drakeet.multitype.ItemViewBinder
import com.machinefi.metapebble.R
import com.machinefi.metapebble.module.db.entries.RecordEntry
import io.iotex.graphql.test.RecordQuery

class RecordItemBinder: ItemViewBinder<RecordQuery.Pebble_device_record, RecordItemBinder.VH>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): VH {
        val view = inflater.inflate(R.layout.item_record, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, item: RecordQuery.Pebble_device_record) {
        holder.mTvLat.text = item.latitude
        holder.mTvLong.text = item.longitude
        holder.mTvTime.text = TimeUtils.millis2String(item.timestamp.toLong())
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val mTvLat = view.findViewById<TextView>(R.id.mTvLat)
        val mTvLong = view.findViewById<TextView>(R.id.mTvLong)
        val mTvTime = view.findViewById<TextView>(R.id.mTvTime)
    }

}