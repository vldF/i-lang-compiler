package edu.itmo.ilang.ir.model

sealed interface Type : IrEntry

sealed interface PrimitiveType : Type

data object IntegerType : PrimitiveType

data object RealType : PrimitiveType

data object BoolType : PrimitiveType

data object UnitType : PrimitiveType

data object MuParameter : Type

data object Nothing : Type

sealed interface UserType : Type {
    val identifier: String?

    fun withIdentifier(identifier: String): UserType
}

data class ArrayType(
    override val identifier: String?,
    var contentType: Type,
) : UserType {
    // null specify any size. Used for parameter declaration
    var size: Int? = null

    override fun withIdentifier(identifier: String): UserType {
        return ArrayType(identifier, contentType)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArrayType

        if (identifier != other.identifier) return false
        if (contentType !== other.contentType) return false

        return true
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }
}

data class RecordType(
    override val identifier: String?,
    var fields: List<Pair<String, Type>>,
) : UserType {
    override fun withIdentifier(identifier: String): UserType {
        return RecordType(identifier, fields)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecordType

        if (identifier != other.identifier) return false
        if (fields !== other.fields) return false

        return true
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }
}

data class RoutineType(
    val argumentTypes: List<Type>,
    val returnType: Type
) : Type