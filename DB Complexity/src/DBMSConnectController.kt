import Animations.Shake
import Configs.CConfig
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.stage.Stage
import java.sql.SQLException

class DBMSConnectController
{
    @FXML private lateinit var fxURL: TextField //URL СУБД
    @FXML private lateinit var fxPort: TextField //порт
    @FXML private lateinit var fxUser: TextField //пользователь MySQL
    @FXML private lateinit var fxPassword: PasswordField //пароль пользователя MySQL

    @FXML private lateinit var fxErrMsg: Label //сообщение об ошибках

    private var conConfig: CConfig = CConfig() //конфигурация подключения

    fun initialize()
    {
        //заполнение полей согласно файлу конфигурации
        fxURL.text = conConfig.URL
        fxPort.text = conConfig.port
    }

    @FXML fun handleButtonConnect(event: ActionEvent)
    {
        val errMsgAnim = Shake(fxErrMsg) //создание объекта анимации

        if((fxURL.text == "") or (fxUser.text == "") or(fxPassword.text == ""))
            fxErrMsg.text = GLOBAL.ERR_FIELDS_ARE_EMPTY
        else
        {
            DBHandler.DBMS_URL = fxURL.text
            DBHandler.port = fxPort.text
            DBHandler.user = fxUser.text
            DBHandler.password = fxPassword.text

            try {
                DBHandler.getDBConnection() //попытка подключиться к MySQL (в случае неудачи будет вызвано исключение)
                DBHandler.closeDB()

                GLOBAL.loadFXMLWindow("MainWindow.fxml", GLOBAL.TITLE, 600.0, 400.0) //запуск главного окна приложения
                ((event.source as Node).scene.window as Stage).close() //закрыть текущее окно

                //сохранение параметров конфигурации входа
                conConfig.URL = fxURL.text
                conConfig.port = fxPort.text
                conConfig.saveProperties()
            }
            catch (e: SQLException) {
                e.printStackTrace()
                fxErrMsg.text = GLOBAL.ERR_NO_CONNECTION_MYSQL
            }
        }
        errMsgAnim.playAnim()
    }
}