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

    private val paintBackground = Paint().apply {
        color = ContextCompat.getColor(context, R.color.BG_COLOUR)
    }

    private val paintSnake1 = Paint().apply {
        color = ContextCompat.getColor(context, R.color.SNAKE_COLOUR)
    }

    private val paintSnake2 = Paint().apply {
        color = ContextCompat.getColor(context, R.color.SNAKE_COLOUR2)
    }

    private val foodColor = Paint().apply {
        color = ContextCompat.getColor(context, R.color.FOOD_COLOUR)
    }
    private lateinit var state: GameState
    private var initialized = false;

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

    val gson = Gson()

    init {
        paintBackground.color = R.color.BG_COLOUR;
        paintSnake1.color = R.color.SNAKE_COLOUR;
        paintSnake2.color = R.color.SNAKE_COLOUR2;
        foodColor.color = R.color.FOOD_COLOUR;

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
            drawGrid(canvas)
            val gridSizeWidth = width.toFloat() / state.gridsize
            val gridSizeHeight = height.toFloat() / state.gridsize

            drawFood(canvas, state.food, gridSizeWidth, gridSizeHeight)
            drawSnake(canvas, state.players[0], gridSizeWidth, gridSizeHeight, paintSnake1)
            drawSnake(canvas, state.players[1], gridSizeWidth, gridSizeHeight, paintSnake2)
        }
            drawGrid(canvas)
            val currentState = state ?: return // Safe access using the safe-call operator

            val gridSizeWidth = width.toFloat() / currentState.gridsize
            val gridSizeHeight = height.toFloat() / currentState.gridsize

            drawFood(canvas, currentState.food, gridSizeWidth, gridSizeHeight)
            drawSnake(canvas, currentState.players[0], gridSizeWidth, gridSizeHeight, paintSnake1)
            drawSnake(canvas, currentState.players[1], gridSizeWidth, gridSizeHeight, paintSnake2)

    }

    private fun drawGrid(canvas: Canvas) {
        val numRows = 40
        val numCols = 40

        println(width.toFloat())
        println(height.toFloat())

        val cellWidth = width.toFloat() / numCols
        val cellHeight = height.toFloat() / numRows

        val gridPaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }

        val cellPaint = Paint().apply {
            color = Color.BLUE
            style = Paint.Style.FILL
        }

        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                val left = col * cellWidth
                val top = row * cellHeight
                val right = left + cellWidth
                val bottom = top + cellHeight
                canvas.drawRect(left, top, right, bottom, cellPaint)
                canvas.drawRect(left, top, right, bottom, gridPaint)
            }
        }
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintBackground)
    }

    private fun drawFood(canvas: Canvas, food: Food, width: Float, height: Float) {
        canvas.drawRect(food.x * width, food.y * height, (food.x + 1) * width, (food.y + 1) * height, foodColor)
    }

    private fun drawSnake(canvas: Canvas, playerState: Player, width: Float, height: Float, paint: Paint) {
        val snake = playerState.snake
        for (cell in snake) {
            canvas.drawRect(cell.x * width, cell.y * height, (cell.x + 1) * width, (cell.y + 1) * height, paint)
        }
    }


    private fun paintPlayer(canvas: Canvas, playerState: Player, width: Float, height: Float, color: Paint) {
        val snake = playerState.snake
        for (cell in snake) {
            canvas.drawRect(cell.x * width, cell.y * height, width, height, color)

        }
    }

    fun isPropertyInitialized(): Boolean {
        return this::state.isInitialized
    }

}