package runners

import edu.itmo.ilang.IrBuilder
import edu.itmo.ilang.codegen.CodeGenerator
import edu.itmo.ilang.semantic.SemanticStageProcessor
import utils.LLVMInterpreter
import kotlin.math.pow
import kotlin.test.assertEquals

object CodeGenTestsRunner : ParseAwareTestRunner() {
    private const val COMMENT_PREFIX = "//"

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

    private fun readExecutionMetaData(testName: String): List<ExecutionMeta> {
        val code = getProgramText(testName)
        val commentsInPreamble = code
            .lines()
            .filter { it.isNotBlank() }
            .takeWhile { line -> line.startsWith(COMMENT_PREFIX) }

        return commentsInPreamble.mapNotNull(::tryParseExecutionMeta)
    }

    private val metaRegex = Regex("(.+)\\((.*)\\)(:(.*))?")

    private fun tryParseExecutionMeta(text: String): ExecutionMeta? {
        val clear = text.removePrefix(COMMENT_PREFIX).trim().replace(" ", "")
        val matchResult = metaRegex.matchEntire(clear) ?: return null

        val routineName = matchResult.groups[1]?.value ?: return null
        val args = matchResult.groups[2]?.value ?: return null
        val expectedValue = if (matchResult.groups.size == 5){
            matchResult.groups[4]?.value
        } else null

        val argValues = args.split(",").map { it.asValue }
        val argsNotNull = argValues.filterNotNull()

        if (args.isNotBlank() && argValues != argsNotNull) {
            return null
        }

        val result: Any? = expectedValue?.asValue

        return ExecutionMeta(
            routineName,
            argsNotNull,
            result
        )
    }

    private val String.asValue: Any?
        get() {
            val asInt = this.toIntOrNull()
            val asDouble = this.toDoubleOrNull()
            val asBoolean = this.toBooleanStrictOrNull()

            return asInt ?: asDouble ?: asBoolean
        }

    data class ExecutionMeta(
        val routineName: String,
        val args: List<Any>,
        val expectedResult: Any?,
    )
}
