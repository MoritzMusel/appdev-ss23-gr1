package com.example.supersnake

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import java.util.concurrent.CancellationException



@SuppressLint("ResourceAsColor")
class SnakeGameViewMultiplayer (context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var gameActive = false
    private val paintBackground = Paint();
    private val paintSnake1 = Paint();
    private val paintSnake2 = Paint();
    private val foodColor = Paint();
    private lateinit var state: GameState

    init {
        paintBackground.color = R.color.BG_COLOUR;
        paintSnake1.color = R.color.SNAKE_COLOUR;
        paintSnake2.color = R.color.SNAKE_COLOUR2;
        foodColor.color = R.color.FOOD_COLOUR;

    }

    fun startGame(gameState: GameState) {
        gameActive = true
        this.state = gameState
        handleGameState()
    }



    private fun handleGameState() {
        if (!gameActive) {
            return
        }
        requestAnimationFrame()
    }


    private fun requestAnimationFrame() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                invalidate() // View neu zeichnen
                handler.postDelayed(this, 16)
            }
        },0)
    }


    @SuppressLint("ResourceAsColor", "DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val viewWidth = width
        val viewHeight = height
        canvas.drawRect(0f, 0f,viewWidth.toFloat(),viewHeight.toFloat(), paintBackground)

        val food = state.food
        val gridSizeWidth = viewWidth / state.gridsize
        val gridSizeHeight = viewWidth /state.gridsize
        //drawFood
        canvas.drawRect(food.x * gridSizeWidth.toFloat(), food.y * gridSizeHeight.toFloat() ,gridSizeWidth.toFloat(),gridSizeHeight.toFloat(), foodColor)

        //drawPlayer 1
        paintPlayer(canvas, state.players[0], gridSizeWidth.toFloat(),gridSizeHeight.toFloat(), paintSnake1)
        //drawPlayer 2
        paintPlayer(canvas, state.players[1], gridSizeWidth.toFloat(),gridSizeHeight.toFloat(), paintSnake1)
    }

    private fun paintPlayer(canvas: Canvas, playerState: Player, width: Float, height: Float, color: Paint) {
        val snake = playerState.snake
        for (cell in snake) {
            canvas?.drawRect(cell.x * width, cell.y * height, width, height, color)

        }
    }

    fun stopGame() {
        gameActive = false
    }
}



