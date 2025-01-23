package frames.dialog;

import common.ePref;
import frames.swing.FrameToFile;
import frames.UGui;
import dataset.Query;
import dataset.Record;
import domain.eArtdet;
import domain.eColor;
import java.awt.CardLayout;
import javax.swing.JToggleButton;
import javax.swing.table.DefaultTableModel;
import frames.swing.DefTableModel;
import domain.eGroups;
import enums.TypeGrup;
import enums.UseColor;
import common.listener.ListenerRecord;
import dataset.Field;
import domain.eParmap;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ParColor extends javax.swing.JDialog {

    private Query qArtdet = new Query(eArtdet.values());
    private Query qGroupsMap = new Query(eGroups.values());
    private Query qGroupsGrp = new Query(eGroups.values());
    private Query qColor = new Query(eColor.values());
    private ListenerRecord listener;
    private Field filter = null;
    private int colorID = -1;

    public ParColor(java.awt.Frame parent, ListenerRecord listener, Field filter, int artiklID, int colorID) {
        super(parent, true);
        initComponents();
        initElements();
        this.listener = listener;
        this.filter = filter;
        this.colorID = colorID;
        loadingData(artiklID);
        loadingModel();
        setVisible(true);
    }

    public void loadingData(int artikl_id) {

        qArtdet.sql(eArtdet.data(), eArtdet.artikl_id, artikl_id);
        List<Integer> listId = new Query(eParmap.values()).sql(eParmap.data(), filter, 1)
                .stream().map(rec -> rec.getInt(eParmap.groups_id)).collect(Collectors.toList());
        if (listId.isEmpty() == false) {
            qGroupsMap.sql(eGroups.data(), eGroups.id, listId).sort(eGroups.name);
        }        
        qGroupsGrp.sql(eGroups.data(), eGroups.grup, TypeGrup.COLOR_GRP.id);

        Record color1 = eColor.up.newRecord(Query.SEL);
        color1.setNo(eColor.groups_id, UseColor.automatic[0]);
        color1.setNo(eColor.id, UseColor.automatic[0]);
        color1.setNo(eColor.code, UseColor.automatic[0]);
        color1.setNo(eColor.name, UseColor.automatic[1]);
        qColor.add(color1);
        Record color2 = eColor.up.newRecord(Query.SEL);
        color2.setNo(eColor.groups_id, UseColor.precision[0]);
        color2.setNo(eColor.id, UseColor.precision[0]);
        color2.setNo(eColor.code, UseColor.precision[0]);
        color2.setNo(eColor.name, UseColor.precision[1]);
        qColor.add(color2);

        for (Record record : qArtdet) {
            if (record.getInt(eArtdet.color_fk) > 0) {
                qColor.addAll(new Query(eColor.values()).sql(eColor.data(), eColor.id, record.getInt(eArtdet.color_fk)));
            } else if (record.getInt(eArtdet.color_fk) < 0) {
                qColor.addAll(new Query(eColor.values()).sql(eColor.data(), eColor.groups_id, record.getInt(eArtdet.color_fk)));
            }
        }
    }

    public void loadingModel() {
        new DefTableModel(tab1, qColor, eColor.groups_id, eColor.code, eColor.name, eColor.id) {
            @Override
            public Object getValueAt(int col, int row, Object val) {
                if (col == 0) {
                    Record colorRec = qColor.get(row);
                    if (colorRec.getInt(eColor.groups_id) == 0 || colorRec.getInt(eColor.groups_id) == 100000) {
                        return "Автоподбор";
                    }
                    Record groupRec = qGroupsGrp.find(eGroups.data(), eGroups.id, colorRec.getInt(eColor.groups_id));
                    return groupRec.get(eGroups.name);
                }
                return val;
            }
        };
        new DefTableModel(tab2, qGroupsMap, eGroups.id, eGroups.name);
        tab1.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lab = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                int rgb = (row > 1) ? qColor.getAs(row - 2, eColor.rgb, 16777215) : 15921906;
                lab.setBackground(new java.awt.Color(rgb));
                return lab;
            }
        });
        ((DefaultTableModel) tab1.getModel()).fireTableDataChanged();
        if (colorID > 0) {
            UGui.setSelectedKey(tab1, colorID);
        } else {
            UGui.setSelectedRow(tab1);
        }
        UGui.setSelectedRow(tab2);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        north = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnChouce = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnCard1 = new javax.swing.JToggleButton();
        btnCard2 = new javax.swing.JToggleButton();
        centr = new javax.swing.JPanel();
        pan1 = new javax.swing.JPanel();
        scr1 = new javax.swing.JScrollPane();
        tab1 = new javax.swing.JTable();
        pan2 = new javax.swing.JPanel();
        scr2 = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JTable();
        south = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Параметры текстуры");
        setMinimumSize(new java.awt.Dimension(200, 44));

        north.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        north.setMaximumSize(new java.awt.Dimension(32767, 31));
        north.setPreferredSize(new java.awt.Dimension(340, 29));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c009.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.ePref.locale); // NOI18N
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

        btnChouce.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c044.gif"))); // NOI18N
        btnChouce.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btnChouce.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnChouce.setFocusable(false);
        btnChouce.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnChouce.setMaximumSize(new java.awt.Dimension(25, 25));
        btnChouce.setMinimumSize(new java.awt.Dimension(25, 25));
        btnChouce.setPreferredSize(new java.awt.Dimension(25, 25));
        btnChouce.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnChouce.addActionListener(new java.awt.event.ActionListener() {
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
                btnRemov(evt);
            }
        });

        buttonGroup1.add(btnCard1);
        btnCard1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnCard1.setSelected(true);
        btnCard1.setText("1");
        btnCard1.setToolTipText("");
        btnCard1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnCard1.setPreferredSize(new java.awt.Dimension(30, 25));
        btnCard1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCard(evt);
            }
        });

        buttonGroup1.add(btnCard2);
        btnCard2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnCard2.setText("2");
        btnCard2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnCard2.setPreferredSize(new java.awt.Dimension(30, 25));
        btnCard2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCard(evt);
            }
        });

        javax.swing.GroupLayout northLayout = new javax.swing.GroupLayout(north);
        north.setLayout(northLayout);
        northLayout.setHorizontalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnChouce, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70)
                .addComponent(btnCard1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCard2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
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
                            .addComponent(btnChouce, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnCard1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnCard2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        centr.setPreferredSize(new java.awt.Dimension(412, 160));
        centr.setLayout(new java.awt.CardLayout());

        pan1.setName("pan1"); // NOI18N
        pan1.setPreferredSize(new java.awt.Dimension(350, 400));
        pan1.setLayout(new java.awt.BorderLayout());

        scr1.setBorder(null);

        tab1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Группа", "Код", "Название", "id"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab1.setFillsViewportHeight(true);
        tab1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ParColor.this.mouseClicked(evt);
            }
        });
        scr1.setViewportView(tab1);
        if (tab1.getColumnModel().getColumnCount() > 0) {
            tab1.getColumnModel().getColumn(0).setPreferredWidth(80);
            tab1.getColumnModel().getColumn(1).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(2).setPreferredWidth(300);
            tab1.getColumnModel().getColumn(3).setResizable(false);
            tab1.getColumnModel().getColumn(3).setPreferredWidth(0);
        }

        pan1.add(scr1, java.awt.BorderLayout.CENTER);

        centr.add(pan1, "card1");

        pan2.setName("pan2"); // NOI18N
        pan2.setPreferredSize(new java.awt.Dimension(350, 180));
        pan2.setLayout(new java.awt.BorderLayout());

        scr2.setBorder(null);

        tab2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"111", "2222222222"},
                {"111", "2222222222"}
            },
            new String [] {
                "Код", "Название "
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab2.setFillsViewportHeight(true);
        tab2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ParColor.this.mouseClicked(evt);
            }
        });
        scr2.setViewportView(tab2);
        if (tab2.getColumnModel().getColumnCount() > 0) {
            tab2.getColumnModel().getColumn(0).setPreferredWidth(80);
            tab2.getColumnModel().getColumn(0).setMaxWidth(80);
        }

        pan2.add(scr2, java.awt.BorderLayout.CENTER);

        centr.add(pan2, "card2");

        getContentPane().add(centr, java.awt.BorderLayout.CENTER);

        south.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.setMinimumSize(new java.awt.Dimension(100, 20));
        south.setName(""); // NOI18N
        south.setPreferredSize(new java.awt.Dimension(350, 20));

        javax.swing.GroupLayout southLayout = new javax.swing.GroupLayout(south);
        south.setLayout(southLayout);
        southLayout.setHorizontalGroup(
            southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 307, Short.MAX_VALUE)
        );
        southLayout.setVerticalGroup(
            southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose
        this.dispose();
    }//GEN-LAST:event_btnClose

    private void btnChoice(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChoice
        if (btnCard1.isSelected() == true) {
            int index = UGui.getIndexRec(tab1);
            Record record = qColor.get(index);
            listener.action(qColor.get(index));
        } else {
            Record record = qGroupsMap.get(UGui.getIndexRec(tab2));
            listener.action(record);
        }
        this.dispose();
    }//GEN-LAST:event_btnChoice

    private void btnRemov(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemov
        listener.action(new Record(Arrays.asList(null, null)));
        this.dispose();
    }//GEN-LAST:event_btnRemov

    private void btnCard(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCard
        JToggleButton btn = (JToggleButton) evt.getSource();
        if (btn == btnCard1) {
            ((CardLayout) centr.getLayout()).show(centr, "card1");
        } else if (btn == btnCard2) {
            ((CardLayout) centr.getLayout()).show(centr, "card2");
        }
    }//GEN-LAST:event_btnCard

    private void mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseClicked
        if (evt.getClickCount() == 2) {
            btnChoice(null);
        }
    }//GEN-LAST:event_mouseClicked

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnCard1;
    private javax.swing.JToggleButton btnCard2;
    private javax.swing.JButton btnChouce;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnRemove;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel centr;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan1;
    private javax.swing.JPanel pan2;
    private javax.swing.JScrollPane scr1;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JPanel south;
    private javax.swing.JTable tab1;
    private javax.swing.JTable tab2;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>

    public void initElements() {

        ePref.read(this, btnClose, (e) -> {
            ePref.write(this, btnClose);
        });         
        btnRemove.setVisible(false);
    }
}
