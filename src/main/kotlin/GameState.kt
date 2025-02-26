import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import entities.Ball
import entities.Pocket
import entities.Vector
import kotlin.random.Random

class GameState() {
    val balls = mutableListOf<Ball>()
    var shootingBall: Ball? = null
        private set
    var shootingDirection by mutableStateOf<Vector?>(null)
        private set
    var force by mutableStateOf(0f)
    var score by mutableStateOf(0)
        private set
    private val physicalEngine = PhysicalEngine(balls)

    val pockets = listOf(
        Pocket(Vector(0f, 0f), POCKET_RADIUS),
        Pocket(Vector(TABLE_WIDTH / 2f, 0f), POCKET_RADIUS),
        Pocket(Vector(TABLE_WIDTH, 0f), POCKET_RADIUS),
        Pocket(Vector(0f, TABLE_HEIGHT), POCKET_RADIUS),
        Pocket(Vector(TABLE_WIDTH / 2f, TABLE_HEIGHT), POCKET_RADIUS),
        Pocket(Vector(TABLE_WIDTH, TABLE_HEIGHT), POCKET_RADIUS)
    )

    init {
        setupBalls()
        shootingBall = balls.last()
    }

    private fun setupBalls() {
        val rows = 6
        val initialX = TABLE_WIDTH * 0.33f
        val initialY = TABLE_HEIGHT * 0.5f
        val spacing = BALL_RADIUS * 2 * 1.05f

        var ballIndex = 0
        for (row in 0 until rows) {
            for (col in 0..row) {
                if (ballIndex < BALLS_NUMBER - 1) {
                    val xOffset = initialX - (row * spacing)
                    val yOffset = initialY - (row * spacing / 2)
                    val ballX = xOffset
                    val ballY = yOffset + col * spacing

                    balls.add(
                        Ball(
                            velocity = Vector(0f, 0f),
                            x = ballX,
                            y = ballY,
                            radius = BALL_RADIUS,
                            mass = 1f,
                            color = Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()),
                        )
                    )
                    ballIndex++
                }
            }
        }

        // Add the shooting ball
        balls.add(
            Ball(
                velocity = Vector(0f, 0f),
                x = TABLE_WIDTH * 0.75f,
                y = TABLE_HEIGHT * 0.5f,
                radius = BALL_RADIUS,
                mass = 1f,
                color = Color.White
            )
        )
    }

    /*
     * Updates balls position and velocity.
     * Uses PhysicalEngine to handle collisions and apply friction.
     */
    fun update() {
        physicalEngine.update()

        // Check if any ball (not shooting ball) falls into a pocket
        val ballsToRemove = balls.filter { ball ->
            ball != shootingBall && pockets.any { pocket ->
                isBallInAPocket(ball, pocket)
            }
        }

        score += ballsToRemove.size

        balls.removeAll(ballsToRemove)

        // Check for restart conditions:
        // (1) The shooting ball falls into a pocket
        // (2) All other balls have been pocketed.
        shootingBall?.let { cueBall ->
            val cueBallInPocket = pockets.any { pocket ->
                isBallInAPocket(cueBall, pocket)
            }
            if (cueBallInPocket || balls.size == 1) {
                restartGame()
            }
        }
    }

    private fun isBallInAPocket(ball: Ball, pocket: Pocket) = (ball.position - pocket.position).length() < pocket.radius

    fun shootBall() {
        shootingBall?.let { ball ->
            shootingDirection?.let { direction ->
                ball.velocity = direction * force * 2f
                shootingDirection = null
            }
        }
    }

    fun setShootingDirection(x: Float, y: Float) {
        shootingBall?.let {
            shootingDirection = Vector(x - it.position.x, y - it.position.y).normalize()
        }
    }

    private fun restartGame() {
        balls.clear()
        score = 0
        setupBalls()
        shootingBall = balls.last()
        shootingDirection = null
    }

    companion object {
        const val BALLS_NUMBER = 16
        const val TABLE_WIDTH = 1200f
        const val TABLE_HEIGHT = 800f
        const val DELTA_TIME = 0.016f
        const val POCKET_RADIUS = 30f
        const val BALL_RADIUS = 20f
        val boardColor = Color(21, 88, 67)
    }
}