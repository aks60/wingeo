package frames.swing.draw;

import builder.Wincalc;
import builder.model.Com5t;
import common.listener.ListenerFrame;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Canvas extends javax.swing.JPanel implements ListenerFrame<MouseEvent, MouseEvent> {

    private Wincalc winc = null;

    public Canvas() {
        initComponents();
    }

    public void init(Wincalc winc) {
        this.winc = winc;
    }

    public void draw() {
        if (winc != null) {
            winc.scale = scale(winc, 0, 24);
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

    //@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (winc != null) {
            winc.gc2d = (Graphics2D) g;
            winc.gc2d.setColor(getBackground());
            winc.gc2d.setStroke(new BasicStroke(2)); //толщина линии
            winc.gc2d.translate(Com5t.TRANSLATE_XY, Com5t.TRANSLATE_XY);
            winc.scale = scale(winc, -3, 0);
            winc.gc2d.scale(winc.scale, winc.scale);
            winc.rootArea.draw();

        } else {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            //g.clearRect(0, 0, getWidth(), getHeight());
        }
    }

    //Создание изображение конструкции
    public static ImageIcon createIcon(Wincalc winc, int length) {
        try {
            BufferedImage bi = new BufferedImage(length, length, BufferedImage.TYPE_INT_RGB);
            winc.gc2d = bi.createGraphics();
            winc.gc2d.fillRect(0, 0, length, length);
            double height = (winc.height1 > winc.height2) ? winc.height1 : winc.height2;
            double width = (winc.width2 > winc.width1) ? winc.width2 : winc.width1;
            winc.scale = (length / width > length / height)
                    ? length / (height + 200) : length / (width + 200);
            winc.gc2d.scale(winc.scale, winc.scale);
            winc.rootArea.draw(); //рисую конструкцию
            return new ImageIcon(bi);
        } catch (Exception e) {
            System.err.println("Canvas.createImageIcon() " + e);
            return new ImageIcon();
        }
    }

    public double scale(Wincalc winc, double dx, double dy) {
        double height = (winc.height1 > winc.height2) ? winc.height1 : winc.height2;
        double width = (winc.width2 > winc.width1) ? winc.width2 : winc.width1;

        return ((getWidth() + dx) / width > (getHeight() + dx) / height)
                ? (getHeight() + dx) / (height + dy) : (getWidth() + dx) / (width + dy);
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
