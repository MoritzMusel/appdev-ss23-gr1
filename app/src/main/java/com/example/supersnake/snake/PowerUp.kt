package com.example.supersnake.snake

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

class PowerUp(private val snake: Snake) {
    private val paint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }

    private var x = 0
    private var y = 0
    private var active = false
    private val powerUpDuration = 10000 // 10 seconds
    private val spawnDelay = 5000 // 5 seconds
    private var spawnTime = 0L
    private var nextSpawnTime = 0L

    fun spawn(width: Int, height: Int) {
        if (System.currentTimeMillis() > nextSpawnTime) {
            // Randomly spawn the power-up on the canvas
            val randomX = (0 until width / Snake.CELL_SIZE).random()
            val randomY = (0 until height / Snake.CELL_SIZE).random()
            x = randomX * Snake.CELL_SIZE
            y = randomY * Snake.CELL_SIZE
            active = true
            spawnTime = System.currentTimeMillis()
            nextSpawnTime = spawnTime + spawnDelay
        }
    }

    fun draw(canvas: Canvas) {
        if (active) {
            val powerUpRect = Rect(x, y, x + Snake.CELL_SIZE, y + Snake.CELL_SIZE)
            canvas.drawRect(powerUpRect, paint)
        }
    }

    fun checkCollision(): Boolean {
        if (!active) return false
        val head = snake.getHead()
        return head.x == x && head.y == y
    }

    fun activatePowerUp() {
        if (active) {
            snake.activateDoubleGrowth(powerUpDuration)
            active = false
        }
    }
    fun isActive(): Boolean {
        return active
    }
    fun getSpawnTime(): Long {
        return spawnTime
    }
}
