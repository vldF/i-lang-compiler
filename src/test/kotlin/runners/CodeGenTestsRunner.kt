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
                val actualResult = codeGenerator.executeTest(meta)
                assertEquals(expectedResult, actualResult)
                println("success: for expected value $expectedResult got $actualResult")
            }
        } finally {
            codeGenerator.dispose()
        }
    }

    private fun CodeGenerator.executeTest(meta: ExecutionMeta): Any? {
        return when (val expectedResult = meta.expectedResult) {
            is Long -> this.interpretWithIntegerResult(meta.routineName, meta.args)
            is Boolean -> this.interpretWithBooleanResult(meta.routineName, meta.args)
            is Double -> this.interpretWithRealResult(meta.routineName, meta.args)
            null -> {
                this.interpretWithRealResult(meta.routineName, meta.args)

                null
            }

            else -> error("can't parse expected result type $expectedResult of type ${expectedResult::class}")
        }
    }

    private fun readExecutionMetaData(testName: String): List<ExecutionMeta> {
        val code = getProgramText(testName)
        val commentsInPreamble = code
            .lines()
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

        if (argValues != argsNotNull) {
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
