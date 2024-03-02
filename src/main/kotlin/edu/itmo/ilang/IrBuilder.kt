package edu.itmo.ilang

import edu.itmo.ilang.ir.IrEntry
import iLangParserVisitor
import iLangParser.*
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.RuleNode
import org.antlr.v4.runtime.tree.TerminalNode

class IrBuilder : iLangParserVisitor<IrEntry> {
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

    override fun visitProgram(ctx: ProgramContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitSimpleDeclaration(ctx: SimpleDeclarationContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitVariableDeclaration(ctx: VariableDeclarationContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitTypeDeclaration(ctx: TypeDeclarationContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitRoutineDeclaration(ctx: RoutineDeclarationContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitParameters(ctx: ParametersContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitParameterDeclaration(ctx: ParameterDeclarationContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitType(ctx: TypeContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitPrimitiveType(ctx: PrimitiveTypeContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitRecordType(ctx: RecordTypeContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitArrayType(ctx: ArrayTypeContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitBody(ctx: BodyContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitStatement(ctx: StatementContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitReturnStatement(ctx: ReturnStatementContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitAssignment(ctx: AssignmentContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitRoutineCallStatement(ctx: RoutineCallStatementContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitRoutineCallExpression(ctx: RoutineCallExpressionContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitWhileLoop(ctx: WhileLoopContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitForLoop(ctx: ForLoopContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitRange(ctx: RangeContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitIfStatement(ctx: IfStatementContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitExpression(ctx: ExpressionContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitPrimary(ctx: PrimaryContext): IrEntry {
        TODO("Not yet implemented")
    }

    override fun visitModifiablePrimary(ctx: ModifiablePrimaryContext): IrEntry {
        TODO("Not yet implemented")
    }
}