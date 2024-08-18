package example.com.game.models

import kotlinx.serialization.Serializable

@Serializable
data class PlayerTurn(val x : Int , val y : Int)
