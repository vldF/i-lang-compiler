package edu.itmo.ilang.semantic.checkers

import edu.itmo.ilang.ir.model.Program
import edu.itmo.ilang.ir.model.RoutineDeclaration
import edu.itmo.ilang.ir.model.UnitType
import edu.itmo.ilang.util.report

class FunctionReturnChecker : Checker {
    override fun check(program: Program) {
        for (routine in program.declarations.filterIsInstance<RoutineDeclaration>()) {
            if (routine.type.returnType != UnitType && !routine.body!!.isTerminating) {
                report("$routine does not return value on all execution paths")
            }
        }
    }
}
