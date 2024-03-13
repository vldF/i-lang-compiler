package edu.itmo.ilang.ir

sealed interface BodyEntry : IrEntry
data class Body(
    val statements: List<BodyEntry>
) : IrEntry {
    val hasTerminalStatement: Boolean
        get() = statements.any { it is Return }

    companion object {
        val EMPTY = Body(emptyList())
    }
}
