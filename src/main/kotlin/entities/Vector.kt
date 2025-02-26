package entities

import kotlin.math.sqrt

data class Vector(var x: Float, var y: Float) {
    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)
    operator fun minus(other: Vector) = Vector(x - other.x, y - other.y)
    operator fun times(scalar: Float) = Vector(x * scalar, y * scalar)
    operator fun div(scalar: Float) = Vector(x / scalar, y / scalar)

    fun dot(other: Vector) = x * other.x + y * other.y
    fun length() = sqrt((x * x + y * y).toDouble()).toFloat()
    fun normalize() = this / length()
}
