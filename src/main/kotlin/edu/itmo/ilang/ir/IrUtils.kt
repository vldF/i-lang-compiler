package edu.itmo.ilang.ir

val BodyEntry.isTerminalStatement: Boolean
    get() = this is Return || this is Break || this is Continue
