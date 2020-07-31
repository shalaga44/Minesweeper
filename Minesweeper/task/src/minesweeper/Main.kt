package minesweeper

class Minesweeper {
    lateinit var matrix: Array<Array<Int>>
    private val mineFlag = 0
    private val safeFlag = 1
    private val mineCell = 'X'
    private val safeCell = '.'
    private val errorCell= '?'
    private var height = 0
    private var width = 0

    fun showGame() {
        matrix.forEach { rows ->
            rows.forEach { cell ->
                print(stringCellOf(cell))
            }
            println()
        }
    }

    private fun stringCellOf(cell: Int): Char {
        return when(cell){
            mineFlag  -> mineCell
            safeFlag -> safeCell
            else ->  errorCell
        }
    }

    fun createNew(height: Int, width: Int, mines: Int) {
        this.height = height
        this.width = width
        matrix = Array(height) { Array(width) { safeFlag } }
        addNewMinesToMatrix(mines)
    }

    private fun addNewMinesToMatrix(mines: Int) {
        repeat(mines) {
            val randomX = (0 until height).random()
            val randomY = (0 until width).random()
            matrix[randomY][randomX] = mineFlag

        }
    }

}


fun main() {
    val game = Minesweeper()
    game.createNew(9, 9, 10)
    game.showGame()
}
