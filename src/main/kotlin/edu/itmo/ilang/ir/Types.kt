package edu.itmo.ilang.ir

sealed interface Type : IrEntry

sealed interface PrimitiveType : Type

data object IntegerType : PrimitiveType

data object RealType : PrimitiveType

data object BoolType : PrimitiveType

data object UnitType : PrimitiveType

data object MuParameter : Type

data object Nothing : Type

sealed interface UserType : Type {
    var identifier: String?
}

data class ArrayType(
    override var identifier: String?,
    val contentType: Type,
) : UserType {
    // null specify any size. Used for parameter declaration
    var size: Int? = null
}

data class RecordType(
    override var identifier: String?,
    val fields: List<Pair<String, Type>>,
) : UserType

data class RoutineType(
    val argumentTypes: List<Type>,
    val returnType: Type
) : Type