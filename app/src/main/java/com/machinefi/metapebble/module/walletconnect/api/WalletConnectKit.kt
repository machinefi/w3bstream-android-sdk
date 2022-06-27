package com.machinefi.metapebble.module.walletconnect.api

import com.machinefi.metapebble.module.walletconnect.common.WalletConnectKitModule
import com.machinefi.metapebble.module.walletconnect.repository.session.SessionManager
import com.machinefi.metapebble.module.walletconnect.repository.wallet.WalletManager

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