package md.harta.painter;

import java.util.Collection;
import md.harta.drawer.AbstractDrawer;
import md.harta.geometry.Bounds;
import md.harta.geometry.CanvasPolygon;
import md.harta.geometry.XYPoint;
import md.harta.osm.Building;
import md.harta.projector.AbstractProjector;
import md.harta.tile.TilePalette;
import md.harta.util.TextUtil;

/**
 * Created by sergpank on 03.03.2015.
 */
public class BuildingPainter extends AbstractPainter{

  public BuildingPainter(AbstractProjector projector, Bounds bounds) {
    super(projector, bounds);
  }

  public void drawBuildings(AbstractDrawer drawer, Collection<Building> buildings, int level){
    drawer.setFont("Arial", 12);
    for (Building building : buildings) {
      CanvasPolygon polygon = createPolygon(building);
      shiftPoints(bounds.getxMin(), polygon.getxPoints());
      shiftPoints(bounds.getyMin(), polygon.getyPoints());

      drawer.setFillColor(TilePalette.BUILDING_COLOR);
      drawer.fillPolygon(polygon.getxPoints(), polygon.getyPoints());

      drawer.setStrokeColor(TilePalette.BUILDING_BORDER_COLOR);
      drawer.drawPolyLine(polygon, 1);

      if (level >= 17)
      {
        drawHouseNumber(drawer, polygon, building);
      }
    }
  }

  private void drawHouseNumber(AbstractDrawer drawer, CanvasPolygon polygon, Building building) {
    String houseNumber = building.getHouseNumber();
    if (houseNumber != null) {
      double w = building.getBounds().getxMax() - building.getBounds().getxMin();
      double h = building.getBounds().getyMax() - building.getBounds().getyMin();
      float stringWidth  = TextUtil.getStringWidth(houseNumber, TilePalette.BUILDING_FONT_NAME, TilePalette.BUILDING_FONT_SIZE);
      float stringHeight = TextUtil.getStringHeight(TilePalette.BUILDING_FONT_NAME, TilePalette.BUILDING_FONT_SIZE);
      if (((w * h) / (stringWidth * stringHeight)) >= 3)
      {
        drawer.setFillColor(TilePalette.BUILDING_FONT_COLOR);
        XYPoint xy = getLabelCenter(polygon, houseNumber, stringWidth, stringHeight);

        drawer.fillText(houseNumber, xy.getX(), xy.getY());
      }
    }
  }
}
