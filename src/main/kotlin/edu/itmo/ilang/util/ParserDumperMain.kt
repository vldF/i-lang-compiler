@file:Suppress("DuplicatedCode")

package edu.itmo.ilang.util

import iLangLexer
import iLangParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

fun main(args: Array<String>) {
    val path = args.first()
    val programText = File(path)

    val code = programText.readText()
    val `in` = ANTLRInputStream(code)
    val lexer = iLangLexer(`in`)
    val tokens = CommonTokenStream(lexer)

    val parser = iLangParser(tokens)
    val program = parser.program()

    print(program.toStringTree(parser))
}
