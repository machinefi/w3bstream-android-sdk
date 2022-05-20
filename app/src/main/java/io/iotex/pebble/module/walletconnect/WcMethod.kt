package io.iotex.pebble.module.walletconnect

enum class WcMethod(val value: String) {
    SIGN_TRANSACTION("eth_signTransaction"),
    PERSONAL_SIGN("personal_sign"),
    SIGN_TYPED_DATA("eth_signTypedData")
}