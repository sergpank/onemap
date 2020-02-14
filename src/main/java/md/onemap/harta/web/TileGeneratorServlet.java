package md.onemap.harta.web;

import md.onemap.harta.db.statistics.TileStatistics;
import md.onemap.harta.db.statistics.VisitorStatistics;
import md.onemap.harta.tile.TileGenerator;
import md.onemap.harta.tile.TileGeneratorGIS;
import md.onemap.harta.util.Stopwatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by sergpank on 16/01/2018.
 */
public class TileGeneratorServlet extends HttpServlet
{
  private static final Logger LOG = LogManager.getLogger();

  private TileGenerator tileGenerator = new TileGeneratorGIS();
  private TileStatistics tileStatistics = new TileStatistics();
  private VisitorStatistics visitorStatistics = new VisitorStatistics();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    try
    {
      response.setContentType("image/png");

      String xParam = request.getParameter("x");
      String yParam = request.getParameter("y");
      String zParam = request.getParameter("z");

      LOG.info("Requested tile zxy: {} - {}:{}", zParam, xParam, yParam);

      if (xParam == null || yParam == null || zParam == null)
      {
        return;
      }

      int x = Integer.parseInt(xParam);
      int y = Integer.parseInt(yParam);
      int z = Integer.parseInt(zParam);

      doSomeStatistics(x, y, z, request.getRemoteAddr());

      Stopwatch.start();
      BufferedImage tile = tileGenerator.generateTileCached(x, y, z);
      Stopwatch.stop();

      LOG.info("Tile {} generation: {}", z + "-" + x + ":" + y, Stopwatch.pretty());

      try
      {
        ImageIO.write(tile, "PNG", response.getOutputStream());
      }
      catch (Throwable t)
      {
        // Do nothing.
        // Too many "java.io.IOException: Broken pipe" Exceptions, because user requests tile,
        // but then moves to other sector and did not read requested tile.
        // This is fine to have such exception and no need to report it in log and waste computing power.
      }
    }
    catch (Throwable t)
    {
      logError(request, t);
    }
  }

  private void doSomeStatistics(int x, int y, int z, String ip)
  {
    // Statistics should not block main flow.
    new Thread( () -> {
      tileStatistics.incrementTileCalls(z, x, y);
      visitorStatistics.incrementTileCalls(ip);
    }).start();
  }

  private void logError(HttpServletRequest request, Throwable t)
  {
    LOG.error("Exception: \"{}\" - Message: \"{}\" - Cause: \"{}\"", t.getClass().getName(), t.getMessage(), t.getCause());
    String collect = ((Set<Map.Entry>) request.getParameterMap().entrySet())
        .stream()
        .map(m -> m.getKey() + "::" + Arrays.toString((String[]) m.getValue()))
        .collect(Collectors.joining("; "));
    LOG.error("Request parameters: {}", collect);

    StringWriter sw = new StringWriter();
    t.printStackTrace(new PrintWriter(sw));
    LOG.error(sw.toString());
  }
}
