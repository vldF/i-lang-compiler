package edu.itmo.ilang.semantic.checkers

import edu.itmo.ilang.ir.*
import edu.itmo.ilang.util.report

class AssignNewValueToArgument : BodyEntriesChecker {
    override fun checkBodyEntry(bodyEntry: BodyEntry) {
        if (bodyEntry is Assignment &&
            bodyEntry.lhs is VariableAccessExpression &&
            bodyEntry.lhs.variable is ParameterDeclaration) {

            report("cannot assign to parameters")
        }
    }
}
