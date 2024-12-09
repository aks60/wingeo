package frames;

import dataset.Conn;
import dataset.eExcep;
import common.eProfile;
import common.eProp;
import java.awt.Color;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import startup.App;

//��������� ����������
public class LogoToDb extends javax.swing.JDialog {

    private int countCon = 0;

    public LogoToDb(java.awt.Window owner) {
        super(owner);
        initComponents();

        //���������� ��� ������������
        if (eProp.dev == true) {
            if ("adm".equals(eProp.profile)) {
                edUser.setText("SYSDBA"); //user
                edPass.setText("masterkey"); //pass
            } else if ("tex".equals(eProp.profile)) {
                edUser.setText("TEXNOLOG"); //user
                edPass.setText("masterkey"); //pass
            } else if ("man".equals(eProp.profile)) {
                edUser.setText("MANAGER"); //user
                edPass.setText("masterkey"); //pass
            } else {
                edUser.setText("TEXNOLOG"); //user
                edPass.setText("masterkey"); //pass                
            }
            connectToDb();

        } else {              
            labMes.setForeground(Color.BLUE);
            labMes.setText("������� ����� � ������");
            edUser.setText(eProp.user.read());
            edPass.requestFocus();
            getRootPane().setDefaultButton(btnOk);
            
//            if (List.of("SYSDBA", "TEXNOLOG", "MANAGER")
//                    .contains(eProp.user.read().toUpperCase())) {
//                //edPass.setText("masterkey");
//                //connectToDb();
//            } else {
//                edPass.requestFocus();
//                getRootPane().setDefaultButton(btnOk);
//            }
        }
    }

    /**
     * ������� �� ���������� � ��.
     */
    public void connectToDb() {
        if (++countCon > 3) {
            dispose();
            PathToDb.pathToDb(null);
        }

        new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                progressBar.setIndeterminate(true);
                labMes.setForeground(Color.BLUE);
                labMes.setText("��������� ���������� � ����� ������");
                String num = eProp.base_num.read();
                //JOptionPane.showMessageDialog(LogoToDb.this, " " + eProp.server(num) + " " +eProp.port(num) + " " +  eProp.base(num)
                //+ " " + edUser.getText() + " " + edPass.getPassword(), "��������!", 1);
                eExcep pass = Conn.connection(eProp.server(num), eProp.port(num), eProp.base(num), edUser.getText(), edPass.getPassword(), null);
                //JOptionPane.showMessageDialog(LogoToDb.this, pass.mes, "��������!", 1);
                if (pass == eExcep.yesConn) {

                    if ("SYSDBA".equalsIgnoreCase(edUser.getText())) {
                        App.createApp(eProfile.P01);
                        eProp.user.write(edUser.getText().trim());
                        eProp.password = String.valueOf(edPass.getPassword()).trim();
                        eProp.save();
                        dispose();

                    } else {
                        //������� ���� �� ����� ������
                        Statement st = Conn.getConnection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                        //ResultSet rs = st.executeQuery("SELECT DISTINCT a.rdb$role_name , b.rdb$user FROM rdb$roles a, rdb$user_privileges b WHERE a.rdb$role_name = b.rdb$relation_name AND b.rdb$user = '" + edUser.getText() + "'");
                        ResultSet rs = st.executeQuery("SELECT u.RDB$USER, u.RDB$RELATION_NAME FROM RDB$USER_PRIVILEGES u WHERE u.RDB$USER = '" + edUser.getText().toUpperCase() + "'");
                        if (rs.next()) {
                            eProp.role = rs.getString("RDB$RELATION_NAME").trim();
                            Conn.getConnection().close();
                            //���������� � ������ ������������
                            pass = Conn.connection(eProp.server(num), eProp.port(num), eProp.base(num), edUser.getText(), edPass.getPassword(), eProp.role);
                            //�� ����� ���� ������� ������ ����������
                            if (pass == eExcep.yesConn) {
                                if (eProfile.P02.roleSet.contains(eProp.role)) {
                                    App.createApp(eProfile.P02);
                                } else if (eProfile.P03.roleSet.contains(eProp.role)) {
                                    App.createApp(eProfile.P03);
                                }
                                eProp.user.write(edUser.getText().trim());
                                eProp.password = String.valueOf(edPass.getPassword()).trim();
                                eProp.save();
                                dispose();
                            }
                        }
                    }
                }
                labMes.setForeground(Color.RED);
                labMes.setText(pass.mes);
                return null;
            }

