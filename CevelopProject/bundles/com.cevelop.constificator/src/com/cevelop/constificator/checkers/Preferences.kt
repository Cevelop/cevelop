package com.cevelop.constificator.checkers;

import org.eclipse.osgi.util.NLS;


class Preferences : NLS() {

	data class Preference<T>(val key: String, val description: String, val default: T)

	companion object {
		private const val NLS_BUNDLE_NAME = "com.cevelop.constificator.checkers.preferences"

		const val CHECK_DATA_MEMBERS_KEY = "checkDataMembers"
		const val CHECK_FUNCTION_PARAMETERS_KEY = "checkFunctionParameters"
		const val CHECK_LOCAL_VARIABLES_KEY = "checkLocalVariables"
		const val CHECK_MEMBER_FUNCTIONS_KEY = "checkMemberFunctions"

		val ALL_CHECK_OPTION_KEYS = arrayOf(
				CHECK_LOCAL_VARIABLES_KEY,
				CHECK_FUNCTION_PARAMETERS_KEY,
				CHECK_DATA_MEMBERS_KEY,
				CHECK_MEMBER_FUNCTIONS_KEY
		)

		lateinit var CheckDataMembers: String
		lateinit var CheckFunctionParameters: String
		lateinit var CheckLocalVariables: String
		lateinit var CheckMemberFunctions: String

		val preferences: List<Preference<Boolean>>

		init {
			NLS.initializeMessages(NLS_BUNDLE_NAME, Preferences::class.java);
			preferences = listOf(
					Preference(CHECK_LOCAL_VARIABLES_KEY, CheckLocalVariables, true),
					Preference(CHECK_FUNCTION_PARAMETERS_KEY, CheckFunctionParameters, true),
					Preference(CHECK_MEMBER_FUNCTIONS_KEY, CheckMemberFunctions, true),
					Preference(CHECK_DATA_MEMBERS_KEY, CheckDataMembers, true)
			)
		}

		fun iterator() = preferences.iterator()
	}

}
