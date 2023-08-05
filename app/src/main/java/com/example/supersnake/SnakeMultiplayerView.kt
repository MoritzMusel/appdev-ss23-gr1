package com.example.supersnake

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.google.gson.Gson

@SuppressLint("ResourceAsColor")
class SnakeMultiplayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paintBackground = Paint()
    private val paintSnake1 = Paint()
    private val paintSnake2 = Paint()
    private val foodColor = Paint()
    private lateinit var state: GameState
    private var initialized = false
    private  var gson: Gson

    val json = """
{
    "players": [
        {
            "pos": {
                "x": 3,
                "y": 10
            },
            "vel": {
                "x": 0,
                "y": 0
            },
            "snake": [
                { "x": 1, "y": 10 },
                { "x": 2, "y": 10 },
                { "x": 3, "y": 10 }
            ],
            "points": 0,
            "playerOneName": ""
        },
        {
            "pos": {
                "x": 18,
                "y": 10
            },
            "vel": {
                "x": 0,
                "y": 0
            },
            "snake": [
                { "x": 20, "y": 10 },
                { "x": 19, "y": 10 },
                { "x": 18, "y": 10 }
            ],
            "points": 0,
            "playerTwoName": ""
        }
    ],
    "food": {},
    "gridsize": 20
}
"""

    init {
        paintBackground.color = ContextCompat.getColor(context, R.color.BG_COLOUR)
        paintSnake1.color = ContextCompat.getColor(context, R.color.SNAKE_COLOUR)
        paintSnake2.color = ContextCompat.getColor(context, R.color.SNAKE_COLOUR2)
        foodColor.color = ContextCompat.getColor(context, R.color.FOOD_COLOUR)
        gson = Gson()
    }

    fun setStuff(gameState: GameState){
        this.state = gameState
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if(!initialized){
            this.state = gson.fromJson(json, GameState::class.java)
            initialized = true;
            drawBackground(canvas)
            //drawGrid(canvas)
           drawGameState(canvas, state)
        }
            //drawGrid(canvas)
        val currentState = state ?: return // Safe access using the safe-call operator
        drawGameState(canvas, currentState)

    }

    private fun drawGameState(canvas: Canvas, state : GameState){
        val gridSizeWidth = width.toFloat() / state.gridsize
        val gridSizeHeight = height.toFloat() / state.gridsize

        paintFood(canvas, state.food, gridSizeWidth, gridSizeHeight)
        paintPlayer(canvas, state.players[0], gridSizeWidth, gridSizeHeight, paintSnake1)
        paintPlayer(canvas, state.players[1], gridSizeWidth, gridSizeHeight, paintSnake2)
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintBackground)
    }


    private fun paintFood(canvas: Canvas, food: Food, width: Float, height: Float) {
        canvas.drawRect(food.x * width, food.y * height, (food.x + 1) * width, (food.y + 1) * height, foodColor)
    }


    private fun paintPlayer(canvas: Canvas, playerState: Player, width: Float, height: Float, paint: Paint) {
        val snake = playerState.snake
        for (cell in snake) {
            canvas.drawRect(cell.x * width, cell.y * height, (cell.x + 1) * width, (cell.y + 1) * height, paint)
        }
    }



}