            public void done() {
                progressBar.setIndeterminate(false);
            }
        }.execute();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pan2 = new javax.swing.JPanel();
        labPass = new javax.swing.JLabel();
        edPass = new javax.swing.JPasswordField();
        labUser = new javax.swing.JLabel();
        edUser = new javax.swing.JTextField();
        progressBar = new javax.swing.JProgressBar();
        labMes = new javax.swing.JLabel();
        pan3 = new javax.swing.JPanel();
        labImg = new javax.swing.JLabel();
        pan4 = new javax.swing.JPanel();
        btnOk = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(86, 0), new java.awt.Dimension(6, 0), new java.awt.Dimension(50, 32767));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("����������� �������");
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage()));
        setPreferredSize(new java.awt.Dimension(340, 200));
        setResizable(false);

        pan2.setAlignmentX(0.0F);
        pan2.setPreferredSize(new java.awt.Dimension(250, 132));

        labPass.setFont(frames.UGui.getFont(0,0));
        labPass.setText("������");
        labPass.setAlignmentX(0.5F);
        labPass.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        labPass.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        labPass.setPreferredSize(new java.awt.Dimension(100, 18));

        edPass.setFont(frames.UGui.getFont(0,0));
        edPass.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        edPass.setPreferredSize(new java.awt.Dimension(120, 18));
        edPass.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                passonCaretUpdate(evt);
            }
        });

        labUser.setFont(frames.UGui.getFont(0,0));
        labUser.setText("������������");
        labUser.setAlignmentX(0.5F);
        labUser.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        labUser.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        labUser.setPreferredSize(new java.awt.Dimension(100, 18));

        edUser.setFont(frames.UGui.getFont(0,0));
        edUser.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        edUser.setMinimumSize(new java.awt.Dimension(0, 0));
        edUser.setPreferredSize(new java.awt.Dimension(120, 18));
        edUser.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                useronCaretUpdate(evt);
            }
        });

        progressBar.setBorder(null);
        progressBar.setFocusable(false);
        progressBar.setPreferredSize(new java.awt.Dimension(230, 2));
        progressBar.setRequestFocusEnabled(false);
        progressBar.setVerifyInputWhenFocusTarget(false);

        labMes.setFont(frames.UGui.getFont(0,0));
        labMes.setForeground(new java.awt.Color(0, 0, 255));
        labMes.setText("<html>������ ���������� � ����� ������!");
        labMes.setToolTipText("������ ���������� � ����� ������!");
        labMes.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        labMes.setPreferredSize(new java.awt.Dimension(240, 14));

        labImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/imgXX/k002.gif"))); // NOI18N

        javax.swing.GroupLayout pan3Layout = new javax.swing.GroupLayout(pan3);
        pan3.setLayout(pan3Layout);
        pan3Layout.setHorizontalGroup(
            pan3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labImg)
        );
        pan3Layout.setVerticalGroup(
            pan3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labImg)
        );

        javax.swing.GroupLayout pan2Layout = new javax.swing.GroupLayout(pan2);
        pan2.setLayout(pan2Layout);
        pan2Layout.setHorizontalGroup(
            pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan2Layout.createSequentialGroup()
                .addContainerGap(88, Short.MAX_VALUE)
                .addGroup(pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pan2Layout.createSequentialGroup()
                        .addGroup(pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(labPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(edPass, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(edUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
            .addGroup(pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pan2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(pan3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(259, Short.MAX_VALUE)))
        );
        pan2Layout.setVerticalGroup(
            pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(33, Short.MAX_VALUE))
            .addGroup(pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pan2Layout.createSequentialGroup()
                    .addGap(20, 20, 20)
                    .addComponent(pan3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(46, Short.MAX_VALUE)))
        );

        getContentPane().add(pan2, java.awt.BorderLayout.CENTER);

        pan4.setPreferredSize(new java.awt.Dimension(175, 40));
        pan4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnOk.setFont(frames.UGui.getFont(0,0));
        btnOk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b028.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.eProp.locale); // NOI18N
        btnOk.setText(bundle.getString("����.��")); // NOI18N
        btnOk.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnOk.setEnabled(false);
        btnOk.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOk.setMaximumSize(new java.awt.Dimension(80, 25));
        btnOk.setMinimumSize(new java.awt.Dimension(0, 0));
        btnOk.setPreferredSize(new java.awt.Dimension(80, 25));
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okAction(evt);
            }
        });
        pan4.add(btnOk);

        btnClose.setFont(frames.UGui.getFont(0,0));
        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b029.gif"))); // NOI18N
        btnClose.setText(bundle.getString("������")); // NOI18N
        btnClose.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnClose.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnClose.setMaximumSize(new java.awt.Dimension(80, 25));
        btnClose.setMinimumSize(new java.awt.Dimension(0, 0));
        btnClose.setPreferredSize(new java.awt.Dimension(80, 25));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeAction(evt);
            }
        });
        pan4.add(btnClose);
        pan4.add(filler1);

        getContentPane().add(pan4, java.awt.BorderLayout.SOUTH);

        getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeAction
        System.exit(0);

    }//GEN-LAST:event_closeAction

    private void okAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okAction
        connectToDb();
    }//GEN-LAST:event_okAction

    private void passonCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_passonCaretUpdate
        if (edPass.getPassword().length > 0 && !edUser.getText().isEmpty()) {
            btnOk.setEnabled(true);
        } else {
            btnOk.setEnabled(false);
        }
    }//GEN-LAST:event_passonCaretUpdate

    private void useronCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_useronCaretUpdate
        labMes.setText("");
        if (edPass.getPassword().length > 0 && !edUser.getText().isEmpty()) {
            btnOk.setEnabled(true);
        } else {
            btnOk.setEnabled(false);
        }
    }//GEN-LAST:event_useronCaretUpdate
// <editor-fold defaultstate="collapsed" desc="Generated Code">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnOk;
    private javax.swing.JPasswordField edPass;
    private javax.swing.JTextField edUser;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel labImg;
    private javax.swing.JLabel labMes;
    private javax.swing.JLabel labPass;
    private javax.swing.JLabel labUser;
    private javax.swing.JPanel pan2;
    private javax.swing.JPanel pan3;
    private javax.swing.JPanel pan4;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables
// </editor-fold> 
}
