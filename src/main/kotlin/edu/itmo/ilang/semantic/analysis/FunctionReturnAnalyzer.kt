package edu.itmo.ilang.semantic.analysis

import edu.itmo.ilang.ir.*

class FunctionReturnAnalyzer : Analyser {
    override fun analyse(program: Program) {
        val routines = program.declarations.filterIsInstance<RoutineDeclaration>()
        for (routine in routines) {
            analyseBody(routine.body ?: continue)
        }
    }

    private fun analyseBody(body: Body) {
        body.isTerminating = alwaysReturns(body.statements)

        val innerBodies = mutableListOf<Body>()
        for (statement in body.statements) {
            when (statement) {
                is ForLoop -> innerBodies.add(statement.body)
                is WhileLoop -> innerBodies.add(statement.body)
                is IfStatement -> {
                    innerBodies.add(statement.thenBody)
                    innerBodies.add(statement.elseBody ?: continue)
                }
                else -> {}
            }
        }

        innerBodies.forEach(::analyseBody)
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
