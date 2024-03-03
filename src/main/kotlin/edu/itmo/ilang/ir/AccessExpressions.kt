package edu.itmo.ilang.ir

sealed interface AccessExpression : Expression

data class VariableAccessExpression(
    val variable: ValueDeclaration
) : AccessExpression {
    override val type = variable.type
}

data class RecordFieldAccessExpression(
    val accessedExpression: AccessExpression,
    val recordType : RecordType,
    val field: String,
) : AccessExpression {
    override val type = recordType.fields[field]!!
}

data class ArrayAccessExpression(
    val accessedExpression: AccessExpression,
    val arrayType: ArrayType,
    val indexExpression: Expression
) : AccessExpression {
    override val type = arrayType.contentType
}