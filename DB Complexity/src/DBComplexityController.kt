import Configs.WConfig
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Stage
import java.text.DecimalFormat

class DBComplexityController
{
    @FXML private lateinit var fxAnchorPane: AnchorPane //панель, на которую будет помещена информация о расчётах
    @FXML private lateinit var fxTitle: Label //заголовок окна
    @FXML private lateinit var fxSumm: Label //лейбл, содержащий сумму критериев сложности: общая сложность БД/таблицы
    @FXML private lateinit var fxErrMsg: Label //лейбл вывода ошибки

    public var type: Int = 0 //тип оценки сложности
    //0 - сложность БД
    //1 - сложность таблицы

    fun initialize()
    {
        Platform.runLater {
            var metrics: IntArray //массив, содержащий метрики
            if(type == 1)
            {
                fxTitle.text += "таблицы «" + MainWindowController.TblSelected + "»"
                metrics =  DBHandler.getTableMetrics(MainWindowController.DBSelected, MainWindowController.TblSelected) //получение метрик таблицы
            }
            else
            {
                fxTitle.text += "БД «" + MainWindowController.DBSelected + "»"
                metrics =  DBHandler.getDBMetrics(MainWindowController.DBSelected) //получение метрик БД
            }
            if(metrics[0] == -1) fxErrMsg.text = GLOBAL.ERR_NO_CONNECTION_MYSQL //если не удалось подключиться к MySQL

            //расчёт сложности
            var complexity:Double  =  0.0 //переменная, хранящая сложность
            for (i in 0..2) //подсчёт к-ва атрибутов, первичных и внешних ключей
                complexity += metrics[i].toDouble() * WConfig.WMetrics[i]
            for (i in 0..1) //добавление к-ва индексов
                if(WConfig.IndexProperties[i]) //проверка, не отключен ли подсчёт данных индексов
                    complexity += metrics[i+3].toDouble() * WConfig.WMetrics[3]
            for (i in 0..3) //добавление к-ва ограничений на поля
                if(WConfig.ColumnProperties[i]) //проверка, не отключен ли подсчёт данных ограничений
                    complexity += metrics[i+5].toDouble() * WConfig.WMetrics[4]

            val DFormat = DecimalFormat() //форматирование вывода переменной типа double
            DFormat.maximumFractionDigits = 3
            DFormat.minimumFractionDigits = 0
            DFormat.isGroupingUsed = false

            fxSumm.text = DFormat.format(complexity) //оторажение сложности

            //создание списка лейблов
            var  labels = ArrayList<Label>()
            labels.add(Label("Количество атрибутов таблиц:"))
            labels.add(Label("Количество атрибутов в составах первичных ключей:"))
            labels.add(Label("Количество атрибутов в составах внешних ключей:"))
            labels.add(Label("Количество уникальных индексов:"))
            labels.add(Label("Количество неуникальных индексов:"))
            labels.add(Label("Количество ограничений auto_increment:"))
            labels.add(Label("Количество ограничений unique:"))
            labels.add(Label("Количество ограничений not null:"))
            labels.add(Label("Количество ограничений default:"))

            var  values = ArrayList<Label>() //лейблы, отображающие полученные метрики
            var  factors = ArrayList<Label>() //лейблы, отображающие коэффициенты домножения

            for (i in 0 until labels.size) {
                labels[i].layoutX = 30.0
                labels[i].layoutY = 40.0 * i
                labels[i].font = Font("System", 14.0)

                values.add(Label(""+metrics[i]))

                //настройка лейблов отображения коэффициентов
                factors.add(Label("× "))
                factors[i].layoutX = 430.0
                factors[i].layoutY = 40.0 * i
                factors[i].font = Font("System", 14.0)
                fxAnchorPane.children.add(factors[i])

                values[i].layoutX = 390.0
                values[i].layoutY = 40.0 * i
                values[i].font = Font("System", 14.0)

                fxAnchorPane.children.addAll(labels[i], values[i]) //добавление лейблов на панель
            }

            fun makeLabelsGray(ind: Int) {
                factors[ind].text += "0"
                labels[ind].textFillProperty().value = Color.DARKGREY
                values[ind].textFillProperty().value = Color.DARKGREY
                factors[ind].textFillProperty().value = Color.DARKGREY
            }

            //добавление численных значений весовых коэффициентов на лейблы вывода
            for (i in 0..2) //значения к-ва атрибутов, первичных и внешних ключей
                factors[i].text += DFormat.format(WConfig.WMetrics[i])
            for (i in 3..4) //значения к-ва индексов
                if(WConfig.IndexProperties[i-3]) //проверка, не отключен ли подсчёт данных индексов
                    factors[i].text += DFormat.format(WConfig.WMetrics[3])
                else makeLabelsGray(i)
            for (i in 5..8) //значения к-ва ограничений на поля БД
                if(WConfig.ColumnProperties[i-5]) //проверка, не отключен ли подсчёт данных ограничений
                    factors[i].text += DFormat.format(WConfig.WMetrics[4])
                else makeLabelsGray(i)

            if (type == 0) //если производился расчёт сложности БД
                DBHandler.addHistory(MainWindowController.DBSelected, complexity) //добавление новой записи в историю расчётов
        }
    }

    @FXML fun handleButtonOK(event: ActionEvent)
    {((event.source as Node).scene.window as Stage).close() }//закрыть текущее окно
}