package md.harta.painter;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import md.harta.geometry.CanvasPolygon;
import md.harta.osm.Bounds;
import md.harta.osm.Building;
import md.harta.projector.AbstractProjector;
import md.harta.geometry.XYPoint;

import java.util.Map;

/**
 * Created by sergpank on 03.03.2015.
 */
public class BuildingPainter extends AbstractPainter{
  private final Map<Long, Building> buildingMap;
  private final FontMetrics fontMetrics;
  private final Font font;

  public BuildingPainter(Map<Long, Building> buildingMap, AbstractProjector projector, Bounds bounds) {
    super(projector, bounds);
    this.buildingMap = buildingMap;

    font = new Font("Arial", 12);
    fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(font);
  }

  public void drawBuildings(GraphicsContext gc){
    gc.setFont(font);
    for (Map.Entry<Long, Building> entry : buildingMap.entrySet()) {
      CanvasPolygon polygon = createPolygon(entry.getValue());
      gc.setFill(new Color(233.0 / 255, 229.0 / 255, 220.0 / 255, 1));
      shiftPoints(polygon.getxPoints(), bounds.getxMin());
      shiftPoints(polygon.getyPoints(), bounds.getyMin());
      gc.fillPolygon(polygon.getxPoints(), polygon.getyPoints(), polygon.getPointsNumber());
      gc.strokePolyline(polygon.getxPoints(), polygon.getyPoints(), polygon.getPointsNumber());
      drawHouseNumber(gc, polygon, font);
    }
  }

  private void drawHouseNumber(GraphicsContext gc, CanvasPolygon polygon, Font font) {
    String houseNumber = buildingMap.get(polygon.getId()).getHouseNumber();
    if (houseNumber != null) {
      gc.setFill(Color.BLACK);
      XYPoint xy = getCenter(polygon, houseNumber);

      gc.fillText(houseNumber, xy.getX(), xy.getY());
      new Text(1, 2, "aaa");
    }
  }

  private XYPoint getCenter(CanvasPolygon polygon, String houseNumber) {
    double minX = Double.MAX_VALUE;
    double maxX = Double.MIN_VALUE;
    double minY = Double.MAX_VALUE;
    double maxY = Double.MIN_VALUE;
    for (double x : polygon.getxPoints()){
      if (x > maxX){
        maxX = x;
      }
      if (x < minX){
        minX = x;
      }
    }
    for (double y : polygon.getyPoints()){
      if (y > maxY){
        maxY = y;
      }
      if (y < minY){
        minY = y;
      }
    }

    float xShift = fontMetrics.computeStringWidth(houseNumber) / 2;
    float yShift = fontMetrics.getLineHeight() / 2;

    return new XYPoint((minX + maxX) / 2 - xShift, (minY + maxY) / 2 + yShift);
  }
}
