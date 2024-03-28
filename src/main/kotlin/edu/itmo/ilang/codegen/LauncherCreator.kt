package edu.itmo.ilang.codegen

import edu.itmo.ilang.ir.model.*
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.writeText

class LauncherCreator(programIr: Program, private val launcherPath: Path) {

    private val supportedRoutineDeclarations = programIr.declarations
        .filterIsInstance<RoutineDeclaration>()
        .filter {
            it.type.returnType is PrimitiveType &&
                    it.type.argumentTypes.all { argType -> argType is PrimitiveType }
        }

    fun createLauncher() {
        launcherPath.createFile()
        launcherPath.writeText(createLauncherSourceCode())
    }

    private fun createLauncherSourceCode(): String {
        return """
        #include <iostream>
        #include <cstring>

        extern "C" {
            ${getFunctionDeclarations()}
        }

        int main(const int argc, const char *argv[]) {
            if (argc < 2) {
                std::cout << "please, specify function name" << std::endl;
                return 0;
            }
            
            const char * original_name = argv[1];
            char name[256] = "$";
            std::strcat(name, original_name);
                
            ${getFunctionsInvokeBranches()}
            
            std::cout << original_name << " function is not declared" << std::endl;
        }
        """.trimIndent()
    }

    private fun getFunctionDeclarations(): String {
        val routineSignatures = supportedRoutineDeclarations.map { routineDeclaration ->
            val routineType = routineDeclaration.type
            "${routineType.returnType.toCType()} ${routineDeclaration.name}(${
                routineType.argumentTypes.joinToString(separator = ", ") { it.toCType() }
            });"
        }

        return routineSignatures.joinToString(separator = System.lineSeparator())
    }

    private fun getFunctionsInvokeBranches(): String {
        fun getArguments(routine: RoutineDeclaration): String {
            var counter = 2
            val args = routine.type.argumentTypes.map {
                when(it) {
                    is IntegerType -> "atoi(argv[${counter++}])"
                    is RealType -> "atof(argv[${counter++}])"
                    is BoolType -> {"(strcmp(argv[${counter++}], \"true\") == 0 ? 1 : 0)"}

                    else -> IllegalStateException()
                }
            }
            return args.joinToString(separator = ", ")
        }

        val branches = supportedRoutineDeclarations.map {
            val functionName = it.name
            val argumentsSize = it.type.argumentTypes.size
            val functionCallString = "$functionName(${getArguments(it)})"
            """
                if (strcmp(name, "$functionName") == 0) {
                    if (argc != ${argumentsSize + 2}) {
                        std::cout << "expected " << $argumentsSize << " function arguments, but got " << argc - 2 << std::endl;
                        return 0;
                    }
                    ${if (it.type.returnType is BoolType) 
                        "if ($functionCallString) std::cout << \"true\" << std::endl; else std::cout << \"false\" << std::endl;"
                    else 
                        "std::cout << $functionCallString << std::endl;"}
                    return 0;
                }
            """.trimIndent()
        }

        return branches.joinToString(separator = System.lineSeparator())
    }

    private fun Type.toCType(): String {
        return when(this) {
            is IntegerType, is BoolType -> "int"
            is RealType -> "double"
            is UnitType -> "void"

            else -> throw IllegalStateException("unsupported type for function signature")
        }
    }
}