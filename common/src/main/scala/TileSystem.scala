class TileSystem {

  val EarthRadius  = 6378137
  val MinLatitude  = -85.05112878
  val MaxLatitude  = 85.05112878
  val MinLongitude = -180
  val MaxLongitude = 180

  private def clip(n: Double, minValue: Double, maxValue: Double): Double =
    Math.min(Math.max(n, minValue), maxValue)

  def mapSize(levelOfDetail: Int): Int =
    256 << levelOfDetail

  def groundResolution(latitude: Double, levelOfDetail: Int): Double = {
    val _latitude = clip(latitude, MinLatitude, MaxLatitude)
    Math.cos(_latitude * Math.PI / 180) * 2 * Math.PI * EarthRadius / mapSize(
      levelOfDetail
    )
  }

  def mapScale(latitude: Double, levelOfDetail: Int, screenDpi: Int): Double =
    groundResolution(latitude, levelOfDetail) * screenDpi / 0.0254

  def latLongToPixelXY(
      latitude: Double,
      longitude: Double,
      levelOfDetail: Int
  ): (Int, Int) = {
    val _latitude  = clip(latitude, MinLatitude, MaxLatitude)
    val _longitude = clip(longitude, MinLongitude, MaxLongitude)

    val x: Double           = (_longitude + 180) / 360
    val sinLatitude: Double = Math.sin(_latitude * Math.PI / 180)
    val y: Double           = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI)

    val _mapSize: Int = mapSize(levelOfDetail)
    val pixelX: Int   = clip(x * _mapSize + 0.5, 0, _mapSize - 1).toInt
    val pixelY: Int   = clip(y * _mapSize + 0.5, 0, _mapSize - 1).toInt

    (pixelX, pixelY)
  }

  def pixelXYToLatLong(
      pixelX: Int,
      pixelY: Int,
      levelOfDetail: Int
  ): (Double, Double) = {
    val _mapSize = mapSize(levelOfDetail)
    val x        = (clip(pixelX, 0, _mapSize - 1) / _mapSize) - 0.5
    val y        = 0.5 - (clip(pixelY, 0, _mapSize - 1) / _mapSize)

    val latitude  = 90 - 360 * Math.atan(Math.exp(-y * 2 * Math.PI)) / Math.PI
    val longitude = 360 * x

    (latitude, longitude)
  }

  def pixelXYToTileXY(pixelX: Int, pixelY: Int): (Int, Int) = {
    val tileX = pixelX / 256
    val tileY = pixelY / 256

    (tileX, tileY)
  }

  def tileXYToPixelXY(tileX: Int, tileY: Int): (Int, Int) = {
    val pixelX = tileX * 256
    val pixelY = tileY * 256

    (pixelX, pixelY)
  }

  def latLongToTileXY(latitude: Double, longitude: Double, levelOfDetail: Int): (Int, Int) = {
    val (pixelX, pixelY) = latLongToPixelXY(latitude, longitude, levelOfDetail)
    val (tileX, tileY)   = pixelXYToTileXY(pixelX, pixelY)

    (tileX, tileY)
  }

  def tileXYToQuadKey(tileX: Int, tileY: Int, levelOfDetail: Int): String = {
    val digits = levelOfDetail to 1 by -1 map { level: Int ⇒
      var digit = 0
      val mask  = 1 << (level - 1)

      if ((tileX & mask) != 0) {
        digit += 1
      }

      if ((tileY & mask) != 0) {
        digit += 2
      }

      digit.toString
    }
    digits.foldLeft("")(_ + _)
  }

  def QuadKeyToTileXY(quadKey: String): (Int, Int, Int) = {
    var tileX, tileY  = 0
    val levelOfDetail = quadKey.length

    levelOfDetail to 1 by -1 map { level ⇒
      val mask = 1 << (level - 1)

      quadKey(level - 1) match {
        case '0' ⇒ {}
        case '1' ⇒ tileX |= mask
        case '2' ⇒ tileY |= mask
        case '3' ⇒ {
          tileX |= mask
          tileY |= mask
        }
        case _ ⇒ throw new Exception("Invalid QuadKey digit sequence.")
      }
    }
    (tileX, tileY, levelOfDetail)
  }

}
