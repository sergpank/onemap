package md.onemap.harta.painter;

import md.onemap.harta.db.gis.entity.Node;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.geometry.CanvasPolygon;
import md.onemap.harta.geometry.XYPoint;
import md.onemap.harta.projector.AbstractProjector;

import java.util.List;

/**
 * Created by sergpank on 03.03.2015.
 */
public class AbstractPainter {
  protected BoundsXY bounds;
  protected AbstractProjector projector;

  public AbstractPainter(AbstractProjector projector, BoundsXY bounds) {
    this.projector = projector;
    this.bounds = bounds;
  }

  protected CanvasPolygon createPolygon(List<Node> nodes) {
    double[] xPoints = new double[nodes.size()];
    double[] yPoints = new double[nodes.size()];
    for (int i = 0; i < nodes.size(); i++){
      Node node = nodes.get(i);
      XYPoint xy = projector.getXY(node.getLat(), node.getLon());
      xPoints[i] = xy.getX();
      yPoints[i] = xy.getY();
    }
    return new CanvasPolygon(xPoints, yPoints);
  }

  protected void shiftPoints(double shift, double[] points) {
    for(int i = 0; i < points.length; i++){
      points[i] = points[i] - shift;
    }
  }

  protected XYPoint shiftPoint(XYPoint point)
  {
    return new XYPoint(point.getX() - bounds.getXmin(), point.getY() - bounds.getYmin());
  }

  protected void shiftPolygon(CanvasPolygon polygon)
  {
    for (int i = 0; i < polygon.getxPoints().length; i++)
    {
      polygon.getxPoints()[i] = polygon.getxPoints()[i] - bounds.getXmin();
      polygon.getyPoints()[i] = polygon.getyPoints()[i] - bounds.getYmin();
    }
  }

  protected XYPoint getLabelCenter(CanvasPolygon polygon, String label, float stringWidth, float stringHeight) {
    double minX = Double.MAX_VALUE;
    double maxX = Double.MIN_VALUE;
    double minY = Double.MAX_VALUE;
    double maxY = Double.MIN_VALUE;
    for (double x : polygon.getxPoints()){
      if (x > maxX){
        maxX = x;
      }
      if (x < minX){
        minX = x;
      }
    }
    for (double y : polygon.getyPoints()){
      if (y > maxY){
        maxY = y;
      }
      if (y < minY){
        minY = y;
      }
    }

    return getLabelCenter(label, stringWidth, stringHeight, new XYPoint((minX + maxX) / 2, (minY + maxY) / 2));
  }

  public XYPoint getLabelCenter(String label, float stringWidth, float stringHeight, XYPoint center)
  {
    float xShift = stringWidth / 2;
    float yShift = stringHeight / 2;

    return new XYPoint(center.getX() - xShift, center.getY() + yShift);
  }
}
