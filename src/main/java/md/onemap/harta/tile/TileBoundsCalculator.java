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
   * @param tileExtension - tile corner may intersect road but not intersect road center,
   *                            in that case "cuts" will appear on the road, to avoid it, we have to extend tile bounds
   *                            for at least two widths of the road.
   *                      If tile should not be extended - just pass 0
   * @return Bounding box of a specific tile in XY format
   */
  // TODO cover it with unit tests
  public BoundsLatLon getTileBounds(int x, int y, int tileExtension)
  {
    XYPoint minXY = new XYPoint(x * tileSize - tileExtension, (y -1) * tileSize - tileExtension);
    XYPoint maxXY = new XYPoint((x + 1) * tileSize + tileExtension, y * tileSize + tileExtension);
    LatLonPoint minLatLon = projector.getLatLon(minXY);
    LatLonPoint maxLatLon = projector.getLatLon(maxXY);

    return new BoundsLatLon(minLatLon.getLat(), minLatLon.getLon(), maxLatLon.getLat(), maxLatLon.getLon());
  }

  public AbstractProjector getProjector()
  {
    return projector;
  }
}
