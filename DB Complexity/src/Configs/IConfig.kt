package Configs

import GLOBAL
import TableShape
import javafx.scene.paint.Color
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.util.*

class IConfig
{
    companion object
    {
        //конфигурация по умолчанию
        @JvmStatic val DEFAULT_CLR_SHAPE_NOT_SELECTED: Color = Color.ORANGE //цвет не выбранного прямоугольника
        @JvmStatic val DEFAULT_CLR_TEXT_NOT_SELECTED: Color = Color.DARKGREEN //цвет не выбранного текста
        @JvmStatic val DEFAULT_CLR_SHAPE_SELECTED: Color = Color.web("#560074") //цвет выбранного прямоугольника (a6000e)
        @JvmStatic val DEFAULT_CLR_TEXT_SELECTED: Color = Color.DARKVIOLET //цвет не выбранного текста
        @JvmStatic val DEFAULT_FONT_SIZE_AUTO: Boolean = true //автоматический расчёт размера шрифта
        @JvmStatic val DEFAULT_FONT_SIZE: Double = 16.0 //размер шрифта

        //параметры текущей конфигурации
        @JvmStatic var CLR_SHAPE_NOT_SELECTED: Color = TableShape.CLR_SHAPE_NOT_SELECTED //цвет не выбранного прямоугольника
        @JvmStatic var CLR_TEXT_NOT_SELECTED: Color = TableShape.CLR_TEXT_NOT_SELECTED //цвет не выбранного текста
        @JvmStatic var CLR_SHAPE_SELECTED: Color = TableShape.CLR_SHAPE_SELECTED //цвет выбранного прямоугольника
        @JvmStatic var CLR_TEXT_SELECTED: Color = TableShape.CLR_TEXT_SELECTED //цвет не выбранного текста

        @JvmStatic var FONT_SIZE_AUTO: Boolean = TableShape.FONT_SIZE_AUTO //автоматический расчёт размера шрифта
        @JvmStatic var FONT_SIZE: Double = TableShape.FONT_SIZE //размер шрифта

        @JvmStatic private val properties = Properties() //объект работы с сохранёнными параметрами

        @JvmStatic
        fun getProperties() //чтение конфигурации
        {
            val configFile = File(GLOBAL.INTERFACE_CONFIG_FILE_URL)
            if (configFile.exists())
            {
                properties.load(FileReader(configFile)) //загрузка параметров

                //получение параметров
                CLR_SHAPE_NOT_SELECTED = Color.web(properties.getProperty("CLR_SHAPE_NOT_SELECTED", TableShape.CLR_SHAPE_NOT_SELECTED.toString()))
                CLR_TEXT_NOT_SELECTED = Color.web(properties.getProperty("CLR_TEXT_NOT_SELECTED", TableShape.CLR_TEXT_NOT_SELECTED.toString()))
                CLR_SHAPE_SELECTED = Color.web(properties.getProperty("CLR_SHAPE_SELECTED", TableShape.CLR_SHAPE_SELECTED.toString()))
                CLR_TEXT_SELECTED = Color.web(properties.getProperty("CLR_TEXT_SELECTED", TableShape.CLR_TEXT_SELECTED.toString()))
                FONT_SIZE_AUTO = properties.getProperty("FONT_SIZE_AUTO", ""+ TableShape.FONT_SIZE_AUTO).toBoolean()
                FONT_SIZE = properties.getProperty("FONT_SIZE", ""+ TableShape.FONT_SIZE).toDouble()

                TableShape.setPropertiesFromConfig() //установка параметров в TableShape
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

            TableShape.setPropertiesFromConfig() //установка параметров в TableShape

            properties.store(FileOutputStream(GLOBAL.INTERFACE_CONFIG_FILE_URL), null)
        }

        @JvmStatic
        fun setDefault() //установка значений по умолчанию
        {
            CLR_SHAPE_NOT_SELECTED = DEFAULT_CLR_SHAPE_NOT_SELECTED
            CLR_TEXT_NOT_SELECTED = DEFAULT_CLR_TEXT_NOT_SELECTED
            CLR_SHAPE_SELECTED = DEFAULT_CLR_SHAPE_SELECTED
            CLR_TEXT_SELECTED = DEFAULT_CLR_TEXT_SELECTED
            FONT_SIZE_AUTO = DEFAULT_FONT_SIZE_AUTO
            FONT_SIZE = DEFAULT_FONT_SIZE

            TableShape.setPropertiesFromConfig() //установка параметров в TableShape
        }
    }
}
