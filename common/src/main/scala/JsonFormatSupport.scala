import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

// TODO change id to long

abstract class VehicleBuilder(
    id: String,
    heading: Double,
    latitude: Double,
    longitude: Double,
    run_id: String,
    route_id: String,
    seconds_since_report: Int,
    predictable: Boolean
)

final case class Vehicle(
    id: String,
    heading: Double,
    latitude: Double,
    longitude: Double,
    run_id: String,
    route_id: String,
    seconds_since_report: Int,
    predictable: Boolean
) extends VehicleBuilder(
      id: String,
      heading,
      latitude,
      longitude,
      run_id,
      route_id,
      seconds_since_report,
      predictable
    )

case class TiledVehicle(
    id: String,
    heading: Double,
    latitude: Double,
    longitude: Double,
    run_id: String,
    route_id: String,
    seconds_since_report: Int,
    predictable: Boolean,
    tile: (Int, Int)
) extends VehicleBuilder(
      id: String,
      heading,
      latitude,
      longitude,
      run_id,
      route_id,
      seconds_since_report,
      predictable
    )

final case class VehicleList(items: List[Vehicle])

final case class TiledVehicles(items: List[TiledVehicle])

final case class AllVehicles(items: List[String])

trait JsonFormatSupport extends SprayJsonSupport {

  import spray.json.DefaultJsonProtocol._

  implicit val vehicle       = jsonFormat8(Vehicle)
  implicit val vehicleList   = jsonFormat1(VehicleList)
  implicit val tiledVehicle  = jsonFormat9(TiledVehicle)
  implicit val tiledVehicles = jsonFormat1(TiledVehicles)
  implicit val allVehicles   = jsonFormat1(AllVehicles)
}
