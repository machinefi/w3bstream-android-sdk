package io.iotex.pebble.pages

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import dev.pinkroom.walletconnectkit.WalletConnectKit
import dev.pinkroom.walletconnectkit.WalletConnectKitConfig
import io.iotex.core.base.BaseActivity
import io.iotex.pebble.R
import io.iotex.pebble.utils.FunctionSignDataUtil
import io.iotex.pebble.utils.extension.setGone
import io.iotex.pebble.utils.extension.setVisible
import io.iotex.pebble.utils.extension.toast
import kotlinx.android.synthetic.main.activity_wallet_connect.*
import kotlinx.coroutines.*
import org.walletconnect.Session
import org.web3j.protocol.core.DefaultBlockParameterName
import java.math.BigInteger

class WalletConnectActivity: BaseActivity(R.layout.activity_wallet_connect) {

    private val mConfig by lazy {
        WalletConnectKitConfig(
            context = this,
            bridgeUrl = "wss://bridge.aktionariat.com:8887",
            appUrl = "walletconnectkit.com",
            appName = "WalletConnect Kit",
            appDescription = "This is the Swiss Army toolkit for WalletConnect!"
        )
    }

    private val mWalletConnectKit by lazy { WalletConnectKit.Builder(mConfig).build() }

    override fun initView(savedInstanceState: Bundle?) {
        mWcBtn.start(mWalletConnectKit, ::onConnected, ::onDisconnected)
        initPerformTransactionView()
        mWcDisconnectBtn.setOnClickListener {
            mWalletConnectKit.removeSession()
        }
    }

    private fun onConnected(address: String) {
        mWcBtn.setGone()
        mLlContent.setVisible()
        mWcDisconnectBtn.setVisible()
        mTvAddress.text = "Connected with: $address"
    }

    private fun onDisconnected() {
        mWcDisconnectBtn.setGone()
        mWcBtn.setVisible()
        mLlContent.setGone()
    }

    private fun initPerformTransactionView() {
        performTransactionView.setOnClickListener {
            val toAddress = mEtToAddress.text.toString()
            val value = mEtValue.text.toString()
            lifecycleScope.launch {
                runCatching {
                    withContext(Dispatchers.IO) {
                        val data = FunctionSignDataUtil.getTransferSignData(toAddress, value, 18)
                        val nonce = FunctionSignDataUtil.getNonce(mWalletConnectKit.address ?: "")
                        val gasPrice = FunctionSignDataUtil.estimateGasPrice()
                        val ret = mWalletConnectKit.performTransaction(toAddress, value, data, nonce?.toString(), gasPrice, "1000000")
                    }
                }
                    .onSuccess { "Transaction done!".toast() }
                    .onFailure {
                        (it.message ?: it.toString()).toast()
                        it.printStackTrace()
                    }
            }
        }
    }


    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun registerObserver() {
    }
}