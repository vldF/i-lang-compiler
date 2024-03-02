package edu.itmo.ilang.ir

sealed interface BinaryExpression : Expression {
    val left: Expression
    val right: Expression
}

data class MulExpression(
    override val left: Expression,
    override val right: Expression
) : BinaryExpression

data class DivExpression(
    override val left: Expression,
    override val right: Expression
) : BinaryExpression

data class ModExpression(
    override val left: Expression,
    override val right: Expression
) : BinaryExpression

data class PlusExpression(
    override val left: Expression,
    override val right: Expression
) : BinaryExpression

data class MinusExpression(
    override val left: Expression,
    override val right: Expression
) : BinaryExpression

data class EqualsExpression(
    override val left: Expression,
    override val right: Expression
) : BinaryExpression

data class NotEqualsExpression(
    override val left: Expression,
    override val right: Expression
) : BinaryExpression

data class LessOrEqualsExpression(
    override val left: Expression,
    override val right: Expression
) : BinaryExpression

data class LessExpression(
    override val left: Expression,
    override val right: Expression
) : BinaryExpression

data class GreaterOrEqualsExpression(
    override val left: Expression,
    override val right: Expression
) : BinaryExpression

data class GreaterExpression(
    override val left: Expression,
    override val right: Expression
) : BinaryExpression

data class AndExpression(
    override val left: Expression,
    override val right: Expression
) : BinaryExpression

data class OrExpression(
    override val left: Expression,
    override val right: Expression
) : BinaryExpression

data class XorExpression(
    override val left: Expression,
    override val right: Expression
) : BinaryExpression