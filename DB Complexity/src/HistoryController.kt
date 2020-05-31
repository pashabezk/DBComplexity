import Animations.Attenuation
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.stage.Stage

class HistoryController
{
    @FXML private lateinit var fxTable: TableView<HistoryTV> //панель, на которую будет помещена информация о расчётах
    @FXML private lateinit var fxDelete: Button //заголовок окна
    @FXML private lateinit var fxErrMsg: Label //лейбл для вывода ошибок

    private var selectedItemId: Int = -1
    private lateinit var pushUp: Attenuation //объект анимации

    public class HistoryTV (
        var id: Int,
        var DBName: String,
        var complexity: Double,
        var datetime: String,
        var comment: String
    )

    fun initialize()
    {
        pushUp = Attenuation(fxErrMsg) //добавление лейбла вывода ошибок в объект анимации

        fxTable.isEditable = true //разрешение на редактирование таблицы

        //создание колонок таблицы
        val nameColumn: TableColumn<HistoryTV, String> = TableColumn("Название БД")
        val complexityColumn: TableColumn<HistoryTV, Double> = TableColumn("Сложность")
        val dateColumn: TableColumn<HistoryTV, String> = TableColumn("Дата и время")
        val commentColumn: TableColumn<HistoryTV, String> = TableColumn("Комментарий")

        //привязка фабрик для автоматического заполнения таблицы
        nameColumn.cellValueFactory = PropertyValueFactory("DBName")
        complexityColumn.cellValueFactory = PropertyValueFactory("complexity")
        dateColumn.cellValueFactory = PropertyValueFactory("datetime")
        commentColumn.cellValueFactory = PropertyValueFactory("comment")

        //добавление возможности создания и редактирования комментариев в истории расчёта сложностей
        commentColumn.cellFactory = TextFieldTableCell.forTableColumn() //встраивание в ячейку текстового поля для ввода
        commentColumn.onEditCommit = EventHandler { event -> //действия при окончании редактирования (сохранении)
            DBHandler.updateHistory(event.rowValue.id, event.newValue) //добавление комментария
        }
        commentColumn.onEditCancel = EventHandler { //действия при отмене редактирования: вывод предупрждения о том, что для сохранения необходимо нажимать enter
            pushUp(GLOBAL.PRESS_ENTER_TO_SAVE, GLOBAL.CLR_BLACK)
        }

        initTable() //заполнение таблицы
        fxTable.columns.addAll(nameColumn, complexityColumn, dateColumn, commentColumn); //добавление колонок

        fxTable.onMousePressed = EventHandler<MouseEvent?>{ //установка прослушивателя на нажатие строчки в таблице
            selectedItemId = (fxTable.selectionModel.selectedItem as HistoryTV).id //получение идентификатора записи истории
            fxDelete.isDisable = false //сделать кнопку "удалить" рабочей
        }

        fxDelete.onAction = EventHandler { //прослушиватель на кнопку "удалить"
            if(DBHandler.deleteHistory(selectedItemId)==1) //удаление записи
            {
                initTable() //обновление данных в таблице
                pushUp(GLOBAL.DELETE_SUCCESS, GLOBAL.CLR_NEUTRAL)
            }
            else pushUp(GLOBAL.ERR_NO_CONNECTION_MYSQL, GLOBAL.CLR_ERROR) //если не удалось подключиться к MySQL
            fxDelete.isDisable = true //сделать кнопку "удалить" недействительной
        }
    }

    private fun pushUp(message: String, color: Color) //создание пуш-ап анимации
    {
        fxErrMsg.text = message
        fxErrMsg.textFill = color
        pushUp.playAnim()
    }

    private fun initTable() //заполнение таблицы
    {
        var list: ArrayList<HistoryTV> = DBHandler.getHistory() //получение списка истории
        if (list.size == 0) //если данных нет вообще, то вывод сообщений о том, что не было произведено расчётов
        {
            fxErrMsg.text = GLOBAL.NO_HISTORY
            fxErrMsg.textFill = GLOBAL.CLR_BLACK
            fxTable.items = FXCollections.observableArrayList(list) //добавление пустого списка в таблицу
        }
        else if (list[0].id==-1) //проверка: если идентификатор первой записи равен -1, значит данные не были получены
        {
            fxErrMsg.text = GLOBAL.ERR_NO_CONNECTION_MYSQL
            fxErrMsg.textFill = GLOBAL.CLR_ERROR
        }
        else fxTable.items = FXCollections.observableArrayList(list) //если всё в порядке, то заполнить таблицу
    }

    @FXML fun handleButtonClear(event: ActionEvent) //кнопка "Очистить"
    {
        if(DBHandler.deleteHistory()==1) //очищение истории
            initTable() //обновление данных в таблице
        else pushUp(GLOBAL.ERR_NO_CONNECTION_MYSQL, GLOBAL.CLR_ERROR) //если не удалось подключиться к MySQL
    }

    @FXML fun handleButtonClose(event: ActionEvent) //кнопка "Закрыть"
    {((event.source as Node).scene.window as Stage).close()}//закрыть текущее окно
}