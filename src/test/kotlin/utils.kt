import edu.itmo.ilang.ir.IrBuilder
import edu.itmo.ilang.ir.model.Program
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream

fun generateIr(program: String): Program {
    val inputStream = ANTLRInputStream(program)
    val lexer = iLangLexer(inputStream)
    val tokens = CommonTokenStream(lexer)
    val parser = iLangParser(tokens)

    return IrBuilder().visitProgram(parser.program())
}