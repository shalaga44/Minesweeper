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
                val xyPair = generateNewRandomPoint2D(width, height)
                if (isMineHere(xyPair.first, xyPair.second)) continue
                setAsMineHere(xyPair.first, xyPair.second); break
            }
        }
    }

    private fun setAsMineHere(x: Int, y: Int) {
        matrix[y][x] = mineFlag
    }

    private fun isMineHere(x: Int, y: Int): Boolean {
        return matrix[y][x] == mineFlag
    }

    private fun generateNewRandomPoint2D(width: Int, height: Int): Pair<Int, Int> {
        return (0 until width * height)
                .random().toPointIn2D(width, height)
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
