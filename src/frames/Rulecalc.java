package frames;

import common.eProp;
import frames.swing.comp.ProgressBar;
import common.listener.ListenerFrame;
import dataset.Field;
import dataset.Query;
import dataset.Record;
import domain.eArtikl;
import domain.eRulecalc;
import enums.TypeArt;
import enums.TypeForm;
import frames.dialog.DicArtikl2;
import frames.dialog.DicEnums;
import frames.swing.comp.DefTableModel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import common.listener.ListenerRecord;
import frames.swing.comp.DefCellEditorCheck;
import frames.swing.comp.DefCellEditorNumb;
import frames.swing.comp.DefCellRendererBool;
import frames.swing.comp.TableFieldFilter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import report.sup.ExecuteCmd;
import report.sup.RTable;
import startup.App;

public class Rulecalc extends javax.swing.JFrame {

    private Query qRulecalc = new Query(eRulecalc.values(), eArtikl.values());
    private ListenerRecord listenerArtikl, listenerForm;

    public Rulecalc() {
        initComponents();
        initElements();
        loadingData();
        loadingModel();
        listenerSet();
        tab2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255)));
    }

    public void loadingData() {
        try {
            qRulecalc.sql(eRulecalc.data(), eRulecalc.up).sort(eRulecalc.type);
            qRulecalc.query(eArtikl.up).join(qRulecalc, eArtikl.data(), eRulecalc.artikl_id, eArtikl.id);
        } catch (Exception e) {
            System.err.println("������:Rulecalc.loadingModel() " + e);
        }
    }

    public void loadingModel() {
        try {
            new DefTableModel(tab2, qRulecalc, eRulecalc.name, eRulecalc.type, eArtikl.code, eArtikl.name, eRulecalc.common, eRulecalc.quant, eRulecalc.size,
                    eRulecalc.coeff, eRulecalc.suppl, eRulecalc.color1, eRulecalc.color2, eRulecalc.color3, eRulecalc.sebes, eRulecalc.form) {

                public Object getValueAt(int col, int row, Object val) {

                    if (val != null) {
                        Field field = columns[col];
                        if (eRulecalc.type == field) {
                            int val2 = Integer.valueOf(String.valueOf(val));
                            return TypeArt.find(val2 / 100, 0) + "." + TypeArt.find(val2 / 100, val2 % 10);

                        } else if (eRulecalc.form == field) {
                            int val2 = (val.equals(0) == true) ? 1 : Integer.valueOf(String.valueOf(val));
                            return TypeForm.P00.find(val2).text();
                        }
                    }
                    return val;
                }
            };

            tab2.getColumnModel().getColumn(5).setCellEditor(new DefCellEditorCheck(6));
            tab2.getColumnModel().getColumn(6).setCellEditor(new DefCellEditorCheck(5));
            tab2.getColumnModel().getColumn(7).setCellEditor(new DefCellEditorNumb(3));
            tab2.getColumnModel().getColumn(8).setCellEditor(new DefCellEditorNumb(3));
            tab2.getColumnModel().getColumn(9).setCellEditor(new DefCellEditorCheck(5));
            tab2.getColumnModel().getColumn(10).setCellEditor(new DefCellEditorCheck(5));
            tab2.getColumnModel().getColumn(11).setCellEditor(new DefCellEditorCheck(5));
            tab2.getColumnModel().getColumn(12).setCellRenderer(new DefCellRendererBool());

            UGui.buttonCellEditor(tab2, 1).addActionListener(event -> {
                DicArtikl2 frame = new DicArtikl2(this, (artiklRec) -> {
                    UGui.stopCellEditing(tab2);
                    int val = artiklRec.getInt(eArtikl.level1) * 100 + artiklRec.getInt(eArtikl.level2);
                    qRulecalc.set(val, UGui.getIndexRec(tab2), eRulecalc.type);
                    ((DefaultTableModel) tab2.getModel()).fireTableRowsUpdated(tab2.getSelectedRow(), tab2.getSelectedRow());
                }, 1, 2, 3, 4, 5);
            });

            UGui.buttonCellEditor(tab2, 2).addActionListener(event -> {
                Record rulecalcRec = qRulecalc.get(UGui.getIndexRec(tab2));
                int id = rulecalcRec.getInt(eRulecalc.artikl_id, -1);
                int type = rulecalcRec.getInt(eRulecalc.type);
                int[] arr = (type == -1) ? new int[]{1, 2, 3, 4, 5} : new int[]{type / 100};
                new DicArtikl2(this, id, listenerArtikl, arr);
            });

            UGui.buttonCellEditor(tab2, 3).addActionListener(event -> {
                Record rulecalcRec = qRulecalc.get(UGui.getIndexRec(tab2));
                int id = rulecalcRec.getInt(eRulecalc.artikl_id, -1);
                int type = rulecalcRec.getInt(eRulecalc.type);
                int[] arr = (type == -1) ? new int[]{1, 2, 3, 4, 5} : new int[]{type / 100};
                new DicArtikl2(this, id, listenerArtikl, arr);
            });

            UGui.buttonCellEditor(tab2, 13).addActionListener(event -> {
                Record rulecalcRec = qRulecalc.get(UGui.getIndexRec(tab2));
                int ID = rulecalcRec.getInt(eRulecalc.form, -1);
                ID = (ID == 0) ? 1 : ID;
                new DicEnums(this, (record) -> UGui.cellParamEnum(record, tab2, eRulecalc.form, tab2), ID, TypeForm.values());
            });

            UGui.setSelectedRow(tab2);

        } catch (Exception e) {
            System.err.println("������:Rulecalc.loadingModel() " + e);
        }
    }

    public void listenerSet() {
        listenerArtikl = (arttiklRec) -> {
            int index = UGui.getIndexRec(tab2);
            UGui.stopCellEditing(tab2);
            qRulecalc.query(eRulecalc.up).set(arttiklRec.getInt(eArtikl.id), index, eRulecalc.artikl_id);
            qRulecalc.query(eArtikl.up).set(arttiklRec.get(eArtikl.code), index, eArtikl.code);
            qRulecalc.query(eArtikl.up).set(arttiklRec.get(eArtikl.name), index, eArtikl.name);
            if ("null".equals(qRulecalc.getAs(index, eRulecalc.name, "null"))) {
                qRulecalc.query(eRulecalc.up).set(arttiklRec.getStr(eArtikl.name), index, eRulecalc.name);
            }
            ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
        };
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
        btnReport = new javax.swing.JButton();
        btnFindArtikl = new javax.swing.JButton();
        btnClone = new javax.swing.JButton();
        centr = new javax.swing.JPanel();
        pan1 = new javax.swing.JPanel();
        scr2 = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JTable();
        south = new javax.swing.JPanel();

        mInsert.setFont(frames.UGui.getFont(1,0));
        mInsert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c033.gif"))); // NOI18N
        mInsert.setText("��������");
        mInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmActionItems(evt);
            }
        });
        ppmCrud.add(mInsert);

        mDelit.setFont(frames.UGui.getFont(1,0));
        mDelit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c034.gif"))); // NOI18N
        mDelit.setText("�������");
        mDelit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmActionItems(evt);
            }
        });
        ppmCrud.add(mDelit);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("������� �������");
        setMinimumSize(new java.awt.Dimension(800, 400));
        setPreferredSize(new java.awt.Dimension(800, 500));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                Rulecalc.this.windowClosed(evt);
            }
        });

        north.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        north.setMaximumSize(new java.awt.Dimension(32767, 31));
        north.setPreferredSize(new java.awt.Dimension(800, 29));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c009.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.eProp.locale); // NOI18N
        btnClose.setToolTipText(bundle.getString("�������")); // NOI18N
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
        btnDel.setToolTipText(bundle.getString("�������")); // NOI18N
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
        btnIns.setToolTipText(bundle.getString("��������")); // NOI18N
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

        btnReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c053.gif"))); // NOI18N
        btnReport.setToolTipText(bundle.getString("������")); // NOI18N
        btnReport.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnReport.setFocusable(false);
        btnReport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnReport.setMaximumSize(new java.awt.Dimension(25, 25));
        btnReport.setMinimumSize(new java.awt.Dimension(25, 25));
        btnReport.setPreferredSize(new java.awt.Dimension(25, 25));
        btnReport.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnReport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReport(evt);
            }
        });

        btnFindArtikl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c090.gif"))); // NOI18N
        btnFindArtikl.setToolTipText(bundle.getString("����� ������")); // NOI18N
        btnFindArtikl.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnFindArtikl.setFocusable(false);
        btnFindArtikl.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFindArtikl.setMaximumSize(new java.awt.Dimension(25, 25));
        btnFindArtikl.setMinimumSize(new java.awt.Dimension(25, 25));
        btnFindArtikl.setPreferredSize(new java.awt.Dimension(25, 25));
        btnFindArtikl.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnFindArtikl.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFindArtikl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindArtikl(evt);
            }
        });

        btnClone.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c065.gif"))); // NOI18N
        btnClone.setToolTipText(bundle.getString("����������� ������")); // NOI18N
        btnClone.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnClone.setFocusable(false);
        btnClone.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClone.setMaximumSize(new java.awt.Dimension(25, 25));
        btnClone.setMinimumSize(new java.awt.Dimension(25, 25));
        btnClone.setPreferredSize(new java.awt.Dimension(25, 25));
        btnClone.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnClone.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClone(evt);
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
                .addGap(18, 18, 18)
                .addComponent(btnClone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFindArtikl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 574, Short.MAX_VALUE)
                .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        northLayout.setVerticalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnDel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnIns, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnReport, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnFindArtikl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        centr.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        centr.setPreferredSize(new java.awt.Dimension(800, 550));
        centr.setLayout(new java.awt.BorderLayout());

        pan1.setLayout(new java.awt.BorderLayout());

        scr2.setBorder(null);

        tab2.setFont(frames.UGui.getFont(0,0));
        tab2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "�������� �������", "�������������", "�������", "��������", "�����", "����������", "��������", "�����������", "��������", "������� ��������", "�����. ��������", "�����. ��������", "������./����", "����� �������", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, true, true, true, true, true, true, true, true, true, true, true, false
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
        tab2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tab2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Rulecalc.this.mousePressed(evt);
            }
        });
        scr2.setViewportView(tab2);
        if (tab2.getColumnModel().getColumnCount() > 0) {
            tab2.getColumnModel().getColumn(0).setMinWidth(102);
            tab2.getColumnModel().getColumn(1).setPreferredWidth(160);
            tab2.getColumnModel().getColumn(3).setPreferredWidth(160);
            tab2.getColumnModel().getColumn(7).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(8).setPreferredWidth(60);
            tab2.getColumnModel().getColumn(9).setPreferredWidth(72);
            tab2.getColumnModel().getColumn(10).setPreferredWidth(72);
            tab2.getColumnModel().getColumn(11).setPreferredWidth(72);
            tab2.getColumnModel().getColumn(12).setPreferredWidth(60);
            tab2.getColumnModel().getColumn(13).setPreferredWidth(180);
            tab2.getColumnModel().getColumn(14).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(14).setMaxWidth(60);
        }

        pan1.add(scr2, java.awt.BorderLayout.CENTER);

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
        if (UGui.isDeleteRecord(tab2, this) == 0) {
            UGui.deleteRecord(tab2);
        }
    }//GEN-LAST:event_btnDelete

    private void btnInsert(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsert
        UGui.insertRecordCur(tab2, eRulecalc.up, (record) -> {
            //record.setDev(eRulecalc.name, "�������");
            record.set(eRulecalc.quant, 1);
            qRulecalc.query(eArtikl.up).add(eArtikl.up.newRecord(Query.SEL));
        });
    }//GEN-LAST:event_btnInsert

    private void btnReport(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReport
        RTable.load("������� �������", tab2);
        ExecuteCmd.documentType(this);
    }//GEN-LAST:event_btnReport

    private void mousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mousePressed

        JTable table = (JTable) evt.getSource();
        UGui.updateBorderAndSql(table, List.of(tab2));
    }//GEN-LAST:event_mousePressed

    private void windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosed
        UGui.stopCellEditingAndExecSql(getRootPane());
    }//GEN-LAST:event_windowClosed

    private void btnFindArtikl(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindArtikl
        if (tab2.getBorder() != null) {
            Record rulecalcRec = qRulecalc.get(UGui.getIndexRec(tab2));
            if (rulecalcRec != null) {
                Integer v = rulecalcRec.getInt(eRulecalc.artikl_id);
                if (v != null) {
                    Record artiklRec = eArtikl.find(v);
                    ProgressBar.create(this, new ListenerFrame() {
                        public void actionRequest(Object obj) {
                            App.Artikles.createFrame(Rulecalc.this, artiklRec);
                        }
                    });
                }
            }
        }
    }//GEN-LAST:event_btnFindArtikl

    private void ppmActionItems(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppmActionItems
        if (evt.getSource() == mInsert) {
            btnInsert(new java.awt.event.ActionEvent(btnIns, -1, ""));
        } else if (evt.getSource() == mDelit) {
            btnDelete(new java.awt.event.ActionEvent(btnDel, -1, ""));
        }
    }//GEN-LAST:event_ppmActionItems

    private void tabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {
            JTable table = List.of(tab2).stream().filter(it -> it == evt.getSource()).findFirst().get();
            List.of(tab2).forEach(tab -> tab.setBorder(null));
            table.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255)));
            ppmCrud.show(table, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tabMouseClicked

    private void btnClone(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClone
        int index = UGui.getIndexRec(tab2);
        if (index != -1 && JOptionPane.showConfirmDialog(this, "�� ������������� ������ ����������� ������� ������?",
                "�������������", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {

            List<Record> data = new ArrayList(qRulecalc);
            Record masterClon = UGui.cloneMaster(qRulecalc, tab2, eRulecalc.up, (clon) -> {
                clon.setNo(eRulecalc.name, clon.getStr(eRulecalc.name) + "-����");
            });
        }
    }//GEN-LAST:event_btnClone

// <editor-fold defaultstate="collapsed" desc="Generated Code"> 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClone;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnFindArtikl;
    private javax.swing.JButton btnIns;
    private javax.swing.JButton btnReport;
    private javax.swing.JPanel centr;
    private javax.swing.JMenuItem mDelit;
    private javax.swing.JMenuItem mInsert;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan1;
    private javax.swing.JPopupMenu ppmCrud;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JPanel south;
    private javax.swing.JTable tab2;
    // End of variables declaration//GEN-END:variables
// </editor-fold> 
    public void initElements() {

        App.loadLocationWin(this, btnClose, (e) -> {
            App.saveLocationWin(this, btnClose);
        });

        TableFieldFilter filterTable = new TableFieldFilter(2, tab2);
        south.add(filterTable, 0);
        filterTable.getTxt().grabFocus();

        List.of(btnIns, btnDel).forEach(b -> b.addActionListener(l -> UGui.stopCellEditing(tab2)));
    }
}
