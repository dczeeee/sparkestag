package cn.wanghy.saprkestag.etl

import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{IntegerType, StructField, StructType}
import org.apache.spark.{SparkConf, SparkContext}

object HotWordSql {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("hot word scala").setMaster("local[2]")
    val sc = new SparkContext(conf)

    //    val lines = sc.textFile("hdfs://VM-0-15-ubuntu:9000/user/ubuntu/SogouQ.sample.txt")
    val lines = sc.textFile("data/SogouQ.sample.txt")

    val row = lines.map(line => {
      var arr = line.split("\t")
      val str = arr(3)
      val rank_click = str.split(" ")
      Row(rank_click(0).toInt, rank_click(1).toInt)
    })

    val structType = StructType(
      StructField("rank", IntegerType, false) ::
        StructField("click", IntegerType, false) :: Nil
    )

    val ss = SparkSession.builder().getOrCreate()
    val df = ss.createDataFrame(row, structType)
    df.createOrReplaceTempView("tb")
    val re = df.sqlContext.sql("select count(if(t.rank=t.click,1,null)) as hit, count(1) as total from tb as t")
    re.show()
    val next = re.toLocalIterator().next()
    val hit = next.getAs[Long]("hit")
    val total = next.getAs[Long]("total")
    println("hit rate = " + (hit.toDouble / total.toDouble))

  }
}
