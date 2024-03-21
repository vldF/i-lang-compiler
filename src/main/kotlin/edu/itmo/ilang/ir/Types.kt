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
    var contentType: Type,
) : UserType {
    // null specify any size. Used for parameter declaration
    var size: Int? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArrayType

        return identifier == other.identifier
    }

    override fun hashCode(): Int {
        return identifier?.hashCode() ?: 0
    }
}

data class RecordType(
    override var identifier: String?,
    var fields: List<Pair<String, Type>>,
) : UserType {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecordType

        return identifier == other.identifier
    }

    override fun hashCode(): Int {
        return identifier?.hashCode() ?: 0
    }
}

data class RoutineType(
    val argumentTypes: List<Type>,
    val returnType: Type
) : Type