package edu.itmo.ilang.util

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun report(message: String): Nothing = throw Exception(message)

@OptIn(ExperimentalContracts::class)
fun iCheck(condition: Boolean, lazyMessage: () -> String = { "Check failed" }) {
    contract {
        returns() implies condition
    }

    check(condition, lazyMessage)
}
