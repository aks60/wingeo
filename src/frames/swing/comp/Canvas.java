package frames.swing.comp;

import builder.Wincalc;
import common.UCom;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.locationtech.jts.geom.Envelope;

public class Canvas extends javax.swing.JPanel {

    public static double translate[] = {2, 2};
    public Wincalc winc = null;
    public static double margin = 200; //��� ��������� ����� 
    public static double koef_scale = 1;

    public Canvas() {
        initComponents();
    }

    public void init(Wincalc winc) {
        if (winc == null) {
            this.winc = null;
        } else {
            this.winc = winc;
            this.winc.canvas = this;
            this.winc.scale = scale();

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
                    winc.scale = scale();
                    repaint();              
                }
            });
        }
        this.requestFocus();
    }

    //���������� �����������
    public void draw() {
        if (winc != null) {
            repaint();
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
            winc.gc2d.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, UCom.scaleFont(winc.scale)));
            winc.gc2d.setColor(getBackground());
            winc.gc2d.setStroke(new BasicStroke(2)); //������� �����
            winc.gc2d.translate(translate[0], translate[1]);
            winc.scale = scale();
            winc.gc2d.scale(winc.scale, winc.scale);
            winc.location();
            winc.actionEvent.action();
            winc.draw();

        } else {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    //�������� ����������� �����������
    public static ImageIcon createIcon(Wincalc winc, int length) {
        try {
            BufferedImage bi = new BufferedImage(length, length, BufferedImage.TYPE_INT_RGB);
            winc.gc2d = bi.createGraphics();
            winc.gc2d.fillRect(0, 0, length, length);
            double height = winc.root.area.getGeometryN(0).getEnvelopeInternal().getMaxY();
            double width = winc.root.area.getGeometryN(0).getEnvelopeInternal().getMaxX();
            winc.scale = (length / width > length / height)
                    ? length / (height + 200) : length / (width + 200);
            winc.gc2d.scale(winc.scale, winc.scale);
            //winc.upgrade();
            winc.draw(); //����� �����������
            return new ImageIcon(bi);

        } catch (Exception e) {
            System.err.println("Canvas.createImageIcon() " + e);
            return new ImageIcon();
        }
    }
    
    public double scale() {
        Envelope env = winc.root.area.getGeometryN(0).getEnvelopeInternal();
        double dX = env.getMaxX(), dY = env.getMaxY();
        return  (getWidth() / (margin + dX) > getHeight() / (margin + dY))
                ? koef_scale * getHeight() / (margin + dY) : koef_scale * getWidth() / (margin + dX);
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
