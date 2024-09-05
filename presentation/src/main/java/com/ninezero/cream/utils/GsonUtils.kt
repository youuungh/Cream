package com.ninezero.cream.utils

import com.google.gson.Gson

object GsonUtils {
    fun toJson(value: Any?) : String {
        return Gson().toJson(value)
    }

    inline fun <reified T> fromJson(json: String?) : T? {
        return runCatching {
            Gson().fromJson(json, T::class.java)
        }.getOrNull()
    }
}