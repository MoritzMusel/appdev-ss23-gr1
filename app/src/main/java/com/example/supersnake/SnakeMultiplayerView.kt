package com.example.supersnake

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

@SuppressLint("ResourceAsColor")
class SnakeMultiplayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

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

    fun setStuff(gameState: GameState){
        this.state = gameState
        invalidate()

    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val viewWidth = width
        val viewHeight = height
        canvas.drawRect(0f, 0f,viewWidth.toFloat(),viewHeight.toFloat(), paintBackground)

        if(isPropertyInitialized()){
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



    }

    private fun paintPlayer(canvas: Canvas, playerState: Player, width: Float, height: Float, color: Paint) {
        val snake = playerState.snake
        for (cell in snake) {
            canvas?.drawRect(cell.x * width, cell.y * height, width, height, color)

        }
    }

    fun isPropertyInitialized(): Boolean {
        return this::state.isInitialized
    }

}