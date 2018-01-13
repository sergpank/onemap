package md.onemap.harta;

import com.sun.javafx.collections.ImmutableObservableList;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import md.onemap.harta.drawer.FxDrawer;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.loader.AbstractLoader;
import md.onemap.harta.loader.OsmLoader;
import md.onemap.harta.painter.BuildingPainter;
import md.onemap.harta.painter.HighwayPainter;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.projector.MercatorProjector;
import md.onemap.harta.util.ScaleCalculator;

/**
 * Created by sergpank on 25.02.2015.
 */
public class MapPanelFx extends Application {

  public static String osmPath;//"/home/sergpank/Downloads/map.osm";
  public static AbstractLoader loader;
  private AbstractProjector projector;

  public static void main(String[] args) {
    osmPath =  "osm/ботанический_сад.osm";
    loader = new OsmLoader();
    launch();
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setScene(new Scene(createContent()));
    primaryStage.show();
  }

  public Parent createContent() {
    VBox root = new VBox();
    root.setPrefSize(800, 500);

    HBox menuBar = new HBox();
    Button updateButton = new Button("Update map");

    Integer[] levels = new Integer[ScaleCalculator.MAX_SCALE_LEVEL];
    for (int i = 1; i <= ScaleCalculator.MAX_SCALE_LEVEL; i++)
    {
      levels[i-1] = i;
    }
    ObservableList<Integer> scaleLevels = new ImmutableObservableList<>(levels);
    ComboBox<Integer> scaleCombo = new ComboBox<>();
    scaleCombo.setItems(scaleLevels);
    scaleCombo.setValue(16);

    TextField pathField = new TextField(osmPath);
    pathField.setPrefWidth(300);
    menuBar.getChildren().addAll(updateButton, new Label("Radius: "),
        scaleCombo, new Label("  OSM file: "), pathField);

    Canvas canvas = new Canvas();

    updateButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        String dataPath = pathField.getText();
        Integer level = scaleCombo.getValue();
        updateMap(level, canvas, dataPath);
      }
    });
    ScrollPane sp = new ScrollPane();
    sp.setContent(canvas);
    root.getChildren().addAll(menuBar, sp);
    return root;
  }

  private void updateMap(int level, Canvas canvas, String dataPath) {
    System.out.println("CLEAR!");
    System.out.println("Level = \"" + level + "\"");

    canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    loader.load(dataPath);

    drawMap(canvas, level);
  }

  private void drawMap(Canvas canvas, int level) {
    int radius = (int)ScaleCalculator.getRadiusForLevel(level);
    projector = new MercatorProjector(radius, 85);
    BoundsXY bounds = new BoundsLatLon(loader.getMinLat(), loader.getMinLon(), loader.getMaxLat(), loader.getMaxLon()).toXY(projector);

    canvas.setHeight(bounds.getYmax() - bounds.getYmin());
    canvas.setWidth(bounds.getXmax() - bounds.getXmin());

    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.setFill(new Color(176.0 / 256, 210.0 / 256, 255.0 / 256, 1));
    gc.fillRect(0, 0, bounds.getXmax(), bounds.getYmax());

    gc.setStroke(Color.BLACK);
    gc.setLineWidth(2);

    new HighwayPainter(projector, bounds).drawHighways(new FxDrawer(gc), loader.getHighways().values(), level);
    new BuildingPainter(projector, bounds).drawBuildings(new FxDrawer(gc), loader.getBuildings().values(), level);
//    new BorderPainter(projector, bounds).drawBorders(drawer, loader.getBorders(), 0, 0, null);

    System.out.println("DRAW!\n");
  }
}
