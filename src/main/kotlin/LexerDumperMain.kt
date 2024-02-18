import org.antlr.v4.runtime.*
import java.io.File

fun main() {
    val basicPath = "./src/test/resources/testdata/"
    val programs = File(basicPath).listFiles()!!

    for (program in programs) {
        val code = program.readText()
        val `in` = ANTLRInputStream(code)
        val lexer = iLangLexer(`in`)
        val tokens = CommonTokenStream(lexer)

        val parser = iLangParser(tokens)
        parser.program()

        printTokensSequence(tokens)
    }
}

private fun printTokensSequence(tokenStream: CommonTokenStream) {
    for (token in tokenStream.tokens) {
        val text = token.text
        if (text.isBlank()) {
            continue
        }

        print("[$text]")
    }
    println()
}
