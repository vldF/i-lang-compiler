package edu.itmo.ilang.ir

sealed interface BodyEntry
data class Body(
    val statements: List<BodyEntry>
)