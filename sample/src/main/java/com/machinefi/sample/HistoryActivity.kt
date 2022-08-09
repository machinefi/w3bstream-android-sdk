package com.machinefi.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.SPUtils
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {

    private val adapter = MultiTypeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        mIvBack.setOnClickListener {
            onBackPressed()
        }

        adapter.register(String::class, HistoryBinder())
        mRvHistory.adapter = adapter

        val list = SPUtils.getInstance().getStringSet(KEY_HISTORY).toList()
        adapter.items = list
        adapter.notifyDataSetChanged()
    }

}