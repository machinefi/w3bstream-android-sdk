package io.iotex.pebble.module.walletconnect.api

import io.iotex.pebble.module.walletconnect.common.WalletConnectKitModule
import io.iotex.pebble.module.walletconnect.repository.session.SessionManager
import io.iotex.pebble.module.walletconnect.repository.wallet.WalletManager

class WalletConnectKit private constructor(
    sessionManager: SessionManager,
    walletManager: WalletManager,
) : SessionManager by sessionManager, WalletManager by walletManager {

    class Builder(config: WalletConnectKitConfig) {

        private val walletConnectKitModule = WalletConnectKitModule(config.context, config)

        fun build() = WalletConnectKit(
            walletConnectKitModule.sessionRepository, walletConnectKitModule.walletRepository
        )
    }
}