package io.iotex.pebble.module.walletconnect

import com.blankj.utilcode.util.Utils
import dev.pinkroom.walletconnectkit.WalletConnectKit
import dev.pinkroom.walletconnectkit.WalletConnectKitConfig
import io.iotex.pebble.utils.extension.i
import org.walletconnect.Session

object WcKit {

    private val mConfig by lazy {
        WalletConnectKitConfig(
            context = Utils.getApp(),
            bridgeUrl = "wss://bridge.aktionariat.com:8887",
            appUrl = "walletconnectkit.com",
            appName = "MetaPebble",
            appDescription = "This is the Swiss Army toolkit for WalletConnect!"
        )
    }

    val mWalletConnectKit by lazy { WalletConnectKit.Builder(mConfig).build() }

    var mIsConnected = false

    fun walletAddress(): String? {
        return mWalletConnectKit.address
    }

    fun isConnected(): Boolean {
        return mWalletConnectKit.isSessionStored && mIsConnected
    }

    fun connect(cb: Session.Callback) {
        if (mWalletConnectKit.isSessionStored) {
            mWalletConnectKit.removeSession()
        }
        mWalletConnectKit.createSession(object : Session.Callback {
            override fun onMethodCall(call: Session.MethodCall) {
                cb.onMethodCall(call)
                when(call) {
                    is Session.MethodCall.Response -> {
                    }
                }
            }

            override fun onStatus(status: Session.Status) {
                cb.onStatus(status)
                "status $status".i()
                when(status) {
                    is Session.Status.Approved -> {
                        mIsConnected = true
                    }
                    is Session.Status.Closed -> {
                        mIsConnected = false
                    }
                }
            }
        })
    }

    fun disconnect() {
        mWalletConnectKit.removeSession()
        mIsConnected = false
    }

}