package md.onemap.harta.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.io.StringWriter;
import java.util.Collection;
import java.util.stream.Stream;

public class SearchServlet extends HttpServlet
{
  // Necessary for verification if it is a house number: 12, 34a, 45/1
  // House number may contain not only digits, but it always starts with a digit
  public static final String STARTS_WITH_DIGIT = "^\\d.*";

  private static final Logger LOG = LoggerFactory.getLogger(SearchServlet.class);

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    try
    {
      resp.setContentType("application/json");
      resp.setCharacterEncoding("UTF-8");

      String key = req.getParameter("key");

      LOG.info("Looking for highway: \"{}\"", key);

      if (key != null && !(key.trim().isEmpty()))
      {
        String[] split = key.split(" ");
        if (split.length > 1
            && split[split.length - 1].trim().matches(STARTS_WITH_DIGIT))
        {
          // Most likely this is a Full Address (Street + House#)
          // In such case we should first find street and then try to find building

        }
        Collection<NormalizedHighway> highways = new StreetSearch().findStreets(key);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();//new Gson();
        String json = gson.toJson(highways);

        LOG.info("Search result: \"{}\"", json);

        PrintWriter writer = resp.getWriter();
        writer.write(json);
        writer.flush();
        writer.close();
      }
    }
    catch (Exception e)
    {
      LOG.error(e.getMessage());
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      LOG.error(sw.toString());
      throw(e);
    }
  }

  public static void main(String[] asdf)
  {
    Stream.of("aaa bbb ccc  1 2   3    ".split(" "))
        .filter(a -> a.length() > 0)
        .forEach(a -> System.out.printf("\"%s\"\n", a));
  }
}
