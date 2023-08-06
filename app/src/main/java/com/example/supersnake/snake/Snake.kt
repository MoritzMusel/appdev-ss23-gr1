package com.example.supersnake.snake

import android.graphics.Canvas
import android.graphics.Paint

class Snake {
    internal val bodyParts = mutableListOf<SnakeBodyPart>()
    private var direction = Direction.RIGHT
    private var doubleGrowthActive = false
    private var doubleGrowthEndTime = 0L

    private val paint = Paint().apply {
        color = android.graphics.Color.parseColor("#69FF93")
        style = Paint.Style.FILL
    }

    init {
        // Initialize the snake with a few body parts
        for (i in 0 until INITIAL_LENGTH) {
            bodyParts.add(SnakeBodyPart(START_X - i * CELL_SIZE, START_Y))
        }
    }

    fun move() {
        val head = bodyParts.first()
        val newHead = when (direction) {
            Direction.UP -> SnakeBodyPart(head.x, head.y - CELL_SIZE)
            Direction.DOWN -> SnakeBodyPart(head.x, head.y + CELL_SIZE)
            Direction.LEFT -> SnakeBodyPart(head.x - CELL_SIZE, head.y)
            Direction.RIGHT -> SnakeBodyPart(head.x + CELL_SIZE, head.y)
        }
        bodyParts.add(0, newHead)
        bodyParts.removeAt(bodyParts.size - 1)
    }

    fun draw(canvas: Canvas) {
        for (bodyPart in bodyParts) {
            canvas.drawRect(bodyPart.x.toFloat(), bodyPart.y.toFloat(),
                (bodyPart.x + CELL_SIZE).toFloat(), (bodyPart.y + CELL_SIZE).toFloat(), paint)
        }
    }

    fun grow() {
        // Double the growth if the power-up is active
        val growth = if (doubleGrowthActive) 2 else 1
        for (i in 0 until growth) {
            val tail = bodyParts.last()
            val newTail = when (direction) {
                Direction.UP -> SnakeBodyPart(tail.x, tail.y + CELL_SIZE)
                Direction.DOWN -> SnakeBodyPart(tail.x, tail.y - CELL_SIZE)
                Direction.LEFT -> SnakeBodyPart(tail.x + CELL_SIZE, tail.y)
                Direction.RIGHT -> SnakeBodyPart(tail.x - CELL_SIZE, tail.y)
            }
            bodyParts.add(newTail)
        }
    }

    fun setDirection(newDirection: Direction) {
        if (direction.isOpposite(newDirection)) return
        direction = newDirection
    }

    fun getHead(): SnakeBodyPart = bodyParts.first()

    fun checkCollision(): Boolean {
        val head = getHead()
        return bodyParts.any { it != head && it.x == head.x && it.y == head.y }
    }

    fun isDoubleGrowthActive(): Boolean {
        return doubleGrowthActive
    }

    fun getDoubleGrowthEndTime(): Long {
        return doubleGrowthEndTime
    }

    fun deactivateDoubleGrowth() {
        doubleGrowthActive = false
    }

    fun activateDoubleGrowth(duration: Int) {
        doubleGrowthActive = true
        doubleGrowthEndTime = System.currentTimeMillis() + duration
    }

    companion object {
        const val CELL_SIZE = 40 // Adjust the size of the cells as per your requirement
        const val START_X = CELL_SIZE // Starting position of the snake
        const val START_Y = CELL_SIZE
        private const val INITIAL_LENGTH = 4
    }
}
