package cn.wanghy.sparkestag.support;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

public class SparkUtils {

    private static ThreadLocal<JavaSparkContext> jscPool = new ThreadLocal<>();
    private static ThreadLocal<SparkSession> sessionPool = new ThreadLocal<>();

    /**
     * 获取 jsc
     *
     * @return
     */
    public static JavaSparkContext getJSC4Es(Boolean auto) {
        JavaSparkContext javaSparkContext = jscPool.get();
        if (javaSparkContext != null) {
            return javaSparkContext;
        }
        SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("es demo");
        conf.set("es.nodes", "VM-0-15-ubuntu");
        conf.set("es.port", "9200");
        conf.set("es.index.auto.create", auto.toString());
        conf.set("es.nodes.wan.only","true");
        conf.set("fs.defaultFS", "hdfs://cluster");
        conf.set("dfs.nameservices", "cluster");
        conf.set("dfs.ha.namenodes.cluster", "nn01,nn02");
        conf.set("dfs.namenode.rpc-address.cluster.nn01", "VM-0-15-ubuntu:9000");
        conf.set("dfs.namenode.rpc-address.cluster.nn02", "VM-0-11-ubuntu:9000");
        conf.set("dfs.client.use.datanode.hostname", "true");
        JavaSparkContext jsc = new JavaSparkContext(conf);

        jscPool.set(jsc);
        return jsc;
    }

    public static SparkSession initSession() {
        if (sessionPool.get() != null) {
            return sessionPool.get();
        }
        SparkSession session = SparkSession.builder().appName("member etl")
                .master("local[*]")
                .config("es.nodes", "VM-0-15-ubuntu")
                .config("es.port", "9200")
                .config("es.index.auto.create", "false")
                .config("es.nodes.wan.only","true")
                .config("fs.defaultFS", "hdfs://cluster")
                .config("dfs.nameservices", "cluster")
                .config("dfs.ha.namenodes.cluster", "nn01,nn02")
                .config("dfs.namenode.rpc-address.cluster.nn01", "VM-0-15-ubuntu:9000")
                .config("dfs.namenode.rpc-address.cluster.nn02", "VM-0-11-ubuntu:9000")
                .config("dfs.client.use.datanode.hostname", "true")
                .enableHiveSupport()
                .getOrCreate();
        sessionPool.set(session);
        return session;

    }


}
