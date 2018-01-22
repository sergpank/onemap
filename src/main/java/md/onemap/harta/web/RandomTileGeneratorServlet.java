package md.onemap.harta.web;

import md.onemap.harta.properties.TileGeneratorProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by sergpank on 01/07/2017.
 */
public class RandomTileGeneratorServlet extends HttpServlet
{
  private static Logger LOG = LoggerFactory.getLogger(RandomTileGeneratorServlet.class);
  public static int TILE_SIZE = new TileGeneratorProperties("properties/tile-generator-db.properties").tileSize();

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    response.setContentType("image/png");

    String collect = ((Set<Map.Entry>) request.getParameterMap().entrySet()).stream().map(m -> m.getKey() + "::" + Arrays.toString((String[])m.getValue())).collect(Collectors.joining("; "));
    LOG.info("Request parameters: {}", collect);

    String x = request.getParameter("x");
    String y = request.getParameter("y");
    String z = request.getParameter("z");

    Random random = new Random();

    int red   = x == null ? random.nextInt(256) : Integer.parseInt(x);
    int green = y == null ? random.nextInt(256) : Integer.parseInt(y);
    int blue  = z == null ? random.nextInt(256) : Integer.parseInt(z);

    BufferedImage bi = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = bi.createGraphics();

    graphics.setPaint(new Color(red, green, blue));
    graphics.fillRect(0, 0, TILE_SIZE, TILE_SIZE);

    graphics.setColor(new Color(255 - red, 255 - green, 255 - blue));
    String input = String.format("x='%s'; y='%s'; z='%s'", x, y, z);
    graphics.drawString(input, 10, TILE_SIZE / 2);
    String generated = String.format("r='%d'; g='%d'; b='%d", red, green, blue);
    graphics.drawString(generated, 10, TILE_SIZE / 2 + 32);

    graphics.drawLine(1, 1, 1, TILE_SIZE - 1);
    graphics.drawLine(1, TILE_SIZE - 1, TILE_SIZE - 1, TILE_SIZE - 1);
    graphics.drawLine(TILE_SIZE - 1, TILE_SIZE - 1, TILE_SIZE - 1, 1);
    graphics.drawLine(TILE_SIZE - 1, 1, 1, 1);

    ImageIO.write(bi, "PNG", response.getOutputStream());
  }
}
