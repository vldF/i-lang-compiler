package edu.itmo.ilang.semantic.checkers

import edu.itmo.ilang.ir.model.*
import edu.itmo.ilang.util.report

/**
 * Deny array size setting like this:
 *
 * routine test() : integer is
 *   var arr1 : array[1] integer
 *   arr1.size := 10
 *
 *   return arr1.size // 10
 * end
 */
class ArraySizeModificationIsProhibited : BodyEntriesChecker {
    override fun processBodyEntry(bodyEntry: BodyEntry) {
        if (bodyEntry is Assignment &&
            bodyEntry.lhs is FieldAccessExpression &&
            bodyEntry.lhs.accessedExpression.type is ArrayType &&
            bodyEntry.lhs.field == "size"
        ) {
            report("cannot assign to array size field")
        }
    }
}
