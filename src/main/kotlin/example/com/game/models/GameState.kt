package example.com.game.models

import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    val playerAtTurn : Player? = null,
    val board : Array<Array<Char?>> = emptyBoard(),
    val winingPlayer: Player? = null,
    val isBoardFull : Boolean = false,
    val connectedPlayers : List<Player> = emptyList(),
    val countdown : Int = 0
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
        if (other == null || this::class != other::class) return false

        other as GameState

        if (playerAtTurn != other.playerAtTurn) return false
        if (!board.contentDeepEquals(other.board)) return false
        if (winingPlayer != other.winingPlayer) return false
        if (isBoardFull != other.isBoardFull) return false
        if (connectedPlayers != other.connectedPlayers) return false
        if (countdown != other.countdown) return false

        return true
    }

    override fun hashCode(): Int {
        var result = playerAtTurn?.hashCode() ?: 0
        result = 31 * result + board.contentDeepHashCode()
        result = 31 * result + (winingPlayer?.hashCode() ?: 0)
        result = 31 * result + isBoardFull.hashCode()
        result = 31 * result + connectedPlayers.hashCode()
        result = 31 * result + countdown
        return result
    }
}

