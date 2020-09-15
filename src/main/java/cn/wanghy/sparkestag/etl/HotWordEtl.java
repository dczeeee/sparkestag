package cn.wanghy.sparkestag.etl;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.List;

public class HotWordEtl {

    public static void main(String[] args) {
        System.setProperty("HADOOP_USER_NAME", "ubuntu");
        SparkConf conf = new SparkConf().setAppName("hot word").setMaster("local[2]");
        JavaSparkContext jsc = new JavaSparkContext(conf);
//        JavaRDD<String> linesRdd = jsc.textFile("hdfs://VM-0-15-ubuntu:9000/user/ubuntu/SogouQ.sample.txt");
        JavaRDD<String> linesRdd = jsc.textFile("data/SogouQ.sample.txt");

        JavaPairRDD<String, Integer> pairRDD = linesRdd.mapToPair(new PairFunction<String, String, Integer>() {
            // string --> (string,integer)
            @Override
            public Tuple2<String, Integer> call(String s) throws Exception {
                String word = s.split("\t")[2];
                return new Tuple2<>(word, 1);
            }
        });

        JavaPairRDD<String, Integer> resultRdd = pairRDD.reduceByKey(new Function2<Integer, Integer, Integer>() {
            // ("hello",1) , ("hello",1) --> ("hello",2)
            @Override
            public Integer call(Integer integer, Integer integer2) throws Exception {
                return integer + integer2;
            }
        });

        JavaPairRDD<Integer, String> swapRdd = resultRdd.mapToPair(new PairFunction<Tuple2<String, Integer>, Integer, String>() {
            // ("hello",2) --> (2,"hello")
            @Override
            public Tuple2<Integer, String> call(Tuple2<String, Integer> stringIntegerTuple2) throws Exception {
                return stringIntegerTuple2.swap();
            }
        });

        JavaPairRDD<Integer, String> sorted = swapRdd.sortByKey(false);

        JavaPairRDD<String, Integer> hotWordRdd = sorted.mapToPair(new PairFunction<Tuple2<Integer, String>, String, Integer>() {
            // (2,"hello") --> ("hello",2)
            @Override
            public Tuple2<String, Integer> call(Tuple2<Integer, String> integerStringTuple2) throws Exception {
                return integerStringTuple2.swap();
            }
        });

        List<Tuple2<String, Integer>> take = hotWordRdd.take(10);

        for (Tuple2<String, Integer> tuple2 : take) {
            System.out.println(tuple2._1 + "===" + tuple2._2);
        }


    }
}
