package edu.itmo.ilang.ir

import edu.itmo.ilang.ir.model.BodyEntry
import edu.itmo.ilang.ir.model.Break
import edu.itmo.ilang.ir.model.Continue
import edu.itmo.ilang.ir.model.Return

val BodyEntry.isTerminalStatement: Boolean
    get() = this is Return || this is Break || this is Continue
