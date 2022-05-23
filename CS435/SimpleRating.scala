import org.apache.spark.sql.SparkSession

object SimpleRating {

  def main(args: Array[String]): Unit = {
    // Change Spark master and port number.
    val sc = SparkSession.builder().master("spark://jefferson-city:30360").getOrCreate().sparkContext

    // Reads file from input and creates RDD -- Change file input path to ratings.csv
    val lines = sc.textFile("hdfs://jefferson-city:30341/TPFiles/ratings.csv", 10)

    val ID_RATINGS = lines.map(s => ( s.split(",")(1), s.split(",")(2) ))

    // Change file output location if needed
    ID_RATINGS.map(x => x._1 + "," + x._2).saveAsTextFile("hdfs://jefferson-city:30341/TPFiles/simpleRatings.csv")
  }
}
