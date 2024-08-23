package frames.dialog;

import frames.swing.FrameToFile;
import dataset.Record;
import domain.eFurniture;
import java.util.ArrayList;
import common.listener.ListenerRecord;
import java.util.Arrays;

//Справочник фурнитур
public class DicHandl extends javax.swing.JDialog {

    private ListenerRecord listener = null;

    public DicHandl(java.awt.Frame parent, ListenerRecord listenet, int indexPosition) {
        super(parent, true);
        initComponents();
        initElements();
        this.listener = listenet;
        loadingModel(indexPosition);
        setVisible(true);
    }

    private void loadingModel(int indexPosition) {
        if (indexPosition == 2) {
            txt01.setEditable(true);
            rbt2.setSelected(true);
        } else if (indexPosition == 1) {
            txt01.setEditable(false);
            rbt1.setSelected(true);
        } else if (indexPosition == 0) {
            txt01.setEditable(false);
            rbt0.setSelected(true);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup = new javax.swing.ButtonGroup();
        centr = new javax.swing.JPanel();
        rbt0 = new javax.swing.JRadioButton();
        rbt1 = new javax.swing.JRadioButton();
        rbt2 = new javax.swing.JRadioButton();
        txt01 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        south = new javax.swing.JPanel();
        north = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnChoice = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Расположение ручки");
        setPreferredSize(new java.awt.Dimension(300, 200));

        centr.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        buttonGroup.add(rbt0);
        rbt0.setFont(frames.UGui.getFont(0,0));
        rbt0.setText("По середине");
        rbt0.setMinimumSize(new java.awt.Dimension(85, 18));
        rbt0.setPreferredSize(new java.awt.Dimension(90, 23));
        rbt0.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rbtStateChanged(evt);
            }
        });

        buttonGroup.add(rbt1);
        rbt1.setFont(frames.UGui.getFont(0,0));
        rbt1.setText("Константная");
        rbt1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rbtStateChanged(evt);
            }
        });

        buttonGroup.add(rbt2);
        rbt2.setFont(frames.UGui.getFont(0,0));
        rbt2.setText("На высоте");
        rbt2.setPreferredSize(new java.awt.Dimension(90, 23));
        rbt2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rbtStateChanged(evt);
            }
        });

        txt01.setFont(frames.UGui.getFont(0,0));
        txt01.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setText("мм");

        javax.swing.GroupLayout centrLayout = new javax.swing.GroupLayout(centr);
        centr.setLayout(centrLayout);
        centrLayout.setHorizontalGroup(
            centrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(centrLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(centrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(rbt2, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                    .addComponent(rbt0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(centrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, centrLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(rbt1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, centrLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(txt01, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel1)))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        centrLayout.setVerticalGroup(
            centrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(centrLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(centrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbt0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbt1))
                .addGap(18, 18, 18)
                .addGroup(centrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbt2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt01, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        getContentPane().add(centr, java.awt.BorderLayout.CENTER);

        south.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.setMinimumSize(new java.awt.Dimension(100, 20));
        south.setPreferredSize(new java.awt.Dimension(300, 20));

        javax.swing.GroupLayout southLayout = new javax.swing.GroupLayout(south);
        south.setLayout(southLayout);
        southLayout.setHorizontalGroup(
            southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 296, Short.MAX_VALUE)
        );
        southLayout.setVerticalGroup(
            southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        north.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        north.setMaximumSize(new java.awt.Dimension(32767, 31));
        north.setPreferredSize(new java.awt.Dimension(300, 29));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c009.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.eProp.locale); // NOI18N
        btnClose.setToolTipText(bundle.getString("Закрыть")); // NOI18N
        btnClose.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnClose.setFocusable(false);
        btnClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClose.setMaximumSize(new java.awt.Dimension(25, 25));
        btnClose.setMinimumSize(new java.awt.Dimension(25, 25));
        btnClose.setPreferredSize(new java.awt.Dimension(25, 25));
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClose(evt);
            }
        });

        btnChoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c044.gif"))); // NOI18N
        btnChoice.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btnChoice.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnChoice.setFocusable(false);
        btnChoice.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnChoice.setMaximumSize(new java.awt.Dimension(25, 25));
        btnChoice.setMinimumSize(new java.awt.Dimension(25, 25));
        btnChoice.setPreferredSize(new java.awt.Dimension(25, 25));
        btnChoice.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChoice(evt);
            }
        });

        btnRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c042.gif"))); // NOI18N
        btnRemove.setToolTipText(bundle.getString("Очистить")); // NOI18N
        btnRemove.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnRemove.setFocusable(false);
        btnRemove.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRemove.setMaximumSize(new java.awt.Dimension(25, 25));
        btnRemove.setMinimumSize(new java.awt.Dimension(25, 25));
        btnRemove.setPreferredSize(new java.awt.Dimension(25, 25));
        btnRemove.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemovebtnRemov(evt);
            }
        });

        javax.swing.GroupLayout northLayout = new javax.swing.GroupLayout(north);
        north.setLayout(northLayout);
        northLayout.setHorizontalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnChoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 203, Short.MAX_VALUE)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        northLayout.setVerticalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(northLayout.createSequentialGroup()
                        .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnChoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose
        this.dispose();
    }//GEN-LAST:event_btnClose

    private void btnChoice(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChoice
        Record record = new Record();
        if(rbt0.isSelected()) {
           record.add(0); 
        } else if(rbt1.isSelected()) {
           record.add(1); 
        } else if(rbt2.isSelected()) {
           record.add(2);  
        } 
        record.add(txt01.getText());
        listener.action(record);
        this.dispose();
    }//GEN-LAST:event_btnChoice

    private void btnRemovebtnRemov(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemovebtnRemov
        listener.action(new Record(Arrays.asList(null, null)));
        this.dispose();
    }//GEN-LAST:event_btnRemovebtnRemov

    private void rbtStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rbtStateChanged
        if (rbt2.isSelected()) {
            txt01.setEditable(true);
        } else {
            txt01.setEditable(false);
        } 
    }//GEN-LAST:event_rbtStateChanged

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChoice;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnRemove;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JPanel centr;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel north;
    private javax.swing.JRadioButton rbt0;
    private javax.swing.JRadioButton rbt1;
    private javax.swing.JRadioButton rbt2;
    private javax.swing.JPanel south;
    private javax.swing.JTextField txt01;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>
    
    private void initElements() {

        FrameToFile.setFrameSize(this);
        new FrameToFile(this, btnClose);
    }
}
