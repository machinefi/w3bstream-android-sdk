package io.iotex.pebble.module.walletconnect.api

import android.content.Context
import org.walletconnect.Session

class WalletConnectKitConfig(
    val context: Context,
    val bridgeUrl: String,
    private val appUrl: String,
    private val appName: String,
    private val appDescription: String,
    private val appIcons: List<String>? = listOf(),
) {
    internal val clientMeta by lazy { Session.PeerMeta(appUrl, appName, appDescription, appIcons) }
}