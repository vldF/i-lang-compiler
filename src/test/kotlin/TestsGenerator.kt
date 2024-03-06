import java.io.File

const val INDENT = "    "
const val testsBasePath = "./src/test/"

fun main() {
    val testdataDir = File(testsBasePath).resolve("resources").resolve("testdata")

    val testCases = testdataDir.listFiles()!!
    testCases.sortBy { it.absoluteFile }

    generateParserTests(testCases)
    generateIrBuilderTests(testCases)
}

private fun generateParserTests(testCases: Array<File>) {
    generateTests(testCases, "iLangParserTests", "ParserTestsRunner")
}

private fun generateIrBuilderTests(testCases: Array<File>) {
    generateTests(testCases, "iLangIrBuilderTests", "IrBuilderTestsRunner")
}

private fun generateTests(testCases: Array<File>, testClassName: String, testRunnerName: String) {
    val code = StringBuilder()
    code.addPreamble()
    code.addTestClass(testClassName) {
        testCases.forEach { addTestFunction(it, testRunnerName) }
    }

    val resultFilePath = File(testsBasePath).resolve("kotlin").resolve("$testClassName.kt")
    resultFilePath.writeText(code.toString())
}

private fun StringBuilder.addPreamble() {
    val content = """
        import org.junit.jupiter.api.Test
        
        // DO NOT MODIFY THIS FILE MANUALLY
        // Edit TestsGenerator.kt instead
    """.trimIndent()

    appendLine(content)
    appendLine()
}

private fun StringBuilder.addTestClass(className: String, content: StringBuilder.() -> Unit) {
    val contentStringBuilder = StringBuilder()
    contentStringBuilder.content()
    val contentLines = contentStringBuilder.lines()

    appendLine("@Suppress(\"ClassName\")")
    appendLine("class $className {")
    contentLines.map { INDENT + it }.forEach(::appendLine)
    appendLine("}")
}

private fun StringBuilder.addTestFunction(file: File, testRunnerName: String) {
    val content = """
        @Test
        fun ${file.nameWithoutExtension}_test() {
            $testRunnerName.run("${file.nameWithoutExtension}")
        }
    """.trimIndent()

    appendLine(content)
    appendLine()
}
