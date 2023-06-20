package com.example.supersnake

import android.R.attr.bitmap
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
        private var apple: Apple? = null
        private var gameStarted = false
        private var isGameOver = false
        private lateinit var snakeHeadBitmap: Bitmap
        private lateinit var snakeHeadDrawable: Drawable
        private lateinit var snakeHeadUpBitmap: Bitmap
        private lateinit var snakeHeadDownBitmap: Bitmap
        private lateinit var snakeHeadLeftBitmap: Bitmap
        private lateinit var snakeHeadRightBitmap: Bitmap
        private lateinit var appleBitmap: Bitmap

        private lateinit var snakeBodyBitmap: Bitmap
        private lateinit var snakeBodyDrawable: Drawable
        private lateinit var snakeBodyVerticalBitmap: Bitmap
        private lateinit var snakeBodyHorizontalBitmap: Bitmap

        private var snakeParts: MutableList<SnakePart> = mutableListOf(
            SnakePart(5, 5, Direction.UP),
            SnakePart(5, 6, Direction.UP),
            SnakePart(5, 7, Direction.UP)
        )

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
            apple = Apple()

            // Load the snake head and body image bitmaps for different orientations
            snakeHeadBitmap = BitmapFactory.decodeResource(resources, R.drawable.snake_head)
            snakeHeadUpBitmap = rotateBitmap(snakeHeadBitmap, -90f)
            snakeHeadDownBitmap = rotateBitmap(snakeHeadBitmap, 90f)
            snakeHeadLeftBitmap = rotateBitmap(snakeHeadBitmap, 180f)
            snakeHeadRightBitmap = rotateBitmap(snakeHeadBitmap, 0f)

            snakeBodyBitmap = BitmapFactory.decodeResource(resources, R.drawable.snake_body)
            snakeBodyVerticalBitmap = rotateBitmap(snakeBodyBitmap, 90f)
            snakeBodyHorizontalBitmap = rotateBitmap(snakeBodyBitmap, 0f)

            snakeHeadBitmap = snakeHeadUpBitmap
            snakeHeadBitmap = Bitmap.createScaledBitmap(snakeHeadBitmap, snakeSize, snakeSize, false)

            // Load the apple image bitmap
            val rawBitmap = BitmapFactory.decodeResource(resources, R.drawable.apple)
            val scaledWidth = snakeSize
            val scaledHeight = snakeSize
            appleBitmap = Bitmap.createScaledBitmap(rawBitmap, scaledWidth, scaledHeight, false)
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
            snakeParts.add(SnakePart(2, 0, Direction.RIGHT))
            snakeParts.add(SnakePart(1, 0, Direction.RIGHT))
            snakeParts.add(SnakePart(0, 0, Direction.RIGHT))
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

        private fun getSnakeHeadDrawable(): Drawable {
            return when (direction) {
                Direction.UP -> BitmapDrawable(resources, snakeHeadUpBitmap)
                Direction.DOWN -> BitmapDrawable(resources, snakeHeadDownBitmap)
                Direction.LEFT -> BitmapDrawable(resources, snakeHeadLeftBitmap)
                Direction.RIGHT -> BitmapDrawable(resources, snakeHeadRightBitmap)
            }
        }

        private fun getSnakeBodyDrawable(dir: Direction): Drawable {
           // val d = if(dir != null) dir else direction
            return when (dir) {
                Direction.UP, Direction.DOWN -> BitmapDrawable(resources, snakeBodyVerticalBitmap)
                Direction.RIGHT, Direction.LEFT -> BitmapDrawable(resources, snakeBodyHorizontalBitmap)
            }
        }

        private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(degrees)
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawColor(Color.BLACK)
            val paint = Paint()
            paint.color = Color.WHITE
            paint.strokeWidth = 5f

            // Draw the snake's head
            val snakeHead = snakeParts.first()
            val snakeHeadX = snakeHead.x * snakeSize
            val snakeHeadY = snakeHead.y * snakeSize
            snakeHeadDrawable = getSnakeHeadDrawable()
            snakeHeadDrawable.setBounds(snakeHeadX, snakeHeadY, snakeHeadX + snakeSize, snakeHeadY + snakeSize)
            snakeHeadDrawable.draw(canvas)

            // Draw the rest of the snake parts
            snakeParts.subList(1, snakeParts.size).forEach { part ->
                val x = part.x * snakeSize
                val y = part.y * snakeSize
               /* var dir: Direction = Direction.LEFT

                if(snakeHead.x == x && (snakeHead.y > y || snakeHead.y < y)){
                    dir = Direction.UP
                }else if(snakeHead.x != x && (snakeHead.y > y || snakeHead.y < y)){
                    dir = Direction.LEFT
                }else if(snakeHead.y == y && (snakeHead.x > x || snakeHead.x < x)){
                    dir = Direction.UP
                }else{
                    dir = Direction.UP
                }

                when(dir){
                    Direction.UP, Direction.DOWN -> {
                        snakeBodyDrawable = getSnakeBodyDrawable(dir)
                    }
                    Direction.LEFT, Direction.RIGHT -> {
                        snakeBodyDrawable = getSnakeBodyDrawable(dir)
                    }
                }*/
                snakeBodyDrawable = getSnakeBodyDrawable(part.direction)
                snakeBodyDrawable.setBounds(x, y, x + snakeSize, y + snakeSize)
                snakeBodyDrawable.draw(canvas)
            }

            // Draw the apple
            apple?.let {
                val appleX = it.x * snakeSize
                val appleY = it.y * snakeSize
                val destRect = RectF(appleX.toFloat(), appleY.toFloat(), (appleX + snakeSize).toFloat(), (appleY + snakeSize).toFloat())
                canvas.drawBitmap(appleBitmap, null, destRect, null)
            }

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

    data class SnakePart(var x: Int, var y: Int, var direction: Direction)

    data class Apple(var x: Int = 5, var y: Int = 5)

    enum class Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
}
