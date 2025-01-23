package frames.dialog;

import common.UCom;
import common.ePref;
import frames.swing.FrameToFile;
import frames.UGui;
import dataset.Query;
import dataset.Record;
import domain.eColor;
import domain.eGroups;
import enums.TypeGrup;
import java.awt.Frame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import frames.swing.DefTableModel;
import java.awt.Component;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import common.listener.ListenerRecord;
import dataset.Field;
import domain.eArtdet;
import enums.UseColor;
import frames.swing.TableFieldFilter;
import java.util.List;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

//�������� ���������
//������������� ������ �� ���������� ������ ����� ��. �������� ���������
public class DicColor extends javax.swing.JDialog {

    private ListenerRecord listener;
    private Query qColgrp = new Query(eGroups.values());
    private Query qColorAll = new Query(eColor.values());
    private Query qColor = new Query(eColor.id, eColor.name);
    private boolean master = false;
    private Integer[] colorArr = null;

    public DicColor(Frame parent, ListenerRecord listener, boolean master, boolean remove) {
        super(parent, true);
        this.master = master;
        initComponents();
        qColgrp.sql(eGroups.data(), eGroups.grup, TypeGrup.COLOR_GRP.id).sort(eGroups.npp, eGroups.name);
        qColorAll.sql(eColor.data(), eColor.up).sort(eColor.name);
        initElements();
        this.listener = listener;
        loadingModel();
        btnRemove.setVisible(remove);
        setVisible(true);
    }

    public DicColor(Frame parent, ListenerRecord listener, HashSet<Record> colorSet, boolean remove, boolean auto) {
        super(parent, true);
        initComponents();
        initElements();
        this.listener = listener;
        qColorAll.addAll(colorSet);
        loadingData(colorSet, auto);
        loadingModel();
        btnRemove.setVisible(remove);
        setVisible(true);
    }

//    public DicColor(Frame parent, ListenerRecord listener, HashSet<Record> colorSet, String colorTxt, boolean remove, boolean auto) {
//        super(parent, true);
//        initComponents();
//        initElements();
//        this.listener = listener;
//        this.colorArr = UCom.parserInt(colorTxt);
//
//        if (colorArr.length != 0) {
//            for (Record rec : colorSet) {
//                for (int i = 0; i < colorArr.length; i = i + 2) { //�������
//                    if (rec.getInt(eColor.code) >= colorArr[i] && rec.getInt(eColor.code) <= colorArr[i + 1]) {
//                        qColorAll.add(rec);
//                    }
//                }
//            }
//        } else {
//            qColorAll.addAll(colorSet);
//        }
//        loadingData(colorSet, auto);
//        loadingModel();
//        btnRemove.setVisible(remove);
//        setVisible(true);
//    }
    private void loadingData(HashSet<Record> colorSet, boolean auto) {

        Query colgrpList = new Query(eGroups.values()).sql(eGroups.data(), eGroups.grup, TypeGrup.COLOR_GRP.id).sort(eGroups.npp, eGroups.name);

        if (auto == true) {
            Record autoRec = eGroups.up.newRecord(Query.SEL);
            autoRec.setNo(eGroups.id, -3);
            autoRec.setNo(eGroups.grup, -3);
            autoRec.setNo(eGroups.name, UseColor.automatic[1]);
            colgrpList.add(autoRec);
            Record autoRec2 = eColor.up.newRecord(Query.SEL);
            autoRec2.set(eColor.id, 0);
            autoRec2.set(eColor.groups_id, -3);
            autoRec2.set(eColor.name, UseColor.automatic[1]);
            colgrpList.add(autoRec2);
        }
        colgrpList.forEach(colgrpRec -> {
            for (Record colorRec : colorSet) {
                if (colorRec.getInt(eColor.groups_id) == colgrpRec.getInt(eGroups.id)) {
                    qColgrp.add(colgrpRec);
                    break;
                }
            }
        });
        Collections.sort(qColorAll, (o1, o2) -> (o1.getStr(eColor.name)).compareTo(o2.getStr(eColor.name)));
    }

