package md.onemap.harta.painter;

import md.onemap.harta.drawer.AbstractDrawer;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.geometry.CanvasPolygon;
import md.onemap.harta.geometry.XYPoint;
import md.onemap.harta.osm.Building;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.tile.Palette;
import md.onemap.harta.util.TextUtil;

import java.util.Collection;

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
    drawer.setFont("Arial", 12);
    for (Building building : buildings)
    {
      CanvasPolygon polygon = createPolygon(building);
      shiftPoints(bounds.getXmin(), polygon.getxPoints());
      shiftPoints(bounds.getYmin(), polygon.getyPoints());

      drawer.setFillColor(Palette.BUILDING_COLOR);
      drawer.fillPolygon(polygon.getxPoints(), polygon.getyPoints());

      drawer.setStrokeColor(Palette.BUILDING_BORDER_COLOR);
      drawer.drawPolyLine(polygon, 1);

      if (level >= 17)
      {
        drawHouseNumber(drawer, polygon, building);
      }
    }
  }

  private void drawHouseNumber(AbstractDrawer drawer, CanvasPolygon polygon, Building building)
  {
    String houseNumber = building.getHouseNumber();
    if (houseNumber != null)
    {
      BoundsXY bounds = building.getBounds().toXY(projector);
      double w = bounds.getXmax() - bounds.getXmin();
      double h = bounds.getYmax() - bounds.getYmin();
      float stringWidth = TextUtil.getStringWidth(houseNumber, Palette.BUILDING_FONT_NAME, Palette.BUILDING_FONT_SIZE);
      float stringHeight = TextUtil.getStringHeight(Palette.BUILDING_FONT_NAME, Palette.BUILDING_FONT_SIZE);
      if (((w * h) / (stringWidth * stringHeight)) >= 3)
      {
        drawer.setFillColor(Palette.BUILDING_FONT_COLOR);
        XYPoint xy = getLabelCenter(polygon, houseNumber, stringWidth, stringHeight);

        drawer.fillText(houseNumber, xy.getX(), xy.getY());
      }
    }
  }
}
