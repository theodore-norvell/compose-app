package model.data.formula

import model.data.Environment

interface Formula {
    fun render( env : Environment) : String

    fun expand( env : Environment) : Formula

    fun evaluate( env : Environment) : Formula

    fun freeVars( ) : Set<String>

    fun asError() : ErrorFormula? = null

    fun asFloatNumber() : NumberFormula? = null

    fun precedence() : Int = 0
    fun isClosed(): Boolean = false
    fun canAppendDigit(base : Int, digit: Byte) = false
    fun appendDigit(base: Int, digit: Byte) = this
    fun close(): Formula = this
    fun appendPoint(): Formula = this
}