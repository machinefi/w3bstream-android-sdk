package com.machinefi.w3bstream.utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.InputStream

object JsonUtil {

    private val objectMapper by lazy {
        ObjectMapper().apply {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        }
    }

    fun toJson(obj: Any): String {
        return objectMapper.writeValueAsString(obj)
    }

    fun <T> parseJson(json: String, type: TypeReference<T>): T {
        return objectMapper.readValue(json, type)
    }

    fun <T> parseJson(`is`: InputStream, type: TypeReference<T>): T {
        return objectMapper.readValue(`is`, type)
    }

}