package com.machinefi.metapebble.widget

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.TypefaceCompat
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter
import com.blankj.utilcode.util.Utils
import com.contrarywind.view.WheelView
import com.machinefi.metapebble.R
import com.machinefi.metapebble.utils.extension.dp2px

@SuppressLint("RestrictedApi")
class PickerDialog(context: Context) : BaseDialog(context, R.layout.dialog_picker) {

    private val mIvClose: ImageView = findView(R.id.mIvClose)
    private val mTvTitle: TextView = findView(R.id.mTvTitle)
    private val mTvConfirm: TextView = findView(R.id.mTvConfirm)
    private val mWheelView: WheelView = findView(R.id.mWheelView)

    private var mOptionList = emptyList<PickerItemData>()

    init {
        mIvClose.setOnClickListener {
            dismiss()
        }

        mWheelView.setCyclic(false)
        mWheelView.setDividerColor(Utils.getApp().getColor(R.color.white_alpha_6))
        mWheelView.setDividerWidth(1.dp2px())
        mWheelView.setTextColorCenter(Utils.getApp().getColor(R.color.green_400))
        mWheelView.setTextSize(16F)
        val typeface = TypefaceCompat.createFromResourcesFontFile(Utils.getApp(), Utils.getApp().resources, R.font.space_grotesk_regular, "", 0)
        mWheelView.setTypeface(typeface)
        mWheelView.setLineSpacingMultiplier(15F)
        mWheelView.setItemsVisibleCount(3)
    }

    fun setTitle(title: String) = apply {
        mTvTitle.text = title
    }

    fun setOptions(list: List<PickerItemData>) = apply {
        mWheelView.adapter = ArrayWheelAdapter(list.map { it.label })
        mOptionList = list
    }

    fun setCurrentItem(item: PickerItemData) = apply {
        val index = mOptionList.indexOfFirst {
            it.value == item.value
        }
        if (index != -1) {
            mWheelView.currentItem = index
        }
    }

    fun setPositiveButton(positive: String, callback: ((PickerItemData) -> Unit)? = null) = apply {
        mTvConfirm.text = positive
        mTvConfirm.setOnClickListener {
            val index = mWheelView.currentItem
            if (mOptionList.size > index) {
                callback?.invoke(mOptionList[index])
            }
            dismiss()
        }
    }
}

data class PickerItemData(val label: String, val value: Int)
