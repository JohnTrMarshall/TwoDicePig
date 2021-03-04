package com.jtmarsha.twodicepig

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.jtmarsha.twodicepig.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)



        // set up binding
        binding  = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // start game button text
        val startGameButtonText = "Start Game"
        binding.startGameButton.text = startGameButtonText

        // listener for start game button, launches new GameActivity
        binding.startGameButton.setOnClickListener {

            //binding.startGameButton.text = "Test"
        val gameIntent = Intent(this, GameActivity::class.java)
        startActivity(gameIntent)


        }
    }
}