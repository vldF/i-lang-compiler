package edu.itmo.ilang.semantic

import edu.itmo.ilang.ir.Program
import edu.itmo.ilang.semantic.analysis.BreakAndContinueInsideCyclesChecker
import edu.itmo.ilang.semantic.analysis.FunctionReturnChecker
import edu.itmo.ilang.semantic.analysis.ReservedKeywordsChecker
import edu.itmo.ilang.semantic.analysis.Typechecker
import edu.itmo.ilang.semantic.transformations.DeadCodeEliminator
import edu.itmo.ilang.semantic.transformations.ImplicitReturnTransformer
import edu.itmo.ilang.semantic.transformations.LazyBoolOperationsTransformer

class SemanticStageProcessor {
    private val analysers = listOf(
        ReservedKeywordsChecker(),
        Typechecker(),
        FunctionReturnChecker(),
        BreakAndContinueInsideCyclesChecker(),
    )

    private val transformers = listOf(
        DeadCodeEliminator(),
        LazyBoolOperationsTransformer(),
        ImplicitReturnTransformer(),
    )

    fun process(program: Program) {
        for (analyser in analysers) {
            analyser.analyse(program)
        }

        for (transformer in transformers) {
            transformer.transform(program)
        }
    }
}