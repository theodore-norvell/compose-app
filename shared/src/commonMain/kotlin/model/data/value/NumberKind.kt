package model.data.value

sealed class NumberKind

data object Flexible : NumberKind()

data object IEEE : NumberKind()