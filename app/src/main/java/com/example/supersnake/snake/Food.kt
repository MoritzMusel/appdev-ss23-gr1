package com.example.supersnake.snake

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
    }

    fun checkCollision(snake: Snake): Boolean {
        val head = snake.getHead()
        return head.x == x && head.y == y
    }
}
