package tests.analysis

import edu.itmo.ilang.ir.IfStatement
import edu.itmo.ilang.ir.Return
import edu.itmo.ilang.ir.RoutineDeclaration
import edu.itmo.ilang.ir.VariableDeclaration
import edu.itmo.ilang.semantic.transformations.ImplicitReturnTransformer
import generateIr
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class ImplicitReturnTransformerTest {
    @Test
    fun unitTest() {
        val example = """
            routine main() is 
                var a is 6
                var b is 3
            end
        """.trimIndent()

        val ir = generateIr(example)
        val routineBody = (ir.declarations.first() as RoutineDeclaration).body!!

        assertTrue { routineBody.statements.last() is VariableDeclaration }

        ImplicitReturnTransformer().transform(ir)

        assertTrue { routineBody.statements.last() is Return }
    }

    @Test
    fun ifTest() {
        val example = """
            routine main() : integer is 
                if true then
                    return 5
                else
                    return 6
                end
            end
        """.trimIndent()

        val ir = generateIr(example)
        val routineBody = (ir.declarations.first() as RoutineDeclaration).body!!

        assertTrue { routineBody.statements.last() is IfStatement }

        ImplicitReturnTransformer().transform(ir)

        assertTrue { routineBody.statements.last() is Return }
    }
}
