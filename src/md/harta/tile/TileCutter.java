package md.harta.tile;

import md.harta.geometry.Bounds;
import md.harta.geometry.LatLonPoint;
import md.harta.geometry.XYPoint;
import md.harta.projector.AbstractProjector;
import md.harta.util.ScaleCalculator;

/**
 * Created by sergpank on 24.05.15.
 */
public class TileCutter
{
  private AbstractProjector projector;
  private int tileSize;
  private double minLat;
  private double minLon;
  private double maxLat;
  private double maxLon;
  private XYPoint minXY;
  private XYPoint maxXY;

  private int minXindex;
  private int minYindex;
  private int maxXindex;
  private int maxYindex;

  //  Min lat = 45.460106
  //  Max lat = 48.490170
  //  Min lon = 26.621311
  //  Max lon = 30.163740
  public TileCutter(AbstractProjector projector, int tileSize, int level,
                    double minLat, double minLon, double maxLat, double maxLon)
  {
    if (level < ScaleCalculator.MIN_SCALE_LEVEL || level > ScaleCalculator.MAX_SCALE_LEVEL)
    {
      throw new IllegalArgumentException(String.format("Tile level should be in [%d, %d]: %d",
          ScaleCalculator.MIN_SCALE_LEVEL, ScaleCalculator.MAX_SCALE_LEVEL, level));
    }
    this.projector = projector;
    this.tileSize = tileSize;
    this.minLat = minLat;
    this.minLon = minLon;
    this.maxLat = maxLat;
    this.maxLon = maxLon;
  }

  public void cut()
  {
    minXY = projector.getXY(minLat, minLon);
    maxXY = projector.getXY(maxLat, maxLon);

    minXindex = (int) (minXY.getX() / tileSize);
    minYindex = (int) (maxXY.getY() / tileSize);

    maxXindex = (int) (maxXY.getX() / tileSize);
    maxYindex = (int) (minXY.getY() / tileSize);
  }

  /**
   * Tile index starts from 1
   * @param x tile index
   * @param y tile index
   * @return Bounding box of a specific tile in XY format
   */
  public Bounds getTileBounds(int x, int y)
  {
    XYPoint minXY = new XYPoint(x * tileSize, y * tileSize);
    XYPoint maxXY = new XYPoint((x + 1) * tileSize, (y + 1) * tileSize);
    LatLonPoint minLatLon = projector.getLatLon(minXY);
    LatLonPoint maxLatLon = projector.getLatLon(maxXY);

    return new Bounds(minXY.getX(), minXY.getY(), maxXY.getX(), maxXY.getY(),
        maxLatLon.getLat(), minLatLon.getLon(), minLatLon.getLat(), maxLatLon.getLon());
  }

  public int getMinTileXindex()
  {
    return minXindex;
  }

  public int getMaxTileXindex()
  {
    return maxXindex;
  }

  public int getMinTileYindex()
  {
    return minYindex;
  }

  public int getMaxTileYindex()
  {
    return maxYindex;
  }
}
