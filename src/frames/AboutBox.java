package frames;

import common.eProp;
import startup.App;

public class AboutBox extends javax.swing.JFrame {

    public AboutBox() {
        initComponents();
        initElements();
        init();
    }

    private void init() {
        labApp.setText("Версия программы - " + eProp.version_ap);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        north = new javax.swing.JPanel();
        labTitle = new javax.swing.JLabel();
        pan2 = new javax.swing.JPanel();
        labImage = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        centr = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        labApp = new javax.swing.JLabel();
        south = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage()));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                AboutBox.this.windowClosed(evt);
            }
        });

        north.setPreferredSize(new java.awt.Dimension(300, 100));
        north.setLayout(new java.awt.BorderLayout());

        labTitle.setBackground(new java.awt.Color(255, 255, 255));
        labTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        labTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labTitle.setText("<<SA-OKNA>>");
        labTitle.setOpaque(true);
        labTitle.setPreferredSize(new java.awt.Dimension(123, 30));
        north.add(labTitle, java.awt.BorderLayout.PAGE_START);

        pan2.setBackground(new java.awt.Color(255, 255, 255));
        pan2.setPreferredSize(new java.awt.Dimension(200, 60));

        labImage.setBackground(new java.awt.Color(255, 255, 255));
        labImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif"))); // NOI18N
        labImage.setMaximumSize(new java.awt.Dimension(300, 70));
        labImage.setOpaque(true);
        labImage.setPreferredSize(new java.awt.Dimension(80, 60));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel1.setText("<html>Программа для расчета\n<br>пластиковых окон и дврей.");

        javax.swing.GroupLayout pan2Layout = new javax.swing.GroupLayout(pan2);
        pan2.setLayout(pan2Layout);
        pan2Layout.setHorizontalGroup(
            pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addContainerGap())
        );
        pan2Layout.setVerticalGroup(
            pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labImage, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        north.add(pan2, java.awt.BorderLayout.CENTER);

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        centr.setBackground(new java.awt.Color(255, 255, 255));
        centr.setPreferredSize(new java.awt.Dimension(300, 160));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel5.setText("Сайт продукта: http:sa-okna.ru/winaks");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel6.setText("Телефон: +7(903)124 7833");

        labApp.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        labApp.setText("Верcия программы - 1.0");
        labApp.setMaximumSize(new java.awt.Dimension(86, 18));
        labApp.setMinimumSize(new java.awt.Dimension(86, 18));
        labApp.setPreferredSize(new java.awt.Dimension(86, 18));

        javax.swing.GroupLayout centrLayout = new javax.swing.GroupLayout(centr);
        centr.setLayout(centrLayout);
        centrLayout.setHorizontalGroup(
            centrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(centrLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(centrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labApp, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(centrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        centrLayout.setVerticalGroup(
            centrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(centrLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(labApp, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        getContentPane().add(centr, java.awt.BorderLayout.CENTER);

        south.setPreferredSize(new java.awt.Dimension(300, 40));

        btnClose.setFont(frames.UGui.getFont(0, 1));
        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b053.gif"))); // NOI18N
        btnClose.setText("ОК");
        btnClose.setPreferredSize(new java.awt.Dimension(69, 20));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout southLayout = new javax.swing.GroupLayout(south);
        south.setLayout(southLayout);
        southLayout.setHorizontalGroup(
            southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(southLayout.createSequentialGroup()
                .addGap(107, 107, 107)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(117, Short.MAX_VALUE))
        );
        southLayout.setVerticalGroup(
            southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, southLayout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosed
        //mAdmin.AboutBox.window = null;
    }//GEN-LAST:event_windowClosed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JPanel centr;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel labApp;
    private javax.swing.JLabel labImage;
    private javax.swing.JLabel labTitle;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan2;
    private javax.swing.JPanel south;
    // End of variables declaration//GEN-END:variables

    public void initElements() {

        App.loadLocationWin(this, btnClose, (e) -> {
            App.saveLocationWin(this, btnClose);
        });
    }
}
