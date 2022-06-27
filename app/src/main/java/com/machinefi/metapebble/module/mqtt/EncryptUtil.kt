package com.machinefi.metapebble.module.mqtt

import android.os.Build
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import java.io.BufferedInputStream
import java.io.InputStream
import java.io.InputStreamReader
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