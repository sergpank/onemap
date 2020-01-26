package md.onemap.harta.painter;

import md.onemap.harta.db.gis.entity.Way;
import md.onemap.harta.drawer.AbstractDrawer;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.geometry.CanvasPolygon;
import md.onemap.harta.geometry.Label;
import md.onemap.harta.geometry.XYPoint;
import md.onemap.harta.osm.Building;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.tile.Palette;
import md.onemap.harta.util.TextUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by sergpank on 03.03.2015.
 */
public class BuildingPainter extends AbstractPainter
{

  public BuildingPainter(AbstractProjector projector, BoundsXY bounds)
  {
    super(projector, bounds);
  }

  public void draw(AbstractDrawer drawer, Collection<Building> buildings, int level)
  {
    drawer.setFont(Palette.HIGHWAY_FONT_NAME, Palette.HIGHWAY_FONT_SIZE);
    for (Building building : buildings)
    {
      CanvasPolygon polygon = createPolygon(building.getNodes());
      shiftPoints(bounds.getXmin(), polygon.getxPoints());
      shiftPoints(bounds.getYmin(), polygon.getyPoints());

      drawer.setFillColor(Palette.BUILDING_COLOR);
      drawer.fillPolygon(polygon.getxPoints(), polygon.getyPoints());

      drawer.setStrokeColor(Palette.BUILDING_BORDER_COLOR);
      drawer.drawPolyLine(polygon, 1, false);

      if (level >= Palette.BUILDING_LABEL_LEVEL)
      {
        createHouseNumberLabel(drawer, polygon, building.getHouseNumber(), building.getBounds().toXY(projector));
      }
    }
  }

  public List<Label> drawBuildings(AbstractDrawer drawer, Collection<Way> buildings, int level)
  {
    List<Label> labels = new ArrayList<>();
    drawer.setFont(Palette.HIGHWAY_FONT_NAME, Palette.HIGHWAY_FONT_SIZE);
    for (Way way : buildings)
    {
      CanvasPolygon polygon = createPolygon(way.getNodes());
      shiftPoints(bounds.getXmin(), polygon.getxPoints());
      shiftPoints(bounds.getYmin(), polygon.getyPoints());

      drawer.setFillColor(Palette.BUILDING_COLOR);
      drawer.fillPolygon(polygon.getxPoints(), polygon.getyPoints());

      drawer.setStrokeColor(Palette.BUILDING_BORDER_COLOR);
      drawer.drawPolyLine(polygon, 1, false);

      if (level >= Palette.BUILDING_LABEL_LEVEL)
      {
        String houseNumber = way.getTags().get("addr:housenumber");
        Label label = createHouseNumberLabel(drawer, polygon, houseNumber, way.getBoundsLatLon().toXY(projector));
        if (label != null)
        {
          labels.add(label);
        }
      }
    }
    return labels;
  }

  private Label createHouseNumberLabel(AbstractDrawer drawer, CanvasPolygon polygon, String houseNumber, BoundsXY bounds)
  {
    Label label = null;
    if (houseNumber != null)
    {
      double w = bounds.getXmax() - bounds.getXmin();
      double h = bounds.getYmax() - bounds.getYmin();
      float stringWidth = TextUtil.getStringWidth(houseNumber, Palette.BUILDING_FONT_NAME, Palette.BUILDING_FONT_SIZE, drawer.getGraphics());
      float stringHeight = TextUtil.getStringHeight(Palette.BUILDING_FONT_NAME, Palette.BUILDING_FONT_SIZE, drawer.getGraphics());
      if (w >= stringWidth || h >= stringHeight)
      {
        XYPoint xy = getLabelCenter(polygon, houseNumber, stringWidth, stringHeight);
        label = new Label(houseNumber, xy, 0, 0, null);
      }
    }
    return label;
  }
}
