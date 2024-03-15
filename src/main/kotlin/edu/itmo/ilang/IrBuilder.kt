package edu.itmo.ilang

import edu.itmo.ilang.ir.*
import edu.itmo.ilang.ir.Nothing
import edu.itmo.ilang.util.report
import iLangParser.*
import iLangParserBaseVisitor
import org.antlr.v4.runtime.tree.TerminalNode

class IrBuilder : iLangParserBaseVisitor<IrEntry>() {

    private val symbolTable = SymbolTable()

    override fun visitProgram(ctx: ProgramContext): Program {
        return symbolTable.withScope {
            addPredefinedSymbols()

            Program(ctx.children.map { it.accept(this)
                    as? Declaration
                ?: report("$it is not declaration")
            })
        }
    }

    private fun addPredefinedSymbols() {
        symbolTable.addSymbol("bool", SymbolInfo(BoolType, TypeDeclaration("bool", BoolType)))
        symbolTable.addSymbol("real", SymbolInfo(RealType, TypeDeclaration("real", RealType)))
        symbolTable.addSymbol("integer", SymbolInfo(IntegerType, TypeDeclaration("integer", IntegerType)))
        symbolTable.addSymbol("uninitialized", SymbolInfo(Nothing, VariableDeclaration("uninitialized", Nothing, null)))
    }

    override fun visitSimpleDeclaration(ctx: SimpleDeclarationContext): Declaration {
        return ctx.typeDeclaration()?.let { visitTypeDeclaration(it) } ?:
            visitVariableDeclaration(ctx.variableDeclaration())
    }

    override fun visitVariableDeclaration(ctx: VariableDeclarationContext): VariableDeclaration {
        val name = ctx.Identifier().text
        var initialExpression = ctx.expression()?.let { visitExpression(it) }
        var type = ctx.type()?.let { visitType(it) }

        if (initialExpression == null && type == null) {
            report("cannot infer type for $ctx")
        }

        if (initialExpression == null) {
            initialExpression = when(type) {
                IntegerType -> IntegralLiteral(0)
                BoolType -> BoolLiteral(false)
                RealType -> RealLiteral(0.0)

                is UserType -> UninitializedLiteral

                else -> throw IllegalStateException()
            }
        }
        if (type == null) {
            type = initialExpression.type
        }

        return VariableDeclaration(name, type, initialExpression)
            .also { symbolTable.addSymbol(name, SymbolInfo(type, it)) }
    }

    override fun visitTypeDeclaration(ctx: TypeDeclarationContext): TypeDeclaration {
        val name = ctx.Identifier().text
        val symbolInfo = SymbolInfo(MuParameter, null)
        symbolTable.addSymbol(name, symbolInfo)

        val type = visitType(ctx.type())
        if (type is UserType) {
            type.identifier = name
        }
        symbolInfo.type = type

        return TypeDeclaration(name, type)
            .also { symbolInfo.declaration = it }
    }

    override fun visitRoutineDeclaration(ctx: RoutineDeclarationContext): RoutineDeclaration {
        val name = ctx.Identifier().text
        val returnType = ctx.type()?.let { visitType(it) } ?: UnitType

        return symbolTable.withScope {
            val parameters = ctx.parameters()?.let {
                it.parameterDeclaration().map { pd -> visitParameterDeclaration(pd) }
            } ?: emptyList()
            val routineType = RoutineType(parameters.map { it.type }, returnType)

            val routineDeclaration = RoutineDeclaration(name, routineType, parameters, null)
            symbolTable.addSymbolToParentScope(name, SymbolInfo(routineType, routineDeclaration))

            routineDeclaration.body = visitBody(ctx.body())
            routineDeclaration
        }
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
                symbolTable.lookup(ctx.Identifier().text) {
                    symbolInfo -> symbolInfo.declaration == null || symbolInfo.declaration is TypeDeclaration
                }?.type ?: report("unknown symbol ${ctx.Identifier().text}")

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
        return Body(ctx.children.filter { it.text != ";" }.map { it.accept(this) as? BodyEntry
            ?: report("$it is not body entry") })
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
        return Return(ctx.expression()?.let { visitExpression(it) })
    }

    override fun visitAssignment(ctx: AssignmentContext): Assignment {
        return Assignment(visitModifiablePrimary(ctx.modifiablePrimary()), visitExpression(ctx.expression()))
    }

    override fun visitRoutineCallStatement(ctx: RoutineCallStatementContext): RoutineCall {
        return visitRoutineCall(ctx.Identifier().text, ctx.expression())
    }

    override fun visitRoutineCallExpression(ctx: RoutineCallExpressionContext): RoutineCall {
        return visitRoutineCall(ctx.Identifier().text, ctx.expression())
    }

    private fun visitRoutineCall(routineName: String, args: List<ExpressionContext>?): RoutineCall {
        val declaration = symbolTable.lookup<RoutineDeclaration>(routineName)?.declaration
            ?: report("unknown symbol $routineName")
        val expressions = args?.map { visitExpression(it) } ?: emptyList()

        return RoutineCall(declaration as RoutineDeclaration, expressions)
    }

    override fun visitWhileLoop(ctx: WhileLoopContext): WhileLoop {
        return WhileLoop(visitExpression(ctx.expression()), symbolTable.withScope { visitBody(ctx.body()) })
    }

    override fun visitForLoop(ctx: ForLoopContext): ForLoop {
        val range = ctx.range()
        val loopVarName = ctx.Identifier().text
        return ForLoop(
            loopVarName,
            range.REVERSE() != null,
            visitExpression(range.expression(0)),
            visitExpression(range.expression(1)),
            symbolTable.withScope {
                symbolTable.addSymbol(loopVarName, SymbolInfo(IntegerType, VariableDeclaration(loopVarName, IntegerType, null)))
                visitBody(ctx.body())
            }
        )
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
        val firstSymbol = ctx.Identifier().first().text
        val firstSymbolDeclaration = symbolTable.lookup<ValueDeclaration>(firstSymbol)?.declaration as? ValueDeclaration
                ?: report("unknown symbol $firstSymbol")
        var result: AccessExpression = VariableAccessExpression(firstSymbolDeclaration)

        for (child in ctx.children.drop(1)) {
            if (child is TerminalNode) {
                when (child.symbol.type) {
                    Identifier -> {
                        if (result is FieldAccessExpression) {
                            result.field = child.text
                        }
                    }
                    DOT -> {
                        result = FieldAccessExpression(result, result.type, "")
                    }
                    L_BRACKET -> {
                        result = ArrayAccessExpression(result, result.type as ArrayType, null)
                    }
                }
            }
            if (child is ExpressionContext && result is ArrayAccessExpression) {
                result.indexExpression = child.accept(this) as Expression
            }
        }

        return result
    }
}