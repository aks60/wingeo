package frames.dialog;

import frames.swing.FrameToFile;
import frames.UGui;
import dataset.Query;
import dataset.Record;
import domain.eGroups;
import java.awt.Frame;
import javax.swing.JTable;
import common.listener.ListenerRecord;
import domain.eColor;
import domain.eParmap;
import domain.eParams;
import enums.TypeGrup;
import frames.swing.DefTableModel;
import frames.swing.TableFieldFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

//Текстура артикулов
public class ParDefault extends javax.swing.JDialog {

    private ListenerRecord listener;
    private Query qGroups = new Query(eGroups.values());
    private Query qParmap = new Query(eParmap.color_id1, eParmap.id, eParmap.groups_id).sql(eParmap.data(),eParmap.up);
    private Query qParams = new Query(eParams.text, eParams.id, eParams.groups_id).sql(eParams.data(), eParams.up);
    private List<Vector> parList = new ArrayList<Vector>();
    private DefaultTableModel dm = null;

    public ParDefault(Frame parent, ListenerRecord listener) {
        super(parent, true);
        initComponents();
        initElements();
        this.listener = listener;
        loadingModel();
        setVisible(true);
    }

    public ParDefault(Frame parent, ListenerRecord listener, int groupsID) {
        super(parent, true);
        initComponents();
        initElements();
        this.listener = listener;
        loadingModel();
        UGui.setSelectedKey(tab1, groupsID);
        setVisible(true);
    }

    private void loadingModel() {
        dm = (DefaultTableModel) tab2.getModel();
        qGroups.sq2(eGroups.data(), eGroups.grup, TypeGrup.PARAM_USER.id, eGroups.grup, TypeGrup.COLOR_MAP.id).sort(eGroups.name);
        for (Record rec : qParams) {
            parList.add(new Vector(List.of(rec.get(eParams.text), rec.get(eParams.id), rec.get(eParams.groups_id))));
        }
        Set set = new HashSet();
        for (Record rec : qParmap) {
            if (set.add(rec.getStr(eParmap.color_id1) + rec.getStr(eParmap.groups_id))) {
                Object param_name = eColor.find(rec.getInt(eParmap.color_id1)).getStr(eColor.name);
                parList.add(new Vector(List.of(param_name, rec.get(eParmap.id), rec.get(eParmap.groups_id))));
            }
        }
        Collections.sort(parList, (o1, o2) -> String.valueOf(o1.get(0)).compareTo(String.valueOf(o2.get(0))));
        new DefTableModel(tab1, qGroups, eGroups.name, eGroups.id);
        UGui.setSelectedRow(tab1);
    }

    private void selectionTab1() {
        dm.getDataVector().clear();
        int index = UGui.getIndexRec(tab1);
        if (index != -1) {
            Record groupsRec = qGroups.get(index);
            Integer groupsId = groupsRec.getInt(eGroups.id);
            for (Vector vector : parList) {
                if (groupsId.equals(vector.get(2))) {
                    dm.addRow(vector);
                }
            }
            dm.fireTableDataChanged();
            UGui.setSelectedRow(tab2);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        north = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnChoice = new javax.swing.JButton();
        centr = new javax.swing.JPanel();
        scr1 = new javax.swing.JScrollPane();
        tab1 = new javax.swing.JTable();
        scr2 = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JTable();
        south = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Параметры");

        north.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        north.setMaximumSize(new java.awt.Dimension(32767, 31));
        north.setPreferredSize(new java.awt.Dimension(400, 29));

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

        javax.swing.GroupLayout northLayout = new javax.swing.GroupLayout(north);
        north.setLayout(northLayout);
        northLayout.setHorizontalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnChoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 323, Short.MAX_VALUE)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        northLayout.setVerticalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(northLayout.createSequentialGroup()
                        .addComponent(btnChoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        centr.setPreferredSize(new java.awt.Dimension(400, 560));
        centr.setLayout(new javax.swing.BoxLayout(centr, javax.swing.BoxLayout.PAGE_AXIS));

        scr1.setMaximumSize(new java.awt.Dimension(32767, 600));
        scr1.setPreferredSize(new java.awt.Dimension(400, 300));

        tab1.setFont(frames.UGui.getFont(0,0));
        tab1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Название", "ID"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab1.setFillsViewportHeight(true);
        tab1.setName("tab1"); // NOI18N
        tab1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scr1.setViewportView(tab1);
        if (tab1.getColumnModel().getColumnCount() > 0) {
            tab1.getColumnModel().getColumn(0).setPreferredWidth(300);
            tab1.getColumnModel().getColumn(1).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(1).setMaxWidth(80);
        }

        centr.add(scr1);

        scr2.setMaximumSize(new java.awt.Dimension(32767, 600));
        scr2.setPreferredSize(new java.awt.Dimension(400, 300));

        tab2.setFont(frames.UGui.getFont(0,0));
        tab2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Значение", "ID", "GRUP"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

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
            tab2.getColumnModel().getColumn(0).setPreferredWidth(300);
            tab2.getColumnModel().getColumn(1).setPreferredWidth(60);
            tab2.getColumnModel().getColumn(1).setMaxWidth(80);
            tab2.getColumnModel().getColumn(2).setMinWidth(0);
            tab2.getColumnModel().getColumn(2).setPreferredWidth(0);
            tab2.getColumnModel().getColumn(2).setMaxWidth(0);
        }

        centr.add(scr2);

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
        Record record = new Record();
        record.add(tab2.getValueAt(tab2.getSelectedRow(), 0)); //text
        record.add(tab2.getValueAt(tab2.getSelectedRow(), 1)); //id
        record.add(tab2.getValueAt(tab2.getSelectedRow(), 2)); //groups_id
        listener.action(record);
        this.dispose();
    }//GEN-LAST:event_btnChoice

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
    private javax.swing.JPanel centr;
    private javax.swing.JPanel north;
    private javax.swing.JScrollPane scr1;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JPanel south;
    private javax.swing.JTable tab1;
    private javax.swing.JTable tab2;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>

    private void initElements() {

        FrameToFile.setFrameSize(this);
        new FrameToFile(this, btnClose);

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
}
