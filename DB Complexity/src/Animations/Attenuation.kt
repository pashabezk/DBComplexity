package Animations

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.scene.Node
import javafx.util.Duration

class Attenuation (var node: Node) //эффект затухания
{
    fun playAnim() {
        Timeline(
            KeyFrame(Duration.ZERO, KeyValue(node.opacityProperty(), 1.0)),
            KeyFrame(Duration.seconds(4.0), KeyValue(node.opacityProperty(), 0.0))
        ).play()
    }

}