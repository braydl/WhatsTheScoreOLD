package com.example.whatsthescore.presentation

import java.util.Stack
import kotlin.math.abs
import kotlin.math.max

enum class Team(val printableName: String) { NONE("None"), YOU("You"), OPPONENT("They") }
enum class Side(val printableName: String) { EVEN("even side"), ODD("odd side") }

data class Stats(
    var serverTeam: Team = Team.NONE,
    var serverSide: Side = Side.EVEN,
    var serverScore: Int = 0,
    var receiverScore: Int = 0,
    var serverNumber: Int = 2
)

abstract class Game {

    protected var stats: Stats = Stats()
    var gameLength: Int = 11
    var numberPlayers: Int = 2
    protected val undoStack = Stack<Stats>()

    init {
        println("playing to $gameLength")
        pushUndo()
    }

    // main API for app



    // tell the game who one the rally
    fun rallyWonBy(theTeam: Team) {
        if (theTeam == stats.serverTeam) {
            point()
        } else {
            sideout()
        }
        pushUndo()
    }

    fun serverTeam(): Team {
        return stats.serverTeam
    }

    fun serverSide(): Side {
        return stats.serverSide
    }

    // ask if the game is over
    fun isGameOver(): Boolean {
        return (max(stats.serverScore, stats.receiverScore) >= gameLength) &&
                (abs(stats.serverScore - stats.receiverScore) >= 2)
    }

    // reset the score to zero
    fun resetGame() {
        stats = Stats()
        undoStack.clear()
        pushUndo()
    }

    fun whoServesFirst(theTeam : Team = Team.YOU)
    {
        resetGame()
        stats.serverTeam = theTeam
    }

    fun isUndoAvailable() : Boolean
    {
        return !undoStack.empty()
    }

    // undo
    fun undo() {
        if (isUndoAvailable()) {
            stats = undoStack.pop()
        }
    }

    // test it
    abstract fun unitTest()

    ///////////////////////////////////////////////////////////////////////////////////////////

    // serving team scored a point
    protected abstract fun point()

    // serving team faulted
    protected abstract fun sideout()

    private fun pushUndo() {
        undoStack.push(stats.copy())
    }
    // move the server to the other side of the court
    protected  fun switchSides() {
        stats.serverSide = if (stats.serverSide == Side.EVEN) Side.ODD else Side.EVEN
    }

    // serving team faulted - swap score and give the serve to the other team
    protected fun swapServerTeam() {
        // swap the score
        val temp = stats.serverScore
        stats.serverScore = stats.receiverScore
        stats.receiverScore = temp

        // swap the server
        stats.serverTeam = if (stats.serverTeam == Team.YOU) Team.OPPONENT else Team.YOU
        stats.serverNumber = 1
        stats.serverSide = Side.EVEN
    }

    open fun scoreToString(): String {
        var aResult = ""
        val aGameOver = isGameOver()

        if (stats.serverTeam == Team.NONE) {
            return "Who Serves First?"
        }

        if (aGameOver) {
            aResult = "${stats.serverTeam.printableName} Won "
        }

        aResult += "${stats.serverScore}-${stats.receiverScore}"

        if (!aGameOver && numberPlayers > 1) {
            aResult += "-${stats.serverNumber}"
        }
        return aResult
    }

    fun printStatus(comment : String = "") {
        val aScore = scoreToString()
        println("${stats.serverTeam.printableName} are servicing from the ${stats.serverSide.printableName}: ${stats.serverTeam} ${stats.serverSide} $aScore // $comment")
    }


}

///////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////
class GameDoublesTraditional : Game () {
    init {
        println("GameDoublesTraditional to $gameLength")
    }

    // serving team scored a point
    override  fun point() {
        stats.serverScore++
        switchSides()
    }

    // serving team faulted
    override  fun sideout() {
        if (stats.serverNumber == 1) {
            stats.serverNumber = 2
            switchSides()
        } else {
            swapServerTeam()
        }
    }

    override fun unitTest() {
        printStatus("YOU EVEN 0-0-2")
        rallyWonBy(Team.YOU); printStatus("YOU ODD 1-0-2")
        rallyWonBy(Team.YOU); printStatus("YOU EVEN 2-0-2")
        rallyWonBy(Team.OPPONENT); printStatus("THEM EVEN 0-2-1")
        rallyWonBy(Team.YOU); printStatus("THEM ODD 0-2-2")
        rallyWonBy(Team.OPPONENT); printStatus("THEM EVEN 1-2-2")
        rallyWonBy(Team.OPPONENT); printStatus("THEM ODD 2-2-2")
        rallyWonBy(Team.YOU); printStatus("YOU EVEN 2-2-1")
        println(undoStack.size)
        println(stats)
        while (undoStack.size > 0) {
            undo()
            println(stats)
        }
    }

}

///////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////
class GameSinglesTraditional : Game () {
    init {
        numberPlayers = 1
        println("GameSinglesTraditional to $gameLength")
    }

    // server scored a point
    override  fun point() {
        stats.serverScore++
        switchSides()
    }

    // server faulted
    override  fun sideout() {
        swapServerTeam()
    }

    override fun unitTest() {
        printStatus("YOU EVEN 0-0")
        rallyWonBy(Team.YOU); printStatus("YOU ODD 1-0")
        rallyWonBy(Team.YOU); printStatus("YOU EVEN 2-0")
        rallyWonBy(Team.OPPONENT); printStatus("THEM EVEN 0-2")
        rallyWonBy(Team.YOU); printStatus("YOU EVEN 2-0")
        rallyWonBy(Team.OPPONENT); printStatus("THEM EVEN 0-2")
        rallyWonBy(Team.OPPONENT); printStatus("THEM ODD 1-2")
        rallyWonBy(Team.YOU); printStatus("YOU EVEN 2-1")
        rallyWonBy(Team.YOU); printStatus("YOU EVEN 3-1")
        rallyWonBy(Team.YOU); printStatus("YOU EVEN 4-1")
    }

}