package io.iotex.pebble.module.encryption

import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.Utils
import io.iotex.pebble.R
import io.iotex.pebble.utils.extension.i
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class EncryptionUtil {


    fun encrypt() {
        val rawData = Utils.getApp().resources.openRawResource(R.raw.cert).bufferedReader().readText()
        rawData.i()
        val data01 = rawData.replace("-----BEGIN PUBLIC KEY-----", "")
            .replace(System.lineSeparator(), "")
            .replace("-----END PUBLIC KEY-----", "")

        val data02 = Utils.getApp().resources.assets.open("data.pem").bufferedReader().readText()





    }





}