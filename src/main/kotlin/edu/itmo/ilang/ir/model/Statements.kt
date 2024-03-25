package edu.itmo.ilang.ir.model

sealed interface Statement : BodyEntry

data class Assignment(
    val lhs: AccessExpression,
    val rhs: Expression
) : Statement

data class Return(
    val expression: Expression?
) : Statement

data object Break : Statement

data object Continue : Statement

data class WhileLoop(
    val condition: Expression,
    val body: Body
) : Statement

data class ForLoop(
    val loopVariableDecl: VariableDeclaration,
    val isReversed: Boolean,
    val rangeStart: Expression,
    val rangeEnd: Expression,
    val body: Body
) : Statement

data class IfStatement(
    val condition: Expression,
    val thenBody: Body,
    val elseBody: Body?
) : Statement
