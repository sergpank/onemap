package md.onemap.harta.painter;

import md.onemap.harta.drawer.AwtDrawer;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.geometry.CanvasPolygon;
import md.onemap.harta.osm.Waterway;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.tile.Palette;

import java.util.Collection;

/**
 * Created by serg on 06-Aug-16.
 */
public class WaterwayPainter extends AbstractPainter {

  public WaterwayPainter(AbstractProjector projector, BoundsXY bounds) {
    super(projector, bounds);
  }

  public void drawWaterways(AwtDrawer drawer, Collection<Waterway> waterways, int level) {
    drawer.setStrokeColor(Palette.WATER_COLOR);

    for (Waterway waterway : waterways) {
      CanvasPolygon polygon = createPolygon(waterway);
      shiftPolygon(polygon);
      drawer.drawPolyLine(polygon, 3);
    }
  }
}
