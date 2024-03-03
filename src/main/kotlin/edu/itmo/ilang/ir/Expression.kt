package edu.itmo.ilang.ir

sealed interface Expression : IrEntry {
    val type: Type
}

data class RoutineCall(
    val routineDeclaration: RoutineDeclaration,
    val arguments: List<Expression>
) : Statement, Expression {
    override val type = routineDeclaration.type.returnType
}

data class UnaryMinusExpression(
    val nestedExpression: Expression
) : Expression {
    override val type = nestedExpression.type
}