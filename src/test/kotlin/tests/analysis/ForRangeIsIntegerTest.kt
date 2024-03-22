package tests.analysis

import edu.itmo.ilang.semantic.checkers.ForRangeIsInteger
import generateIr
import org.junit.jupiter.api.Test
import kotlin.test.assertFails

class ForRangeIsIntegerTest {
    @Test
    fun testForIsIntegerStart() {
        val example = """
            routine main() : integer is 
                for i in 1.0..4 loop
                    break
                end
            end
        """.trimIndent()


        assertFails {
            ForRangeIsInteger().check(generateIr(example))
        }
    }

    @Test
    fun testForIsIntegerEnd() {
        val example = """
            routine main() : integer is 
                for i in 1..true loop
                    break
                end
            end
        """.trimIndent()


        assertFails {
            ForRangeIsInteger().check(generateIr(example))
        }
    }
}