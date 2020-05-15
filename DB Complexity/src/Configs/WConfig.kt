package Configs

import java.io.File
import java.io.FileReader
import java.util.*

class WConfig
{
    companion object
    {
        @JvmStatic public val NumMetrics: Int = 5 //количество метрик, используемых программой
        @JvmStatic public var WMetrics = doubleArrayOf(1.0, 1.0, 1.0, 1.0, 1.0) //весовые коэффициенты метрик

        @JvmStatic private val properties = Properties() //объект работы с сохранёнными параметрами

        @JvmStatic
        fun getProperties() //чтение конфигурации
        {
            var weight = Array(NumMetrics) { Array(NumMetrics) { -1.0 } }

            val configFile = File(GLOBAL.WEIGHT_PARAM_CONFIG_FILE_URL)
            if (configFile.exists())
            {
                properties.load(FileReader(configFile)) //загрузка параметров

                for (i in 1..NumMetrics) //получение весовых коэффициентов
                {
                    WMetrics[i-1] = properties.getProperty("M$i", "1.0").toDouble()
                }

                for (j in 0 until NumMetrics) //получение заполнения таблицы парных коэффициентов
                {
                    for (i in 0 until NumMetrics)
                    {
                        weight[i][j] = properties.getProperty("M$i$j", "-1.0").toDouble()
                    }
                }
            }
        }
    }
}