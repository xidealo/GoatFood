@file:OptIn(ExperimentalForeignApi::class)

package com.bunbeauty.analytic

import com.bunbeauty.analytic.event.EventParameter
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSMutableDictionary
import platform.Foundation.NSString

internal fun List<EventParameter>.toNativeParameters(): Map<Any?, *>? {
    if (isEmpty()) {
        return null
    }

    val dictionary = NSMutableDictionary(capacity = size.toULong())
    forEach { parameter ->
        dictionary.setObject(parameter.value as NSString, forKey = parameter.key as NSString)
    }
    return dictionary as Map<Any?, *>
}
