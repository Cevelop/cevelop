package com.cevelop.constificator.core.util.structural

import org.eclipse.cdt.core.dom.ast.IASTNode

val IASTNode.grandParent get() = this.parent?.parent

/**
 * Return this nodes grandparent if it matches the given type, <code>null</code> otherwise
 */
inline fun <reified T: IASTNode> IASTNode.typedGrandparent() =  this.parent?.parent as? T

/**
 * Check if this node has a grandparent of the given type
 */
inline fun <reified T: IASTNode> IASTNode.hasTypedGrandparent() = this.typedGrandparent<T>() != null

/**
 * Check if this node descends from a node with the given type
 */
inline fun <reified T: IASTNode> IASTNode.descendsFrom() = Relation.isDescendendOf(T::class.java, this)