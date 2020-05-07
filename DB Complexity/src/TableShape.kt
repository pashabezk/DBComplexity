import javafx.geometry.Pos
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text

class TableShape (
        var tableName: String //название таблицы
    )
{
    var fontSize:Double = 16.0 //размер шрифта
        set(value) {
            field = value
            text.font = Font.font(fontSize)
        }

    var rectHeight: Double = 20.0 //высота прямоугольника
        set(value) {
            field = value
            setRectParam()
        }

    var X: Double = 0.0 //координата прямоугольника
        set(value) {
            field = value
            box.layoutX = value
        }

    var Y: Double = 0.0 //координата прямоугольника
        set(value) {
            field = value
            box.layoutY = value - fontSize
        }

    var rect = Rectangle() //прямоугольник
    var text = Text(tableName) //текст с названием таблицы
    val box = VBox(text, rect) //контейнер: текст + прямоугольник

    var clrShapeNSelected: Color = DEFAULT_CLR_SHAPE_NOT_SELECTED //цвет не выбранного прямоугольника
        set(value) {
            field = value
            rect.fill = value
        }

    var clrTextNSelected: Color = DEFAULT_CLR_TEXT_NOT_SELECTED //цвет не выбранного текста
        set(value) {
            field = value
            text.fill = value
        }

    var clrShapeSelected: Color = DEFAULT_CLR_SHAPE_SELECTED //цвет выбранного прямоугольника
    var clrTextSelected: Color = DEFAULT_CLR_TEXT_SELECTED //цвет не выбранного текста

    init
    {
        //установка параметров прямоугольника
        setRectParam()
        rect.fill = clrShapeNSelected

        //установка параметров текста
        text.fill = clrTextNSelected
        text.font = Font.font(fontSize)

        //установка парметров контейнера
        box.alignment = Pos.TOP_LEFT
        box.layoutX = X
        box.layoutY = Y - fontSize

        box.setOnMouseEntered { event -> //действия при наведении курсора
            rect.fill = clrShapeSelected
            text.fill = clrTextSelected
        }

        box.setOnMouseExited { event -> //действия при отводе курсора
            rect.fill = clrShapeNSelected
            text.fill = clrTextNSelected
        }
    }

    private fun setRectParam()
    {
        rect.width = rectHeight + rectHeight/4
        rect.height = rectHeight
        rect.arcWidth = rect.width/4
        rect.arcHeight = rect.arcWidth
    }

    companion object
    {
        @JvmStatic var DEFAULT_CLR_SHAPE_NOT_SELECTED: Color = Color.ORANGE //цвет не выбранного прямоугольника
        @JvmStatic var DEFAULT_CLR_TEXT_NOT_SELECTED: Color = Color.DARKGREEN //цвет не выбранного текста
        @JvmStatic var DEFAULT_CLR_SHAPE_SELECTED: Color = Color.web("#560074") //цвет выбранного прямоугольника (a6000e)
        @JvmStatic var DEFAULT_CLR_TEXT_SELECTED: Color = Color.DARKVIOLET //цвет не выбранного текста

        @JvmStatic var FONT_SIZE_AUTO: Boolean = true //автоматический расчёт размера шрифта
        @JvmStatic var DEFAULT_FONT_SIZE: Double = 16.0 //размер шрифта
    }
}