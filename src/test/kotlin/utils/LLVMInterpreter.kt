package utils

import org.bytedeco.javacpp.BytePointer
import org.bytedeco.llvm.LLVM.LLVMExecutionEngineRef
import org.bytedeco.llvm.LLVM.LLVMMCJITCompilerOptions
import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.bytedeco.llvm.global.LLVM.LLVMCreateMCJITCompilerForModule
import org.bytedeco.llvm.global.LLVM.LLVMGetFunctionAddress
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

class LLVMInterpreter(private val module: LLVMModuleRef) {
    private val errorBuffer = BytePointer()
    private val engine = LLVMExecutionEngineRef()
    private val options = LLVMMCJITCompilerOptions()

    fun interpretWithIntegerResult(routineName: String, args: List<Any>): Int {
        return interpret(routineName, args, ValueLayout.JAVA_INT) as Int
    }

    fun interpretWithRealResult(routineName: String, args: List<Any>): Double {
        return interpret(routineName, args, ValueLayout.JAVA_DOUBLE) as Double
    }

    fun interpretWithBooleanResult(routineName: String, args: List<Any>): Boolean {
        return interpret(routineName, args, ValueLayout.JAVA_BOOLEAN) as Boolean
    }

    fun interpretWithUnitResult(routineName: String, args: List<Any>) {
        interpret(routineName, args, null)
    }

    private fun interpret(routineName: String, args: List<Any>, returnLayout: ValueLayout?): Any {
        if (LLVMCreateMCJITCompilerForModule(engine, module, options, 3, errorBuffer) != 0) {
            error("Failed to create JIT compiler: " + errorBuffer.string)
        }

        val functionAddress = LLVMGetFunctionAddress(engine, routineName)
        val addressSegment = MemorySegment.ofAddress(functionAddress)
        val nativeLinker = Linker.nativeLinker()
        val functionDescriptor = getFunctionDescriptor(args, returnLayout)
        val function = nativeLinker.downcallHandle(addressSegment, functionDescriptor)

        return function.call(args)
    }

    // thank you, @PolymorphicSignature
    private fun MethodHandle.call(args: List<Any>): Any {
        return when {
            args.isEmpty() -> this.invoke()
            args.size == 1 -> this.invoke(args[0])
            args.size == 2 -> this.invoke(args[0], args[1])
            args.size == 3 -> this.invoke(args[0], args[1], args[2])
            args.size == 4 -> this.invoke(args[0], args[1], args[2], args[3])
            args.size == 5 -> this.invoke(args[0], args[1], args[2], args[3], args[4])
            else -> error("can't pass ${args.size} arguments")
        }
    }

    private fun getFunctionDescriptor(args: List<Any>, returnLayout: ValueLayout?): FunctionDescriptor {
        val argsLayouts = args.map { arg ->
            when (arg) {
                is Int -> ValueLayout.JAVA_INT
                is Double -> ValueLayout.JAVA_DOUBLE
                is Boolean -> ValueLayout.JAVA_INT
                else -> error("can't parse $arg")
            }
        }

        if (returnLayout == null) {
            return FunctionDescriptor.ofVoid(*argsLayouts.toTypedArray())
        }
        
        return FunctionDescriptor.of(returnLayout, *argsLayouts.toTypedArray())
    }
}
