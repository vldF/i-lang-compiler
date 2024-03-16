package edu.itmo.ilang.ir

sealed interface BodyEntry : IrEntry
data class Body(
    var statements: List<BodyEntry>
) : IrEntry {
    val hasTerminalStatement: Boolean
        get() = statements.any { it.isTerminalStatement }

    companion object {
        val EMPTY = Body(emptyList())
    }
}
