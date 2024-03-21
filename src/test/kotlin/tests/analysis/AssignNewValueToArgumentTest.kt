package tests.analysis

import edu.itmo.ilang.semantic.checkers.AssignNewValueToArgument
import generateIr
import org.junit.jupiter.api.Test
import kotlin.test.assertFails

class AssignNewValueToArgumentTest {
    @Test
    fun testAssignToParameterSimple() {
        val example = """
            routine main(a : integer) is 
                a := 5
            end
        """.trimIndent()


        assertFails {
            val ir = generateIr(example)
            AssignNewValueToArgument().check(ir)
        }
    }

    @Test
    fun testAssignToParameterInIf() {
        val example = """
            routine main(a : integer) is 
                if true then
                    a := 5
                end
            end
        """.trimIndent()


        assertFails {
            val ir = generateIr(example)
            AssignNewValueToArgument().check(ir)
        }
    }
}