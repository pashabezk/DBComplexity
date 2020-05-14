import javafx.application.Application
import javafx.fxml.FXMLLoader.load
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage

class Main : Application()
{
    override fun start(primaryStage: Stage?)
    {
        CONFIG.getProperties() //загрузка параметров конфигурации

        primaryStage?.scene = Scene(load<Parent?>(javaClass.getResource("DBMSConnect.fxml")))
        //primaryStage?.scene = Scene(load<Parent?>(javaClass.getResource("MainWindow.fxml")))
        primaryStage?.title = GLOBAL.TITLE
        primaryStage?.icons?.add(Image(GLOBAL.ICONURL))
        primaryStage?.show()
    }

    companion object
    {
        @JvmStatic
        fun main(args: Array<String>)
        {
            launch(Main::class.java)
        }
    }
}