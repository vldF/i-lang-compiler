package edu.itmo.ilang.ir.model

import kotlin.Nothing

sealed interface Literal <T> : Expression {
    val value : T
}

data class IntegralLiteral(
    override val value: Int
) : Literal<Int> {
    override val type = IntegerType
}

data class RealLiteral(
    override val value: Double
) : Literal<Double> {
    override val type = RealType
}

data class BoolLiteral(
    override val value: Boolean
) : Literal<Boolean> {
    override val type = BoolType
}

data object UninitializedLiteral : Literal<Nothing?> {
    override val type = Nothing

    override val value: Nothing? = null
}