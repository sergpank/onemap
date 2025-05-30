package md.onemap.harta.tile;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.geometry.XYPoint;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.util.ScaleCalculator;

/**
 * Created by sergpank on 24.05.15.
 */
public class TileCutter
{
  private AbstractProjector projector;
  private int tileSize;

  private TileBoundsCalculator boundsCalculator;

  private double minLat;
  private double minLon;
  private double maxLat;
  private double maxLon;

  private int minXindex;
  private int minYindex;
  private int maxXindex;
  private int maxYindex;

  public TileCutter(AbstractProjector projector, int tileSize, int level, BoundsLatLon bounds)
  {
    if (level < ScaleCalculator.MIN_SCALE_LEVEL || level > ScaleCalculator.MAX_SCALE_LEVEL)
    {
      throw new IllegalArgumentException(String.format("Tile level should be in [%d, %d]: %d",
          ScaleCalculator.MIN_SCALE_LEVEL, ScaleCalculator.MAX_SCALE_LEVEL, level));
    }
    this.boundsCalculator = new TileBoundsCalculator(tileSize, projector);

    this.projector = projector;
    this.tileSize = tileSize;
    this.minLat = bounds.getMinLat();
    this.minLon = bounds.getMinLon();
    this.maxLat = bounds.getMaxLat();
    this.maxLon = bounds.getMaxLon();
  }

  public void cut()
  {
    XYPoint minXY = projector.getXY(minLat, minLon);
    XYPoint maxXY = projector.getXY(maxLat, maxLon);

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
  public BoundsLatLon getTileBounds(int x, int y)
  {
    return boundsCalculator.getTileBounds(x, y);
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
