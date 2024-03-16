package edu.itmo.ilang.semantic.transformations

import edu.itmo.ilang.ir.Program

interface Transformer {
    fun transform(program: Program)
}