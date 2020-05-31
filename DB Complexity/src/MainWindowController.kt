import Animations.Attenuation
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.image.Image
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.stage.Stage
import java.lang.Math.PI
import kotlin.math.cos
import kotlin.math.sin

class MainWindowController
{
    companion object
    {
        @JvmStatic public var DBSelected: String = "" //выбранная БД
        @JvmStatic public var TblSelected: String = "" //выбранная таблица
    }

    @FXML private lateinit var fxList: ListView<String> //список БД
    @FXML private lateinit var fxSecondPane: AnchorPane //панель во второй части SplitPane
    @FXML private lateinit var fxDrawPane: AnchorPane //панель, на которую помещается концептуальный граф
    @FXML private lateinit var lbSelectDB: Label //лейбл "Выберите базу данных"
    @FXML private lateinit var fxErrMsg: Label //лейбл для вывода ошибок
    @FXML private lateinit var fxCalculateButton: Button //кнопка "Рассчитать сложность БД"

    private var tables: ArrayList<TableShape> = ArrayList()
    private var arrows: ArrayList<ArrowDrawObject> = ArrayList()

    private lateinit var pushUp: Attenuation //объект анимации

    fun initialize() //функция, запускающаяся автоматически при включении окна
    {
        pushUp = Attenuation(fxErrMsg) //добавление лейбла вывода ошибок в объект анимации

        fxList.items.addAll(FXCollections.observableArrayList(DBHandler.getDatabases())) //получение списка баз данных и помещение его в левую панель

        fxList.selectionModel.selectedItemProperty().addListener { changed, oldValue, newValue -> //прослушиватель на список БД, чтобы обновлять отрисовку таблиц
            if (newValue != null)
            {
                DBSelected = newValue
                createTableShapes(newValue)
            }
        }

        fxDrawPane.widthProperty().addListener { observable, oldValue, newValue -> draw() } //прослушиватель на изменение ширины окна
        fxDrawPane.heightProperty().addListener { observable, oldValue, newValue -> draw() } // прослушиватель на изменение высоты окна

        fxCalculateButton.onAction = //прослушиватель на кнопку "Расчёт сложности БД"
            EventHandler { GLOBAL.loadFXMLWindow("DBComplexity.fxml", GLOBAL.TITLE + " - " + DBSelected, 510.0, 400.0) } //запуск окна расчёта сложности БД
    }

    private fun createTableShapes(DBName: String)
    {
        var al: ArrayList<String> = DBHandler.getTables(DBName) //получение списка таблиц БД
        if (al[0] == GLOBAL.ERROR) //если в первой записи есть маркер ошибки
            pushUp(GLOBAL.ERR_NO_CONNECTION_MYSQL) //вывести ошибку подключения к MySQL
        else
        {
            tables.clear() //очистка списка форм
            for(i in 0 until al.size)
                tables.add(TableShape(al[i])) //создание списка форм

            arrows = DBHandler.getDependTables(DBName) //получение списка зависимых таблиц
            if(arrows.size > 0)
                if (arrows[0].mainTable == GLOBAL.ERROR) //если в первой записи есть маркер ошибки
                    pushUp(GLOBAL.ERR_NO_CONNECTION_MYSQL) //вывести ошибку подключения к MySQL

            draw()
        }
    }

    public class ArrowDrawObject (mainTable: String, dependTable: String) //описание соединения между формами
    {
        var mainTable: String = mainTable
        var dependTable: String = dependTable
        var startX: Double = 0.0
        var startY: Double = 0.0
        var endX: Double = 0.0
        var endY: Double = 0.0
    }

    private fun createTblComplexityWindow() //запуск окна расчёта сложности таблицы
    {
        val stage = Stage()
        val loader = FXMLLoader(javaClass.getResource("DBComplexity.fxml"))
        stage.scene = Scene(loader.load())
        stage.minWidth = 510.0
        stage.minHeight = 400.0

        var controller: DBComplexityController = loader.getController() //получение экземпляра контроллера
        controller.type = 1 //установка типа сложности: расчёт сложности таблицы

        stage.title = GLOBAL.TITLE //установка заголовка окна
        stage.icons.add(Image(GLOBAL.ICONURL)) //установка иконки окна
        stage.show()
    }

