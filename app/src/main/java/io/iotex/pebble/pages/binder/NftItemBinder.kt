package io.iotex.pebble.pages.binder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewBinder
import io.iotex.graphql.smartcontract.NftListQuery
import io.iotex.pebble.R
import io.iotex.pebble.utils.extension.loadImage
import io.iotex.pebble.utils.extension.setGone
import io.iotex.pebble.utils.extension.setVisible
import java.io.Serializable

class NftItemBinder : ItemViewBinder<NftEntry, NftItemBinder.VH>() {

    private var mSelectedListener: ((NftEntry) -> Unit)? = null
    private var mItemClickListener: ((NftEntry) -> Unit)? = null

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): VH {
        val view = inflater.inflate(R.layout.item_select_nft, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, item: NftEntry) {
        holder.mIvNftLogo.loadImage(item.nft.tokenURI, R.mipmap.ic_nft)
        if (item.nft.consumed == true) {
            holder.mFlSelect.isEnabled = false
            holder.mIvNftLogo.alpha = 0.5F
            holder.mIvArrow.alpha = 0.5F
            holder.mIvMark.setVisible()
        } else {
            holder.mFlSelect.isEnabled = true
            holder.mIvNftLogo.alpha = 1F
            holder.mIvArrow.alpha = 1F
            holder.mIvMark.setGone()
        }

        holder.mTvNftNo.text = "No.${item.nft.tokenId}"
        holder.mRBtnSelect.isChecked = item.selected
        holder.mFlSelect.setOnClickListener {
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
        val mIvNftLogo = view.findViewById<ImageView>(R.id.mIvNftLogo)
        val mIvMark = view.findViewById<ImageView>(R.id.mIvMark)
        val mTvNftNo = view.findViewById<TextView>(R.id.mTvNftNo)
        val mRBtnSelect = view.findViewById<RadioButton>(R.id.mRBtnSelect)
        val mFlSelect = view.findViewById<View>(R.id.mFlSelect)
        val mIvArrow = view.findViewById<View>(R.id.mIvArrow)
    }
}

data class NftEntry(
    val nft: NftListQuery.TokenList,
    val contract: String,
    var selected: Boolean = false
): Serializable