package md.experiments;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ScrollableGraphics extends JPanel
{

  public static void main(String... args)
  {
    ScrollableGraphics test = new ScrollableGraphics();
    JScrollPane scrollPane = new JScrollPane(test);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    JFrame frame = new JFrame();
    frame.add(scrollPane);
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setSize(400, 300);
    frame.setVisible(true);
  }

  @Override
  public Dimension getPreferredSize()
  {
    return new Dimension(1000, 1000);
  }

  @Override
  protected void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;

    g2.drawLine(0, 0, 500, 200);
    g2.drawLine(500, 0, 0, 200);
  }
}