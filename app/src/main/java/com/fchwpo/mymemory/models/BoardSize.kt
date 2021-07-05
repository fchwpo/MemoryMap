package com.fchwpo.mymemory.models

enum class BoardSize(val numOfCards: Int) {
    EASY(8),
    MEDIUM(18),
    HARD(24);

    fun getWidth(): Int {
        return when (this) {
            EASY -> 2
            MEDIUM -> 3
            HARD -> 4
        }
    }

    fun getHeight(): Int {
        return numOfCards / getWidth()
    }

    fun getNumOfPairs() = numOfCards / 2


}