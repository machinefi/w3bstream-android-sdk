package io.iotex.pebble.utils

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.Utils
import io.iotex.pebble.R
import io.iotex.pebble.module.keystore.KeystoreFile
import io.iotex.pebble.module.keystore.KeystoreUtils
import io.iotex.pebble.module.keystore.Numeric
import io.iotex.pebble.utils.extension.toast
import org.passay.CharacterRule
import org.passay.EnglishCharacterData
import org.passay.PasswordGenerator
import java.io.File
import java.security.*
import java.security.spec.MGF1ParameterSpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

const val ANDROID_KEY_STORE = "AndroidKeyStore"
const val PEBBLE_KEYSTORE_ALIAS = "PEBBLE"
const val TRANSFORMATION_CIPHER = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"

object KeyStoreUtil {

    fun initKeyStore() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            showRestrictedDialog()
            return
        }
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)
        if (!keyStore.containsAlias(PEBBLE_KEYSTORE_ALIAS)) {
            val keyGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA,
                ANDROID_KEY_STORE
            )
            val builder = KeyGenParameterSpec.Builder(
                PEBBLE_KEYSTORE_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
            keyGenerator.initialize(builder.build())
            keyGenerator.generateKeyPair()
        }
    }

    fun encodePassword(password: String): String {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)
        if (!keyStore.containsAlias(PEBBLE_KEYSTORE_ALIAS)) {
            Utils.getApp().getString(R.string.keystore_not_init).toast()
            return ""
        }

        val certificate = keyStore.getCertificate(PEBBLE_KEYSTORE_ALIAS)
        if (certificate == null) {
            Utils.getApp().getString(R.string.could_not_load_certificate).toast()
            return ""
        }

        val key = certificate.publicKey
        val unrestrictedPublicKey = KeyFactory.getInstance(key.algorithm).generatePublic(
            X509EncodedKeySpec(key.encoded)
        )
        val spec = OAEPParameterSpec(
            "SHA-256", "MGF1",
            MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT
        )
        val cipher = Cipher.getInstance(TRANSFORMATION_CIPHER)
        cipher.init(Cipher.ENCRYPT_MODE, unrestrictedPublicKey, spec)
        return Base64.encodeToString(cipher.doFinal(password.toByteArray()), Base64.NO_WRAP)
    }

    fun decodePassword(password: String): String {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)
        lateinit var privateKey: PrivateKey
        if (keyStore.containsAlias(PEBBLE_KEYSTORE_ALIAS)) {
            privateKey = keyStore.getKey(PEBBLE_KEYSTORE_ALIAS, null) as PrivateKey
        } else {
            val keyGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA,
                ANDROID_KEY_STORE
            )
            val builder = KeyGenParameterSpec.Builder(
                PEBBLE_KEYSTORE_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
            keyGenerator.initialize(builder.build())
            keyGenerator.generateKeyPair()
            val keyStore2 = KeyStore.getInstance(ANDROID_KEY_STORE)
            keyStore2.load(null)
            privateKey = keyStore2.getKey(PEBBLE_KEYSTORE_ALIAS, null) as PrivateKey
        }
        val cipher = Cipher.getInstance(TRANSFORMATION_CIPHER)
        val spec = OAEPParameterSpec("SHA-256", "MGF1",
            MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT)
        cipher.init(Cipher.DECRYPT_MODE, privateKey, spec)
        val bytes = Base64.decode(password, Base64.NO_WRAP)
        return String(cipher.doFinal(bytes))
    }

    fun createRandomPassword(): String {
        val rand = Random(System.currentTimeMillis())
        val generator = PasswordGenerator(rand)
        return generator.generatePassword(
            10,
            CharacterRule(EnglishCharacterData.Alphabetical),
            CharacterRule(EnglishCharacterData.Digit),
            CharacterRule(EnglishCharacterData.Special)
        )
    }

    private fun showRestrictedDialog() {
        AlertDialog.Builder(ActivityUtils.getTopActivity())
            .setMessage(R.string.device_must_api_level)
            .setPositiveButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun resolvePrivateKey(encodedPwd: String, hash: String): String {
        val pwd = decodePassword(encodedPwd)
        val keystore = getKeystoreFile(hash)
        val pk = KeystoreUtils.loadKeyFromWalletFile(pwd, keystore)
        val pkBytes = Numeric.toBytesPadded(pk, 32)
        return Numeric.toHexString(pkBytes)
    }

    fun saveKeystoreFile(keystoreFile: KeystoreFile) {
        val path = Utils.getApp().filesDir.path + File.separator + keystoreFile.id
        val exist = FileUtils.createOrExistsFile(path)
        if (exist) {
            FileIOUtils.writeFileFromString(path, keystoreFile.toJsonString())
        }
    }

    fun getKeystoreFile(id: String): String {
        val path = Utils.getApp().filesDir.path + File.separator + id
        return FileIOUtils.readFile2String(path)
    }

}