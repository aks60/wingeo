package frames.dialog;

import common.listener.ListenerFrame;
import frames.swing.FrameToFile;
import frames.UGui;
import dataset.Field;
import dataset.Query;
import dataset.Record;
import domain.eArtikl;
import domain.eFurndet;
import enums.TypeArt;
import java.util.Arrays;
import frames.swing.DefTableModel;
import java.util.List;
import java.util.stream.Collectors;
import common.listener.ListenerRecord;
import frames.swing.ProgressBar;
import frames.swing.TableFieldFilter;
import java.util.ArrayList;
import startup.App;

//Справочник артикулов
public class DicArtikl extends javax.swing.JDialog {

    private ListenerRecord listener = null;
    private Query qArtikl = new Query(eArtikl.id, eArtikl.level1, eArtikl.level2, eArtikl.code, eArtikl.name);
    private List<Record> list = null;
    private int recordID = -1;

    public DicArtikl(java.awt.Frame parent, ListenerRecord listener, List<Record> list) {
        super(parent, true);
        initComponents();
        initElements();
        qArtikl.addAll(list);
        this.listener = listener;
        this.list = list;
        loadingModel();
        setVisible(true);
    }

    public DicArtikl(java.awt.Frame parent, int recordID, ListenerRecord listener, List<Record> list) {
        super(parent, true);
        initComponents();
        initElements();
        qArtikl.addAll(list);
        this.recordID = recordID;
        this.listener = listener;
        this.list = list;
        loadingModel();
        setVisible(true);
    }

    public DicArtikl(java.awt.Frame parent, ListenerRecord listenet, boolean del, int... level) {
        super(parent, true);
        initComponents();
        initElements();
        String p1 = Arrays.toString(level).split("[\\[\\]]")[1];
        qArtikl.select(eArtikl.up, "where", eArtikl.level1, "in (", p1, ") order by", eArtikl.level1, ",", eArtikl.level2, ",", eArtikl.code, ",", eArtikl.name);
        this.listener = listenet;
        loadingModel();
        btnRemove.setVisible(del);
        setVisible(true);
    }

    public DicArtikl(java.awt.Frame parent, ListenerRecord listenet, boolean del, int furnId, int level1, int level2) {
        super(parent, true);
        initComponents();
        initElements();
        Query qFurndet = new Query(eFurndet.values(), eArtikl.values());
        eFurndet.sql(qFurndet, furnId, level1, level2);
        //eArtikl.sql(qArtikl, eArtikl.id, qFurndet);
        XXXX
        //Query qFurndet2 = new Query(eFurndet.id, eArtikl.id).select(eFurndet.up, "left join", eArtikl.up, "on", eArtikl.id, "=", eFurndet.artikl_id,
                //"where", eFurndet.furniture_id1, "=", furnId, "and", eArtikl.level1, "=", level1, "and", eArtikl.level2, "=", level2);
       
        String arr = (qFurndet.isEmpty() == false) ? qFurndet.table(eArtikl.up).stream().map(rec -> rec.getStr(eArtikl.id)).collect(Collectors.joining(",", "(", ")")) : "(-1)";
        qArtikl.select(eArtikl.up).select(eArtikl.up, "where", eArtikl.id, "in", arr);   
        
        this.listener = listenet;
        loadingModel();
        btnRemove.setVisible(del);
        setVisible(true);
    }

    public void loadingModel() {

        new DefTableModel(tab2, qArtikl, eArtikl.level2, eArtikl.code, eArtikl.name) {
            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (field == eArtikl.level2) {
                    Record record = qArtikl.get(row);
                    if (record.getInt(eArtikl.level1) == 1) {
                        return "Проф. " + TypeArt.find(record.getInt(eArtikl.level1), record.getInt(eArtikl.level2));
                    } else if (record.getInt(eArtikl.level1) == 2) {
                        return "Акс...  " + TypeArt.find(record.getInt(eArtikl.level1), record.getInt(eArtikl.level2));
                    } else if (record.getInt(eArtikl.level1) == 3) {
                        return "Пог...  " + TypeArt.find(record.getInt(eArtikl.level1), record.getInt(eArtikl.level2));
                    } else if (record.getInt(eArtikl.level1) == 4) {
                        return "Инст..." + TypeArt.find(record.getInt(eArtikl.level1), record.getInt(eArtikl.level2));
                    } else if (record.getInt(eArtikl.level1) == 5) {
                        return "Зап... " + TypeArt.find(record.getInt(eArtikl.level1), record.getInt(eArtikl.level2));
                    } else {
                        return "";
                    }
                }
                return val;
            }
        };
        UGui.setSelectedKey(tab2, recordID);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        north = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnChoice = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnArt = new javax.swing.JButton();
        centr = new javax.swing.JPanel();
        scr2 = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JTable();
        south = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Справочник артикулов");

