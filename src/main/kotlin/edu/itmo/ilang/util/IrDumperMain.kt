package edu.itmo.ilang.util

import edu.itmo.ilang.ir.IrBuilder
import edu.itmo.ilang.ir.model.IrEntry
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


    println(parser.program().accept(IrBuilder()).prettyPrint())
}


// from https://gist.github.com/Mayankmkh/92084bdf2b59288d3e74c3735cccbf9f
fun IrEntry.prettyPrint(): String {

    var indentLevel = 0
    val indentWidth = 4

    fun padding() = "".padStart(indentLevel * indentWidth)

    val toString = toString()

    val stringBuilder = StringBuilder(toString.length)

    var i = 0
    while (i < toString.length) {
        when (val char = toString[i]) {
            '(', '[', '{' -> {
                indentLevel++
                stringBuilder.appendLine(char).append(padding())
            }

            ')', ']', '}' -> {
                indentLevel--
                stringBuilder.appendLine().append(padding()).append(char)
            }

            ',' -> {
                stringBuilder.appendLine(char).append(padding())
                // ignore space after comma as we have added a newline
                val nextChar = toString.getOrElse(i + 1) { char }
                if (nextChar == ' ') i++
            }

            else -> {
                stringBuilder.append(char)
            }
        }
        i++
    }
    return stringBuilder.toString()
}