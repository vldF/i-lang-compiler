package edu.itmo.ilang.semantic.checkers

import edu.itmo.ilang.ir.Program

interface Checker {
    fun check(program: Program)
}
