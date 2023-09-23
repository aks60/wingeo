package frames;

import frames.swing.FrameToFile;
import common.eProp;
import java.awt.GraphicsEnvironment;
import java.io.File;
import javax.swing.DefaultComboBoxModel;

public class Setting extends javax.swing.JFrame {

    private String[] fontName = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

    public Setting(java.awt.Window owner) {
        initComponents();
        initElements();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panCentr = new javax.swing.JPanel();
        pan3 = new javax.swing.JPanel();
        textPane4 = new javax.swing.JTextPane();
        jPanel6 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        comboBox = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        btnProp2 = new javax.swing.JButton();
        spinner2 = new javax.swing.JSpinner();
        pan4 = new javax.swing.JPanel();
        textPane3 = new javax.swing.JTextPane();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtWord = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtExcel = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtHtml = new javax.swing.JTextField();
        btnProp3 = new javax.swing.JButton();
        pan5 = new javax.swing.JPanel();
        textPane2 = new javax.swing.JTextPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtURL = new javax.swing.JTextField();
        btnProp4 = new javax.swing.JButton();
        pan6 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        checkBox1 = new javax.swing.JCheckBox();
        textPane1 = new javax.swing.JTextPane();
        panTool = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Установки grgergererherhreth");
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage()));
        setPreferredSize(new java.awt.Dimension(480, 580));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        panCentr.setPreferredSize(new java.awt.Dimension(480, 300));
        panCentr.setLayout(new javax.swing.BoxLayout(panCentr, javax.swing.BoxLayout.Y_AXIS));

        pan3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Шрифт"));
        pan3.setPreferredSize(new java.awt.Dimension(480, 68));
        pan3.setRequestFocusEnabled(false);
        pan3.setLayout(new java.awt.BorderLayout());

        textPane4.setEditable(false);
        textPane4.setBorder(null);
        textPane4.setFont(frames.UGui.getFont(0,0));
        textPane4.setText("Установка шрифта программы");
        textPane4.setPreferredSize(new java.awt.Dimension(160, 60));
        pan3.add(textPane4, java.awt.BorderLayout.WEST);

        jPanel6.setPreferredSize(new java.awt.Dimension(305, 68));

        jLabel7.setFont(frames.UGui.getFont(0,0));
        jLabel7.setText("Font\n");
        jLabel7.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel7.setMaximumSize(new java.awt.Dimension(50, 22));
        jLabel7.setMinimumSize(new java.awt.Dimension(50, 22));
        jLabel7.setPreferredSize(new java.awt.Dimension(50, 22));

        comboBox.setFont(frames.UGui.getFont(0,0));
        comboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboBox.setBorder(null);
        comboBox.setMaximumSize(new java.awt.Dimension(220, 22));
        comboBox.setMinimumSize(new java.awt.Dimension(220, 22));
        comboBox.setPreferredSize(new java.awt.Dimension(220, 22));

        jLabel8.setFont(frames.UGui.getFont(0,0));
        jLabel8.setText("Размер");
        jLabel8.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel8.setMaximumSize(new java.awt.Dimension(50, 22));
        jLabel8.setMinimumSize(new java.awt.Dimension(50, 22));
        jLabel8.setPreferredSize(new java.awt.Dimension(50, 22));

        btnProp2.setFont(frames.UGui.getFont(0,0));
        btnProp2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b031.gif"))); // NOI18N
        btnProp2.setText("Сохранить шрифт в настройках программы");
        btnProp2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnProp2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnProp2.setMaximumSize(new java.awt.Dimension(80, 25));
        btnProp2.setMinimumSize(new java.awt.Dimension(0, 0));
        btnProp2.setPreferredSize(new java.awt.Dimension(280, 25));
        btnProp2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProp2(evt);
            }
        });

        spinner2.setFont(frames.UGui.getFont(0,0));
        spinner2.setMaximumSize(new java.awt.Dimension(80, 22));
        spinner2.setMinimumSize(new java.awt.Dimension(80, 22));
        spinner2.setPreferredSize(new java.awt.Dimension(80, 22));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnProp2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addComponent(btnProp2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pan3.add(jPanel6, java.awt.BorderLayout.CENTER);

        panCentr.add(pan3);

        pan4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Офис "));
        pan4.setPreferredSize(new java.awt.Dimension(480, 94));
        pan4.setLayout(new java.awt.BorderLayout());

        textPane3.setEditable(false);
        textPane3.setBorder(null);
        textPane3.setFont(frames.UGui.getFont(0,0));
        textPane3.setText("Измените путь к программам запуска отчётов, если на\nвашем компьютере директория установки Office  была изменена  ");
        textPane3.setPreferredSize(new java.awt.Dimension(160, 40));
        pan4.add(textPane3, java.awt.BorderLayout.WEST);

        jLabel3.setFont(frames.UGui.getFont(0,0));
        jLabel3.setText("Word");
        jLabel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel3.setMaximumSize(new java.awt.Dimension(40, 22));
        jLabel3.setMinimumSize(new java.awt.Dimension(40, 22));
        jLabel3.setPreferredSize(new java.awt.Dimension(40, 22));

        txtWord.setFont(frames.UGui.getFont(0,0));
        txtWord.setAutoscrolls(false);
        txtWord.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txtWord.setMaximumSize(new java.awt.Dimension(236, 22));
        txtWord.setMinimumSize(new java.awt.Dimension(236, 22));
        txtWord.setPreferredSize(new java.awt.Dimension(236, 22));

        jLabel4.setFont(frames.UGui.getFont(0,0));
        jLabel4.setText("Excel");
        jLabel4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel4.setMaximumSize(new java.awt.Dimension(40, 22));
        jLabel4.setMinimumSize(new java.awt.Dimension(40, 22));
        jLabel4.setPreferredSize(new java.awt.Dimension(40, 22));

        txtExcel.setFont(frames.UGui.getFont(0,0));
        txtExcel.setAutoscrolls(false);
        txtExcel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txtExcel.setMaximumSize(new java.awt.Dimension(236, 22));
        txtExcel.setMinimumSize(new java.awt.Dimension(236, 22));
        txtExcel.setPreferredSize(new java.awt.Dimension(236, 22));

        jLabel9.setFont(frames.UGui.getFont(0,0));
        jLabel9.setText("HTML");
        jLabel9.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel9.setMaximumSize(new java.awt.Dimension(40, 22));
        jLabel9.setMinimumSize(new java.awt.Dimension(40, 22));
        jLabel9.setPreferredSize(new java.awt.Dimension(40, 22));

        txtHtml.setFont(frames.UGui.getFont(0,0));
        txtHtml.setAutoscrolls(false);
        txtHtml.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txtHtml.setMaximumSize(new java.awt.Dimension(236, 22));
        txtHtml.setMinimumSize(new java.awt.Dimension(236, 22));
        txtHtml.setPreferredSize(new java.awt.Dimension(236, 22));

        btnProp3.setFont(frames.UGui.getFont(0,0));
        btnProp3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b031.gif"))); // NOI18N
        btnProp3.setText("Сохранить параметры отчётов");
        btnProp3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnProp3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnProp3.setMaximumSize(new java.awt.Dimension(80, 25));
        btnProp3.setMinimumSize(new java.awt.Dimension(0, 0));
        btnProp3.setPreferredSize(new java.awt.Dimension(280, 25));
        btnProp3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProp3(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnProp3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtWord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtExcel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtHtml, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtWord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtExcel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtHtml, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnProp3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pan4.add(jPanel5, java.awt.BorderLayout.CENTER);

        panCentr.add(pan4);

        pan5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Сервер обновлений "));
        pan5.setPreferredSize(new java.awt.Dimension(480, 44));
        pan5.setLayout(new java.awt.BorderLayout());

        textPane2.setEditable(false);
        textPane2.setBorder(null);
        textPane2.setFont(frames.UGui.getFont(0,0));
        textPane2.setText("С этого сервера будут скачиваться обновления");
        textPane2.setPreferredSize(new java.awt.Dimension(160, 20));
        pan5.add(textPane2, java.awt.BorderLayout.WEST);

        jPanel4.setPreferredSize(new java.awt.Dimension(305, 20));

        jLabel13.setFont(frames.UGui.getFont(0,0));
        jLabel13.setText("Путь");
        jLabel13.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel13.setMaximumSize(new java.awt.Dimension(40, 22));
        jLabel13.setMinimumSize(new java.awt.Dimension(40, 22));
        jLabel13.setPreferredSize(new java.awt.Dimension(40, 22));

        txtURL.setFont(frames.UGui.getFont(0,0));
        txtURL.setAutoscrolls(false);
        txtURL.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txtURL.setMaximumSize(new java.awt.Dimension(236, 22));
        txtURL.setMinimumSize(new java.awt.Dimension(236, 22));
        txtURL.setPreferredSize(new java.awt.Dimension(236, 22));

        btnProp4.setFont(frames.UGui.getFont(0,0));
        btnProp4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b031.gif"))); // NOI18N
        btnProp4.setText("Сохранить путь к серверу");
        btnProp4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnProp4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnProp4.setMaximumSize(new java.awt.Dimension(80, 25));
        btnProp4.setMinimumSize(new java.awt.Dimension(0, 0));
        btnProp4.setPreferredSize(new java.awt.Dimension(280, 25));
        btnProp4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProp4(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 317, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(btnProp4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(29, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 76, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(4, 4, 4)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(btnProp4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(13, Short.MAX_VALUE)))
        );

        pan5.add(jPanel4, java.awt.BorderLayout.CENTER);

        panCentr.add(pan5);

        pan6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Версия программы "));
        pan6.setPreferredSize(new java.awt.Dimension(480, 30));
        pan6.setLayout(new java.awt.BorderLayout());

        jPanel3.setPreferredSize(new java.awt.Dimension(336, 40));

        checkBox1.setFont(frames.UGui.getFont(0,0));
        checkBox1.setText(" Откатить на предыдущую версию");
        checkBox1.setMaximumSize(new java.awt.Dimension(213, 22));
        checkBox1.setMinimumSize(new java.awt.Dimension(213, 22));
        checkBox1.setPreferredSize(new java.awt.Dimension(280, 22));
        checkBox1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkBox1StateChanged(evt);
            }
        });

        textPane1.setEditable(false);
        textPane1.setBorder(null);
        textPane1.setFont(frames.UGui.getFont(0,0));
        textPane1.setText("Откат на предыдущую версию программы");
        textPane1.setMinimumSize(new java.awt.Dimension(65, 14));
        textPane1.setPreferredSize(new java.awt.Dimension(160, 40));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(textPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(textPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pan6.add(jPanel3, java.awt.BorderLayout.CENTER);

        panCentr.add(pan6);

        getContentPane().add(panCentr, java.awt.BorderLayout.CENTER);

        panTool.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        panTool.setPreferredSize(new java.awt.Dimension(500, 50));

        btnClose.setFont(frames.UGui.getFont(0,0));
        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c009.gif"))); // NOI18N
        btnClose.setText("Выход");
        btnClose.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnClose.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnClose.setMaximumSize(new java.awt.Dimension(80, 25));
        btnClose.setMinimumSize(new java.awt.Dimension(0, 0));
        btnClose.setPreferredSize(new java.awt.Dimension(100, 25));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClose(evt);
            }
        });

        javax.swing.GroupLayout panToolLayout = new javax.swing.GroupLayout(panTool);
        panTool.setLayout(panToolLayout);
        panToolLayout.setHorizontalGroup(
            panToolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panToolLayout.createSequentialGroup()
                .addContainerGap(379, Short.MAX_VALUE)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panToolLayout.setVerticalGroup(
            panToolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panToolLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        getContentPane().add(panTool, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose
        this.dispose();
}//GEN-LAST:event_btnClose

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        //mAdmin.Property.window = null;
    }//GEN-LAST:event_formWindowClosed

    private void btnProp2(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProp2
        eProp.fontname.write(fontName[comboBox.getSelectedIndex()]);
        eProp.fontsize.write(String.valueOf(spinner2.getValue()));
        eProp.save();
    }//GEN-LAST:event_btnProp2

    private void btnProp3(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProp3
        eProp.cmd_word.write(txtWord.getText());
        eProp.cmd_excel.write(txtExcel.getText());
        eProp.cmd_html.write(txtHtml.getText());
        eProp.save();
}//GEN-LAST:event_btnProp3

    private void btnProp4(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProp4
        eProp.url_src.write(txtURL.getText());
        eProp.save();
    }//GEN-LAST:event_btnProp4

    private void checkBox1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkBox1StateChanged
        if(checkBox1.isSelected()) {
            eProp.old.write("1");
        } else {
            eProp.old.write("0");
        }
        eProp.save();
    }//GEN-LAST:event_checkBox1StateChanged

    // <editor-fold defaultstate="collapsed" desc="Generated Code">    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnProp2;
    private javax.swing.JButton btnProp3;
    private javax.swing.JButton btnProp4;
    private javax.swing.JCheckBox checkBox1;
    private javax.swing.JComboBox comboBox;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel pan3;
    private javax.swing.JPanel pan4;
    private javax.swing.JPanel pan5;
    private javax.swing.JPanel pan6;
    private javax.swing.JPanel panCentr;
    private javax.swing.JPanel panTool;
    private javax.swing.JSpinner spinner2;
    private javax.swing.JTextPane textPane1;
    private javax.swing.JTextPane textPane2;
    private javax.swing.JTextPane textPane3;
    private javax.swing.JTextPane textPane4;
    private javax.swing.JTextField txtExcel;
    private javax.swing.JTextField txtHtml;
    private javax.swing.JTextField txtURL;
    private javax.swing.JTextField txtWord;
    // End of variables declaration//GEN-END:variables
// </editor-fold> 
    
    public void initElements() {

        new FrameToFile(this, btnClose);
        
//        textPane1.setBackground(new java.awt.Color(212, 208, 200));
//        textPane2.setBackground(new java.awt.Color(212, 208, 200));
//        textPane3.setBackground(new java.awt.Color(212, 208, 200));
//        textPane4.setBackground(new java.awt.Color(212, 208, 200));
        
        txtWord.setText(eProp.cmd_word.read());
        txtExcel.setText(eProp.cmd_excel.read());
        txtHtml.setText(eProp.cmd_html.read());

        txtURL.setText(eProp.url_src.read());
        Integer num = Integer.valueOf(eProp.web_port.read());
        boolean start = (eProp.web_start.read().equals("true") == true) ? true : false;

        comboBox.setModel(new DefaultComboBoxModel(fontName));
        String name = eProp.fontname.read();
        Integer size = Integer.valueOf(eProp.fontsize.read());
        for (int i = 0; i < fontName.length; i++) {
            if (name.equals(fontName[i])) {
                comboBox.setSelectedIndex(i);
            }
        }
        spinner2.setValue(size);       
        checkBox1.setSelected(eProp.old.read().equals("1"));
    }
}
