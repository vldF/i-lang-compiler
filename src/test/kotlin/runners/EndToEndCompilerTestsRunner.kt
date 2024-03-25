package runners

import edu.itmo.ilang.Compiler
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists
import kotlin.test.assertEquals

object EndToEndCompilerTestsRunner : ParseAwareTestRunner() {

    private val binaryPath = Path("test.out").toAbsolutePath()

    override fun run(testName: String) {
        try {
            Compiler.compile(getProgramText(testName), binaryPath.toString())

            val executionMetadata = readExecutionMetaData(testName)

            for (meta in executionMetadata) {
                val args = mutableListOf(binaryPath.toString(), meta.routineName)
                args.addAll(meta.args.map { it.toString() })

                val processBuilder = ProcessBuilder(args)
                val process = processBuilder.start()

                val result = String(process.inputStream.readAllBytes())

                assertEquals(0, process.waitFor())
                if (meta.expectedResult is Double) {
                    assertEquals(meta.expectedResult, result.trim().toDouble())
                } else {
                    assertEquals(meta.expectedResult.toString(), result.trim())
                }
            }
        } finally {
            binaryPath.deleteIfExists()
        }
    }
}