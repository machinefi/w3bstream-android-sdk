package com.iotex.pebble.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.machinefi.metapebble.utils.extension.cleanHexPrefix
import com.machinefi.metapebble.utils.extension.toHexString
import org.bouncycastle.asn1.DERBitString
import org.bouncycastle.asn1.DERSequence
import org.web3j.abi.TypeEncoder
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.Signature
import java.security.spec.ECGenParameterSpec

const val ANDROID_KEY_STORE = "AndroidKeyStore"
const val PEBBLE_KEYSTORE_ALIAS = "pebble_key"

object KeystoreUtil {

    fun createPk() {
        val ks = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
            load(null)
        }

        if (ks.containsAlias(PEBBLE_KEYSTORE_ALIAS)) return

        val kpg = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC, ANDROID_KEY_STORE
        )

        val parameterSpec = KeyGenParameterSpec.Builder(
            PEBBLE_KEYSTORE_ALIAS,
            KeyProperties.PURPOSE_SIGN
        ).run {
            setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
            setDigests(KeyProperties.DIGEST_SHA256)
            build()
        }

        kpg.initialize(parameterSpec)
        kpg.generateKeyPair()
    }

    fun signData(data: ByteArray): String {
        val ks = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
            load(null)
        }

        val entry = ks.getEntry(PEBBLE_KEYSTORE_ALIAS, null)
        if (entry !is KeyStore.PrivateKeyEntry) {
            throw Exception("Not an instance of a PrivateKeyEntry")
        }

        val signedData = Signature.getInstance("SHA256withECDSA").run {
            initSign(entry.privateKey)
            update(data)
            sign()
        }

        return generateSignature(signedData.toHexString())
    }

    private fun generateSignature(msg: String): String {
        val sigStr = msg.cleanHexPrefix()
        if (sigStr.length < 8) throw Exception("Signature message is too short")
        val len = sigStr.substring(6, 8).toBigInteger(16)
            .times(BigInteger.valueOf(2)).toInt()
        val middle = sigStr.substring(8)
        val arg01 = middle.substring(0, len).toBigInteger(16)
        val arg02 = middle.substring(len + 4).toBigInteger(16)
        val arg01Encode = TypeEncoder.encodePacked(Uint256(arg01))
        val arg02Encode = TypeEncoder.encodePacked(Uint256(arg02))
        return Numeric.prependHexPrefix(arg01Encode + arg02Encode)
    }

    fun getPubKey(): String? {
        val ks = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
            load(null)
        }

        if (!ks.containsAlias(PEBBLE_KEYSTORE_ALIAS)) {
            createPk()
        }

        val pubKeyEncoded = ks.getCertificate(PEBBLE_KEYSTORE_ALIAS).publicKey?.encoded ?: return null
        val subjectPublicKey = DERSequence.getInstance(pubKeyEncoded).getObjectAt(1) as DERBitString
        return Numeric.prependHexPrefix(subjectPublicKey.bytes.toHexString().substring(4))
    }
}