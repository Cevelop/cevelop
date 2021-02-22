package com.cevelop.constificator.core.util.semantic

import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod

val ICPPMethod.isMoveContructor get() = MemberFunction.isMoveConstructor(this)

val ICPPMethod.isMoveAssignmentOperator get() = MemberFunction.isMoveAssignmentOperator(this)