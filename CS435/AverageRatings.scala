import org.apache.spark.sql.SparkSession


object AverageRatings {

  def main(args: Array[String]): Unit = {
    // Change Spark master and port number.
    val sc = SparkSession.builder().master("spark://jefferson-city:30360").getOrCreate().sparkContext


    // Reads file from input and creates RDD -- Change file input path to ratings.csv
    var lines = sc.textFile("hdfs://jefferson-city:30341/TPFiles/simpleRatings.csv", 10)
    val header = lines.first()

    lines = lines.filter(s => s != header)

    val rddHeader = sc.parallelize(List(header)).map(s=> (s.split(",")(0), s.split(",")(1) ))

    val keyVal = lines.map(s => ( s.split(",")(0), s.split(",")(1).toFloat ))

    // Gets the count of each value by key. 
    val count = sc.parallelize(keyVal.countByKey().toSeq)

    // Returns and RDD of (key, sum of values)
    val sum = keyVal.reduceByKey( _+_ )

    // RDD containing (key, (sum, count)) for each key
    val combined = sum.join(count)

    // averages = RDD (key, averageRating)
    val averages = combined.mapValues{case (s, c) => (s / c).toString}

    // JOIN WITH OTHER FILE 
    var movLines = sc.textFile("hdfs://jefferson-city:30341/TPFiles/movies.csv")

    // Get and remove header from movie.csv. Stores in movHeader
    val movHeader = movLines.first()
    movLines = movLines.filter(s => s != movHeader)

    val rddMovHeader = sc.parallelize(List(movHeader)).map(s=> (s.split(",")(0), (s.split(",")(1), s.split(",")(2))))
    // Header RDD for years and movie
    val rddYearHeader = sc.parallelize(List("movieId,year").map(s=> (s.split(",")(0), s.split(",")(1))))
    val rddVoteHeader = sc.parallelize(List("movieId,votes").map(s=> (s.split(",")(0), s.split(",")(1))))


    // Both headers joined by movieId -- (movieId, title, genre, rating, year, votes)
    val joinedHeaders = rddMovHeader.join(rddHeader).join(rddYearHeader).join(rddVoteHeader)

    val movKeyVal = movLines.map(s => ( s.split(",")(0), (s.split(",").drop(1).dropRight(1).mkString(",").replaceAll("[,]", ""), s.split(",").last) ))
    
    // Joined RDD of movies and ratings
    val movieRatings = movKeyVal.join(averages, 10)

    // Creates an RDD of the year the movie was released
    val yearRDD = movieRatings.map{
      x => 
      val yearPattern = "(\\([0-9]{4}\\))".r.findFirstIn(x._2._1._1).getOrElse("0")
      (x._1, yearPattern.toString.replaceAll("[()]", ""))
      // (x._1, x._2._1._1.slice(x._2._1._1.lastIndexOf(')')-4, x._2._1._1.lastIndexOf(')')))
    }

    val combinedRatingsYear = movieRatings.join(yearRDD).join(count.mapValues(s => s.toString))

    // Rejoin headers with data
    val output = joinedHeaders ++ combinedRatingsYear

    output.map(x=> x._1 + "," + x._2._1._1._1._1 + "," + x._2._1._1._1._2 + "," + x._2._1._1._2 + "," + x._2._1._2 + "," + x._2._2).saveAsTextFile(args(0))
  }
}