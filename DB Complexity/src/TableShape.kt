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
    var fontSize:Double = FONT_SIZE //размер шрифта
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

    var clrShapeNSelected: Color = CLR_SHAPE_NOT_SELECTED //цвет не выбранного прямоугольника
        set(value) {
            field = value
            rect.fill = value
        }

    var clrTextNSelected: Color = CLR_TEXT_NOT_SELECTED //цвет не выбранного текста
        set(value) {
            field = value
            text.fill = value
        }

    var clrShapeSelected: Color = CLR_SHAPE_SELECTED //цвет выбранного прямоугольника
    var clrTextSelected: Color = CLR_TEXT_SELECTED //цвет не выбранного текста

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
        @JvmStatic var CLR_SHAPE_NOT_SELECTED: Color = CONFIG.DEFAULT_CLR_SHAPE_NOT_SELECTED //цвет не выбранного прямоугольника
        @JvmStatic var CLR_TEXT_NOT_SELECTED: Color = CONFIG.DEFAULT_CLR_TEXT_NOT_SELECTED //цвет не выбранного текста
        @JvmStatic var CLR_SHAPE_SELECTED: Color = CONFIG.DEFAULT_CLR_SHAPE_SELECTED //цвет выбранного прямоугольника
        @JvmStatic var CLR_TEXT_SELECTED: Color = CONFIG.DEFAULT_CLR_TEXT_SELECTED //цвет не выбранного текста

        @JvmStatic var FONT_SIZE_AUTO: Boolean = CONFIG.DEFAULT_FONT_SIZE_AUTO //автоматический расчёт размера шрифта
        @JvmStatic var FONT_SIZE: Double = CONFIG.DEFAULT_FONT_SIZE //размер шрифта

        @JvmStatic
        fun setPropertiesFromConfig()
        {
            CLR_SHAPE_NOT_SELECTED = CONFIG.CLR_SHAPE_NOT_SELECTED //цвет не выбранного прямоугольника
            CLR_TEXT_NOT_SELECTED = CONFIG.CLR_TEXT_NOT_SELECTED //цвет не выбранного текста
            CLR_SHAPE_SELECTED = CONFIG.CLR_SHAPE_SELECTED //цвет выбранного прямоугольника
            CLR_TEXT_SELECTED = CONFIG.CLR_TEXT_SELECTED //цвет не выбранного текста

            FONT_SIZE_AUTO = CONFIG.FONT_SIZE_AUTO
            FONT_SIZE = CONFIG.FONT_SIZE
        }
    }
}