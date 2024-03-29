package edu.itmo.ilang.semantic.checkers

import edu.itmo.ilang.ir.model.*
import edu.itmo.ilang.util.report

class BreakAndContinueInsideCyclesChecker : Checker {
    override fun check(program: Program) {
        checkIrEntry(program, false)
    }

    private fun checkIrEntry(irEntry: IrEntry, allowedBreakOrContinue: Boolean) {
        when (irEntry) {
            Break -> {
                if (!allowedBreakOrContinue) {
                    report("unexpected break")
                }
            }

            Continue -> {
                if (!allowedBreakOrContinue) {
                    report("unexpected continue")
                }

            }

            is Program -> irEntry.declarations.forEach { checkIrEntry(it, allowedBreakOrContinue) }

            is Body -> irEntry.statements.forEach { checkIrEntry(it, allowedBreakOrContinue) }

            is ForLoop -> irEntry.body.statements.forEach { checkIrEntry(it, true) }

            is WhileLoop -> irEntry.body.statements.forEach { checkIrEntry(it, true) }

            is IfStatement ->  {
                irEntry.thenBody.statements.forEach { checkIrEntry(it, allowedBreakOrContinue) }
                irEntry.elseBody?.statements?.forEach { checkIrEntry(it, allowedBreakOrContinue) }
            }

            is RoutineDeclaration -> irEntry.body?.statements?.forEach { checkIrEntry(it, allowedBreakOrContinue) }

            else -> {}
        }
    }
}
