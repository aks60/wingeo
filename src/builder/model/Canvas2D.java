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

    private Wincalc wing;

    public Canvas2D(Wincalc wing) {
        this.wing = wing;
        wing.canvas = this;
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                wing.mousePressed.forEach(e -> e.mouseEvent(event));
                repaint();
            }

            public void mouseReleased(MouseEvent event) {
                wing.mouseReleased.forEach(e -> e.mouseEvent(event));
                repaint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {

            public void mouseDragged(MouseEvent event) {
                wing.mouseDragged.forEach(e -> e.mouseEvent(event));
                repaint();
            }
        });
        addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent event) {
                wing.scale = scale(wing, 0, 0);
            }
        });
    }

    public void paintComponent(Graphics g) {
        //System.out.println("Canvas2D.paintComponent()");
        wing.gc2d = (Graphics2D) g;
        wing.gc2d.scale(wing.scale, wing.scale);
        wing.draw();
    }

    public double scale(Wincalc wing, double dx, double dy) {
        Rectangle2D rec = wing.rootArea.area.getBounds2D();
        return ((getWidth() + dx) / rec.getMaxX() > (getHeight() + dx) / rec.getMaxY())
                ? (getHeight() + dx) / (rec.getMaxY() + dy) : (getWidth() + dx) / (rec.getMaxX() + dy);
    }
}
