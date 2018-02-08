import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark._
import org.apache.spark.streaming._
import spray.json._

import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions._

object Aggregator extends App with JsonFormatSupport {
  val KafkaHost = "kafka:9092"

  val kafkaParams = Map[String, Object](
    "bootstrap.servers"  -> KafkaHost,
    "group.id"           -> "2",
    "auto.offset.reset"  -> "earliest",
    "key.deserializer"   -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer]
  )

  val conf = new SparkConf().setMaster("local[*]").setAppName("aggregator")
  val ssc  = new StreamingContext(conf, Seconds(10))

  val topics = Array("vehicles")
  val stream = KafkaUtils.createDirectStream[String, String](
    ssc,
    PreferConsistent,
    Subscribe[String, String](topics, kafkaParams)
  )

  val messages = stream.map(_.value()).map(_.parseJson.convertTo[TiledVehicle])

  messages.foreachRDD { message =>
    {
      val sqlContext = SQLContext.getOrCreate(message.sparkContext)

      import sqlContext.implicits._

      val sensorDF = message.toDF()
      println(message.count())
      sensorDF.show()
    }
  }

  ssc.start()
  ssc.awaitTermination()

}
