package edu.itmo.ilang.ir

sealed interface IrEntry

data class Program(
    val declarations: List<Declaration>
) : IrEntry