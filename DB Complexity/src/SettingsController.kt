import javafx.collections.FXCollections.observableArrayList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.ColorPicker
import javafx.scene.control.ComboBox
import javafx.scene.control.RadioButton
import javafx.scene.input.MouseEvent
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

    private var example1: TableShape = TableShape("Table name") //образец формы
    private var example2: TableShape = TableShape("Table name") //образец формы при наведении

    private fun setPropertiesFromConfig()
    {
        //установка цветов, полученных из файла конфигурации в color picker'ы
        fxClrShapeNSelected.value = CONFIG.CLR_SHAPE_NOT_SELECTED
        fxClrTextNSelected.value = CONFIG.CLR_TEXT_NOT_SELECTED
        fxClrShapeSelected.value = CONFIG.CLR_SHAPE_SELECTED
        fxClrTextSelected.value = CONFIG.CLR_TEXT_SELECTED

        if (CONFIG.FONT_SIZE_AUTO) //если включена настройка автоматического размера шрифта
        {
            fxRadioAuto.isSelected = true //установка пункта "Автоматический"
            fxFontSize.isDisable = true //сделать combo box недействительным
        }
        else fxRadioCustom.isSelected = true //установка пункта "Фиксированный"
    }

    fun initialize()
    {
        CONFIG.getProperties() //загрузка конфигурации интерфейса из файла

        setPropertiesFromConfig()

        //установка параметров настройки шрифта из файла конфигурации
        var list = ArrayList<Int>()
        list.add(CONFIG.FONT_SIZE.toInt()) //текущий размер шрифта помещается первым в список
        for (i in 10 until 27 step 2)
            list.add(i) //заполнение списка размеров шрифтов
        fxFontSize.items = observableArrayList(list) //установка списка
        fxFontSize.selectionModel.selectFirst() //выбор первого элемента списка

        //настройка параметров образца формы
        example1.rectHeight = 108.0
        example1.X = 5.0
        example1.Y = 5.0

        //настройка параметров образца формы при наведении
        example2.rectHeight = 108.0
        example2.X = 5.0
        example2.Y = 150.0
        example2.clrShapeNSelected = TableShape.CLR_SHAPE_SELECTED
        example2.clrTextNSelected = TableShape.CLR_TEXT_SELECTED

        fxDrawPane.children.addAll(example1.box, example2.box) //добавление контейнеров на панель

        //установка прослушивателей на изменение цветов
        fxClrShapeNSelected.valueProperty().addListener { observable, oldValue, newValue -> //изменение цвета не выбранной формы
            CONFIG.CLR_SHAPE_NOT_SELECTED = newValue
            example1.clrShapeNSelected = newValue
        }
        fxClrTextNSelected.valueProperty().addListener { observable, oldValue, newValue -> //изменение цвета текста не выбранной формы
            CONFIG.CLR_TEXT_NOT_SELECTED = newValue
            example1.clrTextNSelected = newValue
        }
        fxClrShapeSelected.valueProperty().addListener { observable, oldValue, newValue -> //изменение цвета выбранной формы
            CONFIG.CLR_SHAPE_SELECTED = newValue
            example1.clrShapeSelected = newValue
            example2.clrShapeSelected = newValue
            example2.clrShapeNSelected = newValue
        }
        fxClrTextSelected.valueProperty().addListener { observable, oldValue, newValue -> //изменение цвета текста выбранной формы
            CONFIG.CLR_TEXT_SELECTED = newValue
            example1.clrTextSelected = newValue
            example2.clrTextSelected = newValue
            example2.clrTextNSelected = newValue
        }

        //установка прослушивателей на изменение параметров настройки шрифта
        fxRadioAuto.onAction = EventHandler { //при выборе "автоматически"
            CONFIG.FONT_SIZE_AUTO = true
            fxFontSize.isDisable = true //сделать combo box недействительным

            example1.fontSize = CONFIG.DEFAULT_FONT_SIZE
            example2.fontSize = CONFIG.DEFAULT_FONT_SIZE
        }
        fxRadioCustom.onAction = EventHandler { //при выборе пользовательской настройки
            CONFIG.FONT_SIZE_AUTO = false
            fxFontSize.isDisable = false //сделать combo box действительным

            example1.fontSize = fxFontSize.value.toDouble()
            example2.fontSize = fxFontSize.value.toDouble()
        }
        fxFontSize.valueProperty().addListener { observable, oldValue, newValue -> //при установке размера шрифта
            CONFIG.FONT_SIZE = fxFontSize.value.toDouble()
            example1.fontSize = newValue.toDouble()
            example2.fontSize = newValue.toDouble()
        }
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

    @FXML fun handleLableDefaultClicked(event: MouseEvent)
    {
        CONFIG.setDefault() //установка значений по умолчанию
        setPropertiesFromConfig()
    }
}