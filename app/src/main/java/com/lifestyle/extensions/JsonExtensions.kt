package com.lifestyle.extensions

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * USAGE:   import com.lifestyle.extensions.*
 */

@Throws(JSONException::class)
operator fun JSONArray.iterator() {
    (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()
}

@Throws(JSONException::class)
inline fun JSONArray.forEach(action: (JSONObject) -> Unit) {
    (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()
        .forEach { action(it as JSONObject) }
}
