package com.example.noughtscrosses

import android.media.MediaPlayer
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.example.noughtscrosses.databinding.ActivityMainBinding
import com.google.firebase.firestore.Query
import kotlin.collections.HashMap
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
    GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener,
    View.OnTouchListener {
    var db = FirebaseFirestore.getInstance()
    lateinit var binding: ActivityMainBinding
    var winCount = 1
    var Players: MutableMap<String, Any> = HashMap()
    lateinit var mper: MediaPlayer

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
                mper = MediaPlayer.create(this, R.raw.dropin)
                mper.start()
            } else {
                counter.setImageResource(R.drawable.circle2)
                activePlayer = 1
                count++
                gameState[tappedcounter] = 0
                mper = MediaPlayer.create(this, R.raw.dropin)
                mper.start()
            }
            counter.translationY = -1000f
            counter.animate().translationYBy(1000f).rotationY(1800f).duration = 1000
            for (winningposition in winningPositions) {
                if (gameState[winningposition[0]] == gameState[winningposition[1]] && gameState[winningposition[1]] == gameState[winningposition[2]] && gameState[winningposition[0]] != 2) {
                    if (gameState[winningposition[0]] == 0) {
                        txt.text = "Red Player Wins"
                        db.collection("Players")
                            .whereEqualTo("?????????", binding.player2Name.text.toString())
                            .get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    for (document in task.result!!) {
                                        winCount = document.data["????????????"].toString().toInt()
                                    }
                                    winCount++
                                    Players["????????????"] = winCount
                                    db.collection("Players")
                                        .document(binding.player2Name.text.toString())
                                        .update(Players)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                this, "??????????????????",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        .addOnFailureListener { e ->
                                            Players["?????????"] = binding.player2Name.text.toString()
                                            Players["????????????"] = 1
                                            db.collection("Players")
                                                .document(binding.player2Name.text.toString())
                                                .set(Players)
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        this, "??????????????????",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(
                                                        this, "?????????????????????" + e.toString(),
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                        }
                                }
                            }
                    } else if (gameState[winningposition[0]] == 1) {
                        txt.text = "Green Player Wins"
                        db.collection("Players")
                            .whereEqualTo("?????????", binding.player1Name.text.toString())
                            .get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    for (document in task.result!!) {
                                        winCount = document.data["????????????"].toString().toInt()
                                    }
                                    winCount++
                                    Players["????????????"] = winCount
                                    db.collection("Players")
                                        .document(binding.player1Name.text.toString())
                                        .update(Players)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                this, "??????????????????",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        .addOnFailureListener { e ->
                                            Players["?????????"] = binding.player1Name.text.toString()
                                            Players["????????????"] = 1
                                            db.collection("Players")
                                                .document(binding.player1Name.text.toString())
                                                .set(Players)
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        this, "??????????????????",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(
                                                        this, "?????????????????????" + e.toString(),
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

    fun back(view: View?) {
        binding.gridLayout.visibility = View.GONE
        binding.winner.visibility = View.GONE
        binding.player1Image.visibility = View.GONE
        binding.player2Image.visibility = View.GONE
        binding.player1Name.visibility = View.GONE
        binding.player2Name.visibility = View.GONE
        binding.player.visibility = View.VISIBLE
        binding.charts.visibility = View.VISIBLE
        binding.player1.setText("")
        binding.player2.setText("")
        count = 0
    }

    var player1Count = 0
    var player2Count = 0
    var rolldice = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db.collection("Players")
            .orderBy("????????????", Query.Direction.DESCENDING)
            .limit(2)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var chart: String = ""
                    for (document in task.result!!) {
                        chart += "\n????????????" + document.data["?????????"] + "\n???????????????" + document.data["????????????"] + "\n"
                    }
                    if (chart != "") {
                        binding.chart.text = chart
                    } else {
                        binding.chart.text = "???????????????"
                    }
                }
            }

        binding.btnUpdate.setOnClickListener({
            if (!binding.player1.text.toString().equals("") && !binding.player2.text.toString()
                    .equals("")
            ) {
                binding.player.visibility = View.GONE
                binding.charts.visibility = View.GONE
                binding.player1Name.visibility = View.VISIBLE
                binding.player2Name.visibility = View.VISIBLE
                binding.roll.visibility = View.VISIBLE
                binding.dice.setImageResource(R.drawable.dice)
                binding.player2Name.setText(binding.player2.text.toString())
                binding.player1Name.setText("?????????" + binding.player1.text.toString() + "?????????")
                activePlayer = 1
                gameIsActive = true

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

        binding.roll.setOnClickListener({
            rndDice()
            if (rolldice == 1 && counter>0) {
                player1Count = counter
                binding.player1Name.setText("??????" + binding.player1.text.toString() + "?????????" + player1Count)
                binding.player2Name.setText("?????????" + binding.player2.text.toString() + "?????????")
            } else if (rolldice == 2 && counter>0) {
                rolldice = 0
                player2Count = counter
                binding.player2Name.setText("??????" + binding.player2.text.toString() + "?????????" + player2Count)
                if (player1Count > player2Count) {
                    binding.startGame.visibility = View.VISIBLE
                    binding.start.setText("?????????" + binding.player1.text.toString() + "???????????????")
                } else if (player1Count < player2Count) {
                    binding.startGame.visibility = View.VISIBLE
                    binding.start.setText("?????????" + binding.player2.text.toString() + "???????????????")
                }
            }
        })

        binding.btnStart.setOnClickListener({
            binding.roll.visibility = View.GONE
            binding.startGame.visibility = View.GONE
            binding.gridLayout.visibility = View.VISIBLE
            binding.player1Image.visibility = View.VISIBLE
            binding.player2Image.visibility = View.VISIBLE
            if (player1Count > player2Count) {
                binding.player1Name.setText(binding.player1.text.toString())
                binding.player2Name.setText(binding.player2.text.toString())
            } else if (player1Count < player2Count) {
                binding.player1Name.setText(binding.player2.text.toString())
                binding.player2Name.setText(binding.player1.text.toString())
            }
        })

        gDetector = GestureDetector(this, this)
        home.setOnTouchListener(this)
        var res:Int = -1
        var countDrawables:Int = -1
        while (res != 0) {
            countDrawables++;
            res = getResources().getIdentifier("home" + countDrawables.toString(),
                "drawable", getPackageName());
        }
        TotalPictures = countDrawables
    }

    var counter = 0
    fun rndDice():Int {
        rolldice++
        counter = (1..6).random()
            when (counter) {
                1 -> binding.dice.setImageResource(R.drawable.dice1)
                2 -> binding.dice.setImageResource(R.drawable.dice2)
                3 -> binding.dice.setImageResource(R.drawable.dice3)
                4 -> binding.dice.setImageResource(R.drawable.dice4)
                5 -> binding.dice.setImageResource(R.drawable.dice5)
                6 -> binding.dice.setImageResource(R.drawable.dice6)
        }
        return counter
    }


    lateinit var gDetector: GestureDetector
    var PictureNo:Int = 0  //????????????????????????
    var TotalPictures:Int = 1
    fun ShowPicture() {
        var res:Int = getResources().getIdentifier("home" + PictureNo.toString(),
            "drawable", getPackageName())
        home.setImageResource(res)
    }

    fun btnGo(view: View?) {
        binding.homePage.visibility = View.GONE
        binding.player.visibility = View.VISIBLE
        binding.charts.visibility = View.VISIBLE
        binding.player1.setText("")
        binding.player2.setText("")
        count = 0
    }

    override fun onDown(p0: MotionEvent?): Boolean {
        return true
    }

    override fun onShowPress(p0: MotionEvent?) {
        //txv.text = "????????????????????????"
    }

    // ??????
    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        PictureNo = 0
        ShowPicture()
        return true
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        return true
    }

    override fun onLongPress(p0: MotionEvent?) {
        PictureNo = TotalPictures - 1
        ShowPicture()
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        if (e1!!.getX() < e2!!.getX()){  //????????????
            PictureNo++
            if (PictureNo == TotalPictures) {PictureNo = 0}
        }
        else{     //????????????
            PictureNo--;
            if (PictureNo < 0) {PictureNo = TotalPictures - 1 }
        }
        ShowPicture()
        return true
    }

    override fun onSingleTapConfirmed(p0: MotionEvent?): Boolean {
        return true
    }

    override fun onDoubleTap(p0: MotionEvent?): Boolean {
        return true
    }

    override fun onDoubleTapEvent(p0: MotionEvent?): Boolean {
        return true
    }

    override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
        gDetector.onTouchEvent(event)
        return true
    }
}