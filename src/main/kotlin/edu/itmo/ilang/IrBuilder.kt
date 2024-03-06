package edu.itmo.ilang

import edu.itmo.ilang.ir.*
import iLangParserVisitor
import iLangParser.*
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.RuleNode
import org.antlr.v4.runtime.tree.TerminalNode

class IrBuilder : iLangParserVisitor<IrEntry> {

    private val symbolTable = SymbolTable()

    override fun visit(tree: ParseTree): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitChildren(node: RuleNode): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitTerminal(node: TerminalNode): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitErrorNode(node: ErrorNode): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitProgram(ctx: ProgramContext): Program {
        return symbolTable.withScope {
            Program(ctx.children.map { it.accept(this)
                    as? Declaration
                ?: TODO()})
        }
    }

    override fun visitSimpleDeclaration(ctx: SimpleDeclarationContext): Declaration {
        return ctx.typeDeclaration()?.let { visitTypeDeclaration(it) } ?:
            visitVariableDeclaration(ctx.variableDeclaration())
    }

    override fun visitVariableDeclaration(ctx: VariableDeclarationContext): VariableDeclaration {
        val name = ctx.Identifier().text
        val initialExpression = ctx.expression()?.let { visitExpression(it) }
        var type = ctx.type()?.let { visitType(it) }

        if (initialExpression == null && type == null) {
            TODO()
        }

        if (initialExpression?.type != type) {
            TODO()
        }

        if (type == null) {
            type = initialExpression!!.type
        }

        return VariableDeclaration(name, type, initialExpression)
            .also { symbolTable.addSymbol(name, SymbolInfo(type, it)) }
    }

    override fun visitTypeDeclaration(ctx: TypeDeclarationContext): TypeDeclaration {
        val name = ctx.Identifier().text
        val type = visitType(ctx.type())
        if (type is UserType) {
            type.identifier = name
        }

        return TypeDeclaration(name, type)
            .also { symbolTable.addSymbol(name, SymbolInfo(type, it)) }
    }

    override fun visitRoutineDeclaration(ctx: RoutineDeclarationContext): RoutineDeclaration {
        val name = ctx.Identifier().text
        val returnType = ctx.type()?.let { visitType(it) } ?: UnitType

        symbolTable.enterScope()
        try {
            val parameters = ctx.parameters()?.let {
                it.parameterDeclaration().map { pd -> visitParameterDeclaration(pd) }
            } ?: emptyList()
            val routineType = RoutineType(parameters.map { it.type }, returnType)

            val routineDeclaration = RoutineDeclaration(name, routineType, parameters, null)
            symbolTable.addSymbolToParentScope(name, SymbolInfo(routineType, routineDeclaration))

            routineDeclaration.body = visitBody(ctx.body())
            return routineDeclaration
        } finally {
            symbolTable.leaveScope()
        }
    }

