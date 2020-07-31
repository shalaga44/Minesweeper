package minesweeper

data class Pos(val x: Int, val y: Int)


sealed class Result
class MarkingNumberError(val error: String) : Result()
object Success : Result()


class Minesweeper {
    var isRunning: Boolean = true
    private lateinit var minesList: MutableList<Pos>
    private lateinit var matrix: Array<Array<Int>>
    private val mineFlag = -1
    private val markedMineFlag = -2
    private val safeFlag = -3
    private val markedSafeFlag = -4
    private val markedFlags = listOf(markedMineFlag, markedSafeFlag)
    private val unMarkFlags = listOf(mineFlag, safeFlag)
//    private val mineCell = 'X'
    private val safeCellChar = '.'
    private val markedCellChar = '*'
    private var height = 0
    private var width = 0

    fun showGame() {
        println(" |123456789|")
        println("-|---------|")
        matrix.forEachIndexed { index, rows ->
            print("${index + 1}|")
            rows.forEach { cell ->
                print(stringCellOf(cell))
            }
            println("|")
        }
        println("-|---------|")
        printStarterMessage()
    }

    private fun printStarterMessage() {
        println("Set/delete mine marks (x and y coordinates):")
    }

    private fun stringCellOf(cell: Int): String {
        return when (cell) {
            in markedFlags -> markedCellChar
            in unMarkFlags -> safeCellChar
            else -> "$cell"
        }.toString()
    }

    private fun createNew(mines: Int, height: Int = 9, width: Int = 9) {
        this.height = height
        this.width = width
        matrix = Array(height) { Array(width) { safeFlag } }
        minesList = mutableListOf()
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
                addMineHere(pos); break
            }
        }
    }

    private fun addMineHere(pos: Pos) {
        with(pos) {
            matrix[y][x] = mineFlag
            minesList.add(pos)
        }
    }

    private fun isMineHere(pos: Pos): Boolean {
        return pos in minesList
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

    fun takeUserInput(): Result {
        val input = readLine()!!.trim()
        val pos: Pos = parseUserPosInput(input)
        if (isNumberCell(pos))
            return MarkingNumberError(markingNumberError())
        else
            setOrDeleteMinesMark(pos)
        return Success
    }

    private fun markingNumberError(): String {
        return "There is a number here!"
    }

    private fun isNumberCell(pos: Pos): Boolean {
        return matrix[pos.y][pos.x] > 0
    }

    private fun setOrDeleteMinesMark(pos: Pos) {
        if (isMarked(pos))
            deleteMark(pos)
        else
            setMark(pos)
    }

    private fun setMark(pos: Pos) {
        if (isMineHere(pos))
            matrix[pos.y][pos.x] = markedMineFlag
        else
            matrix[pos.y][pos.x] = markedSafeFlag

    }

    private fun deleteMark(pos: Pos) {
        if (isMarkedMine(pos))
            matrix[pos.y][pos.x] = mineFlag
        else
            matrix[pos.y][pos.x] = safeFlag
    }

    private fun isMarkedMine(pos: Pos): Boolean {
        return matrix[pos.y][pos.x] == markedMineFlag
    }

    private fun isMarked(pos: Pos): Boolean {
        return matrix[pos.y][pos.x] in markedFlags
    }


    private fun parseUserPosInput(input: String): Pos {
        val (x, y) = input.split(" ")
        return Pos(x.toInt() - 1, y.toInt() - 1)
    }
}

private fun Int.toPointIn2D(width: Int, height: Int): Pos {
    return Pos(this % height, this / width)
}

fun main() {
    val game = Minesweeper()
    game.askUserToCreate()
    game.showGame()
    while (game.isRunning) {
        when (val result = game.takeUserInput()) {
            is MarkingNumberError -> println(result.error)
            is Success -> game.showGame()
        }
    }
}
