package com.example.supersnake.snake

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.supersnake.snake.Snake.Companion.CELL_SIZE

class SnakeThread(private val surfaceHolder: SurfaceHolder, private val surfaceView: SurfaceView, private val navigationCallback: CallbackNavigation) : Thread() {

    private var running = false
    private val targetFPS = 10 // Adjust the desired frame rate
    private val frameTime = (1000 / targetFPS).toLong()

    val snake = Snake() // Create your Snake class instance here
    private val food = Food() // Create your Food class instance here

    private val backgroundPaint = Paint().apply {
        color = Color.BLACK
    }

    private val foodPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    private val snakePaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
    }

    fun startThread() {
        running = true
        start()
    }

    fun stopThread() {
        running = false
        interrupt()
    }

    override fun run() {
        var startTime: Long
        var timeMillis: Long
        var waitTime: Long
        while (running) {
            startTime = System.currentTimeMillis()
            updateGame()
            drawGame()
            timeMillis = System.currentTimeMillis() - startTime
            waitTime = frameTime - timeMillis
            if (waitTime > 0 && running) {
                sleep(waitTime)
            }
        }
    }

    private fun updateGame() {
        // Update the game state here (snake movement, collisions, etc.)
        // Move the snake based on its current direction
        snake.move()

        // Check for collisions with the food
        if (food.checkCollision(snake)) {
            Log.i("SnakeThread", "food hit")
            snake.grow()
            food.respawn(surfaceView.width, surfaceView.height, snake)
        }

        // Check for collisions with the walls
        if (checkWallCollision()) {
            gameOver()
        }

        // Check for collisions with itself
        if (snake.checkCollision()) {
            gameOver()
        }
    }

    private fun drawGame() {
        val canvas: Canvas? = surfaceHolder.lockCanvas()
        canvas?.let {
            // Clear the canvas with a black background
            it.drawRect(0f, 0f, it.width.toFloat(), it.height.toFloat(), backgroundPaint)

            // Draw the food
            val foodRect = Rect(food.x, food.y, food.x + CELL_SIZE, food.y + CELL_SIZE)
            it.drawRect(foodRect, foodPaint)

            // Draw the snake
            for (bodyPart in snake.bodyParts) {
                val snakeRect = Rect(bodyPart.x, bodyPart.y, bodyPart.x + CELL_SIZE, bodyPart.y + CELL_SIZE)
                it.drawRect(snakeRect, snakePaint)
            }

            // Unlock the canvas and post the changes
            surfaceHolder.unlockCanvasAndPost(it)
        }
    }

    private fun checkWallCollision(): Boolean {
        val head = snake.getHead()
        return head.x < 0 || head.y < 0 || head.x >= surfaceView.width || head.y >= surfaceView.height
    }

    private fun gameOver() {
        // Handle the game over condition here (e.g., show a dialog, restart the game, etc.)
        // You can stop the game loop by setting running = false or reset the game state as needed.
        Log.i("SnakeGame", "GameOver")
        running = false
        this.stopThread()
        navigationCallback.onGameOver(snake.bodyParts.size)
    }
}