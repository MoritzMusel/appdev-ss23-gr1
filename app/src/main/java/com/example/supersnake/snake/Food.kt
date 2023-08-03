package com.example.supersnake.snake

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.supersnake.R
import java.util.Random


class Food {
    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    internal var x = Snake.CELL_SIZE*3
    internal var y = Snake.CELL_SIZE*3

    fun respawn(width: Int, height: Int, snake: Snake) {
        val random = Random()
        var validPosition = false
        while (!validPosition) {
            x = random.nextInt(width / Snake.CELL_SIZE) * Snake.CELL_SIZE
            y = random.nextInt(height / Snake.CELL_SIZE) * Snake.CELL_SIZE
            validPosition = snake.bodyParts.none { it.x == x && it.y == y }
        }
    }

    fun draw(canvas: Canvas) {
        val originalBitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.snake_apple)
        val imageBitmap = Bitmap.createScaledBitmap(originalBitmap, Snake.CELL_SIZE, Snake.CELL_SIZE, false)
        originalBitmap.recycle()

        canvas?.let {
            imageBitmap?.let { bitmap ->
                // Draw the image on the canvas
                canvas.drawBitmap(bitmap, x.toFloat(), y.toFloat(), null)
            }
        }
        //val applePic = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.apple)
       //canvas.drawBitmap(applePic, Rect(0, 0, (x + Snake.CELL_SIZE), (y + Snake.CELL_SIZE)), Rect(0, 0, x, y), paint)
    }

    fun checkCollision(snake: Snake): Boolean {
        val head = snake.getHead()
        return head.x == x && head.y == y
    }
}
