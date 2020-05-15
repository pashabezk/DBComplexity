package Animations

import javafx.animation.TranslateTransition
import javafx.scene.Node
import javafx.util.Duration

class Shake (node: Node?) //эффект встряхивания
{
    private val tt: TranslateTransition

    init {
        tt = TranslateTransition(Duration.millis(100.0), node)
        tt.fromX = 0.0
        tt.byX = -7.0
        tt.cycleCount = 3
        tt.isAutoReverse = true
    }

    fun playAnim()
    {
        tt.playFromStart() //запуск анимации
    }
}