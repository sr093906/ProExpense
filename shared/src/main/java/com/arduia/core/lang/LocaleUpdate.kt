package com.arduia.core.lang

import android.content.Context
import android.content.res.Configuration
import java.util.*

/**
 * To change locale of context - language changes
 */
fun Context.updateResource(language: String):Context{
    val locale = Locale(language)
    Locale.setDefault(locale)
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    return createConfigurationContext(config)
}
