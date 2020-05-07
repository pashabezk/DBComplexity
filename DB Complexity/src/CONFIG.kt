import javafx.scene.paint.Color
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.util.*

class CONFIG
{
    companion object
    {
        //если файла конфигурации не будет, то по умолчанию цвета будут браться из TableShape
        @JvmStatic var CLR_SHAPE_NOT_SELECTED: Color = TableShape.DEFAULT_CLR_SHAPE_NOT_SELECTED //цвет не выбранного прямоугольника
        @JvmStatic var CLR_TEXT_NOT_SELECTED: Color = TableShape.DEFAULT_CLR_TEXT_NOT_SELECTED //цвет не выбранного текста
        @JvmStatic var CLR_SHAPE_SELECTED: Color = TableShape.DEFAULT_CLR_SHAPE_SELECTED //цвет выбранного прямоугольника
        @JvmStatic var CLR_TEXT_SELECTED: Color = TableShape.DEFAULT_CLR_TEXT_SELECTED //цвет не выбранного текста

        @JvmStatic var FONT_SIZE_AUTO: Boolean = TableShape.FONT_SIZE_AUTO //автоматический расчёт размера шрифта
        @JvmStatic var FONT_SIZE: Double = TableShape.DEFAULT_FONT_SIZE //размер шрифта

        @JvmStatic private val properties = Properties()

        @JvmStatic
        fun getProperties() //чтение конфигурации
        {
            val configFile = File(GLOBAL.INTERFACE_CONFIG_FILE_URL)

            if (configFile.exists())
            {
                properties.load(FileReader(configFile))//загрузка параметров

                //получение параметров
                CLR_SHAPE_NOT_SELECTED = Color.web(properties.getProperty("CLR_SHAPE_NOT_SELECTED", TableShape.DEFAULT_CLR_SHAPE_NOT_SELECTED.toString()))
                CLR_TEXT_NOT_SELECTED = Color.web(properties.getProperty("CLR_TEXT_NOT_SELECTED", TableShape.DEFAULT_CLR_TEXT_NOT_SELECTED.toString()))
                CLR_SHAPE_SELECTED = Color.web(properties.getProperty("CLR_SHAPE_SELECTED", TableShape.DEFAULT_CLR_SHAPE_SELECTED.toString()))
                CLR_TEXT_SELECTED = Color.web(properties.getProperty("CLR_TEXT_SELECTED", TableShape.DEFAULT_CLR_TEXT_SELECTED.toString()))
                FONT_SIZE_AUTO = properties.getProperty("FONT_SIZE_AUTO", ""+TableShape.FONT_SIZE_AUTO).toBoolean()
                FONT_SIZE = properties.getProperty("FONT_SIZE", ""+TableShape.DEFAULT_FONT_SIZE).toDouble()

                //сохранение в качестве параметров "по умолчанию"
                TableShape.DEFAULT_CLR_SHAPE_NOT_SELECTED = CLR_SHAPE_NOT_SELECTED
                TableShape.DEFAULT_CLR_TEXT_NOT_SELECTED = CLR_TEXT_NOT_SELECTED
                TableShape.DEFAULT_CLR_SHAPE_SELECTED = CLR_SHAPE_SELECTED
                TableShape.DEFAULT_CLR_TEXT_SELECTED = CLR_TEXT_SELECTED
                TableShape.FONT_SIZE_AUTO = FONT_SIZE_AUTO
                TableShape.DEFAULT_FONT_SIZE = FONT_SIZE
            }
        }

        @JvmStatic
        fun saveProperties() //сохранение конфигурации
        {
            properties.setProperty("CLR_SHAPE_NOT_SELECTED", CLR_SHAPE_NOT_SELECTED.toString())
            properties.setProperty("CLR_TEXT_NOT_SELECTED", CLR_TEXT_NOT_SELECTED.toString())
            properties.setProperty("CLR_SHAPE_SELECTED", CLR_SHAPE_SELECTED.toString())
            properties.setProperty("CLR_TEXT_SELECTED", CLR_TEXT_SELECTED.toString())
            properties.setProperty("FONT_SIZE_AUTO", FONT_SIZE_AUTO.toString())
            properties.setProperty("FONT_SIZE", FONT_SIZE.toString())

            properties.store(FileOutputStream(GLOBAL.INTERFACE_CONFIG_FILE_URL), null)
        }
    }
}
