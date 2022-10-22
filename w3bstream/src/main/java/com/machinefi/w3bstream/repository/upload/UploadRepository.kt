package com.machinefi.w3bstream.repository.upload

import com.blankj.utilcode.util.SPUtils
import com.machinefi.w3bstream.W3bStreamKitConfig
import com.machinefi.w3bstream.common.request.ApiService

internal class UploadRepository(
    apiService: ApiService,
    private val config: W3bStreamKitConfig
): UploadManager {

    private val httpsUploader = HttpsUploader(apiService, config)

    override fun uploadData(data: String, publisherKey: String, publisherToken: String) {
        httpsUploader.uploadData(data, publisherKey, publisherToken)
    }

    override fun addServerApi(api: String) {
        if (!config.innerServerApis.contains(api)) {
            config.innerServerApis.add(api)
            val serverApis = SPUtils.getInstance().getStringSet(KEY_SERVER_APIS).toMutableSet()
            serverApis.add(api)
            SPUtils.getInstance().put(KEY_SERVER_APIS, serverApis)
        }
    }

    override fun addServerApis(apis: List<String>) {
        apis.filter {
            !config.innerServerApis.contains(it)
        }.also {
            config.innerServerApis.addAll(it)
            val serverApis = SPUtils.getInstance().getStringSet(KEY_SERVER_APIS).toMutableSet()
            serverApis.addAll(apis)
            SPUtils.getInstance().put(KEY_SERVER_APIS, serverApis)
        }
    }

    override fun removeServerApi(api: String) {
        if (config.innerServerApis.contains(api)) {
            config.innerServerApis.remove(api)
        }
    }
}