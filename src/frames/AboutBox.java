package frames;

import frames.UGui;

public class AboutBox extends javax.swing.JFrame {
    
    public AboutBox() {
        initComponents();

//        labProjectVersion.setText("Версия программы - 1.0");
//        labBase.setText("Версия базы данных - 1.0");
//        labConfig.setText("Конфигурация  - 2.0");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        north = new javax.swing.JPanel();
        labTitle = new javax.swing.JLabel();
        pan2 = new javax.swing.JPanel();
        labApp = new javax.swing.JLabel();
        labBase = new javax.swing.JLabel();
        labConfig = new javax.swing.JLabel();
        labImage = new javax.swing.JLabel();
        centr = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        south = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage()));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        north.setPreferredSize(new java.awt.Dimension(300, 100));
        north.setLayout(new java.awt.BorderLayout());

        labTitle.setBackground(new java.awt.Color(255, 255, 255));
        labTitle.setFont(frames.UGui.getFont(0, 1));
        labTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labTitle.setText("<<SA.OKNA>>");
        labTitle.setOpaque(true);
        labTitle.setPreferredSize(new java.awt.Dimension(123, 30));
        north.add(labTitle, java.awt.BorderLayout.PAGE_START);

        pan2.setBackground(new java.awt.Color(255, 255, 255));
        pan2.setPreferredSize(new java.awt.Dimension(200, 60));

        labApp.setFont(frames.UGui.getFont(0, 1));
        labApp.setText("Верия программы - 1.0");

        labBase.setFont(frames.UGui.getFont(0, 1));
        labBase.setText("Версия базы данных - 1.0");

        labConfig.setFont(frames.UGui.getFont(0, 1));
        labConfig.setText("Конфигурация  - 2.0");

        labImage.setBackground(new java.awt.Color(255, 255, 255));
        labImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif"))); // NOI18N
        labImage.setMaximumSize(new java.awt.Dimension(300, 70));
        labImage.setOpaque(true);
        labImage.setPreferredSize(new java.awt.Dimension(80, 60));

        javax.swing.GroupLayout pan2Layout = new javax.swing.GroupLayout(pan2);
        pan2.setLayout(pan2Layout);
        pan2Layout.setHorizontalGroup(
            pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labConfig, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labBase, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                    .addComponent(labApp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pan2Layout.setVerticalGroup(
            pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labImage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan2Layout.createSequentialGroup()
                .addComponent(labApp, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(labBase, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(labConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        labConfig.getAccessibleContext().setAccessibleName("");

        north.add(pan2, java.awt.BorderLayout.CENTER);

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        centr.setBackground(new java.awt.Color(255, 255, 255));
        centr.setPreferredSize(new java.awt.Dimension(300, 160));

        jLabel1.setFont(frames.UGui.getFont(0,0));
        jLabel1.setText("Программа для расчета оконных конструкций");

        jLabel3.setFont(frames.UGui.getFont(0,0));
        jLabel3.setText("из пластика, дерева и алюминия.");

        jLabel4.setFont(frames.UGui.getFont(0,0));
        jLabel4.setText("Разработчик: Аксёнов Сергей Аркадьевич");

        jLabel5.setFont(frames.UGui.getFont(0,0));
        jLabel5.setText("Сайт разработчика: http: www.iicokna.ru");

        jLabel6.setFont(frames.UGui.getFont(0,0));
        jLabel6.setText("Телефон: +7(903)XXX-XX-XX");

        jLabel7.setFont(frames.UGui.getFont(0,0));
        jLabel7.setText("Релиз версии: 1.01");

        jLabel8.setFont(frames.UGui.getFont(0,0));
        jLabel8.setText("Выпуск первой версии 2021");

        javax.swing.GroupLayout centrLayout = new javax.swing.GroupLayout(centr);
        centr.setLayout(centrLayout);
        centrLayout.setHorizontalGroup(
            centrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(centrLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(centrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(centrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        centrLayout.setVerticalGroup(
            centrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(centrLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addGap(8, 8, 8)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addContainerGap(14, Short.MAX_VALUE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        //mAdmin.AboutBox.window = null;
    }//GEN-LAST:event_formWindowClosed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JPanel centr;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel labApp;
    private javax.swing.JLabel labBase;
    private javax.swing.JLabel labConfig;
    private javax.swing.JLabel labImage;
    private javax.swing.JLabel labTitle;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan2;
    private javax.swing.JPanel south;
    // End of variables declaration//GEN-END:variables
}
