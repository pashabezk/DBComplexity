import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.stage.Stage


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

        var complexity:Int  = metrics[0] + metrics[1] + metrics[2] + metrics[3] + metrics[4]
        fxDBC.text = "" + complexity
    }

    @FXML fun handleButtonOK(event: ActionEvent)
    {
        ((event.getSource() as Node).getScene().getWindow() as Stage).close() //закрыть текущее окно
    }
}