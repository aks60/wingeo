package frames.dialog;

import builder.model.UPar;
import builder.param.KitDet;
import builder.param.ParamList;
import common.UCom;
import common.listener.ListenerObject;
import dataset.Conn;
import dataset.Field;
import frames.swing.FrameToFile;
import frames.UGui;
import dataset.Query;
import dataset.Record;
import domain.eColor;
import java.awt.Frame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import frames.swing.DefTableModel;
import java.util.HashSet;
import java.util.stream.Stream;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import domain.eArtikl;
import domain.eGroups;
import domain.eKitdet;
import domain.eKitpar2;
import domain.eKits;
import domain.ePrjkit;
import enums.Enam;
import enums.TypeGrup;
import enums.UseColor;
import enums.UseUnit;
import java.util.HashMap;
import java.util.List;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

//Дополнительные комплекты
public class DicKits extends javax.swing.JDialog {

    private ListenerObject<Query> listener = null;
    private Query qGroups = new Query(eGroups.values());
    private Query qCateg = new Query(eGroups.values());
    private Query qKits = new Query(eKits.values());
    private Query qKitdet = new Query(eKitdet.values());
    private Query qKitpar2 = new Query(eKitpar2.values());
    private int colorID[] = {-1, -1, -1};
    private int projectID = -1;
    private int prjprodID = -1;

    public DicKits(Frame parent, ListenerObject<Query> listener, int projectID, int prjprodID) {
        super(parent, true);
        initComponents();
        initElements();
        this.listener = listener;
        this.projectID = projectID;
        this.prjprodID = prjprodID;
        loadingData("0");
        loadingModel();
        setVisible(true);
    }

    public void loadingData(String type) {
        eArtikl.data();
        qKits.sql(eKits.data(), eKits.up).sort(eKits.groups_id, eKits.name);
    }

    private void loadingModel() {
        qGroups.sql(eGroups.data(), eGroups.grup, TypeGrup.COLOR_MAP.id, TypeGrup.PARAM_USER.id);
        qCateg.sql(eGroups.data(), eGroups.grup, 10);
        new DefTableModel(tab1, qKits, eKits.groups_id, eKits.name) {

            public Object getValueAt(int col, int row, Object val) {
                if (val != null && col == 0) {
                    return qCateg.find(val, eGroups.id).getStr(eGroups.name);
                }
                return val;
            }
        };
        new DefTableModel(tab2, qKitdet, eKitdet.artikl_id, eKitdet.artikl_id,
                eKitdet.color1_id, eKitdet.color2_id, eKitdet.color3_id, eKitdet.id, eKitdet.flag) {

            public Object getValueAt(int col, int row, Object val) {

                if (val != null && val.equals(0) && List.of(eKitdet.color1_id, eKitdet.color2_id, eKitdet.color3_id).contains(columns[col])) {
                    return UseColor.automatic[1];

                } else if (val != null && col == 0) {
                    return eArtikl.get((int) val).getStr(eArtikl.code);

                } else if (val != null && col == 1) {
                    return eArtikl.get((int) val).getStr(eArtikl.name);

                } else if (val != null && columns[col] == eKitdet.color1_id) {
                    return eColor.get((int) val).getStr(eColor.name);

                } else if (val != null && columns[col] == eKitdet.color2_id) {
                    return eColor.get((int) val).getStr(eColor.name);

                } else if (val != null && columns[col] == eKitdet.color3_id) {
                    return eColor.get((int) val).getStr(eColor.name);

                } else if (val != null && col == 5) { //columns[col] == eArtikl.unit) {
                    int id = qKitdet.getAs(row, eKitdet.artikl_id);
                    Record record = eArtikl.get(id);
                    return UseUnit.getName(record.getInt(eArtikl.unit));
                }
                return val;
            }
        };
        new DefTableModel(tab3, qKitpar2, eKitpar2.groups_id, eKitpar2.text) {

            public Object getValueAt(int col, int row, Object val) {
                if (val != null) {
                    Field field = columns[col];
                    if (field == eKitpar2.groups_id) {

                        if (Integer.valueOf(String.valueOf(val)) < 0) {
                            return qGroups.find(val, eGroups.id).getDev(eGroups.name, val);
                        } else {
                            Enam en = ParamList.find(val);
                            return Record.getDev(en.numb(), en.text());
                        }
                    }
                }
                return val;
            }
        };
        UGui.setSelectedRow(tab1);
    }

