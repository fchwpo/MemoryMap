package com.fchwpo.mymemory.models

import com.fchwpo.mymemory.utils.DEFAULT_ICONS

class MemoryGame(private val boardSize: BoardSize) {
    val cards: List<MemoryCard>
    private var numOfPairsFound: Int = 0
    private var lastFlippedPosition: Int? = null
    private var numberOfCardsFlipped: Int = 0

    init {
        val randomImages = DEFAULT_ICONS.shuffled().take(boardSize.getNumOfPairs())
        val chosenImages = (randomImages + randomImages).shuffled()
        cards = chosenImages.map { MemoryCard(it) }
    }

    private fun restoreCards() {
        cards.forEach {
            if (!it.isMatched) {
                it.isFlipped = false
            }
        }
    }

    fun flip(position: Int): Boolean {
        val memoryGame = cards[position]
        var foundMatch = false
        numberOfCardsFlipped++
        if (lastFlippedPosition == null) {
            restoreCards()
            lastFlippedPosition = position
        } else {
            foundMatch = checkIfMatch(position, lastFlippedPosition!!)
            lastFlippedPosition = null
        }
        // Three cases are possible
        // 0 cards flipped
        // 1 card flipped
        // 2 cards flipped
        memoryGame.isFlipped = !memoryGame.isFlipped
        return foundMatch
    }

    private fun checkIfMatch(position1: Int, position2: Int): Boolean {
        return if (cards[position1].id == cards[position2].id) {
            cards[position1].isMatched = true
            cards[position2].isMatched = true
            numOfPairsFound++
            true
        } else {
            false
        }
    }

    fun getNumberOfPairsFound() = numOfPairsFound

    fun hasUserWon() = numOfPairsFound == boardSize.getNumOfPairs()

    fun clickedLastFlippedPosition(position: Int): Boolean {
        return lastFlippedPosition == position
    }

    fun getNumberOfMoves(): Int {
        return numberOfCardsFlipped / 2
    }

}