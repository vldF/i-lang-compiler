package edu.itmo.ilang.semantic.checkers

import edu.itmo.ilang.ir.*

interface BodyEntriesChecker : Checker, BodyEntriesProcessor {
    override fun check(program: Program) {
        processIrEntry(program)
    }
}