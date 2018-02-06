import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._

object AppConfiguration {

  private val config = ConfigFactory.load()

  /** cassandra **/
  val cassandraHosts: List[String] = config.getStringList("cassandra.hosts").asScala.toList //List("cassandra")
  val keyspace                     = config.getString("cassandra.keyspace")

  /** kafka **/
  val kafkaBroker: String = config.getString("kafka.broker")
  val kafkaPort: Int      = config.getInt("kafka.port")

  /** Collector App **/
  val collectorURL: String = config.getString("collector.url")

  /** Api App **/
  val apiPort: Int    = config.getInt("api.port")
  val apiHost: String = config.getString("api.host")

  /** Tile system **/
  val levelOfDetail: Int = config.getInt("tileSystem.levelOfDetail")

}
