package runners

abstract class ExecutionMetadataTestRunner : ParseAwareTestRunner() {

    private val commentPrefix = "//"

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