package runners

import edu.itmo.ilang.IrBuilder
import edu.itmo.ilang.codegen.CodeGenerator
import kotlin.test.assertEquals

object CodeGenTestsRunner : ParseAwareTestRunner() {
    private const val COMMENT_PREFIX = "//"

    override fun run(testName: String) {
        val parser = parse(testName)
        val programIr = IrBuilder().visitProgram(parser.program())
        val codeGenerator = CodeGenerator()

        val executionMetadata = readExecutionMetaData(testName)

        try {
            codeGenerator.generate(programIr)

            for (meta in executionMetadata) {
                val expectedResult = meta.expectedResult
                val result = when (expectedResult) {
                    is Long -> codeGenerator.interpretWithIntegerResult(meta.routineName, meta.args)
                    is Boolean -> codeGenerator.interpretWithBooleanResult(meta.routineName, meta.args)
                    is Double -> codeGenerator.interpretWithRealResult(meta.routineName, meta.args)
                    null -> {
                        codeGenerator.interpretWithRealResult(meta.routineName, meta.args)

                        null
                    }
                    else -> error("can't parse expected result type $expectedResult of type ${expectedResult::class}")
                }

                assertEquals(expectedResult, result)
            }
        } finally {
            codeGenerator.dispose()
        }
    }

    private fun readExecutionMetaData(testName: String): List<ExecutionMeta> {
        val code = getProgramText(testName)
        val commentsInPreamble = code
            .lines()
            .takeWhile { line -> line.startsWith(COMMENT_PREFIX) }

        return commentsInPreamble.mapNotNull(::tryParseExecutionMeta)
    }

    private fun tryParseExecutionMeta(string: String): ExecutionMeta? {
        val trimmed = string.removePrefix(COMMENT_PREFIX).trim()
        val parts = trimmed.replace(" ", "").split(",")
        val routineName = parts.first()

        val operands = parts.drop(1).map { it.asValue }
        val operandsNotNull = operands.filterNotNull()

        if (operands != operandsNotNull) {
            return null
        }

        val args = operandsNotNull.dropLast(1)
        var result: Any? = operandsNotNull.last()
        if (result == UNIT_RET_TYPE) {
            result = null
        }

        return ExecutionMeta(
            routineName,
            args,
            result
        )
    }

    private val String.asValue: Any?
        get() {
            val asLong = this.toLongOrNull()
            val asDouble = this.toDoubleOrNull()
            val asBoolean = this.toBooleanStrictOrNull()
            val asNoArg = if (this == UNIT_RET_TYPE) UNIT_RET_TYPE else null

            return asLong ?: asDouble ?: asBoolean ?: asNoArg
        }

    data class ExecutionMeta(
        val routineName: String,
        val args: List<Any>,
        val expectedResult: Any?,
    )

    private const val UNIT_RET_TYPE = "unit"
}
