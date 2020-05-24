package minesweeper

import java.util.*
import kotlin.random.Random

fun main() {
    val height = 9
    val width = 9
    val scanner = Scanner(System.`in`)

    println("How many mines do you want on the field? ")
    val numMines = scanner.nextInt()

    val minefield = MineField(width, height, numMines)

    while (minefield.hasNext()) {
        println("Set/unset mines marks or claim a cell as free: > ")
        val x = scanner.nextInt() - 1
        val y = scanner.nextInt() - 1
        val action = scanner.next()
        minefield.next(action, x, y)
        minefield.print()
    }

    if (minefield.state == MineField.State.WON) {
        println("Congratulations! You found all mines!")
    } else if (minefield.state == MineField.State.LOST) {
        println("You stepped on a mine and failed!")
    }
}

class MineField(private val width: Int, private val height: Int, private var numMines: Int) {
    private val minefield = Array(height) { CharArray(width) { '.' } }
    private val mines = mutableSetOf<Pair<Int, Int>>()
    private val hints = mutableMapOf<Pair<Int, Int>, Int>().withDefault { 0 }
    private val marked = mutableSetOf<Pair<Int, Int>>()
    private var numCovered = height * width
    var state = State.RUNNING
        private set

    init {
        create()
    }

    fun next(action: String, x: Int, y: Int) {
        val coord = Pair(x, y)
        if (Action.findByAction(action) == Action.FREE) {
            if (hints.containsKey(coord)) {
                minefield[y][x] = hints[coord]?.plus(48)?.toChar()!!
                numCovered--
            } else if (mines.contains(coord)) {
                state = State.LOST
                revealMines()
            } else {
                traverse(x, y)
            }
        }
        if (Action.findByAction(action) == Action.MINE) {
            if (minefield[y][x] != '.' || minefield[y][x] != '*') {
                if (marked.contains(coord)) {
                    marked.remove(coord)
                    minefield[y][x] = '.'
                } else {
                    marked.add(coord)
                    minefield[y][x] = '*'
                }
            }

            if (mines == marked) {
                state = State.WON
            }
        }
        if (numCovered == mines.size)
            state = State.WON
    }

    fun hasNext(): Boolean {
        return state == State.RUNNING
    }

    fun print() {
        println(" │123456789│")
        println("—│—————————│")
        minefield.forEachIndexed { i, row ->
            println((i + 1).toString() + "|" + row.joinToString(separator = "") + "|")
        }
        println("—│—————————│")
    }

    private fun generateMines() {
        while (numMines > 0) {
            val y = Random.nextInt(0, height)
            val x = Random.nextInt(0, width)

            if (!mines.contains(Pair(x, y))) {
                mines.add(Pair(x, y))
                numMines--
            }
        }
    }

    private fun create() {
        generateMines()

        for ((x, y) in mines) {
            if (y - 1 >= 0 && x - 1 >= 0 && !mines.contains(Pair(x - 1, y - 1))) {
                this.setHint(x - 1, y - 1)
            }
            if (y - 1 >= 0 && !mines.contains(Pair(x, y - 1))) {
                this.setHint(x, y - 1)
            }
            if (y - 1 >= 0 && x + 1 < 9 && !mines.contains(Pair(x + 1, y - 1))) {
                this.setHint(x + 1, y - 1)
            }

            if (x - 1 >= 0 && !mines.contains(Pair(x - 1, y))) {
                this.setHint(x - 1, y)
            }
            if (x + 1 < 9 && !mines.contains(Pair(x + 1, y))) {
                this.setHint(x + 1, y)
            }

            if (y + 1 < 9 && x - 1 >= 0 && !mines.contains(Pair(x - 1, y + 1))) {
                this.setHint(x - 1, y + 1)
            }
            if (y + 1 < 9 && !mines.contains(Pair(x, y + 1))) {
                this.setHint(x, y + 1)
            }
            if (y + 1 < 9 && x + 1 < 9 && !mines.contains(Pair(x + 1, y + 1))) {
                this.setHint(x + 1, y + 1)
            }
        }

        print()
    }

    private fun setHint(x: Int, y: Int) {
        hints.computeIfPresent(Pair(x, y)) { _, v -> v + 1 }
        hints.putIfAbsent(Pair(x, y), 1)
    }

    private fun revealMines() {
        for ((x, y) in mines) {
            minefield[y][x] = 'X'
        }
    }

    private fun traverse(x: Int, y: Int) {
        val queue = ArrayDeque<Pair<Int, Int>>()
        val visited = mutableSetOf<Pair<Int, Int>>()
        queue.add(Pair(x, y))
        while (!queue.isEmpty()) {
            val current = queue.pop()
            if (minefield[current.second][current.first] != '.' || minefield[current.second][current.first] != '*') {
                continue
            }
            if (hints.contains(current)) {
                minefield[current.second][current.first] = hints[current]?.plus(48)?.toChar()!!
            } else {
                if (current.first - 1 >= 0) {
                    val neighbor = Pair(current.first - 1, current.second)
                    if (!visited.contains(neighbor) && !queue.contains(neighbor))
                        queue.add(neighbor)
                }
                if (current.second - 1 >= 0) {
                    val neighbor = Pair(current.first, current.second - 1)
                    if (!visited.contains(neighbor) && !queue.contains(neighbor))
                        queue.add(neighbor)
                }
                if (current.first + 1 < width) {
                    val neighbor = Pair(current.first + 1, current.second)
                    if (!visited.contains(neighbor) && !queue.contains(neighbor))
                        queue.add(neighbor)
                }
                if (current.second + 1 < height) {
                    val neighbor = Pair(current.first, current.second + 1)
                    if (!visited.contains(neighbor) && !queue.contains(neighbor))
                        queue.add(neighbor)
                }
                visited.add(current)
                minefield[current.second][current.first] = '/'
            }
            numCovered--
        }
    }

    enum class State {
        RUNNING,
        WON,
        LOST
    }

    enum class Action(val action: String) {
        MINE("mine"),
        FREE("free"),
        INVALID("invalid");

        companion object {
            fun findByAction(action: String): Action {
                for (enum in Action.values()) {
                    if (action == enum.action) return enum
                }
                return INVALID
            }
        }
    }
}