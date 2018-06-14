package md.onemap.harta.painter;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.text.Font;
import md.onemap.harta.drawer.AbstractDrawer;
import md.onemap.harta.geometry.*;
import md.onemap.harta.osm.Highway;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.tile.TilePalette;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by sergpank on 03.03.2015.
 */
public class HighwayPainter extends AbstractPainter
{
  public static final int ROAD_WIDTH_METERS = 6;
  private static final Font FONT = new Font(TilePalette.HIGHWAY_FONT_NAME, TilePalette.HIGHWAY_FONT_SIZE);
  private static final FontMetrics fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(FONT);

  public HighwayPainter(AbstractProjector projector, BoundsXY bounds)
  {
    super(projector, bounds);
  }

  public void drawHighways(AbstractDrawer drawer, Collection<Highway> highways, int level)
  {
    drawer.setStrokeColor(TilePalette.HIGHWAY_COLOR);
    drawer.setFillColor(TilePalette.HIGHWAY_COLOR);

    List<Label> labels = new ArrayList<>();
    List<CanvasPolygon> polygons = new ArrayList<>();

    // First draw road contour (by drawing wider roads)
    double roadWidth = getRoadWidthPixels(projector);
    for (Highway highway : highways)
    {
      addLabel(labels, highway);
      CanvasPolygon polygon = createPolygon(highway);
      polygons.add(polygon);
      if (level > 15)
      {
        drawer.setStrokeColor(TilePalette.HIGHWAY_BORDER_COLOR);
        drawer.setFillColor(TilePalette.HIGHWAY_BORDER_COLOR);
        drawLinesAsPolygons(polygon, drawer, roadWidth + 2);
      }
    }

    // Then draw road (Thicker road over the "contour" road)
    for (CanvasPolygon polygon : polygons)
    {
      if (level < 13)
      {
        drawer.setLineWidth(1);
        shiftPolygon(polygon);
        drawer.drawPolyLine(polygon, 1);
      }
      else
      {
        drawer.setStrokeColor(TilePalette.HIGHWAY_COLOR);
        drawer.setFillColor(TilePalette.HIGHWAY_COLOR);
        drawLinesAsPolygons(polygon, drawer, roadWidth);
      }
    }

    if (level > 15)
    {
      TextPainter textPainter = new TextPainter(projector, bounds);
      for (Label label : labels)
      {
        textPainter.paintHighwayLabel(drawer, label);
      }
    }
  }

  private void addLabel(List<Label> labels, Highway highway)
  {
    BoundsXY bounds = highway.getBounds().toXY(projector);
    XYPoint minXY = shiftPoint(new XYPoint(bounds.getXmin(), bounds.getYmin()));
    XYPoint maxXY = shiftPoint(new XYPoint(bounds.getXmax(), bounds.getYmax()));

//    drawer.setFillColor(Color.RED);
//    drawer.setStrokeColor(Color.RED);
//    drawer.setLineWidth(1);
//    CanvasPolygon boundingRectangle = new CanvasPolygon(0,
//        new double[]{minXY.getX(), maxXY.getX(), maxXY.getX(), minXY.getX()},
//        new double[]{minXY.getY(), minXY.getY(), maxXY.getY(), maxXY.getY()});
//    drawer.drawPolyLine(boundingRectangle);

    XYPoint highwayCenter = new XYPoint((minXY.getX() + maxXY.getX()) / 2, (minXY.getY() + maxXY.getY()) / 2);
    if (highway.getName() != null && !highway.getName().isEmpty())
    {
      float height = fontMetrics.getLineHeight();
      float width = fontMetrics.computeStringWidth(highway.getName()) + highway.getName().length();
      Label label = new Label(highway.getName(), highwayCenter, height, width);
      label.setHighway(highway);
      labels.add(label);
    }
  }

  private void drawLinesAsPolygons(CanvasPolygon polygon, AbstractDrawer drawer, double roadWidth)
  {
    for (int i = 0; i < polygon.getPointsNumber() - 1; i++)
    {
      XYPoint startPoint = new XYPoint(polygon.getxPoints()[i], polygon.getyPoints()[i]);
      XYPoint endPoint = new XYPoint(polygon.getxPoints()[i + 1], polygon.getyPoints()[i + 1]);

        Line line = new Line(startPoint, endPoint);

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

    shiftPoints(bounds.getXmin(), xPoints);
    shiftPoints(bounds.getYmin(), yPoints);

    drawer.fillPolygon(xPoints, yPoints);
  }

  private void drawJunctionCircles(AbstractDrawer drawer, double roadWidth, XYPoint startPoint, XYPoint endPoint, LatLonPoint latLonStart, LatLonPoint latLonEnd)
  {
    double startDiameter = projector.getScale(latLonStart) * roadWidth;
    double endDiameter = projector.getScale(latLonEnd) * roadWidth;

    startPoint = shiftPoint(startPoint);
    endPoint = shiftPoint(endPoint);

    drawer.fillOval(startPoint.getX() - startDiameter / 2.0, startPoint.getY() - startDiameter / 2.0, startDiameter, startDiameter);
    drawer.fillOval(endPoint.getX() - endDiameter / 2.0, endPoint.getY() - endDiameter / 2.0, endDiameter, endDiameter);
  }

  public static double getRoadWidthPixels(AbstractProjector projector)
  {
    double roadWidth = GeometryUtil.DEGREES_IN_METER * ROAD_WIDTH_METERS;
    XYPoint center = projector.getXY(0, 0);
    XYPoint xyLon = projector.getXY(0, roadWidth);

    return Math.ceil(xyLon.getX() - center.getX());
  }
}
