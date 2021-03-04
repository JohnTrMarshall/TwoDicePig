package com.jtmarsha.twodicepig

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jtmarsha.twodicepig.databinding.ActivityGameBinding


class GameActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGameBinding
    private lateinit var fullscreenContent: TextView
    private lateinit var fullscreenContentControls: LinearLayout
    private val hideHandler = Handler()

    private val player1 = Player()
    private val player2 = Player()
    private var turnScore = 0
    private var currentPlayer = 1
    private var hasRolled = false

    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        fullscreenContent.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        fullscreenContentControls.visibility = View.VISIBLE
    }
    private var isFullscreen: Boolean = false

    private val hideRunnable = Runnable { hide() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val delayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS)
            }
            MotionEvent.ACTION_UP -> view.performClick()
            else -> {
            }
        }
        false
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isFullscreen = true

        // Set up the user interaction to manually show or hide the system UI.
        fullscreenContent = findViewById(R.id.fullscreen_content)
        fullscreenContent.setOnClickListener { toggle() }

        fullscreenContentControls = findViewById(R.id.fullscreen_content_controls)

        // set up bindings
        binding  = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.currentPlayer.text = "Player 1 is rolling"


        // create game object




        // roll button onClick
        binding.rollButton.setOnClickListener {

            hasRolled = true
            val diceResult = Dice.roll() // rolls dice
            setDiceImage(diceResult) // set dice images

            val diceTotal = Dice.getDiceTotal()


            when(diceTotal) {

                -2 -> { // double were rolled, player must continue turn

                    hasRolled = false

                    turnScore += (diceResult[0] + diceResult[1])

                    if(currentPlayer == 1 && turnScore + player1.getScore() >= 50) {

                        endGame()
                    }
                    else if(currentPlayer == 2 && turnScore + player2.getScore() >= 50) {

                        endGame()
                    }

                    binding.turnScore.text = "Turn Score: $turnScore"
                }
                -1 -> { // double 1 was rolled, set playerScore to 0, end turn

                    if(currentPlayer == 1)
                        player1.resetScore()
                    else
                        player2.resetScore()

                    turnScore = 0
                    endPlayerTurn()
                }
                0 -> { // single 1 was rolled, set turnScore to 0, end turn

                    turnScore = 0
                    endPlayerTurn()
                }
                else -> {

                    turnScore += diceTotal

                    if(currentPlayer == 1 && turnScore + player1.getScore() >= 50) {

                        endGame()
                    }
                    else if(currentPlayer == 2 && turnScore + player2.getScore() >= 50) {

                        endGame()
                    }

                    binding.turnScore.text = "Turn Score: $turnScore"
                }
            }

        } //end rollButton onClick

        // hold button onClick listener
        binding.holdButton.setOnClickListener {

            if (!hasRolled) {

                displayAlert()
            }
            else  {

                endPlayerTurn()
            }

        }//end holdButton onClick
    }

    @SuppressLint("SetTextI18n")
    private fun endPlayerTurn() {

       if(currentPlayer == 1) { // end player 1 turn

           binding.currentPlayer.text = "Player 2 is rolling"
           binding.turnScore.text = "Turn Score: 0"

           player1.updateScore(turnScore)
           binding.player1Score.text = player1.getScore().toString()

           turnScore = 0
           currentPlayer = 2
           hasRolled = false
        }
        else { // end player 2 turn

           binding.currentPlayer.text = "Player 1 is rolling"
           binding.turnScore.text = "Turn Score: 0"

           // update player 2 score
           player2.updateScore(turnScore)
           binding.player2Score.text = player2.getScore().toString()

           turnScore = 0
           currentPlayer = 1
           hasRolled = false
        }
    }
    private fun endGame() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Player $currentPlayer Wins!")
        builder.setPositiveButton( "OK",DialogInterface.OnClickListener { dialog, id ->
            dialog.dismiss()
        })

        val dialog = builder.create()
        dialog.show()

        finish()
    }
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    private fun toggle() {
        if (isFullscreen) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        fullscreenContentControls.visibility = View.GONE
        isFullscreen = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        fullscreenContent.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        isFullscreen = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }

    // displays an alert if player attempts to end turn before rolling
    private fun displayAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("You haven't completed your turn, please roll the dice")
        builder.setPositiveButton( "OK",DialogInterface.OnClickListener { dialog, id ->
            dialog.cancel()
        })

        val dialog = builder.create()
        dialog.show()
    }

    private fun setDiceImage(diceResult: IntArray) {

        when(diceResult[0]) {
            1 -> binding.dice1Image.setImageResource(R.drawable.diceone)
            2 -> binding.dice1Image.setImageResource(R.drawable.dicetwo)
            3 -> binding.dice1Image.setImageResource(R.drawable.dicethree)
            4 -> binding.dice1Image.setImageResource(R.drawable.dicefour)
            5 -> binding.dice1Image.setImageResource(R.drawable.dicefive)
            6 -> binding.dice1Image.setImageResource(R.drawable.dicesix)
        }
        when(diceResult[1]) {
            1 -> binding.dice2Image.setImageResource(R.drawable.diceone)
            2 -> binding.dice2Image.setImageResource(R.drawable.dicetwo)
            3 -> binding.dice2Image.setImageResource(R.drawable.dicethree)
            4 -> binding.dice2Image.setImageResource(R.drawable.dicefour)
            5 -> binding.dice2Image.setImageResource(R.drawable.dicefive)
            6 -> binding.dice2Image.setImageResource(R.drawable.dicesix)
        }
    }
}