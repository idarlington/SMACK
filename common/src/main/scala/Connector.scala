package com.lunatech.collector

import com.outworkers.phantom.connectors.{ CassandraConnection, ContactPoints }
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._

object Connector {
  private val config = ConfigFactory.load()

  private val hosts    = List("127.0.0.1") //config.getStringList("cassandra.host").asScala
  private val keyspace = "collector"       //config.getString("cassandra.keyspace")

  lazy val connector: CassandraConnection = ContactPoints(hosts).keySpace(keyspace)

}
