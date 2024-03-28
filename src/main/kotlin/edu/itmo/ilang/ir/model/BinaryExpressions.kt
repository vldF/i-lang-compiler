package edu.itmo.ilang.ir.model

sealed interface BinaryExpression : Expression {
    val left: Expression
    val right: Expression
}

data class MulExpression(
    override val left: Expression,
    override val right: Expression,
    override val type: Type
) : BinaryExpression

data class DivExpression(
    override val left: Expression,
    override val right: Expression,
    override val type: Type
) : BinaryExpression

data class ModExpression(
    override val left: Expression,
    override val right: Expression,
    override val type: Type
) : BinaryExpression

data class PlusExpression(
    override val left: Expression,
    override val right: Expression,
    override val type: Type
) : BinaryExpression

data class MinusExpression(
    override val left: Expression,
    override val right: Expression,
    override val type: Type
) : BinaryExpression


sealed interface LogicalExpression : BinaryExpression {
    override val type: Type
        get() = BoolType
}

data class EqualsExpression(
    override val left: Expression,
    override val right: Expression
) : LogicalExpression

data class NotEqualsExpression(
    override val left: Expression,
    override val right: Expression
) : LogicalExpression

data class LessOrEqualsExpression(
    override val left: Expression,
    override val right: Expression
) : LogicalExpression

data class LessExpression(
    override val left: Expression,
    override val right: Expression
) : LogicalExpression

data class GreaterOrEqualsExpression(
    override val left: Expression,
    override val right: Expression
) : LogicalExpression

data class GreaterExpression(
    override val left: Expression,
    override val right: Expression
) : LogicalExpression

data class AndExpression(
    override val left: Expression,
    override val right: Expression
) : LogicalExpression

data class OrExpression(
    override val left: Expression,
    override val right: Expression
) : LogicalExpression

data class XorExpression(
    override val left: Expression,
    override val right: Expression
) : LogicalExpression