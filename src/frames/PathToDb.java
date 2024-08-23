package frames;

import frames.swing.FrameToFile;
import common.eProfile;
import common.eProp;
import dataset.Conn;
import java.io.File;
import javax.swing.JFileChooser;
import dataset.eExcep;
import frames.swing.FileFilter;
import java.awt.Frame;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.SwingWorker;
import startup.App;

/**
 * <p>
 * Указать путь к БД </p>
 */
public class PathToDb extends javax.swing.JDialog {

    private String num_base = null;

    public PathToDb(Frame parent, String num_base) {
        super(parent, true);
        this.num_base = num_base;

        initComponents();

        //Загрузка параметров входа
        labMes.setText("");
        edHost.setText(eProp.server(num_base));
        edPath.setText(eProp.base(num_base));
        edPort.setText(eProp.port(num_base));
        edUser.setText(eProp.user.read());
        edPass.setText(eProp.password);
        onCaretUpdate(null);
    }

    //Соединение с БД.
    public void connectToDb() {
        new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                progressBar.setIndeterminate(true);
                labMes.setText("Установка соединения с базой данных");
                eExcep pass = Conn.connection(edHost.getText(), edPort.getText(), edPath.getText(), edUser.getText(), edPass.getPassword(), null);
                if (pass == eExcep.yesConn) {
                    
                    if ("SYSDBA".equalsIgnoreCase(edUser.getText())) {

                            if (App.Top.frame == null) {
                                App.createApp(eProfile.P01);
                            }
                            eProp.user.write(edUser.getText().trim());
                            eProp.password = String.valueOf(edPass.getPassword()).trim();
                            eProp.base_num.write(num_base);
                            eProp.port(num_base, edPort.getText().trim());
                            eProp.server(num_base, edHost.getText().trim());
                            eProp.base(num_base, edPath.getText().trim());
                            eProp.save();
                            dispose();
                        
                    } else {  
                        //Получим роль по имени логина
                        Statement st = Conn.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                        //ResultSet rs = st.executeQuery("SELECT DISTINCT a.rdb$role_name , b.rdb$user FROM rdb$roles a, rdb$user_privileges b WHERE a.rdb$role_name = b.rdb$relation_name AND b.rdb$user = '" + edUser.getText() + "'");
                        ResultSet rs = st.executeQuery("SELECT u.RDB$USER, u.RDB$RELATION_NAME FROM RDB$USER_PRIVILEGES u WHERE u.RDB$USER = '" + edUser.getText().toUpperCase() + "'");
                        while (rs.next()) {
                            eProp.role = rs.getString("RDB$RELATION_NAME").trim();
                            Conn.connection().close();
                            //Соединение с новыми привелегиями
                            pass = Conn.connection(edHost.getText(), edPort.getText(), edPath.getText(), edUser.getText(), edPass.getPassword(), eProp.role);
                            if (pass == eExcep.yesConn) {
                                //По имени роли откроем нужное приложение
                                if (App.Top.frame == null && eProfile.P02.roleSet.contains(eProp.role)) {
                                    App.createApp(eProfile.P02);
                                } else if (App.Top.frame == null && eProfile.P03.roleSet.contains(eProp.role)) {
                                    App.createApp(eProfile.P03);
                                }
                                eProp.base_num.write(num_base);
                                eProp.port(num_base, edPort.getText().trim());
                                eProp.server(num_base, edHost.getText().trim());
                                eProp.base(num_base, edPath.getText().trim());
                                eProp.user.write(edUser.getText().trim());
                                eProp.save();
                                dispose();
                            }
                        }                        
                    }
                }
                if (pass == eExcep.noLogin) {
                    labMes.setText(eExcep.noLogin.mes);
                } else if (pass == eExcep.noGrant) {
                    labMes.setText(eExcep.noGrant.mes);
                } else {
                    labMes.setText(eExcep.noConn.mes);
                }
                return null;
            }

