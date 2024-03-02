package edu.itmo.ilang.ir

sealed interface Expression : IrEntry

sealed interface PossibleLhsExpression : Expression

data class RoutineCall(
    val routineDeclaration: RoutineDeclaration,
    val arguments: List<Expression>
) : Statement, Expression

data class UnaryMinusExpression(
    val nestedExpression: Expression
) : Expression