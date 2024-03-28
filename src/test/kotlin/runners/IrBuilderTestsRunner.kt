package runners

import edu.itmo.ilang.ir.IrBuilder
import org.junit.jupiter.api.assertDoesNotThrow

object IrBuilderTestsRunner : ParseAwareTestRunner() {
    override fun run(testName: String) {
        val parser = parse(testName)

        assertDoesNotThrow {
            parser.program().accept(IrBuilder())
        }
    }
}
