package edu.itmo.ilang.semantic.checkers

import edu.itmo.ilang.ir.BodyEntry
import edu.itmo.ilang.ir.ForLoop
import edu.itmo.ilang.ir.IntegerType
import edu.itmo.ilang.util.report

class ForRangeIsInteger : BodyEntriesChecker {
    override fun processBodyEntry(bodyEntry: BodyEntry) {
        if (bodyEntry is ForLoop &&
            (bodyEntry.rangeStart.type != IntegerType || bodyEntry.rangeEnd.type != IntegerType)) {
            report("ranges in for must be integer")
        }
    }
}
