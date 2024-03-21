package edu.itmo.ilang.ir

interface BodyEntriesProcessor {

    fun processBodyEntry(bodyEntry: BodyEntry)

    fun processIrEntry(irEntry: IrEntry) {
        when (irEntry) {
            is Program -> irEntry.declarations.forEach { processIrEntry(it) }

            is Body -> irEntry.statements.forEach {
                processBodyEntry(it)
                processIrEntry(it)
            }

            is ForLoop -> irEntry.body.statements.forEach {
                processBodyEntry(it)
                processIrEntry(it)
            }

            is WhileLoop -> irEntry.body.statements.forEach {
                processBodyEntry(it)
                processIrEntry(it)
            }

            is IfStatement ->  {
                irEntry.thenBody.statements.forEach {
                    processBodyEntry(it)
                    processIrEntry(it)
                }
                irEntry.elseBody?.statements?.forEach {
                    processBodyEntry(it)
                    processIrEntry(it)
                }
            }

            is RoutineDeclaration -> irEntry.body?.statements?.forEach {
                processBodyEntry(it)
                processIrEntry(it)
            }

            else -> {}
        }
    }
}