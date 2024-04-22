package edu.itmo.ilang.ir.model

sealed interface Declaration : IrEntry {
    val name: String
    val type: Type
}

sealed interface ValueDeclaration : Declaration

data class VariableDeclaration(
    override val name: String,
    override val type: Type,
    val initialExpression: Expression?,
) : ValueDeclaration, BodyEntry {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VariableDeclaration

        if (name != other.name) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}

data class TypeDeclaration(
    override val name: String,
    override val type: Type
) : Declaration, BodyEntry

data class RoutineDeclaration(
    override val name: String,
    override val type: RoutineType,
    val parameters: List<ParameterDeclaration>,
    var body: Body?
) : Declaration

data class ParameterDeclaration(
    override val name: String,
    override val type: Type
) : ValueDeclaration
