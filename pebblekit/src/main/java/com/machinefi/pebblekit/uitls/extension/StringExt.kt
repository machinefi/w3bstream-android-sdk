package com.machinefi.pebblekit.uitls.extension

import com.blankj.utilcode.util.GsonUtils
import com.google.gson.Gson
import java.lang.Exception

fun String.isJsonValid(): Boolean {
    return try {
        Gson().fromJson("", Any::class.java)
        true
    } catch (e: Exception) {
        false
    }
}