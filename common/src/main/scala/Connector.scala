import com.outworkers.phantom.connectors.{ CassandraConnection, ContactPoints }
import com.datastax.driver.core.{ Cluster, Session }

import AppConfiguration._

object Connector {

  private val hosts: List[String] = cassandraHosts

  private val cluster: Cluster = Cluster
    .builder()
    .withClusterName("myCluster")
    .addContactPoint(hosts(0))
    .build()

  val session: Session = cluster.connect(keyspace)

  lazy val connector: CassandraConnection = ContactPoints(hosts).keySpace(keyspace)

}
