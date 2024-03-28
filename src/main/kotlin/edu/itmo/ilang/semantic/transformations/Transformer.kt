package edu.itmo.ilang.semantic.transformations

import edu.itmo.ilang.ir.model.Program

interface Transformer {
    fun transform(program: Program)
}