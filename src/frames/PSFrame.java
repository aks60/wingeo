package frames;

import common.ePref;
import frames.swing.FrameToFile;
import dataset.Query;
import domain.eProject;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import frames.swing.DefTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;
import javax.swing.JTable;
import startup.Test;

public class PSFrame extends javax.swing.JFrame {

    private TableRowSorter<DefTableModel> sorter1 = null;
    private TableRowSorter<DefTableModel> sorter2 = null;
    private TableRowSorter<DefTableModel> sorter3 = null;
    private Connection cn = Test.connect1();
    private Query qOrders = new Query(eProject.values());

    public PSFrame() {
        initComponents();
        initElements();
        loadingData();
        loadingTab1();
    }

    private void loadingData() {

    }

    private void loadingTab1() {
        try {
            sql1.setText("select * from furnlst order by fname");
            ResultSet rs = cn.createStatement().executeQuery(sql1.getText());
            ResultSetMetaData rsmd = rs.getMetaData();

            Vector column = new Vector();
            for (int i = 0; i < rsmd.getColumnCount(); ++i) {
                column.add(rsmd.getColumnName(i + 1));
            }
            Vector<Vector> data = new Vector();
            while (rs.next()) {
                Vector vector = new Vector();
                for (int i = 0; i < column.size(); i++) {
                    vector.add(rs.getObject(i + 1));
                }
                data.add(vector);
            }
            tab1.setModel(new DefaultTableModel(data, column));

            UGui.setSelectedRow(tab1);
            //((DefaultTableModel) tab2.getModel()).setRowCount(0);
            //((DefaultTableModel) tab3.getModel()).setRowCount(0);

//            TableCellRenderer headerRenderer = new VerticalTableHeaderCellRenderer();
//            tab1.getColumnModel().getColumn(1).setHeaderRenderer(headerRenderer);
//            Enumeration<TableColumn> columns = tab1.getColumnModel().getColumns();
//            while (columns.hasMoreElements()) {
//                ((TableColumn) columns.nextElement()).setHeaderRenderer(headerRenderer);
//            }

        } catch (Exception e) {
            System.err.println("frames.TestFrame.selectionTab1() " + e);
        }
    }

    private void selectionTab2() {
        try {
            int row = tab1.getSelectedRow();
            if (row != -1) {
                Object id = tab1.getValueAt(row, 1);
                sql2.setText("select a.* from FURNSPC a where a.FUNIC = " + id);
                sql2.setText("select a.*, b.aname from FURNSPC a left join artikls b on b.anumb = a.anumb  where a.FUNIC = " + id + " order by a.anumb");
                ResultSet rs = cn.createStatement().executeQuery(sql2.getText());
                ResultSetMetaData rsmd = rs.getMetaData();

                Vector column = new Vector();
                for (int i = 0; i < rsmd.getColumnCount(); ++i) {
                    column.add(rsmd.getColumnName(i + 1));
                }
                Vector<Vector> data = new Vector();
                while (rs.next()) {
                    Vector vector = new Vector();
                    for (int i = 0; i < column.size(); i++) {
                        vector.add(rs.getObject(i + 1));
                    }
                    data.add(vector);
                }
                tab2.setModel(new DefaultTableModel(data, column));
                ((DefaultTableModel) tab3.getModel()).setRowCount(0);
                UGui.setSelectedRow(tab2);
            } else {
                sql2.setText("");
                ((DefaultTableModel) tab2.getModel()).getDataVector().clear();
                ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
            }

        } catch (Exception e) {
            System.err.println("frames.TestFrame.selectionTab2() " + e);
        }
    }

