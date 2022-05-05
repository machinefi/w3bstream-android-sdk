package io.iotex.pebble.pages.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.drakeet.multitype.MultiTypeAdapter
import io.iotex.core.base.BaseActivity
import io.iotex.pebble.R
import io.iotex.pebble.constant.PebbleStore
import io.iotex.pebble.module.db.entries.RecordEntry
import io.iotex.pebble.module.viewmodel.PebbleVM
import io.iotex.pebble.pages.binder.*
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
        mAdapter.register(RecordEntry::class, recordBinder)
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