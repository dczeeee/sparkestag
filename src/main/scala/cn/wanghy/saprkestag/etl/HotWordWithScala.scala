package cn.wanghy.saprkestag.etl

import org.apache.spark.{SparkConf, SparkContext}

object HotWordWithScala {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("hot word scala").setMaster("local[2]")
    conf.set("dfs.client.use.datanode.hostname", "true")
    val sc = new SparkContext(conf)

    val lines = sc.textFile("hdfs://VM-0-15-ubuntu:9000/user/ubuntu/SogouQ.sample.txt")
//    val lines = sc.textFile("data/SogouQ.sample.txt")
    val total = lines.count()
    val hitCount = lines.map(x => x.split("\t")(3))
      .map(words => words.split(" ")(0).equals(words.split(" ")(1)))
      .filter(x => x == true).count()

    println("hit rate = " + (hitCount.toDouble / total.toDouble))

  }
}
