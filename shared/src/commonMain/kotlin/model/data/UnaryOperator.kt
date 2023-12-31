package model.data

enum class UnaryOperator {
    NEGATE {
        override fun precedence() = 50

        override fun toString() = "-"
    };

    abstract fun precedence() : Int
}