    private void selectionTab3() {
        try {
            /*int row = tab2.getSelectedRow();
            if (row != -1) {
                Object id = tab2.getValueAt(row, 1);
                sql3.setText("select * from connspc where cunic = " + id + " order by anumb");
                ResultSet rs = cn.createStatement().executeQuery(sql3.getText());
                ResultSetMetaData rsmd = rs.getMetaData();

                Vector column = new Vector();
                for (int i = 0; i < rsmd.getColumnCount(); ++i) {
                    column.add(rsmd.getColumnName(i + 1));
                }
                Vector<Vector> data = new Vector();
                while (rs.next()) {
                    Vector vector = new Vector();
                    for (int i = 0; i < column.size(); i++) {
                        vector.add(rs.getObject(i + 1));
                    }
                    data.add(vector);
                }
                tab3.setModel(new DefaultTableModel(data, column));
                Util.setSelectedRow(tab3);
            }*/
        } catch (Exception e) {
            System.err.println("frames.TestFrame.selectionTab3() " + e);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        north = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        center = new javax.swing.JPanel();
        pan1 = new javax.swing.JPanel();
        sql1 = new javax.swing.JTextField();
        pan11 = new javax.swing.JPanel();
        lab1 = new javax.swing.JLabel();
        txt1 = new javax.swing.JTextField();
        btn1 = new javax.swing.JButton();
        scr1 = new javax.swing.JScrollPane();
        tab1 = new javax.swing.JTable();
        pan2 = new javax.swing.JPanel();
        pan22 = new javax.swing.JPanel();
        lab2 = new javax.swing.JLabel();
        txt2 = new javax.swing.JTextField();
        sql2 = new javax.swing.JTextField();
        btn2 = new javax.swing.JButton();
        scr2 = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JTable();
        pan3 = new javax.swing.JPanel();
        pan33 = new javax.swing.JPanel();
        lab3 = new javax.swing.JLabel();
        txt3 = new javax.swing.JTextField();
        sql3 = new javax.swing.JTextField();
        btn3 = new javax.swing.JButton();
        scr3 = new javax.swing.JScrollPane();
        tab3 = new javax.swing.JTable();
        south = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("TEST");

        north.setMaximumSize(new java.awt.Dimension(32767, 31));
        north.setPreferredSize(new java.awt.Dimension(900, 29));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c009.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.ePref.locale); // NOI18N
        btnClose.setToolTipText(bundle.getString("Закрыть")); // NOI18N
        btnClose.setBorder(null);
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

        javax.swing.GroupLayout northLayout = new javax.swing.GroupLayout(north);
        north.setLayout(northLayout);
        northLayout.setHorizontalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addContainerGap(880, Short.MAX_VALUE)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        northLayout.setVerticalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, northLayout.createSequentialGroup()
                .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        center.setLayout(new javax.swing.BoxLayout(center, javax.swing.BoxLayout.PAGE_AXIS));

        pan1.setPreferredSize(new java.awt.Dimension(800, 24));
        pan1.setLayout(new java.awt.BorderLayout());

        sql1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        sql1.setText("'select * from LISTPRJ'");
        sql1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        sql1.setMinimumSize(new java.awt.Dimension(6, 20));
        sql1.setPreferredSize(new java.awt.Dimension(600, 20));
        pan1.add(sql1, java.awt.BorderLayout.CENTER);

        pan11.setLayout(new java.awt.BorderLayout());

        lab1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lab1.setPreferredSize(new java.awt.Dimension(80, 18));
        pan11.add(lab1, java.awt.BorderLayout.WEST);

        txt1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txt1.setPreferredSize(new java.awt.Dimension(61, 20));
        txt1.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txt1CaretUpdate(evt);
            }
        });
        pan11.add(txt1, java.awt.BorderLayout.CENTER);

        pan1.add(pan11, java.awt.BorderLayout.WEST);

        btn1.setText("...");
        btn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn1ActionPerformed(evt);
            }
        });
        pan1.add(btn1, java.awt.BorderLayout.EAST);

        center.add(pan1);

        scr1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scr1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scr1.setPreferredSize(new java.awt.Dimension(800, 204));

        tab1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7", "Title 8", "Title 9", "Title 10", "Title 11", "Title 12", "Title 13", "Title 14"
            }
        ));
        tab1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tab1.setFillsViewportHeight(true);
        tab1.setName("tab1"); // NOI18N
        tab1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tab1MousePressed(evt);
            }
        });
        scr1.setViewportView(tab1);

        center.add(scr1);

        pan2.setPreferredSize(new java.awt.Dimension(800, 24));
        pan2.setLayout(new java.awt.BorderLayout());

        pan22.setLayout(new java.awt.BorderLayout());

        lab2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lab2.setPreferredSize(new java.awt.Dimension(80, 18));
        pan22.add(lab2, java.awt.BorderLayout.WEST);

        txt2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txt2.setPreferredSize(new java.awt.Dimension(61, 20));
        txt2.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txt2CaretUpdate(evt);
            }
        });
        pan22.add(txt2, java.awt.BorderLayout.CENTER);

        pan2.add(pan22, java.awt.BorderLayout.WEST);

        sql2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        sql2.setText("'select a.* from SPECPAU a where a.PUNIC = ' + id + ' order by a.anumb'");
        sql2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pan2.add(sql2, java.awt.BorderLayout.CENTER);

        btn2.setText("...");
        btn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn2ActionPerformed(evt);
            }
        });
        pan2.add(btn2, java.awt.BorderLayout.EAST);

        center.add(pan2);

        scr2.setPreferredSize(new java.awt.Dimension(800, 400));

        tab2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tab2.setFillsViewportHeight(true);
        tab2.setName("tab2"); // NOI18N
        tab2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tab2MousePressed(evt);
            }
        });
        scr2.setViewportView(tab2);

        center.add(scr2);

        pan3.setPreferredSize(new java.awt.Dimension(800, 24));
        pan3.setLayout(new java.awt.BorderLayout());

        pan33.setLayout(new java.awt.BorderLayout());

        lab3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lab3.setPreferredSize(new java.awt.Dimension(80, 18));
        pan33.add(lab3, java.awt.BorderLayout.WEST);

        txt3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txt3.setPreferredSize(new java.awt.Dimension(61, 20));
        txt3.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txt3CaretUpdate(evt);
            }
        });
        pan33.add(txt3, java.awt.BorderLayout.CENTER);

        pan3.add(pan33, java.awt.BorderLayout.WEST);

        sql3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        sql3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pan3.add(sql3, java.awt.BorderLayout.CENTER);

        btn3.setText("...");
        pan3.add(btn3, java.awt.BorderLayout.EAST);

        center.add(pan3);

        scr3.setPreferredSize(new java.awt.Dimension(800, 200));

        tab3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tab3.setFillsViewportHeight(true);
        tab3.setName("tab3"); // NOI18N
        tab3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tab3MousePressed(evt);
            }
        });
        scr3.setViewportView(tab3);

        center.add(scr3);

        getContentPane().add(center, java.awt.BorderLayout.CENTER);

        south.setMaximumSize(new java.awt.Dimension(32767, 31));
        south.setPreferredSize(new java.awt.Dimension(688, 20));
        south.setLayout(new javax.swing.BoxLayout(south, javax.swing.BoxLayout.LINE_AXIS));
        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose
        this.dispose();
    }//GEN-LAST:event_btnClose

    private void txt1CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txt1CaretUpdate

        lab1.setText(tab1.getColumnName(tab1.getSelectedColumn()));
        TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) tab1.getRowSorter();

        if (txt1.getText().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            int index = (tab1.getSelectedColumn() == -1 || tab1.getSelectedColumn() == 0) ? 0 : tab1.getSelectedColumn();
            sorter.setRowFilter(RowFilter.regexFilter("^" + txt1.getText(), index));
        }
    }//GEN-LAST:event_txt1CaretUpdate

    private void txt2CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txt2CaretUpdate

        lab2.setText(tab2.getColumnName(tab2.getSelectedColumn()));
        TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) tab2.getRowSorter();

        if (txt2.getText().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            int index = (tab1.getSelectedColumn() == -1 || tab1.getSelectedColumn() == 0) ? 0 : tab1.getSelectedColumn();
            sorter.setRowFilter(RowFilter.regexFilter("^" + txt2.getText(), index));
        }
    }//GEN-LAST:event_txt2CaretUpdate

    private void txt3CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txt3CaretUpdate
        lab3.setText(tab3.getColumnName(tab3.getSelectedColumn()));
        TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) tab3.getRowSorter();

        if (txt3.getText().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("^" + txt3.getText(), tab3.getSelectedColumn()));
        }
    }//GEN-LAST:event_txt3CaretUpdate

    private void btn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn1ActionPerformed
        loadingTab1();
    }//GEN-LAST:event_btn1ActionPerformed

    private void tab1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab1MousePressed

        JTable table = (JTable) evt.getSource();
        if (txt1.getText().length() == 0) {
            lab1.setText(table.getColumnName((table.getSelectedColumn() == -1 || table.getSelectedColumn() == 0) ? 0 : table.getSelectedColumn()));
            txt1.setName(table.getName());
        }
    }//GEN-LAST:event_tab1MousePressed

    private void tab2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab2MousePressed
        JTable table = (JTable) evt.getSource();
        if (txt2.getText().length() == 0) {
            lab2.setText(table.getColumnName((table.getSelectedColumn() == -1 || table.getSelectedColumn() == 0) ? 0 : table.getSelectedColumn()));
            txt2.setName(table.getName());
        }
    }//GEN-LAST:event_tab2MousePressed

    private void tab3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab3MousePressed
        JTable table = (JTable) evt.getSource();
        if (txt3.getText().length() == 0) {
            lab3.setText(table.getColumnName((table.getSelectedColumn() == -1 || table.getSelectedColumn() == 0) ? 0 : table.getSelectedColumn()));
            txt3.setName(table.getName());
        }
    }//GEN-LAST:event_tab3MousePressed

    private void btn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn2ActionPerformed
        selectionTab2();
    }//GEN-LAST:event_btn2ActionPerformed
    // <editor-fold defaultstate="collapsed" desc="Generated Code"> 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn1;
    private javax.swing.JButton btn2;
    private javax.swing.JButton btn3;
    private javax.swing.JButton btnClose;
    private javax.swing.JPanel center;
    private javax.swing.JLabel lab1;
    private javax.swing.JLabel lab2;
    private javax.swing.JLabel lab3;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan1;
    private javax.swing.JPanel pan11;
    private javax.swing.JPanel pan2;
    private javax.swing.JPanel pan22;
    private javax.swing.JPanel pan3;
    private javax.swing.JPanel pan33;
    private javax.swing.JScrollPane scr1;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JScrollPane scr3;
    private javax.swing.JPanel south;
    private javax.swing.JTextField sql1;
    private javax.swing.JTextField sql2;
    private javax.swing.JTextField sql3;
    private javax.swing.JTable tab1;
    private javax.swing.JTable tab2;
    private javax.swing.JTable tab3;
    private javax.swing.JTextField txt1;
    private javax.swing.JTextField txt2;
    private javax.swing.JTextField txt3;
    // End of variables declaration//GEN-END:variables
// </editor-fold> 
    private void initElements() {
        
        ePref.read(this, btnClose, (e) -> {
            ePref.write(this, btnClose);
        }); 
        
        tab1.setAutoCreateRowSorter(true);
        tab2.setAutoCreateRowSorter(true);
        tab3.setAutoCreateRowSorter(true);

        tab1.getSelectionModel().addListSelectionListener(event -> {
            if (event.getValueIsAdjusting() == false) {
                selectionTab2();
            }
        });
        tab2.getSelectionModel().addListSelectionListener(event -> {
            if (event.getValueIsAdjusting() == false) {
                selectionTab3();
            }
        });
    }
}
