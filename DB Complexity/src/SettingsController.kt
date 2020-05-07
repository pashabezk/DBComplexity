import javafx.collections.FXCollections.observableArrayList
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.ColorPicker
import javafx.scene.control.ComboBox
import javafx.scene.control.RadioButton
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

class SettingsController
{
    @FXML private lateinit var fxClrShapeNSelected: ColorPicker //цвет невыбранной формы
    @FXML private lateinit var fxClrTextNSelected: ColorPicker //цвет текста невыбранной фигуры
    @FXML private lateinit var fxClrShapeSelected: ColorPicker //цвет выбранной формы
    @FXML private lateinit var fxClrTextSelected: ColorPicker //цвет текста выбранной фигуры

    @FXML private lateinit var fxRadioAuto: RadioButton //группа радио-кнопок для настройки шрифта (автоматический)
    @FXML private lateinit var fxRadioCustom: RadioButton //группа радио-кнопок для настройки шрифта (заданный пользователем)

    @FXML private lateinit var fxFontSize: ComboBox<Int> //выпадающий список с размерами шрифта

    @FXML private lateinit var fxDrawPane: AnchorPane //панель, на которую будут помещёны примеры фигур

    fun initialize()
    {
        CONFIG.getProperties() //загрузка конфигурации интерфейса из файла

        fxClrShapeNSelected.value = CONFIG.CLR_SHAPE_NOT_SELECTED
        fxClrTextNSelected.value = CONFIG.CLR_TEXT_NOT_SELECTED
        fxClrShapeSelected.value = CONFIG.CLR_SHAPE_SELECTED
        fxClrTextSelected.value = CONFIG.CLR_TEXT_SELECTED

        var list = ArrayList<Int>()
        list.add(CONFIG.FONT_SIZE.toInt())
        for (i in 10 until 27 step 2)
            list.add(i)
        fxFontSize.items = observableArrayList(list)
        fxFontSize.selectionModel.selectFirst()

        if (CONFIG.FONT_SIZE_AUTO)
        {
            fxRadioAuto.isSelected = true
            fxFontSize.isDisable = true
        }
        else fxRadioCustom.isSelected = true

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

    @FXML fun handleButtonCancel(event: ActionEvent)
    {
        CONFIG.getProperties() //загрузка предыдущей конфигурации интерфейса из файла
        ((event.source as Node).scene.window as Stage).close() //закрыть текущее окно
    }

    @FXML fun handleButtonSave(event: ActionEvent)
    {
        CONFIG.saveProperties() //сохранение конфигурации
        ((event.source as Node).scene.window as Stage).close() //закрыть текущее окно
    }
}