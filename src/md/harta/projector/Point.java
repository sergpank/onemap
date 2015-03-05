package md.harta.projector;

/**
 * Created by sergpank on 20.02.2015.
 */
public class Point {
  private double x;
  private double y;

  /**
   * Да, широта и долгота, в моей реализации не соотвествуют Х и У, мне так удобнее.
   *
   * @param x или широта
   * @param y или долгота
   */
  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   * @return x или широту
   */
  public double getX() {
    return x;
  }

  /**
   * @return у или долготу
   */
  public double getY() {
    return y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Point point = (Point) o;

    if (Double.compare(point.x, x) != 0) return false;
    if (Double.compare(point.y, y) != 0) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(x);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "Point{" +
        "x[or lon]=" + x +
        ", y[or lat]=" + y +
        '}';
  }
}
