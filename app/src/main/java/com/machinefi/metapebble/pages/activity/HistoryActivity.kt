package com.machinefi.metapebble.pages.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.drakeet.multitype.MultiTypeAdapter
import com.machinefi.core.base.BaseActivity
import com.machinefi.metapebble.R
import com.machinefi.metapebble.constant.PebbleStore
import com.machinefi.metapebble.module.db.entries.RecordEntry
import com.machinefi.metapebble.module.viewmodel.PebbleVM
import com.machinefi.metapebble.pages.binder.*
import io.iotex.graphql.test.RecordQuery
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : BaseActivity(R.layout.activity_history), OnLoadMoreListener {

    private val mPebbleVM by lazy {
        ViewModelProvider(this, mVmFactory)[PebbleVM::class.java]
    }

    private val mAdapter = MultiTypeAdapter()
    private lateinit var mLoadMoreDelegate: LoadMoreDelegate

    private var page = 1
    private val pageSize = 15

    private val mImei by lazy {
        PebbleStore.mDevice?.imei ?: ""
    }

    override fun initView(savedInstanceState: Bundle?) {
        val recordBinder = RecordItemBinder()
        val loadMoreBinder = LoadMoreBinder()
        mAdapter.register(RecordQuery.Pebble_device_record::class, recordBinder)
        mAdapter.register(LoadMoreEntry::class, loadMoreBinder)
        mRvContent.adapter = mAdapter
        mLoadMoreDelegate = LoadMoreDelegate(mAdapter, this)
        mLoadMoreDelegate.attach(mRvContent)
    }

    override fun initData(savedInstanceState: Bundle?) {
        mPebbleVM.queryRecordList(mImei, page, pageSize)
    }

    override fun onLoadMore() {
        page++
        mPebbleVM.queryRecordList(mImei, page, pageSize)
    }

    override fun registerObserver() {
        mPebbleVM.mRecordListLD.observe(this) {
            mLoadMoreDelegate.addData(it)
            mLoadMoreDelegate.loadMoreComplete()
            if (mAdapter.itemCount <= 0) {
                mRvContent?.visibility = View.GONE
                mLlEmpty?.visibility = View.VISIBLE
            } else {
                mRvContent?.visibility = View.VISIBLE
                mLlEmpty?.visibility = View.GONE
            }
        }
    }
}