package com.fchwpo.mymemory

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fchwpo.mymemory.models.BoardSize
import com.fchwpo.mymemory.models.MemoryGame
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var ctlRoot: ConstraintLayout
    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView
    private var boardSize = BoardSize.HARD
    private lateinit var memoryGame: MemoryGame
    private lateinit var adapter: MemoryBoardAdapter

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ctlRoot = findViewById(R.id.ctlRoot)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)

        createFreshBoard()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miRestart -> {
                // restartGame
                if (memoryGame.getNumberOfMoves() > 0 && !memoryGame.hasUserWon()) {
                    // show alert dialog
                    showAlertDialog("Quit your current game !?", null) {
                        createFreshBoard()
                    }
                } else {
                    createFreshBoard()
                }
            }
            R.id.miChoseNewSize -> {
                showNewSizeDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("InflateParams")
    private fun showNewSizeDialog() {
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        val radioGroup = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        when(boardSize) {
            BoardSize.EASY -> radioGroup.check(R.id.rbEasy)
            BoardSize.MEDIUM -> radioGroup.check(R.id.rbMedium)
            BoardSize.HARD -> radioGroup.check(R.id.rbHard)
        }
        showAlertDialog("Choose new size", boardSizeView) {
            // Set a new value of boardSize
            boardSize = when (radioGroup.checkedRadioButtonId) {
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD
            }
            // create new fresh board according to selection
            createFreshBoard()
        }
    }

    private fun showAlertDialog(
        title: String,
        view: View?,
        onPositiveButtonClickListener: View.OnClickListener
    ) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK") { _: DialogInterface, _: Int ->
                onPositiveButtonClickListener.onClick(null)
            }.show()
    }

    @SuppressLint("SetTextI18n")
    private fun createFreshBoard() {
        when(boardSize){
            BoardSize.EASY -> {
                tvNumPairs.text = "Pairs: 0/4"
                tvNumMoves.text = "Easy: 4x2"
            }
            BoardSize.MEDIUM -> {
                tvNumPairs.text = "Pairs: 0/9"
                tvNumMoves.text = "Medium: 6x3"
            }
            BoardSize.HARD -> {
                tvNumPairs.text = "Pairs: 0/24"
                tvNumMoves.text = "Hard: 6x4"
            }
        }
        memoryGame = MemoryGame(boardSize)
        adapter = MemoryBoardAdapter(
            this,
            boardSize,
            memoryGame.cards,
            object : MemoryBoardAdapter.SetOnViewClick {
                override fun onClick(position: Int) {
                    Log.i(TAG, "Clicked on $position")
                    updateGameOnCardClick(position)
                }
            })
        rvBoard.adapter = adapter
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }

    @SuppressLint("SetTextI18n")
    private fun updateGameOnCardClick(position: Int) {

        if (memoryGame.hasUserWon()) {
            Snackbar.make(ctlRoot, "You have Won!", Snackbar.LENGTH_LONG).show()
            return
        }
        if (memoryGame.clickedLastFlippedPosition(position)) {
            Snackbar.make(ctlRoot, "Invalid Move!", Snackbar.LENGTH_SHORT).show()
            return
        }

        if (memoryGame.flip(position)) {
            tvNumPairs.text =
                "Pairs ${memoryGame.getNumberOfPairsFound()} / ${boardSize.getNumOfPairs()}"
            val color = ArgbEvaluator().evaluate(
                (memoryGame.getNumberOfPairsFound() / boardSize.getNumOfPairs()).toFloat(),
                ContextCompat.getColor(this, R.color.color_progress_none),
                ContextCompat.getColor(this, R.color.color_progress_full)
            ) as Int
            tvNumPairs.setTextColor(color)
            if (memoryGame.hasUserWon()) {
                Snackbar.make(ctlRoot, "You have Won! Congratulations", Snackbar.LENGTH_LONG).show()
            }
        }

        tvNumMoves.text = "Moves : ${memoryGame.getNumberOfMoves()}"

        adapter.notifyDataSetChanged()
    }
}