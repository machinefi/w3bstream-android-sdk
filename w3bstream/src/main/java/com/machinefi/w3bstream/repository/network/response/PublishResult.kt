package com.machinefi.w3bstream.repository.network.response

import kotlin.properties.Delegates

class PublishResult {
    lateinit var projectName: String
    lateinit var pubID: String
    lateinit var pubName: String
    lateinit var eventID: String
    lateinit var wasmResults: List<WasmResult>
}

class WasmResult {
    var code by Delegates.notNull<Long>()
    lateinit var instanceID: String
    lateinit var errMsg: String
}