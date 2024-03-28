package edu.itmo.ilang

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlin.io.path.Path
import kotlin.io.path.readText

fun main(args: Array<String>) {
    val parser = ArgParser("I-Lang compiler")

    val sourceFile by parser.argument(ArgType.String, fullName = "source path", description = "path to source ilang file")
    val destFile by parser.option(ArgType.String, shortName = "o", fullName = "output", description = "path for compiled binary file")
        .default("a.out")

    parser.parse(args)

    Compiler.compile(Path(sourceFile).readText(), destFile)
}