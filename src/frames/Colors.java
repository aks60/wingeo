package frames;

import common.ePrefs;
import dataset.Query;
import dataset.Record;
import domain.eArtdet;
import domain.eColor;
import domain.eElemdet;
import domain.eFurndet;
import domain.eGlasdet;
import domain.eGroups;
import domain.eJoindet;
import domain.eKitdet;
import enums.TypeGrup;
import frames.swing.DefCellEditorBtn;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import frames.swing.DefCellRendererBool;
import frames.swing.DefTableModel;
import java.awt.Component;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import frames.swing.DefCellEditorNumb;
import frames.swing.TableFieldFilter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import report.sup.ExecuteCmd;
import report.sup.RTable;

public class Colors extends javax.swing.JFrame {

    private Query qColall = new Query(eColor.values());
    private Query qGroups = new Query(eGroups.values());
    private Query qColor = new Query(eColor.values());

    public Colors() {
        initComponents();
        initElements();
        loadingData();
        loadingModel();
        listenerAdd();
    }

    public void loadingData() {
        qColall.sql(eGroups.data(), eColor.up).sort(eColor.name);
        qGroups.sql(eGroups.data(), eGroups.grup, TypeGrup.COLOR_GRP.id).sort(eGroups.npp, eGroups.name);
    }

