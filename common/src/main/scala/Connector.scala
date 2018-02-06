package com.lunatech.collector

import com.outworkers.phantom.connectors.{ CassandraConnection, ContactPoints }
import com.typesafe.config.ConfigFactory

import com.datastax.driver.core.{ Cluster, Session }

import scala.collection.JavaConverters._

object Connector {
  private val config = ConfigFactory.load()

  private val hosts: List[String] = config.getStringList("cassandra.hosts").asScala.toList //List("cassandra")
  private val keyspace            = config.getString("cassandra.keyspace")

  private val cluster: Cluster = Cluster
    .builder()
    .withClusterName("myCluster")
    .addContactPoint(hosts(0))
    .build()

  val session: Session = cluster.connect(keyspace)

  lazy val connector: CassandraConnection = ContactPoints(hosts).keySpace(keyspace)

}
