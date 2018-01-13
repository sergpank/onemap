package md.onemap.harta.painter;

import md.onemap.harta.drawer.TileDrawer;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.geometry.CanvasPolygon;
import md.onemap.harta.osm.Waterway;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.tile.TilePalette;

import java.util.Collection;

/**
 * Created by serg on 06-Aug-16.
 */
public class WaterwayPainter extends AbstractPainter {

  public WaterwayPainter(AbstractProjector projector, BoundsXY bounds) {
    super(projector, bounds);
  }

  public void drawWaterways(TileDrawer drawer, Collection<Waterway> waterways, int level) {
    drawer.setStrokeColor(TilePalette.WATER_COLOR);

    for (Waterway waterway : waterways) {
      CanvasPolygon polygon = createPolygon(waterway);
      shiftPolygon(polygon);
      drawer.drawPolyLine(polygon, 3);
    }
  }
}
