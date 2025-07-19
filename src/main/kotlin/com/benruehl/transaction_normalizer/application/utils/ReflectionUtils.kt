package com.benruehl.transaction_normalizer.application.utils

import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

fun <T : Any, V> setDataclassProperty(
    instance: T,
    property: KProperty1<T, V>,
    newValue: V
): T {
    val kClass = instance::class
    require(kClass.isData) { "Only works for data classes." }

    // Get the copy method
    val copy = kClass.memberFunctions.first { it.name == "copy" }

    // Map all property names to their value, replacing the one we want to update
    val args = kClass
        .memberProperties.map { it as KProperty1<T, *> }
        .associate {
            val value = if (it.name == property.name) newValue else it.get(instance)
            it.name to value
        }

    // Build argument map for callBy: parameter -> value
    val parameterMap = copy.parameters.associateWith { param ->
        // Parameter "name" corresponds to property "name"
        args[param.name] ?: instance // for the instance parameter
    }

    // Call the copy method with the updated arguments
    return copy.callBy(parameterMap) as T
}