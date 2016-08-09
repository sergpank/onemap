package md.harta.painter;

import md.harta.drawer.TileDrawer;
import md.harta.geometry.Bounds;
import md.harta.geometry.CanvasPolygon;
import md.harta.osm.Landuse;
import md.harta.projector.AbstractProjector;
import md.harta.tile.TilePalette;
import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * Created by serg on 08-Aug-16.
 */
public class LandusePainter extends AbstractPainter{

  private static Logger log = Logger.getLogger("landuse_painter");

  public LandusePainter(AbstractProjector projector, Bounds bounds) {
    super(projector, bounds);
  }

  public void drawLanduse(TileDrawer drawer, Collection<Landuse> landuse, int level)
  {

    for (Landuse land : landuse) {
      switch (land.getType()) {
        case "grass":
          drawer.setFillColor(TilePalette.PARK_COLOR);
          break;
        case "greenhouse_horticulture":
          drawer.setFillColor(TilePalette.GREEN_HOUSE);
          break;
        default:
          log.error(String.format("Landuse type : \"%s\" is not supported for drawing", land.getType()));
          continue;
      }
      CanvasPolygon polygon = createPolygon(land);
      shiftPolygon(polygon);
      drawer.fillPolygon(polygon.getxPoints(), polygon.getyPoints());
    }

//    CanvasPolygon polygon = createPolygon(building);
//    shiftPoints(bounds.getxMin(), polygon.getxPoints());
//    shiftPoints(bounds.getyMin(), polygon.getyPoints());
//
//    drawer.fillPolygon(polygon.getxPoints(), polygon.getyPoints());
  }
}
