package model.data.value

data class ComplexNumberValue  (
    val realPart : ANumberValue,
    val imaginaryPart : ANumberValue
)
: Value()
{
    override fun render(): String {
        // TODO, accommodate display preferences
        val rootMinus1 = "i"
        if( imaginaryPart.isZero() ) {
            return realPart.render()
        } else if( realPart.isZero()) {
            return imaginaryPart.render() + " " + rootMinus1
        } else {
            return "($realPart.render(groupLengthBefore, groupLengthAfter, separatorBefore, separatorAfter, radixPoint)) + " +
                    "$imaginaryPart.render(groupLengthBefore, groupLengthAfter, separatorBefore, separatorAfter, radixPoint)) $rootMinus1)"
        }
    }
    override fun negate(): ComplexNumberValue = copy( realPart = realPart.negate(), imaginaryPart = imaginaryPart.negate())
}