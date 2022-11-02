package com.machinefi.w3bstream.repository.network.response

import kotlin.properties.Delegates

class PublishResult {
    var code by Delegates.notNull<Int>()
    lateinit var errMsg: String
    lateinit var instanceID: String
}