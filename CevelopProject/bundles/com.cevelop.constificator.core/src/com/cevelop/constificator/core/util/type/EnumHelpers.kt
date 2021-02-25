package com.cevelop.constificator.core.util.type

import java.util.EnumSet

inline fun <reified T: Enum<T>> enumSetOf() = EnumSet.allOf(T::class.java)