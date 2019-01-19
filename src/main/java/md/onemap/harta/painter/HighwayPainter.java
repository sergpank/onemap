package md.onemap.harta.painter;

import md.onemap.harta.db.gis.entity.Way;
import md.onemap.harta.drawer.AbstractDrawer;
import md.onemap.harta.drawer.AwtDrawer;
import md.onemap.harta.geometry.*;
import md.onemap.harta.osm.Highway;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.tile.Palette;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.text.Font;

import java.util.*;

/**
 * Created by sergpank on 03.03.2015.
 */
public class HighwayPainter extends AbstractPainter
{
  private static final Font FONT = new Font(Palette.HIGHWAY_FONT_NAME, Palette.HIGHWAY_FONT_SIZE);
  private static final FontMetrics fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(FONT);

  public HighwayPainter(AbstractProjector projector, BoundsXY bounds)
  {
    super(projector, bounds);
  }

  public void draw(AbstractDrawer drawer, Collection<Highway> highways, int level)
  {
    List<Label> labels = new ArrayList<>();
    List<Highway> highwayList = new ArrayList<>(highways);

    highwayList.sort(Comparator.comparingInt((Highway h) -> h.getHighwayType().getPriority()));

    // First draw road contour (by drawing wider roads)
    for (Highway highway : highwayList)
    {
      HighwayType highwayType = highway.getHighwayType();
      if (level > 16 && !isFootway(highwayType)) // I draw contour only for levels >= 16
      {
        CanvasPolygon polygon = createPolygon(highway.getNodes());
        drawer.setStrokeColor(highwayType.getBorderColor());
        drawer.setFillColor(highwayType.getBorderColor());
        shiftPolygon(polygon);
        drawer.drawPolyLine(polygon, highwayType.getWidth(projector, true), false);
      }
    }

    // Then draw road (Thicker road over the "contour" road)
    for (Highway highway : highwayList)
    {
      addLabel(labels, highway);
      CanvasPolygon polygon = createPolygon(highway.getNodes());
      if (level < 13)
      {
        shiftPolygon(polygon);
        drawer.drawPolyLine(polygon, 1, false);
      }
      else
      {
        HighwayType highwayType = highway.getHighwayType();
        drawer.setStrokeColor(highwayType.getSurfaceColor());
        drawer.setFillColor(highwayType.getSurfaceColor());
        shiftPolygon(polygon);
        drawer.drawPolyLine(polygon, highwayType.getWidth(projector, false), isFootway(highwayType));
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

  public void drawHighways(AwtDrawer drawer, Set<Way> highways, int level)
  {
    List<Label> labels = new ArrayList<>();
    List<Way> highwayList = new ArrayList<>(highways);

    highwayList.sort(Comparator.comparingInt((Way w) -> Highway.defineType(w.getTags().get("highway")).getPriority()));

    // This cycle adds additional countour to the road (like sideway-footwalk along along the roads)
    for (Way way : highwayList)
    {
      HighwayType highwayType = Highway.defineType(way.getTags().get("highway"));
      if (level > 16 && !isFootway(highwayType)) // I draw contour only for levels >= 16
      {
        CanvasPolygon polygon = createPolygon(way.getNodes());
        drawer.setStrokeColor(Palette.HIGHWAY_CONTOUR_COLOR);
        drawer.setFillColor(Palette.HIGHWAY_CONTOUR_COLOR);
        shiftPolygon(polygon);
        int width = highwayType.getWidth(projector, true);
        drawer.drawPolyLine(polygon, width * 2, false);
      }
    }

    // First draw road contour (by drawing wider roads)
    for (Way way : highwayList)
    {
      HighwayType highwayType = Highway.defineType(way.getTags().get("highway"));
      if (level > 16 && !isFootway(highwayType)) // I draw contour only for levels >= 16
      {
        CanvasPolygon polygon = createPolygon(way.getNodes());
        drawer.setStrokeColor(highwayType.getBorderColor());
        drawer.setFillColor(highwayType.getBorderColor());
        shiftPolygon(polygon);
        drawer.drawPolyLine(polygon, highwayType.getWidth(projector, true), false);
      }
    }

    // Then draw road (Thicker road over the "contour" road)
    for (Way way : highwayList)
    {
      addWayLabel(labels, way);
      CanvasPolygon polygon = createPolygon(way.getNodes());
      if (level < 13)
      {
        shiftPolygon(polygon);
        drawer.drawPolyLine(polygon, 1, false);
      }
      else
      {
        HighwayType highwayType = Highway.defineType(way.getTags().get("highway"));
        drawer.setStrokeColor(highwayType.getSurfaceColor());
        drawer.setFillColor(highwayType.getSurfaceColor());
        shiftPolygon(polygon);
        drawer.drawPolyLine(polygon, highwayType.getWidth(projector, false), isFootway(highwayType));
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

  private boolean isFootway(HighwayType highwayType)
  {
    return highwayType == HighwayType.pedestrian
        || highwayType == HighwayType.footway
        || highwayType == HighwayType.bridleway
        || highwayType == HighwayType.cycleway
        || highwayType == HighwayType.steps
        || highwayType == HighwayType.path;
  }

  private void addLabel(List<Label> labels, Highway highway)
  {
    BoundsXY bounds = highway.getBounds().toXY(projector);
    XYPoint minXY = shiftPoint(new XYPoint(bounds.getXmin(), bounds.getYmin()));
    XYPoint maxXY = shiftPoint(new XYPoint(bounds.getXmax(), bounds.getYmax()));

    XYPoint highwayCenter = new XYPoint((minXY.getX() + maxXY.getX()) / 2, (minXY.getY() + maxXY.getY()) / 2);
    if (highway.getName() != null && !highway.getName().isEmpty())
    {
      float height = fontMetrics.getLineHeight() / 2;
      float width = fontMetrics.computeStringWidth(highway.getName()) + highway.getName().length();
      Label label = new Label(highway.getName(), highwayCenter, height, width, highway.getNodes());
      labels.add(label);
    }
  }

  private void addWayLabel(List<Label> labels, Way way)
  {
    BoundsXY bounds = way.getBoundsLatLon().toXY(projector);
    XYPoint minXY = shiftPoint(new XYPoint(bounds.getXmin(), bounds.getYmin()));
    XYPoint maxXY = shiftPoint(new XYPoint(bounds.getXmax(), bounds.getYmax()));

    XYPoint highwayCenter = new XYPoint((minXY.getX() + maxXY.getX()) / 2, (minXY.getY() + maxXY.getY()) / 2);
    String name = way.getTags().get("name");
    if (name != null && !name.isEmpty())
    {
      float height = fontMetrics.getLineHeight() / 2;
      float width = fontMetrics.computeStringWidth(name) + name.length();
      Label label = new Label(name, highwayCenter, height, width, way.getNodes());
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
}
