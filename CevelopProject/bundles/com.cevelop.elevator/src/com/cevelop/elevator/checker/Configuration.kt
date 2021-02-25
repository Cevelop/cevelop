package com.cevelop.elevator.checker

import org.eclipse.cdt.codan.core.model.IProblem
import org.eclipse.cdt.codan.core.param.IProblemPreferenceCompositeDescriptor
import org.eclipse.osgi.util.NLS

class Preferences : NLS() {
    data class Preference<T>(val key: String, val description: String, val default: T)

    companion object {
        private const val NLS_BUNDLE_NAME = "com.cevelop.elevator.checker.preferences"

        const val MARK_EQUALS_INITIALIZERS = "markEqualsInitializers"

        val ALL_PREFERENCE_KEYS = arrayOf(
            MARK_EQUALS_INITIALIZERS
        )

        lateinit var MarkEqualsInitializers: String

        val all: Map<String, Preference<*>>

        init {
            initializeMessages(NLS_BUNDLE_NAME, Preferences::class.java)
            all = mapOf(
                MARK_EQUALS_INITIALIZERS to Preference(MARK_EQUALS_INITIALIZERS, MarkEqualsInitializers, false)
            )
        }
    }
}

data class Configuration(val markEqualsInitializers: Boolean) {
    companion object {
        fun of(problem: IProblem): Configuration =
            (problem.getPreference() as? IProblemPreferenceCompositeDescriptor)?.let {
                Configuration(it.getChildDescriptor(Preferences.MARK_EQUALS_INITIALIZERS).getValue() as Boolean)
            } ?: throw RuntimeException("Failed to read configuration for ${problem.id}")
    }
}