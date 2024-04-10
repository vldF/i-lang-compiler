package edu.itmo.ilang.semantic.checkers

import edu.itmo.ilang.ir.IrProcessor
import edu.itmo.ilang.ir.model.*
import edu.itmo.ilang.ir.model.Nothing
import edu.itmo.ilang.util.report

class TypeChecker : Checker, IrProcessor<Unit> {
    private val currentRoutineReturnExpressions: MutableList<Return> = mutableListOf()

    override fun check(program: Program) {
        process(program)
    }

    override fun processProgram(program: Program) {
        program.declarations.forEach(::process)
    }

    override fun processBody(body: Body) {
        body.statements.forEach(::process)
    }

    override fun processAssignment(assignment: Assignment) {
        visitExpression(assignment.rhs, assignment.rhs.type)
        if (assignment.lhs.type == assignment.rhs.type) {
            return
        }

        if (assignment.lhs.type == RealType && assignment.rhs.type == IntegerType) {
            return
        }

        report("Can't assign expression with type ${assignment.rhs.type} to left side with type ${assignment.lhs.type}")
    }

    override fun processForLoop(forLoop: ForLoop) {
        visitExpression(forLoop.rangeStart, forLoop.loopVariableDecl.type)
        visitExpression(forLoop.rangeEnd, forLoop.loopVariableDecl.type)
        forLoop.loopVariableDecl.initialExpression?.let {
            visitExpression(it, forLoop.loopVariableDecl.type)
        }
        process(forLoop.body)
    }

    override fun processIfStatement(ifStatement: IfStatement) {
        visitExpression(ifStatement.condition, BoolType)
        process(ifStatement.thenBody)
        ifStatement.elseBody?.let {
            process(it)
        }
    }

    override fun processReturn(`return`: Return) {
        this.currentRoutineReturnExpressions.add(`return`)
    }

    override fun processRoutineCall(routineCall: RoutineCall) {
        visitExpression(routineCall)
    }

    override fun processWhileLoop(whileLoop: WhileLoop) {
        visitExpression(whileLoop.condition, BoolType)
        process(whileLoop.body)
    }

    override fun processTypeDeclaration(typeDeclaration: TypeDeclaration) {
        process(typeDeclaration.type)
    }

    override fun processVariableDeclaration(variableDeclaration: VariableDeclaration) {
        variableDeclaration.initialExpression?.let {
            if (it is UninitializedLiteral) {
                return
            }

            visitExpression(it, variableDeclaration.type, "Wrong initialize type. ")
        }
    }

    override fun processRoutineDeclaration(routineDeclaration: RoutineDeclaration) {
        routineDeclaration.body?.let { process(it) }
        val returnExpressions = currentRoutineReturnExpressions

        if (returnExpressions.isEmpty()) {
            if (routineDeclaration.type.returnType !is UnitType) {
                report("Expected return type ${routineDeclaration.type.returnType} but got UnitType for ${routineDeclaration.name}")
            }
            return
        }

        for (returnExpr: Return in returnExpressions) {
            if (returnExpr.expression == null) {
                if (routineDeclaration.type.returnType is UnitType) {
                    continue
                }
                report("Expected return type ${routineDeclaration.type.returnType} but got UnitType for ${routineDeclaration.name}")
            }

            validateTypes(returnExpr.expression.type, routineDeclaration.type.returnType, "Wrong return type. ")
            visitExpression(returnExpr.expression, returnExpr.expression.type)
        }
        currentRoutineReturnExpressions.clear()
    }

    override fun processAndExpression(andExpression: AndExpression) {
        visitExpression(andExpression)
    }

    override fun processDivExpression(divExpression: DivExpression) {
        visitExpression(divExpression)
    }

    override fun processEqualsExpression(equalsExpression: EqualsExpression) {
        visitExpression(equalsExpression)
    }

    override fun processGreaterExpression(greaterExpression: GreaterExpression) {
        visitExpression(greaterExpression)
    }

    override fun processGreaterOrEqualsExpression(greaterOrEqualsExpression: GreaterOrEqualsExpression) {
        visitExpression(greaterOrEqualsExpression)
    }

    override fun processLessExpression(lessExpression: LessExpression) {
        visitExpression(lessExpression)
    }

    override fun processLessOrEqualsExpression(lessOrEqualsExpression: LessOrEqualsExpression) {
        visitExpression(lessOrEqualsExpression)
    }

    override fun processMinusExpression(minusExpression: MinusExpression) {
        visitExpression(minusExpression)
    }

    override fun processModExpression(modExpression: ModExpression) {
        visitExpression(modExpression)
    }

    override fun processMulExpression(mulExpression: MulExpression) {
        visitExpression(mulExpression)
    }

    override fun processNotEqualsExpression(notEqualsExpression: NotEqualsExpression) {
        visitExpression(notEqualsExpression)
    }

    override fun processOrExpression(orExpression: OrExpression) {
        visitExpression(orExpression)
    }

    override fun processPlusExpression(plusExpression: PlusExpression) {
        visitExpression(plusExpression)
    }

    override fun processXorExpression(xorExpression: XorExpression) {
        visitExpression(xorExpression)
    }

    override fun processBoolLiteral(boolLiteral: BoolLiteral) {
        visitExpression(boolLiteral)
    }

