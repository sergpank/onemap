package md.onemap.harta;

import md.onemap.harta.drawer.AwtDrawer;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.geometry.XYPoint;
import md.onemap.harta.loader.AbstractLoader;
import md.onemap.harta.loader.OsmLoader;
import md.onemap.harta.osm.*;
import md.onemap.harta.painter.BuildingPainter;
import md.onemap.harta.painter.HighwayPainter;
import md.onemap.harta.painter.LandusePainter;
import md.onemap.harta.painter.LeisurePainter;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.projector.MercatorProjector;
import md.onemap.harta.tile.TileCutter;
import md.onemap.harta.util.ScaleCalculator;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;

/**
 * Created by sergpank on 07.02.2015.
 */
public class MapViewerSwing extends JPanel {

  private static final Logger LOG = LoggerFactory.getLogger(MapViewerSwing.class);

  public static int LEVEL = 18;
  public static String DATA_SOURCE = "osm/victory_park.osm";

  private static final int TILE_SIZE = 512;

  private AbstractProjector projector;
  private AbstractLoader loader;
  private Collection<Highway> highways;
  private Collection<Building> buildings;
  private Collection<Leisure> leisure;
  private Collection<Natural> nature;
  private Collection<Waterway> waterways;
  private Collection<Landuse> landuse;
  private JTextField dataField;

  public static void main(String[] args)
  {
    DOMConfigurator.configure("log4j.xml");

    double radiusForLevel = ScaleCalculator.getRadiusForLevel(LEVEL);
    MercatorProjector projector = new MercatorProjector(radiusForLevel, MercatorProjector.MAX_LAT);
    MapViewerSwing map = new MapViewerSwing(new OsmLoader(), projector);

//    map.loader = new PostgisLoader("botanica");

    map.loader = new OsmLoader();
    map.loader.load(DATA_SOURCE);

    map.highways = map.loader.getHighways().values();
    map.buildings = map.loader.getBuildings().values();
    map.leisure = map.loader.getLeisure().values();
//    map.nature = map.loader.getNature().values();
//    map.waterways = map.loader.getWaterways().values();
    map.landuse = map.loader.getLanduse().values();

    JScrollPane scrollPane = new JScrollPane(map);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    JFrame frame = new JFrame("Tile drawer live debug");
    frame.add(scrollPane, BorderLayout.CENTER);
    frame.add(map.createControlPanel(), BorderLayout.NORTH);
    map.dataField.setText(DATA_SOURCE);
    frame.pack();
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setSize(1000, 700);
    frame.setVisible(true);
  }

  private JPanel createControlPanel()
  {
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());

    TileCutter tileCutter = new TileCutter(projector, TILE_SIZE, LEVEL, loader.getBounds());
    tileCutter.cut();
    JComboBox<Integer> levelCombo = createCombo(ScaleCalculator.MIN_SCALE_LEVEL, ScaleCalculator.MAX_SCALE_LEVEL, LEVEL);

    int pos = 0;
    panel.add(new JLabel("DB / OSM : "), pos++);
    dataField = new JTextField();
    dataField.setPreferredSize(new Dimension(400, 30));
    panel.add(dataField, pos++);
    panel.add(new JLabel("Level : "), pos++);
    panel.add(levelCombo, pos++);

    JButton repaintButton = new JButton("Repaint");
    panel.add(new JLabel("Button:"), pos++);
    panel.add(repaintButton, pos++);

    repaintButton.addActionListener(e -> repaintMap(levelCombo));

    dataField.addKeyListener(new KeyAdapter(){
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) repaintMap(levelCombo);
      }
    });

    return panel;
  }

  private void repaintMap(JComboBox<Integer> levelCombo) {
    LEVEL = (int) levelCombo.getSelectedItem();
    projector = new MercatorProjector(ScaleCalculator.getRadiusForLevel(LEVEL), MercatorProjector.MAX_LAT);
    String data = dataField.getText();
    if (data != null && !data.isEmpty())
    {
      LOG.info("Loading map: " + data);
      loader.load(data);
      this.repaint();
    }
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
    BoundsXY bounds = new BoundsLatLon(loader.getMinLat(), loader.getMinLon(), loader.getMaxLat(), loader.getMaxLon()).toXY(projector);
    double width = bounds.getXmax() - bounds.getXmin();
    double height = bounds.getYmax() - bounds.getYmin();

    return new Dimension((int)width, (int)height);
  }

  public MapViewerSwing(OsmLoader loader, AbstractProjector projector) {
    this.loader = loader;
    this.projector = projector;
    System.out.println(String.format("%f x %f", projector.getWidth(), projector.getHeight()));
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    BoundsXY bounds = new BoundsLatLon(loader.getMinLat(), loader.getMinLon(), loader.getMaxLat(), loader.getMaxLon()).toXY(projector);

    LeisurePainter leisurePainter = new LeisurePainter(projector, bounds);
//    NaturePainter naturePainter = new NaturePainter(projector, bounds);
//    WaterwayPainter waterwayPainter = new WaterwayPainter(projector, bounds);
    LandusePainter landusePainter = new LandusePainter(projector, bounds);
    HighwayPainter highwayPainter = new HighwayPainter(projector, bounds);
    BuildingPainter buildingPainter = new BuildingPainter(projector, bounds);

    landusePainter.draw(new AwtDrawer((Graphics2D)g), landuse, LEVEL);
    leisurePainter.draw(new AwtDrawer((Graphics2D) g), leisure, LEVEL);
//    naturePainter.drawWater(new AwtDrawer((Graphics2D)g), nature, LEVEL);
//    waterwayPainter.draw(new AwtDrawer((Graphics2D)g), waterways, LEVEL);
    highwayPainter.draw(new AwtDrawer((Graphics2D) g), highways, LEVEL);
    buildingPainter.draw(new AwtDrawer((Graphics2D) g), buildings, LEVEL);
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
