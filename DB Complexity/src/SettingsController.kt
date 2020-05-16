import Configs.WConfig
import javafx.collections.FXCollections.observableArrayList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.ArrayList

class SettingsController
{
    /*переменные, задействованные на вкладке с настройкой интерфейса*/
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

    /*переменные, задействованные на вкладке с настройкой весовых коэффициентов*/

    //сохранённые весовые коэффициенты
    @FXML private lateinit var fxW1: TextField
    @FXML private lateinit var fxW2: TextField
    @FXML private lateinit var fxW3: TextField
    @FXML private lateinit var fxW4: TextField
    @FXML private lateinit var fxW5: TextField

    //параметры учёта индексов
    @FXML private lateinit var fxIndU: CheckBox //уникальные индексы
    @FXML private lateinit var fxIndNU: CheckBox //неуникальные индексы

    //параметры учёта ограничений полей
    @FXML private lateinit var fxPrAuto_inc: CheckBox //auto_increment
    @FXML private lateinit var fxPrUnique: CheckBox //unique
    @FXML private lateinit var fxPrNot_null: CheckBox //not_null
    @FXML private lateinit var fxPrDefault: CheckBox //default

    private fun setInterfacePropertiesFromConfig()
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
            example1.fontSize = CONFIG.DEFAULT_FONT_SIZE //установка размера шрифта по умолчанию
            example2.fontSize = CONFIG.DEFAULT_FONT_SIZE //установка размера шрифта по умолчанию
        }
        else fxRadioCustom.isSelected = true //установка пункта "Фиксированный"
    }

    private fun setWeightParametersFromConfig() //установка параметров весовых коэффициентов из файла конфигурации
    {
        //форматирование вывода переменной типа double
        val dfs = DecimalFormatSymbols(Locale.getDefault())
        dfs.setDecimalSeparator('.')
        val DFormat = DecimalFormat("##0.000", dfs)
        DFormat.minimumFractionDigits = 0
        DFormat.isGroupingUsed = false

        //установка весовых коэффициентов
        fxW1.text = DFormat.format(WConfig.WMetrics[0]).toString()
        fxW2.text = DFormat.format(WConfig.WMetrics[1]).toString()
        fxW3.text = DFormat.format(WConfig.WMetrics[2]).toString()
        fxW4.text = DFormat.format(WConfig.WMetrics[3]).toString()
        fxW5.text = DFormat.format(WConfig.WMetrics[4]).toString()

        //установка параметров учёта индексов
        fxIndU.isSelected = WConfig.IndexProperties[0]
        fxIndNU.isSelected = WConfig.IndexProperties[1]

        //установка параметров учёта ограничений полей
        fxPrAuto_inc.isSelected = WConfig.ColumnProperties[0]
        fxPrUnique.isSelected = WConfig.ColumnProperties[1]
        fxPrNot_null.isSelected = WConfig.ColumnProperties[2]
        fxPrDefault.isSelected = WConfig.ColumnProperties[3]
    }

    fun initialize()
    {
        /*подготовка вкладки с настройкой интерфейса*/

        CONFIG.getProperties() //загрузка конфигурации интерфейса из файла

        setInterfacePropertiesFromConfig()

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
        fxRadioAuto.onAction = EventHandler { //при выборе "автоматический"
            CONFIG.FONT_SIZE_AUTO = true
            fxFontSize.isDisable = true //сделать combo box недействительным

            //изменение размера шрифта на образцах
            example1.fontSize = CONFIG.DEFAULT_FONT_SIZE
            example2.fontSize = CONFIG.DEFAULT_FONT_SIZE
        }
        fxRadioCustom.onAction = EventHandler { //при выборе "фиксированный"
            CONFIG.FONT_SIZE_AUTO = false
            fxFontSize.isDisable = false //сделать combo box действительным

            //изменение размера шрифта на образцах
            example1.fontSize = fxFontSize.value.toDouble()
            example2.fontSize = fxFontSize.value.toDouble()
        }
        fxFontSize.valueProperty().addListener { observable, oldValue, newValue -> //при установке размера шрифта
            CONFIG.FONT_SIZE = fxFontSize.value.toDouble()
            example1.fontSize = newValue.toDouble()
            example2.fontSize = newValue.toDouble()
        }


        /*подготовка вкладки с настройкой весовых коэффициентов*/
        WConfig.getProperties() //загрузка текущей конфигурации весовых коэффициентов из файла
        setWeightParametersFromConfig() //установка весовых коэффициентов
    }

    @FXML fun handleButtonCancel(event: ActionEvent)
    {
        CONFIG.getProperties() //загрузка предыдущей конфигурации интерфейса из файла
        ((event.source as Node).scene.window as Stage).close() //закрыть текущее окно
    }

    @FXML fun handleButtonSave(event: ActionEvent)
    {
        CONFIG.saveProperties() //сохранение конфигурации интерфейса

        //сохранение результатов настройки весовых коэффициентов
        WConfig.WMetrics[0] = fxW1.text.toDouble()
        WConfig.WMetrics[1] = fxW2.text.toDouble()
        WConfig.WMetrics[2] = fxW3.text.toDouble()
        WConfig.WMetrics[3] = fxW4.text.toDouble()
        WConfig.WMetrics[4] = fxW5.text.toDouble()
        WConfig.IndexProperties[0] = fxIndU.isSelected
        WConfig.IndexProperties[1] = fxIndNU.isSelected
        WConfig.ColumnProperties[0] = fxPrAuto_inc.isSelected
        WConfig.ColumnProperties[1] = fxPrUnique.isSelected
        WConfig.ColumnProperties[2] = fxPrNot_null.isSelected
        WConfig.ColumnProperties[3] = fxPrDefault.isSelected
        WConfig.saveProperties()

        ((event.source as Node).scene.window as Stage).close() //закрыть текущее окно
    }

    @FXML fun handleLableDefaultInterfaceClicked(event: MouseEvent) //установка параметров интерфейса в значения по умолчанию
    {
        CONFIG.setDefault() //установка значений по умолчанию
        setInterfacePropertiesFromConfig()
    }

    @FXML fun handleLableTablePairCompairClicked(event: MouseEvent) //вызов настройки весовых коэффициентов методом таблицы парных сравнений
    {
        GLOBAL.loadFXMLWindow("WeightParameters.fxml", "Настройка весовых коэффициентов")
            .setOnHiding{ event -> setWeightParametersFromConfig() } //установка весовых коэффициентов по закрытию окна
    }

    @FXML fun handleLableDefaultWeigthClicked(event: MouseEvent) //установка весовых коэффициентов в значения по умолчанию
    {
        WConfig.setDefaultProperties() //установка весовых коэффициентов в значения по умолчанию
        setWeightParametersFromConfig() //установка весовых коэффициентов
    }
}