package frames.swing.draw;

import builder.Wincalc;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.locationtech.jts.awt.ShapeWriter;

public class Canvas extends javax.swing.JPanel {

    private Wincalc winc = null;
    public static double ds = 72; //для размерных линий 

    public Canvas() {
        initComponents();
    }

    public void init(Wincalc winc) {
        if (winc == null) {
            this.winc = null;
        } else {
            this.winc = winc;
            this.winc.canvas = this;
            this.winc.scale = scale(winc);

            List.of(getKeyListeners()).forEach(l -> removeKeyListener(l));
            List.of(getMouseListeners()).forEach(l -> removeMouseListener(l));
            List.of(getMouseMotionListeners()).forEach(l -> removeMouseMotionListener(l));
            List.of(getComponentListeners()).forEach(l -> removeComponentListener(l));

            addKeyListener(new KeyAdapter() {

                public void keyPressed(KeyEvent event) {
                    winc.keyboardPressed.forEach(e -> e.keysEvent(event));
                    repaint();
                }
            });
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent event) {
                    winc.mousePressed.forEach(e -> e.mouseEvent(event));
                    //repaint();
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
                    winc.scale = scale(winc);
                }
            });
        }
        this.requestFocus();
    }

    //Прорисовка конструкции
    public void draw() {
        if (winc != null) {;
            repaint();
            //this.requestFocus();
        }
    }

    public void saveImage(String name, String type) {

        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        paint(g2);
        try {
            ImageIO.write(image, type, new File(name + "." + type));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (winc != null) {            
            winc.gc2d = (Graphics2D) g;
            winc.gc2d.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, resizeFont()));
            winc.gc2d.setColor(getBackground());
            winc.gc2d.setStroke(new BasicStroke(2)); //толщина линии
            winc.gc2d.translate(0, 0);
            winc.gc2d.scale(winc.scale, winc.scale);
            winc.upgrade();
            winc.draw();

        } else {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    //Создание изображения конструкции
    public static ImageIcon createIcon(Wincalc winc, int length) {
        try {
            BufferedImage bi = new BufferedImage(length, length, BufferedImage.TYPE_INT_RGB);
            winc.gc2d = bi.createGraphics();
            winc.gc2d.fillRect(0, 0, length, length);
            double height = winc.root.area.getEnvelopeInternal().getMaxY();
            double width = winc.root.area.getEnvelopeInternal().getMaxX();
            winc.scale = (length / width > length / height)
                    ? length / (height + 200) : length / (width + 200);
            winc.gc2d.scale(winc.scale, winc.scale);
            //winc.upgrade();
            winc.draw(); //рисую конструкцию
            return new ImageIcon(bi);

        } catch (Exception e) {
            System.err.println("Canvas.createImageIcon() " + e);
            return new ImageIcon();
        }
    }

    private int resizeFont() {
        if (winc.scale > .44) {
            return 28;
        } else if (winc.scale > .24) {
            return 43;
        } else if (winc.scale > .18) {
            return 55;
        } else {
            return 64;
        }
    }
    
    public double scale(Wincalc winc) {
        Shape shape = new ShapeWriter().toShape(winc.root.area);
        Rectangle2D rect = shape.getBounds2D();
        return (getWidth() / (ds + rect.getMaxX()) > getHeight() / (ds + rect.getMaxY()))
                ? getHeight() / (ds + rect.getMaxY()) : getWidth() / (ds + rect.getMaxX());
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
