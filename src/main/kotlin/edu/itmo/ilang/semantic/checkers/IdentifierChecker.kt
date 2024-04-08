package edu.itmo.ilang.semantic.checkers

import edu.itmo.ilang.ir.IrProcessor
import edu.itmo.ilang.ir.model.*
import edu.itmo.ilang.ir.model.Nothing
import edu.itmo.ilang.util.report

// 1. identifier length < 256
// 2. identifier != "uninitialized"
class IdentifierChecker : Checker, IrProcessor<Unit> {

    private fun checkIdentifier(identifier: String) {
        if (identifier.length >= 256) {
            report("identifier $identifier length must be less then 256")
        }
        if (identifier == "uninitialized") {
            report("name \"uninitialized\" is reserved")
        }
    }

    override fun check(program: Program) {
        process(program)
    }

    override fun processProgram(program: Program) {
        program.declarations.forEach(::process)
    }

    override fun processBody(body: Body) {
        body.statements.forEach(::process)
    }

    override fun processForLoop(forLoop: ForLoop) {
        process(forLoop.loopVariableDecl)
    }

    override fun processTypeDeclaration(typeDeclaration: TypeDeclaration) {
        checkIdentifier(typeDeclaration.name)
        process(typeDeclaration.type)
    }

    override fun processVariableDeclaration(variableDeclaration: VariableDeclaration) {
        checkIdentifier(variableDeclaration.name)
    }

    override fun processParameterDeclaration(parameterDeclaration: ParameterDeclaration) {
        checkIdentifier(parameterDeclaration.name)
    }

    override fun processRoutineDeclaration(routineDeclaration: RoutineDeclaration) {
        checkIdentifier(routineDeclaration.name.drop(1))
        routineDeclaration.parameters.forEach(::process)
        routineDeclaration.body?.let { process(it) }
    }

    override fun processRecordType(recordType: RecordType) {
        recordType.fields.map { it.first }.forEach(::checkIdentifier)
    }

    override fun processVariableAccess(variableAccess: VariableAccessExpression) {
    }

    override fun processArrayType(arrayType: ArrayType) {
    }

    override fun processRoutineType(routineType: RoutineType) {
    }

    override fun processAssignment(assignment: Assignment) {
    }

    override fun processBreak(`break`: Break) {
    }

    override fun processContinue(`continue`: Continue) {
    }

    override fun processIfStatement(ifStatement: IfStatement) {
    }

    override fun processReturn(`return`: Return) {
    }

    override fun processRoutineCall(routineCall: RoutineCall) {
    }

    override fun processWhileLoop(whileLoop: WhileLoop) {
    }

    override fun processAndExpression(andExpression: AndExpression) {
    }

    override fun processDivExpression(divExpression: DivExpression) {
    }

    override fun processEqualsExpression(equalsExpression: EqualsExpression) {
    }

    override fun processGreaterExpression(greaterExpression: GreaterExpression) {
    }

    override fun processGreaterOrEqualsExpression(greaterOrEqualsExpression: GreaterOrEqualsExpression) {
    }

    override fun processLessExpression(lessExpression: LessExpression) {
    }

    override fun processLessOrEqualsExpression(lessOrEqualsExpression: LessOrEqualsExpression) {
    }

    override fun processMinusExpression(minusExpression: MinusExpression) {
    }

    override fun processModExpression(modExpression: ModExpression) {
    }

    override fun processMulExpression(mulExpression: MulExpression) {
    }

    override fun processNotEqualsExpression(notEqualsExpression: NotEqualsExpression) {
    }

    override fun processOrExpression(orExpression: OrExpression) {
    }

    override fun processPlusExpression(plusExpression: PlusExpression) {
    }

    override fun processXorExpression(xorExpression: XorExpression) {
    }

    override fun processBoolLiteral(boolLiteral: BoolLiteral) {
    }

    override fun processIntegralLiteral(integralLiteral: IntegralLiteral) {
    }

    override fun processRealLiteral(realLiteral: RealLiteral) {
    }

    override fun processUninitializedLiteral(uninitializedLiteral: UninitializedLiteral) {
    }

    override fun processArrayAccessExpression(arrayAccessExpression: ArrayAccessExpression) {
    }

    override fun processFieldAccessExpression(fieldAccessExpression: FieldAccessExpression) {
    }

    override fun processUnaryMinusExpression(unaryMinusExpression: UnaryMinusExpression) {
    }

    override fun processBoolType(boolType: BoolType) {
    }

    override fun processIntegerType(integerType: IntegerType) {
    }

    override fun processRealType(realType: RealType) {
    }

    override fun processUnitType(unitType: UnitType) {
    }

    override fun processMuParameter(muParameter: MuParameter) {
    }

    override fun processNothing(nothing: Nothing) {
    }
}