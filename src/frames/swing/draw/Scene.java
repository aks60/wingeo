package frames.swing.draw;

import builder.Wincalc;
import common.listener.ListenerReload;
import java.awt.Graphics;
import javax.swing.JSpinner;
import org.locationtech.jts.geom.Geometry;

public class Scene extends javax.swing.JPanel {

    private Wincalc winc = null;

    public Scene(Canvas canvas, JSpinner spinner, ListenerReload listenerWinc) {
        initComponents();
        add(canvas, java.awt.BorderLayout.CENTER);
    }

    public void init(Wincalc winc) {
        this.winc = winc;        
    }

    //Прорисовка конструкции
    public void draw() {     
        Geometry geo = winc.root.area.getEnvelope();
        //panVert.
    }

    public void paintComponentHor(Graphics g) {
        
    }
    
    public void paintComponentVer(Graphics g) {
        
    }
    
    private int resizeFont() {
        if (winc.scale > .18) {
            return 12;
        } else if (winc.scale > .16) {
            return 11;
        } else if (winc.scale > .15) {
            return 10;
        } else {
            return 9;
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panVert = new javax.swing.JPanel();
        panHoriz = new javax.swing.JPanel();

        setName(""); // NOI18N
        setLayout(new java.awt.BorderLayout());
        add(panVert, java.awt.BorderLayout.WEST);
        add(panHoriz, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

// <editor-fold defaultstate="collapsed" desc="Generated Code"> 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panHoriz;
    private javax.swing.JPanel panVert;
    // End of variables declaration//GEN-END:variables
    // </editor-fold> 
}
