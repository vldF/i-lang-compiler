package runners

import edu.itmo.ilang.IrBuilder
import iLangLexer
import iLangParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File

object IrBuilderTestsRunner : ITestRunner {
    private const val FILE_EXTENSION = ".il"

    override fun run(testName: String) {
        val testFilePathStr = "/testdata/$testName$FILE_EXTENSION"
        val testFilePath = this::class.java.getResource(testFilePathStr)?.toURI()
            ?: error("can't find resource '$testFilePathStr'")
        val programText = File(testFilePath).readText()

        val inputStream = ANTLRInputStream(programText)
        val lexer = iLangLexer(inputStream)
        val tokens = CommonTokenStream(lexer)
        val parser = iLangParser(tokens)

        assertDoesNotThrow {
            parser.program().accept(IrBuilder())
        }
    }
}
