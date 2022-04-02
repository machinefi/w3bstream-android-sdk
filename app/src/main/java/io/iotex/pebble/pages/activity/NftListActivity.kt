package io.iotex.pebble.pages.activity

import android.os.Bundle
import com.drakeet.multitype.MultiTypeAdapter
import io.iotex.core.base.BaseActivity
import io.iotex.pebble.R
import io.iotex.pebble.module.walletconnect.WcKit
import io.iotex.pebble.pages.binder.NftEntry
import io.iotex.pebble.pages.binder.NftItemBinder
import io.iotex.pebble.pages.fragment.LoadingFragment
import io.iotex.pebble.utils.extension.ellipsis
import kotlinx.android.synthetic.main.activity_nft_list.*
import org.jetbrains.anko.startActivity

class NftListActivity: BaseActivity(R.layout.activity_nft_list) {

    private val mAdapter = MultiTypeAdapter()

    private var mSelectedNft: NftEntry? = null

    override fun initView(savedInstanceState: Bundle?) {
        mTvAddress.text = WcKit.walletAddress()?.ellipsis(6, 6)
        val binder = NftItemBinder().apply {
            setOnSelectedListener {
                mSelectedNft = it
                mTvActivate.isEnabled = true
            }
            setOnItemClickListener {
                startActivity<NftDetailActivity>()
            }
        }
        mAdapter.register(NftEntry::class, binder)
        mRvNft.adapter = mAdapter
        mTvDisconnect.setOnClickListener {
            WcKit.disconnect()
            this.onBackPressed()
        }
        mTvActivate.setOnClickListener {
            activateAndRegister()
        }
    }

    private fun activateAndRegister() {
        val step02 = LoadingFragment()
            .renderTitle(getString(R.string.registering_metapebble), getString(R.string.meta_pebble))
            .setCompleteCallback {
                startActivity<ActivateCompleteActivity>()
                finish()
            }

        LoadingFragment()
            .renderTitle(getString(R.string.activating_metapebble), getString(R.string.meta_pebble))
            .setCompleteCallback {
                step02.show(supportFragmentManager, R.id.mRlContainer)
            }
            .show(supportFragmentManager, R.id.mRlContainer)
    }

    override fun initData(savedInstanceState: Bundle?) {
        val nftList = listOf(NftEntry("No.12"), NftEntry("No.283"), NftEntry("No.300"))
        mAdapter.items = nftList
        mAdapter.notifyDataSetChanged()
    }

    override fun registerObserver() {
    }

    companion object {
        const val KEY_WALLET_ADDRESS = "key_wallet_address"
    }
}