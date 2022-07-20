package com.machinefi.metapebble.pages.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.SimpleItemAnimator
import com.drakeet.multitype.MultiTypeAdapter
import com.machinefi.core.base.BaseActivity
import com.machinefi.metapebble.R
import com.machinefi.metapebble.constant.PebbleStore
import com.machinefi.metapebble.module.viewmodel.ActivateVM
import com.machinefi.metapebble.module.viewmodel.PebbleVM
import com.machinefi.metapebble.module.walletconnect.WalletConnector
import com.machinefi.metapebble.pages.binder.NftEntry
import com.machinefi.metapebble.pages.binder.NftItemBinder
import com.machinefi.metapebble.pages.fragment.LoadingFragment
import com.machinefi.metapebble.utils.AddressUtil
import com.machinefi.metapebble.utils.RxUtil
import com.machinefi.metapebble.utils.extension.ellipsis
import com.machinefi.metapebble.utils.extension.gone
import com.machinefi.metapebble.utils.extension.updateItem
import com.machinefi.metapebble.utils.extension.visible
import com.machinefi.metapebble.widget.DisconnectDialog
import com.machinefi.metapebble.widget.LoadingDialog
import com.machinefi.metapebble.widget.PromptDialog
import kotlinx.android.synthetic.main.activity_nft_list.*
import org.jetbrains.anko.startActivity
import java.util.concurrent.TimeUnit

class NftListActivity : BaseActivity(R.layout.activity_nft_list) {

    private val mPebbleVM by lazy {
        ViewModelProvider(this, mVmFactory)[PebbleVM::class.java]
    }
    private val mActivateVM by lazy {
        ViewModelProvider(this, mVmFactory)[ActivateVM::class.java]
    }

    private val mProgress by lazy {
        LoadingDialog(this)
    }

    private val mAdapter = MultiTypeAdapter()

    private var mSelectedNft: NftEntry? = null

    private val mDevice by lazy {
        PebbleStore.mDevice
    }

    @SuppressLint("CheckResult")
    override fun initView(savedInstanceState: Bundle?) {
        mTvEmptyAddress.text = WalletConnector.walletAddress?.ellipsis(6, 6)
        mTvAddress.text = WalletConnector.walletAddress?.ellipsis(6, 6)
        mSrlContent.setColorSchemeResources(R.color.colorPrimary)
        mSrlContent.setOnRefreshListener {
            queryNftList()
        }
        val binder = NftItemBinder().apply {
            setOnSelectedListener {
                mSelectedNft = it
                mTvApprove.isEnabled = true
                mTvActivate.isEnabled = true
                if (AddressUtil.isValidAddress(it.nft.approved ?: "")) {
                    mTvApprove.gone()
                    mTvActivate.visible()
                } else {
                    mTvApprove.visible()
                    mTvActivate.gone()
                }
            }
            setOnItemClickListener { nftEntry ->
                val approved = AddressUtil.isValidAddress(nftEntry.nft.approved ?: "")
                val nftWrapper = NftWrapper(
                    nftEntry.nft.tokenId ?: "",
                    nftEntry.nft.consumed ?: true,
                    approved,
                    nftEntry.contract,
                    WalletConnector.walletAddress ?: ""
                )
                startActivity<NftDetailActivity>(
                    NftDetailActivity.KEY_NFT_WRAPPER to nftWrapper,
                )
            }
        }
        mAdapter.register(NftEntry::class, binder)
        mRvNft.adapter = mAdapter
        (mRvNft.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        mTvDisconnect.setOnClickListener {
            disconnectWallet()
        }
        RxUtil.clicks(mTvApprove)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                approve()
            }
        mTvActivate.setOnClickListener {
            activateAndRegister()
        }

        mTvSwitchWallet.setOnClickListener {
            WalletConnector.disconnect()
            WalletConnector.connect()
        }
        mTvWhereToBug.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                Uri.parse("iopay://io.iotex.iopay/open?action=web&url=https://metapebble.app/faucet")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            applicationContext.startActivity(intent)
        }
    }

    private fun disconnectWallet() {
        DisconnectDialog(this)
            .setTitle(getString(R.string.disconnect_wallet))
            .setContent(getString(R.string.disconnect_wallet_warning))
            .setPositiveButton(getString(R.string.disconnect)) {
                WalletConnector.disconnect()
                this.onBackPressed()
            }.show()
    }

    private fun approve() {
        PromptDialog(this)
            .setTitle(getString(R.string.approve))
            .setContent(getString(R.string.sign_wallet_tips))
            .setPositiveButton(getString(R.string.confirm)) {
                if (mDevice != null && mSelectedNft != null) {
                    mActivateVM.approveRegistration(mSelectedNft?.nft?.tokenId ?: "")
                }
            }
            .show()
    }

    private fun activateAndRegister() {
        PromptDialog(this)
            .setTitle(getString(R.string.activating_tips))
            .setContent(getString(R.string.sign_wallet_tips))
            .setPositiveButton(getString(R.string.confirm)) {
                if (mDevice != null && mSelectedNft != null) {
                    mActivateVM.signDevice(mDevice!!)
                    mProgress.show()
                }
            }
            .show()
    }

    override fun initData(savedInstanceState: Bundle?) {
        queryNftList()
    }

    private fun queryNftList() {
        val address = WalletConnector.walletAddress
        if (!address.isNullOrBlank()) {
            mPebbleVM.queryNftList(AddressUtil.convertIoAddress(address))
        }
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
        mPebbleVM.mNftListLD.observe(this) {
            mSrlContent.isRefreshing = false
            if (!it.isNullOrEmpty()) {
                mTlContentContainer.visible()
                mRlEmptyContainer.gone()
                mAdapter.items = it
                mAdapter.notifyDataSetChanged()
            } else {
                mTlContentContainer.gone()
                mRlEmptyContainer.visible()
            }
        }
        mActivateVM.mApproveLd.observe(this) { tokenId ->
            if (!tokenId.isNullOrBlank()) {
                val item = mAdapter.items.firstOrNull { item ->
                    if (item is NftEntry) {
                        item.nft.tokenId == tokenId
                    } else {
                        false
                    }
                } as? NftEntry
                mAdapter.updateItem(item) { nft ->
                    nft?.nft?.tokenId == item?.nft?.tokenId
                }
            }
            if (mSelectedNft?.nft?.tokenId == tokenId) {
                mTvApprove.gone()
                mTvActivate.visible()
            }
        }
        mActivateVM.mSignDeviceLD.observe(this) {
            mProgress.dismiss()
            if (it != null) {
                mDevice?.let { device ->
                    val tokenId = mSelectedNft!!.nft.tokenId.toString()
                    mActivateVM.activateMetaPebble(
                        tokenId,
                        device.pubKey,
                        it.imei,
                        it.sn,
                        it.timestamp.toString(),
                        it.authentication
                    )
                }
            }
        }
        mActivateVM.mActivateLd.observe(this) {
            activateProgress()
        }
    }
}