import org.apache.spark.sql.SparkSession

case class Employee(id:Long, name:String, age:Short, salary:Float)
object SparkPhoenixConnector {

  def main(args: Array[String]): Unit = {

    val appName = "Spark Phoenix Integration"

    val spark: SparkSession = SparkSession.builder().master("local").appName(appName).getOrCreate()
    val zkUrl = "ZK_HOST:2181"
    val tableName = "EMPLOYEE1"
    import spark.implicits._

    val inputDF = Seq(Employee(1L, "Employee1", 29, 70000.5f),Employee(2L, "Employee2", 34, 15000.2f),Employee(3L, "Employee3", 37, 17000.2f)).toDF()
    inputDF.write.format("org.apache.phoenix.spark").mode("OverWrite").options(Map("table" -> tableName, "zkUrl" -> zkUrl)).save()

    val df = spark.sqlContext.load("org.apache.phoenix.spark", Map("table" -> tableName, "zkUrl" -> zkUrl))
    df.show()

    df.filter(df("AGE") > 30).show()

    val employeeDF = Seq(Employee(4L, "Employee4", 49, 70000.5f),Employee(5L, "Employee5", 43, 15000.2f)).toDF()
    employeeDF.write.format("org.apache.phoenix.spark").mode("OverWrite").options(Map("table" -> tableName, "zkUrl" -> zkUrl)).save()

    val dfNew = spark.sqlContext.load("org.apache.phoenix.spark", Map("table" -> tableName, "zkUrl" -> zkUrl))
    dfNew.show()

  }
}
