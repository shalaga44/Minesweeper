package minesweeper

import java.util.*

data class Pos(val x: Int, val y: Int)


sealed class Result
class MarkingNumberError(val error: String = "There is a number here!") : Result()
object Success : Result()
class UserWins(val message: String = "Congratulations! You found all the mines!") : Result()
class UserFailed(val message: String = "You stepped on a mine and failed!") : Result()
class InputError(val error: String) : Result()


sealed class UserAction
class MineAction(val pos: Pos) : UserAction()
class FreeAction(val pos: Pos) : UserAction()
class ErrorAction(val input: String) : UserAction()

class Minesweeper {
    private val visibleNumberTag = 'v'
    private val hidedenNumberTag = 'h'
    private var totalMines: Int = 0
    private var markedCounter: Int = 0
    var isRunning: Boolean = true
    private lateinit var minesList: MutableList<Pos>
    private lateinit var matrix: Array<Array<String>>
    private val mineFlag = "mine"
    private val markedMineFlag = "marked mine"
    private val unOpenedFlag = "un opened"
    private val markedSafeFlag = "marked safe"
    private val markedFlags = listOf(markedMineFlag, markedSafeFlag)
    private val unMarkFlags = listOf(mineFlag, unOpenedFlag)
    private val visibleMineFlag = "visible mine"
    private val safeCellFlag = "safe cell"

    private val markedNumberTag = 'm'
    private val unMarkedNumberTag = 'n'

    private val visibleMineCell = 'X'
    private val unOpenedCellChar = '.'
    private val markedCellChar = '*'
    private val safeCellChar = '/'

    private val inputMineFlag = "mine"
    private val inputFreeFlag = "free"

    private var height = 0
    private var width = 0

    private val queue: Queue<Pos> = LinkedList()
    private var visitedPositions: HashMap<String, Boolean> = hashMapOf()

    private fun showGrid() {
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

    }

    fun showNormalGameGrid() {
        showGrid()
        printStarterMessage()
    }

    private fun printStarterMessage() {
        println("Set/unset mine marks or claim a cell as free:")
    }

    private fun stringCellOf(cell: String): String {
        return when (cell) {
            safeCellFlag -> safeCellChar
            in markedFlags -> markedCellChar
            in unMarkFlags -> unOpenedCellChar
            visibleMineFlag -> visibleMineCell
            else -> getCellNumberOrHide(cell)
        }.toString()
    }

    private fun getCellNumberOrHide(cell: String): Char {
        return if (isVisibleNumber(cell))
            getCellNumber(cell)
        else if (isNumberMarked(cell))
            markedCellChar
        else
            unOpenedCellChar
    }

    private fun getCellNumber(cell: String): Char {
        return cell.last()
    }

    private fun isVisibleNumber(cell: String): Boolean {
        if (cell[0] == visibleNumberTag)
            return true
        return false
    }

    private fun createNew(mines: Int, height: Int = 9, width: Int = 9) {
        this.totalMines = mines
        this.height = height
        this.width = width
        matrix = Array(height) { Array(width) { unOpenedFlag } }
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
        val sumOfAround = getListOfAround(x, y).filter { isMineHere(it) }.count()
        if (sumOfAround != 0)
            matrix[y][x] = hidedenNumberTag +
                    unMarkedNumberTag.toString() +
                    sumOfAround.toString()


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
        markAllMatrixAsNotVisited()
//        enableAllMines()//for debugging

    }

    private fun markAllMatrixAsNotVisited() {
        (0 until height).forEach { y ->
            (0 until width).forEach { x ->
                markAsNotVisited(Pos(x, y))
            }

        }
    }

    fun takeUserInput(): Result {
        val input = readLine()!!.trim()
        val action = getUserInputAction(input)

        return when (action) {
            is MineAction -> doMineAction(action)
            is FreeAction -> doFreeAction(action)
            is ErrorAction -> doErrorAction(action)
        }

    }

    private fun doErrorAction(action: ErrorAction): Result {
        return InputError("Unknown: ${action.input.split(" ").last()}")
    }

    private fun doFreeAction(action: FreeAction): Result {

        return freeSafeCellOrFailed(action.pos)

    }

    private fun freeSafeCellOrFailed(pos: Pos): Result {

        if (isMineHere(pos))
            return stopTheGameUserFailed()
        else
            freeFromHere(pos)

        return Success
    }

    private fun freeFromHere(start: Pos) {
        visit(start)
        while (queue.isNotEmpty()) {
            val pos = queue.remove()
            getNextPositions(pos).forEach {
                if (isNotVisited(it))
                    visit(it)

            }
        }

    }

    private fun visit(pos: Pos) {
        freePos(pos)
        markAsVisited(pos)
        queue.add(pos)
    }

    private fun getNextPositions(pos: Pos) = sequence {

        getPositions(pos)
                .filter { isValidPosition(it) }
                .forEach {
                    if (!isMineHere(it))
                        yield(it)
                }
    }

    private fun getPositions(pos: Pos): List<Pos> {
        with(pos) {
            return listOf(
                    Pos(x + 1, y),
                    Pos(x - 1, y),
                    Pos(x, y + 1),
                    Pos(x, y - 1))
        }
    }

    private fun markAsVisited(pos: Pos) {
        this.visitedPositions[pos.toString()] = true
    }

    private fun markAsNotVisited(pos: Pos) {
        this.visitedPositions[pos.toString()] = false
    }

