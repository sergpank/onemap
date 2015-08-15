package md.harta.painter;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.text.Font;
import md.harta.geometry.CanvasPolygon;
import md.harta.geometry.XYPoint;
import md.harta.geometry.Bounds;
import md.harta.osm.OsmNode;
import md.harta.osm.OsmWay;
import md.harta.projector.AbstractProjector;

/**
 * Created by sergpank on 03.03.2015.
 */
public class AbstractPainter {
  protected Bounds bounds;
  protected AbstractProjector projector;

  public AbstractPainter(AbstractProjector projector, Bounds bounds) {
    this.projector = projector;
    this.bounds = bounds;
  }

  protected CanvasPolygon createPolygon(OsmWay way) {
    double[] xPoints = new double[way.getNodes().size()];
    double[] yPoints = new double[way.getNodes().size()];
    for (int i = 0; i < way.getNodes().size(); i++){
      OsmNode node = way.getNodes().get(i);
      XYPoint xy = projector.getXY(node.getLat(), node.getLon());
      xPoints[i] = xy.getX();
      yPoints[i] = xy.getY();
    }
    return new CanvasPolygon(way.getId(), xPoints, yPoints);
  }

  protected void shiftPoints(double shift, double[] points) {
    for(int i = 0; i < points.length; i++){
      points[i] = points[i] - shift;
    }
  }

  protected XYPoint shiftPoint(XYPoint point)
  {
    return new XYPoint(point.getX() - bounds.getxMin(), point.getY() - bounds.getyMin());
  }

  protected void shiftPolygon(CanvasPolygon polygon)
  {
    for (int i = 0; i < polygon.getxPoints().length; i++)
    {
      polygon.getxPoints()[i] = polygon.getxPoints()[i] - bounds.getxMin();
      polygon.getyPoints()[i] = polygon.getyPoints()[i] - bounds.getyMin();
    }
  }

  protected XYPoint getLabelCenter(CanvasPolygon polygon, String label, String fontName, double fontSize) {
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

    return getLabelCenter(label, fontName, fontSize, new XYPoint((minX + maxX) / 2, (minY + maxY) / 2));
  }

  public XYPoint getLabelCenter(String label, String fontName, double fontSize, XYPoint center)
  {
    FontMetrics fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(new Font(fontName, fontSize));

    float xShift = fontMetrics.computeStringWidth(label) / 2;
    float yShift = fontMetrics.getLineHeight() / 2;

    return new XYPoint(center.getX() - xShift, center.getY() + yShift);
  }
}
