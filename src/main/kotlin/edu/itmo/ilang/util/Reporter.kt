package edu.itmo.ilang.util

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun report(message: String): Nothing = throw ILangGeneralException(message)

@OptIn(ExperimentalContracts::class)
fun iCheck(condition: Boolean, lazyMessage: () -> String = { "Check failed" }) {
    contract {
        returns() implies condition
    }

    throw ILangGeneralException(lazyMessage())
}

class ILangGeneralException(message: String) : Exception(message)
