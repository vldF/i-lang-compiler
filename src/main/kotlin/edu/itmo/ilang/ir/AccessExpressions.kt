package edu.itmo.ilang.ir

sealed interface AccessExpression : Expression

data class VariableAccessExpression(
    val variable: ValueDeclaration
) : AccessExpression

data class RecordFieldAccessExpression(
    val accessedExpression: AccessExpression,
    val recordType : RecordType,
    val field: String,
) : AccessExpression

data class ArrayAccessExpression(
    val accessedExpression: AccessExpression,
    val arrayType: ArrayType,
    val indexExpression: Expression
) : AccessExpression