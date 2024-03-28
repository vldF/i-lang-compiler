package edu.itmo.ilang.ir.model

sealed interface IrEntry

data class Program(
    val declarations: List<Declaration>
) : IrEntry