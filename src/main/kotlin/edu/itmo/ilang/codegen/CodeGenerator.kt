package edu.itmo.ilang.codegen

import edu.itmo.ilang.ir.*
import edu.itmo.ilang.util.iCheck
import edu.itmo.ilang.util.report
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.*
import org.bytedeco.llvm.global.LLVM.*
import org.jetbrains.annotations.TestOnly
import java.io.Closeable

class CodeGenerator : Closeable {
    private val llvmContext = LLVMContextCreate()
    private val module = LLVMModuleCreateWithNameInContext("i-lang-program", llvmContext)
    private val builder = LLVMCreateBuilderInContext(llvmContext)

    private val triple = LLVMGetDefaultTargetTriple()
    private val target = LLVMTargetRef()
    private val errorBuffer = BytePointer()

    private val primaryTypes = PrimaryTypes()
    private val constants = Constants()

    private var codegenContext = CodeGenContext()

    inner class PrimaryTypes {
        val integerType: LLVMTypeRef = LLVMInt32TypeInContext(llvmContext)
        val doubleType: LLVMTypeRef = LLVMDoubleTypeInContext(llvmContext)
        val boolType: LLVMTypeRef = LLVMInt1TypeInContext(llvmContext)
        val voidType: LLVMTypeRef = LLVMVoidTypeInContext(llvmContext)
    }

    inner class Constants {
        val falseConst: LLVMValueRef = LLVMConstInt(primaryTypes.boolType, 0, /* SignExtend = */ 0)
        val trueConst: LLVMValueRef = LLVMConstInt(primaryTypes.boolType, 1, /* SignExtend = */ 0)

        val iZero: LLVMValueRef = LLVMConstInt(primaryTypes.integerType, 0, /* SignExtend = */ 0)
        val iOne: LLVMValueRef = LLVMConstInt(primaryTypes.integerType, 1, /* SignExtend = */ 0)

        val rZero: LLVMValueRef = LLVMConstReal(primaryTypes.doubleType, 0.0)
        val rOne: LLVMValueRef = LLVMConstReal(primaryTypes.doubleType, 1.0)
    }

    fun generate(program: Program) {
        initializeLlvm()

        for (declaration in program.declarations) {
            processTopLevelDeclaration(declaration)
        }

        for (declaration in program.declarations) {
            processDeclaration(declaration)
        }

        LLVMDumpModule(module)

        val verificationResult = LLVMVerifyModule(module, LLVMPrintMessageAction, errorBuffer)
        if (verificationResult != 0) {
            report("function verification error!\n${errorBuffer.string}")
        }

        if (LLVMGetTargetFromTriple(triple, target, errorBuffer) != 0) {
            report("Failed to get target from triple: " + errorBuffer.string)
        }

        iCheck(codegenContext.parent == null) { "all additional contexts must be popped" }
    }

    private fun initializeLlvm() {
        LLVMInitializeNativeTarget()
        LLVMInitializeNativeAsmPrinter()
        LLVMInitializeNativeAsmParser()

        val targetTriple = LLVMGetDefaultTargetTriple()
        LLVMSetTarget(module, targetTriple)
    }

    override fun close() {
        LLVMDisposeBuilder(builder)
        LLVMContextDispose(llvmContext)
        LLVMDisposeErrorMessage(errorBuffer)
    }

    fun saveObjectFile(fileName: String) {
        val cpu = "generic"
        val cpuFeatures = ""
        val optimizationLevel = 0
        val tm = LLVMCreateTargetMachine(
            target, triple.string, cpu, cpuFeatures, optimizationLevel,
            LLVMRelocDefault, LLVMCodeModelDefault
        )

        val outputFile = BytePointer(fileName)
        if (LLVMTargetMachineEmitToFile(tm, module, outputFile, LLVMObjectFile, errorBuffer) != 0) {
            report("Failed to emit relocatable object file: " + errorBuffer.string)
        }
    }

    @TestOnly
    fun getModule(): LLVMModuleRef {
        return module
    }

    private fun processTopLevelDeclaration(declaration: Declaration) {
        when (declaration) {
            is RoutineDeclaration -> processRoutineDeclaration(declaration)
            is TypeDeclaration -> processTypeDeclaration(declaration)
            else -> {}
        }
    }

    private fun processDeclaration(declaration: Declaration) {
        when (declaration) {
            is RoutineDeclaration -> processRoutineDefinition(declaration)
            is TypeDeclaration -> processTypeDefinition(declaration)
            else -> {}
        }
    }

    private fun processRoutineDeclaration(routineDeclaration: RoutineDeclaration) {
        val routineSignature = routineDeclaration.signatureType
        val routineName = routineDeclaration.name
        LLVMAddFunction(module, routineName, routineSignature)
    }

