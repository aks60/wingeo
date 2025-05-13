package frames.dialog;

import frames.UGui;
import enums.Enam;
import dataset.Field;
import dataset.Query;
import dataset.Record;
import domain.eParams;
import builder.param.ParamList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import common.listener.ListenerFrame;
import java.awt.CardLayout;
import java.awt.Frame;
import java.util.List;
import java.util.Vector;
import javax.swing.JToggleButton;
import javax.swing.table.DefaultTableModel;
import frames.swing.comp.DefTableModel;
import common.listener.ListenerRecord;
import domain.eGroups;
import frames.swing.comp.ProgressBar;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JTable;
import startup.App;

public class ParName extends javax.swing.JDialog {

    private Frame parent = null;
    private ListenerRecord listener;
    private Query qGroups = new Query(eGroups.values());
    private Field filter = null;
    private Integer paramID = null;
    private int ID = -1;

    public ParName(Frame parent, ListenerRecord listener, Field filter, int... part) {
        super(parent, true);
        initComponents();
        initElements();
        this.parent = parent;
        this.listener = listener;
        this.filter = filter;
        loadingData();
        loadingModel(part);
        setVisible(true);
    }

    public ParName(Frame parent, int paramID, ListenerRecord listener, Field filter, int... part) {
        super(parent, true);
        initComponents();
        initElements();
        this.parent = parent;
        this.listener = listener;
        this.filter = filter;
        this.paramID = paramID;
        loadingData();
        loadingModel(part);
        setVisible(true);
    }

    public void loadingData() {
        List<Integer> listId = new ArrayList<Integer>();
        Query qParams = new Query(eParams.values()).sql(eParams.data(), filter, 1);
        qParams.forEach(rec -> listId.add(rec.getInt(eParams.groups_id)));
        if (listId.isEmpty() == false) {
            qGroups.sql(eGroups.data(), eGroups.id, listId).sort(eGroups.name);
        }
    }

    public void loadingModel(int... part) {
        DefaultTableModel dm1 = (DefaultTableModel) tab1.getModel();
        DefaultTableModel dm3 = (DefaultTableModel) tab3.getModel();
        dm1.getDataVector().clear();
        dm3.getDataVector().clear();
        List<List> recordList1 = new Vector();
        List<List> recordList3 = new Vector();
        for (Enam el : ParamList.values()) {
            for (int it : part) {
                if (el.pass() == 1 && el.numb() >= it && el.numb() < it + 100) {
                    List record = new Vector();
                    record.add(el.numb());
                    record.add(el.text());
                    recordList1.add(record);

                } else if (el.pass() == 0 && el.numb() >= it && el.numb() < it + 100) {
                    List record = new Vector();
                    record.add(el.numb());
                    record.add(el.text());
                    recordList3.add(record);
                }
            }
        }
        Collections.sort(recordList1, (o1, o2) -> o1.get(1).toString().compareTo(o2.get(1).toString()));
        Collections.sort(recordList3, (o1, o2) -> o1.get(1).toString().compareTo(o2.get(1).toString()));
        for (List record : recordList1) {
            dm1.addRow((Vector) record);
        }
        for (List record : recordList3) {
            dm3.addRow((Vector) record);
        }
        tab2.setModel(new DefTableModel(tab2, qGroups, eGroups.id, eGroups.name));
        ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();

        UGui.setSelectedRow(tab1);
        UGui.setSelectedRow(tab2);
        UGui.setSelectedRow(tab3);

        if (paramID != null) {
            if (paramID > 0) {
                btnCard(new ActionEvent(btnCard1, -1, ""));
                for (int index = 0; index < recordList1.size(); index++) {
                    if (paramID.equals(recordList1.get(index).get(0))) {
                        UGui.setSelectedIndex(tab1, index);
                    }
                }
            } else {
                btnCard(new ActionEvent(btnCard2, -1, ""));
                UGui.setSelectedKey(tab2, paramID);
            }
        }
    }

