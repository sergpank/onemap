package md.onemap.harta.painter;

import md.onemap.harta.db.gis.entity.Way;
import md.onemap.harta.drawer.AbstractDrawer;
import md.onemap.harta.drawer.AwtDrawer;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.geometry.CanvasPolygon;
import md.onemap.harta.geometry.Label;
import md.onemap.harta.geometry.XYPoint;
import md.onemap.harta.osm.Highway;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.tile.Palette;
import md.onemap.harta.util.TextUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Created by sergpank on 03.03.2015.
 */
public class HighwayPainter extends AbstractPainter
{
  private static final Logger log = LogManager.getLogger();

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
      addLabel(labels, highway, drawer.getGraphics());
      CanvasPolygon polygon = createPolygon(highway.getNodes());
      HighwayType highwayType = highway.getHighwayType();
      drawer.setStrokeColor(highwayType.getSurfaceColor());
      drawer.setFillColor(highwayType.getSurfaceColor());
      if (level < 13)
      {
        shiftPolygon(polygon);
        drawer.drawPolyLine(polygon, 1, false);
      }
      else
      {
        shiftPolygon(polygon);
        drawer.drawPolyLine(polygon, highwayType.getWidth(projector, false), isFootway(highwayType));
      }
    }

    if (level > Palette.STREET_LABEL_LEVEL)
    {
      TextPainter textPainter = new TextPainter(projector, bounds);
      for (Label label : labels)
      {
        textPainter.paintHighwayLabel(drawer, label);
      }
    }
  }

  public List<Label> drawHighways(AwtDrawer drawer, Set<Way> highways, int level)
  {
    List<Label> labels = new ArrayList<>();
    List<Way> highwayList = new ArrayList<>(highways);

    highwayList.sort(Comparator.comparingInt((Way w) -> Highway.defineType(w.getTags().get("highway")).getPriority()));

    // This cycle adds additional contour to the road (like sideway-footwalk along along the roads)
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
      addWayLabel(labels, way, drawer.getGraphics());
      CanvasPolygon polygon = createPolygon(way.getNodes());
      HighwayType highwayType = Highway.defineType(way.getTags().get("highway"));
      drawer.setStrokeColor(highwayType.getSurfaceColor());
      drawer.setFillColor(highwayType.getSurfaceColor());
      if (level < 13)
      {
        shiftPolygon(polygon);
        drawer.drawPolyLine(polygon, 1, false);
      }
      else
      {
        shiftPolygon(polygon);
        drawer.drawPolyLine(polygon, highwayType.getWidth(projector, false), isFootway(highwayType));
      }
    }

    return labels;
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

  private void addLabel(List<Label> labels, Highway highway, Graphics2D g)
  {
    BoundsXY bounds = highway.getBounds().toXY(projector);
    XYPoint minXY = shiftPoint(new XYPoint(bounds.getXmin(), bounds.getYmin()));
    XYPoint maxXY = shiftPoint(new XYPoint(bounds.getXmax(), bounds.getYmax()));

    XYPoint highwayCenter = new XYPoint((minXY.getX() + maxXY.getX()) / 2, (minXY.getY() + maxXY.getY()) / 2);
    if (highway.getName() != null && !highway.getName().isEmpty())
    {
      float height = TextUtil.getStringHeight(Palette.HIGHWAY_FONT_NAME, Palette.HIGHWAY_FONT_SIZE, g);//fontMetrics.getLineHeight() / 2;
      float width = TextUtil.getStringWidth(highway.getName(), Palette.HIGHWAY_FONT_NAME, Palette.HIGHWAY_FONT_SIZE, g) + highway.getName().length();
      Label label = new Label(highway.getName(), highwayCenter, height, width, highway.getNodes());
      labels.add(label);
    }
  }

  private void addWayLabel(List<Label> labels, Way way, Graphics2D g)
  {
    BoundsXY bounds = way.getBoundsLatLon().toXY(projector);
    XYPoint minXY = shiftPoint(new XYPoint(bounds.getXmin(), bounds.getYmin()));
    XYPoint maxXY = shiftPoint(new XYPoint(bounds.getXmax(), bounds.getYmax()));

    XYPoint highwayCenter = new XYPoint((minXY.getX() + maxXY.getX()) / 2, (minXY.getY() + maxXY.getY()) / 2);
    String name = way.getTags().get("name");
    if (name != null && !name.isEmpty())
    {
      float height = TextUtil.getStringHeight(Palette.HIGHWAY_FONT_NAME, Palette.HIGHWAY_FONT_SIZE, g);
      float width = TextUtil.getStringWidth(name, Palette.HIGHWAY_FONT_NAME, Palette.HIGHWAY_FONT_SIZE, g) + name.length();
      Label label = new Label(name, highwayCenter, height, width, way.getNodes());
      labels.add(label);
    }
  }
}
