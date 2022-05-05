package io.iotex.pebble.utils

import io.iotex.pebble.module.keystore.Bech32
import io.iotex.pebble.module.keystore.Numeric
import io.iotex.pebble.module.walletconnect.WcKit
import java.math.BigInteger

object AddressUtil {

    fun getWalletAddress(): String {
        return WcKit.mWalletConnectKit.address ?: ""
    }

    fun getIoWalletAddress(): String {
        return convertIoAddress(getWalletAddress())
    }

    fun convertWeb3Address(address: String): String {
        if (address.startsWith("0x")) return address
        runCatching {
            val dec = Bech32.decode(address).data
            return "0x" + Numeric.toHexString(Bech32.convertBits(dec, 0, dec.size, 5, 8, false))
        }.getOrElse { return address }
    }

    fun convertIoAddress(address: String): String {
        if (!address.startsWith("0x")) return address
        val byteData = Numeric.hexStringToByteArray(address.substring(2, address.length))

        val grouped = Bech32.convertBits(
            byteData,
            0,
            byteData.size,
            8,
            5,
            true
        )
        return Bech32.encode("io", grouped)
    }

    fun isValidAddress(address: String): Boolean {
        try {
            if (address.isBlank()) {
                return false
            }

            val ethContract = convertWeb3Address(address)

            if (ethContract.isBlank() || !ethContract.startsWith("0x"))
                return false

            val cleanInput = org.web3j.utils.Numeric.cleanHexPrefix(ethContract)

            val value = org.web3j.utils.Numeric.toBigIntNoPrefix(cleanInput)

            if (value == BigInteger.ZERO) return false

            return cleanInput.length == 40
        } catch (e: Exception) {
            return false
        }
    }
}