    public void loadingModel() {

        new DefTableModel(tab1, qGroups, eGroups.name, eGroups.id, eGroups.val);
        new DefTableModel(tab2, qColor, eColor.code, eColor.rgb, eColor.name, eColor.coef1, eColor.coef2, eColor.coef3, eColor.is_prod) {

            public Object getValueAt(int col, int row, Object val) {

                if (val != null && columns[col] == eColor.rgb) {
                    String hex = Integer.toHexString(Integer.parseInt(val.toString())).toUpperCase();
                    while (hex.length() < 6) {
                        hex = "0" + hex;
                    }
                    return hex;
                }
                return val;
            }
        };

        tab1.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                if (col == 1) {
                    int groupArr[] = qGroups.stream().mapToInt(rec -> rec.getInt(eGroups.id)).sorted().toArray();
                    int groupID = Integer.parseInt(value.toString());
                    int index = Arrays.stream(groupArr).boxed().collect(Collectors.toList()).indexOf(groupID);
                    return super.getTableCellRendererComponent(table, ++index * 1000, isSelected, hasFocus, row, col);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            }
        });
        tab2.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lab = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                int index = table.convertRowIndexToModel(row);
                int rgb = qColor.getAs(index, eColor.rgb);
                lab.setBackground(new java.awt.Color(rgb));
                return lab;
            }
        });
        tab2.getColumnModel().getColumn(3).setCellEditor(new DefCellEditorNumb(3));
        tab2.getColumnModel().getColumn(4).setCellEditor(new DefCellEditorNumb(3));
        tab2.getColumnModel().getColumn(5).setCellEditor(new DefCellEditorNumb(3));
        tab2.getColumnModel().getColumn(6).setCellRenderer(new DefCellRendererBool());

        UGui.setSelectedRow(tab1);
    }

    public void selectionTab1(ListSelectionEvent event) {

        UGui.stopCellEditing(tab1, tab2);
        List.of(qGroups, qColor).forEach(q -> q.execsql());
        int index = UGui.getIndexRec(tab1);
        if (index != -1) {
            Record record = qGroups.table(eGroups.up).get(index);
            Integer cgrup = record.getInt(eGroups.id);
            qColor.sql(eColor.data(), eColor.groups_id, cgrup).sort(eColor.code);
            ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab2);
        }
    }

    public void listenerAdd() {
        UGui.buttonCellEditor(tab2, 0).addActionListener(event -> {
            UGui.stopCellEditing(tab1, tab2);
            int rgb = qColor.getAs(UGui.getIndexRec(tab2), eColor.rgb);
            java.awt.Color color = JColorChooser.showDialog(this, "Âûáîð öâåòà", new java.awt.Color(rgb));
            if (color != null) {
                qColor.set(color.getRGB() & 0x00ffffff, UGui.getIndexRec(tab2), eColor.rgb);
                qColor.execsql();
            }
        });
        ((DefCellEditorBtn) tab2.getColumnModel().getColumn(0).getCellEditor()).getTextField().setEditable(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ppmCrud = new javax.swing.JPopupMenu();
        mInsert = new javax.swing.JMenuItem();
        mDelit = new javax.swing.JMenuItem();
        north = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        btnIns = new javax.swing.JButton();
        btnRep = new javax.swing.JButton();
        btnTest = new javax.swing.JButton();
        centr = new javax.swing.JPanel();
        pan1 = new javax.swing.JPanel();
        scr1 = new javax.swing.JScrollPane();
        tab1 = new javax.swing.JTable();
        scr2 = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JTable();
        south = new javax.swing.JPanel();

        mInsert.setFont(frames.UGui.getFont(1,0));
        mInsert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c033.gif"))); // NOI18N
        mInsert.setText("Äîáàâèòü");
        mInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmActionItems(evt);
            }
        });
        ppmCrud.add(mInsert);

        mDelit.setFont(frames.UGui.getFont(1,0));
        mDelit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c034.gif"))); // NOI18N
        mDelit.setText("Óäàëèòü");
        mDelit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmActionItems(evt);
            }
        });
        ppmCrud.add(mDelit);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Òåêñòóðû");
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage()));
        setMinimumSize(new java.awt.Dimension(800, 500));
        setPreferredSize(new java.awt.Dimension(900, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                Colors.this.windowClosed(evt);
            }
        });

        north.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        north.setMaximumSize(new java.awt.Dimension(32767, 31));
        north.setPreferredSize(new java.awt.Dimension(800, 29));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c009.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.ePrefs.locale); // NOI18N
        btnClose.setToolTipText(bundle.getString("Çàêðûòü")); // NOI18N
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

        btnDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c034.gif"))); // NOI18N
        btnDel.setToolTipText(bundle.getString("Óäàëèòü")); // NOI18N
        btnDel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnDel.setFocusable(false);
        btnDel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDel.setMaximumSize(new java.awt.Dimension(25, 25));
        btnDel.setMinimumSize(new java.awt.Dimension(25, 25));
        btnDel.setPreferredSize(new java.awt.Dimension(25, 25));
        btnDel.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnDel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelete(evt);
            }
        });

        btnIns.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c033.gif"))); // NOI18N
        btnIns.setToolTipText(bundle.getString("Äîáàâèòü")); // NOI18N
        btnIns.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnIns.setFocusable(false);
        btnIns.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnIns.setMaximumSize(new java.awt.Dimension(25, 25));
        btnIns.setMinimumSize(new java.awt.Dimension(25, 25));
        btnIns.setPreferredSize(new java.awt.Dimension(25, 25));
        btnIns.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnIns.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnIns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsert(evt);
            }
        });

        btnRep.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c053.gif"))); // NOI18N
        btnRep.setToolTipText(bundle.getString("Ïå÷àòü")); // NOI18N
        btnRep.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnRep.setFocusable(false);
        btnRep.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRep.setMaximumSize(new java.awt.Dimension(25, 25));
        btnRep.setMinimumSize(new java.awt.Dimension(25, 25));
        btnRep.setPreferredSize(new java.awt.Dimension(25, 25));
        btnRep.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnRep.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRepActionPerformed(evt);
            }
        });

        btnTest.setText("Test");
        btnTest.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnTest.setMaximumSize(new java.awt.Dimension(25, 25));
        btnTest.setMinimumSize(new java.awt.Dimension(25, 25));
        btnTest.setPreferredSize(new java.awt.Dimension(25, 25));
        btnTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTest(evt);
            }
        });

        javax.swing.GroupLayout northLayout = new javax.swing.GroupLayout(north);
        north.setLayout(northLayout);
        northLayout.setHorizontalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnIns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 641, Short.MAX_VALUE)
                .addComponent(btnTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        northLayout.setVerticalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRep, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(northLayout.createSequentialGroup()
                        .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(btnDel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnIns, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(btnTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        centr.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        centr.setPreferredSize(new java.awt.Dimension(800, 500));
        centr.setLayout(new java.awt.BorderLayout());

        pan1.setPreferredSize(new java.awt.Dimension(800, 500));
        pan1.setLayout(new javax.swing.BoxLayout(pan1, javax.swing.BoxLayout.LINE_AXIS));

        scr1.setBorder(null);
        scr1.setPreferredSize(new java.awt.Dimension(280, 584));

        tab1.setFont(frames.UGui.getFont(0,0));
        tab1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Íàçâàíèå ãðóïï", "Êîä ãðóïïû", "Êîýôôèöèåíò", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab1.setFillsViewportHeight(true);
        tab1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Colors.this.mousePressed(evt);
            }
        });
        scr1.setViewportView(tab1);
        if (tab1.getColumnModel().getColumnCount() > 0) {
            tab1.getColumnModel().getColumn(0).setPreferredWidth(280);
            tab1.getColumnModel().getColumn(1).setPreferredWidth(50);
            tab1.getColumnModel().getColumn(2).setPreferredWidth(40);
            tab1.getColumnModel().getColumn(2).setMaxWidth(60);
            tab1.getColumnModel().getColumn(3).setPreferredWidth(40);
            tab1.getColumnModel().getColumn(3).setMaxWidth(60);
        }

        pan1.add(scr1);

        scr2.setBorder(null);
        scr2.setAutoscrolls(true);
        scr2.setPreferredSize(new java.awt.Dimension(520, 400));

        tab2.setFont(frames.UGui.getFont(0,0));
        tab2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Êîä òåêñòóðû", "RGB", "Íàçâàíèå", "Êîýô.(îñíîâí.òåêñòóðà)", "Êîýô.(âíóòð.òåêñòóðà)", "Êîýô.(âíåøí.òåêñòóðà)", "Äëÿ èçäåëèé", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Boolean.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, true, true, true, true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab2.setFillsViewportHeight(true);
        tab2.setName("tab2"); // NOI18N
        tab2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Colors.this.mousePressed(evt);
            }
        });
        scr2.setViewportView(tab2);
        if (tab2.getColumnModel().getColumnCount() > 0) {
            tab2.getColumnModel().getColumn(0).setPreferredWidth(80);
            tab2.getColumnModel().getColumn(0).setMaxWidth(120);
            tab2.getColumnModel().getColumn(2).setPreferredWidth(320);
            tab2.getColumnModel().getColumn(3).setPreferredWidth(80);
            tab2.getColumnModel().getColumn(4).setPreferredWidth(80);
            tab2.getColumnModel().getColumn(5).setPreferredWidth(80);
            tab2.getColumnModel().getColumn(6).setPreferredWidth(60);
            tab2.getColumnModel().getColumn(6).setMaxWidth(120);
            tab2.getColumnModel().getColumn(7).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(7).setMaxWidth(60);
        }

        pan1.add(scr2);

        centr.add(pan1, java.awt.BorderLayout.CENTER);

        getContentPane().add(centr, java.awt.BorderLayout.CENTER);

        south.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.setMinimumSize(new java.awt.Dimension(100, 20));
        south.setPreferredSize(new java.awt.Dimension(800, 20));
        south.setLayout(new javax.swing.BoxLayout(south, javax.swing.BoxLayout.LINE_AXIS));
        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose
        this.dispose();
    }//GEN-LAST:event_btnClose

    private void btnDelete(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete

        if (tab1.getBorder() != null) {
            if (UGui.isDeleteRecord(tab1, this, tab2) == 0) {
                UGui.deleteRecord(tab1);
            }
        } else if (tab2.getBorder() != null) {
            int colorID = qColor.get(UGui.getIndexRec(tab2)).getInt(eColor.id);
            String mes[] = {"ÐÐµÐ»ÑŒÐ·Ñ ÑƒÐ´Ð°Ð»Ð¸Ñ‚ÑŒ Ð·Ð°Ð¿Ð¸ÑÑŒ Ð½Ð° ÐºÐ¾Ñ‚Ð¾Ñ€ÑƒÑŽ Ð¸Ð¼ÐµÑŽÑ‚ÑÑ ÑÑÑ‹Ð»ÐºÐ¸ Ð¸Ð· Ð´Ñ€ÑƒÐ³Ð¸Ñ… Ñ„Ð¾Ñ€Ð¼, ", "SQL Ð¿Ñ€ÐµÐ´ÑƒÐ¿Ñ€ÐµÐ¶Ð´ÐµÐ½Ð¸Ðµ", ""};
            if (!new Query(eKitdet.id).sql(eKitdet.data(), eKitdet.color1_id, colorID).isEmpty()) {
                mes[2] = "ÑÐ¼. Ñ„Ð¾Ñ€Ð¼Ñƒ ÐšÐ¾Ð¸Ð¿Ð»ÐµÐºÑ‚Ñ‹";
            } else if (!new Query(eJoindet.id).sql(eJoindet.data(), eJoindet.color_fk, colorID).isEmpty()) {
                mes[2] = "ÑÐ¼. Ñ„Ð¾Ñ€Ð¼Ñƒ Ð¡Ð¾ÐµÐ´Ð¸Ð½ÐµÐ½Ð¸Ñ";
            } else if (!new Query(eElemdet.id).sql(eElemdet.data(), eElemdet.color_fk, colorID).isEmpty()) {
                mes[2] = "ÑÐ¼. Ñ„Ð¾Ñ€Ð¼Ñƒ Ð¡Ð¾ÑÑ‚Ð°Ð²Ñ‹";
            } else if (!new Query(eGlasdet.id).sql(eGlasdet.data(), eGlasdet.color_fk, colorID).isEmpty()) {
                mes[2] = "ÑÐ¼. Ñ„Ð¾Ñ€Ð¼Ñƒ Ð—Ð°Ð¿Ð¾Ð»Ð½ÐµÐ½Ð¸Ñ";
            } else if (!new Query(eFurndet.id).sql(eFurndet.data(), eFurndet.color_fk, colorID).isEmpty()) {
                mes[2] = "ÑÐ¼. Ñ„Ð¾Ñ€Ð¼Ñƒ Ð¤ÑƒÑ€Ð½Ð¸Ñ‚ÑƒÑ€Ð°";
            } else if (!new Query(eArtdet.id).sql(eArtdet.data(), eArtdet.color_fk, colorID).isEmpty()) {
                mes[2] = "ÑÐ¼. Ñ„Ð¾Ñ€Ð¼Ñƒ ÐÑ€Ñ‚Ð¸ÐºÑƒÐ»Ñ‹";
            }
            if (mes[2].isEmpty() == false) {
                JOptionPane.showMessageDialog(this, mes[0] + mes[2], mes[1], JOptionPane.INFORMATION_MESSAGE);
            } else if (UGui.isDeleteRecord(tab2, this) == 0) {
                UGui.deleteRecord(tab2);
            }
        }
    }//GEN-LAST:event_btnDelete

    private void btnInsert(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsert

        if (tab1.getBorder() != null) {
            UGui.insertRecordCur(tab1, eGroups.up, (record) -> {
                record.set(eGroups.grup, TypeGrup.COLOR_GRP.id);
                record.set(eGroups.name, "");
                record.set(eGroups.val, 1);
            });

        } else if (tab2.getBorder() != null) {
            UGui.insertRecordCur(tab2, eColor.up, (record) -> {
                Record groupRec = qGroups.get(UGui.getIndexRec(tab1));
                Integer max = (qColor.stream().filter(rec -> rec.getInt(eColor.code) > 1000).count() > 0)
                        ? qColor.stream().filter(rec -> rec.getInt(eColor.code) > 1000)
                                .mapToInt(rec -> Integer.valueOf(rec.getStr(eColor.code)
                                .substring(rec.getStr(eColor.code).length() - 3))).max().getAsInt() : 0;
                int groupArr[] = qGroups.stream().mapToInt(rec -> rec.getInt(eGroups.id)).sorted().toArray();
                int index = Arrays.stream(groupArr).boxed().collect(Collectors.toList()).indexOf(groupRec.getInt(eGroups.id));
                record.setNo(eColor.groups_id, groupRec.getInt(eGroups.id));
                record.setNo(eColor.code, ++index * 1000 + max + 1);
                //record.setDev(eColor.name, "Ð¦Ð²ÐµÑ‚");
                record.setNo(eColor.rgb, 0xCCCCCC);
                record.setNo(eColor.coef1, 1);
                record.setNo(eColor.coef2, 1);
                record.setNo(eColor.coef3, 1);

                qColall.add(record);
            });
        }
    }//GEN-LAST:event_btnInsert

    private void windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosed
        UGui.stopCellEditingAndExecSql(getRootPane());
    }//GEN-LAST:event_windowClosed

    private void mousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mousePressed
        JTable table = (JTable) evt.getSource();
        UGui.updateBorderAndSql(table, List.of(tab1, tab2));
    }//GEN-LAST:event_mousePressed

    private void btnRepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRepActionPerformed
        JTable tab = null;
        if (tab1.getBorder() != null) {
            tab = tab1;
        } else if (tab2.getBorder() != null) {
            tab = tab2;
        }
        if (tab != null) {
            RTable.load("Ð¢ÐµÐºÑÑ‚ÑƒÑ€Ñ‹", tab);
            ExecuteCmd.documentType(this);
        }
    }//GEN-LAST:event_btnRepActionPerformed

    private void btnTest(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTest
        // 
    }//GEN-LAST:event_btnTest

    private void ppmActionItems(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppmActionItems
        if (evt.getSource() == mInsert) {
            btnInsert(new java.awt.event.ActionEvent(btnIns, -1, ""));
        } else if (evt.getSource() == mDelit) {
            btnDelete(new java.awt.event.ActionEvent(btnDel, -1, ""));
        }
    }//GEN-LAST:event_ppmActionItems

    private void tabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {
            JTable table = List.of(tab1, tab2).stream().filter(it -> it == evt.getSource()).findFirst().get();
            List.of(tab1, tab2).forEach(tab -> tab.setBorder(null));
            table.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255)));
            ppmCrud.show(table, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tabMouseClicked
    // <editor-fold defaultstate="collapsed" desc="Generated Code"> 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnIns;
    private javax.swing.JButton btnRep;
    private javax.swing.JButton btnTest;
    private javax.swing.JPanel centr;
    private javax.swing.JMenuItem mDelit;
    private javax.swing.JMenuItem mInsert;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan1;
    private javax.swing.JPopupMenu ppmCrud;
    private javax.swing.JScrollPane scr1;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JPanel south;
    private javax.swing.JTable tab1;
    private javax.swing.JTable tab2;
    // End of variables declaration//GEN-END:variables
// </editor-fold> 

    public void initElements() {

        ePrefs.getWin(this, btnClose, (e) -> {
            ePrefs.putWin(this, btnClose);
        });

        TableFieldFilter filterTable = new TableFieldFilter(1, tab2, tab1);
        south.add(filterTable, 0);
        filterTable.getTxt().grabFocus();

        tab1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255)));
        tab1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    selectionTab1(event);
                }
            }
        });
    }
}
