package com.example.supersnake

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView

class SnakeThreadMultiplayer(private val surfaceHolder: SurfaceHolder, private val surfaceView: SurfaceView) : Thread() {
    private var running = false


    @SuppressLint("ResourceAsColor")
    private val paintBackground = Paint().apply {
        color = R.color.BG_COLOUR;
    }

    @SuppressLint("ResourceAsColor")
    private val paintSnake1 = Paint().apply {
        color = R.color.SNAKE_COLOUR;
    }

    @SuppressLint("ResourceAsColor")
    private val paintSnake2 = Paint().apply {
        color = R.color.SNAKE_COLOUR2;
    }
    @SuppressLint("ResourceAsColor")
    private val foodColor = Paint().apply {
        color = R.color.FOOD_COLOUR;
    }

/*
    @SuppressLint("ResourceAsColor")
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
            onDraw()
        }
    }
    */


    fun onDraw() {
        val canvas: Canvas? = surfaceHolder.lockCanvas()
        canvas?.let{
            it.drawRect(0f, 0f,it.width.toFloat(),it.height.toFloat(), paintBackground)
            val state = StateHandler.getState()
            val food = state.food
            val gridSizeWidth = it.width / state.gridsize
            val gridSizeHeight = it.height /state.gridsize
            //drawFood
            it.drawRect(food.x * gridSizeWidth.toFloat(), food.y * gridSizeHeight.toFloat() ,gridSizeWidth.toFloat(),gridSizeHeight.toFloat(), foodColor)

            //drawPlayer 1
            paintPlayer(canvas, state.players[0], gridSizeWidth.toFloat(),gridSizeHeight.toFloat(), paintSnake1)
            //drawPlayer 2
            paintPlayer(canvas, state.players[1], gridSizeWidth.toFloat(),gridSizeHeight.toFloat(), paintSnake1)
            // Unlock the canvas and post the changes
            surfaceHolder.unlockCanvasAndPost(it)

        }

    }

    private fun paintPlayer(canvas: Canvas, playerState: Player, width: Float, height: Float, color: Paint) {
        val snake = playerState.snake
        for (cell in snake) {
            canvas?.drawRect(cell.x * width, cell.y * height, width, height, color)

        }
    }
}