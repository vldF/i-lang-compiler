import java.io.File

const val INDENT = "    "
const val testsBasePath = "./src/test/"

fun main() {
    val testdataDir = File(testsBasePath).resolve("resources").resolve("testdata")

    val testCases = testdataDir.listFiles()!!
    testCases.sortBy { it.absoluteFile }

    val code = StringBuilder()
    code.addPreamble()
    code.addTestClass {
        testCases.forEach(::addTestFunction)
    }

    val resultFilePath = File(testsBasePath).resolve("kotlin").resolve("iLangTests.kt")
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

private fun StringBuilder.addTestClass(content: StringBuilder.() -> Unit) {
    val contentStringBuilder = StringBuilder()
    contentStringBuilder.content()
    val contentLines = contentStringBuilder.lines()

    appendLine("@Suppress(\"ClassName\")")
    appendLine("class iLangTests {")
    contentLines.map { INDENT + it }.forEach(::appendLine)
    appendLine("}")
}

private fun StringBuilder.addTestFunction(file: File) {
    val content = """
        @Test
        fun ${file.nameWithoutExtension}_test() {
            ParserTestsRunner.run("${file.nameWithoutExtension}")
        }
    """.trimIndent()

    appendLine(content)
    appendLine()
}
