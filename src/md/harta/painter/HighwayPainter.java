package md.harta.painter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import md.harta.geometry.*;
import md.harta.osm.Bounds;
import md.harta.osm.Highway;
import md.harta.projector.MercatorProjector;

import java.util.Map;

/**
 * Created by sergpank on 03.03.2015.
 */
public class HighwayPainter extends AbstractPainter{
  public static final int ROAD_WIDTH = 1;
  private Map<Long, Highway> highways;

  public HighwayPainter(Map<Long, Highway> highways, MercatorProjector projector, Bounds bounds) {
    super(projector, bounds);
    this.highways = highways;
  }

  public void drawHighways(GraphicsContext gc) {
    gc.setFill(new Color(233.0 / 255, 229.0 / 255, 220.0 / 255, 1));
    gc.setLineWidth(1);
    for (Highway highway : highways.values()) {
      CanvasPolygon polygon = createPolygon(highway);
      shiftPoints(polygon.getxPoints(), bounds.getxMin());
      shiftPoints(polygon.getyPoints(), bounds.getyMin());
      gc.strokePolyline(polygon.getxPoints(), polygon.getyPoints(), polygon.getPointsNumber());
      drawLinesAsPolygons(polygon, gc);
    }
  }

  private void drawLinesAsPolygons(CanvasPolygon polygon, GraphicsContext gc) {
    for (int i = 0; i < polygon.getPointsNumber() - 1; i++){
      XYPoint startPoint = new XYPoint(polygon.getxPoints()[i], polygon.getyPoints()[i]);
      XYPoint endPoint = new XYPoint(polygon.getxPoints()[i + 1], polygon.getyPoints()[i + 1]);
      Line line = LineGeometry.getLine(startPoint, endPoint);
      LatLonPoint latLonStart = projector.getLatLon(startPoint);
      XYPoint[] startPoints = LineGeometry.getPerpendicularPoints(line, latLonStart, ROAD_WIDTH, projector);
      LatLonPoint latLonEnd = projector.getLatLon(endPoint);
      XYPoint[] endPoints = LineGeometry.getPerpendicularPoints(line, latLonEnd, ROAD_WIDTH, projector);
      double[] xPoints = new double[]{
        startPoints[0].getX(), startPoints[1].getX(), endPoints[1].getX(), endPoints[0].getX(), startPoints[0].getX()
      };
      double[] yPoints = new double[]{
        startPoints[0].getY(), startPoints[1].getY(), endPoints[1].getY(), endPoints[0].getY(), startPoints[0].getY()
      };
      gc.fillPolygon(xPoints, yPoints, 5);
      double startRadius = projector.getScale(latLonStart) * ROAD_WIDTH;
      gc.fillOval(startPoint.getX() - startRadius / 2, startPoint.getY() - startRadius / 2, startRadius, startRadius);
      double endRadius = projector.getScale(latLonEnd) * ROAD_WIDTH;
      gc.fillOval(endPoint.getX() - endRadius / 2, endPoint.getY() - startRadius / 2, endRadius, endRadius);
//      gc.strokePolyline(xPoints, yPoints, 5);
    }
  }
}
