class GLOBAL
{
    companion object
    {
        @JvmStatic public val ICONURL:String = "/resources/icon.png" //адрес иконки приложения
        @JvmStatic public val TITLE:String = "DBComplexity" //заголовок приложения

        @JvmStatic public var DBSelected:String = "" //выбранная БД

        @JvmStatic public val NumMetrics:Int = 5 //количество метрик, используемых программой
        @JvmStatic public var WMetrics = doubleArrayOf(1.0, 1.0, 1.0, 1.0, 1.0) //весовые коэффициенты метрик
    }
}