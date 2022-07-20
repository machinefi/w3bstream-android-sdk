package com.machinefi.metapebble.pages.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.machinefi.core.base.BaseActivity
import com.machinefi.metapebble.R
import com.machinefi.metapebble.constant.PebbleStore
import com.machinefi.metapebble.module.viewmodel.ActivateVM
import com.machinefi.metapebble.pages.fragment.LoadingFragment
import com.machinefi.metapebble.utils.RxUtil
import com.machinefi.metapebble.utils.extension.gone
import com.machinefi.metapebble.utils.extension.visible
import com.machinefi.metapebble.widget.LoadingDialog
import com.machinefi.metapebble.widget.PromptDialog
import kotlinx.android.synthetic.main.activity_nft_detail.*
import kotlinx.android.synthetic.main.activity_nft_detail.mTvActivate
import kotlinx.android.synthetic.main.activity_nft_detail.mTvApprove
import kotlinx.android.synthetic.main.activity_nft_list.*
import org.jetbrains.anko.startActivity
import java.io.Serializable
import java.util.concurrent.TimeUnit

class NftDetailActivity : BaseActivity(R.layout.activity_nft_detail) {

    private val mActivateVM by lazy {
        ViewModelProvider(this, mVmFactory)[ActivateVM::class.java]
    }

    private val mNftWrapper by lazy {
        intent.getSerializableExtra(KEY_NFT_WRAPPER) as? NftWrapper
    }

    private val mProgress by lazy {
        LoadingDialog(this)
    }

    private val mDevice by lazy {
        PebbleStore.mDevice
    }

    override fun beforeInflate(savedInstanceState: Bundle?) {
        super.beforeInflate(savedInstanceState)
        mNftWrapper ?: onBackPressed()
    }

    @SuppressLint("CheckResult")
    override fun initView(savedInstanceState: Bundle?) {
        mTvWalletAddress.text = mNftWrapper?.walletAddress
        mTvNftNo.text = "No.${mNftWrapper?.tokenId}"
        mTvContract.text = mNftWrapper?.contract

        if (mNftWrapper?.consumed == false) {
            mTvApprove.gone()
            mTvActivate.visible()
            mTvActivate.isEnabled = false
        } else if (mNftWrapper?.approved == true && mNftWrapper?.consumed == false) {
            mTvApprove.visible()
            mTvActivate.gone()
        } else if (mNftWrapper?.approved == false && mNftWrapper?.consumed == false) {
            mTvApprove.gone()
            mTvActivate.visible()
        }

        RxUtil.clicks(mTvApprove)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                approve()
            }

        mTvActivate.setOnClickListener {
            mDevice ?: return@setOnClickListener
            mActivateVM.signDevice(mDevice!!)
            mProgress.show()
        }
    }

    private fun approve() {
        PromptDialog(this)
            .setTitle(getString(R.string.approve))
            .setContent(getString(R.string.sign_wallet_tips))
            .setPositiveButton(getString(R.string.confirm)) {
                val tokenId = mNftWrapper?.tokenId
                if (!tokenId.isNullOrBlank()) {
                    mActivateVM.approveRegistration(tokenId)
                }
            }
            .show()
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    private fun activateProgress() {
        val step02 = LoadingFragment()
            .renderTitle(
                getString(R.string.registering_metapebble),
                getString(R.string.meta_pebble)
            )
            .setCompleteCallback {
                startActivity<ActivateCompleteActivity>()
                finish()
            }

        LoadingFragment()
            .renderTitle(getString(R.string.activating_metapebble), getString(R.string.meta_pebble))
            .setCompleteCallback {
                step02.start(supportFragmentManager, R.id.mRlContainer)
                    .complete()
            }
            .start(supportFragmentManager, R.id.mRlContainer)
            .complete()
    }

    override fun registerObserver() {
        mActivateVM.mSignDeviceLD.observe(this) {
            mProgress.dismiss()
            val tokenId = mNftWrapper?.tokenId
            if (it != null && !mNftWrapper?.walletAddress.isNullOrBlank()) {
                if (!tokenId.isNullOrBlank() && mDevice != null) {
                    mActivateVM.activateMetaPebble(
                        tokenId,
                        mDevice?.pubKey ?: "",
                        it.imei,
                        it.sn,
                        it.timestamp.toString(),
                        it.authentication
                    )
                }
            }
        }
        mActivateVM.mApproveLd.observe(this) { tokenId ->
            if (!tokenId.isNullOrBlank()) {
                mTvApprove.gone()
                mTvActivate.visible()
            }
        }
        mActivateVM.mActivateLd.observe(this) {
            activateProgress()
        }
    }

    companion object {
        const val KEY_NFT_WRAPPER = "key_nft_wrapper"
    }
}

data class NftWrapper(
    val tokenId: String,
    val consumed: Boolean,
    val approved: Boolean,
    val contract: String,
    val walletAddress: String
): Serializable