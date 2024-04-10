package tests.analysis

import edu.itmo.ilang.semantic.checkers.TypeChecker
import edu.itmo.ilang.util.ILangGeneralException
import generateIr
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class TypeCheckerTest {
    @Test
    fun testInitializeWrongType() {
        val example = """
        routine test() : integer is
          var num1 : integer is true
        
          return 0
        end
        """.trimIndent()


        assertFailWith<ILangGeneralException>("fatal error: Wrong initialize type. Expected type IntegerType but got BoolType") {
            val ir = generateIr(example)
            TypeChecker().check(ir)
        }
    }

    @Test
    fun testAssignWrongType() {
        val example = """
        routine test() : integer is
          var num1 : integer
          num1 := false
        
          return 0
        end
        """.trimIndent()

        assertFailWith<ILangGeneralException>("fatal error: Can't assign expression with type BoolType to left side with type IntegerType") {
            val ir = generateIr(example)
            TypeChecker().check(ir)
        }
    }

    @Test
    fun testWrongRoutineReturnType() {
        val example = """
        routine test() : integer is
          var num1 : bool is false
        
          return num1
        end
        """.trimIndent()

        assertFailWith<ILangGeneralException>("fatal error: Wrong return type. Expected type IntegerType but got BoolType") {
            val ir = generateIr(example)
            TypeChecker().check(ir)
        }
    }

    @Test
    fun testWrongUnitRoutineReturnType() {
        val example = """
        routine test() : integer is
          var num1 : bool is false
        
          return
        end
        """.trimIndent()

        assertFailWith<ILangGeneralException>("fatal error: Expected return type IntegerType but got UnitType for \$test") {
            val ir = generateIr(example)
            TypeChecker().check(ir)
        }
    }

    @Test
    fun testWrongImplicitUnitRoutineReturnType() {
        val example = """
        routine test() : integer is
          var num1 : bool is false
        end
        """.trimIndent()

        assertFailWith<ILangGeneralException>("fatal error: Expected return type IntegerType but got UnitType for \$test") {
            val ir = generateIr(example)
            TypeChecker().check(ir)
        }
    }

    @Test
    fun testIntegerTypeCanBeAssignedToReal() {
        val example = """
        routine test(a : integer) : real is
          var num1 : integer is 10
        
          return a + num1
        end
        """.trimIndent()

        assertDoesNotThrow {
            val ir = generateIr(example)
            TypeChecker().check(ir)
        }
    }

    @Test
    fun testRealTypeCantBeAssignedToInteger() {
        val example = """
        routine test(a : real) : integer is
          var num1 : integer is 10
        
          return a + num1 // (integer + real) cast to real
        end
        """.trimIndent()

        assertFailWith<ILangGeneralException>("fatal error: Wrong return type. Expected type IntegerType but got RealType") {
            val ir = generateIr(example)
            TypeChecker().check(ir)
        }
    }

    @Test
    fun testConditionalWrongUnitRoutineType() {
        val example = """
        routine test(a : integer) : integer is
          var num1 : bool is false
        
          if a > 10 then
            return num1
          end
        
          return a
        end
        """.trimIndent()

        assertFailWith<ILangGeneralException>("fatal error: Wrong return type. Expected type IntegerType but got BoolType") {
            val ir = generateIr(example)
            TypeChecker().check(ir)
        }
    }

    @Test
    fun testUnitRoutineWrongReturnType() {
        val example = """
        routine test() is
          var num1 : bool is false
        
          return num1
        end
        """.trimIndent()

        assertFailWith<ILangGeneralException>("fatal error: Wrong return type. Expected type UnitType but got BoolType") {
            val ir = generateIr(example)
            TypeChecker().check(ir)
        }
    }

    @Test
    fun testLogicalExpressionWrongType() {
        val example = """
        routine test() is
          var num : integer is 1
          var invalidBoolAssign : bool is true and num // := true && num
          
          return
        end
        """.trimIndent()

        assertFailWith<ILangGeneralException>("fatal error: Invalid Boolean expression with type IntegerType") {
            val ir = generateIr(example)
            TypeChecker().check(ir)
        }
    }

    @Test
    fun testRoutineWrongArgumentSize() {
        val example = """        
        routine getBool(arg1: bool, arg2: integer): bool is 
          var num : integer is 1
          var someBool : bool is true
          
          return someBool or num <= 0
        end
        
        routine test() is
          var num : integer is 1
          var invalidBoolAssign : bool is true and getBool(2)
          
          return
        end
        """.trimIndent()

        assertFailWith<ILangGeneralException>("fatal error: Can't execute \$getBool, expected 2 arguments but actual 1") {
            val ir = generateIr(example)
            TypeChecker().check(ir)
        }
    }

    @Test
    fun testRoutineWrongArgumentType() {
        val example = """        
        routine getBool(arg1: bool, arg2: integer): bool is 
          var num : integer is 1
          var invalidBoolAssign : bool is true
          
          return invalidBoolAssign or num <= 0
        end
        
        routine test() is
          var num : integer is 1
          var invalidIntegerArgument is true
          var invalidBoolAssign : bool is true and getBool(num < 0, invalidIntegerArgument)
          
          return
        end
        """.trimIndent()

        assertFailWith<ILangGeneralException>("fatal error: Invalid argument type. Expected type IntegerType but got BoolType") {
            val ir = generateIr(example)
            TypeChecker().check(ir)
        }
    }

    @Test
    fun testRoutineCallWrongType() {
        val example = """        
        routine getInt(): integer is 
          var num : integer is 1
          return num
        end
        
        routine test() is
          var invalidBoolAssign : bool is true and getInt()
        end
        """.trimIndent()

        assertFailWith<ILangGeneralException>("fatal error: Invalid Boolean expression with type IntegerType") {
            val ir = generateIr(example)
            TypeChecker().check(ir)
        }
    }

    @Test
    fun testArrayWrongAssignType() {
        val example = """        
       routine arrays(i: integer, n: integer): integer is
           var arr : array [10] integer
           arr[n] := true

           return arr[n]
       end
        """.trimIndent()

        assertFailWith<ILangGeneralException>("fatal error: Can't assign expression with type BoolType to left side with type IntegerType") {
            val ir = generateIr(example)
            TypeChecker().check(ir)
        }
    }

    @Test
    fun testArrayWrongIndexType() {
        val example = """        
       routine arrays(i: integer, n: integer): integer is
           var arr : array [10] integer
           arr[n] := 1
           
           var someReal is 2.2

           return arr[someReal]
       end
        """.trimIndent()

        assertFailWith<ILangGeneralException>("fatal error: Wrong array index type. Expected type IntegerType but got RealType") {
            val ir = generateIr(example)
            TypeChecker().check(ir)
        }
    }

    @Test
    fun testFieldAssignWrongType() {
        val example = """        
       type TreeNode is record
           var value : real
           var key : integer
           var children : array [2] TreeNode
       end
       
       routine main() : integer is
            var root : TreeNode
            root.key := 0
            root.value := 0.0
        
            var node1 : TreeNode
            node1.key := root.value
            
            return 0
       end
        """.trimIndent()

        assertFailWith<ILangGeneralException>("fatal error: Can't assign expression with type RealType to left side with type IntegerType") {
            val ir = generateIr(example)
            TypeChecker().check(ir)
        }
    }

    @Test
    fun testFieldAssignWrongLinkType() {
        val example = """        
       type TreeNode is record
           var value : real
           var key : integer
           var children : array [2] TreeNode
       end

       type MayBeNode is record
           var hasValue : bool
           var node : TreeNode
       end
       
       routine main() : integer is
            var root : TreeNode
            root.key := 0
            root.value := 0.0
        
            var someNode : MayBeNode
            
            someNode := root
            
            return 0
       end
        """.trimIndent()

        assertFailWith<ILangGeneralException>("fatal error: Can't assign expression with type RecordType(TreeNode) to left side with type RecordType(MayBeNode)") {
            val ir = generateIr(example)
            TypeChecker().check(ir)
        }
    }

    private inline fun <reified T : Throwable> assertFailWith(message: String, block: () -> Unit): Boolean {
        try {
            block()
        } catch (err: Throwable) {
            if (err !is T) {
                throw err
            }
            if (err.message == message) {
                return true
            }
            throw AssertionError("Exception has wrong message: ${err.message}")
        }

        throw AssertionError("Exception was not thrown")
    }
}