    private void loadingModel() {
        new DefTableModel(tab1, qColgrp, eGroups.name);
        new DefTableModel(tab2, qColor, eColor.code, eColor.name);
        tab2.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lab = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                int rgb = qColor.getAs(row, eColor.rgb);
                lab.setBackground(new java.awt.Color(rgb));
                return lab;
            }
        });
        UGui.setSelectedRow(tab1);
    }

    private void selectionTab1() {
        int index = UGui.getIndexRec(tab1);
        if (index != -1) {
            Record record = qColgrp.get(index);
            int colgrpId = record.getInt(eGroups.id);
            qColor.clear();
            qColorAll.forEach(rec -> {
                if (rec.getInt(eColor.groups_id) == colgrpId) {
                    qColor.add(rec);
                }
            });
            ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
            if (master == false) {
                UGui.setSelectedRow(tab2); //��������� ����� ����� �������
                tab2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255)));
                tab1.setBorder(null);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        north = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnChoice = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        centr = new javax.swing.JPanel();
        pan1 = new javax.swing.JPanel();
        scr1 = new javax.swing.JScrollPane();
        tab1 = new javax.swing.JTable();
        scr2 = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JTable();
        south = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("���������� �������");
        setPreferredSize(new java.awt.Dimension(412, 600));

        north.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        north.setMaximumSize(new java.awt.Dimension(32767, 31));
        north.setPreferredSize(new java.awt.Dimension(400, 29));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c009.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.ePref.locale); // NOI18N
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

        btnChoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c044.gif"))); // NOI18N
        btnChoice.setToolTipText(bundle.getString("�������")); // NOI18N
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
        btnRemove.setToolTipText(bundle.getString("��������")); // NOI18N
        btnRemove.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnRemove.setFocusable(false);
        btnRemove.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRemove.setMaximumSize(new java.awt.Dimension(25, 25));
        btnRemove.setMinimumSize(new java.awt.Dimension(25, 25));
        btnRemove.setPreferredSize(new java.awt.Dimension(25, 25));
        btnRemove.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemov(evt);
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 292, Short.MAX_VALUE)
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

        centr.setPreferredSize(new java.awt.Dimension(412, 560));
        centr.setLayout(new java.awt.BorderLayout());

        pan1.setLayout(new java.awt.BorderLayout());

        scr1.setPreferredSize(new java.awt.Dimension(412, 160));

        tab1.setFont(frames.UGui.getFont(0,0));
        tab1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "������ �������"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab1.setFillsViewportHeight(true);
        tab1.setName("tab1"); // NOI18N
        tab1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });
        scr1.setViewportView(tab1);

        pan1.add(scr1, java.awt.BorderLayout.NORTH);

        tab2.setFont(frames.UGui.getFont(0,0));
        tab2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "��� ��������", "�������� ��������"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
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
        });
        scr2.setViewportView(tab2);
        if (tab2.getColumnModel().getColumnCount() > 0) {
            tab2.getColumnModel().getColumn(0).setPreferredWidth(60);
            tab2.getColumnModel().getColumn(0).setMaxWidth(80);
        }

        pan1.add(scr2, java.awt.BorderLayout.CENTER);

        centr.add(pan1, java.awt.BorderLayout.CENTER);

        getContentPane().add(centr, java.awt.BorderLayout.CENTER);

        south.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.setMinimumSize(new java.awt.Dimension(100, 20));
        south.setPreferredSize(new java.awt.Dimension(400, 20));
        south.setLayout(new javax.swing.BoxLayout(south, javax.swing.BoxLayout.LINE_AXIS));
        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose
        this.dispose();
    }//GEN-LAST:event_btnClose

    private void btnChoice(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChoice

        if (tab1.getBorder() != null) {
            int index = UGui.getIndexRec(tab1);
            if (index != -1) {
                listener.action(qColgrp.get(index));
                this.dispose();
            }
        } else if (tab2.getBorder() != null) {
            int index = UGui.getIndexRec(tab2);
            if (index != -1) {
                listener.action(qColor.get(index));
                this.dispose();
            }
        } else {
            JOptionPane.showMessageDialog(this, "������ �� �������", "��������������", JOptionPane.NO_OPTION);
        }
    }//GEN-LAST:event_btnChoice

    private void btnRemov(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemov
        listener.action(new Record(Arrays.asList(null, null)));
        this.dispose();
    }//GEN-LAST:event_btnRemov

    private void tabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabMouseClicked
        JTable table = (JTable) evt.getSource();
        UGui.updateBorderAndSql(table, List.of(tab1, tab2));
        if (evt.getClickCount() == 2) {
            btnChoice(null);
        }
    }//GEN-LAST:event_tabMouseClicked

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChoice;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnRemove;
    private javax.swing.JPanel centr;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan1;
    private javax.swing.JScrollPane scr1;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JPanel south;
    private javax.swing.JTable tab1;
    private javax.swing.JTable tab2;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>

    private void initElements() {

        ePref.read(this, btnClose, (e) -> {
            ePref.write(this, btnClose);
        }); 

        TableFieldFilter filterTable = new TableFieldFilter(0, tab1, tab2);
        south.add(filterTable, 0);
        filterTable.getTxt().grabFocus();

        tab1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    selectionTab1();
                }
            }
        });
    }

    public static HashSet<Record> filterTxt(List<Record> colorSrc, String colorTxt) {
        HashSet<Record> colorRet = new HashSet<Record>();
        try {
            Integer[] colorArr = UCom.parserInt(colorTxt);
            colorArr = UCom.parserInt(colorTxt);

            if (colorArr.length != 0) {
                for (Record rec : colorSrc) {
                    for (int i = 0; i < colorArr.length; i = i + 2) { //�������
                        if (rec.getInt(eColor.code) >= colorArr[i] && rec.getInt(eColor.code) <= colorArr[i + 1]) {
                            colorRet.add(rec);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка: DicColor.filterTxt() " + e);
        }
        return colorRet;
    }

    public static HashSet<Record> filterDet(HashSet<Record> colorSrc, List<Record> artdetList, Field field) {
        HashSet<Record> colorSet = new HashSet<Record>();
        try {
            artdetList.forEach(rec -> {
                if (rec.getInt(field) == 1) {

                    if (rec.getInt(eArtdet.color_fk) < 0) { //��� �������� ����� color_fk                        
                        colorSrc.forEach(rec2 -> {
                            if (rec2.getInt(eColor.groups_id) == rec.getInt(eArtdet.color_fk)) {
                                colorSet.add(rec2);
                            }
                        });
                    } else { //�������� color_fk 
                        colorSet.add(eColor.find(rec.getInt(eArtdet.color_fk)));
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("Ошибка: DicColor.filterDet() " + e);
        }
        return colorSet;
    }
}
