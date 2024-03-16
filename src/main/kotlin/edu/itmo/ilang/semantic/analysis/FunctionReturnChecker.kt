package edu.itmo.ilang.semantic.analysis

import edu.itmo.ilang.ir.*
import edu.itmo.ilang.util.report

class FunctionReturnChecker : Analyser {
    override fun analyse(program: Program) {
        for (routine in program.declarations.filterIsInstance<RoutineDeclaration>()) {
            if (routine.type.returnType != UnitType && !alwaysReturns(routine.body!!.statements)) {
                report("$routine does not return value on all execution paths")
            }
        }
    }

    private fun alwaysReturns(statements: List<BodyEntry>): Boolean {
        if (statements.isEmpty()) {
            return false
        }
        val firstStatement = statements.first()
        if (firstStatement is Return) {
            return true
        }
        if (firstStatement is IfStatement && firstStatement.elseBody != null) {
            return alwaysReturns(firstStatement.thenBody.statements) &&
                    alwaysReturns(firstStatement.elseBody.statements) ||
                    alwaysReturns(statements.drop(1))
        }
        return alwaysReturns(statements.drop(1))
    }
}