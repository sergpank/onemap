package md.harta;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.Collection;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import md.harta.drawer.TileDrawer;
import md.harta.geometry.Bounds;
import md.harta.geometry.XYPoint;
import md.harta.loader.AbstractLoader;
import md.harta.loader.OsmLoader;
import md.harta.osm.Highway;
import md.harta.painter.HighwayPainter;
import md.harta.projector.AbstractProjector;
import md.harta.projector.MercatorProjector;
import md.harta.tile.TileCutter;
import md.harta.tile.TileGenerator;
import md.harta.util.ScaleCalculator;

/**
 * Created by sergpank on 07.02.2015.
 */
public class MapPanelTile extends JPanel {

  public static final int LEVEL = 17;
  private AbstractProjector projector;
  private AbstractLoader loader;

  public static void main(String[] args)
  {
    double radiusForLevel = ScaleCalculator.getRadiusForLevel(LEVEL);
    MercatorProjector projector = new MercatorProjector(radiusForLevel, MercatorProjector.MAX_LAT);
    MapPanelTile map = new MapPanelTile(new OsmLoader(), projector);

//    map.loader = new DbLoader("debug");
//    map.loader.load("debug", projector);
    map.loader = new OsmLoader();
//    map.loader.load("osm/только_круг.osm", projector);
    map.loader.load("osm/греческая_площадь.osm", projector);
//    map.loader.load("osm/map.osm", projector);
//    map.loader.load("osm/test_data.osm", projector);

    JScrollPane scrollPane = new JScrollPane(map);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    JFrame frame = new JFrame("Tile drawer live debug");
    frame.add(scrollPane, BorderLayout.CENTER);
    frame.add(map.createControlPanel(), BorderLayout.WEST);
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1000, 700);
    frame.setVisible(true);
  }

  private JPanel createControlPanel()
  {
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(0, 2, 5, 5));

    TileCutter tileCutter = new TileCutter(projector, TileGenerator.TILE_SIZE, LEVEL, loader.getMinLat(), loader.getMinLon(), loader.getMaxLat(), loader.getMaxLon());
    tileCutter.cut();
    JComboBox<Integer> levelCombo = createCombo(ScaleCalculator.MIN_SCALE_LEVEL, ScaleCalculator.MAX_SCALE_LEVEL, LEVEL);
    JComboBox<Integer> xCombo = createCombo(tileCutter.getMinTileXindex(), tileCutter.getMaxTileXindex(), LEVEL);
    JComboBox<Integer> yCombo = createCombo(tileCutter.getMinTileYindex(), tileCutter.getMaxTileYindex(), LEVEL);

    int pos = 0;
    panel.add(new JLabel("DB / OSM : "), pos++);
    panel.add(new JTextField(), pos++);
    panel.add(new JLabel("Level : "), pos++);
    panel.add(levelCombo, pos++);
    panel.add(new JLabel("X : "), pos++);
    panel.add(xCombo, pos++);
    panel.add(new JLabel("Y : "), pos++);
    panel.add(yCombo, pos++);

    JButton repaintButton = new JButton("Repaint");
    panel.add(new JLabel("Button:"), pos++);
    panel.add(repaintButton, pos++);

    repaintButton.addActionListener(
        e -> {
          projector = new MercatorProjector(ScaleCalculator.getRadiusForLevel((int) levelCombo.getSelectedItem()), MercatorProjector.MAX_LAT);
          this.repaint();
        });

    return panel;
  }

  private JComboBox<Integer> createCombo(int min, int max, int level)
  {
    JComboBox<Integer> comboBox = new JComboBox<>();
    for (int i = min; i <= max; i++)
    {
      comboBox.addItem(i);
    }
    comboBox.setSelectedItem(level);
    return comboBox;
  }

  @Override
  public Dimension getPreferredSize() {
    Bounds bounds = new Bounds(projector, loader.getMinLat(), loader.getMinLon(), loader.getMaxLat(), loader.getMaxLon());
    double width = bounds.getxMax() - bounds.getxMin();
    double height = bounds.getyMax() - bounds.getyMin();

    return new Dimension((int)width, (int)height);
  }

  private void drawGrid(Bounds d, Graphics2D g)
  {
    g.setColor(Color.RED);
    for ( int x = 100; x < d.getxMax(); x +=100)
    {
      g.drawLine(x, 0, x, (int) d.getyMax());
    }

    for ( int y = 100; y < d.getyMax(); y +=100)
    {
      g.drawLine(0, y, (int) d.getxMax(), y);
    }
  }

  public MapPanelTile(OsmLoader loader, AbstractProjector projector) {
    this.loader = loader;
    this.projector = projector;
    System.out.println(String.format("%f x %f", projector.getWidth(), projector.getHeight()));
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    Collection<Highway> highways = loader.getHighways(projector).values();
    Bounds bounds = new Bounds(projector, loader.getMinLat(), loader.getMinLon(), loader.getMaxLat(), loader.getMaxLon());
    HighwayPainter highwayPainter = new HighwayPainter(projector, bounds);
    highwayPainter.drawHighways(new TileDrawer((Graphics2D) g), highways, LEVEL);

    drawGrid(bounds, (Graphics2D)g);
  }

  private void drawParallels(Graphics g) {
    g.setColor(Color.RED);
    for (int i = 0; i <= 90; i = i + 10){
      XYPoint start1 = projector.getXY(i, -180);
      XYPoint end1 = projector.getXY(i, 180);
      g.drawLine((int)start1.getX(), (int)start1.getY(), (int)end1.getX(), (int)end1.getY());
      XYPoint start2 = projector.getXY(-i, -180);
      XYPoint end2 = projector.getXY(-i, 180);
      g.drawLine((int)start2.getX(), (int)start2.getY(), (int)end2.getX(), (int)end2.getY());
      g.setColor(Color.BLACK);
    }
  }

  private void drawMeridians(Graphics g) {
    g.setColor(Color.RED);
    for (int i = 0; i <= 180; i = i + 20){
      XYPoint start1 = projector.getXY(90, i);
      XYPoint end1 = projector.getXY(-90, i);
      g.drawLine((int)start1.getX(), (int)start1.getY(), (int)end1.getX(), (int)end1.getY());
      XYPoint start2 = projector.getXY(90, -i);
      XYPoint end2 = projector.getXY(-90, -i);
      g.drawLine((int)start2.getX(), (int)start2.getY(), (int)end2.getX(), (int)end2.getY());
      g.setColor(Color.BLACK);
    }
  }
}
