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
    private Timer timer = new javax.swing.Timer(500, null); //залаёт инкриментную скорость коорд...

    public Scene(Canvas canvas, ListenerReload listenerReload, ListenerAction listenerAction) {
        initComponents();
        this.canvas = canvas;
        this.listenerReload = listenerReload;
        this.listenerAction = listenerAction;
        //this.pan2.setVisible(false);
        add(canvas, java.awt.BorderLayout.CENTER);
    }

    public Scene(Canvas canvas, ListenerReload listenerReload) {
        initComponents();
        this.canvas = canvas;
        this.listenerReload = listenerReload;
        //this.pan2.setVisible(false);
        add(canvas, java.awt.BorderLayout.CENTER);
    }

    public void init(Wincalc winc) {
        this.winc = winc;
        canvas.init(winc);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pan1 = new javax.swing.JPanel();
        Tooll = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnRevert = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(10, 8), new java.awt.Dimension(0, 32767));
        btnMovUp = new javax.swing.JButton();
        btnMovDo = new javax.swing.JButton();
        btnMovRi = new javax.swing.JButton();
        btnMovLe = new javax.swing.JButton();
        pan2 = new javax.swing.JPanel();
        btnScale1 = new javax.swing.JButton();
        btnScale2 = new javax.swing.JButton();
        btnScelet = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 4, 0, 0));
        setName(""); // NOI18N
        setLayout(new java.awt.BorderLayout());

        pan1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        pan1.setPreferredSize(new java.awt.Dimension(22, 300));
        pan1.setLayout(new java.awt.BorderLayout());

        Tooll.setMinimumSize(new java.awt.Dimension(20, 20));
        Tooll.setPreferredSize(new java.awt.Dimension(29, 300));
        Tooll.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c036.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.ePrefs.locale); // NOI18N
        btnSave.setToolTipText(bundle.getString("Сохранить")); // NOI18N
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
        btnRevert.setToolTipText(bundle.getString("Отменить")); // NOI18N
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
        btnMovUp.setToolTipText(bundle.getString("Переместить вверх")); // NOI18N
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
        btnMovDo.setToolTipText(bundle.getString("Переместить вниз")); // NOI18N
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
        btnMovRi.setToolTipText(bundle.getString("Переместить вправо")); // NOI18N
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
        btnMovLe.setToolTipText(bundle.getString("Переместить влево")); // NOI18N
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

        pan1.add(Tooll, java.awt.BorderLayout.CENTER);

        pan2.setPreferredSize(new java.awt.Dimension(29, 76));
        pan2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        btnScale1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c094.gif"))); // NOI18N
        btnScale1.setToolTipText(bundle.getString("Переместить влево")); // NOI18N
        btnScale1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnScale1.setFocusable(false);
        btnScale1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnScale1.setMaximumSize(new java.awt.Dimension(25, 25));
        btnScale1.setMinimumSize(new java.awt.Dimension(25, 25));
        btnScale1.setPreferredSize(new java.awt.Dimension(25, 25));
        btnScale1.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnScale1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnScale1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScale1btnMove(evt);
            }
        });
        pan2.add(btnScale1);

        btnScale2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c095.gif"))); // NOI18N
        btnScale2.setToolTipText(bundle.getString("Переместить влево")); // NOI18N
        btnScale2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnScale2.setFocusable(false);
        btnScale2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnScale2.setMaximumSize(new java.awt.Dimension(25, 25));
        btnScale2.setMinimumSize(new java.awt.Dimension(25, 25));
        btnScale2.setPreferredSize(new java.awt.Dimension(25, 25));
        btnScale2.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnScale2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnScale2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScale2btnMove(evt);
            }
        });
        pan2.add(btnScale2);

        btnScelet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c096.gif"))); // NOI18N
        btnScelet.setToolTipText(bundle.getString("Переместить влево")); // NOI18N
        btnScelet.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnScelet.setFocusable(false);
        btnScelet.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnScelet.setMaximumSize(new java.awt.Dimension(25, 25));
        btnScelet.setMinimumSize(new java.awt.Dimension(25, 25));
        btnScelet.setPreferredSize(new java.awt.Dimension(25, 25));
        btnScelet.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnScelet.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnScelet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSceletbtnMove(evt);
            }
        });
        pan2.add(btnScelet);

        pan1.add(pan2, java.awt.BorderLayout.SOUTH);

        add(pan1, java.awt.BorderLayout.WEST);
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

    private void btnScale1btnMove(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScale1btnMove
        canvas.koef_scale = canvas.koef_scale + .1;
        this.canvas.draw();
    }//GEN-LAST:event_btnScale1btnMove

    private void btnScale2btnMove(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScale2btnMove
        canvas.koef_scale = canvas.koef_scale - .1;
        this.canvas.draw();
    }//GEN-LAST:event_btnScale2btnMove

    private void btnSceletbtnMove(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSceletbtnMove
        winc.sceleton = !winc.sceleton;
        this.canvas.draw();
    }//GEN-LAST:event_btnSceletbtnMove


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Tooll;
    private javax.swing.JButton btnMovDo;
    private javax.swing.JButton btnMovLe;
    private javax.swing.JButton btnMovRi;
    private javax.swing.JButton btnMovUp;
    private javax.swing.JButton btnRevert;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnScale1;
    private javax.swing.JButton btnScale2;
    private javax.swing.JButton btnScelet;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JPanel pan1;
    private javax.swing.JPanel pan2;
    // End of variables declaration//GEN-END:variables
}