    private void selectionTab1() {
        UGui.clearTable(tab2);
        List.of(txt9, txt13, txt14).forEach(txt -> txt.setText(null));
        int index = UGui.getIndexRec(tab1);
        if (index != -1) {
            Record kitsRec = qKits.get(index);
            Integer kitsId = kitsRec.getInt(eKits.id);
            qKitdet.sql(eKitdet.data(), eKitdet.kits_id, kitsId).sort(eKitdet.artikl_id);
            List.of(txt1, txt2, txt3, txt9, txt13, txt14).forEach(act -> act.setEditable(false));
            List.of(txt1, txt2, txt3, txt9, txt13, txt14).forEach(act -> act.setBackground(new java.awt.Color(212, 208, 200)));

            for (Record kitdetRec : qKitdet) {
                List<Record> kitparList = eKitpar2.filter(kitdetRec.getInt(eKitdet.id));
                for (Record kitparRec : kitparList) {
                    String text = kitparRec.getStr(eKitpar2.text);
                    if (text.contains("Q")) {
                        txt3.setEditable(true);
                        txt3.setBackground(new java.awt.Color(255, 255, 255));
                    }
                    if (text.contains("L") || text.contains("8050")) {
                        txt2.setEditable(true);
                        txt2.setBackground(new java.awt.Color(255, 255, 255));
                    }
                    if (text.contains("H")) {
                        txt1.setEditable(true);
                        txt1.setBackground(new java.awt.Color(255, 255, 255));
                    }
                }
            }
        }
        ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
        if (tab2.getRowCount() > 0) {
            tab2.setRowSelectionInterval(0, tab2.getRowCount() - 1);
        }
    }

    private void selectionTab2() {
        qKitpar2.clear();
        int indexArr[] = tab2.getSelectedRows();
        for (int i = 0; i < indexArr.length; i++) {
            Record kitdetRec = qKitdet.get(tab2.convertRowIndexToModel(indexArr[i]));
            List<Record> kitpar2List = eKitpar2.filter(kitdetRec.getInt(eKitdet.id));
            qKitpar2.addAll(kitpar2List);
        }
        ((DefaultTableModel) tab3.getModel()).fireTableDataChanged();
    }

