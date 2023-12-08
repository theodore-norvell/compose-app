package model.data.formula

import model.data.Environment

interface Formula {
    fun render( env : Environment) : String

    fun expand( env : Environment) : Formula

    fun evaluate( env : Environment) : Formula

    fun freeVars( ) : Set<String>

    fun asError() : ErrorFormula? = null

    fun asFloatNumber() : FloatNumber? = null

    fun precedence() : Int = 0
}