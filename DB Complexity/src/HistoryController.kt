import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.input.MouseEvent
import javafx.stage.Stage


class HistoryController
{
    @FXML private lateinit var fxTable: TableView<HistoryTV> //панель, на которую будет помещена информация о расчётах
    @FXML private lateinit var fxDelete: Button //заголовок окна

    private var selectedItemId: Int = -1

    public class HistoryTV (
        var id: Int,
        var DBName: String,
        var complexity: Double,
        var datetime: String,
        var comment: String
    ) {
        override fun toString(): String = "$id $DBName"
    }

    fun initialize()
    {
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
//        commentColumn.onEditCancel = EventHandler { //действия при отмене редактирования
//            //вставить лейбл с выводом предупреждения о том, что сохранение происходит по нажатию ентера
//        }

        fxTable.items = FXCollections.observableArrayList(DBHandler.getHistory()) //получение списка истории
        fxTable.columns.addAll(nameColumn, complexityColumn, dateColumn, commentColumn); //добавление колонок

        fxTable.onMousePressed = EventHandler<MouseEvent?>{ //установка прослушивателя на нажатие строчки в таблице
            selectedItemId = (fxTable.selectionModel.selectedItem as HistoryTV).id //получение идентификатора записи истории
            fxDelete.isDisable = false //сделать кнопку "удалить" рабочей
        }

        fxDelete.onAction = EventHandler { //прослушиватель на кнопку "удалить"
            DBHandler.deleteHistory(selectedItemId)
            fxTable.items = FXCollections.observableArrayList(DBHandler.getHistory()) //получение списка истории
            fxDelete.isDisable = true //сделать кнопку "удалить" недействительной
        }
    }

    @FXML fun handleButtonClear(event: ActionEvent) //кнопка "Очистить"
    {
        DBHandler.deleteHistory() //очищение истории
        fxTable.items = FXCollections.observableArrayList(DBHandler.getHistory()) //обновление данных в таблице
    }

    @FXML fun handleButtonClose(event: ActionEvent) //кнопка "Закрыть"
    {((event.source as Node).scene.window as Stage).close()}//закрыть текущее окно
}