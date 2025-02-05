package frames.swing.cmp;

import builder.Wincalc;
import common.listener.ListenerAction;
import dataset.Query;
import domain.eSysprod;
import javax.swing.JButton;
import javax.swing.Timer;
import common.listener.ListenerReload;

public class Scene extends javax.swing.JPanel {

    private Canvas canvas = null;
    private ListenerReload listenerReload = null;
    private ListenerAction listenerAction = null;
    public Wincalc winc = null;
    private Timer timer = new javax.swing.Timer(500, null); //����� ������������ �������� �����...

    public Scene(Canvas canvas, ListenerReload listenerReload, ListenerAction listenerAction) {
        initComponents();
        this.canvas = canvas;
        this.listenerReload = listenerReload;
        this.listenerAction = listenerAction;
        add(canvas, java.awt.BorderLayout.CENTER);
    }
    
    public Scene(Canvas canvas, ListenerReload listenerReload) {
        initComponents();
        this.canvas = canvas;
        this.listenerReload = listenerReload;
        add(canvas, java.awt.BorderLayout.CENTER);
    }

    public void init(Wincalc winc) {
        this.winc = winc;
        canvas.init(winc);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Tooll = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnRevert = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(10, 8), new java.awt.Dimension(0, 32767));
        btnMovUp = new javax.swing.JButton();
        btnMovDo = new javax.swing.JButton();
        btnMovRi = new javax.swing.JButton();
        btnMovLe = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(10, 8), new java.awt.Dimension(0, 32767));

        setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 4, 0, 0));
        setName(""); // NOI18N
        setLayout(new java.awt.BorderLayout());

        Tooll.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        Tooll.setAlignmentX(10.0F);
        Tooll.setAlignmentY(10.0F);
        Tooll.setMinimumSize(new java.awt.Dimension(20, 20));
        Tooll.setPreferredSize(new java.awt.Dimension(29, 300));
        Tooll.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c036.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.ePrefs.locale); // NOI18N
        btnSave.setToolTipText(bundle.getString("���������")); // NOI18N
        btnSave.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setMaximumSize(new java.awt.Dimension(25, 25));
        btnSave.setMinimumSize(new java.awt.Dimension(25, 25));
        btnSave.setPreferredSize(new java.awt.Dimension(25, 25));
        btnSave.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSave(evt);
            }
        });
        Tooll.add(btnSave);

        btnRevert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c039.gif"))); // NOI18N
        btnRevert.setToolTipText(bundle.getString("��������")); // NOI18N
        btnRevert.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnRevert.setFocusable(false);
        btnRevert.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRevert.setMaximumSize(new java.awt.Dimension(25, 25));
        btnRevert.setMinimumSize(new java.awt.Dimension(25, 25));
        btnRevert.setPreferredSize(new java.awt.Dimension(25, 25));
        btnRevert.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnRevert.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRevert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRevert(evt);
            }
        });
        Tooll.add(btnRevert);
        Tooll.add(filler1);

        btnMovUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c051.gif"))); // NOI18N
        btnMovUp.setToolTipText(bundle.getString("����������� �����")); // NOI18N
        btnMovUp.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnMovUp.setFocusable(false);
        btnMovUp.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMovUp.setMaximumSize(new java.awt.Dimension(25, 25));
        btnMovUp.setMinimumSize(new java.awt.Dimension(25, 25));
        btnMovUp.setPreferredSize(new java.awt.Dimension(25, 25));
        btnMovUp.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnMovUp.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMovUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMove(evt);
            }
        });
        Tooll.add(btnMovUp);

        btnMovDo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c052.gif"))); // NOI18N
        btnMovDo.setToolTipText(bundle.getString("����������� ����")); // NOI18N
        btnMovDo.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnMovDo.setFocusable(false);
        btnMovDo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMovDo.setMaximumSize(new java.awt.Dimension(25, 25));
        btnMovDo.setMinimumSize(new java.awt.Dimension(25, 25));
        btnMovDo.setPreferredSize(new java.awt.Dimension(25, 25));
        btnMovDo.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnMovDo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMovDo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMove(evt);
            }
        });
        Tooll.add(btnMovDo);

        btnMovRi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c046.gif"))); // NOI18N
        btnMovRi.setToolTipText(bundle.getString("����������� ������")); // NOI18N
        btnMovRi.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnMovRi.setFocusable(false);
        btnMovRi.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMovRi.setMaximumSize(new java.awt.Dimension(25, 25));
        btnMovRi.setMinimumSize(new java.awt.Dimension(25, 25));
        btnMovRi.setPreferredSize(new java.awt.Dimension(25, 25));
        btnMovRi.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnMovRi.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMovRi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMove(evt);
            }
        });
        Tooll.add(btnMovRi);

        btnMovLe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c048.gif"))); // NOI18N
        btnMovLe.setToolTipText(bundle.getString("����������� �����")); // NOI18N
        btnMovLe.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnMovLe.setFocusable(false);
        btnMovLe.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMovLe.setMaximumSize(new java.awt.Dimension(25, 25));
        btnMovLe.setMinimumSize(new java.awt.Dimension(25, 25));
        btnMovLe.setPreferredSize(new java.awt.Dimension(25, 25));
        btnMovLe.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnMovLe.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMovLe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMove(evt);
            }
        });
        Tooll.add(btnMovLe);
        Tooll.add(filler2);

        add(Tooll, java.awt.BorderLayout.WEST);
    }// </editor-fold>//GEN-END:initComponents

    private void btnMove(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMove
        timer.setRepeats(false);
        JButton btn = (JButton) evt.getSource();
        double dxy = (timer.isRunning() == true) ? 2 : 1;
        if (btn == btnMovDo) {
            winc.gson.translate(winc.gson, .0, dxy, winc.scale);
        } else if (btn == btnMovUp) {
            winc.gson.translate(winc.gson, .0, -dxy, winc.scale);
        } else if (btn == btnMovLe) {
            winc.gson.translate(winc.gson, -dxy, .0, winc.scale);
        } else if (btn == btnMovRi) {
            winc.gson.translate(winc.gson, dxy, .0, winc.scale);
        }
        timer.stop();
        timer.start();
        listenerReload.reload(false);
    }//GEN-LAST:event_btnMove

    private void btnSave(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSave
        listenerReload.reload(true).execsql();
    }//GEN-LAST:event_btnSave

    private void btnRevert(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRevert
        listenerAction.action();
    }//GEN-LAST:event_btnRevert


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Tooll;
    private javax.swing.JButton btnMovDo;
    private javax.swing.JButton btnMovLe;
    private javax.swing.JButton btnMovRi;
    private javax.swing.JButton btnMovUp;
    private javax.swing.JButton btnRevert;
    private javax.swing.JButton btnSave;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    // End of variables declaration//GEN-END:variables
}
