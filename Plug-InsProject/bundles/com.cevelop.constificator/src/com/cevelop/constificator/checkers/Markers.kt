package com.cevelop.constificator.checkers

import org.eclipse.osgi.util.NLS

class Markers private constructor() : NLS() {
	companion object {
		private val BUNDLE_NAME = "com.cevelop.constificator.checkers.markers" //$NON-NLS-1$

		lateinit var MissingQualification: String
		lateinit var PotentiallyMissingQualification: String

		init {
			NLS.initializeMessages(BUNDLE_NAME, Markers::class.java)
		}
	}
}