    private fun draw()
    {
        if (tables.isNotEmpty()) //если таблицы не получены, то очищать не надо (необходимо, чтобы при первом заходе был виден лейбл "выберите БД")
        {
            fxSecondPane.children.remove(lbSelectDB) //удаление надписи "выберите БД"
            fxCalculateButton.isDisable = false //сделать кнопку "Расчёт сложности БД" активной
        }

        fxDrawPane.children.clear() //очистка панели

        var centerX = fxDrawPane.width / 2 //центральная координата X во второй части splitPane
        var centerY = fxDrawPane.height / 2 //центральная координата Y во второй части splitPane
        var R = if (centerX < centerY) centerX*4/5 else centerY*4/5 //радиус окружности, по контуру которой будут размещены элементы

        var rectHeight = (if (centerX < centerY) fxDrawPane.width else fxDrawPane.height) / tables.size //высота прямоугольника
        var rectWidth = rectHeight + rectHeight/4 //ширина прямоугольника

        for(i in 0 until tables.size)
        {
            //рассчёт координат прямоугольников
            tables[i].X = centerX + R * cos(2 * PI * i / tables.size) - rectWidth/2
            tables[i].Y = centerY + R * sin(2 * PI * i / tables.size) - rectHeight/2

            for(j in 0 until arrows.size) //рассчёт координат стрелок
            {
                if(arrows[j].mainTable == tables[i].tableName)
                {
                    arrows[j].endX = tables[i].X + rectWidth/2
                    arrows[j].endY = tables[i].Y + rectHeight/2
                }
                if(arrows[j].dependTable == tables[i].tableName)
                {
                    arrows[j].startX = tables[i].X + rectWidth/2
                    arrows[j].startY = tables[i].Y + rectHeight/2
                }
            }

            //добавление прослушивателя на нажатие контейнера
            tables[i].box.onMouseClicked = EventHandler { event ->
                TblSelected = tables[i].tableName
                createTblComplexityWindow()
            }
        }

        var fontSize = if(R<150) 12.0 else if(R<300) 16.0 else 18.0 //размер шрифта

        for(i in 0 until arrows.size) //отрисовка стрелок
        {
            var line = Line (arrows[i].startX, arrows[i].startY, arrows[i].endX, arrows[i].endY)
            fxDrawPane.children.add(line) //добавление стрелки на панель
        }

        for (i in 0 until tables.size)
        {
            tables[i].rectHeight = rectHeight
            if(TableShape.FONT_SIZE_AUTO) //если включена автоматическая настройка шрифта
                tables[i].fontSize = fontSize
            fxDrawPane.children.add(tables[i].box) //добавление контейнера на панель
        }
    }

    private fun pushUp(message: String) //создание пуш-ап анимации
    {
        fxErrMsg.text = message
        pushUp.playAnim()
    }

    @FXML fun handleButtonSettings(event: ActionEvent) //кнопка "настройки"
    {
        GLOBAL.loadFXMLWindow("Settings.fxml", GLOBAL.TITLE + " - настройки", 540.0, 530.0) //запуск окна настроек
            .setOnHiding{ createTableShapes(DBSelected) } //при закрытии окна настроек перерисовать граф
    }

    @FXML fun handleButtonReload(event: ActionEvent) //кнопка "обновить 🔃"
    {
        fxList.items.clear() //очистка списка БД
        fxList.items.addAll(FXCollections.observableArrayList(DBHandler.getDatabases())) //получение списка баз данных и помещение его в левую панель
    }

    @FXML fun handleButtonHistory(event: ActionEvent) //кнопка "История"
    {
        GLOBAL.loadFXMLWindow("History.fxml", GLOBAL.TITLE + " - История расчёта", 510.0, 400.0) //запуск окна истории расчётов
    }
}