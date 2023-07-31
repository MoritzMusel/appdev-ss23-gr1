package com.example.supersnake

object StateHandler {
    lateinit var stateGame: GameState

    fun setState(gameState: GameState){
        this.stateGame = gameState

    }

    fun getState(): GameState {
        return this.stateGame
    }
}