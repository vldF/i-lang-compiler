package edu.itmo.ilang.ir

sealed interface BodyEntry : IrEntry
data class Body(
    var statements: List<BodyEntry>
) : IrEntry {
    companion object {
        val EMPTY = Body(emptyList())
    }
}
