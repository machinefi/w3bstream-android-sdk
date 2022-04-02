package io.iotex.pebble.pages.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.FragmentUtils
import com.blankj.utilcode.util.Utils
import io.iotex.core.base.BaseFragment
import io.iotex.pebble.R
import io.iotex.pebble.utils.extension.renderHighlightTips
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_loading.*
import java.util.concurrent.TimeUnit

class LoadingFragment : BaseFragment(R.layout.fragment_loading) {

    private var mOnCompleteCallback: (() -> Unit)? = null
    private var mOnStartCallback: (() -> Unit)? = null

    private var mSourceTips: String = Utils.getApp().getString(R.string.prepare_tips)
    private var mHighlight: String = Utils.getApp().getString(R.string.meta_pebble)

    override fun initView(view: View, savedInstanceState: Bundle?) {
        mTvTips.renderHighlightTips(mSourceTips, mHighlight)

        Flowable.intervalRange(0, 101, 0, 30, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                mOnStartCallback?.invoke()
            }
            .doOnNext {
                mLoadingProgress.setProgress(it.toInt())
            }
            .doOnComplete {
                mOnCompleteCallback?.invoke()
            }
            .subscribe()
    }

    fun renderTitle(source: String, highlight: String) = apply {
        mSourceTips = source
        mHighlight = highlight
    }

    fun show(fm: FragmentManager, parent: Int) {
        FragmentUtils.add(fm, this, parent)
    }

    fun dismiss(fm: FragmentManager) {
        if (this.isAdded) {
            FragmentUtils.remove(this)
        }
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun registerObserver() {

    }

    fun setStartCallback(cb: () -> Unit) = apply {
        this.mOnStartCallback = cb
    }

    fun setCompleteCallback(cb: () -> Unit) = apply {
        this.mOnCompleteCallback = cb
    }
}