package model.data

enum class BinaryOperator {
    ADD {
        override fun precedence() = 100

        override fun toString() = "+"
    },
    DIVIDE {
        override fun precedence() = 80

        override fun toString() = "/"
    },
    MULTIPLY {
        override fun precedence() = 80

        override fun toString() = "×"
    },

    POW {
        override fun precedence() = 60

        override fun toString() = "^"
    },

    SUBTRACT {
        override fun precedence() = 100

        override fun toString() = "-"
    } ;

    abstract fun precedence() : Int
}
