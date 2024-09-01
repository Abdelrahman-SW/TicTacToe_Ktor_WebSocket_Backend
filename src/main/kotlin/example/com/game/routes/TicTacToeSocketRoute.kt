package example.com.game.routes

import example.com.game.domain.TicTacToeGame
import example.com.game.models.Player
import example.com.game.models.PlayerTurn
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json

fun Route.ticTacToeGameRoute(game: TicTacToeGame) {
    webSocket("/play") {
        val username = call.parameters["username"] ?: "Guest"
        val player = Player(username = username)
        val successfullyConnected = game.connectPlayer(player , this)
        if (!successfullyConnected) {
            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT , "Room IS Full"))
        }
        try {
            incoming.consumeEach { frame ->
                 if (frame is Frame.Text) {
                     val playerTurn = Json.decodeFromString<PlayerTurn>(frame.readText())
                     game.makeTurn(player , playerTurn)
                 }
            }
        }
        catch (e : Exception) {
           e.printStackTrace()
        }
        finally {
            game.disconnectPlayer(player)
            game.resetGame()
        }
    }
}