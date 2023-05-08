package com.machinefi.w3bstream.repository.network

import kotlin.properties.Delegates

class Response {
    lateinit var channel: String
    lateinit var publisherID: String
    lateinit var eventID: String
    var results: List<Result>? = null
    var error: String? = null
}

class Result {
    lateinit var appletName: String
    var code by Delegates.notNull<Int>()
    lateinit var handler: String
    lateinit var instanceID: String
    var returnValue: Any? = null
}