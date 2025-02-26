import entities.Ball
import entities.Vector
import kotlin.math.abs

class PhysicalEngine(private val balls: List<Ball>) {
    /*
     * Updates balls velocity and position, and applies friction.
     */
    fun update() {
        balls.forEach { ball ->
            if (ball.velocity.length() > 0) {
                ball.position += ball.velocity * GameState.DELTA_TIME
                simulateFriction(ball)

                // Check for table boundaries collision
                if (ball.position.x - ball.radius < 0 || ball.position.x + ball.radius > GameState.TABLE_WIDTH) {
                    ball.velocity = ball.velocity.copy(x = -ball.velocity.x)
                }
                if (ball.position.y - ball.radius < 0 || ball.position.y + ball.radius > GameState.TABLE_HEIGHT) {
                    ball.velocity = ball.velocity.copy(y = -ball.velocity.y)
                }
            }
        }

        checkBallsCollisions()
    }

    private fun simulateFriction(ball: Ball) {
        val newVelocity = ball.velocity * 0.995f
        if (abs((newVelocity - ball.velocity).length()) < 0.05) {
            ball.velocity = Vector(0f, 0f)
        } else {
            ball.velocity = newVelocity
        }
    }

    private fun checkBallsCollisions() {
        for (i in 0 until balls.size) {
            for (j in i + 1 until balls.size) {
                val ball1 = balls[i]
                val ball2 = balls[j]
                val distance = (ball1.position - ball2.position).length()

                if (distance < ball1.radius + ball2.radius) {
                    resolveCollision(ball1, ball2)
                    val overlap = ball1.radius + ball2.radius - distance
                    val separation = (ball2.position - ball1.position).normalize() * overlap / 2f
                    ball1.position -= separation
                    ball2.position += separation
                }
            }
        }
    }

    private fun resolveCollision(ball1: Ball, ball2: Ball) {
        val m1 = ball1.mass
        val m2 = ball2.mass
        val p1 = ball1.position
        val p2 = ball2.position
        val v1 = ball1.velocity
        val v2 = ball2.velocity

        val diffPos = p1 - p2
        val distanceSquared = diffPos.dot(diffPos)
        if (distanceSquared == 0f) return

        val relativeVelocity = v1 - v2
        val dotProduct = relativeVelocity.dot(diffPos)

        val factor1 = (2 * m2 / (m1 + m2)) * (dotProduct / distanceSquared)
        val factor2 = (2 * m1 / (m1 + m2)) * (dotProduct / distanceSquared)

        ball1.velocity = v1 - diffPos * factor1
        ball2.velocity = v2 + diffPos * factor2
    }
}
