package com.example.supersnake

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
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
    private val paintSnake1Head = Paint()
    private val paintSnake2 = Paint()
    private val paintSnake2Head = Paint()
    private val foodColor = Paint()
    private lateinit var state: GameState
    private var initialized = false
    private  var gson: Gson
    private var originalBitmap : Bitmap


    private val json = """
{
    "players": [
        {
            "pos": {
                "x": 3,
                "y": 20
            },
            "vel": {
                "x": 0,
                "y": 0
            },
            "snake": [
                { "x": 1, "y": 20 },
                { "x": 2, "y": 20 },
                { "x": 3, "y": 20 }
            ],
            "points": 0,
            "playerOneName": ""
        },
        {
            "pos": {
                "x": 38,
                "y": 20
            },
            "vel": {
                "x": 0,
                "y": 0
            },
            "snake": [
                { "x": 40, "y": 20 },
                { "x": 39, "y": 20 },
                { "x": 38, "y": 20 }
            ],
            "points": 0,
            "playerTwoName": ""
        }
    ],
    "food": {},
    "gridsize": 40
}
"""
    /**
     * Initializes the properties of the class.
     * This initializer sets up various properties including colors for background,
     * snakes, and food using resources from the given context. It also initializes
     * the Gson object for JSON serialization and deserialization.
     */
    init {
        paintBackground.color = ContextCompat.getColor(context, R.color.BG_COLOUR)
        paintSnake1.color = ContextCompat.getColor(context, R.color.SNAKE_COLOUR)
        paintSnake1Head.color = ContextCompat.getColor(context, R.color.SNAKE_COLOUR_HEAD)

        paintSnake2.color = ContextCompat.getColor(context, R.color.SNAKE_COLOUR2)
        paintSnake2Head.color = ContextCompat.getColor(context, R.color.SNAKE_COLOUR2_HEAD)
        foodColor.color = ContextCompat.getColor(context, R.color.FOOD_COLOUR)
        gson = Gson()
        originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.snake_apple)
    }

    /**
     * Sets the game state and triggers a redraw of the view.
     * This method updates the internal game state with the provided {@link GameState}
     * object and triggers a redraw of the view to reflect the changes made to the game state.
     *
     * @param gameState The {@link GameState} object containing the updated game information.
     */
    fun setStuff(gameState: GameState){
        this.state = gameState
        invalidate()
    }

    /**
     * Draws the game view on the canvas.
     * This method is responsible for rendering the game view on the provided canvas.
     * It first initializes the game state from JSON if not already initialized,
     * then draws the background and the current game state on the canvas.
     *
     * @param canvas The canvas on which the game view is drawn.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if(!initialized){
            this.state = gson.fromJson(json, GameState::class.java)
            initialized = true;
            drawBackground(canvas)
            drawGameState(canvas, state)
        }
        val currentState = state
        drawGameState(canvas, currentState)
    }

    /**
     * This method renders the current game state on the given canvas using the provided dimensions and paints.
     * It paints the food item and the snakes of both players on the canvas.
     * @param canvas The canvas on which the game state is to be drawn.
     * @param state The current {@link GameState} to be rendered.
     */
    private fun drawGameState(canvas: Canvas, state : GameState){
        val gridSizeWidth = width.toFloat() / state.gridsize
        val gridSizeHeight = height.toFloat() / state.gridsize

        paintFood(canvas, state.food, gridSizeWidth, gridSizeHeight)
        paintPlayer(canvas, state.players[0], gridSizeWidth, gridSizeHeight, paintSnake1, paintSnake1Head)
        paintPlayer(canvas, state.players[1], gridSizeWidth, gridSizeHeight, paintSnake2, paintSnake2Head)
    }

    /**
     * This method draws the background of the game view on the given canvas using the defined paint.
     * It fills the entire canvas area with a rectangle using the specified background color.
     * @param canvas The canvas on which the background is to be drawn.
     */
    private fun drawBackground(canvas: Canvas) {
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), paintBackground)
    }

    /**
     * This method paints the specified food item on the given canvas using the predefined food color.
     * It draws a rectangle to represent the food item on the canvas.
     * @param canvas The canvas on which the food is to be painted.
     * @param food The food item to be painted.
     * @param width The width of a single cell on the canvas.
     * @param height The height of a single cell on the canvas.
     */
    private fun paintFood(canvas: Canvas, food: Food, width: Float, height: Float) {
        val left = food.x * width
        val top = food.y * height
        val right = (food.x + 1) * width
        val bottom = (food.y + 1) * height

        val rect = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        canvas.drawBitmap(originalBitmap, null, rect, null)
        //canvas.drawRect(food.x * width, food.y * height, (food.x + 1) * width, (food.y + 1) * height, foodColor)
    }


    /**
     * This method paints the snake of the specified player on the given canvas using the provided paint.
     * It iterates through the cells of the snake and draws rectangles to represent each cell on the canvas.
     * @param canvas The canvas on which the snake is to be painted.
     * @param playerState The state of the player whose snake is to be painted.
     * @param width The width of a single cell on the canvas.
     * @param height The height of a single cell on the canvas.
     * @param paint The paint used for drawing the snake cells.
     */
    private fun paintPlayer(canvas: Canvas, playerState: Player, width: Float, height: Float, paint: Paint, paintHead: Paint) {
        val snake = playerState.snake
        for (i in snake.indices) {
            val cell = snake[i]

            // Verwende eine andere Farbe für das letzte Element
            if (i == snake.size - 1) {
                val lastCellPaint = Paint(paint) // Kopiere die vorhandene Paint-Eigenschaften
                canvas.drawRect(cell.x * width, cell.y * height, (cell.x + 1) * width, (cell.y + 1) * height, paintHead)
            } else {
                canvas.drawRect(cell.x * width, cell.y * height, (cell.x + 1) * width, (cell.y + 1) * height, paint)
            }
        }
    }



}