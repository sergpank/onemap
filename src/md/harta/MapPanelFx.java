package md.harta;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import md.harta.osm.Bounds;
import md.harta.painter.BuildingPainter;
import md.harta.painter.HighwayPainter;
import md.harta.projector.AbstractProjector;
import md.harta.projector.SimpleProjector;
import md.harta.util.OsmLoader;

/**
 * Created by sergpank on 25.02.2015.
 */
public class MapPanelFx extends Application {

  public static final int EQUATOR_LENGTH = 3000;//AbstractProjector.EARTH_RADIUS_M * 2;
  public static final String OSM_PATH = "Moldova.osm";//"/home/sergpank/Downloads/map.osm";
  private Bounds bounds;
  private OsmLoader osmLoader;
  private int radius;

  public static void main(String[] args) {
    launch();
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    osmLoader = new OsmLoader();
    primaryStage.setScene(new Scene(createContent()));
    primaryStage.show();
  }

  private Parent createContent() {
    VBox root = new VBox();
    root.setPrefSize(800, 500);

    HBox menuBar = new HBox();
    Button updateButton = new Button("Update map");
    TextField scaleField = new TextField(Integer.toString(EQUATOR_LENGTH));
    TextField pathField = new TextField(OSM_PATH);
    pathField.setPrefWidth(300);
    menuBar.getChildren().addAll(updateButton, new Label("Radius: "),
        scaleField, new Label("  OSM file: "), pathField);

    Canvas canvas = new Canvas();

    updateButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        updateMap(scaleField, canvas, pathField);
      }
    });
    ScrollPane sp = new ScrollPane();
    sp.setContent(canvas);
    root.getChildren().addAll(menuBar, sp);
    return root;
  }

  private void updateMap(TextField scaleField, Canvas canvas, TextField pathField) {
    System.out.println("CLEAR!");
    String text = scaleField.getText();
    System.out.println("Radius = \"" + text + "\"");
    canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    radius = Integer.valueOf(text);
    String dataPath = pathField.getText();
    osmLoader.load(dataPath);

    drawMap(canvas);
  }

  private void setBounds(AbstractProjector projector) {
    this.bounds = new Bounds(projector,
        osmLoader.getMaxLat(), osmLoader.getMinLon(), osmLoader.getMinLat(), osmLoader.getMaxLon());
  }

  private void drawMap(Canvas canvas) {
    AbstractProjector projector = new SimpleProjector(radius);//new MercatorProjector(radius, 85);
    setBounds(projector);
    canvas.setHeight(bounds.getyMax() - bounds.getyMin());
    canvas.setWidth(bounds.getxMax() - bounds.getxMin());

    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.setFill(new Color(176.0/256, 210.0/256, 255.0/256, 1));
    gc.fillRect(0, 0, bounds.getxMax(), bounds.getyMax());

    gc.setStroke(Color.BLACK);
    gc.setLineWidth(2);

    new HighwayPainter(osmLoader.getHighways(), projector, bounds).drawHighways(gc);
    new BuildingPainter(osmLoader.getBuildings(), projector, bounds).drawBuildings(gc);

    System.out.println("DRAW!\n");
  }
}
