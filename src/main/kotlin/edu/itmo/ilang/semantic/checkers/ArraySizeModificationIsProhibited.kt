package edu.itmo.ilang.semantic.checkers

import edu.itmo.ilang.ir.model.Program

/**
 * todo: deny array size setting like this:
 * In current implementation the next code is valid:
 *
 * routine test() : integer is
 *   var arr1 : array[1] integer
 *   arr1.size := 10
 *
 *   return arr1.size // 10
 * end
 */
class ArraySizeModificationIsProhibited : Checker {
    override fun check(program: Program) {
        TODO("Not yet implemented")
    }
}
