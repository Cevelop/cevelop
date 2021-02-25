package com.cevelop.constificator.core.util.semantic

import org.eclipse.cdt.core.dom.ast.IType

val IType.isPointer get() = Type.isPointer(this)

val IType.isReference get() = Type.isReference(this)

val IType.isArrayLike get() = Type.isArrayLike(this)

fun IType.isConst(pointerLevel: Int) = Type.isConst(this, pointerLevel)