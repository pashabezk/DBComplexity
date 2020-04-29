import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.control.ColorPicker
import javafx.scene.control.ComboBox
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text

class SettingsController
{
    @FXML private lateinit var fxClrShapeNSelected: ColorPicker //цвет невыбранной формы
    @FXML private lateinit var fxClrTextNSelected: ColorPicker //цвет текста невыбранной фигуры
    @FXML private lateinit var fxClrShapeSelected: ColorPicker //цвет выбранной формы
    @FXML private lateinit var fxClrTextSelected: ColorPicker //цвет текста выбранной фигуры

    @FXML private lateinit var fxRadio: ToggleGroup //группа радио-кнопок для настройки шрифта
    @FXML private lateinit var fxFontSize: ComboBox<Int> //выпадающий список с размерами шрифта

    @FXML private lateinit var fxPane1: AnchorPane //панель, на которую будет помещён пример невыбранной фигуры
    @FXML private lateinit var fxPane2: AnchorPane //панель, на которую будет помещён пример выбранной фигуры

    fun initialize()
    {
        var rectHeight= fxPane1.width / 2 //высота прямоугольника
        var rectWidth = rectHeight + rectHeight/4 //ширина прямоугольника
        var rectArc = rectWidth / 4 //закругление краёв прямоугольника

        var rect = javafx.scene.shape.Rectangle() //создание прямоугольника
        rect.width = rectWidth
        rect.height = rectHeight
        rect.arcWidth = rectArc
        rect.arcHeight = rectArc
        rect.fill = Color.ORANGE

        val text = Text("Table name") //создание названия для прямоугольника
        text.fill = fxClrShapeNSelected.value
        text.font = Font.font(16.0)

        val box = VBox(text, rect) //создание контейнера: текст - прямоугольник
        box.alignment = Pos.TOP_LEFT
        box.layoutX = 0.0
        box.layoutY = 0.0

//        box.setOnMouseEntered { event -> //действия при наведении курсора
//            rect.fill = clrTblSelected
//            text.fill = clrTblNameSelected
//        }
//
//        box.setOnMouseExited { event -> //действия при отводе курсора
//            rect.fill = clrTblNSelected
//            text.fill = clrTblNameNSelected
//        }

        fxPane1.children.add(box) //добавление контейнера на панель
    }
}