    override fun visitParameters(ctx: ParametersContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitParameterDeclaration(ctx: ParameterDeclarationContext): ParameterDeclaration {
        val name = ctx.Identifier().text
        val type = visitType(ctx.type())

        return ParameterDeclaration(name, type)
            .also { symbolTable.addSymbol(name, SymbolInfo(type, it)) }
    }

    override fun visitType(ctx: TypeContext): Type {
        return when {
            ctx.primitiveType() != null -> visitPrimitiveType(ctx.primitiveType())
            ctx.arrayType() != null -> visitArrayType(ctx.arrayType())
            ctx.recordType() != null -> visitRecordType(ctx.recordType())
            ctx.Identifier() != null ->
                symbolTable.lookup(ctx.Identifier().text)?.type ?: TODO()

            else -> throw IllegalStateException()
        }
    }

    override fun visitPrimitiveType(ctx: PrimitiveTypeContext): PrimitiveType {
        return when {
            ctx.BOOLEAN() != null -> BoolType
            ctx.REAL() != null -> RealType
            ctx.INTEGER() != null -> IntegerType

            else -> throw IllegalStateException()
        }
    }

    override fun visitRecordType(ctx: RecordTypeContext): RecordType {
        val fields = ctx.variableDeclaration().map { Pair(it.Identifier().text, visitType(it.type())) }
        return RecordType(null, fields)
    }

    override fun visitArrayType(ctx: ArrayTypeContext): ArrayType {
        val sizeConstant = ctx.expression()?.let { CompileTimeEvaluator.evaluateInt(visitExpression(it)) } ?: -1
        return ArrayType(null, visitType(ctx.type())). apply { size = sizeConstant }
    }

    override fun visitBody(ctx: BodyContext): Body {
        return Body(ctx.children.map { it.accept(this) as? BodyEntry
            ?: TODO() })
    }

    override fun visitStatement(ctx: StatementContext): Statement {
        return when {
            ctx.assignment() != null -> visitAssignment(ctx.assignment())
            ctx.routineCallStatement() != null -> visitRoutineCallStatement(ctx.routineCallStatement())
            ctx.whileLoop() != null -> visitWhileLoop(ctx.whileLoop())
            ctx.forLoop() != null -> visitForLoop(ctx.forLoop())
            ctx.ifStatement() != null -> visitIfStatement(ctx.ifStatement())
            ctx.returnStatement() != null -> visitReturnStatement(ctx.returnStatement())
            ctx.BREAK() != null -> Break
            ctx.CONTINUE() != null -> Continue

            else -> throw IllegalStateException()
        }
    }

    override fun visitReturnStatement(ctx: ReturnStatementContext): Return {
        return Return(visitExpression(ctx.expression()))
    }

    override fun visitAssignment(ctx: AssignmentContext): Assignment {
        return Assignment(visitModifiablePrimary(ctx.modifiablePrimary()), visitExpression(ctx.expression()))
    }

    override fun visitRoutineCallStatement(ctx: RoutineCallStatementContext): RoutineCall {
        TODO("Not yet implemented")
    }

    override fun visitRoutineCallExpression(ctx: RoutineCallExpressionContext): RoutineCall {
        TODO("Not yet implemented")
    }

    override fun visitWhileLoop(ctx: WhileLoopContext): WhileLoop {
        return WhileLoop(visitExpression(ctx.expression()), symbolTable.withScope { visitBody(ctx.body()) })
    }

    override fun visitForLoop(ctx: ForLoopContext): ForLoop {
        val range = ctx.range()
        return ForLoop(
            ctx.Identifier().text,
            range.REVERSE() != null,
            visitExpression(range.expression(0)),
            visitExpression(range.expression(1)),
            symbolTable.withScope { visitBody(ctx.body()) }
        )
    }

    override fun visitRange(ctx: RangeContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitIfStatement(ctx: IfStatementContext): IfStatement {
        return IfStatement(
            visitExpression(ctx.expression()),
            symbolTable.withScope { visitBody(ctx.main_body) },
            ctx.else_body?.let {  symbolTable.withScope {  visitBody(it) } }
        )
    }

    override fun visitExpression(ctx: ExpressionContext): Expression {
        if (ctx.L_PARENTHESIS() != null) {
            return visitExpression(ctx.expression(0))
        }

        if (ctx.expression().size == 1 && ctx.MINUS() != null) {
            return UnaryMinusExpression(visitExpression(ctx.expression(0)))
        }

        if (ctx.primary() != null) {
            return visitPrimary(ctx.primary())
        }

        val leftExpression = visitExpression(ctx.expression(0))
        val rightExpression = visitExpression(ctx.expression(1))

        when (ctx.op.type) {
            EQ -> return EqualsExpression(leftExpression, rightExpression)
            NOT_EQ -> return NotEqualsExpression(leftExpression, rightExpression)
            LESS_EQ -> return LessOrEqualsExpression(leftExpression, rightExpression)
            LESS -> return LessExpression(leftExpression, rightExpression)
            GREAT_EQ -> return GreaterOrEqualsExpression(leftExpression, rightExpression)
            GREAT -> return GreaterExpression(leftExpression, rightExpression)
            AND -> return AndExpression(leftExpression, rightExpression)
            OR -> return OrExpression(leftExpression, rightExpression)
            XOR -> return XorExpression(leftExpression, rightExpression)
        }

        val type =
            if (leftExpression.type == RealType || rightExpression.type == RealType)
                RealType
            else
                IntegerType

        return when (ctx.op.type) {
            PLUS  -> PlusExpression(leftExpression, rightExpression, type)
            MINUS -> MinusExpression(leftExpression, rightExpression, type)
            MUL -> MulExpression(leftExpression, rightExpression, type)
            DIV -> DivExpression(leftExpression, rightExpression, type)
            MOD -> ModExpression(leftExpression, rightExpression, type)

            else -> throw IllegalStateException()
        }
    }

    override fun visitPrimary(ctx: PrimaryContext): Expression {
        return when {
            ctx.IntegralLiteral() != null -> IntegralLiteral(ctx.IntegralLiteral().text.toInt())
            ctx.RealLiteral() != null -> RealLiteral(ctx.RealLiteral().text.toDouble())
            ctx.TRUE() != null -> BoolLiteral(true)
            ctx.FALSE() != null -> BoolLiteral(false)
            ctx.modifiablePrimary() != null -> visitModifiablePrimary(ctx.modifiablePrimary())
            ctx.routineCallExpression() != null -> visitRoutineCallExpression(ctx.routineCallExpression())

            else -> throw IllegalStateException()
        }
    }

    override fun visitModifiablePrimary(ctx: ModifiablePrimaryContext): AccessExpression {
        TODO("Not yet implemented")
    }
}