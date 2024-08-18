package example.com.plugins

import example.com.game.domain.TicTacToeGame
import example.com.game.routes.ticTacToeGameRoute
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(game: TicTacToeGame) {
    routing {
        ticTacToeGameRoute(game)
    }
}
