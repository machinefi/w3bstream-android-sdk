package io.iotex.pebble.pages.binder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewBinder
import io.iotex.pebble.R

class NftItemBinder : ItemViewBinder<NftEntry, NftItemBinder.VH>() {

    private var mSelectedListener: ((NftEntry) -> Unit)? = null
    private var mItemClickListener: ((NftEntry) -> Unit)? = null

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): VH {
        val view = inflater.inflate(R.layout.item_select_nft, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, item: NftEntry) {
        holder.mTvNftNo.text = item.no
        holder.mRBtnSelect.isChecked = item.selected
        holder.mRBtnSelect.setOnClickListener {
            if (item.selected) return@setOnClickListener
            adapterItems.forEach {
                if (it is NftEntry) {
                    it.selected = false
                }
            }
            item.selected = !item.selected
            holder.mRBtnSelect.isChecked = item.selected
            adapter.notifyDataSetChanged()
            mSelectedListener?.invoke(item)
        }
        holder.itemView.setOnClickListener {
            mItemClickListener?.invoke(item)
        }
    }

    fun setOnSelectedListener(l: (NftEntry) -> Unit) {
        this.mSelectedListener = l
    }

    fun setOnItemClickListener(l: (NftEntry) -> Unit) {
        this.mItemClickListener = l
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val mTvNftNo = view.findViewById<TextView>(R.id.mTvNftNo)
        val mRBtnSelect = view.findViewById<RadioButton>(R.id.mRBtnSelect)
    }
}

data class NftEntry(val no: String, var selected: Boolean = false)