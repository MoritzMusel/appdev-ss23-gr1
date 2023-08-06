package com.example.supersnake.snake

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.supersnake.snake.Snake.Companion.CELL_SIZE
import kotlin.random.Random

class SnakeThread(private val surfaceHolder: SurfaceHolder, private val surfaceView: SurfaceView, private val navigationCallback: CallbackNavigation, private val context:Context) : Thread() {

    private var running = false
    private val targetFPS = 60 // Adjust the desired frame rate
    private val frameTime = (1000 / targetFPS).toLong()
    private var lastUpdateTime = System.currentTimeMillis()
    // Adjust the speed of the snake here
    private var moveInterval = 150L // Snake moves every 200 milliseconds

    private var currentDirection = Direction.RIGHT

    val snake = Snake() // Create your Snake class instance here
    private val food = Food() // Create your Food class instance here
    private val powerUp = PowerUp(snake) //Snake power up

    private val backgroundPaint = Paint().apply {
        color = Color.parseColor("#303030")
    }

    private val foodPaint = Paint().apply {
        color = Color.parseColor("#FA1919")
        style = Paint.Style.FILL
    }

    private val snakePaint = Paint().apply {
        color = Color.parseColor("#69FF93")
        style = Paint.Style.FILL
    }
    private val snakePaintHead = Paint().apply {
        color = Color.parseColor("#00AD31")
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
        while (running) {
            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - lastUpdateTime

            if (elapsedTime >= moveInterval) {
                updateGame()
                drawGame()
                lastUpdateTime = currentTime
            }

            val timeMillis = System.currentTimeMillis() - currentTime
            val sleepTime = frameTime - timeMillis

            if (sleepTime > 0) {
                try {
                    sleep(sleepTime)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun updateGame() {
        // Update the game state here (snake movement, collisions, etc.)
        // Move the snake based on its current direction
        snake.setDirection(currentDirection)
        snake.move()

        // Check for collisions with the food
        if (food.checkCollision(snake)) {
            snake.grow()
            food.respawn(surfaceView.width, surfaceView.height, snake)
            if(snake.bodyParts.size%3==0) moveInterval = (moveInterval*1.1).toLong()
        }

        if (!powerUp.isActive() && Random.nextInt(100) < 5) {
            // Spawn a new power-up with a 5% chance on each game loop iteration
            powerUp.spawn(surfaceView.width, surfaceView.height)
        }

        if (powerUp.checkCollision()) {
            powerUp.activatePowerUp()
        }
        // Check if the power-up duration has ended
        if (snake.isDoubleGrowthActive() && System.currentTimeMillis() >= snake.getDoubleGrowthEndTime()) {
            snake.deactivateDoubleGrowth()
        }

        // Check for collisions with the walls and the snake itself
        if (checkWallCollision() || snake.checkCollision()) {
            gameOver()
        }
    }

    private fun drawGame() {
        val canvas: Canvas? = surfaceHolder.lockCanvas()
        canvas?.let {
            // Clear the canvas with a black background
            it.drawRect(0f, 0f, it.width.toFloat(), it.height.toFloat(), backgroundPaint)

            // Draw the food
            food.draw(canvas, context)

            powerUp.draw(canvas)
            // Draw the snake
            val snakeRect = Rect(snake.getHead().x, snake.getHead().y, snake.getHead().x + CELL_SIZE, snake.getHead().y + CELL_SIZE)
            it.drawRect(snakeRect, snakePaintHead)
            for (bodyPart in snake.bodyParts.subList(1, snake.bodyParts.size-1)) {
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

    fun updateDirection(newDirection: Direction) {
        currentDirection = newDirection
    }
}