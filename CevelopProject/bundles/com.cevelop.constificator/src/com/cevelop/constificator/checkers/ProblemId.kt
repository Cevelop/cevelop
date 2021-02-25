package com.cevelop.constificator.checkers

import java.util.Optional
import com.cevelop.constificator.core.util.type.Truelean
import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId

enum class ProblemId private constructor(private val id: String) : IProblemId<ProblemId> {
	MISSING_QUALIFICATION(Markers.MissingQualification),
	POTENTIALLY_MISSING_QUALIFICATION(Markers.PotentiallyMissingQualification);

	override fun getId() = id
	
	override fun unstringify(string: String?): ProblemId? {
		return of(string)
	}

	override fun stringify(): String? {
		return id
	}

	companion object {
		fun of(string: String?): ProblemId? {
			for (problemId in values()) {
				if (problemId.id.equals(string)) {
					return problemId
				}
			}
			throw IllegalArgumentException("Illegal ProblemId: " + string!!)
		}

		fun of(decision: Truelean): Optional<ProblemId> {
			when (decision) {
				Truelean.YES -> return Optional.of(MISSING_QUALIFICATION)
				Truelean.MAYBE -> return Optional.of(POTENTIALLY_MISSING_QUALIFICATION)
				else -> return Optional.empty()
			}
		}
	}
}