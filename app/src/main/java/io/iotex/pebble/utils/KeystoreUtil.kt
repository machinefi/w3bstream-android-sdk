package io.iotex.pebble.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import io.iotex.pebble.utils.extension.e
import io.iotex.pebble.utils.extension.toHexString
import org.bouncycastle.asn1.DERBitString
import org.bouncycastle.asn1.DERSequence
import org.web3j.utils.Numeric
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.Signature
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec


const val ANDROID_KEY_STORE = "AndroidKeyStore"
const val PEBBLE_KEYSTORE_ALIAS = "PEBBLE"

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

        kpg.generateKeyPair().public.encoded
    }

    fun signData(data: ByteArray): ByteArray? {
        val ks = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
            load(null)
        }

        val entry = ks.getEntry(PEBBLE_KEYSTORE_ALIAS, null)
        if (entry !is KeyStore.PrivateKeyEntry) {
            "Not an instance of a PrivateKeyEntry".e()
            return null
        }
        val signature = Signature.getInstance("SHA256withECDSA").run {
            initSign(entry.privateKey)
            update(data)
            sign()
        }
        return signature
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
        return "0x" + subjectPublicKey.bytes.toHexString().substring(4)
    }
}