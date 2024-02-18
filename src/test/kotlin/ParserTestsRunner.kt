import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.ATNConfigSet
import org.antlr.v4.runtime.dfa.DFA
import org.junit.jupiter.api.Assertions
import java.io.File
import java.util.*

object ParserTestsRunner {
    private const val FILE_EXTENSION = ".il"

    fun run(testName: String) {
        val testFilePathStr = "/testdata/$testName$FILE_EXTENSION"
        val testFilePath = this::class.java.getResource(testFilePathStr)?.toURI()
            ?: error("can't find resource '$testFilePathStr'")
        val programText = File(testFilePath).readText()

        val inputStream = ANTLRInputStream(programText)
        val lexer = iLangLexer(inputStream)
        val tokens = CommonTokenStream(lexer)
        val parser = iLangParser(tokens)

        val lexerErrorListener = ErrorListener()
        lexer.addErrorListener(lexerErrorListener)

        val parserErrorListener = ErrorListener()
        parser.addErrorListener(parserErrorListener)

        parser.program()

        Assertions.assertFalse(lexerErrorListener.hasErrors, "lexer errors were met!")
        Assertions.assertFalse(parserErrorListener.hasErrors, "lexer errors were met!")
    }

    private class ErrorListener : DiagnosticErrorListener(/* exactOnly = */ false) {
        var hasErrors = false

        override fun syntaxError(
            recognizer: Recognizer<*, *>?,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String?,
            e: RecognitionException?,
        ) {
            hasErrors = true

            super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e)
        }

        override fun reportAmbiguity(
            recognizer: Parser?,
            dfa: DFA?,
            startIndex: Int,
            stopIndex: Int,
            exact: Boolean,
            ambigAlts: BitSet?,
            configs: ATNConfigSet?,
        ) { }

        override fun reportAttemptingFullContext(
            recognizer: Parser?,
            dfa: DFA?,
            startIndex: Int,
            stopIndex: Int,
            conflictingAlts: BitSet?,
            configs: ATNConfigSet?,
        ) { }

        override fun reportContextSensitivity(
            recognizer: Parser?,
            dfa: DFA?,
            startIndex: Int,
            stopIndex: Int,
            prediction: Int,
            configs: ATNConfigSet?,
        ) { }

    }
}
