package com.example.supersnake

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import java.util.Random

class GameViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val parentLayout = FrameLayout(this)
        val snakeView = SnakeView(this)
        parentLayout.addView(snakeView)
        parentLayout.addView(snakeView.retryButton)
        setContentView(parentLayout)
    }

    inner class SnakeView(context: Context) : View(context) {
        private var timer: Thread? = null
        private var direction = Direction.RIGHT
        private var isPlaying = false
        private val snakeSize = 50
        private val snakeParts = ArrayList<SnakePart>()
        private var apple: Apple? = null
        private var gameStarted = false
        private var isGameOver = false

        // Retry button
        val retryButton = Button(context).apply {
            text = "Retry"
            visibility = View.GONE
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
                width = resources.getDimensionPixelSize(R.dimen.retry_button_width)
                height = resources.getDimensionPixelSize(R.dimen.retry_button_height)
            }
            setOnClickListener {
                restartGame()
            }
        }

        init {
            snakeParts.add(SnakePart(2, 0))
            snakeParts.add(SnakePart(1, 0))
            snakeParts.add(SnakePart(0, 0))
            apple = Apple()
        }

        fun startGame() {
            isPlaying = true
            timer = Thread {
                while (isPlaying) {
                    updateGame()
                    postInvalidate()
                    try {
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                gameover()
            }
            timer?.start()
        }

        private fun updateGame() {
            val head = snakeParts.first().copy()
            when (direction) {
                Direction.UP -> head.y--
                Direction.DOWN -> head.y++
                Direction.LEFT -> head.x--
                Direction.RIGHT -> head.x++
            }
            snakeParts.add(0, head)
            // Check collision with the wall
            if (head.x < 0 || head.y < 0 || head.x >= width / snakeSize || head.y >= height / snakeSize) {
                isPlaying = false
                return
            }

            // Check collision with the apple
            if (head.x == apple?.x && head.y == apple?.y) {
                // Remove the apple and increase snake length
                apple = null
                increaseSnakeLength()
            } else {
                snakeParts.removeAt(snakeParts.size - 1)
            }

            // If there is no apple, generate a new one
            if (apple == null) {
                generateApple()
            }
        }

        private fun increaseSnakeLength() {
            // ... Logic to increase snake length ...
            generateApple()
        }

        private fun generateApple() {
            val maxX = width / snakeSize
            val maxY = height / snakeSize
            val random = Random()
            var appleX = random.nextInt(maxX)
            var appleY = random.nextInt(maxY)

            // Make sure the apple doesn't overlap with the snake's body
            while (snakeParts.any { it.x == appleX && it.y == appleY }) {
                appleX = random.nextInt(maxX)
                appleY = random.nextInt(maxY)
            }

            apple = Apple(appleX, appleY)
        }

        private fun restartGame() {
            snakeParts.clear()
            snakeParts.add(SnakePart(2, 0))
            snakeParts.add(SnakePart(1, 0))
            snakeParts.add(SnakePart(0, 0))
            apple = Apple()
            direction = Direction.RIGHT // Set initial direction to right
            isGameOver = false
            isPlaying = false
            retryButton.visibility = View.GONE
            startGame()
        }

        private fun gameover() {
            isPlaying = false
            isGameOver = true
            post {
                retryButton.visibility = View.VISIBLE
            }
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawColor(Color.BLACK)
            val paint = Paint()
            paint.color = Color.WHITE
            paint.strokeWidth = 5f

            snakeParts.forEach { part ->
                val x = part.x * snakeSize
                val y = part.y * snakeSize
                canvas.drawRect(x.toFloat(), y.toFloat(), (x + snakeSize).toFloat(), (y + snakeSize).toFloat(), paint)
            }

            paint.color = Color.RED
            val appleX = apple?.x ?: 0
            val appleY = apple?.y ?: 0
            canvas.drawRect(appleX * snakeSize.toFloat(), appleY * snakeSize.toFloat(),
                (appleX * snakeSize + snakeSize).toFloat(), (appleY * snakeSize + snakeSize).toFloat(), paint)

            // Draw retry button when game over
            if (isGameOver) {
                retryButton.x = (width - retryButton.width) / 2f
                retryButton.y = (height - retryButton.height) / 2f
            }
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!gameStarted) {
                        gameStarted = true
                        startGame()
                    }
                    val x = event.x
                    val y = event.y
                    val snakeHead = snakeParts.first()
                    if (x < snakeHead.x * snakeSize && direction != Direction.RIGHT) {
                        direction = Direction.LEFT
                    } else if (x > snakeHead.x * snakeSize + snakeSize && direction != Direction.LEFT) {
                        direction = Direction.RIGHT
                    } else if (y < snakeHead.y * snakeSize && direction != Direction.DOWN) {
                        direction = Direction.UP
                    } else if (y > snakeHead.y * snakeSize + snakeSize && direction != Direction.UP) {
                        direction = Direction.DOWN
                    }
                }
            }
            return true
        }
    }

    data class SnakePart(var x: Int, var y: Int)

    data class Apple(var x: Int = 5, var y: Int = 5)

    enum class Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
}
