package io.iotex.pebble.pages.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.SimpleItemAnimator
import com.drakeet.multitype.MultiTypeAdapter
import io.iotex.core.base.BaseActivity
import io.iotex.pebble.R
import io.iotex.pebble.constant.PebbleStore
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.module.viewmodel.ActivateVM
import io.iotex.pebble.module.viewmodel.PebbleVM
import io.iotex.pebble.module.walletconnect.WcKit
import io.iotex.pebble.pages.binder.NftEntry
import io.iotex.pebble.pages.binder.NftItemBinder
import io.iotex.pebble.pages.fragment.LoadingFragment
import io.iotex.pebble.utils.AddressUtil
import io.iotex.pebble.utils.extension.ellipsis
import io.iotex.pebble.utils.extension.setGone
import io.iotex.pebble.utils.extension.setVisible
import io.iotex.pebble.utils.extension.updateItem
import io.iotex.pebble.widget.DisconnectDialog
import kotlinx.android.synthetic.main.activity_nft_list.*
import org.jetbrains.anko.startActivity

class NftListActivity : BaseActivity(R.layout.activity_nft_list) {

    private val mPebbleVM by lazy {
        ViewModelProvider(this, mVmFactory)[PebbleVM::class.java]
    }
    private val mActivateVM by lazy {
        ViewModelProvider(this, mVmFactory)[ActivateVM::class.java]
    }

    private val mAdapter = MultiTypeAdapter()

    private var mSelectedNft: NftEntry? = null

    private val mDevice by lazy {
        PebbleStore.mDevice
    }

    override fun initView(savedInstanceState: Bundle?) {
        mTvAddress.text = AddressUtil.getIoWalletAddress().ellipsis(6, 6)
        val binder = NftItemBinder().apply {
            setOnSelectedListener {
                mSelectedNft = it
                mTvApprove.isEnabled = true
                mTvActivate.isEnabled = true
                if (AddressUtil.isValidAddress(it.nft.approved ?: "")) {
                    mTvApprove.setGone()
                    mTvActivate.setVisible()
                } else {
                    mTvApprove.setVisible()
                    mTvActivate.setGone()
                }
            }
            setOnItemClickListener { nftEntry ->
                startActivity<NftDetailActivity>(
                    NftDetailActivity.KEY_TOKEN_ID to nftEntry.nft.tokenId,
                    NftDetailActivity.KEY_CONTRACT to nftEntry.contract,
                    NftDetailActivity.KEY_WALLET_ADDRESS to WcKit.walletAddress(),
                )
            }
        }
        mAdapter.register(NftEntry::class, binder)
        mRvNft.adapter = mAdapter
        (mRvNft.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        mTvDisconnect.setOnClickListener {
            disconnectWallet()
        }
        mTvActivate.setOnClickListener {
            activateAndRegister()
        }
        mTvApprove.setOnClickListener {
            approve()
        }
    }

    private fun disconnectWallet() {
        DisconnectDialog(this)
            .setPositiveButton(getString(R.string.confirm)) {
                WcKit.disconnect()
                this.onBackPressed()
            }.show()
    }

    private fun approve() {
        if (mDevice != null && mSelectedNft != null) {
            mActivateVM.approveRegistration(mSelectedNft?.nft?.tokenId ?: "")
        }
    }

    private fun activateAndRegister() {
        if (mDevice != null && mSelectedNft != null) {
            mActivateVM.signDevice(mDevice!!)
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        val address = AddressUtil.getWalletAddress()
        if (address.isNotBlank()) {
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
            if (!it.isNullOrEmpty()) {
                mAdapter.items = it
                mAdapter.notifyDataSetChanged()
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
                mTvApprove.setGone()
                mTvActivate.setVisible()
            }
        }
        mActivateVM.mSignDeviceLD.observe(this) {
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