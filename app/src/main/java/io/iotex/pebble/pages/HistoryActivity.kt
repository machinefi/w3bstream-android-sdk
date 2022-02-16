package io.iotex.pebble.pages

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.drakeet.multitype.MultiTypeAdapter
import io.iotex.core.base.BaseActivity
import io.iotex.pebble.R
import io.iotex.pebble.module.db.entries.RecordEntry
import io.iotex.pebble.module.viewmodel.WalletVM
import io.iotex.pebble.pages.binder.*
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : BaseActivity(), OnLoadMoreListener {

    private val mWalletVM by lazy {
        ViewModelProvider(this)[WalletVM::class.java]
    }

    private val mAdapter = MultiTypeAdapter()
    private lateinit var mLoadMoreDelegate: LoadMoreDelegate

    private var page = 1
    private val pageSize = 15

    private val mImei by lazy {
        intent.getStringExtra(KEY_IMEI) ?: ""
    }

    override fun layoutResourceID(savedInstanceState: Bundle?): Int {
        return R.layout.activity_history
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
        mWalletVM.queryRecordList(mImei, page, pageSize)
    }

    override fun onLoadMore() {
        page++
        mWalletVM.queryRecordList(mImei, page, pageSize)
    }

    override fun registerObserver() {
        mWalletVM.mRecordListLiveData.observe(this) {
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

    companion object {
        const val KEY_IMEI = "key_imei"
    }


}