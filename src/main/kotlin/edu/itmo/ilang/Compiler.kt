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
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

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
        val launcherFilePath = outputFilePath.parent.resolve("launcher.cpp")

        try {
            CodeGenerator().use { codeGenerator ->
                codeGenerator.generate(programIr)
                codeGenerator.saveObjectFile(objectFilePath)
            }

            LauncherCreator(programIr, launcherFilePath).createLauncher()

            val clang = Loader.load(org.bytedeco.llvm.program.clang::class.java)

            val clangArgs = arrayOf(
                objectFilePath,
                launcherFilePath.toString(),
                "-o",
                outputFile,
                "--driver-mode=g++",
                *getAdditionalClangArgs()
            )

            val processBuilder = ProcessBuilder(clang, *clangArgs)
            processBuilder.inheritIO().start().waitFor()
        } finally {
            Files.deleteIfExists(Path.of(objectFilePath))
            Files.deleteIfExists(launcherFilePath)
        }
    }

    private val isMacOS: Boolean = System.getProperty("os.name").lowercase(Locale.getDefault()).contains("mac")

    private fun getAdditionalClangArgs(): Array<String> {
        if (isMacOS) {
            val sdkBasePath = getSdkBasePathOnMacOs() ?: return emptyArray()
            return arrayOf("-isysroot", sdkBasePath)
        }

        return emptyArray()
    }

    private fun getSdkBasePathOnMacOs(): String? {
        val xcrunProcessBuilder = ProcessBuilder("xcrun", "--show-sdk-path")
        val xcrunProcess = xcrunProcessBuilder.start()

        val output = BufferedReader(InputStreamReader(xcrunProcess.inputStream)).readText().trim('\n')
        val exitCode = xcrunProcess.waitFor()
        if (exitCode != 0) {
            return null
        }

        return output
    }
}
