package md.harta.painter;

import md.harta.drawer.TileDrawer;
import md.harta.geometry.Bounds;
import md.harta.geometry.CanvasPolygon;
import md.harta.osm.Waterway;
import md.harta.projector.AbstractProjector;
import md.harta.tile.TilePalette;

import java.util.Collection;

/**
 * Created by serg on 06-Aug-16.
 */
public class WaterwayPainter extends AbstractPainter {

  public WaterwayPainter(AbstractProjector projector, Bounds bounds) {
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
