package tests.analysis

import edu.itmo.ilang.semantic.checkers.ArraySizeModificationIsProhibited
import generateIr
import org.junit.jupiter.api.Test
import kotlin.test.assertFails


class ArraySizeModificationTest {
    @Test
    fun testAssignToArraySizeSimple() {
        val example = """
        routine test() : integer is
          var arr1 : array[1] integer
          arr1.size := 10
        
          return arr1.size // 10
        end
        """.trimIndent()

        assertFails {
            val ir = generateIr(example)
            ArraySizeModificationIsProhibited().check(ir)
        }
    }
}