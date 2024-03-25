package runners

import edu.itmo.ilang.ir.IrBuilder
import edu.itmo.ilang.codegen.CodeGenerator
import edu.itmo.ilang.semantic.SemanticStageProcessor
import utils.LLVMInterpreter
import kotlin.math.pow
import kotlin.test.assertEquals

object CodeGenTestsRunner : ParseAwareTestRunner() {
    override fun run(testName: String) {
        val parser = parse(testName)
        val programIr = IrBuilder().visitProgram(parser.program())
        val semanticStageProcessor = SemanticStageProcessor()
        semanticStageProcessor.process(programIr)

        val executionMetadata = readExecutionMetaData(testName)

        CodeGenerator().use { codeGenerator ->
            codeGenerator.generate(programIr)
            val interpreter = LLVMInterpreter(codeGenerator.getModule())

            for (meta in executionMetadata) {
                val expectedResult = meta.expectedResult
                val actualResult = interpreter.executeFunction(meta)
                if (expectedResult is Double && actualResult is Double) {
                    assertEquals(expectedResult, actualResult, 10.0.pow(-5.0))
                } else {
                    assertEquals(expectedResult, actualResult)
                }

                println("success: for expected value $expectedResult got $actualResult")
            }
        }
    }

    private fun LLVMInterpreter.executeFunction(meta: ExecutionMeta): Any? {
        return when (val expectedResult = meta.expectedResult) {
            is Int -> this.interpretWithIntegerResult(meta.routineName, meta.args)
            is Boolean -> this.interpretWithBooleanResult(meta.routineName, meta.args)
            is Double -> this.interpretWithRealResult(meta.routineName, meta.args)
            null -> {
                this.interpretWithUnitResult(meta.routineName, meta.args)

                null
            }

            else -> error("can't parse expected result type $expectedResult of type ${expectedResult::class}")
        }
    }
}
