package minesweeper

import java.util.Scanner
import kotlin.random.Random

fun setHint(board: Array<CharArray>, col: Int, row: Int) {
    if (board[col][row] == '.') {
        board[col][row] = '1'
    } else {
        board[col][row] = (board[col][row].toInt() + 1).toChar()
    }
}

fun main() {
    println("How many mines do you want on the field? ")
    val scanner = Scanner(System.`in`)
    var numMines = scanner.nextInt()
    val mines = mutableSetOf<Pair<Int, Int>>()

    val minefield = Array<CharArray>(9) { CharArray(9) { '.' } }
    while (numMines > 0) {
        val row = Random.nextInt(0, 9)
        val col = Random.nextInt(0, 9)

        if (!mines.contains(Pair(col,row))) {
            mines.add(Pair(col, row))
            numMines--
        }
    }

    for ((col, row) in mines) {
        if (row - 1 >= 0 && col - 1 >= 0 && !mines.contains(Pair(col - 1,row - 1))) {
            setHint(minefield, col - 1, row - 1)
        }
        if (row - 1 >= 0 && !mines.contains(Pair(col,row - 1))) {
            setHint(minefield, col, row - 1)
        }
        if (row - 1 >= 0 && col + 1 < 9 && !mines.contains(Pair(col + 1,row - 1))) {
            setHint(minefield, col + 1, row - 1)
        }

        if (col - 1 >= 0 && !mines.contains(Pair(col - 1,row))) {
            setHint(minefield, col - 1, row)
        }
        if (col + 1 < 9 && !mines.contains(Pair(col + 1,row))) {
            setHint(minefield, col + 1, row)
        }

        if (row + 1 < 9 && col - 1 >= 0 && !mines.contains(Pair(col - 1,row + 1))) {
            setHint(minefield, col - 1, row + 1)
        }
        if (row + 1 < 9 && !mines.contains(Pair(col,row + 1))) {
            setHint(minefield, col, row + 1)
        }
        if (row + 1 < 9 && col + 1 < 9 && !mines.contains(Pair(col + 1,row + 1))) {
            setHint(minefield, col + 1, row + 1)
        }
    }

    for (row in minefield) {
        println(row.joinToString(separator = ""))
    }
}
