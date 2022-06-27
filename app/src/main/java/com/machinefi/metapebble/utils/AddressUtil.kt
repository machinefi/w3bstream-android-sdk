package com.machinefi.metapebble.utils

import com.machinefi.metapebble.module.walletconnect.WalletConnector
import com.machinefi.metapebble.utils.extension.cleanHexPrefix
import com.machinefi.metapebble.utils.extension.toHexByteArray
import com.machinefi.metapebble.utils.extension.toHexString
import org.web3j.utils.Numeric
import java.math.BigInteger

object AddressUtil {

    fun getIoWalletAddress(): String {
        return convertIoAddress(WalletConnector.walletAddress ?: "")
    }

    fun convertWeb3Address(address: String): String {
        if (address.startsWith("0x")) return address
        runCatching {
            val dec = com.machinefi.metapebble.utils.Bech32.decode(address).data
            return com.machinefi.metapebble.utils.Bech32.convertBits(dec, 0, dec.size, 5, 8, false).toHexString()
        }.getOrElse { return address }
    }

    fun convertIoAddress(address: String): String {
        if (!address.startsWith("0x")) return address
        val byteData = address.substring(2, address.length).toHexByteArray()

        val grouped = com.machinefi.metapebble.utils.Bech32.convertBits(
            byteData,
            0,
            byteData.size,
            8,
            5,
            true
        )
        return com.machinefi.metapebble.utils.Bech32.encode("io", grouped)
    }

    fun isValidAddress(address: String): Boolean {
        try {
            if (address.isBlank()) {
                return false
            }

            val ethContract = convertWeb3Address(address)

            if (ethContract.isBlank() || !ethContract.startsWith("0x"))
                return false

            val cleanInput = ethContract.cleanHexPrefix()

            val value = Numeric.toBigIntNoPrefix(cleanInput)

            if (value == BigInteger.ZERO) return false

            return cleanInput.length == 40
        } catch (e: Exception) {
            return false
        }
    }
}