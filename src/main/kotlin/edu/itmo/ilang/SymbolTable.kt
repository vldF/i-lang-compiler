package edu.itmo.ilang

import edu.itmo.ilang.ir.Type

data class SymbolInfo(
    val type: Type
)

class SymbolTable {
    private val symbols = mutableListOf<MutableMap<String, SymbolInfo>>()

    fun <T> withScope(action: () -> T) : T {
        enterScope()
        try {
            return action()
        } finally {
            leaveScope()
        }
    }

    fun addSymbol(symbol: String, symbolInfo: SymbolInfo) {
        addSymbol(symbol, symbolInfo, symbols.last)
    }

    fun addSymbolToParentScope(symbol: String, symbolInfo: SymbolInfo) {
        addSymbol(symbol, symbolInfo, symbols[symbols.size - 2])
    }

    private fun addSymbol(symbol: String, symbolInfo: SymbolInfo, scope: MutableMap<String, SymbolInfo>) {
        if (scope.containsKey(symbol)) {
            TODO()
        }

        scope[symbol] = symbolInfo
    }

    fun lookup(symbol: String): SymbolInfo? {
        return symbols.asReversed().firstNotNullOfOrNull { it[symbol] }
    }

    fun enterScope() {
        symbols.add(mutableMapOf())
    }

    fun leaveScope() {
        symbols.removeLast()
    }
}