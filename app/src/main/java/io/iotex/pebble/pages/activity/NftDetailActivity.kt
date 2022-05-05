package io.iotex.pebble.pages.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import io.iotex.core.base.BaseActivity
import io.iotex.pebble.R
import io.iotex.pebble.constant.PebbleStore
import io.iotex.pebble.module.viewmodel.ActivateVM
import kotlinx.android.synthetic.main.activity_nft_detail.*

class NftDetailActivity : BaseActivity(R.layout.activity_nft_detail) {

    private val mActivateVM by lazy {
        ViewModelProvider(this, mVmFactory)[ActivateVM::class.java]
    }

    private val mTokenId by lazy {
        intent.getStringExtra(KEY_TOKEN_ID)
    }
    private val mContract by lazy {
        intent.getStringExtra(KEY_CONTRACT)
    }
    private val mWalletAddress by lazy {
        intent.getStringExtra(KEY_WALLET_ADDRESS)
    }
    private val mDevice by lazy {
        PebbleStore.mDevice
    }

    override fun initView(savedInstanceState: Bundle?) {
        mTvWalletAddress.text = mWalletAddress
        mTvNftNo.text = "No.$mTokenId"
        mTvContract.text = mContract

        mTvSwitchWallet.setOnClickListener {
            mDevice ?: return@setOnClickListener
            mActivateVM.signDevice(mDevice!!)
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun registerObserver() {
        mActivateVM.mSignDeviceLD.observe(this) {
            if (it != null && !mWalletAddress.isNullOrBlank()) {
                if (!mTokenId.isNullOrBlank() && mDevice != null) {
                    mActivateVM.activateMetaPebble(
                        mTokenId!!,
                        mDevice!!,
                        it.imei,
                        it.sn,
                        it.pubkey,
                        it.timestamp.toString(),
                        it.authentication
                    )
                }
            }
        }
    }

    companion object {
        const val KEY_TOKEN_ID = "key_token_id"
        const val KEY_CONTRACT = "key_contract"
        const val KEY_WALLET_ADDRESS = "key_wallet_address"
    }
}