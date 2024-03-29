package edu.itmo.ilang.ir

import edu.itmo.ilang.ir.model.*
import edu.itmo.ilang.ir.model.Nothing

interface IrProcessor<T> {
    fun process(entry: IrEntry): T {
        return when(entry) {
            is Body -> processBody(entry)
            is Assignment -> processAssignment(entry)
            is Break -> processBreak(entry)
            is Continue -> processContinue(entry)
            is ForLoop -> processForLoop(entry)
            is IfStatement -> processIfStatement(entry)
            is Return -> processReturn(entry)
            is RoutineCall -> processRoutineCall(entry)
            is WhileLoop -> processWhileLoop(entry)
            is TypeDeclaration -> processTypeDeclaration(entry)
            is VariableDeclaration -> processVariableDeclaration(entry)
            is ParameterDeclaration -> processParameterDeclaration(entry)
            is RoutineDeclaration -> processRoutineDeclaration(entry)
            is AndExpression -> processAndExpression(entry)
            is DivExpression -> processDivExpression(entry)
            is EqualsExpression -> processEqualsExpression(entry)
            is GreaterExpression -> processGreaterExpression(entry)
            is GreaterOrEqualsExpression -> processGreaterOrEqualsExpression(entry)
            is LessExpression -> processLessExpression(entry)
            is LessOrEqualsExpression -> processLessOrEqualsExpression(entry)
            is MinusExpression -> processMinusExpression(entry)
            is ModExpression -> processModExpression(entry)
            is MulExpression -> processMulExpression(entry)
            is NotEqualsExpression -> processNotEqualsExpression(entry)
            is OrExpression -> processOrExpression(entry)
            is PlusExpression -> processPlusExpression(entry)
            is XorExpression -> processXorExpression(entry)
            is BoolLiteral -> processBoolLiteral(entry)
            is IntegralLiteral -> processIntegralLiteral(entry)
            is RealLiteral -> processRealLiteral(entry)
            is UninitializedLiteral -> processUninitializedLiteral(entry)
            is ArrayAccessExpression -> processArrayAccessExpression(entry)
            is FieldAccessExpression -> processFieldAccessExpression(entry)
            is UnaryMinusExpression -> processUnaryMinusExpression(entry)
            is Program -> processProgram(entry)
            is BoolType -> processBoolType(entry)
            is IntegerType -> processIntegerType(entry)
            is RealType -> processRealType(entry)
            is UnitType -> processUnitType(entry)
            is MuParameter -> processMuParameter(entry)
            is Nothing -> processNothing(entry)
            is RoutineType -> processRoutineType(entry)
            is ArrayType -> processArrayType(entry)
            is RecordType -> processRecordType(entry)
            is VariableAccessExpression -> processVariableAccess(entry)
        }
    }

    fun processProgram(program: Program): T

    fun processBody(body: Body): T

    fun processAssignment(assignment: Assignment): T

    fun processBreak(`break`: Break): T

    fun processContinue(`continue`: Continue): T

    fun processForLoop(forLoop: ForLoop): T

    fun processIfStatement(ifStatement: IfStatement): T

    fun processReturn(`return`: Return): T

    fun processRoutineCall(routineCall: RoutineCall): T

    fun processWhileLoop(whileLoop: WhileLoop): T

    fun processTypeDeclaration(typeDeclaration: TypeDeclaration): T

    fun processVariableDeclaration(variableDeclaration: VariableDeclaration): T

    fun processParameterDeclaration(parameterDeclaration: ParameterDeclaration): T

    fun processRoutineDeclaration(routineDeclaration: RoutineDeclaration): T

    fun processAndExpression(andExpression: AndExpression): T

    fun processDivExpression(divExpression: DivExpression): T

    fun processEqualsExpression(equalsExpression: EqualsExpression): T

    fun processGreaterExpression(greaterExpression: GreaterExpression): T

    fun processGreaterOrEqualsExpression(greaterOrEqualsExpression: GreaterOrEqualsExpression): T

    fun processLessExpression(lessExpression: LessExpression): T

    fun processLessOrEqualsExpression(lessOrEqualsExpression: LessOrEqualsExpression): T

    fun processMinusExpression(minusExpression: MinusExpression): T

    fun processModExpression(modExpression: ModExpression): T

    fun processMulExpression(mulExpression: MulExpression): T

    fun processNotEqualsExpression(notEqualsExpression: NotEqualsExpression): T

    fun processOrExpression(orExpression: OrExpression): T

    fun processPlusExpression(plusExpression: PlusExpression): T

    fun processXorExpression(xorExpression: XorExpression): T

    fun processBoolLiteral(boolLiteral: BoolLiteral): T

    fun processIntegralLiteral(integralLiteral: IntegralLiteral): T

    fun processRealLiteral(realLiteral: RealLiteral): T

    fun processUninitializedLiteral(uninitializedLiteral: UninitializedLiteral): T

    fun processArrayAccessExpression(arrayAccessExpression: ArrayAccessExpression): T

    fun processFieldAccessExpression(fieldAccessExpression: FieldAccessExpression): T

    fun processUnaryMinusExpression(unaryMinusExpression: UnaryMinusExpression): T

    fun processBoolType(boolType: BoolType): T

    fun processIntegerType(integerType: IntegerType): T

    fun processRealType(realType: RealType): T

    fun processUnitType(unitType: UnitType): T

    fun processMuParameter(muParameter: MuParameter): T

    fun processNothing(nothing: Nothing): T

    fun processRoutineType(routineType: RoutineType): T

    fun processArrayType(arrayType: ArrayType): T

    fun processRecordType(recordType: RecordType): T

    fun processVariableAccess(variableAccess: VariableAccessExpression): T
}