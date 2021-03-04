package com.jtmarsha.twodicepig

import kotlin.random.Random

object Dice {

    private val dice = IntArray(2) { 1 }

    // uses random to roll the dice
    fun roll():IntArray{
        dice[0] = Random.nextInt(1, 6) // roll dice 1
        dice[1] = Random.nextInt(1, 6) // roll dice 2

        return dice
    } // end roll

    fun getDiceTotal(): Int {

        if((dice[0] == 1) xor (dice[1] == 1)) {

            return 0
        }
        else if (dice[0] == 1 && dice[1] == 1) {

            return -1
        }
        else if(dice[0] == dice[1]) {

            return -2
        }
        else {

            return dice[0] + dice[1]
        }
    }
}