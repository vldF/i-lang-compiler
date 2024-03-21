package edu.itmo.ilang.semantic.checkers

import edu.itmo.ilang.ir.*

interface BodyEntriesChecker : Checker {

    fun checkBodyEntry(bodyEntry: BodyEntry)

    override fun check(program: Program) {
        checkIrEntry(program)
    }

    private fun checkIrEntry(irEntry: IrEntry) {
        when (irEntry) {
            is Program -> irEntry.declarations.forEach { checkIrEntry(it) }

            is Body -> irEntry.statements.forEach {
                checkBodyEntry(it)
                checkIrEntry(it)
            }

            is ForLoop -> irEntry.body.statements.forEach {
                checkBodyEntry(it)
                checkIrEntry(it)
            }

            is WhileLoop -> irEntry.body.statements.forEach {
                checkBodyEntry(it)
                checkIrEntry(it)
            }

            is IfStatement ->  {
                irEntry.thenBody.statements.forEach {
                    checkBodyEntry(it)
                    checkIrEntry(it)
                }
                irEntry.elseBody?.statements?.forEach {
                    checkBodyEntry(it)
                    checkIrEntry(it)
                }
            }

            is RoutineDeclaration -> irEntry.body?.statements?.forEach {
                checkBodyEntry(it)
                checkIrEntry(it)
            }

            else -> {}
        }
    }
}