package edu.itmo.ilang.codegen

import edu.itmo.ilang.ir.*
import edu.itmo.ilang.util.report
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMBuilderRef
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM.*

class CodeGenerator {
    // todo: add debug renderer for a function CFG preview with LLVMViewFunctionCFG
    private val llvmContext = LLVMContextCreate()
    private val module = LLVMModuleCreateWithNameInContext("i-lang-program", llvmContext)
    private val builder = LLVMCreateBuilderInContext(llvmContext)

    private val primaryTypes = PrimaryTypes()
    private val constants = Constants()

    private var codegenContext = CodeGenContext(routine = null)

    fun generate(program: Program) {
        initializeLlvm()

        try {
            for (declaration in program.declarations) {
                processDeclaration(declaration)
            }

            LLVMDumpModule(module)
        } finally {
            deinitializeLlvm()
        }
    }

    private fun initializeLlvm() {
        LLVMLinkInMCJIT()
        LLVMInitializeNativeAsmPrinter()
        LLVMInitializeNativeAsmParser()
        LLVMInitializeNativeTarget()
    }

    private fun deinitializeLlvm() {
        LLVMDisposeBuilder(builder)
        LLVMContextDispose(llvmContext)
    }

    private fun processDeclaration(declaration: Declaration) {
        when (declaration) {
            is RoutineDeclaration -> processRoutineDeclaration(declaration)
            else -> {}
        }
    }

    private fun processRoutineDeclaration(routineDeclaration: RoutineDeclaration) {
        pushContext(routineDeclaration)

        val routineSignature = routineDeclaration.signatureType
        val routineName = routineDeclaration.name
        val function = LLVMAddFunction(module, routineName, routineSignature)
        LLVMSetFunctionCallConv(function, LLVMCCallConv)

        for ((i, param) in routineDeclaration.parameters.withIndex()) {
            val paramValue = LLVMGetParam(function, i)
            codegenContext.storeValueDecl(param, paramValue)
        }

        generateFunctionBody(routineDeclaration.body!!, function)

        popContext()
    }

    private fun generateFunctionBody(body: Body, parentFunction: LLVMValueRef) {
        val entryBlock = LLVMAppendBasicBlockInContext(llvmContext, parentFunction, "entry")
        LLVMPositionBuilderAtEnd(builder, entryBlock)

        for (statement in body.statements) {
            when (statement) {
                is IfStatement -> processIfStatement(statement)
                is Assignment -> processAssignment(statement)
                is VariableDeclaration -> processVariableDeclaration(statement)
                is Return -> processReturn(statement)
                is ForLoop -> TODO()
                is WhileLoop -> TODO()
                Break -> TODO()
                Continue -> TODO()
                is RoutineCall -> TODO()
                is TypeDeclaration -> TODO()
            }
        }
    }

    private fun processIfStatement(statement: IfStatement) {
        val conditionExpr = processExpression(statement.condition)
        val cmpExpr = LLVMBuildICmp(builder, LLVMIntEQ, conditionExpr, constants.one, statement.condition.toString())
    }

    private fun processVariableDeclaration(declaration: VariableDeclaration) {
        val name = declaration.name
        val type = declaration.type.llvmType

        val allocaValue = LLVMBuildAlloca(builder, type, name)
        codegenContext.storeValueDecl(declaration, allocaValue)

        val initializer = declaration.initialExpression ?: return

        val initValue = processExpression(initializer)
        LLVMBuildStore(builder, initValue, allocaValue)
    }

    private fun processReturn(statement: Return) {
        val expression = statement.expression
        if (expression != null) {
            val value = processExpression(expression)
            LLVMBuildRet(builder, value)
        } else {
            LLVMBuildRetVoid(builder)
        }
    }

    private fun processAssignment(statement: Assignment) {
        val lhv = when (val lhs = statement.lhs) {
            is VariableAccessExpression -> {
                val variableDecl = lhs.variable
                codegenContext.resolveValue(variableDecl)
            }
            is ArrayAccessExpression -> TODO()
            is FieldAccessExpression -> TODO()
        }

        val rhs = statement.rhs
        val rhv = processExpression(rhs)
        LLVMBuildStore(builder, lhv, rhv)
    }

