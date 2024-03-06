package edu.itmo.ilang

import edu.itmo.ilang.ir.*

object CompileTimeEvaluator {
    fun evaluateInt(expression: Expression): Int {
        return when (expression) {
            is IntegralLiteral -> expression.value

            is PlusExpression -> evaluateInt(expression.left) + evaluateInt(expression.right)

            is MulExpression -> evaluateInt(expression.left) * evaluateInt(expression.right)

            is DivExpression -> evaluateInt(expression.left) / evaluateInt(expression.right)

            is ModExpression -> evaluateInt(expression.left) % evaluateInt(expression.right)

            else -> TODO()
        }
    }
}