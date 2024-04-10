package edu.itmo.ilang.semantic.checkers

import edu.itmo.ilang.ir.IrProcessor
import edu.itmo.ilang.ir.model.*
import edu.itmo.ilang.ir.model.Nothing
import edu.itmo.ilang.util.report

class TypeChecker : Checker, IrProcessor<Unit> {
    private val currentRoutineReturnExpressions = mutableListOf<Return>()

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

        // Check assign declaration: https://cs-uni.ru/images/f/f8/Project_I.pdf
        if (assignment.lhs.type == IntegerType && assignment.rhs.isOneOf(RealType, BoolType) ||
            assignment.lhs.type == RealType && assignment.rhs.isOneOf(IntegerType, BoolType)
        ) {
            return
        }

        // I don't check if num is 1 or 0
        // It must check not TypeChecker cause expression could be evaluated in runtime
        if (assignment.lhs.type == BoolType && assignment.rhs.isOneOf(IntegerType, RealType)) {
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
        currentRoutineReturnExpressions.add(`return`)
    }

    override fun processWhileLoop(whileLoop: WhileLoop) {
        visitExpression(whileLoop.condition, BoolType)
        process(whileLoop.body)
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
            report("Can't evaluate expression ${expr.type} with types $leftType, $rightType")
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

    private fun Expression.isOneOf(vararg types: Type): Boolean {
        for (type in types) {
            if (this.type == type) {
                return true
            }
        }
        return false
    }

    override fun processRoutineCall(routineCall: RoutineCall) {}

    override fun processTypeDeclaration(typeDeclaration: TypeDeclaration) {}

    override fun processAndExpression(andExpression: AndExpression) {}

    override fun processDivExpression(divExpression: DivExpression) {}

    override fun processEqualsExpression(equalsExpression: EqualsExpression) {}

    override fun processGreaterExpression(greaterExpression: GreaterExpression) {}

    override fun processGreaterOrEqualsExpression(greaterOrEqualsExpression: GreaterOrEqualsExpression) {}

    override fun processLessExpression(lessExpression: LessExpression) {}

    override fun processLessOrEqualsExpression(lessOrEqualsExpression: LessOrEqualsExpression) {}

    override fun processMinusExpression(minusExpression: MinusExpression) {}

    override fun processModExpression(modExpression: ModExpression) {}

    override fun processMulExpression(mulExpression: MulExpression) {}

    override fun processNotEqualsExpression(notEqualsExpression: NotEqualsExpression) {}

    override fun processOrExpression(orExpression: OrExpression) {}

    override fun processPlusExpression(plusExpression: PlusExpression) {}

    override fun processXorExpression(xorExpression: XorExpression) {}

    override fun processBoolLiteral(boolLiteral: BoolLiteral) {}

    override fun processIntegralLiteral(integralLiteral: IntegralLiteral) {}

    override fun processRealLiteral(realLiteral: RealLiteral) {}

    override fun processUninitializedLiteral(uninitializedLiteral: UninitializedLiteral) {}

    override fun processArrayAccessExpression(arrayAccessExpression: ArrayAccessExpression) {}

    override fun processFieldAccessExpression(fieldAccessExpression: FieldAccessExpression) {}

    override fun processUnaryMinusExpression(unaryMinusExpression: UnaryMinusExpression) {}

    override fun processVariableAccess(variableAccess: VariableAccessExpression) {}

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
}
