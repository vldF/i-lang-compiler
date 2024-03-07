package edu.itmo.ilang

import edu.itmo.ilang.ir.Declaration
import edu.itmo.ilang.ir.Type
import edu.itmo.ilang.util.report

data class SymbolInfo(
    var type: Type,
    var declaration: Declaration?
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
        addSymbol(symbol, symbolInfo, symbols.last())
    }

    fun addSymbolToParentScope(symbol: String, symbolInfo: SymbolInfo) {
        addSymbol(symbol, symbolInfo, symbols[symbols.size - 2])
    }

    private fun addSymbol(symbol: String, symbolInfo: SymbolInfo, scope: MutableMap<String, SymbolInfo>) {
        if (scope.containsKey(symbol)) {
            report("$symbol already defined in current scope")
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