            public void done() {
                progressBar.setIndeterminate(false);
            }
        }.execute();
    }

    public static void pathToDb(Frame parent) {
        String num_base = eProp.base_num.read();
        PathToDb pathToDb = new PathToDb(parent, num_base);
        FrameToFile.setFrameSize(pathToDb);
        pathToDb.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        labMes = new javax.swing.JLabel();
        labUser1 = new javax.swing.JLabel();
        edHost = new javax.swing.JTextField();
        labUser2 = new javax.swing.JLabel();
        edPath = new javax.swing.JTextField();
        btnFile = new javax.swing.JButton();
        labUser3 = new javax.swing.JLabel();
        edPort = new javax.swing.JTextField();
        labUser = new javax.swing.JLabel();
        labPass = new javax.swing.JLabel();
        edUser = new javax.swing.JTextField();
        edPass = new javax.swing.JPasswordField();
        progressBar = new javax.swing.JProgressBar();
        jPanel2 = new javax.swing.JPanel();
        btnOk = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Установка соединения с базой данных");
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage()));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                PathToDb.this.windowClosed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 255)));
        jPanel4.setPreferredSize(new java.awt.Dimension(510, 200));

        labMes.setFont(frames.UGui.getFont(0,1));
        labMes.setText("Ошибка соединения с базой данных!");
        labMes.setMaximumSize(new java.awt.Dimension(200, 14));
        labMes.setPreferredSize(new java.awt.Dimension(390, 14));

        labUser1.setFont(frames.UGui.getFont(0,0));
        labUser1.setText("Сервер");
        labUser1.setAlignmentX(0.5F);
        labUser1.setMaximumSize(new java.awt.Dimension(60, 14));
        labUser1.setPreferredSize(new java.awt.Dimension(60, 14));

        edHost.setFont(frames.UGui.getFont(0,0));
        edHost.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        edHost.setMinimumSize(new java.awt.Dimension(0, 0));
        edHost.setPreferredSize(new java.awt.Dimension(146, 18));
        edHost.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                onCaretUpdate(evt);
            }
        });

        labUser2.setFont(frames.UGui.getFont(0,0));
        labUser2.setText("Файл БД");
        labUser2.setAlignmentX(0.5F);
        labUser2.setMaximumSize(new java.awt.Dimension(60, 14));
        labUser2.setPreferredSize(new java.awt.Dimension(60, 14));

        edPath.setFont(frames.UGui.getFont(0,0));
        edPath.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        edPath.setMinimumSize(new java.awt.Dimension(0, 0));
        edPath.setPreferredSize(new java.awt.Dimension(340, 16));
        edPath.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                onCaretUpdate(evt);
            }
        });

        btnFile.setFont(frames.UGui.getFont(0,0));
        btnFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b033.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.eProp.locale); // NOI18N
        btnFile.setToolTipText(bundle.getString("Выбрать файл")); // NOI18N
        btnFile.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnFile.setMaximumSize(new java.awt.Dimension(27, 16));
        btnFile.setMinimumSize(new java.awt.Dimension(27, 26));
        btnFile.setPreferredSize(new java.awt.Dimension(27, 26));
        btnFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFileActionPerformed(evt);
            }
        });

        labUser3.setFont(frames.UGui.getFont(0,0));
        labUser3.setText("Порт");
        labUser3.setAlignmentX(0.5F);

        edPort.setFont(frames.UGui.getFont(0,0));
        edPort.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        edPort.setMinimumSize(new java.awt.Dimension(0, 0));
        edPort.setPreferredSize(new java.awt.Dimension(60, 18));

        labUser.setFont(frames.UGui.getFont(0,0));
        labUser.setText("Пользователь");
        labUser.setAlignmentX(0.5F);
        labUser.setMaximumSize(new java.awt.Dimension(120, 14));
        labUser.setPreferredSize(new java.awt.Dimension(96, 14));

        labPass.setFont(frames.UGui.getFont(0,0));
        labPass.setText("Пароль");
        labPass.setAlignmentX(0.5F);
        labPass.setMaximumSize(new java.awt.Dimension(120, 14));
        labPass.setPreferredSize(new java.awt.Dimension(96, 14));

        edUser.setFont(frames.UGui.getFont(0,0));
        edUser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        edUser.setMinimumSize(new java.awt.Dimension(0, 0));
        edUser.setPreferredSize(new java.awt.Dimension(120, 16));
        edUser.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                onCaretUpdate(evt);
            }
        });

        edPass.setFont(frames.UGui.getFont(0,0));
        edPass.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        edPass.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                onCaretUpdate(evt);
            }
        });

        progressBar.setBorder(null);
        progressBar.setFocusable(false);
        progressBar.setPreferredSize(new java.awt.Dimension(340, 2));
        progressBar.setRequestFocusEnabled(false);
        progressBar.setVerifyInputWhenFocusTarget(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(labUser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                            .addComponent(labPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(edPass))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(labUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(edUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(labUser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labUser3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(53, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(80, 80, 80)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(labMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(edHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labUser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labUser3)
                    .addComponent(edPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(edPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labUser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(20, 20, 20)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        getContentPane().add(jPanel4, java.awt.BorderLayout.CENTER);

        jPanel2.setPreferredSize(new java.awt.Dimension(510, 46));

        btnOk.setFont(frames.UGui.getFont(0,0));
        btnOk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b028.gif"))); // NOI18N
        btnOk.setText("ОК");
        btnOk.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnOk.setEnabled(false);
        btnOk.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOk.setMaximumSize(new java.awt.Dimension(80, 25));
        btnOk.setMinimumSize(new java.awt.Dimension(0, 0));
        btnOk.setPreferredSize(new java.awt.Dimension(80, 25));
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOk(evt);
            }
        });

        btnClose.setFont(frames.UGui.getFont(0,0));
        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b029.gif"))); // NOI18N
        btnClose.setText("Отмена");
        btnClose.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnClose.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnClose.setMaximumSize(new java.awt.Dimension(80, 25));
        btnClose.setMinimumSize(new java.awt.Dimension(0, 0));
        btnClose.setPreferredSize(new java.awt.Dimension(80, 25));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClose(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(313, Short.MAX_VALUE)
                .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    //Событие ввода данных
    private void onCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_onCaretUpdate
        labMes.setText("");
        if (!edPath.getText().isEmpty()
                && !edUser.getText().isEmpty()
                && edPass.getPassword().length > 0
                && !edHost.getText().isEmpty()
                && !edPort.getText().isEmpty()) {
            btnOk.setEnabled(true);
        } else {
            btnOk.setEnabled(false);
        }
}//GEN-LAST:event_onCaretUpdate

    //Нажал кнопку "ОК"
    private void btnOk(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOk
        connectToDb();
}//GEN-LAST:event_btnOk
    //Нажал кнопку "ОТМЕНА"
    private void btnClose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose
        this.dispose();
}//GEN-LAST:event_btnClose

    private void btnFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFileActionPerformed
        FileFilter filter = new FileFilter();
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        chooser.setFileFilter(filter);
        int result = chooser.showDialog(this, "Выбрать");
        if (result == JFileChooser.APPROVE_OPTION) {
            edPath.setText(chooser.getSelectedFile().getPath());
        }
}//GEN-LAST:event_btnFileActionPerformed

    private void windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosed
        //
    }//GEN-LAST:event_windowClosed

// <editor-fold defaultstate="collapsed" desc="Generated Code">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnFile;
    private javax.swing.JButton btnOk;
    private javax.swing.JTextField edHost;
    private javax.swing.JPasswordField edPass;
    private javax.swing.JTextField edPath;
    private javax.swing.JTextField edPort;
    private javax.swing.JTextField edUser;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel labMes;
    private javax.swing.JLabel labPass;
    private javax.swing.JLabel labUser;
    private javax.swing.JLabel labUser1;
    private javax.swing.JLabel labUser2;
    private javax.swing.JLabel labUser3;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables
// </editor-fold> 
}
