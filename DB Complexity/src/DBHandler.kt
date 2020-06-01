import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*
import kotlin.collections.ArrayList

public class DBHandler
{
    companion object
    {
        @JvmStatic private val DBHISTORY: String = "/dbcomplexity" //название БД для работы с историей расчётов

        @JvmStatic private var DBConnection: Connection? = null

        @JvmStatic var DBMS_URL: String = ""
        @JvmStatic var port: String = ""
        @JvmStatic var user: String = ""
        @JvmStatic var password: String = ""

        @Throws(SQLException::class)
        fun getDBConnection(DBName: String = "/information_schema"): Connection? {
            if (DBConnection == null) //проверка не открыт ли уже доступ к БД
            {
                val p = Properties() //создание параметров для подключения к БД
                p.setProperty("user", user)
                p.setProperty("password", password)
                p.setProperty("useUnicode", "true")
                p.setProperty("characterEncoding", "cp1251")
                DBConnection = DriverManager.getConnection(
                    "jdbc:mysql://" + DBMS_URL + (if(port != "") ":$port" else "") + DBName, p) //создание подключения к БД
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
            var al: ArrayList<String> = ArrayList()
            try {
                val result = getDBConnection()!!.createStatement().executeQuery("show databases;")
                while (result.next()) {
                    al.add(result.getString("database"))
                }
                closeDB()
            } catch (e: SQLException) {
                e.printStackTrace()
                al.add(GLOBAL.ERROR) //создание маркера ошибки в первой записи
            }
            return al
        }

        @JvmStatic
        fun getTables(database: String): ArrayList<String> //получение списка таблиц БД
        {
            var al: ArrayList<String> = ArrayList()
            try {
                val result = getDBConnection()!!.createStatement()
                    .executeQuery("select TABLE_NAME from tables where TABLE_SCHEMA='$database';")
                while (result.next()) {
                    al.add(result.getString("TABLE_NAME"))
                }
                closeDB()
            } catch (e: SQLException) {
                e.printStackTrace()
                al.add(GLOBAL.ERROR) //создание маркера ошибки в первой записи
            }
            return al
        }

        @JvmStatic
        fun getDependTables(database: String): ArrayList<MainWindowController.ArrowDrawObject> //получение списка зависимых таблиц (главная таблица - зависимая)
        {
            var al: ArrayList<MainWindowController.ArrowDrawObject> = ArrayList()
            try {
                val result = getDBConnection()!!.createStatement()
                    .executeQuery("select referenced_table_name as main, table_name as depend from referential_constraints where constraint_schema='$database';")
                while (result.next()) {
                    al.add(MainWindowController.ArrowDrawObject(result.getString("main"), result.getString("depend")))
                }
                closeDB()
            } catch (e: SQLException) {
                e.printStackTrace()
                al.add(MainWindowController.ArrowDrawObject(GLOBAL.ERROR, "")) //создание маркера ошибки в первой записи
            }
            return al
        }

        @JvmStatic
        fun getDBMetrics(database: String): IntArray //получение списка метрик БД
        {
            var metrics: IntArray = intArrayOf(-1)
            try {
                val result = getDBConnection()!!.createStatement()
                    .executeQuery("select col,pk,fk,ind_u,ind_nu,au_i,uniq,nn,def from\n" +
                            "(select count(*) as col from tables,columns where tables.table_name=columns.table_name and columns.table_schema='$database' and tables.table_schema='$database')t1,\n" +
                            "(select count(*) as pk from (select table_name from key_column_usage where constraint_schema='$database' and constraint_name='PRIMARY')a)t2,\n" +
                            "(select count(*) as fk from table_constraints where constraint_type='FOREIGN KEY' and table_schema='$database')t3,\n" +
                            "(select count(*) as ind_u from statistics where table_schema='$database' and non_unique=0)t4,\n" +
                            "(select count(*) as ind_nu from statistics where table_schema='$database' and non_unique=1)t5,\n" +
                            "(select count(*) as au_i from columns where table_schema='$database' and extra like '%auto_increment%')t6,\n" +
                            "(select count(*) as uniq from table_constraints where table_schema='$database' and constraint_type='UNIQUE')t7,\n" +
                            "(select count(*) as nn from columns where table_schema='$database' and is_nullable='NO')t8,\n" +
                            "(select count(*) as def from columns where table_schema='$database' and column_default!=null)t9;")
                if (result.next())
                    metrics = intArrayOf(
                        result.getInt("col"), //количество атрибутов таблиц БД
                        result.getInt("pk"), //количество атрибутов в составе первичных ключенй в БД
                        result.getInt("fk"), //количество атрибутов в составе внешних ключей в БД
                        result.getInt("ind_u"), //количество уникальных индексов в БД
                        result.getInt("ind_nu"), //количество неуникальных индексов в БД
                        result.getInt("au_i"), //количество ограничений auto_increment
                        result.getInt("uniq"), //количество ограничений unique
                        result.getInt("nn"), //количество ограничений not_null
                        result.getInt("def") //количество ограничений auto_increment
                    )
                closeDB()
            } catch (e: SQLException) {e.printStackTrace()}
            return metrics
        }

        @JvmStatic
        fun getTableMetrics(database: String, table: String): IntArray //получение списка метрик таблицы БД
        {
            var metrics: IntArray = intArrayOf(-1)
            try {
                val result = getDBConnection()!!.createStatement()
                    .executeQuery("select col,pk,fk,ind_u,ind_nu,au_i,uniq,nn,def from\n" +
                            "(select count(*) as col from tables,columns where tables.table_name=columns.table_name and columns.table_schema='$database' and tables.table_schema='$database' and tables.table_name='$table')t1,\n" +
                            "(select count(*) as pk from key_column_usage where constraint_schema='$database' and constraint_name='PRIMARY' and table_name='$table')t2,\n" +
                            "(select count(*) as fk from table_constraints where constraint_type='FOREIGN KEY' and table_schema='$database' and table_name='$table')t3,\n" +
                            "(select count(*) as ind_u from statistics where table_schema='$database' and non_unique=0 and table_name='$table')t4,\n" +
                            "(select count(*) as ind_nu from statistics where table_schema='$database' and non_unique=1 and table_name='$table')t5,\n" +
                            "(select count(*) as au_i from columns where table_schema='$database' and table_name='$table' and extra like '%auto_increment%')t6,\n" +
                            "(select count(*) as uniq from table_constraints where table_schema='$database' and table_name='$table' and constraint_type='UNIQUE')t7,\n" +
                            "(select count(*) as nn from columns where table_schema='$database' and table_name='$table' and is_nullable='NO')t8,\n" +
                            "(select count(*) as def from columns where table_schema='$database' and table_name='$table' and column_default!=null)t9;")
                if (result.next())
                    metrics = intArrayOf(
                        result.getInt("col"), //количество атрибутов таблицы
                        result.getInt("pk"), //количество атрибутов в составе первичных ключенй
                        result.getInt("fk"), //количество атрибутов в составе внешних ключей
                        result.getInt("ind_u"), //количество уникальных индексов
                        result.getInt("ind_nu"), //количество неуникальных индексов
                        result.getInt("au_i"), //количество ограничений auto_increment
                        result.getInt("uniq"), //количество ограничений unique
                        result.getInt("nn"), //количество ограничений not_null
                        result.getInt("def") //количество ограничений auto_increment
                    )
                closeDB()
            } catch (e: SQLException) {e.printStackTrace()}
            return metrics
        }

        @JvmStatic
        fun getHistory(): ArrayList<HistoryController.HistoryTV> //получение списка баз данных
        {
            var al: ArrayList<HistoryController.HistoryTV> = ArrayList()
            try {
                val result = getDBConnection(DBHISTORY)!!.createStatement().executeQuery("select * from history;")
                while (result.next()) {
                    al.add(HistoryController.HistoryTV(result.getInt("id"),
                        result.getString("dbname"), result.getDouble("complexity"),
                        result.getString("ddate") + " " + result.getString("ttime"), result.getString("comment")))
                }
                closeDB()
            } catch (e: SQLException) {
                e.printStackTrace()
                al.add(HistoryController.HistoryTV(-1, GLOBAL.ERR_NO_CONNECTION_MYSQL, 0.0, "", ""))
            }
            return al
        }

        @JvmStatic
        fun addHistory(DBName: String, complexity: Double): Int //удаление истории расчёта сложности
        {
            var ret: Int = 0 //в случае успешного удаления возвращается 1, иначе 0
            try {
                getDBConnection(DBHISTORY)!!.createStatement().executeUpdate("insert into history values(default,'$DBName',$complexity,curdate(), curtime(),'');")
                closeDB()
                ret = 1
            } catch (e: SQLException) {e.printStackTrace()}
            return ret
        }

        @JvmStatic
        fun updateHistory(id: Int, comment: String): Int //удаление истории расчёта сложности
        {
            var ret: Int = 0 //в случае успешного редактирования возвращается 1, иначе 0
            try {
                getDBConnection(DBHISTORY)!!.createStatement().executeUpdate("update history set comment='$comment' where id=$id;")
                closeDB()
                ret = 1
            } catch (e: SQLException) {e.printStackTrace()}
            return ret
        }

        @JvmStatic
        fun deleteHistory(id: Int = -1): Int //удаление истории расчёта сложности
        {
            //если id==-1, то удалить всю историю, иначе только запись по её идентификатору
            var ret: Int = 0 //в случае успешного удаления возвращается 1, иначе 0
            try {
                getDBConnection(DBHISTORY)!!.createStatement().executeUpdate("delete from history" +
                        if(id == -1) "" else {" where id=$id"} + ";")
                closeDB()
                ret = 1
            } catch (e: SQLException) {e.printStackTrace()}
            return ret
        }
    }
}