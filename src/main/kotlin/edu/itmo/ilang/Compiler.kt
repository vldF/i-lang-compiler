package edu.itmo.ilang

import edu.itmo.ilang.codegen.CodeGenerator
import edu.itmo.ilang.codegen.LauncherCreator
import edu.itmo.ilang.ir.IrBuilder
import edu.itmo.ilang.semantic.SemanticStageProcessor
import iLangLexer
import iLangParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.bytedeco.javacpp.Loader
import java.nio.file.Files
import java.nio.file.Path

object Compiler {
    fun compile(programSourceCode: String, outputFile: String) {
        val inputStream = ANTLRInputStream(programSourceCode)
        val lexer = iLangLexer(inputStream)
        val tokens = CommonTokenStream(lexer)
        val parser = iLangParser(tokens)
        val parseTree = parser.program()

        val programIr = IrBuilder().visitProgram(parseTree)
        SemanticStageProcessor().process(programIr)

        val outputFilePath = Path.of(outputFile).toAbsolutePath().normalize()
        val objectFilePath = "$outputFilePath.o"
        val launcherFilePath = Path.of(outputFilePath.parent.toString(), "launcher.cpp")

        try {
            CodeGenerator().use { codeGenerator ->
                codeGenerator.generate(programIr)
                codeGenerator.saveObjectFile(objectFilePath)
            }

            LauncherCreator(programIr, launcherFilePath).createLauncher()

            val clang = Loader.load(org.bytedeco.llvm.program.clang::class.java)
            val processBuilder = ProcessBuilder(clang,
                objectFilePath, launcherFilePath.toString(), "-o", outputFile, "--driver-mode=g++")
            processBuilder.inheritIO().start().waitFor()
        } finally {
            Files.deleteIfExists(Path.of(objectFilePath))
            Files.deleteIfExists(launcherFilePath)
        }
    }
}