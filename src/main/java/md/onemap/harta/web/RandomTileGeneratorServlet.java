package md.onemap.harta.web;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * Created by sergpank on 01/07/2017.
 */
public class RandomTileGeneratorServlet extends HttpServlet
{
  public static int TILE_SIZE = 512;

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    response.setContentType("image/png");

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
    String str = String.format("x='%s'; y='%s'; z='%s'", x, y, z);
    graphics.drawString(str, 10, 128);

    graphics.drawLine(1, 1, 1, 255);
    graphics.drawLine(1, 255, 255, 255);
    graphics.drawLine(255, 255, 255, 1);
    graphics.drawLine(255, 1, 1, 1);

    ImageIO.write(bi, "PNG", response.getOutputStream());
  }
}
