package com.machinefi.pebblekit.repository.device

import com.blankj.utilcode.util.SPUtils
import com.machinefi.pebblekit.constant.SP_KEY_IMEI
import com.machinefi.pebblekit.constant.SP_KEY_sn
import com.machinefi.pebblekit.constant.SP_NAME
import com.machinefi.pebblekit.uitls.KeystoreUtil
import com.machinefi.pebblekit.uitls.RandomUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class DeviceRepository: DeviceManager {

    override suspend fun createDevice() = withContext(Dispatchers.IO) {
        KeystoreUtil.createPk()
        val pubKey = KeystoreUtil.getPubKey()
        if (pubKey.isNullOrBlank()) {
            throw Exception("Failed to create MetaPebble")
        }
        val imei = imei()
        val sn = sn()
        SPUtils.getInstance(SP_NAME).put(SP_KEY_IMEI, imei)
        SPUtils.getInstance(SP_NAME).put(SP_KEY_sn, sn)
        return@withContext Device(imei, sn, pubKey)
    }

    internal fun imei(): String {
        return "100${RandomUtil.number(LEN_IMEI - 3)}"
    }

    internal fun sn(): String {
        return RandomUtil.string(LEN_SN)
    }

}