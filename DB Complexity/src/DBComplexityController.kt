import Configs.WConfig
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.stage.Stage
import java.text.DecimalFormat

class DBComplexityController
{
    @FXML private lateinit var fxTitle: Label //заголовок окна
    @FXML private lateinit var fxCol: Label //количество атрибутов
    @FXML private lateinit var fxPK: Label //количество атрибутов в составах первичных ключей
    @FXML private lateinit var fxFK: Label //количество атрибутов в составах внешних ключей
    @FXML private lateinit var fxIndU: Label //количество уникальных индексов
    @FXML private lateinit var fxIndNU: Label //количество неуникальных индексов
    @FXML private lateinit var fxDBC: Label //сложность БД

    fun initialize()
    {
        var metrics: IntArray =  DBHandler.getDBMetrics(GLOBAL.DBSelected) //получение метрик БД

        fxTitle.text += " «" + GLOBAL.DBSelected + "»"

        fxCol.text = "" + metrics[0]
        fxPK.text = "" + metrics[1]
        fxFK.text = "" + metrics[2]
        fxIndU.text = "" + metrics[3]
        fxIndNU.text = "" + metrics[4]

        val DFormat = DecimalFormat() //форматирование вывода переменной типа double
        DFormat.maximumFractionDigits = 3
        DFormat.minimumFractionDigits = 0
        DFormat.isGroupingUsed = false

        var complexity:Double  =  0.0
        for (i in 0 until WConfig.NumMetrics)
            complexity += metrics[i].toDouble() * WConfig.WMetrics[i]
        fxDBC.text = "" + DFormat.format(complexity)
    }

    @FXML fun handleButtonOK(event: ActionEvent)
    {
        ((event.source as Node).scene.window as Stage).close() //закрыть текущее окно
    }
}