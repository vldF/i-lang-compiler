package runners

import iLangLexer
import iLangParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

abstract class ParseAwareTestRunner : ITestRunner {

    private val commentPrefix = "//"

    protected fun parse(testName: String): iLangParser {
        val programText = getProgramText(testName)
        val inputStream = ANTLRInputStream(programText)
        val lexer = iLangLexer(inputStream)
        val tokens = CommonTokenStream(lexer)
        return iLangParser(tokens)
    }

    protected fun getProgramText(testName: String): String {
        val testFilePathStr = "/testdata/$testName${I_LANG_FILE_EXT}"
        val testFilePath = this::class.java.getResource(testFilePathStr)?.toURI()
            ?: error("can't find resource '$testFilePathStr'")
        return File(testFilePath).readText()
    }

    data class ExecutionMeta(
        val routineName: String,
        val args: List<Any>,
        val expectedResult: Any?,
    )

    protected fun readExecutionMetaData(testName: String): List<ExecutionMeta> {
        val code = getProgramText(testName)
        val commentsInPreamble = code
            .lines()
            .filter { it.isNotBlank() }
            .takeWhile { line -> line.startsWith(commentPrefix) }

        return commentsInPreamble.mapNotNull(::tryParseExecutionMeta)
    }

    private val metaRegex = Regex("(.+)\\((.*)\\)(:(.*))?")

    private fun tryParseExecutionMeta(text: String): ExecutionMeta? {
        val clear = text.removePrefix(commentPrefix).trim().replace(" ", "")
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
}
