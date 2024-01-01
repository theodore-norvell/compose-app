package model.layouts

import model.state.CalculatorModes

private object BasicLayouts {
    val basicLayout = listOf(
        listOf(
            Btns.SHIFT,
            Btns.EVAL,
            Btns.STO,
            Btns.x,
            Btns.y,
            Btns.z
        ),
        listOf(
            Btns.TODO,
            Btns.TODO,
            Btns.TODO,
            Btns.TODO,
            Btns.TODO,
            Btns.TODO
        ),
        listOf(
            Btns.TODO,
            Btns.TODO,
            Btns.TODO,
            Btns.TODO,
            Btns.TODO,
            Btns.TODO
        ),
        listOf(
            Btns.ENTER_DROP,
            Btns.SWAP_ROLL,
            Btns.TODO,
            Btns.TODO,
            Btns.TODO),
        listOf(
            Btns.UNDO_REDO,
            Btns.D7,
            Btns.D8,
            Btns.D9,
            Btns.DIVIDE_MOD
        ),
        listOf(
            Btns.i_ABS,
            Btns.D4,
            Btns.D5,
            Btns.D6,
            Btns.MULTIPLY_REM
        ),
        listOf(
            Btns.NEGATE,
            Btns.D1,
            Btns.D2,
            Btns.D3,
            Btns.SUBTRACT
        ),
        listOf(
            Btns.CLEAR,
            Btns.D0,
            Btns.POINT,
            Btns.EXP,
            Btns.ADD)
    )
}

private fun replace( layout : List<List<ButtonDescription>>, p :ButtonDescription, q : ButtonDescription)
: List<List<ButtonDescription>> {
    for( i in 0..<layout.size ) {
        for( j in 0 ..< layout[i].size ) {
            if( layout[i][j] == p ) {
                return layout.take(i) +
                        listOf((layout[i].take(j) + listOf(q) + layout[i].drop(j+1))) +
                        layout.drop(i+1)
            }
        }
    }
    // Fail silently.
    return layout
}
fun makeLayout( mode : CalculatorModes, exponent : Boolean )
: List<List<ButtonDescription>> {
    // TODO make this a memo function.
    var layout = BasicLayouts.basicLayout
    if( !exponent && (mode.base > 10 || mode.base == 2)) {
        val newRow = listOf(
                            Btns.DA,
                            Btns.DB,
                            Btns.DC,
                            Btns.DD,
                            Btns.DE,
                            Btns.DF
                        )
        layout = layout.take(4) + listOf(newRow) + layout.drop(4)
        if( mode.base == 2) {
            val replacments : List<Pair<ButtonDescription, ButtonDescription>> = listOf(
                Pair(Btns.D0, Btns.D0000),
                Pair(Btns.D1, Btns.D0001),
                Pair(Btns.D2, Btns.D0010),
                Pair(Btns.D3, Btns.D0011),
                Pair(Btns.D4, Btns.D0100),
                Pair(Btns.D5, Btns.D0101),
                Pair(Btns.D6, Btns.D0110),
                Pair(Btns.D7, Btns.D0111),
                Pair(Btns.D8, Btns.D1000),
                Pair(Btns.D9, Btns.D1001),
                Pair(Btns.DA, Btns.D1010),
                Pair(Btns.DB, Btns.D1011),
                Pair(Btns.DC, Btns.D1100),
                Pair(Btns.DD, Btns.D1101),
                Pair(Btns.DE, Btns.D1110),
                Pair(Btns.DF, Btns.D1111)
            )
            for( (p,q) in replacments) layout = replace(layout,p,q)
        }
    }
    if( !exponent ) {
        val digits  = listOf(Btns.D0,Btns.D1,Btns.D2,Btns.D3,Btns.D4,Btns.D5,Btns.D6,Btns.D7,Btns.D8,Btns.D9,
            Btns.DA,Btns.DB,Btns.DC,Btns.DD,Btns.DE,Btns.DF)
        for( i in mode.base..<16) {
            val disabledOp = digits[i].primaryOperation.copy( enabled = false )
            val disabled = digits[i].copy( primaryOperation = disabledOp )
            layout = replace(layout, digits[i], disabled)
        }
    }
    return layout
}