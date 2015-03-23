package md.harta.painter;

import md.harta.geometry.CanvasPolygon;
import md.harta.osm.Bounds;
import md.harta.osm.OsmNode;
import md.harta.osm.OsmWay;
import md.harta.projector.AbstractProjector;
import md.harta.geometry.XYPoint;

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

  protected void shiftPoints(double[] points, double shift) {
    for(int i = 0; i < points.length; i++){
      points[i] = points[i] - shift;
    }
  }
}
