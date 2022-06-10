package com.example.noughtscrosses

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.example.noughtscrosses.databinding.ActivityMainBinding
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {
    var db = FirebaseFirestore.getInstance()
    lateinit var binding: ActivityMainBinding
    var user: MutableMap<String, Any> = HashMap()
    var winCount = 1
    var Players: MutableMap<String, Any> = HashMap()

    //1=green  0 =red
    var activePlayer = 1
    var gameIsActive = true
    var count = 0
    var gameState = intArrayOf(2, 2, 2, 2, 2, 2, 2, 2, 2)
    var winningPositions = arrayOf(
        intArrayOf(0, 1, 2),
        intArrayOf(3, 4, 5),
        intArrayOf(6, 7, 8),
        intArrayOf(0, 3, 6),
        intArrayOf(1, 4, 7),
        intArrayOf(2, 5, 8),
        intArrayOf(0, 4, 8),
        intArrayOf(2, 4, 6)
    )

    fun dropIn(view: View) {
        val counter = view as ImageView
        val txt = findViewById<TextView>(R.id.winner1)
        val layout = findViewById<LinearLayout>(R.id.winner)
        //understand
        val tappedcounter = counter.tag.toString().toInt()
        if (gameState[tappedcounter] == 2 && gameIsActive) {
            if (activePlayer == 1) {
                counter.setImageResource(R.drawable.circle1)
                activePlayer = 0
                count++
                gameState[tappedcounter] = 1
            } else {
                counter.setImageResource(R.drawable.circle2)
                activePlayer = 1
                count++
                gameState[tappedcounter] = 0
            }
            counter.translationY = -1000f
            counter.animate().translationYBy(1000f).rotationY(1800f).duration = 1000
            for (winningposition in winningPositions) {
                if (gameState[winningposition[0]] == gameState[winningposition[1]] && gameState[winningposition[1]] == gameState[winningposition[2]] && gameState[winningposition[0]] != 2) {
                    if (gameState[winningposition[0]] == 0) {
                        txt.text = "Red Player Wins"
                        db.collection("Players")
                            .whereEqualTo("玩家名", binding.player2Name.text.toString())
                            .get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    for (document in task.result!!) {
                                        winCount = document.data["贏的次數"].toString().toInt()
                                    }
                                    winCount++
                                    Players["贏的次數"] = winCount
                                    db.collection("Players")
                                        .document(binding.player2Name.text.toString())
                                        .update(Players)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                this, "異動資料成功",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        .addOnFailureListener { e ->
                                            Players["玩家名"] = binding.player2Name.text.toString()
                                            Players["贏的次數"] = 1
                                            db.collection("Players")
                                                .document(binding.player2Name.text.toString())
                                                .set(Players)
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        this, "新增資料成功",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(
                                                        this, "新增資料失敗：" + e.toString(),
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                        }
                                }
                            }
                    } else if (gameState[winningposition[0]] == 1) {
                        txt.text = "Green Player Wins"
                        db.collection("Players")
                            .whereEqualTo("玩家名", binding.player1Name.text.toString())
                            .get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    for (document in task.result!!) {
                                        winCount = document.data["贏的次數"].toString().toInt()
                                    }
                                    winCount++
                                    Players["贏的次數"] = winCount
                                    db.collection("Players")
                                        .document(binding.player1Name.text.toString())
                                        .update(Players)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                this, "異動資料成功",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        .addOnFailureListener { e ->
                                            Players["玩家名"] = binding.player1Name.text.toString()
                                            Players["贏的次數"] = 1
                                            db.collection("Players")
                                                .document(binding.player1Name.text.toString())
                                                .set(Players)
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        this, "新增資料成功",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(
                                                        this, "新增資料失敗：" + e.toString(),
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                        }
                                }
                            }
                    }
                    layout.visibility = View.VISIBLE
                    gameIsActive = false
                }
            }
        }
        if (gameIsActive && count == 9) {
            txt.text = "DRAW"
            layout.visibility = View.VISIBLE
            gameIsActive = false
        }
    }

    fun playAgain(view: View?) {
        activePlayer = 1
        gameIsActive = true
        count = 0
        val linearLayout = findViewById<LinearLayout>(R.id.winner)
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        for (i in gameState.indices) {
            gameState[i] = 2
        }
        linearLayout.visibility = View.INVISIBLE
        for (i in 0 until gridLayout.childCount) {
            (gridLayout.getChildAt(i) as ImageView).setImageResource(0) //p t n
        }
    }

    fun home(view: View?) {
        binding.gridLayout.visibility = View.GONE
        binding.winner.visibility = View.GONE
        binding.player1Data.visibility = View.GONE
        binding.player2Data.visibility = View.GONE
        binding.player.visibility = View.VISIBLE
        binding.charts.visibility = View.VISIBLE
        binding.player1.setText("")
        binding.player2.setText("")
    }

    var players: String = ""
    lateinit var player1: EditText
    lateinit var player2: EditText
    lateinit var player1Name: TextView
    lateinit var player2Name: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db.collection("Players")
            .orderBy("贏的次數", Query.Direction.DESCENDING)
            .limit(2)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var chart: String = ""
                    for (document in task.result!!) {
                        chart += "\n玩家名：" + document.data["玩家名"] + "\n贏的次數：" + document.data["贏的次數"] + "\n"
                    }
                    if (chart != "") {
                        binding.chart.text = chart
                    } else {
                        binding.chart.text = "無排行資料"
                    }
                }
            }

        binding.btnUpdate.setOnClickListener({
            if (!binding.player1.text.toString().equals("") && !binding.player2.text.toString()
                    .equals("")
            ) {
                binding.player.visibility = View.GONE
                binding.charts.visibility = View.GONE
                binding.player1Data.visibility = View.VISIBLE
                binding.player2Data.visibility = View.VISIBLE
                binding.gridLayout.visibility = View.VISIBLE
                binding.player1Name.setText(binding.player1.text.toString())
                binding.player2Name.setText(binding.player2.text.toString())

                activePlayer = 1
                gameIsActive = true
                count = 0
                for (i in gameState.indices) {
                    gameState[i] = 2
                }
                val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
                for (i in 0 until gridLayout.childCount) {
                    (gridLayout.getChildAt(i) as ImageView).setImageResource(0) //p t n
                }
            } else {
                binding.error.visibility = View.VISIBLE
            }
        })
    }
}