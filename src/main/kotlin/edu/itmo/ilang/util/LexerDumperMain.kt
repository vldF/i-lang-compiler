package edu.itmo.ilang.util

import iLangLexer
import iLangParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

fun main(args: Array<String>) {
    val path = args.first()
    val program = File(path)

    val code = program.readText()
    val `in` = ANTLRInputStream(code)
    val lexer = iLangLexer(`in`)
    val tokens = CommonTokenStream(lexer)

    val parser = iLangParser(tokens)
    parser.program()

    printTokensSequence(tokens)
}

private fun printTokensSequence(tokenStream: CommonTokenStream) {
    for (token in tokenStream.tokens) {
        val text = token.text
        if (text.isBlank()) {
            continue
        }

        print("$text\n")
    }
    println()
}
