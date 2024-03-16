package edu.itmo.ilang.semantic

import edu.itmo.ilang.ir.Program
import edu.itmo.ilang.semantic.analysis.FunctionReturnAnalyzer
import edu.itmo.ilang.semantic.checkers.BreakAndContinueInsideCyclesChecker
import edu.itmo.ilang.semantic.checkers.FunctionReturnChecker
import edu.itmo.ilang.semantic.checkers.ReservedKeywordsChecker
import edu.itmo.ilang.semantic.checkers.TypeChecker
import edu.itmo.ilang.semantic.transformations.DeadCodeEliminator
import edu.itmo.ilang.semantic.transformations.ImplicitReturnTransformer
import edu.itmo.ilang.semantic.transformations.LazyBoolOperationsTransformer

class SemanticStageProcessor {
    private val analysers = listOf(
        FunctionReturnAnalyzer()
    )

    private val checkers = listOf(
        ReservedKeywordsChecker(),
        TypeChecker(),
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

        for (checker in checkers) {
            checker.check(program)
        }

        for (transformer in transformers) {
            transformer.transform(program)
        }
    }
}
