package md.onemap.experiments;

import javax.swing.*;
import java.awt.*;

/**
 * Created by sergpank on 08.02.2015.
 */
public class CirclePanel
{
  public static void main(String[] args)
  {
    JFrame frame = new JFrame("Circle");

    frame.add(new Panel());

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(500, 600);
    frame.setVisible(true);
  }

  private static class Panel extends JPanel
  {
    int radius = 100;

    @Override
    protected void paintComponent(Graphics g)
    {
      super.paintComponent(g);

//      for (int x = -100; x <= 100; x = x + 10)
//      {
//        int y = (int) Math.sqrt((radius * radius) - (x * x));
//        drawPoint(g, x, y, 150);
//        drawPoint(g, x, -y, 150);
//      }
//
//      for (int i = 0; i < 360; i = i + 6)
//      {
//        double sin = Math.sin(Math.toRadians(i));
//        double cos = Math.cos(Math.toRadians(i));
//        int x = (int) (radius * cos);
//        int y = (int) (radius * sin);
//        drawPoint(g, x, y, 300);
//      }

      int shift = 69;
      float width = 3f;
      float[] dash = {8f, 4f};

      for (int y = 50; y <= 450; y+= 50)
      {
        ((Graphics2D) g).setStroke(new BasicStroke(width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, width, dash, 0.0f));
        int[] xPoints = new int[] {50, 100, 150};
        int[] yPoints = new int[] {y, y + shift, y};
        g.drawPolyline(xPoints, yPoints, 3);
      }

      for (int y = 50; y <= 450; y+= 50)
      {
        ((Graphics2D) g).setStroke(new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, width, dash, 0.0f));
        int[] xPoints = new int[] {200, 250, 300};
        int[] yPoints = new int[] {y, y + shift, y};
        g.drawPolyline(xPoints, yPoints, 3);
      }

      for (int y = 50; y <= 450; y+= 50)
      {
        ((Graphics2D) g).setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, width, dash, 0.0f));
        int[] xPoints = new int[] {350, 400, 450};
        int[] yPoints = new int[] {y, y + shift, y};
        g.drawPolyline(xPoints, yPoints, 3);
      }
    }

    private void drawPoint(Graphics g, int x, int y, int shift)
    {
      int diameter = 7;
      int x1 = x + shift - diameter / 2;
      int y1 = y + shift - diameter / 2;
      g.fillOval(x1, y1, diameter, diameter);
    }
  }
}
