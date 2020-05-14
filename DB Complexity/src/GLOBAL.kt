import javafx.fxml.FXMLLoader.load
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage

class GLOBAL
{
    companion object
    {
        @JvmStatic public val ICONURL: String = "/resources/icon.png" //адрес иконки приложения
        @JvmStatic public val INTERFACE_CONFIG_FILE_URL: String = "files/Interface.config" //адрес файла конфигурации интерфейса приложения
        @JvmStatic public val TITLE: String = "DBComplexity" //заголовок приложения

        @JvmStatic public var DBSelected: String = "" //выбранная БД

        @JvmStatic public val NumMetrics: Int = 5 //количество метрик, используемых программой
        @JvmStatic public var WMetrics = doubleArrayOf(1.0, 1.0, 1.0, 1.0, 1.0) //весовые коэффициенты метрик

        //описание ошибок
        @JvmStatic public val ERR_FIELDS_ARE_EMPTY: String = "Не все поля заполнены"
        @JvmStatic public val ERR_NO_CONNECTION_MYSQL: String = "Не удалось подключиться к MySQL"

        @JvmStatic
        fun loadFXMLWindow(FXMLName: String, title: String, minWidth: Double = -1.0, minHeight: Double = -1.0): Stage //загрузка нового окна
        {
            val stage = Stage()
            stage.scene = Scene(load<Parent?>(javaClass.getResource(FXMLName)))
            stage.title = title //установка заголовка окна
            if ((minWidth == -1.0) or (minHeight == -1.0)) //если не были переданы параметры минимального размера окна
                stage.isResizable = false //запретить масштабирование
            else //иначе установка минимальных размеров окна
            {
                stage.minWidth = minWidth
                stage.minHeight = minHeight
            }
            stage.icons.add(Image(ICONURL)) //установка иконки окна
            stage.show()

            return stage;
        }
    }
}