package com.jtmarsha.twodicepig

class Player() {

    private var score = 0 // tracks player's score
    private var name = "" // allows player to set their name

    // updates player's score with their current turn score
    // this function should never be called before turn is over
    fun updateScore(turnScore :Int) {

        score += turnScore
    }

    fun resetScore() {

        score = 0
    }

    fun getScore() : Int {

        return score;
    }
}