    public void addSpecific() {

        double H = UCom.getDbl(txt1.getText(), 0.0);
        double L = UCom.getDbl(txt2.getText(), 0.0);
        double Q = UCom.getDbl(txt3.getText(), 1.0);
        HashMap<Integer, String> mapParam = new HashMap<Integer, String>();
        KitDet kitDet = new KitDet(Q, L, H);

        //Цикл по списку детализации
        for (Record kitdetRec : qKitdet) {
            mapParam.clear();

            //ФИЛЬТР детализации, параметры накапливаются в mapParam
            if (kitDet.filter(mapParam, kitdetRec) == true) {

                Record prjkitRec = ePrjkit.up.newRecord(Query.INS);
                prjkitRec.set(ePrjkit.id, Conn.genId(ePrjkit.up));
                prjkitRec.set(ePrjkit.project_id, projectID);
                prjkitRec.set(ePrjkit.prjprod_id, prjprodID);
                prjkitRec.set(ePrjkit.numb, 0);
                prjkitRec.set(ePrjkit.artikl_id, kitdetRec.getInt(eKitdet.artikl_id));

                double count = UPar.to_7030_7031_8060_8061_9060_9061(mapParam);
                count = (count == 0) ? Q : count;
                prjkitRec.set(ePrjkit.numb, count); //количество    

                //Длина, мм
                Double width = UPar.to_8065_8066_9065_9066(mapParam);
                prjkitRec.set(ePrjkit.width, width); //длина мм   

                //Ширина, мм
                Double height = UPar.to_8070_8071_9070_9071(mapParam);
                if (height == null) {
                    Record artkitRec = eArtikl.get(kitdetRec.getInt(eKitdet.artikl_id));
                    height = artkitRec.getDbl(eArtikl.height);
                }
                prjkitRec.set(ePrjkit.height, height); //ширина  

                //Поправка, мм
                double correct = UPar.to_8050(mapParam);
                prjkitRec.set(ePrjkit.width, width + correct); //длина мм 

                //Угол реза 1
                Double angl1 = UPar.to_8075(mapParam, 0);
                angl1 = (angl1 == null) ? 90 : angl1;
                prjkitRec.set(ePrjkit.angl1, angl1); //угол 1  

                //Угол реза 2
                Double angl2 = UPar.to_8075(mapParam, 1);
                angl2 = (angl2 == null) ? 90 : angl2;
                prjkitRec.set(ePrjkit.angl2, angl2); //угол 2

                //Текстура по умолчанию из детализации
                prjkitRec.set(ePrjkit.color1_id, kitdetRec.get(eKitdet.color1_id));
                prjkitRec.set(ePrjkit.color2_id, kitdetRec.get(eKitdet.color2_id));
                prjkitRec.set(ePrjkit.color3_id, kitdetRec.get(eKitdet.color3_id));

                int artiklID = kitdetRec.getInt(eKitdet.artikl_id);

                //Автоподбор
                if (colorID[0] != -1) {
                    HashSet<Record> colorSet = UGui.artiklToColorSet(artiklID, 1); //все текстуры артикула
                    for (Record colorRec : colorSet) {
                        if (colorID[0] == colorRec.getInt(eColor.id)) {
                            prjkitRec.set(ePrjkit.color1_id, colorID[0]); //color1
                        }
                    }
                }

                //Автоподбор
                if (colorID[1] != -1) {
                    HashSet<Record> colorSet = UGui.artiklToColorSet(artiklID, 2); //все текстуры артикула
                    for (Record colorRec : colorSet) {
                        if (colorID[1] == colorRec.getInt(eColor.id)) {
                            prjkitRec.set(ePrjkit.color2_id, colorID[1]); //color2
                        }
                    }
                }
                //Автоподбор
                if (colorID[2] != -1) {
                    HashSet<Record> colorSet = UGui.artiklToColorSet(artiklID, 3); //все текстуры артикула
                    for (Record colorRec : colorSet) {
                        if (colorID[2] == colorRec.getInt(eColor.id)) {
                            prjkitRec.set(ePrjkit.color3_id, colorID[2]); //color3
                        }
                    }
                }
                ePrjkit.data().add(prjkitRec);
                ePrjkit.data().execsql();
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        north = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnChoice = new javax.swing.JButton();
        centr = new javax.swing.JPanel();
        pan2 = new javax.swing.JPanel();
        lab30 = new javax.swing.JLabel();
        lab13 = new javax.swing.JLabel();
        txt1 = new javax.swing.JTextField();
        lab14 = new javax.swing.JLabel();
        txt2 = new javax.swing.JTextField();
        txt3 = new javax.swing.JTextField();
        lab27 = new javax.swing.JLabel();
        lab31 = new javax.swing.JLabel();
        lab32 = new javax.swing.JLabel();
        txt9 = new javax.swing.JTextField();
        txt13 = new javax.swing.JTextField();
        txt14 = new javax.swing.JTextField();
        btn9 = new javax.swing.JButton();
        btn13 = new javax.swing.JButton();
        btn14 = new javax.swing.JButton();
        pan1 = new javax.swing.JPanel();
        scr1 = new javax.swing.JScrollPane();
        tab1 = new javax.swing.JTable();
        scr2 = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JTable();
        scr3 = new javax.swing.JScrollPane();
        tab3 = new javax.swing.JTable();
        south = new javax.swing.JPanel();
        labFilter = new javax.swing.JLabel();
        txtFilter = new javax.swing.JTextField(){
            public JTable table = null;
        };
        checkFilter = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Справочник комплектов");
        setPreferredSize(new java.awt.Dimension(612, 650));

        north.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        north.setMaximumSize(new java.awt.Dimension(32767, 31));
        north.setPreferredSize(new java.awt.Dimension(600, 29));

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 587, Short.MAX_VALUE)
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

        centr.setPreferredSize(new java.awt.Dimension(612, 600));
        centr.setLayout(new java.awt.BorderLayout());

        pan2.setPreferredSize(new java.awt.Dimension(513, 88));

        lab30.setFont(frames.UGui.getFont(0,0));
        lab30.setText("Кол. комп.  ( Q )");
        lab30.setToolTipText("");
        lab30.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab30.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab30.setMinimumSize(new java.awt.Dimension(34, 14));
        lab30.setPreferredSize(new java.awt.Dimension(92, 18));

        lab13.setFont(frames.UGui.getFont(0,0));
        lab13.setText("Длина  ( L )");
        lab13.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab13.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab13.setMinimumSize(new java.awt.Dimension(34, 14));
        lab13.setPreferredSize(new java.awt.Dimension(92, 18));

        txt1.setFont(frames.UGui.getFont(0,0));
        txt1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt1.setPreferredSize(new java.awt.Dimension(60, 18));

        lab14.setFont(frames.UGui.getFont(0,0));
        lab14.setText("Ширина  ( H )");
        lab14.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab14.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab14.setMinimumSize(new java.awt.Dimension(34, 14));
        lab14.setPreferredSize(new java.awt.Dimension(92, 18));

        txt2.setFont(frames.UGui.getFont(0,0));
        txt2.setToolTipText("");
        txt2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt2.setPreferredSize(new java.awt.Dimension(60, 18));

        txt3.setFont(frames.UGui.getFont(0,0));
        txt3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt3.setPreferredSize(new java.awt.Dimension(60, 18));

        lab27.setFont(frames.UGui.getFont(0,0));
        lab27.setText("Основная текстура");
        lab27.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab27.setMaximumSize(new java.awt.Dimension(120, 18));
        lab27.setMinimumSize(new java.awt.Dimension(120, 18));
        lab27.setPreferredSize(new java.awt.Dimension(120, 18));

        lab31.setFont(frames.UGui.getFont(0,0));
        lab31.setText("Внутренняя текстура");
        lab31.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab31.setMaximumSize(new java.awt.Dimension(120, 18));
        lab31.setMinimumSize(new java.awt.Dimension(120, 18));
        lab31.setPreferredSize(new java.awt.Dimension(120, 18));

        lab32.setFont(frames.UGui.getFont(0,0));
        lab32.setText("Внешняя текстура");
        lab32.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab32.setMaximumSize(new java.awt.Dimension(120, 18));
        lab32.setMinimumSize(new java.awt.Dimension(120, 18));
        lab32.setPreferredSize(new java.awt.Dimension(120, 18));

        txt9.setEditable(false);
        txt9.setFont(frames.UGui.getFont(0,0));
        txt9.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt9.setPreferredSize(new java.awt.Dimension(180, 18));

        txt13.setEditable(false);
        txt13.setFont(frames.UGui.getFont(0,0));
        txt13.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt13.setPreferredSize(new java.awt.Dimension(180, 18));

        txt14.setEditable(false);
        txt14.setFont(frames.UGui.getFont(0,0));
        txt14.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt14.setPreferredSize(new java.awt.Dimension(180, 18));

        btn9.setText("...");
        btn9.setToolTipText(bundle.getString("Закрыть")); // NOI18N
        btn9.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn9.setMaximumSize(new java.awt.Dimension(21, 20));
        btn9.setMinimumSize(new java.awt.Dimension(21, 20));
        btn9.setName("btn9"); // NOI18N
        btn9.setPreferredSize(new java.awt.Dimension(21, 18));
        btn9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToWindows(evt);
            }
        });

        btn13.setText("...");
        btn13.setToolTipText(bundle.getString("Закрыть")); // NOI18N
        btn13.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn13.setMaximumSize(new java.awt.Dimension(21, 20));
        btn13.setMinimumSize(new java.awt.Dimension(21, 20));
        btn13.setName("btn13"); // NOI18N
        btn13.setPreferredSize(new java.awt.Dimension(21, 18));
        btn13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToWindows(evt);
            }
        });

        btn14.setText("...");
        btn14.setToolTipText(bundle.getString("Закрыть")); // NOI18N
        btn14.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn14.setMaximumSize(new java.awt.Dimension(21, 20));
        btn14.setMinimumSize(new java.awt.Dimension(21, 20));
        btn14.setName("btn14"); // NOI18N
        btn14.setPreferredSize(new java.awt.Dimension(21, 18));
        btn14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToWindows(evt);
            }
        });

        javax.swing.GroupLayout pan2Layout = new javax.swing.GroupLayout(pan2);
        pan2.setLayout(pan2Layout);
        pan2Layout.setHorizontalGroup(
            pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pan2Layout.createSequentialGroup()
                        .addComponent(lab30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan2Layout.createSequentialGroup()
                        .addComponent(lab14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan2Layout.createSequentialGroup()
                        .addComponent(lab13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(txt2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan2Layout.createSequentialGroup()
                        .addComponent(lab32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt14, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE))
                    .addGroup(pan2Layout.createSequentialGroup()
                        .addComponent(lab27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt9, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE))
                    .addGroup(pan2Layout.createSequentialGroup()
                        .addComponent(lab31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt13, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        pan2Layout.setVerticalGroup(
            pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan2Layout.createSequentialGroup()
                        .addGroup(pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lab27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lab31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lab32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pan2Layout.createSequentialGroup()
                        .addGroup(pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lab30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lab13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lab14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        centr.add(pan2, java.awt.BorderLayout.NORTH);

        pan1.setPreferredSize(new java.awt.Dimension(454, 512));
        pan1.setLayout(new javax.swing.BoxLayout(pan1, javax.swing.BoxLayout.PAGE_AXIS));

        scr1.setPreferredSize(new java.awt.Dimension(412, 250));

        tab1.setFont(frames.UGui.getFont(0,0));
        tab1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Категория", "Название комплекта", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab1.setFillsViewportHeight(true);
        tab1.setName("tab1"); // NOI18N
        tab1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tab1MouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tabMousePressed(evt);
            }
        });
        scr1.setViewportView(tab1);
        if (tab1.getColumnModel().getColumnCount() > 0) {
            tab1.getColumnModel().getColumn(0).setPreferredWidth(80);
            tab1.getColumnModel().getColumn(1).setPreferredWidth(300);
            tab1.getColumnModel().getColumn(2).setPreferredWidth(40);
            tab1.getColumnModel().getColumn(2).setMaxWidth(60);
        }

        pan1.add(scr1);

        scr2.setMaximumSize(new java.awt.Dimension(32767, 120));
        scr2.setPreferredSize(new java.awt.Dimension(454, 150));

        tab2.setFont(frames.UGui.getFont(0,0));
        tab2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Артикул", "Название", "Основная текстура", "Внутренняя текстура", "Внешняя текстура", "Ед.измерения", "Основной элемент", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
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
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tabMousePressed(evt);
            }
        });
        scr2.setViewportView(tab2);
        if (tab2.getColumnModel().getColumnCount() > 0) {
            tab2.getColumnModel().getColumn(0).setPreferredWidth(80);
            tab2.getColumnModel().getColumn(1).setPreferredWidth(400);
            tab2.getColumnModel().getColumn(2).setPreferredWidth(160);
            tab2.getColumnModel().getColumn(3).setPreferredWidth(160);
            tab2.getColumnModel().getColumn(4).setPreferredWidth(160);
            tab2.getColumnModel().getColumn(6).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(7).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(7).setMaxWidth(60);
        }

        pan1.add(scr2);

        scr3.setMaximumSize(new java.awt.Dimension(32767, 80));
        scr3.setPreferredSize(new java.awt.Dimension(454, 120));

        tab3.setFont(frames.UGui.getFont(0,0));
        tab3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Параметр", "Значение"
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
        scr3.setViewportView(tab3);
        if (tab3.getColumnModel().getColumnCount() > 0) {
            tab3.getColumnModel().getColumn(0).setPreferredWidth(120);
            tab3.getColumnModel().getColumn(1).setPreferredWidth(20);
        }

        pan1.add(scr3);

        centr.add(pan1, java.awt.BorderLayout.CENTER);

        getContentPane().add(centr, java.awt.BorderLayout.CENTER);

        south.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.setMinimumSize(new java.awt.Dimension(100, 20));
        south.setPreferredSize(new java.awt.Dimension(600, 20));
        south.setLayout(new javax.swing.BoxLayout(south, javax.swing.BoxLayout.LINE_AXIS));

        labFilter.setFont(frames.UGui.getFont(0,0));
        labFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c054.gif"))); // NOI18N
        labFilter.setText("Поле");
        labFilter.setMaximumSize(new java.awt.Dimension(100, 14));
        labFilter.setMinimumSize(new java.awt.Dimension(100, 14));
        labFilter.setPreferredSize(new java.awt.Dimension(100, 14));
        south.add(labFilter);

        txtFilter.setFont(frames.UGui.getFont(0,0));
        txtFilter.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txtFilter.setMaximumSize(new java.awt.Dimension(180, 20));
        txtFilter.setMinimumSize(new java.awt.Dimension(180, 20));
        txtFilter.setName(""); // NOI18N
        txtFilter.setPreferredSize(new java.awt.Dimension(180, 20));
        txtFilter.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                filterCaretUpdate(evt);
            }
        });
        south.add(txtFilter);

        checkFilter.setFont(frames.UGui.getFont(0,0));
        checkFilter.setText("в конце строки");
        south.add(checkFilter);

        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose
        this.dispose();
    }//GEN-LAST:event_btnClose

    private void btnChoice(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChoice
        if (tab2.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "РќРё РѕРґРЅР° РёР· Р·Р°РїРёСЃРµР№ РЅРµ РІС‹РґРµР»РµРЅР°", "РџСЂРµРґСѓРїСЂРµР¶РґРµРЅРёРµ", JOptionPane.NO_OPTION);
            return;
        }
        if (txt3.getText().isEmpty() && txt3.isEditable()) {
            JOptionPane.showMessageDialog(this, "РЈРєР°Р¶РёС‚Рµ РєРѕР»РёС‡РµСЃС‚РІРѕ РєРѕРјРїР»РµРєС‚РѕРІ.", "РџСЂРµРґСѓРїСЂРµР¶РґРµРЅРёРµ", JOptionPane.INFORMATION_MESSAGE);
            return;
        } else if (txt2.getText().isEmpty() && txt2.isEditable()) {
            JOptionPane.showMessageDialog(this, "РЈРєР°Р¶РёС‚Рµ РґР»РёРЅСѓ РєРѕРјРїР»РµРєС‚Р°.", "РџСЂРµРґСѓРїСЂРµР¶РґРµРЅРёРµ", JOptionPane.INFORMATION_MESSAGE);
            return;
        } else if (txt1.getText().isEmpty() && txt1.isEditable()) {
            JOptionPane.showMessageDialog(this, "РЈРєР°Р¶РёС‚Рµ С€РёСЂРёРЅСѓ РєРѕРјРїР»РµРєС‚Р°.", "РџСЂРµРґСѓРїСЂРµР¶РґРµРЅРёРµ", JOptionPane.INFORMATION_MESSAGE);
            return;
        } else if ((txt9.getText().isEmpty() && txt9.isEditable())
                || (txt13.getText().isEmpty() && txt13.isEditable())
                || (txt14.getText().isEmpty() && txt14.isEditable())) {
            JOptionPane.showMessageDialog(this, "РЈРєР°Р¶РёС‚Рµ С‚РµРєСЃС‚СѓСЂСѓ РєРѕРјРїР»РµРєС‚Р°.", "РџСЂРµРґСѓРїСЂРµР¶РґРµРЅРёРµ", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        addSpecific();

        listener.action(null);
        this.dispose();
    }//GEN-LAST:event_btnChoice

    private void tab1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab1MouseClicked
        if (evt.getClickCount() == 2 && tab2.getRowCount() == 0) {
            btnChoice(null);
        }
    }//GEN-LAST:event_tab1MouseClicked

    private void filterCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_filterCaretUpdate
        JTable table = Stream.of(tab1, tab2).filter(tab -> tab.getName().equals(txtFilter.getName())).findFirst().orElse(tab2);
        if (txtFilter.getText().length() == 0) {
            ((DefTableModel) table.getModel()).getSorter().setRowFilter(null);
        } else {
            int index = (table.getSelectedColumn() == -1 || table.getSelectedColumn() == 0) ? 0 : table.getSelectedColumn();
            String text = (checkFilter.isSelected()) ? txtFilter.getText() + "$" : "^" + txtFilter.getText();
            ((DefTableModel) table.getModel()).getSorter().setRowFilter(RowFilter.regexFilter(text, index));
        }
    }//GEN-LAST:event_filterCaretUpdate

    private void tabMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabMousePressed
        JTable table = (JTable) evt.getSource();
        UGui.updateBorderAndSql(table, List.of(tab1, tab2));
        if (txtFilter.getText().length() == 0) {
            labFilter.setText(table.getColumnName((table.getSelectedColumn() == -1 || table.getSelectedColumn() == 0) ? 0 : table.getSelectedColumn()));
            txtFilter.setName(table.getName());
        }
