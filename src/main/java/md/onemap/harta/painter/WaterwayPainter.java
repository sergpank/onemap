package md.onemap.harta.painter;

import md.onemap.harta.drawer.AbstractDrawer;
import md.onemap.harta.drawer.AwtDrawer;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.geometry.CanvasPolygon;
import md.onemap.harta.geometry.GeometryUtil;
import md.onemap.harta.osm.Waterway;
import md.onemap.harta.osm.Way;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.tile.Palette;

import java.util.Collection;
import java.util.Set;

/**
 * Created by serg on 06-Aug-16.
 */
public class WaterwayPainter extends AbstractPainter {

  public WaterwayPainter(AbstractProjector projector, BoundsXY bounds) {
    super(projector, bounds);
  }

  public void draw(AbstractDrawer drawer, Collection<Waterway> waterways, int level) {
    drawer.setStrokeColor(Palette.WATER_COLOR);

    for (Waterway waterway : waterways) {
      CanvasPolygon polygon = createPolygon(waterway.getNodes());
      drawer.setStrokeColor(Palette.WATER_COLOR);
      drawer.setFillColor(Palette.WATER_COLOR);
      shiftPolygon(polygon);
      drawer.drawPolyLine(polygon, (int)GeometryUtil.metersToPixels(projector, 8), false);
    }
  }

  public void drawWaterways(AwtDrawer drawer, Set<Way> waterways, int level)
  {
    drawer.setStrokeColor(Palette.WATER_COLOR);

    for (Way waterway : waterways) {
      CanvasPolygon polygon = createPolygon(waterway.getNodes());
      drawer.setStrokeColor(Palette.WATER_COLOR);
      drawer.setFillColor(Palette.WATER_COLOR);
      shiftPolygon(polygon);
      drawer.drawPolyLine(polygon, (int)GeometryUtil.metersToPixels(projector, 8), false);
    }
  }
}
