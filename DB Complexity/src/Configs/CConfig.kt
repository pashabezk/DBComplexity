package Configs

import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.util.*

class CConfig //connection config - конфигурация подключения к MySQL
{
    var URL: String = "" //URL базы данных
    var port: String = "" //порт
    val properties = Properties() //объект работы с сохранёнными параметрами

    init
    {
        val configFile = File(GLOBAL.CONNECTION_CONFIG_FILE_URL)
        if (configFile.exists())
        {
            properties.load(FileReader(configFile)) //загрузка параметров
            URL = properties.getProperty("URL", "")
            port = properties.getProperty("PORT", "")
        }
    }

    fun saveProperties() //сохранение конфигурации
    {
        properties.setProperty("URL", URL)
        properties.setProperty("PORT", port)

        properties.store(FileOutputStream(GLOBAL.CONNECTION_CONFIG_FILE_URL), null)
    }
}