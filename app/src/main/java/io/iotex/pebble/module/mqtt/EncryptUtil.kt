package io.iotex.pebble.module.mqtt

import android.os.Build
import com.blankj.utilcode.util.TimeUtils
import com.google.protobuf.ByteString
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.utils.KeystoreUtil
import io.iotex.pebble.utils.RandomUtil
import io.iotex.pebble.utils.extension.toHexByteArray
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import wallet.core.jni.Hash
import java.io.BufferedInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.math.BigDecimal
import java.security.KeyFactory
import java.security.KeyStore
import java.security.Security
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory


object EncryptUtil {

    @Throws(Exception::class)
    fun getSocketFactory(
        caPem: InputStream,
        certPem: InputStream,
        keyPem: InputStream,
        pws: String
    ): SSLSocketFactory {
        Security.addProvider(BouncyCastleProvider())

        // load CA certificate
        var caCert: X509Certificate? = null
        var bis = BufferedInputStream(caPem)
        val cf = CertificateFactory.getInstance("X.509")
        while (bis.available() > 0) {
            caCert = cf.generateCertificate(bis) as X509Certificate
        }

        // load client certificate
        bis = BufferedInputStream(certPem)
        var cert: X509Certificate? = null
        while (bis.available() > 0) {
            cert = cf.generateCertificate(bis) as X509Certificate
        }

        // load client private cert
        val pemParser = PEMParser(InputStreamReader(keyPem))
        val `object` = pemParser.readObject()
        val keyFactory: KeyFactory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // fix BC provider has been removed after Android P
            KeyFactory.getInstance("RSA")
        } else {
            KeyFactory.getInstance("RSA", "BC")
        }
        val converter = JcaPEMKeyConverter().setProvider(keyFactory.provider)
        val key = converter.getKeyPair(`object` as PEMKeyPair)
        val caKs = KeyStore.getInstance(KeyStore.getDefaultType())
        caKs.load(null, null)
        caKs.setCertificateEntry("cert-certificate", caCert)
        val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        tmf.init(caKs)
        val ks = KeyStore.getInstance(KeyStore.getDefaultType())
        ks.load(null, null)
        ks.setCertificateEntry("certificate", cert)
        ks.setKeyEntry(
            "private-cert",
            key.private,
            pws.toCharArray(),
            arrayOf<Certificate?>(cert)
        )
        val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        kmf.init(ks, pws.toCharArray())
        val context = SSLContext.getInstance("TLSv1.2")
        context.init(kmf.keyManagers, tmf.trustManagers, null)
        return context.socketFactory
    }

    fun signMessage(device: DeviceEntry, lat: Long, long: Long): ByteArray {
        val bd = TimeUtils.getNowMills().toBigDecimal().div(BigDecimal.TEN.pow(3))
        val timestampStr = bd.setScale(0, BigDecimal.ROUND_DOWN)
        val timestampBytes = Integer.toHexString(timestampStr.toInt()).toHexByteArray()
        val random = RandomUtil.integer(10000, 99999)
        val data = SensorProtoData.SensorData.newBuilder()
            .setSnr(1024)
            .setLatitude(lat.toInt())
            .setLongitude(long.toInt())
            .setRandom(random.toString())
            .build().toByteArray()

        val typeData = Integer.toHexString(SensorProtoData.BinPackage.PackageType.DATA.number).toHexByteArray()
        val result = concat(setLength(typeData, 4), data, setLength(timestampBytes, 4))

        val hash = Hash.sha256(result)
        val signatureData = KeystoreUtil.signData(hash)
        return SensorProtoData.BinPackage.newBuilder()
            .setType(SensorProtoData.BinPackage.PackageType.DATA)
            .setData(ByteString.copyFrom(data))
            .setSignature(ByteString.copyFrom(signatureData))
            .setTimestamp(timestampStr.toInt())
            .build().toByteArray()
    }

    fun setLength(msg: ByteArray, length: Int, right: Boolean = false): ByteArray {
        val result = ByteArray(length)
        val salt = ByteArray(length)
        if (right) {
            if (msg.size < length) {
                System.arraycopy(msg, 0, result, 0, msg.size)
                System.arraycopy(salt, 0, result, msg.size, salt.size - msg.size)
                return result
            }
            return msg
        } else {
            if (msg.size < length) {
                System.arraycopy(salt, 0, result, 0, salt.size - msg.size)
                System.arraycopy(msg, 0, result, salt.size - msg.size, msg.size)
                return result
            }
            return msg
        }
    }

    fun concat(vararg srcList: ByteArray): ByteArray {
        val length = srcList.map { it.size }.sum()
        val result = ByteArray(length)
        var cursor = 0
        srcList.forEach {
            System.arraycopy(it, 0, result, cursor, it.size)
            cursor += it.size
        }
        return result
    }

}