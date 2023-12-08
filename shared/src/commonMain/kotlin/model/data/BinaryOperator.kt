package model.data

enum class BinaryOperator {
    ADD {
        override fun precedence() = 10

        override fun toString() = "+"
    },
    DIVIDE {
        override fun precedence() = 20

        override fun toString() = "/"
    },
    MULTIPLY {
        override fun precedence() = 20

        override fun toString() = "×"
    },
    SUBTRACT {
        override fun precedence() = 10

        override fun toString() = "-"
    } ;

    abstract fun precedence() : Int
}
