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

    @FXML private lateinit var fxDrawPane: AnchorPane //панель, на которую будут помещёны примеры фигур

    fun initialize()
    {
        var example1: TableShape = TableShape("Table name")
        example1.rectHeight = 108.0
        example1.X = 5.0
        example1.Y = 5.0

        var example2: TableShape = TableShape("Table name")
        example2.rectHeight = 108.0
        example2.X = 5.0
        example2.Y = 150.0
        example2.clrShapeNSelected = TableShape.DEFAULT_CLR_SHAPE_SELECTED
        example2.clrTextNSelected = TableShape.DEFAULT_CLR_TEXT_SELECTED

        fxDrawPane.children.addAll(example1.box, example2.box) //добавление контейнера на панель
    }
}