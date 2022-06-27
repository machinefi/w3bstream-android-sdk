package com.machinefi.metapebble.pages.activity

import android.content.Intent
import android.os.Bundle
import com.machinefi.core.base.BaseActivity
import com.machinefi.metapebble.R
import com.machinefi.metapebble.constant.QueryActivateResultEvent
import com.machinefi.metapebble.utils.extension.Style
import com.machinefi.metapebble.utils.extension.renderHighlightTips
import com.machinefi.metapebble.widget.PromptDialog
import kotlinx.android.synthetic.main.activity_activate_complete.*
import kotlinx.android.synthetic.main.include_bar.*
import org.greenrobot.eventbus.EventBus

class ActivateCompleteActivity : BaseActivity(R.layout.activity_activate_complete) {

    override fun initView(savedInstanceState: Bundle?) {
        renderTips()

        mPubToolbar.setNavigationOnClickListener {
            backHome()
        }

        mTvBackHome.setOnClickListener {
            backHome()
        }

        mTvAuthorize.setOnClickListener {
            PromptDialog(this)
                .setTitle(getString(R.string.authorize_to_pop))
                .setContent(getString(R.string.authorize_to_pop_tips_01))
                .setPositiveButton(getString(R.string.authorize))
                .setCaption(getString(R.string.try_later), true)
                .show()
        }
    }

    private fun renderTips() {
        val tipsStr01 = getString(R.string.activated_registered_successfully)
        val highLightStr01 = getString(R.string.meta_pebble)
        val normalStyle = Style(R.color.white, 20)
        val highlightStyle = Style(R.color.green_400, 20)
        mTvTips02.renderHighlightTips(tipsStr01, normalStyle, highLightStr01, highlightStyle)
    }

    private fun backHome() {
        EventBus.getDefault().post(QueryActivateResultEvent())
        startActivity(Intent(this, DevicePanelActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        })
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun registerObserver() {
    }
}