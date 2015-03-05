package md.harta.projector;

/**
 * Created by sergpank on 20.02.2015.
 */
public abstract class AbstractProjector {
  public static final int MAX_LAT = 90;
  public static final int MAX_LON = 180;

  protected double shift;
  protected double width;
  protected double height;
  protected double scale;

  public abstract Point getXY(double lat, double lon);

  public abstract Point getLonLat(double x, double y);

  public double getWidth() {
    return width;
  }

  public double getHeight() {
    return height;
  }
}
