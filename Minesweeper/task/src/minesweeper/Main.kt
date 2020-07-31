package minesweeper

class Minesweeper {
    private lateinit var minesList: Map<Pair<Int, Int>, Int>
    private lateinit var matrix: Array<Array<Int>>
    private val mineFlag = -1
    private val safeFlag = 0
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

    private fun stringCellOf(cell: Int): String {
        return when (cell) {
            mineFlag -> mineCell
            safeFlag -> safeCell
            else -> "$cell"
        }.toString()
    }

    private fun createNew(mines: Int, height: Int = 9, width: Int = 9) {
        this.height = height
        this.width = width
        matrix = Array(height) { Array(width) { safeFlag } }
        minesList = HashMap(width * height)
        addNewMinesToMatrix(mines)
        lookAroundAllMatrix()
    }

    private fun lookAroundAllMatrix() {
        for (y in 0 until height)
            for (x in 0 until width) {
                if (!isMineHere(x, y))
                    lookAroundCell(x, y)
            }
    }

    private fun lookAroundCell(x: Int, y: Int) {
        val sumOfAround = getListOfAround(x, y).count()
        matrix[y][x] = sumOfAround

    }

    private fun getListOfAround(x: Int, y: Int): List<Pair<Int, Int>> {
        return listOf(Pair(x + 1, y),
                Pair(x - 1, y),
                Pair(x, y + 1),
                Pair(x, y - 1),
                Pair(x + 1, y - 1),
                Pair(x + 1, y + 1),
                Pair(x - 1, y - 1),
                Pair(x - 1, y + 1)).filter { isValidPosition(it) }
                .filter { isMineHere(it.first, it.second) }
    }

    private fun isValidPosition(pos: Pair<Int, Int>): Boolean {
        if (isVerticallyAligned(pos) && isHorizontallyAligned(pos)) {
            return true
        }
        return false
    }

    private fun isHorizontallyAligned(pos: Pair<Int, Int>): Boolean {
        return (pos.second >= 0) && (pos.second < height)
    }

    private fun isVerticallyAligned(pos: Pair<Int, Int>): Boolean {
        return (pos.first >= 0) && (pos.first < width)
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
