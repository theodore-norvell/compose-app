package model.data.formula

import model.data.Environment

sealed class TopItem {
    abstract fun asNumberBuilder() : NumberBuilder?

    abstract fun negate() : TopItem

    abstract fun render( env : Environment) : String
}
