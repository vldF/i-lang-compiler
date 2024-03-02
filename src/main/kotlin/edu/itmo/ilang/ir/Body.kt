package edu.itmo.ilang.ir

sealed interface BodyEntry : IrEntry
data class Body(
    val statements: List<BodyEntry>
) : IrEntry