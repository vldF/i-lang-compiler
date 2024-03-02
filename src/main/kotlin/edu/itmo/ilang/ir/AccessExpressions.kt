package edu.itmo.ilang.ir

data class RecordFieldAccessExpression(
    val record : RecordType,
    val field: String,
) : PossibleLhsExpression

data class ArrayAccessExpression(
    val array: ArrayType,
    val indexExpression: Expression
) : PossibleLhsExpression