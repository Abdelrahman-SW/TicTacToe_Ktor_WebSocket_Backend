package example.com.game.models

import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    val playerAtTurn : Player? = null,
    val board : Array<Array<Char?>> = emptyBoard(),
    val winingPlayer: Player? = null,
    val isBoardFull : Boolean = false,
    val connectedPlayers : List<Player> = emptyList()
) {
    companion object {
        fun emptyBoard() : Array<Array<Char?>>  {
            return arrayOf(
                arrayOf(null , null , null),
                arrayOf(null , null , null),
                arrayOf(null , null , null),
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameState

        if (playerAtTurn != other.playerAtTurn) return false
        if (!board.contentDeepEquals(other.board)) return false
        if (winingPlayer != other.winingPlayer) return false
        if (isBoardFull != other.isBoardFull) return false
        if (connectedPlayers != other.connectedPlayers) return false

        return true
    }

    override fun hashCode(): Int {
        var result = playerAtTurn?.hashCode() ?: 0
        result = 31 * result + board.contentDeepHashCode()
        result = 31 * result + (winingPlayer?.hashCode() ?: 0)
        result = 31 * result + isBoardFull.hashCode()
        result = 31 * result + connectedPlayers.hashCode()
        return result
    }
}