        north.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        north.setMaximumSize(new java.awt.Dimension(32767, 31));
        north.setPreferredSize(new java.awt.Dimension(460, 29));

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

        btnArt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c064.gif"))); // NOI18N
        btnArt.setToolTipText(bundle.getString("Добавить и выбрать")); // NOI18N
        btnArt.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnArt.setFocusable(false);
        btnArt.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnArt.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnArt.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnArt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnArtmn94(evt);
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
                .addComponent(btnArt)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 422, Short.MAX_VALUE)
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
                            .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnArt, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        centr.setPreferredSize(new java.awt.Dimension(460, 500));
        centr.setLayout(new java.awt.BorderLayout());

        scr2.setBorder(null);

        tab2.setFont(frames.UGui.getFont(0,0));
        tab2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Тип артикула", "Код арикула", "Наименование артикула"
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
        tab2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                DicArtikl.this.mouseClicked(evt);
            }
        });
        scr2.setViewportView(tab2);
        if (tab2.getColumnModel().getColumnCount() > 0) {
            tab2.getColumnModel().getColumn(0).setMinWidth(20);
            tab2.getColumnModel().getColumn(0).setPreferredWidth(80);
            tab2.getColumnModel().getColumn(0).setMaxWidth(160);
            tab2.getColumnModel().getColumn(1).setMinWidth(20);
            tab2.getColumnModel().getColumn(1).setPreferredWidth(60);
            tab2.getColumnModel().getColumn(1).setMaxWidth(300);
            tab2.getColumnModel().getColumn(2).setPreferredWidth(300);
            tab2.getColumnModel().getColumn(2).setMaxWidth(800);
        }

        centr.add(scr2, java.awt.BorderLayout.CENTER);

        getContentPane().add(centr, java.awt.BorderLayout.CENTER);

        south.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.setMinimumSize(new java.awt.Dimension(100, 20));
        south.setPreferredSize(new java.awt.Dimension(460, 20));
        south.setLayout(new javax.swing.BoxLayout(south, javax.swing.BoxLayout.LINE_AXIS));
        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose
        this.dispose();
    }//GEN-LAST:event_btnClose

    private void btnChoice(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChoice
        int index = UGui.getIndexRec(tab2);
        if (index != -1) {
            Record record = qArtikl.get(index);
            listener.action(record);
        }
        this.dispose();
    }//GEN-LAST:event_btnChoice

    private void btnRemov(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemov
        listener.action(new Record(Arrays.asList(null, null)));
        this.dispose();
    }//GEN-LAST:event_btnRemov

    private void mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseClicked
        if (evt.getClickCount() == 2) {
            btnChoice(null);
        }
    }//GEN-LAST:event_mouseClicked

    private void btnArtmn94(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnArtmn94
        dispose();
        ProgressBar.create(DicArtikl.this.getOwner(), new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.Artikles.createFrame(DicArtikl.this.getOwner(), listener);
            }
        });
    }//GEN-LAST:event_btnArtmn94

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnArt;
    private javax.swing.JButton btnChoice;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnRemove;
    private javax.swing.JPanel centr;
    private javax.swing.JPanel north;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JPanel south;
    private javax.swing.JTable tab2;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>

    public void initElements() {

        FrameToFile.setFrameSize(this);
        new FrameToFile(this, btnClose);

        TableFieldFilter filterTable = new TableFieldFilter(1, tab2);
        south.add(filterTable, 0);
        filterTable.getTxt().grabFocus();
    }
}
