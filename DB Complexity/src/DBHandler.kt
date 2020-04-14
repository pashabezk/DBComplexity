import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*
import kotlin.collections.ArrayList

public class DBHandler
{
    companion object
    {
        private var DBConnection: Connection? = null
        private val DBURL: String = "//192.168.1.17/information_schema"

        @Throws(SQLException::class)
        fun getDBConnection(): Connection? {
            if (DBConnection == null) //проверка не открыт ли уже доступ к БД
            {
                val p = Properties() //создание параметров для подключения к БД
                p.setProperty("user", "user1")
                p.setProperty("password", "1234")
                p.setProperty("useUnicode", "true")
                p.setProperty("characterEncoding", "cp1251")
                DBConnection = DriverManager.getConnection("jdbc:mysql:" + DBURL, p) //создание подключения к БД
            }
            return DBConnection
        }

        @Throws(SQLException::class)
        fun closeDB()
        {
            DBConnection!!.close()
            DBConnection = null
        }

        @JvmStatic
        fun getDatabases(): ArrayList<String> //получение списка баз данных
        {
            var al: ArrayList<String> = ArrayList<String>()
            try {
                val result = getDBConnection()!!.createStatement()
                    .executeQuery("select distinct TABLE_SCHEMA from tables;")
                while (result.next()) {
                    al.add(result.getString("TABLE_SCHEMA"))
                }
                closeDB()
            } catch (e: SQLException) {e.printStackTrace()}
            return al
        }

        @JvmStatic
        fun getTables(database: String): ArrayList<String> //получение списка таблиц БД
        {
            var al: ArrayList<String> = ArrayList<String>()
            try {
                val result = getDBConnection()!!.createStatement()
                    .executeQuery("select TABLE_NAME from tables where TABLE_SCHEMA='$database';")
                while (result.next()) {
                    al.add(result.getString("TABLE_NAME"))
                }
                closeDB()
            } catch (e: SQLException) {e.printStackTrace()}
            return al
        }

        @JvmStatic
        fun getDependTables(database: String): ArrayList<MainWindowController.ArrowDrawObject> //получение списка зависимых таблиц (главная таблица - зависимая)
        {
            var al: ArrayList<MainWindowController.ArrowDrawObject> = ArrayList<MainWindowController.ArrowDrawObject>()
            try {
                val result = getDBConnection()!!.createStatement()
                    .executeQuery("select referenced_table_name as main, table_name as depend from referential_constraints where constraint_schema='$database';")
                while (result.next()) {
                    al.add(MainWindowController.ArrowDrawObject(result.getString("main"), result.getString("depend")))
                }
                closeDB()
            } catch (e: SQLException) {e.printStackTrace()}
            return al
        }

        @JvmStatic
        fun getDBMetrics(database: String): IntArray //получение списка метрик БД
        {
            var metrics: IntArray = intArrayOf(-1)
            try {
                val result = getDBConnection()!!.createStatement()
                    .executeQuery("select col,pk,fk,ind_u,ind_nu from" +
                            "(select count(columns.column_name) as col from tables,columns where tables.table_name=columns.table_name and columns.table_schema='$database' and tables.table_schema='$database')t1," +
                            "(select count(table_name) as pk from (SELECT table_name FROM  key_column_usage where constraint_schema='$database' and constraint_name='PRIMARY')a)t2," +
                            "(select count(constraint_type) as fk from table_constraints where constraint_type='FOREIGN KEY' and table_schema='$database')t3," +
                            "(select count(non_unique) as ind_u from statistics where table_schema='$database' and non_unique=0)t4," +
                            "(select count(non_unique) as ind_nu from statistics where table_schema='$database' and non_unique=1)t5;")
                if (result.next())
                    metrics = intArrayOf(
                        result.getInt("col"), //количество атрибутов таблиц БД
                        result.getInt("pk"), //количество атрибутов в составе первичных ключенй в БД
                        result.getInt("fk"), //количество атрибутов в составе внешних ключей в БД
                        result.getInt("ind_u"), //количество уникальных индексов в БД
                        result.getInt("ind_nu") //количество неуникальных индексов в БД
                    )
                closeDB()
            } catch (e: SQLException) {e.printStackTrace()}
            return metrics
        }
    }
}