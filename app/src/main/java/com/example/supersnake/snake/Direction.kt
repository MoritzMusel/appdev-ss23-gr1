package com.example.supersnake.snake

enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    fun isOpposite(newDirection: Direction): Boolean {
        return when (this) {
            UP -> newDirection == DOWN
            DOWN -> newDirection == UP
            LEFT -> newDirection == RIGHT
            RIGHT -> newDirection == LEFT
        }
    }
}
