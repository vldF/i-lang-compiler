import edu.itmo.ilang.semantic.analysis.BreakAndContinueInsideCyclesChecker
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertFails

class BreakAndContinueInsideCyclesTest {
    @Test
    fun testBreakInFor() {
        val example = """
            routine main() : integer is 
                for i in 1..4 loop
                    break
                end
            end
        """.trimIndent()


        assertDoesNotThrow { BreakAndContinueInsideCyclesChecker().analyse(generateIr(example)) }
    }

    @Test
    fun testBreakInRoutine() {
        val example = """
            routine main() : integer is 
                break
            end
        """.trimIndent()


        assertFails { BreakAndContinueInsideCyclesChecker().analyse(generateIr(example)) }
    }

}