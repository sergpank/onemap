package md.harta.painter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import md.harta.drawer.AbstractDrawer;
import md.harta.geometry.Bounds;
import md.harta.geometry.CanvasPolygon;
import md.harta.geometry.GeometryUtil;
import md.harta.geometry.Label;
import md.harta.geometry.LatLonPoint;
import md.harta.geometry.Line;
import md.harta.geometry.XYPoint;
import md.harta.osm.Highway;
import md.harta.projector.AbstractProjector;
import md.harta.tile.TilePalette;

/**
 * Created by sergpank on 03.03.2015.
 */
public class HighwayPainter extends AbstractPainter
{
  public static final int ROAD_WIDTH_METERS = 8;

  public HighwayPainter(AbstractProjector projector, Bounds bounds)
  {
    super(projector, bounds);
  }

  public void drawHighways(AbstractDrawer drawer, Collection<Highway> highways, int level)
  {
    List<Label> labels = new ArrayList<>();
    double roadWidth = getRoadWidthPixels(projector, ROAD_WIDTH_METERS);
    for (Highway highway : highways)
    {
      addLabel(labels, highway);
      CanvasPolygon polygon = createPolygon(highway);
      if (level < 16)
      {
        drawer.setLineWidth(1);
        shiftPolygon(polygon);
        drawer.drawPolyLine(polygon);
      }
      else
      {
        drawer.setFillColor(TilePalette.HIGHWAY_COLOR);
        drawLinesAsPolygons(polygon, drawer, roadWidth);
      }
    }
    TextPainter textPainter = new TextPainter(projector, bounds);
    if (level > 15)
    {
      for (Label label : labels)
      {
        textPainter.paintHighwayLabel(drawer, label);
      }
    }
  }

  private void addLabel(List<Label> labels, Highway highway)
  {
    XYPoint minXY = shiftPoint(new XYPoint(highway.getBounds().getxMin(), highway.getBounds().getyMin()));
    XYPoint maxXY = shiftPoint(new XYPoint(highway.getBounds().getxMax(), highway.getBounds().getyMax()));

//    drawer.setFillColor(Color.RED);
//    drawer.setStrokeColor(Color.RED);
//    drawer.setLineWidth(1);
//    CanvasPolygon boundingRectangle = new CanvasPolygon(0,
//        new double[]{minXY.getX(), maxXY.getX(), maxXY.getX(), minXY.getX()},
//        new double[]{minXY.getY(), minXY.getY(), maxXY.getY(), maxXY.getY()});
//    drawer.drawPolyLine(boundingRectangle);

    XYPoint highwayCenter = new XYPoint((minXY.getX() + maxXY.getX()) / 2, (minXY.getY() + maxXY.getY()) / 2);
    Label label = new Label(highway.getName(), highwayCenter, TilePalette.FONT_NAME, TilePalette.FONT_SIZE);
    label.setHighway(highway);
    labels.add(label);
  }

  private void drawLinesAsPolygons(CanvasPolygon polygon, AbstractDrawer drawer, double roadWidth)
  {
    for (int i = 0; i < polygon.getPointsNumber() - 1; i++)
    {
      XYPoint startPoint = new XYPoint(polygon.getxPoints()[i], polygon.getyPoints()[i]);
      XYPoint endPoint = new XYPoint(polygon.getxPoints()[i + 1], polygon.getyPoints()[i + 1]);

        Line line = GeometryUtil.getLine(startPoint, endPoint);

        LatLonPoint latLonStart = projector.getLatLon(startPoint);
        LatLonPoint latLonEnd = projector.getLatLon(endPoint);

        drawRoadPolygon(drawer, roadWidth, line, latLonStart, latLonEnd);
        drawJunctionCircles(drawer, roadWidth, startPoint, endPoint, latLonStart, latLonEnd);
    }
  }

  private void drawRoadPolygon(AbstractDrawer drawer, double roadWidth, Line line, LatLonPoint latLonStart, LatLonPoint latLonEnd)
  {
    XYPoint[] startPoints = GeometryUtil.getPerpendicularPoints(line, latLonStart, roadWidth, projector);
    XYPoint[] endPoints = GeometryUtil.getPerpendicularPoints(line, latLonEnd, roadWidth, projector);

    double[] xPoints = new double[]
        {
            startPoints[0].getX(), startPoints[1].getX(), endPoints[1].getX(), endPoints[0].getX(), startPoints[0].getX()
        };
    double[] yPoints = new double[]
        {
            startPoints[0].getY(), startPoints[1].getY(), endPoints[1].getY(), endPoints[0].getY(), startPoints[0].getY()
        };

    shiftPoints(bounds.getxMin(), xPoints);
    shiftPoints(bounds.getyMin(), yPoints);

    drawer.setFillColor(TilePalette.HIGHWAY_COLOR);
    drawer.fillPolygon(xPoints, yPoints);

//    drawer.setStrokeColor(TilePalette.FONT_COLOR);
//    drawer.drawLine(new XYPoint(xPoints[0], yPoints[0]), new XYPoint(xPoints[3], yPoints[3]));
//    drawer.drawLine(new XYPoint(xPoints[1], yPoints[1]), new XYPoint(xPoints[2], yPoints[2]));
  }

  private void drawJunctionCircles(AbstractDrawer drawer, double roadWidth, XYPoint startPoint, XYPoint endPoint, LatLonPoint latLonStart, LatLonPoint latLonEnd)
  {
    double startDiameter = projector.getScale(latLonStart) * roadWidth;
    double endDiameter = projector.getScale(latLonEnd) * roadWidth;

    startPoint = shiftPoint(startPoint);
    endPoint = shiftPoint(endPoint);

    drawer.setFillColor(TilePalette.HIGHWAY_COLOR);
    drawer.fillOval(startPoint.getX() - startDiameter / 2.0, startPoint.getY() - startDiameter / 2.0, startDiameter, startDiameter);
    drawer.fillOval(endPoint.getX() - endDiameter / 2.0, endPoint.getY() - endDiameter / 2.0, endDiameter, endDiameter);
  }

  private double getRoadWidthPixels(AbstractProjector projector, int roadWidthMeters)
  {
    double roadWidth = GeometryUtil.DEGREES_IN_METER * roadWidthMeters;
    XYPoint center = projector.getXY(0, 0);
    XYPoint xyLon = projector.getXY(0, roadWidth);

    return Math.ceil(xyLon.getX() - center.getX());
  }
}
