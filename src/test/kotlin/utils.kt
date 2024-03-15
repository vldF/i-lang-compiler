import edu.itmo.ilang.IrBuilder
import edu.itmo.ilang.ir.IrEntry
import edu.itmo.ilang.ir.Program
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream

fun generateIr(program: String): Program {
    val inputStream = ANTLRInputStream(program)
    val lexer = iLangLexer(inputStream)
    val tokens = CommonTokenStream(lexer)
    val parser = iLangParser(tokens)

    return IrBuilder().visitProgram(parser.program())
}