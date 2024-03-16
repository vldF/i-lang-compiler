package edu.itmo.ilang.semantic.transformations

import edu.itmo.ilang.ir.*

class ImplicitReturnTransformer : Transformer {
    override fun transform(program: Program) {
        for (routine in program.declarations.filterIsInstance<RoutineDeclaration>()) {
            val body = routine.body!!
            if (body.statements.last() !is Return) {
                val returnStatement = when(routine.type.returnType) {
                    BoolType -> Return(BoolLiteral(false))
                    IntegerType -> Return(IntegralLiteral(0))
                    RealType -> Return(RealLiteral(0.0))
                    UnitType -> Return(UninitializedLiteral)
                    is UserType -> Return(UninitializedLiteral)

                    else -> throw IllegalStateException()
                }

                body.statements += returnStatement
            }
        }
    }
}