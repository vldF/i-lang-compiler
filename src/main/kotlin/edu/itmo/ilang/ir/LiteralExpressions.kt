package edu.itmo.ilang.ir

sealed interface Literal <T> : Expression {
    val value : T
}

data class IntegralLiteral(
    override val value: Int
) : Literal<Int>

data class RealLiteral(
    override val value: Double
) : Literal<Double>

data class BoolLiteral(
    override val value: Boolean
) : Literal<Boolean>