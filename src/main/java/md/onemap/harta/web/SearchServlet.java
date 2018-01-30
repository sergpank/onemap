package md.onemap.harta.web;

import com.google.gson.Gson;
import md.onemap.harta.osm.NormalizedHighway;
import md.onemap.harta.search.StreetSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

public class SearchServlet extends HttpServlet
{
  private static final Logger LOG = LoggerFactory.getLogger(SearchServlet.class);

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");

    String key = req.getParameter("key");

    LOG.info("Looking for highway: \"{}\"", key);

    if (key != null && !(key.trim().isEmpty()))
    {
      Collection<NormalizedHighway> highways = new StreetSearch().findStreets(key);

      Gson gson = new Gson();
      String json = gson.toJson(highways);

      LOG.info("Search result: \"{}\"", json);

      PrintWriter writer = resp.getWriter();
      writer.write(json);
      writer.flush();
      writer.close();
    }
  }
}
