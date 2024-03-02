package edu.itmo.ilang.ir

sealed interface Type

sealed interface PrimitiveType : Type

data object IntegerType : PrimitiveType

data object RealType : PrimitiveType

data object BoolType : PrimitiveType

sealed interface UserType : Type {
    val identifier: String?
}

data class ArrayType(
    override val identifier: String?,
    val contentType: Type,
) : UserType

data class RecordType(
    override val identifier: String?,
    val fields: Map<String, Type>,
) : UserType


data class RoutineType(
    val argumentTypes: List<Type>,
    val returnType: Type
) : Type