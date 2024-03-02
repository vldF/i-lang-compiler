package edu.itmo.ilang.ir

sealed interface Declaration : IrEntry {
    val name: String
    val type: Type
}

data class VariableDeclaration(
    override val name: String,
    override val type: Type,
    val initialExpression: Expression,
) : Declaration, BodyEntry

data class TypeDeclaration(
    override val name: String,
    override val type: Type
) : Declaration, BodyEntry

data class RoutineDeclaration(
    override val name: String,
    override val type: RoutineType,
    val parameters: List<ParameterDeclaration>,
    val body: Body
) : Declaration

data class ParameterDeclaration(
    override val name: String,
    override val type: Type
) : Declaration