//        if (evt.getClickCount() == 2) {
//            btnChoice(null);
//        }
    }//GEN-LAST:event_tabMousePressed

    private void colorToWindows(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorToWindows
        try {
            JTextField txt = (evt.getSource() == btn9) ? txt9 : (evt.getSource() == btn13) ? txt13 : txt14;
            int indexBtn = (evt.getSource() == btn9) ? 0 : (evt.getSource() == btn13) ? 1 : 2;
            Record kitdetRec = qKitdet.stream().filter(rec -> 1 == rec.getInt(eKitdet.flag)).findFirst().orElse(null);
//            if (qKitdet.size() == 1) {
//                kitdetRec = qKitdet.get(0);
//            }
            if (kitdetRec != null) {
                int id = kitdetRec.getInt(eKitdet.artikl_id);
                HashSet<Record> colorSet = new HashSet<Record>();
                colorSet = UGui.artiklToColorSet(id, indexBtn + 1);
                DicColor frame = new DicColor(null, (colorRc) -> {
                    setColor(indexBtn, colorRc);
                }, colorSet, true, false);
            } else {
                DicColor frame = new DicColor(null, (colorRec) -> {
                    setColor(indexBtn, colorRec);
                }, false, false);
            }
        } catch (Exception e) {
            System.err.println("РћС€РёР±РєР°: " + e);
        }
    }//GEN-LAST:event_colorToWindows

    private void setColor(int indexBtn, Record colorRec) {
        if (indexBtn == 0) {
            txt9.setText(colorRec.getStr(eColor.name));
            colorID[0] = colorRec.getInt(eColor.id);

        } else if (indexBtn == 1) {
            txt13.setText(colorRec.getStr(eColor.name));
            colorID[1] = colorRec.getInt(eColor.id);

        } else if (indexBtn == 2) {
            txt14.setText(colorRec.getStr(eColor.name));
            colorID[2] = colorRec.getInt(eColor.id);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code"> 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn13;
    private javax.swing.JButton btn14;
    private javax.swing.JButton btn9;
    private javax.swing.JButton btnChoice;
    private javax.swing.JButton btnClose;
    private javax.swing.JPanel centr;
    private javax.swing.JCheckBox checkFilter;
    private javax.swing.JLabel lab13;
    private javax.swing.JLabel lab14;
    private javax.swing.JLabel lab27;
    private javax.swing.JLabel lab30;
    private javax.swing.JLabel lab31;
    private javax.swing.JLabel lab32;
    private javax.swing.JLabel labFilter;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan1;
    private javax.swing.JPanel pan2;
    private javax.swing.JScrollPane scr1;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JScrollPane scr3;
    private javax.swing.JPanel south;
    private javax.swing.JTable tab1;
    private javax.swing.JTable tab2;
    private javax.swing.JTable tab3;
    private javax.swing.JTextField txt1;
    private javax.swing.JTextField txt13;
    private javax.swing.JTextField txt14;
    private javax.swing.JTextField txt2;
    private javax.swing.JTextField txt3;
    private javax.swing.JTextField txt9;
    private javax.swing.JTextField txtFilter;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>
    private void initElements() {

        FrameToFile.setFrameSize(this);
        new FrameToFile(this, btnClose);
        tab1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    selectionTab1();
                }
            }
        });
        tab2.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    selectionTab2();
                }
            }
        });

        JTextField editorText1 = (JTextField) txt3;
        PlainDocument doc1 = (PlainDocument) editorText1.getDocument();
        doc1.setDocumentFilter(new DocumentFilter() {

            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if ("0123456789.,".indexOf(string) != -1) { //РїСЂРѕРІРµСЂРєР° РЅР° РєРѕСЂСЂРµРєРЅРѕСЃС‚СЊ РІРІРѕРґР°
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
                if ("0123456789.,".indexOf(string) != -1) {  //РїСЂРѕРІРµСЂРєР° РЅР° РєРѕСЂСЂРµРєРЅРѕСЃС‚СЊ РІРІРѕРґР°
                    super.replace(fb, offset, length, string, attrs);
                }
            }
        });

        JTextField editorText2 = (JTextField) txt2;
        PlainDocument doc2 = (PlainDocument) editorText2.getDocument();
        doc2.setDocumentFilter(new DocumentFilter() {

            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if ("0123456789.,".indexOf(string) != -1) { //РїСЂРѕРІРµСЂРєР° РЅР° РєРѕСЂСЂРµРєРЅРѕСЃС‚СЊ РІРІРѕРґР°
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
                if ("0123456789.,".indexOf(string) != -1) {  //РїСЂРѕРІРµСЂРєР° РЅР° РєРѕСЂСЂРµРєРЅРѕСЃС‚СЊ РІРІРѕРґР°
                    super.replace(fb, offset, length, string, attrs);
                }
            }
        });

        JTextField editorText3 = (JTextField) txt1;
        PlainDocument doc3 = (PlainDocument) editorText3.getDocument();
        doc3.setDocumentFilter(new DocumentFilter() {

            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if ("0123456789.,".indexOf(string) != -1) { //РїСЂРѕРІРµСЂРєР° РЅР° РєРѕСЂСЂРµРєРЅРѕСЃС‚СЊ РІРІРѕРґР°
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
                if ("0123456789.,".indexOf(string) != -1) {  //РїСЂРѕРІРµСЂРєР° РЅР° РєРѕСЂСЂРµРєРЅРѕСЃС‚СЊ РІРІРѕРґР°
                    super.replace(fb, offset, length, string, attrs);
                }
            }
        });
    }
}
