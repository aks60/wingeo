package frames.swing.draw;

import builder.Wincalc;
import builder.model.Com5t;
import common.listener.ListenerFrame;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;

public class Canvas extends javax.swing.JPanel implements ListenerFrame<MouseEvent, MouseEvent> {

    private Wincalc winc = null;

    public Canvas() {
        initComponents();       
    }

    public void init(Wincalc winc) {
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

    public void draw() {
//        if (winc != null) {
//            winc.scale = scale(winc, 0, 24);
//            repaint();
//        }
    }

    public void saveImage(String name, String type) {

//        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
//        Graphics2D g2 = image.createGraphics();
//        paint(g2);
//        try {
//            ImageIO.write(image, type, new File(name + "." + type));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    //@Override
    public void paintComponent(Graphics g) {
        //System.out.println("Canvas2D.paintComponent()");
        super.paintComponent(g);
        if (winc != null) {
            winc.gc2d = (Graphics2D) g;
            //winc.gc2d.setColor(getBackground());
            //winc.gc2d.setStroke(new BasicStroke(2)); //толщина линии
            //winc.gc2d.translate(Com5t.TRANSLATE_XY, Com5t.TRANSLATE_XY);
            //winc.scale = scale(winc, -3, 0);
            winc.gc2d.scale(winc.scale, winc.scale - 0.001);
            winc.draw();

        } else {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            //g.clearRect(0, 0, getWidth(), getHeight());
        }
    }

    //Создание изображения конструкции
    public static ImageIcon createIcon(Wincalc winc, int length) {
//        try {
//            BufferedImage bi = new BufferedImage(length, length, BufferedImage.TYPE_INT_RGB);
//            winc.gc2d = bi.createGraphics();
//            winc.gc2d.fillRect(0, 0, length, length);
//            double height = (winc.height1 > winc.height2) ? winc.height1 : winc.height2;
//            double width = (winc.width2 > winc.width1) ? winc.width2 : winc.width1;
//            winc.scale = (length / width > length / height)
//                    ? length / (height + 200) : length / (width + 200);
//            winc.gc2d.scale(winc.scale, winc.scale);
//            winc.root.draw(); //рисую конструкцию
//            return new ImageIcon(bi);
//        } catch (Exception e) {
//            System.err.println("Canvas.createImageIcon() " + e);
            return new ImageIcon();
//        }
    }

    public double scale(Wincalc winc, double dx, double dy) {
        Rectangle2D rec = winc.root.area.getBounds2D();
        return ((getWidth() + dx) / rec.getMaxX() > (getHeight() + dx) / rec.getMaxY())
                ? (getHeight() + dx) / (rec.getMaxY() + dy) : (getWidth() + dx) / (rec.getMaxX() + dy);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

// <editor-fold defaultstate="collapsed" desc="Generated Code"> 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
// </editor-fold> 
}
