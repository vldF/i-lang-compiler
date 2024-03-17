package edu.itmo.ilang.ir

sealed interface Declaration : IrEntry {
    val name: String
    val type: Type
}

sealed interface ValueDeclaration : Declaration

data class VariableDeclaration(
    override val name: String,
    override val type: Type,
    val initialExpression: Expression?,
) : ValueDeclaration, BodyEntry

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
