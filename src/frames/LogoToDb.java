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

//Установка соединения
public class LogoToDb extends javax.swing.JDialog {

    private int countCon = 0;

    public LogoToDb(java.awt.Window owner) {
        super(owner);
        initComponents();
        initElements();

        //Автопароль при тестировании
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
            labMes.setText("Введите логин и пароль");
            edUser.setText(eProp.user.getProp());
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
     * Команда на соединение с БД.
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
                labMes.setText("Установка соединения с базой данных");
                String num = eProp.base_num.getProp();
                eExcep pass = Conn.connection(eProp.getServer(num), eProp.getPort(num), eProp.getBase(num), edUser.getText(), edPass.getPassword(), null);

                if (pass == eExcep.yesConn) {

                    if ("SYSDBA".equalsIgnoreCase(edUser.getText())) {
                        App.createApp(eProfile.P01);
                        eProp.user.putProp(edUser.getText().trim());
                        eProp.password = String.valueOf(edPass.getPassword()).trim();
                        dispose();

                    } else {
                        //Получим роль по имени логина
                        Statement st = Conn.сonnection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                        //ResultSet rs = st.executeQuery("SELECT DISTINCT a.rdb$role_name , b.rdb$user FROM rdb$roles a, rdb$user_privileges b WHERE a.rdb$role_name = b.rdb$relation_name AND b.rdb$user = '" + edUser.getText() + "'");
                        ResultSet rs = st.executeQuery("SELECT u.RDB$USER, u.RDB$RELATION_NAME FROM RDB$USER_PRIVILEGES u WHERE u.RDB$USER = '" + edUser.getText().toUpperCase() + "'");
                        if (rs.next()) {
                            eProp.role = rs.getString("RDB$RELATION_NAME").trim();
                            Conn.сonnection().close();
                            //Соединение с новыми привелегиями
                            pass = Conn.connection(eProp.getServer(num), eProp.getPort(num), eProp.getBase(num), edUser.getText(), edPass.getPassword(), eProp.role);
                            //По имени роли откроем нужное приложение
                            if (pass == eExcep.yesConn) {
                                if (eProfile.P02.roleSet.contains(eProp.role)) {
                                    App.createApp(eProfile.P02);
                                } else if (eProfile.P03.roleSet.contains(eProp.role)) {
                                    App.createApp(eProfile.P03);
                                }
                                eProp.user.putProp(edUser.getText().trim());
                                eProp.password = String.valueOf(edPass.getPassword()).trim();
                                dispose();
                            }
                        } else {
                            pass = eExcep.noUser;
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
        pan3 = new javax.swing.JPanel();
        labImg = new javax.swing.JLabel();
        pan5 = new javax.swing.JPanel();
        labMes = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        labPass = new javax.swing.JLabel();
        edPass = new javax.swing.JPasswordField();
        labUser = new javax.swing.JLabel();
        edUser = new javax.swing.JTextField();
        pan4 = new javax.swing.JPanel();
        btnOk = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(50, 32767));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Авторизация доступа");
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage()));
        setMinimumSize(new java.awt.Dimension(348, 206));
        setPreferredSize(new java.awt.Dimension(348, 206));
        setResizable(false);

        pan2.setAlignmentX(0.0F);
        pan2.setMinimumSize(new java.awt.Dimension(250, 132));
        pan2.setPreferredSize(new java.awt.Dimension(250, 132));
        pan2.setLayout(new java.awt.BorderLayout());

        pan3.setPreferredSize(new java.awt.Dimension(82, 66));
        pan3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 30));

        labImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/imgXX/k002.gif"))); // NOI18N
        pan3.add(labImg);

        pan2.add(pan3, java.awt.BorderLayout.WEST);

        labMes.setFont(frames.UGui.getFont(0,0));
        labMes.setForeground(new java.awt.Color(0, 0, 255));
        labMes.setText("<html>Ошибка соединения с базой данных!");
        labMes.setToolTipText("Ошибка соединения с базой данных!");
        labMes.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        labMes.setPreferredSize(new java.awt.Dimension(240, 14));

        progressBar.setBorder(null);
        progressBar.setFocusable(false);
        progressBar.setMinimumSize(new java.awt.Dimension(10, 2));
        progressBar.setPreferredSize(new java.awt.Dimension(220, 3));
        progressBar.setRequestFocusEnabled(false);
        progressBar.setVerifyInputWhenFocusTarget(false);

        labPass.setFont(frames.UGui.getFont(0,0));
        labPass.setText("Пароль");
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
        labUser.setText("Пользователь");
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

        javax.swing.GroupLayout pan5Layout = new javax.swing.GroupLayout(pan5);
        pan5.setLayout(pan5Layout);
        pan5Layout.setHorizontalGroup(
            pan5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan5Layout.createSequentialGroup()
                .addGroup(pan5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan5Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(labMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pan5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pan5Layout.createSequentialGroup()
                                .addComponent(labPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(edPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pan5Layout.createSequentialGroup()
                                .addComponent(labUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(edUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(pan5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(228, 228, 228))
        );
        pan5Layout.setVerticalGroup(
            pan5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pan5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pan5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(edPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pan2.add(pan5, java.awt.BorderLayout.CENTER);

        getContentPane().add(pan2, java.awt.BorderLayout.CENTER);

        pan4.setMinimumSize(new java.awt.Dimension(175, 42));
        pan4.setPreferredSize(new java.awt.Dimension(175, 42));
        pan4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnOk.setFont(frames.UGui.getFont(0,0));
        btnOk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b028.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.eProp.locale); // NOI18N
        btnOk.setText(bundle.getString("Меню.ОК")); // NOI18N
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
        btnClose.setText(bundle.getString("Отмена")); // NOI18N
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

        getContentPane().add(pan4, java.awt.BorderLayout.PAGE_END);

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
    private javax.swing.JPanel pan5;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables
// </editor-fold>
    
    public void initElements() {
        eProp.getWin(this, btnClose, (e) -> {
        });     
    }    
}
