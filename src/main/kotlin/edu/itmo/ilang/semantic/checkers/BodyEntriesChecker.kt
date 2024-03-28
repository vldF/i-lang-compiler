package edu.itmo.ilang.semantic.checkers

import edu.itmo.ilang.ir.*
import edu.itmo.ilang.ir.model.Program

interface BodyEntriesChecker : Checker, BodyEntriesProcessor {
    override fun check(program: Program) {
        processIrEntry(program)
    }
}