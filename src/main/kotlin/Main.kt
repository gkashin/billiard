import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay

@Composable
fun billiardGameView(gameState: GameState) {
    var isShooting by remember { mutableStateOf(false) }
    val delayFor60FPS = (GameState.DELTA_TIME * 1000).toLong()

    // Game loop
    LaunchedEffect(Unit) {
        while (true) {
            gameState.update()
            delay(delayFor60FPS)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            gameState.setShootingDirection(offset.x, offset.y)
                        },
                        onDrag = { change, _ ->
                            gameState.setShootingDirection(change.position.x, change.position.y)
                        }
                    )
                }
        ) {
            // Draw table background
            drawRect(
                color = GameState.boardColor,
                size = size.copy(width = GameState.TABLE_WIDTH, height = GameState.TABLE_HEIGHT)
            )
            // Draw table border
            drawRect(
                color = Color.Black,
                size = size.copy(width = GameState.TABLE_WIDTH, height = GameState.TABLE_HEIGHT),
                topLeft = Offset(0f, 0f),
                style = Stroke(width = 10f)
            )
            // Draw pockets
            gameState.pockets.forEach { pocket ->
                drawCircle(
                    color = Color.Black,
                    radius = pocket.radius,
                    center = Offset(pocket.position.x, pocket.position.y)
                )
            }
            // Draw the shooting ball's direction as a dashed line
            gameState.shootingBall?.let { cueBall ->
                gameState.shootingDirection?.let { direction ->
                    val start = Offset(cueBall.position.x, cueBall.position.y)
                    val lineLength = GameState.TABLE_WIDTH
                    val end = Offset(
                        cueBall.position.x + direction.x * lineLength,
                        cueBall.position.y + direction.y * lineLength
                    )
                    drawLine(
                        color = Color.White,
                        start = start,
                        end = end,
                        strokeWidth = 5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }
            }
            // Draw balls
            gameState.balls.forEach { ball ->
                drawCircle(
                    color = ball.color,
                    radius = ball.radius,
                    center = Offset(ball.position.x, ball.position.y)
                )
            }
        }

        // Display the score on the right
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Score: ${gameState.score}", style = MaterialTheme.typography.h4, color = Color.Black)
        }

        // Force slider and shoot button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Slider(
                value = gameState.force,
                onValueChange = { gameState.force = it },
                valueRange = 0f..500f,
                modifier = Modifier.padding(16.dp)
            )
            Button(onClick = {
                if (!isShooting) {
                    isShooting = true
                    gameState.shootBall()
                    isShooting = false
                }
            }) {
                Text("Shoot")
            }
        }
    }
}

fun main() = application {
    val gameState = GameState()

    Window(onCloseRequest = ::exitApplication, title = "Billiard Game") {
        billiardGameView(gameState)
    }
}
