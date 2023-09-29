package builder.model;

import builder.Wincalc;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;

public class Canvas2D extends JComponent {

    private Wincalc winc;

    public Canvas2D(Wincalc winc) {
        this.winc = winc;
        winc.canvas = this;
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                winc.mousePressed.forEach(e -> e.mouseEvent(event));
                repaint();
            }

            public void mouseReleased(MouseEvent event) {
                winc.mouseReleased.forEach(e -> e.mouseEvent(event));
                repaint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {

            public void mouseDragged(MouseEvent event) {
                winc.mouseDragged.forEach(e -> e.mouseEvent(event));
                repaint();
            }
        });
        addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent event) {
                winc.scale = scale(winc, 0, 0);
            }
        });
    }

    public void paintComponent(Graphics g) {
        //System.out.println("Canvas2D.paintComponent()");
        winc.gc2d = (Graphics2D) g;
        winc.gc2d.scale(winc.scale, winc.scale);
        winc.draw();
    }

    public double scale(Wincalc winc, double dx, double dy) {
        Rectangle2D rec = winc.rootArea.area.getBounds2D();
        return ((getWidth() + dx) / rec.getMaxX() > (getHeight() + dx) / rec.getMaxY())
                ? (getHeight() + dx) / (rec.getMaxY() + dy) : (getWidth() + dx) / (rec.getMaxX() + dy);
    }
}
