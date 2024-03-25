package edu.itmo.ilang.semantic.analysis

import edu.itmo.ilang.ir.model.Program

interface Analyser {
    fun analyse(program: Program)
}