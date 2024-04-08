package tests.analysis

import edu.itmo.ilang.semantic.checkers.IdentifierChecker
import generateIr
import org.junit.jupiter.api.Test
import kotlin.test.assertFails

class IdentifierCheckerTest {
    @Test
    fun testSize() {
        val bigName = List(257) { 'a' }.joinToString(separator = "")
        val example = """
            type TreeNode is record
                var $bigName : real
                var key : integer
                var children : array [2] TreeNode
            end
            
            routine main(a : integer) is 
                a := 5
            end
        """.trimIndent()


        assertFails {
            val ir = generateIr(example)
            IdentifierChecker().check(ir)
        }
    }

    @Test
    fun testUninitialized() {
        val example = """
            routine main(uninitialized : integer) is 
                uninitialized := 5
            end
        """.trimIndent()


        assertFails {
            val ir = generateIr(example)
            IdentifierChecker().check(ir)
        }
    }
}