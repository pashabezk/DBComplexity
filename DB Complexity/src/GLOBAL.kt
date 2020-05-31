import javafx.fxml.FXMLLoader.load
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.stage.Stage

class GLOBAL
{
    companion object
    {
        @JvmStatic public val TITLE: String = "DBComplexity" //заголовок приложения
        @JvmStatic public val ICONURL: String = "Resources/icon.png" //адрес иконки приложения
        @JvmStatic public val INTERFACE_CONFIG_FILE_URL: String = "files/Interface.config" //адрес файла конфигурации интерфейса приложения
        @JvmStatic public val CONNECTION_CONFIG_FILE_URL: String = "files/Connection.config" //адрес файла конфигурации подключения к MySQL
        @JvmStatic public val WEIGHT_PARAM_CONFIG_FILE_URL: String = "files/WeightParameters.config" //адрес файла конфигурации весовых коэффициентов

        //описание ошибок
        @JvmStatic public val ERROR: String = "Error"
        @JvmStatic public val ERR_FIELDS_ARE_EMPTY: String = "Не все поля заполнены"
        @JvmStatic public val ERR_NO_CONNECTION_MYSQL: String = "Не удалось подключиться к MySQL"
        @JvmStatic public val ERR_CELL_CONTAINS_NOT_NUMBER: String = "Все ячейки должны содержать числа"
        @JvmStatic public val ERR_CELL_CONTAINS_NULL: String = "Ячейки не должны содержать ноль"
        @JvmStatic public val DELETE_SUCCESS: String = "Запись удалена"
        @JvmStatic public val PRESS_ENTER_TO_SAVE: String = "Для сохранения нажмите Enter"
        @JvmStatic public val NO_HISTORY: String = "Нет сохранённых записей о расчётах сложности"

        //цвета
        @JvmStatic val CLR_BLACK: Color = Color.BLACK //чёрный цвет
        @JvmStatic val CLR_NEUTRAL: Color = Color.BLUE //нейтральный цвет
        @JvmStatic val CLR_ERROR: Color = Color.RED //цвет ошибки

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