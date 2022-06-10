package io.iotex.pebble.pages.binder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.TimeUtils
import com.drakeet.multitype.ItemViewBinder
import io.iotex.pebble.R
import io.iotex.pebble.module.db.entries.RecordEntry

class RecordItemBinder: ItemViewBinder<RecordEntry, RecordItemBinder.VH>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): VH {
        val view = inflater.inflate(R.layout.item_record, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, item: RecordEntry) {
        holder.mTvLat.text = "lat: ${item.lat}"
        holder.mTvLong.text = "long: ${item.lng}"
        holder.mTvTime.text = TimeUtils.millis2String(item.timestamp.toLong())
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val mTvLat = view.findViewById<TextView>(R.id.mTvLat)
        val mTvLong = view.findViewById<TextView>(R.id.mTvLong)
        val mTvTime = view.findViewById<TextView>(R.id.mTvTime)
    }

}