package md.harta.painter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import md.harta.geometry.CanvasPolygon;
import md.harta.geometry.XYPoint;
import md.harta.geometry.Bounds;
import md.harta.osm.Building;
import md.harta.projector.AbstractProjector;

import java.util.Map;

/**
 * Created by sergpank on 03.03.2015.
 */
public class BuildingPainter extends AbstractPainter{
  private final Map<Long, Building> buildingMap;
  private final Font font;

  public BuildingPainter(Map<Long, Building> buildingMap, AbstractProjector projector, Bounds bounds) {
    super(projector, bounds);
    this.buildingMap = buildingMap;

    font = new Font("Arial", 12);
  }

  public void drawBuildings(GraphicsContext gc){
    gc.setFont(font);
    for (Map.Entry<Long, Building> entry : buildingMap.entrySet()) {
      CanvasPolygon polygon = createPolygon(entry.getValue());
      gc.setFill(new Color(233.0 / 255, 229.0 / 255, 220.0 / 255, 1));
      shiftPoints(bounds.getxMin(), polygon.getxPoints());
      shiftPoints(bounds.getyMin(), polygon.getyPoints());
      gc.fillPolygon(polygon.getxPoints(), polygon.getyPoints(), polygon.getPointsNumber());
      gc.strokePolyline(polygon.getxPoints(), polygon.getyPoints(), polygon.getPointsNumber());
      drawHouseNumber(gc, polygon);
    }
  }

  private void drawHouseNumber(GraphicsContext gc, CanvasPolygon polygon) {
    String houseNumber = buildingMap.get(polygon.getId()).getHouseNumber();
    if (houseNumber != null) {
      gc.setFill(Color.BLACK);
      XYPoint xy = getLabelCenter(polygon, houseNumber, font.getName(), font.getSize());

      gc.fillText(houseNumber, xy.getX(), xy.getY());
    }
  }
}
