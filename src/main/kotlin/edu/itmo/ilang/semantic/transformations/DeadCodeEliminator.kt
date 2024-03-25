package edu.itmo.ilang.semantic.transformations

import edu.itmo.ilang.ir.*
import edu.itmo.ilang.ir.model.*

class DeadCodeEliminator : Transformer {
    override fun transform(program: Program) {
        removeDeadCode(program)
    }

    private fun removeDeadCode(irEntry: IrEntry) {
        when(irEntry) {
            is Program -> irEntry.declarations.forEach { removeDeadCode(it) }
            is Body -> {
                val indexOfTerminatingStatement = irEntry.statements
                    .indexOfFirst { it.isTerminalStatement }

                if (indexOfTerminatingStatement != -1) {
                    irEntry.statements = irEntry.statements.take(indexOfTerminatingStatement + 1)
                }
                irEntry.statements.forEach { removeDeadCode(it) }
            }
            is ForLoop -> removeDeadCode(irEntry.body)
            is IfStatement -> {
                removeDeadCode(irEntry.thenBody)
                irEntry.elseBody?.let { removeDeadCode(it) }
            }
            is WhileLoop -> removeDeadCode(irEntry.body)
            is RoutineDeclaration -> irEntry.body?.let { removeDeadCode(it) }

            else -> {}
        }
    }
}
