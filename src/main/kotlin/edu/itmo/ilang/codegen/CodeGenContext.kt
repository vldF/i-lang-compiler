package edu.itmo.ilang.codegen

import edu.itmo.ilang.ir.ValueDeclaration
import edu.itmo.ilang.util.report
import org.bytedeco.llvm.LLVM.LLVMValueRef

class CodeGenContext(
    val parent: CodeGenContext? = null,
    private val _currentFunction: LLVMValueRef? = parent?.currentFunction
) {
    private val valueDeclarations = mutableMapOf<ValueDeclaration, LLVMValueRef>()
    val currentFunction: LLVMValueRef?
        get() = _currentFunction ?: parent?.currentFunction

    fun storeValueDecl(decl: ValueDeclaration, value: LLVMValueRef) {
        if (valueDeclarations.containsKey(decl)) {
            report("redeclaration of $decl")
        }

        valueDeclarations[decl] = value
    }

    fun resolveValue(decl: ValueDeclaration): LLVMValueRef {
        return valueDeclarations[decl] ?: parent?.resolveValue(decl) ?: report("can't find declaration of $decl")
    }
}
