package md.onemap.harta.tile;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.geometry.LatLonPoint;
import md.onemap.harta.geometry.XYPoint;
import md.onemap.harta.projector.AbstractProjector;

/**
 * Created by sergpank on 16/01/2018
 */
public class TileBoundsCalculator
{
  private int tileSize;
  private AbstractProjector projector;

  public TileBoundsCalculator(int tileSize, AbstractProjector projector)
  {
    this.tileSize = tileSize;
    this.projector = projector;
  }

  /**
   * Tile index starts from 1
   * @param x tile index
   * @param y tile index
   * @return Bounding box of a specific tile in XY format
   */
  // TODO cover it with unit tests
  public BoundsLatLon getTileBounds(int x, int y)
  {
    XYPoint minXY = new XYPoint(x * tileSize, (y -1) * tileSize);
    XYPoint maxXY = new XYPoint((x + 1) * tileSize, y * tileSize);
    LatLonPoint min = projector.getLatLon(minXY);
    LatLonPoint max = projector.getLatLon(maxXY);

    return new BoundsLatLon(min.getLat(), min.getLon(), max.getLat(), max.getLon());
  }

  public AbstractProjector getProjector()
  {
    return projector;
  }
}
