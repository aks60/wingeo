package frames.swing;

import dataset.Field;
import dataset.Query;
import frames.UGui;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class TableFieldFilter extends javax.swing.JPanel {

    private JTable table = null;
    private JTable[] tableList = null;
    private int indexColumn = 0;
    private int indexBegin = 0;
    private boolean search = false;
    //Toolkit.getDefaultToolkit().beep();
    
    public TableFieldFilter() {
        initComponents();
        initElements();
    }

    public TableFieldFilter(int indexColName, JTable... tables) {
        initComponents();
        initElements();
        this.tableList = tables;
        this.table = tables[0];
        labFilter.setText(tables[0].getColumnName(indexColName));
        txtFilter.setName(tables[0].getName());
        this.indexBegin = indexColName;
        for (JTable tab : tables) {
            tab.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    try {
                        if (txtFilter.getText().length() == 0) {
                            table = (JTable) evt.getSource();
                            labFilter.setText(table.getColumnName((table.getSelectedColumn() == -1 || table.getSelectedColumn() == 0) ? 0 : table.getSelectedColumn()));
                            txtFilter.setName(table.getName());
                        }
                    } catch (Exception e) {
                        System.err.println("ОШИБКА:swing.FilterTable.mousePressed() " + e);
                    }
                }
            });
        }
    }

    public JLabel getLab() {
        return labFilter;
    }

    public JTextField getTxt() {
        return txtFilter;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn1 = new javax.swing.JButton();
        labFilter = new javax.swing.JLabel();
        txtFilter = new javax.swing.JTextField(){
            public JTable table = null;
        };
        btn2 = new javax.swing.JButton();
        checkFilter = new javax.swing.JCheckBox();

        setMaximumSize(new java.awt.Dimension(520, 19));
        setPreferredSize(new java.awt.Dimension(428, 19));

        btn1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c054.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.eProp.locale); // NOI18N
        btn1.setToolTipText(bundle.getString("Фильтр/Поиск")); // NOI18N
        btn1.setBorder(null);
        btn1.setMaximumSize(new java.awt.Dimension(30, 18));
        btn1.setMinimumSize(new java.awt.Dimension(30, 18));
        btn1.setPreferredSize(new java.awt.Dimension(30, 18));
        btn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn1ActiPerf(evt);
            }
        });

        labFilter.setFont(frames.UGui.getFont(0,0));
        labFilter.setText("Поле не выбрано");
        labFilter.setMaximumSize(new java.awt.Dimension(120, 18));
        labFilter.setMinimumSize(new java.awt.Dimension(120, 18));
        labFilter.setPreferredSize(new java.awt.Dimension(120, 18));

        txtFilter.setFont(frames.UGui.getFont(0,0));
        txtFilter.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txtFilter.setMaximumSize(new java.awt.Dimension(120, 17));
        txtFilter.setMinimumSize(new java.awt.Dimension(120, 17));
        txtFilter.setName(""); // NOI18N
        txtFilter.setPreferredSize(new java.awt.Dimension(120, 17));
        txtFilter.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtCaretUpdate(evt);
            }
        });

        btn2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b041.gif"))); // NOI18N
        btn2.setToolTipText(bundle.getString("Вставить из буфера обмена / Удалить")); // NOI18N
        btn2.setBorder(null);
        btn2.setMaximumSize(new java.awt.Dimension(30, 18));
        btn2.setMinimumSize(new java.awt.Dimension(30, 18));
        btn2.setPreferredSize(new java.awt.Dimension(30, 18));
        btn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn2ActiPerf(evt);
            }
        });

        checkFilter.setFont(frames.UGui.getFont(0,0));
        checkFilter.setText("в конце строки");
        checkFilter.setMaximumSize(new java.awt.Dimension(120, 18));
        checkFilter.setMinimumSize(new java.awt.Dimension(103, 18));
        checkFilter.setPreferredSize(new java.awt.Dimension(120, 18));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btn1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(labFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(btn2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(checkFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btn1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(labFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btn2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(checkFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtCaretUpdate
        try {
            if (table != null) {
                if (txtFilter.getText().length() == 0) {
                    btn2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b041.gif")));
                    ((TableRowSorter<TableModel>) table.getRowSorter()).setRowFilter(null);

                } else if (search == true) {
                    btn2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b064.gif")));
                    indexColumn = (table.getSelectedColumn() == -1) ? indexBegin : table.getSelectedColumn();
                    if (table.getModel() instanceof DefTableModel) {
                        Query query = ((DefTableModel) table.getModel()).getQuery();
                        Field field = ((DefTableModel) table.getModel()).columns[indexColumn];
                        for (int index = 0; index < query.size(); ++index) {

                            if (query.table(field).get(index).getStr(field).startsWith(txtFilter.getText())) {
                                UGui.setSelectedIndex(table, index);
                                UGui.scrollRectToIndex(index, table);
                                return;
                            }
                        }
                    } else {
                        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
                        for (int index = 0; index < dtm.getDataVector().size(); ++index) {
                            Vector vector = (Vector) dtm.getDataVector().get(index);
                            if (String.valueOf(vector.get(indexColumn)).startsWith(txtFilter.getText())) {
                                UGui.setSelectedIndex(table, index);
                                UGui.scrollRectToIndex(index, table);
                                return;
                            }
                        }
                    }
                } else if (search == false) {
                    btn2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b064.gif")));
                    indexColumn = (table.getSelectedColumn() == -1) ? indexBegin : table.getSelectedColumn();
                    String text = (checkFilter.isSelected()) ? txtFilter.getText() + "$" : "^" + txtFilter.getText();
                    ((TableRowSorter<TableModel>) table.getRowSorter()).setRowFilter(RowFilter.regexFilter(text, indexColumn));
                    UGui.setSelectedRow(table);
                }
            }
        } catch (Exception e) {
            System.err.println("ОШИБКА:swing.FilterTable.txtCaretUpdate() " + e);
        }
    }//GEN-LAST:event_txtCaretUpdate

    private void btn1ActiPerf(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn1ActiPerf
        try {
            if (search == true) {
                btn1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c054.gif")));
                search = !search;
                txtCaretUpdate(null);
            } else {
                btn1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c043.gif")));
                search = !search;
                String txt = txtFilter.getText();
                txtFilter.setText("");
                txtCaretUpdate(null);
                txtFilter.setText(txt);
            }
            UGui.scrollRectToRow(table.getSelectedRow() - 1, table);
        } catch (Exception e) {
            System.err.println("ERROR");
        }
    }//GEN-LAST:event_btn1ActiPerf

    private void btn2ActiPerf(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn2ActiPerf
        try {
            if (txtFilter.getText().isEmpty()) {
                btn2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b064.gif")));
                try {
                    Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                    Transferable t = cb.getContents(null);
                    if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        txtFilter.setText(t.getTransferData(DataFlavor.stringFlavor).toString());
                    }
                } catch (UnsupportedFlavorException | IOException ex) {
                    System.err.println("frames.swing.TableFilter.btn2ActiPerf()");
                }
            } else {
                btn2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b041.gif")));
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(txtFilter.getText()), null);
                txtFilter.setText("");
            }
        } catch (Exception e) {
            System.err.println("ERROR");
        }
    }//GEN-LAST:event_btn2ActiPerf

// <editor-fold defaultstate="collapsed" desc="Generated Code"> 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn1;
    private javax.swing.JButton btn2;
    private javax.swing.JCheckBox checkFilter;
    private javax.swing.JLabel labFilter;
    private javax.swing.JTextField txtFilter;
    // End of variables declaration//GEN-END:variables
// </editor-fold>

    public final void initElements() {
//        PlainDocument doc = (PlainDocument) txtFilter.getDocument();
//        doc.setDocumentFilter(new DocumentFilter() {
//
//            @Override
//            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
//                    super.insertString(fb, offset, firstUpperCase(offset, string), attr);
//            }
//
//            @Override
//            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
//                    super.replace(fb, offset, length, firstUpperCase(offset, string), attrs);
//            }
//        });
    }
    
//    public String firstUpperCase(int offset, String word) {
//        if (offset != 0 || word == null || word.isEmpty()) {
//            return word;
//        }
//        return word.substring(0, 1).toUpperCase() + word.substring(1);
//    }    
}
