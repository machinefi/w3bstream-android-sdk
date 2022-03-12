package io.iotex.pebble.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import io.iotex.pebble.R

class LoadingProgress(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val mTvProgress: TextView
    private val mPbLoading: ProgressBar

    init {
        View.inflate(context, R.layout.widget_loading_progress, this)
        mTvProgress = findViewById(R.id.mTvProgress)
        mPbLoading = findViewById(R.id.mPbLoading)
    }

    fun setProgress(progress: Int) {
        if (progress in (0..100)) {
            mPbLoading.progress = progress
            mTvProgress.text = "$progress%"
        }
    }

}