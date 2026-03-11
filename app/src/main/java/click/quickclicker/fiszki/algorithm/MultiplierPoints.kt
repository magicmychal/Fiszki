package click.quickclicker.fiszki.algorithm

class MultiplierPoints(private val priority: IntArray) {

    private val priorytyRange = IntArray(5)

    fun multipler(): IntArray {
        priorytyRange[0] = priority[0] * 25
        priorytyRange[1] = (priority[1] * 20) + priorytyRange[0]
        priorytyRange[2] = (priority[2] * 15) + priorytyRange[1]
        priorytyRange[3] = (priority[3] * 10) + priorytyRange[2]
        priorytyRange[4] = (priority[4] * 5) + priorytyRange[3]
        return priorytyRange
    }
}