    public String findHelp(int ID) {

        try (Reader reader = new InputStreamReader(this.getClass()
                .getResourceAsStream("/resource/json/param_desc.json"), "windows-1251")) {

            JsonObject jsonObj = new Gson().fromJson(reader, JsonObject.class);
            JsonArray jsonArr = jsonObj.getAsJsonArray("records");

            for (JsonElement elem : jsonArr) {
                JsonObject jsonObect = new GsonBuilder().create().fromJson(elem, JsonObject.class);
                int id = jsonObect.get("id").getAsInt();
                if (id == ID) {
                    String s1 = jsonObect.get("name").getAsString();
                    String s2 = jsonObect.get("desc").getAsString();
                    return s1 + " - " + s2.substring(0, 1).toLowerCase() + s2.substring(1);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка:ParName.loadingHelp() " + e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        north = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnChoice = new javax.swing.JButton();
        btnCard1 = new javax.swing.JToggleButton();
        btnCard2 = new javax.swing.JToggleButton();
        btnCard3 = new javax.swing.JToggleButton();
        btnParam = new javax.swing.JButton();
        btnCard4 = new javax.swing.JToggleButton();
        centr = new javax.swing.JPanel();
        pan1 = new javax.swing.JPanel();
        scr1 = new javax.swing.JScrollPane();
        tab1 = new javax.swing.JTable();
        pan2 = new javax.swing.JPanel();
        scr2 = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JTable();
        pan3 = new javax.swing.JPanel();
        scr3 = new javax.swing.JScrollPane();
        tab3 = new javax.swing.JTable();
        pan4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        south = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Группы параметров");

        north.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        north.setMaximumSize(new java.awt.Dimension(32767, 31));
        north.setPreferredSize(new java.awt.Dimension(350, 29));

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

        buttonGroup1.add(btnCard1);
        btnCard1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnCard1.setSelected(true);
        btnCard1.setText("1");
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

        buttonGroup1.add(btnCard3);
        btnCard3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnCard3.setText("3");
        btnCard3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnCard3.setPreferredSize(new java.awt.Dimension(30, 25));
        btnCard3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCard(evt);
            }
        });

        btnParam.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c064.gif"))); // NOI18N
        btnParam.setToolTipText(bundle.getString("Добавить и выбрать")); // NOI18N
        btnParam.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnParam.setFocusable(false);
        btnParam.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnParam.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnParam.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnParam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnParametr(evt);
            }
        });

        buttonGroup1.add(btnCard4);
        btnCard4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c035.gif"))); // NOI18N
        btnCard4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnCard4.setPreferredSize(new java.awt.Dimension(30, 25));
        btnCard4.addActionListener(new java.awt.event.ActionListener() {
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
                .addComponent(btnChoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnParam)
                .addGap(34, 34, 34)
                .addComponent(btnCard1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCard2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCard3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCard4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        northLayout.setVerticalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(northLayout.createSequentialGroup()
                        .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnCard1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnCard2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnCard3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnCard4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnChoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnParam, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        centr.setLayout(new java.awt.CardLayout());

        pan1.setName("pan1"); // NOI18N
        pan1.setPreferredSize(new java.awt.Dimension(350, 400));
        pan1.setLayout(new java.awt.BorderLayout());

        scr1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "Параметры системы", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));

        tab1.setFont(frames.UGui.getFont(0,0));
        tab1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Код", "Название"
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
        tab1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });
        scr1.setViewportView(tab1);
        if (tab1.getColumnModel().getColumnCount() > 0) {
            tab1.getColumnModel().getColumn(0).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(0).setMaxWidth(60);
        }

        pan1.add(scr1, java.awt.BorderLayout.CENTER);

        centr.add(pan1, "card1");

        pan2.setName("pan2"); // NOI18N
        pan2.setPreferredSize(new java.awt.Dimension(350, 180));
        pan2.setLayout(new java.awt.BorderLayout());

        scr2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "Параметры пользователя", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));

        tab2.setFont(frames.UGui.getFont(0,0));
        tab2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"2", "3"},
                {"2", "3"}
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
                tabMouseClicked(evt);
            }
        });
        scr2.setViewportView(tab2);
        if (tab2.getColumnModel().getColumnCount() > 0) {
            tab2.getColumnModel().getColumn(0).setMaxWidth(60);
        }

        pan2.add(scr2, java.awt.BorderLayout.CENTER);

        centr.add(pan2, "card2");

        pan3.setName("pan3"); // NOI18N
        pan3.setPreferredSize(new java.awt.Dimension(350, 400));
        pan3.setLayout(new java.awt.BorderLayout());

        scr3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "Перспектива или устарели", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));

        tab3.setFont(frames.UGui.getFont(0,0));
        tab3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"1", "2"},
                {"1", "2"}
            },
            new String [] {
                "Код", "Название"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab3.setFillsViewportHeight(true);
        tab3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scr3.setViewportView(tab3);
        if (tab3.getColumnModel().getColumnCount() > 0) {
            tab3.getColumnModel().getColumn(0).setPreferredWidth(60);
            tab3.getColumnModel().getColumn(0).setMaxWidth(60);
        }

        pan3.add(scr3, java.awt.BorderLayout.CENTER);

        centr.add(pan3, "card3");

        pan4.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        pan4.setMinimumSize(new java.awt.Dimension(22, 34));
        pan4.setName("pan4"); // NOI18N
        pan4.setLayout(new java.awt.BorderLayout());

        jTextArea1.setColumns(20);
        jTextArea1.setFont(frames.UGui.getFont(0,0));
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        pan4.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        centr.add(pan4, "card4");

        getContentPane().add(centr, java.awt.BorderLayout.CENTER);

        south.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.setMinimumSize(new java.awt.Dimension(100, 20));
        south.setPreferredSize(new java.awt.Dimension(350, 20));

        javax.swing.GroupLayout southLayout = new javax.swing.GroupLayout(south);
        south.setLayout(southLayout);
        southLayout.setHorizontalGroup(
            southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 332, Short.MAX_VALUE)
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
            Record record = new Record(2);
            record.add(tab1.getModel().getValueAt(UGui.getIndexRec(tab1), 0));
            record.add(tab1.getModel().getValueAt(UGui.getIndexRec(tab1), 1));
            listener.action(record);

        } else if (btnCard2.isSelected() == true) {
            listener.action(qGroups.get(UGui.getIndexRec(tab2)));
        }
        this.dispose();
    }//GEN-LAST:event_btnChoice

    private void btnCard(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCard
        JToggleButton btn = (JToggleButton) evt.getSource();
        btnParam.setEnabled(false);
        if (btn == btnCard1) {
            btnCard1.setSelected(true);
            btnChoice.setEnabled(true);
            ((CardLayout) centr.getLayout()).show(centr, "card1");

        } else if (btn == btnCard2) {
            btnCard2.setSelected(true);
            btnChoice.setEnabled(true);
            btnParam.setEnabled(true);
            ((CardLayout) centr.getLayout()).show(centr, "card2");

        } else if (btn == btnCard3) {
            btnCard3.setSelected(true);
            btnChoice.setEnabled(false);
            ((CardLayout) centr.getLayout()).show(centr, "card3");

        } else if (btn == btnCard4) {
            btnCard4.setSelected(true);
            btnChoice.setEnabled(false);

            String str = findHelp(ID);
            jTextArea1.append(str);

            ((CardLayout) centr.getLayout()).show(centr, "card4");
        }
    }//GEN-LAST:event_btnCard

    private void tabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabMouseClicked

        if (evt.getClickCount() == 2) {
            btnChoice(null);

        } else {
            JTable tab = (JTable) evt.getSource();
            if (tab == tab1 || tab == tab3) {
                ID = (Integer) tab1.getValueAt(tab.getSelectedRow(), 0);
                ID = 42;
            }
        }
    }//GEN-LAST:event_tabMouseClicked

    private void btnParametr(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnParametr
        dispose();
        ProgressBar.create(ParName.this.getOwner(), new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.Param.createFrame(ParName.this.getOwner(), listener);
            }
        });
    }//GEN-LAST:event_btnParametr

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnCard1;
    private javax.swing.JToggleButton btnCard2;
    private javax.swing.JToggleButton btnCard3;
    private javax.swing.JToggleButton btnCard4;
    private javax.swing.JButton btnChoice;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnParam;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel centr;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan1;
    private javax.swing.JPanel pan2;
    private javax.swing.JPanel pan3;
    private javax.swing.JPanel pan4;
    private javax.swing.JScrollPane scr1;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JScrollPane scr3;
    private javax.swing.JPanel south;
    private javax.swing.JTable tab1;
    private javax.swing.JTable tab2;
    private javax.swing.JTable tab3;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>     
    public void initElements() {

        App.loadLocationWin(this, btnClose, (e) -> {
            App.saveLocationWin(this, btnClose);
        });
        btnParam.setEnabled(false);
    }
}
