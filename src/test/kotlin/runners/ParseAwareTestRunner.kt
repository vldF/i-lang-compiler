package runners

import iLangLexer
import iLangParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

abstract class ParseAwareTestRunner : ITestRunner {
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
}