    private fun processExpression(expression: Expression): LLVMValueRef {
        return when (expression) {
            is IntegralLiteral -> {
                LLVMConstInt(primaryTypes.integerType, expression.value.toLong(), /* SignExtend = */ 0)
            }
            is BoolLiteral -> {
                val intValue = if (expression.value) 1L else 0L
                LLVMConstInt(primaryTypes.boolType, intValue, /* SignExtend = */ 0)
            }
            is RealLiteral -> {
                LLVMConstReal(primaryTypes.realType, expression.value)
            }
            is UnaryMinusExpression -> {
                val nested = processExpression(expression.nestedExpression)
                when (expression.type) {
                    is RealType -> {
                        LLVMBuildFSub(builder, constants.zero, nested, expression.toString())
                    }

                    is IntegerType -> {
                        LLVMBuildSub(builder, constants.zero, nested, expression.toString())
                    }

                    else -> report("can't use unary minus with $expression")
                }
            }
            is MinusExpression -> {
                processArithmeticBinaryExpressionWithCast(
                    expression.left,
                    expression.right,
                    ::LLVMBuildSub,
                    ::LLVMBuildFSub,
                )
            }
            is MulExpression -> {
                processArithmeticBinaryExpressionWithCast(
                    expression.left,
                    expression.right,
                    ::LLVMBuildMul,
                    ::LLVMBuildFMul,
                )
            }
            is PlusExpression -> {
                processArithmeticBinaryExpressionWithCast(
                    expression.left,
                    expression.right,
                    ::LLVMBuildAdd,
                    ::LLVMBuildFAdd,
                )
            }
            is EqualsExpression -> {
                equalBasedBinaryOperator(expression.left, expression.right, LLVMIntEQ, LLVMRealOEQ)
            }
            is GreaterExpression -> {
                equalBasedBinaryOperator(expression.left, expression.right, LLVMIntSGT, LLVMRealOGT)
            }
            is GreaterOrEqualsExpression -> {
                equalBasedBinaryOperator(expression.left, expression.right, LLVMIntSGE, LLVMRealOGE)
            }
            is LessExpression -> {
                equalBasedBinaryOperator(expression.left, expression.right, LLVMIntUGT, LLVMRealOLT)
            }
            is LessOrEqualsExpression -> {
                equalBasedBinaryOperator(expression.left, expression.right, LLVMIntULE, LLVMRealOLE)
            }
            is NotEqualsExpression -> equalBasedBinaryOperator(expression.left, expression.right, LLVMIntNE, LLVMRealUNE)
            is ArrayAccessExpression -> TODO()
            is FieldAccessExpression -> TODO()
            is VariableAccessExpression -> {
                codegenContext.resolveValue(expression.variable)
            }
            is AndExpression -> TODO()
            is DivExpression -> TODO()
            is OrExpression -> TODO()
            is XorExpression -> TODO()
            is ModExpression -> TODO()
            is RoutineCall -> TODO()
        }
    }

    private fun processArithmeticBinaryExpressionWithCast(
        left: Expression,
        right: Expression,
        opBuilderForIntegers: (LLVMBuilderRef, LLVMValueRef, LLVMValueRef, String) -> LLVMValueRef,
        opBuilderForFP: (LLVMBuilderRef, LLVMValueRef, LLVMValueRef, String) -> LLVMValueRef
    ): LLVMValueRef {
        var leftValue = processExpression(left)
        var rightValue = processExpression(right)

        if (left.type is IntegerType) {
            if (right.type is RealType) {
                leftValue = LLVMBuildSIToFP(
                    builder,
                    leftValue,
                    primaryTypes.realType,
                    "cast left value to floating point"
                )

                return opBuilderForFP(builder, leftValue, rightValue, "binary op")
            }
        } else {
            if (right.type is IntegerType) {
                rightValue = LLVMBuildSIToFP(
                    builder,
                    rightValue,
                    primaryTypes.realType,
                    "cast right value to integer"
                )

                return opBuilderForFP(builder, leftValue, rightValue, "binary op")
            }
        }

        return opBuilderForIntegers(builder, leftValue, rightValue, "binary op")
    }

    private fun equalBasedBinaryOperator(
        left: Expression,
        right: Expression,
        intOp: Int,
        floatOp: Int
    ): LLVMValueRef {
        return processArithmeticBinaryExpressionWithCast(
            left,
            right,
            opBuilderForIntegers = { builder, leftVal, rightVal, name -> LLVMBuildICmp(builder, intOp, leftVal, rightVal, name) },
            opBuilderForFP = { builder, leftVal, rightVal, name -> LLVMBuildFCmp(builder, floatOp, leftVal, rightVal, name) },
        )
    }

    inner class PrimaryTypes {
        val integerType: LLVMTypeRef = LLVMInt32TypeInContext(llvmContext)
        val realType: LLVMTypeRef = LLVMDoubleTypeInContext(llvmContext)
        val boolType: LLVMTypeRef = LLVMInt1TypeInContext(llvmContext)
        val voidType: LLVMTypeRef = LLVMVoidTypeInContext(llvmContext)
    }

    inner class Constants {
        val zero = LLVMConstInt(primaryTypes.integerType, 1, /* SignExtend = */ 0)
        val one = LLVMConstInt(primaryTypes.integerType, 1, /* SignExtend = */ 0)
    }

    private val RoutineDeclaration.signatureType: LLVMTypeRef
        get() {
            val retType = this.type.returnType.llvmType
            val argumentTypes = this.type.argumentTypes.llvmType
            return LLVMFunctionType(retType, argumentTypes, this.type.argumentTypes.size, /* IsVarArg = */ 0)
        }

    private val Type.llvmType: LLVMTypeRef
        get() = when(this) {
            is IntegerType -> primaryTypes.integerType
            is RealType -> primaryTypes.realType
            is BoolType -> primaryTypes.boolType
            UnitType -> primaryTypes.voidType
            else -> report("unsupported type $this")
        }

    private val Collection<Type>.llvmType: PointerPointer<LLVMTypeRef>
        get() = PointerPointer(*this.map { it.llvmType }.toTypedArray())

    private fun pushContext(routine: RoutineDeclaration) {
        codegenContext = CodeGenContext(codegenContext, routine)
    }

    private fun popContext() {
        codegenContext = codegenContext.parent ?: report("root context can't be pop-ed")
    }
}
