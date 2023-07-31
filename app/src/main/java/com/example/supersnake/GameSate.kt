package com.example.supersnake

import io.socket.client.Socket

data class GameState(
    val players: List<Player>,
    val food: Food,
    val gridsize: Int
)

data class Player(
    val pos: Position,
    val vel: Velocity,
    val snake: List<Position>,
    val points: Int,
    val playerOneName: String,
)

data class Position(
    val x: Int,
    val y: Int
)

data class Velocity(
    val x: Int,
    val y: Int
)

data class Food(
    val x: Int,
    val y: Int
)
