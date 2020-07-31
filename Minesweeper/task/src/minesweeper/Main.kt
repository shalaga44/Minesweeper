package minesweeper

data class Pos(val x: Int, val y: Int)

class Minesweeper {
    private lateinit var minesList: Map<Pos, Int>
    private lateinit var matrix: Array<Array<Int>>
    private val mineFlag = 0
    private val safeFlag = -1
    private val mineCell = 'X'
    private val safeCell = '.'
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
                if (!isMineHere(Pos(x, y)))
                    lookAroundCell(x, y)
            }
    }

    private fun lookAroundCell(x: Int, y: Int) {
        val sumOfAround = getListOfAround(x, y).count()
        if (sumOfAround != 0)
            matrix[y][x] = sumOfAround

    }

    private fun getListOfAround(x: Int, y: Int): List<Pos> {
        return listOf(Pos(x + 1, y),
                Pos(x - 1, y),
                Pos(x, y + 1),
                Pos(x, y - 1),
                Pos(x + 1, y - 1),
                Pos(x + 1, y + 1),
                Pos(x - 1, y - 1),
                Pos(x - 1, y + 1))
                .filter { isValidPosition(it) }
                .filter { isMineHere(it) }
    }

    private fun isValidPosition(pos: Pos): Boolean {
        if (isVerticallyAligned(pos) && isHorizontallyAligned(pos)) {
            return true
        }
        return false
    }

    private fun isHorizontallyAligned(pos: Pos): Boolean {
        return (pos.y >= 0) && (pos.y < height)
    }

    private fun isVerticallyAligned(pos: Pos): Boolean {
        return (pos.x >= 0) && (pos.x < width)
    }

    private fun addNewMinesToMatrix(mines: Int) {
        repeat(mines) {
            while (true) {
                val pos = generateNewRandomPoint2D(width, height)
                if (isMineHere(pos)) continue
                setAsMineHere(pos); break
            }
        }
    }

    private fun setAsMineHere(pos: Pos) {
        with(pos) { matrix[y][x] = mineFlag }
    }

    private fun isMineHere(pos: Pos): Boolean {
        with(pos) { return matrix[y][x] == mineFlag }
    }

    private fun generateNewRandomPoint2D(width: Int, height: Int): Pos {
        val num = (0 until (width * height)).random()
        return num.toPointIn2D(width, height)

    }


    fun askUserToCreate() {
        println("How many mines do you want on the field?")
        val mines = readLine()!!.toInt()
        createNew(mines)

    }
}

private fun Int.toPointIn2D(width: Int, height: Int): Pos {
    return Pos(this % height, this / width)
}

fun main() {
    val game = Minesweeper()
    game.askUserToCreate()
    game.showGame()
}
