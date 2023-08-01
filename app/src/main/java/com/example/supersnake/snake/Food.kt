package com.example.supersnake.snake

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
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
        canvas.drawRect(x.toFloat(), y.toFloat(),
            (x + Snake.CELL_SIZE).toFloat(), (y + Snake.CELL_SIZE).toFloat(), paint)
       // val applePic = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.apple)
       // canvas.drawBitmap(applePic, Rect(0, 0, (x + Snake.CELL_SIZE), (y + Snake.CELL_SIZE)), Rect(0, 0, x, y), paint)

    }

    fun checkCollision(snake: Snake): Boolean {
        val head = snake.getHead()
        return head.x == x && head.y == y
    }
}
