package entities

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

data class Ball(
    var velocity: Vector,
    var x: Float,
    var y: Float,
    val radius: Float,
    val mass: Float,
    val color: Color
) {
    var position by mutableStateOf(Vector(x, y))
}

