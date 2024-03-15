import edu.itmo.ilang.semantic.analysis.FunctionReturnChecker
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertFails

class FunctionReturnCheckerTest {
    @Test
    fun testUnitRoutine() {
        val example = """
            routine main() is 
                var a is 5 + 5
            end
        """.trimIndent()


        assertDoesNotThrow { FunctionReturnChecker().analyse(generateIr(example)) }
    }

    @Test
    fun testOkIntRoutine() {
        val example = """
            routine main() : integer is 
                return 5
            end
        """.trimIndent()


        assertDoesNotThrow { FunctionReturnChecker().analyse(generateIr(example)) }
    }

    @Test
    fun testBadIntRoutine() {
        val example = """
            routine main() : integer is 
                var a is 5 + 5
            end
        """.trimIndent()


        assertFails { FunctionReturnChecker().analyse(generateIr(example)) }
    }

    @Test
    fun testOkIfElse() {
        val example = """
            routine main() : integer is 
                if true then
                    return 5
                else 
                    return 6
                end
            end
        """.trimIndent()


        assertDoesNotThrow { FunctionReturnChecker().analyse(generateIr(example)) }
    }
}