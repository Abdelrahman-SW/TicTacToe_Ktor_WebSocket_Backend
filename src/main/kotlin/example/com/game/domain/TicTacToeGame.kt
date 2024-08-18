package example.com.game.domain

import example.com.game.models.GameState
import example.com.game.models.Player
import example.com.game.models.PlayerTurn
import example.com.game.models.PlayerType
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class TicTacToeGame {

    private val state = MutableStateFlow(GameState())
    private val playerSockets = ConcurrentHashMap<Player, WebSocketSession>()
    private val gameScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        state.onEach(::broadcastGameState).launchIn(gameScope)
    }

    fun connectPlayer(player: Player, session: WebSocketSession): Boolean {
        if (state.value.connectedPlayers.size >= 2) {
            return false
        }
        // assign player Type :
        val isPlayerXExits = state.value.connectedPlayers.any { it.type == PlayerType.X }
        player.type = if (isPlayerXExits) PlayerType.O else PlayerType.X

        playerSockets[player] = session
        state.update {
            val connectedPlayers = it.connectedPlayers + player
            val playerAtTurn = if (connectedPlayers.size == 2) connectedPlayers.random() else null
            it.copy(connectedPlayers = connectedPlayers, playerAtTurn = playerAtTurn)
        }
        return true
    }

    fun disconnectPlayer(player: Player) {
        playerSockets.remove(player)
        state.update {
            it.copy(connectedPlayers = it.connectedPlayers - player)
        }
    }

    suspend fun broadcastGameState(gameState: GameState) {
        playerSockets.values.forEach { socketSession ->
            socketSession.send(Json.encodeToString(gameState))
        }
    }

    fun makeTurn(player: Player, turn: PlayerTurn) {
        if (state.value.board[turn.y][turn.x] != null || state.value.playerAtTurn != player || state.value.winingPlayer != null || player.type == null) {
            return
        }
        state.update { currentGameState ->
            val newBoard = currentGameState.board.also { board ->
                board[turn.y][turn.x] = player.type!!.char
            }
            val isBoardFull = currentGameState.board.all { column -> column.all { row -> row!=null } }
            val winingPlayer = getWinningPlayer()
            val currentPlayerIndex = currentGameState.connectedPlayers.indexOf(currentGameState.playerAtTurn!!)
            val nextPlayerIndex = if (currentPlayerIndex == 0) 1 else 0
            val playerAtTurn = currentGameState.connectedPlayers[nextPlayerIndex]
            currentGameState.copy(
                playerAtTurn = playerAtTurn,
                board = newBoard,
                winingPlayer = winingPlayer,
                isBoardFull = isBoardFull
            )
        }
    }

    private fun getWinningPlayer(): Player? {
        val board = state.value.board
        val winningChar =  if (board[0][0] != null && board[0][0] == board[0][1] && board[0][1] == board[0][2]) {
            board[0][0]
        } else if (board[1][0] != null && board[1][0] == board[1][1] && board[1][1] == board[1][2]) {
            board[1][0]
        } else if (board[2][0] != null && board[2][0] == board[2][1] && board[2][1] == board[2][2]) {
            board[2][0]
        } else if (board[0][0] != null && board[0][0] == board[1][0] && board[1][0] == board[2][0]) {
            board[0][0]
        } else if (board[0][1] != null && board[0][1] == board[1][1] && board[1][1] == board[2][1]) {
            board[0][1]
        } else if (board[0][2] != null && board[0][2] == board[1][2] && board[1][2] == board[2][2]) {
            board[0][2]
        } else if (board[0][0] != null && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            board[0][0]
        } else if (board[0][2] != null && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            board[0][2]
        } else null
        if (winningChar == null) return null
        return if (winningChar == PlayerType.X.char) state.value.connectedPlayers.find { it.type == PlayerType.X } else state.value.connectedPlayers.find { it.type == PlayerType.O}
    }
}