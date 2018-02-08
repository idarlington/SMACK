import com.outworkers.phantom.dsl._

import scala.concurrent.Future

abstract class VehiclesByTileModel extends Table[VehiclesByTileModel, TiledVehicle] {
  override def tableName: String = "vehicles_by_tile"

  object id extends StringColumn with ClusteringOrder {
    override lazy val name = "vehicle_id"
  }

  object tile                 extends Col[(Int, Int)] with PartitionKey
  object heading              extends DoubleColumn
  object latitude             extends DoubleColumn
  object longitude            extends DoubleColumn
  object run_id               extends OptionalStringColumn
  object route_id             extends StringColumn
  object seconds_since_report extends IntColumn
  object predictable          extends BooleanColumn

  def getByTile(tile: (Int, Int)): Future[List[TiledVehicle]] =
    select
      .where(_.tile eqs tile)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .fetch()
}

abstract class VehiclesModel extends Table[VehiclesModel, TiledVehicle] {
  override def tableName: String = "vehicles"

  object id extends StringColumn with PartitionKey {
    override lazy val name = "vehicle_id"
  }
  object tile                 extends Col[(Int, Int)] /**/
  object heading              extends DoubleColumn
  object latitude             extends DoubleColumn
  object longitude            extends DoubleColumn
  object run_id               extends OptionalStringColumn
  object route_id             extends StringColumn
  object seconds_since_report extends IntColumn
  object predictable          extends BooleanColumn

  def getVehicleById(id: String): Future[Option[TiledVehicle]] =
    select
      .where(_.id eqs id)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .one()

  def getVehicles(): Future[List[TiledVehicle]] =
    select.fetch()

  def getVehiclesID(): Future[List[String]] =
    select(_.id).fetch
}
