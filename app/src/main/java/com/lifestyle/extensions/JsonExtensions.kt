package com.lifestyle.extensions

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * USAGE:   import com.lifestyle.extensions.*
 */

// enables --> for (jsonObj in jsonArr)
@Throws(JSONException::class)
operator fun JSONArray.iterator() {
    (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()
}

// enables --> jsonArr.forEach { jsonObj -> ... }
@Throws(JSONException::class)
inline fun JSONArray.forEach(action: (JSONObject) -> Unit) {
    (0 until length()).forEach { action(get(it) as JSONObject) }
}
