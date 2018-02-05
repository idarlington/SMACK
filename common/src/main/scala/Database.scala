import com.lunatech.collector.Connector._

import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl._

import scala.concurrent.Future

/**
 * This is our Database object that wraps our two existing tables,
 * giving the ability to receive different connectors
 * for example: One for production and other for testing
 */
class VehicleDatabase(override val connector: CassandraConnection) extends Database[VehicleDatabase](connector) {
  object VehiclesModel       extends VehiclesModel with connector.Connector
  object VehiclesByTileModel extends VehiclesByTileModel with connector.Connector

  def saveOrUpdate(vehicle: TiledVehicle): Future[ResultSet] =
    Batch.logged
      .add(VehiclesModel.store(vehicle))
      .add(VehiclesByTileModel.store(vehicle))
      .future()

  def getVehicleById(id: String) =
    VehiclesModel.getVehicleById(id)

  def vehiclesList(): Future[List[TiledVehicle]] =
    VehiclesModel.getVehicles()

  def getVehiclesID(): Future[List[String]] =
    VehiclesModel.getVehiclesID()
}

/**
 * This is the database, it connects to a cluster with multiple contact points
 */
object Database extends VehicleDatabase(connector)
