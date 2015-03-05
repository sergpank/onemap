package md.harta;

import md.harta.osm.OsmNode;
import md.harta.osm.OsmWay;
import md.harta.projector.AbstractProjector;
import md.harta.projector.Point;
import md.harta.util.OsmLoader;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by sergpank on 07.02.2015.
 */
public class MapPanel extends JPanel {

  private AbstractProjector projector;

  private OsmLoader loader;

  public MapPanel(OsmLoader loader, AbstractProjector projector) {
    this.loader = loader;
    this.projector = projector;
    System.out.println(String.format("%f x %f", projector.getWidth(), projector.getHeight()));
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    for (OsmWay way : loader.getBuildings().values()) {
      drawWay(g, way);
    }

//    drawParallels(g);
//    drawMeridians(g);

//    for (OsmNode node : loader.getNodeMap().values()) {
//      drawNode(g, node);
//    }
  }

  private void drawParallels(Graphics g) {
    g.setColor(Color.RED);
    for (int i = 0; i <= 90; i = i + 10){
      Point start1 = projector.getXY(i, -180);
      Point end1 = projector.getXY(i, 180);
      g.drawLine((int)start1.getX(), (int)start1.getY(), (int)end1.getX(), (int)end1.getY());
      Point start2 = projector.getXY(-i, -180);
      Point end2 = projector.getXY(-i, 180);
      g.drawLine((int)start2.getX(), (int)start2.getY(), (int)end2.getX(), (int)end2.getY());
      g.setColor(Color.BLACK);
    }
  }

  private void drawMeridians(Graphics g) {
    g.setColor(Color.RED);
    for (int i = 0; i <= 180; i = i + 20){
      Point start1 = projector.getXY(90, i);
      Point end1 = projector.getXY(-90, i);
      g.drawLine((int)start1.getX(), (int)start1.getY(), (int)end1.getX(), (int)end1.getY());
      Point start2 = projector.getXY(90, -i);
      Point end2 = projector.getXY(-90, -i);
      g.drawLine((int)start2.getX(), (int)start2.getY(), (int)end2.getX(), (int)end2.getY());
      g.setColor(Color.BLACK);
    }
  }

  private void drawNode(Graphics g, OsmNode node) {
    Point point = getPoint(node);
    g.fillOval((int) point.getX() - 2, (int) point.getY() - 2, 5, 5);
  }

  private void drawWay(Graphics g, OsmWay way) {
    List<OsmNode> nodes = way.getNodes();
    int[] xPoints = new int[nodes.size()];
    int[] yPoints = new int[nodes.size()];

    for (int i = 0; i < nodes.size(); i++) {
      OsmNode node = nodes.get(i);
      Point point = getPoint(node);
      xPoints[i] = (int) point.getX();
      yPoints[i] = (int) point.getY();
    }

    g.drawPolyline(xPoints, yPoints, nodes.size());
  }

  private Point getPoint(OsmNode node) {
    return projector.getXY(node.getLat(), node.getLon());
  }
}
