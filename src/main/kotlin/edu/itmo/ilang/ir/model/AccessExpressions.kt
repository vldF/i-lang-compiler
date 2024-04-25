package edu.itmo.ilang.ir.model

sealed interface AccessExpression : Expression

data class VariableAccessExpression(
    val variable: ValueDeclaration
) : AccessExpression {
    override val type = variable.type
}

data class FieldAccessExpression(
    val accessedExpression: AccessExpression,
    val accessedType : Type,
    var field: String,
) : AccessExpression {
    override val type
        get() = when (accessedType) {
            is RecordType -> accessedType.fields.first { it.first == field }.second
            is ArrayType -> if (field == "size") IntegerType else Nothing
            else -> error("unsupported accessed type")
        }
}

data class ArrayAccessExpression(
    val accessedExpression: AccessExpression,
    val arrayType: ArrayType,
    var indexExpression: Expression?
) : AccessExpression {
    override val type = arrayType.contentType
}
