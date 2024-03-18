import runners.CodeGenTestsRunner
import runners.ITestRunner
import runners.IrBuilderTestsRunner
import runners.ParserTestsRunner
import java.io.File
import kotlin.reflect.KClass

const val INDENT = "    "
const val testsBasePath = "./src/test/"
const val generatedTestsPath = "$testsBasePath/kotlin/tests/generated"

fun main() {
    val testdataDir = File(testsBasePath).resolve("resources").resolve("testdata")

    val testCases = testdataDir.listFiles()!!
    testCases.sortBy { it.absoluteFile }

    generateParserTests(testCases)
    generateIrBuilderTests(testCases)
    generateCodegenTests(testCases)
}

private fun generateParserTests(testCases: Array<File>) {
    generateTests(testCases, "iLangParserTests", ParserTestsRunner::class)
}

private fun generateIrBuilderTests(testCases: Array<File>) {
    generateTests(testCases, "iLangIrBuilderTests", IrBuilderTestsRunner::class)
}

private fun generateCodegenTests(testCases: Array<File>) {
    generateTests(testCases, "iLangCodeGenTests", CodeGenTestsRunner::class)
}

private fun generateTests(testCases: Array<File>, testClassName: String, testRunner: KClass<out ITestRunner>) {
    val code = StringBuilder()
    code.addPreamble(testRunner)
    code.addTestClass(testClassName) {
        testCases.forEach { addTestFunction(it, testRunner.simpleName!!) }
    }

    val resultFilePath = File(generatedTestsPath).resolve("$testClassName.kt")
    resultFilePath.writeText(code.toString())
}

private fun StringBuilder.addPreamble(testRunner: KClass<out ITestRunner>) {
    val content = """
        import org.junit.jupiter.api.Test
        import ${testRunner.qualifiedName}
        
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
