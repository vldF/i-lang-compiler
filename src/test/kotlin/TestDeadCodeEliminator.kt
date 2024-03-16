import edu.itmo.ilang.ir.*
import edu.itmo.ilang.semantic.transformations.DeadCodeEliminator
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class TestDeadCodeEliminator {
    @Test
    fun simpleTest() {
        val example = """
            routine main() : integer is 
                return 5
                var a is 6
                var b is 3
            end
        """.trimIndent()

        val ir = generateIr(example)
        val routineBody = (ir.declarations.first() as RoutineDeclaration).body!!

        assertTrue { routineBody.statements.last() is VariableDeclaration }

        DeadCodeEliminator().transform(ir)

        assertTrue { routineBody.statements.last() is Return }
    }

    @Test
    fun testLoop() {
        val example = """
            routine isSorted(arr : array[] integer) : bool is
                for i in 2..(arr.size) loop
                    var b is 3 
                    break
                    var a is 5
                    return false
                end
            
                return true
            end
        """.trimIndent()

        val ir = generateIr(example)
        val forInRoutineBody = (ir.declarations.first() as RoutineDeclaration).body!!.statements.first() as ForLoop

        assertTrue { forInRoutineBody.body.statements.last() is Return }

        DeadCodeEliminator().transform(ir)

        assertTrue { forInRoutineBody.body.statements.last() is Break }
    }
}