    private fun isNotVisited(pos: Pos): Boolean {
        return !this.visitedPositions[pos.toString()]!!
    }

    private fun freePos(pos: Pos) {
        if (isNumberCell(pos))
            setNumberVisible(pos)
        else
            setCellSafe(pos)
    }

    private fun setCellSafe(pos: Pos) {
        matrix[pos.y][pos.x] = safeCellFlag
    }

    private fun setNumberVisible(pos: Pos) {
        val cellNum = matrix.atPos(pos)
        if (isVisibleNumber(cellNum))
            throw Exception("$cellNum is already visible")
        val num = extractCellNumber(cellNum)
        val newCell = visibleNumberTag + num.toString()
        matrix[pos.y][pos.x] = newCell

    }

    private fun extractCellNumber(cellNum: String): Int {
        val cellList = cellNum.filter { it.isDigit() }
        return cellList.toInt()
    }

    private fun stopTheGameUserFailed(): Result {
        enableAllMines()
        showGrid()
        isRunning = false
        return UserFailed()
    }

    private fun enableAllMines() {
        minesList.forEach {
            matrix[it.y][it.x] = visibleMineFlag
        }
    }

    private fun doMineAction(action: MineAction): Result {
        return if (isNumberCell(action.pos))
            setOrDeleteNumberMark(action.pos)
        else
            setOrDeleteMinesMark(action.pos)
    }

    private fun setOrDeleteNumberMark(pos: Pos): Result {
        if (isNumberMarked(pos))
            deleteNumberMark(pos)
        else
            setNumberMark(pos)
        return Success
    }

    private fun setNumberMark(pos: Pos) {
        val cellNum = matrix.atPos(pos).split("").drop(1).dropLast(1).toMutableList()
        cellNum[1] = markedNumberTag.toString()
        matrix[pos.y][pos.x] = cellNum.joinToString("")
    }

    private fun deleteNumberMark(pos: Pos) {
        val cellNum = matrix.atPos(pos).split("").drop(1).dropLast(1).toMutableList()
        cellNum[1] = unMarkedNumberTag.toString()
        matrix[pos.y][pos.x] = cellNum.joinToString("")
    }

    private fun isNumberMarked(pos: Pos): Boolean {
        val tag = getNumberMarkingTag(matrix.atPos(pos))
        if (tag == markedNumberTag.toString())
            return true
        return false


    } private fun isNumberMarked(cellNum: String): Boolean {
        val tag = getNumberMarkingTag(cellNum)
        if (tag == markedNumberTag.toString())
            return true
        return false


    }

    private fun getNumberMarkingTag(cellNum: String): String {
        val cellList = cellNum.split("").drop(1).dropLast(1)
        return cellList[1]

    }

    private fun getUserInputAction(input: String): UserAction {
        val argv = input.split(" ")
        val pos = Pos(argv[0].toInt() - 1, argv[1].toInt() - 1)

        return when (argv.last()) {
            inputFreeFlag -> FreeAction(pos)
            inputMineFlag -> MineAction(pos)
            else -> ErrorAction(input)
        }
    }


    private fun isNumberCell(pos: Pos): Boolean {
        val cellNum = matrix.atPos(pos)
        if (cellNum[0] == hidedenNumberTag || cellNum[0] == visibleNumberTag)
            return true
        return false
    }

    private fun setOrDeleteMinesMark(pos: Pos): Result {
        if (isMarked(pos))
            deleteMark(pos)
        else
            setMark(pos)
        if (isUserWon())
            return UserWins()
        return Success
    }

    private fun isUserWon(): Boolean {
        if (isAllMinesMarked())
            if (isOnlyMinesMarked())
                return stopTheGameWin()
        return false
    }

    private fun stopTheGameWin(): Boolean {
        isRunning = false
        return true
    }

    private fun isOnlyMinesMarked(): Boolean {
        return markedCounter == totalMines
    }

    private fun isAllMinesMarked(): Boolean {
        minesList.forEach { pos ->
            if (matrix.atPos(pos) != markedMineFlag)
                return false
        }
        return true
    }

    private fun setMark(pos: Pos) {
        if (isMineHere(pos))
            matrix[pos.y][pos.x] = markedMineFlag
        else
            matrix[pos.y][pos.x] = markedSafeFlag
        markedCounter++

    }

    private fun deleteMark(pos: Pos) {
        if (isMarkedMine(pos))
            matrix[pos.y][pos.x] = mineFlag
        else
            matrix[pos.y][pos.x] = unOpenedFlag
        markedCounter--
    }

    private fun isMarkedMine(pos: Pos): Boolean {
        return matrix.atPos(pos) == markedMineFlag
    }

    private fun isMarked(pos: Pos): Boolean {
        return matrix[pos.y][pos.x] in markedFlags
    }

}

private fun Array<Array<String>>.atPos(pos: Pos): String {
    return this[pos.y][pos.x]
}

private fun Int.toPointIn2D(width: Int, height: Int): Pos {
    return Pos(this % height, this / width)
}

fun main() {
    val game = Minesweeper()
    game.askUserToCreate()
    game.showNormalGameGrid()
    while (game.isRunning) {
        val result = when (val result = game.takeUserInput()) {
            is Success -> game.showNormalGameGrid()
            is MarkingNumberError -> println(result.error)
            is UserWins -> println(result.message)
            is InputError -> println(result.error)
            is UserFailed -> println(result.message)
        }
    }
}
