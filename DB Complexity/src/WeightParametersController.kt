import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import java.text.DecimalFormat
import kotlin.math.pow


class WeightParametersController
{
    @FXML private lateinit var fxGridPane: GridPane //таблица парных сравнений

    private val N:Int = GLOBAL.NumMetrics //количество метрических характеристик
    private var weight = Array(N) { Array(N) { TextField() } }

    fun initialize()
    {
        for (i in 0 until N) //главная диагональ матрицы должна быть заполнена единицами и не иметь возможности редактирования
        {
            weight[i][i].text = "1.0"
            weight[i][i].isDisable = true
        }

        for (j in 0 until N)
        {
            for (i in 0 until N)
            {
                weight[i][j].alignment = Pos.CENTER //установка выравнивания по середине

                weight[i][j].textProperty().addListener{observable, oldValue, newValue -> //добавление прослушивателя, который будет заполнять симметричный элемент относительно главной диагонали
                    weight[j][i].text = "" + 1/newValue.toDouble()
                    //нужно добавить обработку исключений на не числа
                }

                fxGridPane.add(weight[i][j], i+1, j+1) //добавление текстового поля в таблицу
            }
        }
    }

    @FXML fun handleButtonCalculate(event: ActionEvent)
    {
        try
        {
            var weightNum = Array(N) { DoubleArray(N) }
            for (j in 0 until N)
            {
                for (i in 0 until N)
                {
                    weightNum[i][j] = weight[i][j].text.toDouble() //попытка преобразовать в массив чисел
                }
            }

            var weightRow = DoubleArray(N)
            for (i in 0 until N) //поиск цены альтернатив: геометрическое среднее строки таблицы
            {
                weightRow[i] = 1.0
                for (j in 0 until N)
                    weightRow[i] *= weightNum[j][i]

                weightRow[i] = weightRow[i].pow(1.0 / (N.toDouble()))
            }

            var summ:Double = 0.0 //сумма цен альтернатив
            for (i in 0 until N) summ += weightRow[i]

            for (i in 0 until N)
            {
                weightRow[i] /= summ //нахождение весов альтернатив
                GLOBAL.WMetrics[i] = weightRow[i] //сохранение расчитанного веса
            }

            val DFormat = DecimalFormat() //форматирование вывода переменной типа double
            DFormat.maximumFractionDigits = 3
            DFormat.minimumFractionDigits = 0
            DFormat.isGroupingUsed = false

            val alert = Alert(AlertType.INFORMATION)
            alert.title = GLOBAL.TITLE
            alert.headerText = "Весовые коэффициенты"
            alert.contentText = "Количество атрибутов таблицы: " + DFormat.format(weightRow[0]) +
                    "\nКоличество атрибутов в составе первичного ключа: " +  DFormat.format(weightRow[1]) +
                    "\nКоличество внешних ключей: " + DFormat.format(weightRow[2]) +
                    "\nКоличество уникальных индексов: " + DFormat.format(weightRow[3]) +
                    "\nКоличество неуникальных индексов: " + DFormat.format(weightRow[4])
            val stage = alert.dialogPane.scene.window as Stage
            stage.icons.add(Image(GLOBAL.ICONURL))
            val result = alert.showAndWait()
            if (result.orElse(ButtonType.CANCEL) == ButtonType.OK)
                ((event.source as Node).scene.window as Stage).close() //закрыть текущее окно

        }
        catch (e: NumberFormatException) {println("не везде цифры")}
    }
}