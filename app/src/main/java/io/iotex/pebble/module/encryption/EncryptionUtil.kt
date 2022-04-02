package io.iotex.pebble.module.encryption

import com.blankj.utilcode.util.*
import io.iotex.pebble.R
import io.iotex.pebble.utils.extension.i

class EncryptionUtil {


    fun encrypt() {
        val rawData = Utils.getApp().resources.openRawResource(R.raw.cert).bufferedReader().readText()
        rawData.i()
        val data01 = rawData.replace("-----BEGIN PUBLIC KEY-----", "")
            .replace(System.lineSeparator(), "")
            .replace("-----END PUBLIC KEY-----", "")

        val data02 = Utils.getApp().resources.assets.open("data.txt").bufferedReader().readText()

        AppUtils.getAppPackageName()



    }





}