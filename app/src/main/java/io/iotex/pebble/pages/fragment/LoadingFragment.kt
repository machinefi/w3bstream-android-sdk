package io.iotex.pebble.pages.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.FragmentUtils
import com.blankj.utilcode.util.Utils
import io.iotex.core.base.BaseFragment
import io.iotex.pebble.R
import io.iotex.pebble.utils.extension.Style
import io.iotex.pebble.utils.extension.i
import io.iotex.pebble.utils.extension.renderHighlightTips
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_loading.*
import java.util.concurrent.TimeUnit

class LoadingFragment : BaseFragment(R.layout.fragment_loading) {

    private var mOnCompleteCallback: (() -> Unit)? = null
    private var mOnStartCallback: (() -> Unit)? = null

    private var mSourceTips: String = Utils.getApp().getString(R.string.prepare_tips)
    private var mHighlight: String = Utils.getApp().getString(R.string.meta_pebble)

    private val mFirstTrip = 90L
    private val mSecondTrip = 100L

    private var mFirstTripComplete = false
    private var mShouldStartSecondTrip = false

    override fun initView(view: View, savedInstanceState: Bundle?) {
        val normalStyle = Style(R.color.white, 35)
        val highlightStyle = Style(R.color.green_400, 35)
        mTvTips.renderHighlightTips(mSourceTips, normalStyle, mHighlight, highlightStyle)
    }

    fun renderTitle(source: String, highlight: String) = apply {
        mSourceTips = source
        mHighlight = highlight
    }

    fun start(fm: FragmentManager, parent: Int) = apply {
        FragmentUtils.add(fm, this, parent)
        startFirstTrip()
    }

    private fun startFirstTrip() {
        Flowable.intervalRange(1, mFirstTrip, 0, 30, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                mOnStartCallback?.invoke()
            }
            .doOnNext {
                mLoadingProgress.setProgress(it.toInt())
            }
            .doFinally {
                mFirstTripComplete = true
                if (mShouldStartSecondTrip) {
                    startSecondTrip()
                }
            }
            .subscribe()
    }

    private fun startSecondTrip() {
        Flowable.intervalRange(mFirstTrip + 1, mSecondTrip - mFirstTrip, 0, 50, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                mLoadingProgress.setProgress(it.toInt())
            }
            .doFinally {
                mOnCompleteCallback?.invoke()
            }
            .subscribe()
    }

    fun complete() {
        if (mFirstTripComplete) {
            startSecondTrip()
        } else {
            mShouldStartSecondTrip = true
        }
    }

    fun dismiss() {
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