    override fun processIntegralLiteral(integralLiteral: IntegralLiteral) {
        visitExpression(integralLiteral)
    }

    override fun processRealLiteral(realLiteral: RealLiteral) {
        visitExpression(realLiteral)
    }

    override fun processUninitializedLiteral(uninitializedLiteral: UninitializedLiteral) {
        visitExpression(uninitializedLiteral)
    }

    override fun processArrayAccessExpression(arrayAccessExpression: ArrayAccessExpression) {
        visitExpression(arrayAccessExpression)
    }

    override fun processFieldAccessExpression(fieldAccessExpression: FieldAccessExpression) {
        visitExpression(fieldAccessExpression)
    }

    override fun processUnaryMinusExpression(unaryMinusExpression: UnaryMinusExpression) {
        visitExpression(unaryMinusExpression)
    }

    private fun visitExpression(expr: Expression, expectedType: Type? = null, errPredicate: String? = null): Type {
        val type = when (expr) {
            is IntegralLiteral -> IntegerType
            is RealLiteral -> RealType
            is BoolLiteral -> BoolType
            is UninitializedLiteral -> Nothing
            is UnaryMinusExpression -> visitExpression(expr.nestedExpression)
            is VariableAccessExpression -> expr.type
            is LogicalExpression -> visitLogicalExpression(expr)
            is BinaryExpression -> visitBinaryExpression(expr)
            is RoutineCall -> visitRoutineCallExpression(expr)
            is FieldAccessExpression -> expr.type
            is ArrayAccessExpression -> visitArrayAccessExpression(expr)
        }

        return validateTypes(type, expectedType, errPredicate)
    }

    private fun validateTypes(actualType: Type, expectedType: Type?, errPredicate: String? = null): Type {
        if (expectedType == null || actualType == expectedType) {
            return actualType
        }

        if (expectedType == RealType && actualType == IntegerType) {
            return expectedType
        }

        report("${errPredicate ?: ""}Expected type $expectedType but got $actualType")
    }

    private fun visitLogicalExpression(expr: LogicalExpression): Type {
        val leftType = visitExpression(expr.left)
        val rightType = visitExpression(expr.right)

        // logical and, or, xor
        if (expr is AndExpression || expr is OrExpression || expr is XorExpression) {
            if (leftType != BoolType) {
                report("Invalid Boolean expression with type ${expr.left.type}")
            }
            if (rightType != BoolType) {
                report("Invalid Boolean expression with type ${expr.right.type}")
            }
        }

        // ==, !=
        if (expr is EqualsExpression || expr is NotEqualsExpression) {
            return BoolType
        }

        // >, >=, <, <=,
        if (leftType != rightType) {
            val validBinaryCast =
                leftType == IntegerType && rightType == RealType || leftType == RealType && rightType == IntegerType
            if (!validBinaryCast) {
                report("Can't evaluate expression ${expr.type} with types $leftType, $rightType")
            }
        }

        return BoolType
    }

    // +, -, *, /, %
    private fun visitBinaryExpression(expr: BinaryExpression): Type {
        val leftType = visitExpression(expr.left)
        val rightType = visitExpression(expr.right)

        if (leftType != rightType) {
            val validBinaryCast =
                leftType == IntegerType && rightType == RealType || leftType == RealType && rightType == IntegerType
            if (!validBinaryCast) {
                report("Can't evaluate expression ${expr.type} with types $leftType, $rightType")
            }
            return RealType
        }

        return leftType
    }

    private fun visitRoutineCallExpression(expr: RoutineCall): Type {
        val parameterSize = expr.routineDeclaration.parameters.size
        val argumentSize = expr.arguments.size
        if (parameterSize != argumentSize) {
            report("Can't execute ${expr.routineDeclaration.name}, expected $parameterSize arguments but actual $argumentSize")
        }

        val arguments = expr.arguments
        val parameters = expr.routineDeclaration.parameters
        val mapArgumentToParameter: Map<Expression, ParameterDeclaration> = arguments.zip(parameters).toMap()

        for ((argument, parameter) in mapArgumentToParameter) {
            visitExpression(argument, parameter.type, "Invalid argument type. ")
        }

        return expr.type
    }

    private fun visitArrayAccessExpression(expr: ArrayAccessExpression): Type {
        expr.indexExpression?.let {
            visitExpression(it, IntegerType, "Wrong array index type. ")
        }
        visitExpression(expr.accessedExpression, expr.arrayType)
        return expr.arrayType.contentType
    }

    override fun processParameterDeclaration(parameterDeclaration: ParameterDeclaration) {}

    override fun processBreak(`break`: Break) {}

    override fun processContinue(`continue`: Continue) {}

    override fun processBoolType(boolType: BoolType) {}

    override fun processIntegerType(integerType: IntegerType) {}

    override fun processRealType(realType: RealType) {}

    override fun processUnitType(unitType: UnitType) {}

    override fun processMuParameter(muParameter: MuParameter) {}

    override fun processNothing(nothing: Nothing) {}

    override fun processRoutineType(routineType: RoutineType) {}

    override fun processArrayType(arrayType: ArrayType) {}

    override fun processRecordType(recordType: RecordType) {}

    override fun processVariableAccess(variableAccess: VariableAccessExpression) {}
}
