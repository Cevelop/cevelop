package com.cevelop.constificator.checkers

import org.eclipse.cdt.codan.core.model.IProblem
import org.eclipse.cdt.codan.core.param.IProblemPreferenceCompositeDescriptor
import com.cevelop.constificator.core.util.type.Cast
import com.cevelop.constificator.checkers.Preferences

data class Configuration(
        val isEnabled: Boolean,
        val checkDataMembers: Boolean,
        val checkFunctionParameters: Boolean,
        val checkLocalVariables: Boolean,
        val checkMemberFunctions: Boolean) {

    companion object {
        fun of(problem: IProblem): Configuration {
            val preference = (problem.getPreference() as? IProblemPreferenceCompositeDescriptor)
                    ?: throw RuntimeException("Failed to read configuration for problem ${problem.id}")
            return Configuration(
                    problem.isEnabled,
                    preference.getChildDescriptor(Preferences.CHECK_DATA_MEMBERS_KEY).getValue() as Boolean,
                    preference.getChildDescriptor(Preferences.CHECK_FUNCTION_PARAMETERS_KEY).getValue() as Boolean,
                    preference.getChildDescriptor(Preferences.CHECK_LOCAL_VARIABLES_KEY).getValue() as Boolean,
                    preference.getChildDescriptor(Preferences.CHECK_MEMBER_FUNCTIONS_KEY).getValue() as Boolean
            )
        }
    }

}