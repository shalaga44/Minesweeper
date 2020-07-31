package minesweeper

class Minesweeper {
    private lateinit var minesList: Map<Pair<Int, Int>, Int>
    private lateinit var matrix: Array<Array<Int>>
    private val mineFlag = 0
    private val safeFlag = 1
    private val mineCell = 'X'
    private val safeCell = '.'
    private val errorCell = '?'
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
        return when (cell) {
            mineFlag -> mineCell
            safeFlag -> safeCell
            else -> errorCell
        }
    }

    private fun createNew(mines: Int, height: Int = 9, width: Int = 9) {
        this.height = height
        this.width = width
        matrix = Array(height) { Array(width) { safeFlag } }
        minesList = HashMap(width * height)
        addNewMinesToMatrix(mines)
    }

    private fun addNewMinesToMatrix(mines: Int) {
        repeat(mines) {
            while (true) {
                val pos = (0 until width * height)
                        .random().toPointIn2D(width, height)
                if (matrix[pos.second][pos.first] != safeFlag)
                    continue
                matrix[pos.second][pos.first] = mineFlag
                break
            }
        }
    }


    fun askUserToCreate() {
        println("How many mines do you want on the field?")
        val mines = readLine()!!.toInt()
        createNew(mines)
    }
}

private fun Int.toPointIn2D(width: Int, height: Int): Pair<Int, Int> {
    return Pair(this % width, this / height)
}

fun main() {
    val game = Minesweeper()
    game.askUserToCreate()
    game.showGame()
}
