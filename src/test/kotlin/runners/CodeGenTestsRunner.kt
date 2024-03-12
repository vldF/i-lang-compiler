package runners

import edu.itmo.ilang.IrBuilder
import edu.itmo.ilang.codegen.CodeGenerator
import org.junit.jupiter.api.assertDoesNotThrow

object CodeGenTestsRunner : ParseAwareTestRunner() {
    override fun run(testName: String) {
        val parser = parse(testName)
        val programIr = IrBuilder().visitProgram(parser.program())

        assertDoesNotThrow {
            CodeGenerator().generate(programIr)
        }
    }
}
