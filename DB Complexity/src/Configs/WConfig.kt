package Configs

import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.util.*

class WConfig
{
    companion object
    {
        @JvmStatic public var IndexProperties = booleanArrayOf(true, true) //массив, содержащий описание учёта индексов при расчёте сложности
        @JvmStatic public var ColumnProperties = booleanArrayOf(true, true, true, true) //массив, содержащий описание учёта параметров ограничений полей при расчёте сложности

        @JvmStatic public val NumMetrics: Int = 5 //количество метрик, используемых программой
        @JvmStatic public var WMetrics = doubleArrayOf(1.0, 1.0, 1.0, 1.0, 1.0) //весовые коэффициенты метрик
        @JvmStatic public var WElements = Array(NumMetrics) { DoubleArray(NumMetrics) } //весовые коэффициенты метрик

        @JvmStatic private val properties = Properties() //объект работы с сохранёнными параметрами

        @JvmStatic
        fun getProperties() //чтение конфигурации
        {
            val configFile = File(GLOBAL.WEIGHT_PARAM_CONFIG_FILE_URL)
            if (configFile.exists())
            {
                properties.load(FileReader(configFile)) //загрузка параметров

                for (i in 1..NumMetrics) //получение весовых коэффициентов
                    WMetrics[i-1] = properties.getProperty("M$i", "1.0").toDouble()

                for (j in 0 until NumMetrics) //получение заполнения таблицы парных коэффициентов
                    for (i in 0 until NumMetrics)
                        WElements[i][j] = properties.getProperty("P$i$j", "-1.0").toDouble()

                for (i in 0..1) //получение параметров учёта индексов
                    IndexProperties[i] = properties.getProperty("I$i", "true").toBoolean()

                for (i in 0..3) //получение параметров учёта ограничений полей
                    ColumnProperties[i] = properties.getProperty("C$i", "true").toBoolean()
            }
            else
                for (j in 0 until NumMetrics)
                    for (i in 0 until NumMetrics)
                        WElements[i][j] = -1.0 //заполнение элементов -1
        }

        @JvmStatic
        fun saveProperties() //запись конфигурации
        {
            for (i in 1..NumMetrics) //сохранение весовых коэффициентов
                properties.setProperty("M$i", WMetrics[i-1].toString())

            for (j in 0 until NumMetrics) //сохранение заполнения таблицы парных коэффициентов
                for (i in 0 until NumMetrics)
                    properties.setProperty("P$i$j", WElements[i][j].toString())

            for (i in 0..1) //сохранение параметров учёта индексов
                properties.setProperty("I$i", IndexProperties[i].toString())

            for (i in 0..3) //сохранение параметров учёта ограничений полей
                properties.setProperty("C$i", ColumnProperties[i].toString())

            properties.store(FileOutputStream(GLOBAL.WEIGHT_PARAM_CONFIG_FILE_URL), null)
        }

        @JvmStatic
        fun setDefaultProperties() //запись конфигурации
        {
            WMetrics = doubleArrayOf(1.0, 1.0, 1.0, 1.0, 1.0)
            IndexProperties = booleanArrayOf(true, true)
            ColumnProperties = booleanArrayOf(true, true, true, true)
        }
    }
}