    private fun processRoutineDefinition(routineDeclaration: RoutineDeclaration) {
        pushContext()

        val function = LLVMGetNamedFunction(module, routineDeclaration.name)
        LLVMSetFunctionCallConv(function, LLVMCCallConv)

        for ((i, param) in routineDeclaration.parameters.withIndex()) {
            val paramValue = LLVMGetParam(function, i)
            codegenContext.storeValueDecl(param, paramValue)
        }

        val entryBlock = LLVMAppendBasicBlock(function, "entry")
        LLVMPositionBuilderAtEnd(builder, entryBlock)

        processBody(routineDeclaration.body!!)

        popContext()

        val verificationResult = LLVMVerifyFunction(function, LLVMPrintMessageAction)
        if (verificationResult != 0) {
            LLVMDumpValue(function)
            report("function verification error!")
        }
    }

    private fun processBody(body: Body) {
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
                is RoutineCall -> processRoutineCall(statement)
                is TypeDeclaration -> TODO()
            }
        }
    }

    private fun processIfStatement(statement: IfStatement) {
        val function = getLastFunction()

        val thenBody = statement.thenBody
        val elseBody = statement.elseBody ?: Body.EMPTY

        val thenBlock = LLVMAppendBasicBlock(function, "if-then")
        val elseBlock = LLVMAppendBasicBlock(function, "if-else")
        val mergeBlock = LLVMCreateBasicBlockInContext(llvmContext, "if-merge")

        val addMergeBlock = !thenBody.isTerminating || !elseBody.isTerminating
        if (addMergeBlock) {
            LLVMAppendExistingBasicBlock(function, mergeBlock)
        }

        val conditionExpr = statement.condition
        val conditionValue = processExpression(conditionExpr)
        val cmpExpr = LLVMBuildICmp(builder, LLVMIntEQ, conditionValue, constants.trueConst, "condition")

        LLVMBuildCondBr(builder, cmpExpr, thenBlock, elseBlock)

        withContext {
            LLVMPositionBuilderAtEnd(builder, thenBlock)
            processBody(thenBody)
            if (!thenBody.isTerminating) {
                LLVMBuildBr(builder, mergeBlock)
            }
        }

        withContext {
            LLVMPositionBuilderAtEnd(builder, elseBlock)
            processBody(elseBody)
            if (!elseBody.isTerminating) {
                LLVMBuildBr(builder, mergeBlock)
            }
        }

        if (addMergeBlock) {
            LLVMPositionBuilderAtEnd(builder, mergeBlock)
        } else {
            // nothing, we are at the function end
        }
    }

    private fun processVariableDeclaration(declaration: VariableDeclaration) {
        val name = declaration.name
        val type = declaration.type.llvmType

        val allocaValue = LLVMBuildAlloca(builder, type, name)
        codegenContext.storeValueDecl(declaration, allocaValue)

        val initializer = createVariableInitializerValue(declaration) ?: return

        LLVMBuildStore(builder, initializer, allocaValue)
    }

    private fun createVariableInitializerValue(declaration: VariableDeclaration): LLVMValueRef? {
        val initialExpression = declaration.initialExpression

        val initialValue = if (initialExpression == null || initialExpression == UninitializedLiteral) {
            null
        } else {
            processExpression(initialExpression)
        }

        return when (val type = declaration.type) {
            is ArrayType -> {
                val elementType = type.contentType.llvmType
                val arraySizeInBytes = LLVMConstInt(
                    primaryTypes.integerType,
                    type.sizeof,
                    /* SignExtend = */ 0)

                return LLVMBuildArrayMalloc(builder, elementType, arraySizeInBytes, "array-initializer")
            }
            else -> {
                initialValue
            }
        }
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
        val lhv = processAccessExpressionAsLhs(statement.lhs)

        val rhs = statement.rhs
        val rhv = processExpression(rhs)
        LLVMBuildStore(builder, rhv, lhv)
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
                LLVMConstReal(primaryTypes.doubleType, expression.value)
            }
            is UnaryMinusExpression -> {
                val nested = processExpression(expression.nestedExpression)
                when (expression.type) {
                    is RealType -> {
                        LLVMBuildFSub(builder, constants.rZero, nested, "unary-minus")
                    }

                    is IntegerType -> {
                        LLVMBuildSub(builder, constants.iZero, nested, "unary-minus")
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
            is DivExpression -> {
                processArithmeticBinaryExpressionWithCast(
                    expression.left,
                    expression.right,
                    ::LLVMBuildSDiv,
                    ::LLVMBuildFDiv
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
                equalBasedBinaryOperator(expression.left, expression.right, LLVMIntSLT, LLVMRealOLT)
            }
            is LessOrEqualsExpression -> {
                equalBasedBinaryOperator(expression.left, expression.right, LLVMIntSLE, LLVMRealOLE)
            }
            is NotEqualsExpression -> equalBasedBinaryOperator(expression.left, expression.right, LLVMIntNE, LLVMRealUNE)
            is AccessExpression -> processAccessExpressionAsRhs(expression)
            is AndExpression -> {
                LLVMBuildAnd(
                    builder,
                    processExpression(expression.left),
                    processExpression(expression.right),
                    "and"
                )
            }
            is OrExpression -> {
                LLVMBuildOr(
                    builder,
                    processExpression(expression.left),
                    processExpression(expression.right),
                    "or"
                )
            }
            is XorExpression -> {
                LLVMBuildXor(
                    builder,
                    processExpression(expression.left),
                    processExpression(expression.right),
                    "xor"
                )
            }
            is ModExpression -> {
                processArithmeticBinaryExpressionWithCast(
                    expression.left,
                    expression.right,
                    ::LLVMBuildSRem,
                    ::LLVMBuildFRem
                )
            }
            is RoutineCall -> processRoutineCall(expression)
            is UninitializedLiteral -> LLVMConstNull(primaryTypes.voidType)
        }
    }

    private fun processAccessExpressionAsLhs(expression: AccessExpression): LLVMValueRef {
        return when (expression) {
            is VariableAccessExpression -> {
                val variableDecl = expression.variable

                codegenContext.resolveValue(variableDecl)
            }
            is ArrayAccessExpression -> {
                getPointerToArrayElement(expression)
            }
            is FieldAccessExpression -> {
                val idx = expression.getFieldIndex
                getPointerToStructField(expression.accessedExpression, idx)
            }
        }
    }

    private val FieldAccessExpression.getFieldIndex: Int
        get() {
            val field = this.field
            return (this.accessedType as RecordType).fields.indexOfFirst { it.first == field }
        }

    private fun processAccessExpressionAsRhs(expression: AccessExpression): LLVMValueRef {
        return when (expression) {
            is VariableAccessExpression -> {
                val variable = expression.variable
                val storeValue = codegenContext.resolveValue(variable)
                if (variable is ParameterDeclaration) {
                    return storeValue
                }

                val valueType = LLVMGetAllocatedType(storeValue)

                LLVMBuildLoad2(builder, valueType, storeValue, variable.name + "_load")
            }
            is ArrayAccessExpression -> {
                val pointer = getPointerToArrayElement(expression)
                val elementType = expression.arrayType.contentType.llvmType

                LLVMBuildLoad2(builder, elementType, pointer, "load-array-elem")
            }
            is FieldAccessExpression -> {
                val idx = expression.getFieldIndex
                val pointer = getPointerToStructField(expression.accessedExpression, idx,)

                val fieldType = expression.type.llvmType

                return LLVMBuildLoad2(builder, fieldType, pointer, "load-field")
            }
        }
    }

    private fun getPointerToArrayElement(arrayAccess: ArrayAccessExpression): LLVMValueRef {
        val arrType = arrayAccess.arrayType.llvmType
        val elemType = arrayAccess.arrayType.contentType.llvmType

        val accessedExpression = processAccessExpressionAsLhs(arrayAccess.accessedExpression)
        val loadArr = LLVMBuildLoad2(builder, arrType, accessedExpression, "load-arr")

        val idx = processExpression(arrayAccess.indexExpression ?: error("index must be not null!")) // todo
        // as far as our array indexes starts with 1, we need to subtract 1 from it
        val idxPlusOne = LLVMBuildSub(builder, idx, constants.iOne, "array-index-correction")
        val idxPointerPointer = PointerPointer<LLVMValueRef>(/* size = */ 1)
        idxPointerPointer.put(0, idxPlusOne)

        return LLVMBuildGEP2(
            builder,
            elemType,
            loadArr,
            idxPointerPointer,
            1,
            "array-access"
        )
    }

    private fun getPointerToStructField(accessedExpression: AccessExpression, fieldIndex: Int): LLVMValueRef {
        return LLVMBuildStructGEP2(
            builder,
            accessedExpression.type.llvmType,
            processAccessExpressionAsLhs(accessedExpression),
            fieldIndex,
            "get_field"
        )
    }

    private fun processArithmeticBinaryExpressionWithCast(
        left: Expression,
        right: Expression,
        opBuilderForIntegers: (LLVMBuilderRef, LLVMValueRef, LLVMValueRef, String) -> LLVMValueRef,
        opBuilderForFP: (LLVMBuilderRef, LLVMValueRef, LLVMValueRef, String) -> LLVMValueRef,
    ): LLVMValueRef {
        var leftValue = processExpression(left)
        var rightValue = processExpression(right)

        if (left.type is IntegerType) {
            if (right.type is RealType) {
                // right is real -> generalize left to real
                leftValue = LLVMBuildSIToFP(
                    builder,
                    leftValue,
                    primaryTypes.doubleType,
                    "int-to-real"
                )

                return opBuilderForFP(builder, leftValue, rightValue, "binary-op-fp")
            }

            return opBuilderForIntegers(builder, leftValue, rightValue, "binary-op-ints")
        } else {
            if (right.type is IntegerType) {
                // left is real -> generalize right to real
                rightValue = LLVMBuildSIToFP(
                    builder,
                    rightValue,
                    primaryTypes.doubleType,
                    "int-to-real"
                )
            }

            return opBuilderForFP(builder, leftValue, rightValue, "binary-op-fp")
        }
    }

    private fun equalBasedBinaryOperator(
        left: Expression,
        right: Expression,
        intOp: Int,
        floatOp: Int,
    ): LLVMValueRef {
        return processArithmeticBinaryExpressionWithCast(
            left,
            right,
            opBuilderForIntegers = { builder, leftVal, rightVal, name -> LLVMBuildICmp(builder, intOp, leftVal, rightVal, name) },
            opBuilderForFP = { builder, leftVal, rightVal, name -> LLVMBuildFCmp(builder, floatOp, leftVal, rightVal, name) },
        )
    }

    private fun processRoutineCall(call: RoutineCall): LLVMValueRef {
        val routineSignature = call.routineDeclaration.signatureType
        val routineName = call.routineDeclaration.name
        val function = LLVMGetNamedFunction(module, routineName)

        val args = call.arguments.asCallArgs

        return LLVMBuildCall2(
            builder,
            routineSignature,
            function,
            args,
            call.arguments.size,
            routineName + "_call"
        )
    }

    private fun processTypeDeclaration(declaration: TypeDeclaration) {
        LLVMStructCreateNamed(llvmContext, declaration.name)
    }

    private fun processTypeDefinition(declaration: TypeDeclaration) {
        val structure = LLVMGetTypeByName(module, declaration.name)
        val type = declaration.type as? RecordType ?: error("structure excepted but got ${declaration.type}")
        val elementTypesPointer = type.elementTypes

        LLVMStructSetBody(structure, elementTypesPointer, type.fields.size, /* Packed = */ 0)
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
            is RealType -> primaryTypes.doubleType
            is BoolType -> primaryTypes.boolType
            UnitType -> primaryTypes.voidType
            is ArrayType -> LLVMPointerTypeInContext(llvmContext, 0)
            is RecordType -> LLVMStructTypeInContext(llvmContext, this.elementTypes, this.fields.size, /* Packed = */ 0)
            else -> report("unsupported type $this")
        }

    private val RecordType.elementTypes: PointerPointer<LLVMTypeRef>
        get() {
            val elementTypes = fields.map { it.second.llvmType }.toTypedArray()
            val elementTypePointer = PointerPointer<LLVMTypeRef>(fields.size.toLong())
            elementTypePointer.put(*elementTypes)

            return elementTypePointer
        }


    private val Collection<Type>.llvmType: PointerPointer<LLVMTypeRef>
        get() = PointerPointer(*this.map { it.llvmType }.toTypedArray())

    @Suppress("RecursivePropertyAccessor")
    private val Type.sizeof: Long
        get() {
            return when (this) {
                BoolType -> 1
                IntegerType -> 32
                RealType -> 64
                is ArrayType -> this.contentType.sizeof * (this.size ?: 0)
                is RecordType -> TODO()
                else -> 0
            }
        }

    private fun pushContext() {
        codegenContext = CodeGenContext(codegenContext)
    }

    private fun popContext() {
        codegenContext = codegenContext.parent ?: report("root context can't be pop-ed")
    }

    private fun getLastBasicBlock(): LLVMBasicBlockRef {
        val function = getLastFunction()
        return LLVMGetLastBasicBlock(function)
    }

    private fun getLastFunction(): LLVMValueRef {
        return LLVMGetLastFunction(module)
    }

    private val List<Expression>.asCallArgs: PointerPointer<LLVMValueRef>
        get() {
            val values = this.map(::processExpression)

            return PointerPointer(*values.toTypedArray())
        }

    private fun withContext(func: () -> Unit) {
        pushContext()
        func()
        popContext()
    }
}
