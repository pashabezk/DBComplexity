import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.image.Image
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.stage.Stage
import java.io.IOException
import java.lang.Math.PI
import kotlin.math.cos
import kotlin.math.sin


class MainWindowController
{
    @FXML private lateinit var fxList: ListView<String>
    @FXML private lateinit var fxSecondPane: AnchorPane
    @FXML private lateinit var fxDrawPane: AnchorPane
    @FXML private lateinit var lbSelectDB: Label

    private var tables: ArrayList<TableDrawObject> = ArrayList()
    private var arrows: ArrayList<ArrowDrawObject> = ArrayList()

    fun initialize()
    {
        fxList.items.addAll(FXCollections.observableArrayList(DBHandler.getDatabases())) //получение списка баз данных и помещение его в левую панель

        fxList.selectionModel.selectedItemProperty().addListener { changed, oldValue, newValue -> //прослушиватель на список БД, чтобы обновлять отрисовку таблиц
            var al: ArrayList<String>? = newValue?.let { DBHandler.getTables(it) } //получение списка таблиц БД
            GLOBAL.DBSelected = newValue
            tables.clear()
            if (al != null)
            {
                arrows = newValue?.let { DBHandler.getDependTables(it) }!! //получение списка зависимых таблиц
                for(i in 0 until al.size)
                    tables.add(TableDrawObject(al[i]))
                draw()
            }
        }

        fxDrawPane.widthProperty().addListener { observable, oldValue, newValue -> draw() } //прослушиватель на изменение ширины окна
        fxDrawPane.heightProperty().addListener { observable, oldValue, newValue -> draw() } // прослушиватель на изменение высоты окна
    }

    private class TableDrawObject (name: String)
    {
        var name: String = name
        var X: Double = 0.0
        var Y: Double = 0.0
    }

    public class ArrowDrawObject (mainTable: String, dependTable: String)
    {
        var mainTable: String = mainTable
        var dependTable: String = dependTable
        var startX: Double = 0.0
        var startY: Double = 0.0
        var endX: Double = 0.0
        var endY: Double = 0.0
    }

    private fun draw()
    {
        var clrTblNSelected = Color.ORANGE //цвет не выбранного прямоугольника
        var clrTblNameNSelected = Color.DARKGREEN //цвет не выбранного текста

        var clrTblSelected = Color.web("#560074") //цвет выбранного прямоугольника (a6000e)
        var clrTblNameSelected = Color.DARKVIOLET //цвет не выбранного текста

        if (tables.isNotEmpty()) //если таблицы не получены, то очищать не надо (необходимо, чтобы при первом заходе был виден лейбл "выберите БД")
            fxSecondPane.children.remove(lbSelectDB) //удаление надписи "выберите БД"

        fxDrawPane.children.clear() //очистка панели

        var centerX = fxDrawPane.width / 2 //центральная координата X во второй части splitPane
        var centerY = fxDrawPane.height / 2 //центральная координата Y во второй части splitPane
        var R = if (centerX < centerY) centerX*4/5 else centerY*4/5 //радиус окружности, по контуру которой будут размещены элементы

        var rectHeight= if (centerX < centerY) fxDrawPane.width / tables.size else fxDrawPane.height / tables.size //высота прямоугольника
        var rectWidth = rectHeight + rectHeight/4 //ширина прямоугольника
        var rectArc = rectWidth / 4 //закругление краёв прямоугольника

        for(i in 0 until tables.size) //рассчёт координат прямоугольников
        {
            tables[i].X = centerX + R * cos(2 * PI * i / tables.size) - rectWidth/2
            tables[i].Y = centerY + R * sin(2 * PI * i / tables.size) - rectHeight/2

            for(j in 0 until arrows.size) //рассчёт координат стрелок
            {
                if(arrows[j].mainTable == tables[i].name)
                {
                    arrows[j].endX = tables[i].X + rectWidth/2
                    arrows[j].endY = tables[i].Y + rectHeight/2
                }
                if(arrows[j].dependTable == tables[i].name)
                {
                    arrows[j].startX = tables[i].X + rectWidth/2
                    arrows[j].startY = tables[i].Y + rectHeight/2
                }
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
            var rect = javafx.scene.shape.Rectangle() //создание прямоугольника
            rect.width = rectWidth
            rect.height = rectHeight
            rect.arcWidth = rectArc
            rect.arcHeight = rectArc
            rect.fill = clrTblNSelected

            val text = Text(tables[i].name) //создание названия для прямоугольника
            text.fill = clrTblNameNSelected
            text.font = Font.font(fontSize)

            val box = VBox(text, rect) //создание контейнера: текст - прямоугольник
            box.alignment = Pos.TOP_LEFT
            box.layoutX = tables[i].X
            box.layoutY = tables[i].Y - fontSize

            box.setOnMouseEntered { event -> //действия при наведении курсора
                rect.fill = clrTblSelected
                text.fill = clrTblNameSelected
            }

            box.setOnMouseExited { event -> //действия при отводе курсора
                rect.fill = clrTblNSelected
                text.fill = clrTblNameNSelected
            }

            fxDrawPane.children.add(box) //добавление контейнера на панель
        }
    }

    @FXML fun handleButtonSettings(event: ActionEvent) {}

    @FXML fun handleButtonCalculateDBComplexity(event: ActionEvent) //открытие окна расчёта сложности БД
    {
        val loader = FXMLLoader()
        loader.location = javaClass.getResource("DBComplexity.fxml")
        try {
            loader.load<Any>()
        } catch (ex: IOException) {ex.printStackTrace()}

        val stage = Stage()
        stage.scene = Scene(loader.getRoot())
        stage.title = GLOBAL.TITLE + " - " + GLOBAL.DBSelected
        stage.isResizable = false
        stage.icons.add(Image(GLOBAL.ICONURL))
        stage.show()
    }
}
