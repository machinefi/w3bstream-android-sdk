package com.machinefi.metapebble.pages.binder

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter

class LoadMoreDelegate(val mAdapter: MultiTypeAdapter, val mListener: OnLoadMoreListener) {

    private lateinit var mScrollListener: ScrollListener

    private val mItems = mutableListOf<Any>()

    fun attach(recyclerView: RecyclerView) {
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        mScrollListener = ScrollListener(
            mAdapter,
            mItems,
            linearLayoutManager,
            mListener
        )
        recyclerView.addOnScrollListener(mScrollListener)
    }

    fun loadMoreComplete() {
        mScrollListener.setLoading(false)
    }

    fun refresh(items: List<Any>) {
        mItems.clear()
        mItems.addAll(items)
        mAdapter.items = mItems
        mAdapter.notifyDataSetChanged()
    }

    fun addData(items: List<Any>) {
        if (mItems.isNotEmpty() && mItems[mItems.size - 1]::class.java.name == LoadMoreEntry::class.java.name) {
            mItems.removeAt(mItems.size - 1)
            mItems.addAll(items)
        } else {
            mItems.addAll(items)
        }
        mAdapter.items = mItems
        mAdapter.notifyItemRemoved(mItems.size)
        mAdapter.notifyItemRangeInserted(mItems.size - items.size, items.size)
        mAdapter.notifyItemRangeChanged(mItems.size - items.size, items.size)
    }

    fun addDataInHeader(item: Any) {
        if (mItems.isNotEmpty()) {
            mItems.add(0, item)
        } else {
            mItems.add(item)
        }
        mAdapter.items = mItems
        mAdapter.notifyDataSetChanged()
    }

    inner class ScrollListener(
        val adapter: MultiTypeAdapter,
        val items: MutableList<Any>,
        val linearLayoutManager: LinearLayoutManager,
        val onLoadMoreListener: OnLoadMoreListener
    ) : RecyclerView.OnScrollListener() {

        private val size = 2
        private var mLoading = false

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy < 0) {
                return
            }
            val totalNum: Int = linearLayoutManager.itemCount
            val lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()
            if (!mLoading && lastVisiblePosition == totalNum - size) {
                mLoading = true
                recyclerView.post {
                    items.add(LoadMoreEntry())
                    adapter.notifyItemInserted(mItems.size - 1)
                    onLoadMoreListener.onLoadMore()
                }
            }
        }

        fun setLoading(loading: Boolean) {
            mLoading = loading
        }
    }
}

interface OnLoadMoreListener {
    fun onLoadMore()
}
