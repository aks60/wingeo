package frames;

import frames.swing.comp.ProgressBar;
import builder.model.Com5t;
import dataset.Connect;
import dataset.Field;
import dataset.Query;
import dataset.Record;
import frames.dialog.DicArtikl;
import frames.dialog.DicEnums;
import frames.dialog.DicName;
import domain.eArtikl;
import domain.eFurniture;
import domain.eParams;
import domain.eSysfurn;
import domain.eSyspar1;
import domain.eSysprod;
import domain.eSysprof;
import domain.eSystree;
import enums.LayoutProd;
import enums.LayoutHand;
import enums.TypeArt;
import enums.UseSide;
import enums.TypeOpen2;
import enums.UseType;
import enums.TypeUse;
import java.util.ArrayList;
import java.util.stream.Stream;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import frames.swing.comp.DefCellRendererBool;
import frames.swing.comp.TableFieldFormat;
import frames.swing.comp.DefTableModel;
import builder.Wincalc;
import builder.making.TFurniture;
import builder.script.GsonElem;
import common.UCom;
import domain.eArtdet;
import domain.eColor;
import enums.Layout;
import enums.PKjson;
import frames.dialog.DicColor;
import frames.swing.comp.Canvas;
import frames.swing.comp.DefMutableTreeNode;
import java.awt.CardLayout;
import java.awt.Component;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import startup.App;
import common.listener.ListenerRecord;
import common.listener.ListenerFrame;
import builder.making.UColor;
import builder.model.AreaStvorka;
import builder.model.UPar;
import builder.script.GsonRoot;
import builder.script.GsonScript;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import common.eProp;
import common.listener.ListenerAction;
import domain.eGroups;
import enums.TypeGrup;
import frames.dialog.ParDefVal;
import frames.dialog.ParDefault;
import frames.swing.comp.Scene;
import frames.swing.comp.TableFieldFilter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import common.listener.ListenerReload;
import static java.util.stream.Collectors.toList;
import javax.swing.JTree;
import org.locationtech.jts.geom.Envelope;
import common.listener.ListenerGet;
import common.listener.ListenerSet;
import frames.swing.comp.CardPanel;

public class Systree extends javax.swing.JFrame implements ListenerReload, ListenerAction {

    private ListenerRecord listenerArtikl, listenerModel, listenerFurn, listenerElemvar,
            listenerParam1, listenerParam2, listenerArt211, listenerArt212, ListenerCom5t;
    private ListenerGet<Wincalc> listenerWincalc = null;
    private ListenerAction listenerCangeAndRedraw = null;
    private ImageIcon icon = new ImageIcon(getClass().getResource("/resource/img16/b031.gif"));
    private Query qGroups = new Query(eGroups.values());
    private Query qParams = new Query(eParams.values());
    private Query qArtikl = new Query(eArtikl.id, eArtikl.code, eArtikl.name);
    private Query qSystree = new Query(eSystree.values());
    private Query qSysprod = new Query(eSysprod.values());
    private Query qSysprof = new Query(eSysprof.values(), eArtikl.values());
    private Query qSysfurn = new Query(eSysfurn.values(), eFurniture.values());
    private Query qSyspar1b = new Query(eSyspar1.values());
    private Query qSyspar1a = new Query(eSyspar1.values());
    private int systreeID = -1; //выбранная система (nuni)
    private int elementID = -1; //выбранная система (nuni)
    private boolean writeNuni = true;
    private Canvas canvas = new Canvas();
    private Scene scene = null;
    private CardPanel cardPanel = null;
    private TableFieldFormat rsvSystree;
    private java.awt.Frame models = null;
    private DefMutableTreeNode sysNode = null;
    private DefMutableTreeNode winNode = null;
    private TreeNode[] selectedPath = null;

    public Systree() {
        initComponents();
        scene = new Scene(canvas, this, this);
        initElements();
        cardPanel = new CardPanel(listenerWincalc, listenerCangeAndRedraw, winTree, ppmTree, qGroups, qSysprof, pan7);
        loadingData();
        loadingModel();
        listenerAdd();
        listenerSet();
        tabb1.setSelectedIndex(4);
    }

    //Конструктор с поиском из конструктива 
    //(составов и фурнитуры) см. btnFind2
    public Systree(int nuni, int mode) {
        initComponents();
        scene = new Scene(canvas, this);
        this.systreeID = nuni;
        this.writeNuni = false;
        initElements();
        cardPanel = new CardPanel(listenerWincalc, listenerCangeAndRedraw, winTree, ppmTree, qGroups, qSysprof, pan7);
        loadingData();
        loadingModel();
        listenerAdd();
        listenerSet();
        tabb1.setSelectedIndex(mode);
    }

    public final void loadingData() {
        //Получим сохранённую systreeID при выходе из программы
        Record sysprodRec = null; //при открытии указывает на конструкцию
        if (this.systreeID == -1 && "-1".equals(eProp.sysprodID.getProp()) != true) {
            sysprodRec = eSysprod.find(Integer.valueOf(eProp.sysprodID.getProp()));
        }
        if (sysprodRec != null) {
            this.systreeID = sysprodRec.getInt(eSysprod.systree_id);

        } else if (eSysprod.data().isEmpty() == false) {
            if (this.systreeID == -1) {
                sysprodRec = eSysprod.data().get(0);
                this.systreeID = sysprodRec.getInt(eSysprod.systree_id);
            }
        }
        qGroups.sq2(eGroups.data(), eGroups.grup, TypeGrup.PARAM_USER.id, eGroups.grup, TypeGrup.COLOR_MAP.id);
        qSystree.sql(eSystree.data(), eSystree.up).sort(eSystree.npp);
        qParams.sql(eParams.data(), eParams.up);
        qArtikl.sql(eArtikl.data(), eArtikl.level1, 2, eArtikl.level2, 11, 12);

        loadingTree1();
    }

    public final void loadingModel() {
        ((DefaultTreeCellEditor) sysTree.getCellEditor()).addCellEditorListener(new CellEditorListener() {

            public void editingStopped(ChangeEvent e) {
                String str = ((DefaultTreeCellEditor) sysTree.getCellEditor()).getCellEditorValue().toString();
                sysNode.rec().set(eSystree.name, str);
                sysNode.setUserObject(str);
                qSystree.update(sysNode.rec()); //сохраним в базе
            }

            public void editingCanceled(ChangeEvent e) {
                editingStopped(e);
            }
        });
        new DefTableModel(tab2, qSysprof, eSysprof.npp, eSysprof.use_type, eSysprof.use_side, eArtikl.code, eArtikl.name) {

            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (field == eSysprof.use_side && val != null) {
                    UseSide en = UseSide.get(Integer.valueOf(val.toString()));
                    if (en != null) {
                        return en.text();
                    }
                } else if (field == eSysprof.use_type && val != null) {
                    UseType en = UseType.get(Integer.valueOf(val.toString()));
                    if (en != null) {
                        return en.text();
                    }
                }
                return val;
            }
        };
        new DefTableModel(tab3, qSysfurn, eSysfurn.npp, eFurniture.name, eSysfurn.side_open, eSysfurn.replac, eSysfurn.hand_pos, eSysfurn.artikl_id1, eSysfurn.artikl_id2) {

            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (val != null) {
                    if (field == eSysfurn.side_open) {
                        int id = Integer.valueOf(val.toString());
                        return List.of(TypeOpen2.values()).stream().filter(el -> el.id == id).findFirst().orElse(TypeOpen2.REQ).name;
                    } else if (field == eSysfurn.hand_pos) {
                        int id = Integer.valueOf(val.toString());
                        return List.of(LayoutHand.values()).stream().filter(el -> el.id == id).findFirst().orElse(LayoutHand.MIDL).name;
                    } else if (field == eSysfurn.artikl_id1) {
                        int id = Integer.valueOf(val.toString());
                        return qArtikl.stream().filter(rec -> rec.getInt(eArtikl.id) == id).findFirst().orElse(eArtikl.up.newRecord(Query.SEL)).get(eArtikl.code);
                    } else if (field == eSysfurn.artikl_id2) {
                        int id = Integer.valueOf(val.toString());
                        return qArtikl.stream().filter(rec -> rec.getInt(eArtikl.id) == id).findFirst().orElse(eArtikl.up.newRecord(Query.SEL)).get(eArtikl.code);
                    }
                }
                return val;
            }
        };
        new DefTableModel(tab4, qSyspar1a, eSyspar1.groups_id, eSyspar1.text, eSyspar1.fixed) {
            public Object getValueAt(int col, int row, Object val) {

                Field field = columns[col];
                if (val != null && field == eSyspar1.groups_id) {
                    //Record paramsRec = qParams.find(val, eParams.id);
                    return qGroups.find(eGroups.data(), eGroups.id, Integer.valueOf(String.valueOf(val))).getDev(eGroups.name, val);
                }
                return val;
            }
        };
        new DefTableModel(tab5, qSysprod, eSysprod.name, eSysprod.id);
        new DefTableModel(tab7, qSyspar1b, eSyspar1.groups_id, eSyspar1.text) {
            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (val != null && field == eSyspar1.groups_id) {
                    return qGroups.find(eGroups.data(), eGroups.id, Integer.valueOf(String.valueOf(val))).getDev(eGroups.name, val);
                }
                return val;
            }
        };

        tab4.getColumnModel().getColumn(2).setCellRenderer(new DefCellRendererBool());
        tab5.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 1) {
                    Object v = qSysprod.get(row).get(eSysprod.values().length);
                    if (v instanceof Wincalc) {
                        label.setIcon(((Wincalc) v).imageIcon);
                    }
                } else {
                    label.setIcon(null);
                }
                return label;
            }
        });

        rsvSystree = new TableFieldFormat(sysTree) {

            public Set<JTextField> set = new HashSet<JTextField>();

            public void setTxt(JTextField jtf, String str) {
                set.add(jtf);
                jtf.setText(str);
            }

            @Override
            public void load() {
                super.load();
                int typesID = sysNode.rec().getInt(eSystree.types);
                int imgviewID = sysNode.rec().getInt(eSystree.imgview);
                TypeUse typeUse = Stream.of(TypeUse.values()).filter(en -> en.numb() == typesID).findFirst().orElse(TypeUse.EMPTY);
                LayoutProd layoutProduct = Stream.of(LayoutProd.values()).filter(en -> en.numb() == imgviewID).findFirst().orElse(LayoutProd.P1);
                setTxt(txt7, typeUse.name);
                setTxt(txt11, layoutProduct.name);
            }

            @Override
            public void clear() {
                super.clear();
                set.forEach(s -> s.setText(null));
            }
        };
        rsvSystree.add(eSystree.glas, txt1);
        rsvSystree.add(eSystree.depth, txt2);
        rsvSystree.add(eSystree.col1, txt3);
        rsvSystree.add(eSystree.col2, txt4);
        rsvSystree.add(eSystree.col3, txt5);
        rsvSystree.add(eSystree.pref, txt12);
        rsvSystree.add(eSystree.coef, txt35);

        canvas.setVisible(true);
        if (selectedPath != null) {
            sysTree.setSelectionPath(new TreePath(selectedPath));
        } else {
            UGui.setSelectedRow(tab4);
        }
    }

    public void loadingTree1() {
        Record recordRoot = eSystree.up.newRecord(Query.SEL);
        recordRoot.set(eSystree.id, -1);
        recordRoot.set(eSystree.parent_id, -1);
        recordRoot.set(eSystree.name, "Дерево системы профилей");
        DefMutableTreeNode rootTree = new DefMutableTreeNode(recordRoot);
        ArrayList<DefMutableTreeNode> nodeList = new ArrayList<DefMutableTreeNode>();

        for (Record record : qSystree) { //первый уровень
            if (record.getInt(eSystree.parent_id) == record.getInt(eSystree.id)) {
                DefMutableTreeNode node = new DefMutableTreeNode(record);
                rootTree.add(node);
                nodeList.add(node);
                if (record.getInt(eSystree.id) == systreeID) {
                    selectedPath = node.getPath(); //запомним path для nuni
                }
            }
        }
        ArrayList<DefMutableTreeNode> nodeList2 = addChild(nodeList);  //второй
        ArrayList<DefMutableTreeNode> nodeList3 = addChild(nodeList2); //третий
        ArrayList<DefMutableTreeNode> nodeList4 = addChild(nodeList3);
        ArrayList<DefMutableTreeNode> nodeList5 = addChild(nodeList4);
        ArrayList<DefMutableTreeNode> nodeList6 = addChild(nodeList5);

        sysTree.setModel(new DefaultTreeModel(rootTree));
        scr1.setViewportView(sysTree);
    }

    public void loadingTree2(Wincalc winc) {
        try {
            DefMutableTreeNode root = UTree.loadWinTree(winc);
            winTree.setModel(new DefaultTreeModel(root));
            winTree.setRootVisible(false);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.loadingWinTree() " + e);
        }
    }

    public void loadingTab5() {
        qSysprod.sql(eSysprod.data(), eSysprod.systree_id, systreeID).sort(eSysprod.npp, eSysprod.id);
        DefaultTableModel dm = (DefaultTableModel) tab5.getModel();
        dm.getDataVector().removeAllElements();
        for (Record record : qSysprod.table(eSysprod.up)) {
            try {
                String script = record.getStr(eSysprod.script);
                Wincalc iwinc = new Wincalc(script);
                iwinc.imageIcon = Canvas.createIcon(iwinc, 68);
                record.add(iwinc);

            } catch (Exception e) {
                System.err.println("Ошибка:Systree.loadingTab5() " + e);
            }
        }
        ((DefaultTableModel) tab5.getModel()).fireTableDataChanged();
    }

    //При выборе другой системы
    public void selectionTree1() {
        try {
            UGui.stopCellEditing(tab2, tab3, tab4, tab5);
            List.of(tab2, tab3, tab4).forEach(table -> ((DefTableModel) table.getModel()).getQuery().execsql());

            sysNode = (DefMutableTreeNode) sysTree.getLastSelectedPathComponent();
            if (sysNode != null && sysNode.getChildCount() == 0) {
                systreeID = sysNode.rec().getInt(eSystree.id);
                rsvSystree.load();
                qSysprof.sql(eSysprof.data(), eSysprof.systree_id, sysNode.rec().getInt(eSystree.id)).sort(eSysprof.npp);
                qSysprof.table(eArtikl.up).join(qSysprof, eArtikl.data(), eSysprof.artikl_id, eArtikl.id);
                qSysfurn.sql(eSysfurn.data(), eSysfurn.systree_id, sysNode.rec().getInt(eSystree.id)).sort(eSysfurn.npp);
                qSysfurn.table(eFurniture.up).join(qSysfurn, eFurniture.data(), eSysfurn.furniture_id, eFurniture.id);
                qSyspar1a.sql(eSyspar1.data(), eSyspar1.systree_id, sysNode.rec().getInt(eSystree.id));

                lab1.setText("Система ID = " + systreeID);
                lab2.setText("Элемент ID = -1");
                Collections.sort(qSyspar1a, (o1, o2) -> qGroups.find(eGroups.data(), eGroups.id, o1.getInt(eSyspar1.groups_id)).getStr(eGroups.name)
                        .compareTo(qGroups.find(eGroups.data(), eGroups.id, o2.getInt(eSyspar1.groups_id)).getStr(eGroups.name)));

                loadingTab5();

                ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
                ((DefaultTableModel) tab3.getModel()).fireTableDataChanged();
                ((DefaultTableModel) tab4.getModel()).fireTableDataChanged();
                ((DefaultTableModel) tab5.getModel()).fireTableDataChanged();
                UGui.setSelectedRow(tab2);
                UGui.setSelectedRow(tab3);
                UGui.setSelectedRow(tab4);
                if (qSysprod.isEmpty() == false) {

                    int index = -1;
                    int sysprodID = Integer.valueOf(eProp.sysprodID.getProp());
                    for (int i = 0; i < qSysprod.size(); ++i) {
                        if (qSysprod.get(i).getInt(eSysprod.id) == sysprodID) {
                            index = i;
                            UGui.scrollRectToIndex(index, tab5);
                        }
                    }
                    if (index != -1) {
                        UGui.setSelectedIndex(tab5, index);

                    } else {
                        UGui.setSelectedRow(tab5);
                    }
                } else {
                    canvas.init(null);
                    canvas.repaint();
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.selectionTree1() " + e);
        }
    }

    //При выборе элемента конструкции public  
    public void selectionTree2() {
        try {
            //Выделенный элемент
            Object selNode = winTree.getLastSelectedPathComponent();
            if (selNode instanceof DefMutableTreeNode) {
                winNode = (DefMutableTreeNode) winTree.getLastSelectedPathComponent();
                Com5t com5t = winNode.com5t();
                Wincalc winc = wincalc();

                UGui.changePpmTree(winTree, ppmTree, com5t);

                if (com5t.type == enums.Type.PARAM) {
                    ((CardLayout) pan7.getLayout()).show(pan7, "card11");
                    qSyspar1b.clear();
                    winc.mapPardef.forEach((pk, syspar1Rec) -> qSyspar1b.add(syspar1Rec));
                    Collections.sort(qSyspar1b, (o1, o2) -> qGroups.find(eGroups.data(), eGroups.id, o1.getInt(eSyspar1.groups_id)).getStr(eGroups.name)
                            .compareTo(qGroups.find(eGroups.data(), eGroups.id, o2.getInt(eSyspar1.groups_id)).getStr(eGroups.name)));
                    ((DefTableModel) tab7.getModel()).fireTableDataChanged();

                } else if (cardPanel != null) {
                    cardPanel.selectionTree();
                }
                lab2.setText("Элемент ID = " + UCom.format(com5t.id, 2));
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.selectionTree2() " + e);
        }
    }

    //Выбор другой конструкции
    public void selectionTab5() {
        int index = UGui.getIndexRec(tab5);
        if (index != -1) {
            Record sysprodRec = qSysprod.table(eSysprod.up).get(index);
            if (writeNuni == true) {
                eProp.sysprodID.putProp(sysprodRec.getStr(eSysprod.id)); //запишем текущий sysprodID в файл
            }
            App.Top.frame.setTitle(UGui.designTitle());

            Object w = sysprodRec.get(eSysprod.values().length);
            if (w instanceof Wincalc) { //прорисовка окна               
                Wincalc winc = (Wincalc) w;

                winc.sizeEvent = cardPanel.sizeEvent;

                GsonElem.setMaxID(winc); //установим генератор идентификаторов

                loadingTree2(winc);

                winTree.setSelectionRow(1);
                scene.init(winc);
                canvas.draw();
            }
        } else {
            winTree.setModel(new DefaultTreeModel(new DefMutableTreeNode("")));
        }
    }

    public void listenerAdd() {
        UGui.buttonCellEditor(tab2, 1).addActionListener(event -> {
            new DicEnums(this, (record) -> {
                UGui.cellParamEnum(record, tab2, eSysprof.use_type, tab2, tab3, tab4);
            }, UseType.values());
        });

        UGui.buttonCellEditor(tab2, 2).addActionListener(event -> {
            new DicEnums(this, (record) -> {
                UGui.cellParamEnum(record, tab2, eSysprof.use_side, tab2, tab3, tab4);
            }, UseSide.values());
        });

        UGui.buttonCellEditor(tab2, 3).addActionListener(event -> {
            new DicArtikl(this, listenerArtikl, false, 1);
        });

        UGui.buttonCellEditor(tab2, 4).addActionListener(event -> {
            new DicArtikl(this, listenerArtikl, false, 1);
        });

        UGui.buttonCellEditor(tab3, 1).addActionListener(event -> {
            DicName frame = new DicName(this, listenerFurn, new Query(eFurniture.values())
                    .sql(eFurniture.data(), eFurniture.types, 0, 1).sort(eFurniture.name), eFurniture.name);
        });

        UGui.buttonCellEditor(tab3, 2).addActionListener(event -> {
            DicEnums frame = new DicEnums(this, (record) -> {
                UGui.cellParamEnum(record, tab3, eSysfurn.side_open, tab2, tab3, tab4, tab5);
            }, TypeOpen2.values());
        });

        UGui.buttonCellEditor(tab3, 4).addActionListener(event -> {
            DicEnums frame = new DicEnums(this, (record) -> {
                UGui.cellParamEnum(record, tab3, eSysfurn.hand_pos, tab2, tab3, tab4, tab5);
            }, LayoutHand.values());
        });

        UGui.buttonCellEditor(tab3, 5).addActionListener(event -> {
            int furnityreId = qSysfurn.getAs(UGui.getIndexRec(tab3), eSysfurn.furniture_id);
            new DicArtikl(this, listenerArt211, false, furnityreId, TypeArt.X211.id1, TypeArt.X211.id2);
        });

        UGui.buttonCellEditor(tab3, 6).addActionListener(event -> {
            int furnityreId = qSysfurn.getAs(UGui.getIndexRec(tab3), eSysfurn.furniture_id);
            new DicArtikl(this, listenerArt212, false, furnityreId, TypeArt.X212.id1, TypeArt.X212.id2);
        });

        UGui.buttonCellEditor(tab4, 0).addActionListener(event -> {
            new ParDefault(this, listenerParam1);
        });

        UGui.buttonCellEditor(tab4, 1).addActionListener(event -> {
            Record syspar1Rec = qSyspar1a.get(UGui.getIndexRec(tab4));
            int groupsID = syspar1Rec.getInt(eSyspar1.groups_id);
            if (groupsID == -1) {
                new ParDefault(this, listenerParam1);
            } else {
                new ParDefault(this, listenerParam1, groupsID);
            }
        });

        UGui.buttonCellEditor(tab7, 1).addActionListener(event -> {
            Record syspar1Rec = qSyspar1b.get(UGui.getIndexRec(tab7));
            if (syspar1Rec.getInt(eSyspar1.fixed) != 1) {
                int groupsID = syspar1Rec.getInt(eSyspar1.groups_id);
                new ParDefVal(this, listenerParam2, groupsID);
            } else {
                JOptionPane.showMessageDialog(Systree.this, "Неизменяемый параметр в системе", "ВНИМАНИЕ!", 1);
            }
        });
    }

    public void listenerSet() {

        listenerArtikl = (record) -> {
            UGui.stopCellEditing(tab2, tab3, tab4, tab5);
            int index = UGui.getIndexRec(tab2);
            qSysprof.set(record.getInt(eArtikl.id), UGui.getIndexRec(tab2), eSysprof.artikl_id);
            qSysprof.table(eArtikl.up).set(record.get(eArtikl.name), UGui.getIndexRec(tab2), eArtikl.name);
            qSysprof.table(eArtikl.up).set(record.get(eArtikl.code), UGui.getIndexRec(tab2), eArtikl.code);
            ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
            UGui.setSelectedIndex(tab2, index);
        };

        //Вставка без UGui.insertRecordCur() т.к. рисунок добавляется в доп. поле
        listenerModel = (record) -> {
            UGui.stopCellEditing(tab2, tab3, tab4, tab5);

            //Запишем в скрипт ветку из которого будет создаваться окно  
            String script = record.get(2).toString();
            JsonObject je = new GsonBuilder().create().fromJson(script, JsonObject.class);
            je.addProperty("nuni", systreeID);
            String script2 = new GsonBuilder().create().toJson(je);

            //Сохраним скрипт в базе
            Record sysprodRec = eSysprod.up.newRecord(Query.INS);
            sysprodRec.setNo(eSysprod.id, Connect.genId(eSysprod.id));
            sysprodRec.setNo(eSysprod.npp, sysprodRec.get(eSysprod.id));
            sysprodRec.setNo(eSysprod.systree_id, systreeID);
            sysprodRec.setNo(eSysprod.name, record.get(1));
            sysprodRec.setNo(eSysprod.script, script2);
            eSysprod.data().add(sysprodRec); //добавим в кэш новую запись
            qSysprod.insert(sysprodRec);

            loadingTab5();

            ((DefaultTableModel) tab5.getModel()).fireTableDataChanged();
            for (int index = 0; index < qSysprod.size(); ++index) {
                if (qSysprod.get(index, eSysprod.id) == sysprodRec.get(eSysprod.id)) {
                    UGui.setSelectedIndex(tab5, index); //выделение рабочей записи
                    UGui.scrollRectToRow(index, tab5);
                    winTree.setSelectionRow(0);
                }
            }
        };

        listenerFurn = (record) -> {
            UGui.stopCellEditing(tab2, tab3, tab4, tab5);
            int index = UGui.getIndexRec(tab3);
            qSysfurn.set(record.getInt(eFurniture.id), UGui.getIndexRec(tab3), eSysfurn.furniture_id);
            qSysfurn.table(eFurniture.up).set(record.get(eFurniture.name), UGui.getIndexRec(tab3), eFurniture.name);
            ((DefaultTableModel) tab3.getModel()).fireTableDataChanged();
            UGui.setSelectedIndex(tab3, index);
        };

        listenerArt211 = (record) -> {
            UGui.stopCellEditing(tab2, tab3, tab4, tab5);
            int index = UGui.getIndexRec(tab3);
            qSysfurn.set(record.getInt(eArtikl.id), UGui.getIndexRec(tab3), eSysfurn.artikl_id1);
            ((DefaultTableModel) tab3.getModel()).fireTableDataChanged();
            UGui.setSelectedIndex(tab3, index);
        };

        listenerArt212 = (record) -> {
            UGui.stopCellEditing(tab2, tab3, tab4, tab5);
            int index = UGui.getIndexRec(tab3);
            qSysfurn.set(record.getInt(eArtikl.id), UGui.getIndexRec(tab3), eSysfurn.artikl_id2);
            ((DefaultTableModel) tab3.getModel()).fireTableDataChanged();
            UGui.setSelectedIndex(tab3, index);
        };

        listenerParam1 = (record) -> {
            UGui.stopCellEditing(tab2, tab3, tab4, tab5);
            int index = UGui.getIndexRec(tab4);
            int index2 = UGui.getIndexRec(tab5);
            if (index2 != -1) {
                systreeID = sysNode.rec().getInt(eSystree.id);
                qSyspar1a.set(systreeID, index, eSyspar1.systree_id);
                qSyspar1a.set(record.get(0), index, eSyspar1.text);
                qSyspar1a.set(record.get(2), index, eSyspar1.groups_id);
                ((DefaultTableModel) tab4.getModel()).fireTableDataChanged();
                UGui.setSelectedIndex(tab4, index);
            }
        };

        listenerParam2 = (record) -> {
            UGui.stopCellEditing(tab2, tab3, tab4, tab5, tab7);
            int index = UGui.getIndexRec(tab5);
            int index2 = UGui.getIndexRec(tab7);
            if (index != -1) {
                Record sysprodRec = qSysprod.get(index);
                String script = sysprodRec.getStr(eSysprod.script);
                String script2 = UGui.ioknaParamUpdate(script, record.getInt(0));
                sysprodRec.set(eSysprod.script, script2);
                wincalc().build(script2);
                selectionTree2();
                UGui.setSelectedIndex(tab7, index2);
            }
        };
    }

    public ArrayList<DefMutableTreeNode> addChild(ArrayList<DefMutableTreeNode> nodeList1) {
        try {
            ArrayList<DefMutableTreeNode> nodeList2 = new ArrayList<DefMutableTreeNode>();
            for (DefMutableTreeNode node : nodeList1) {
                for (Record record : qSystree) {
                    if (record.getInt(eSystree.parent_id) == node.rec().getInt(eSystree.id)
                            && record.getInt(eSystree.parent_id) != record.getInt(eSystree.id)) {

                        DefMutableTreeNode node2 = new DefMutableTreeNode(record);
                        node.add(node2);
                        nodeList2.add(node2);
                        if (record.getInt(eSystree.id) == systreeID) {
                            selectedPath = node2.getPath(); //запомним path для nuni
                        }
                    }
                }
            }
            return nodeList2;

        } catch (Exception e) {
            System.err.println("ОШИБКА:Systree.addChild() " + e);
            return null;
        }
    }

    //Изменить скрипт в базе, пересчитать и перерисовать
    public void changeAndRedraw() {
        try {
            //Сохраним скрипт в базе
            String script = wincalc().gson.toJson();
            Record sysprodRec = qSysprod.get(UGui.getIndexRec(tab5));
            sysprodRec.set(eSysprod.script, script);

            //Экземпляр нового скрипта
            Wincalc winc = wincalc();
            winc.build(script);
            winc.imageIcon = Canvas.createIcon(winc, 68);

            //Запомним курсор
            DefMutableTreeNode selectNode = (DefMutableTreeNode) winTree.getLastSelectedPathComponent();
            double id = (selectNode != null) ? selectNode.com5t().id : -1;

            //Перегрузим winTree
            loadingTree2(winc);

            //Установим курсор
            UTree.selectionPathWin(id, winTree);

            //Перерисуем конструкцию
            canvas.init(winc);
            canvas.draw();

            //Обновим поля форм
            selectionTree2();

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.updateScript() " + e);
        }
    }

    //Отменить все изменения
    private void undoChanges() {
        try {
            int index = UGui.getIndexRec(tab5);
            if (index != -1) {

                Record sysprodRow = qSysprod.get(index);
                Record sysprodRec = new Query(eSysprod.values()).select(eSysprod.up, "where", eSysprod.id, "=", sysprodRow.getInt(eSysprod.id)).get(0);
                String script = sysprodRec.getStr(eSysprod.script);

                Wincalc winc = wincalc();
                winc.build(script);
                sysprodRow.set(eSysprod.up, Query.SEL);

                //Запомним курсор
                DefMutableTreeNode selectNode = (DefMutableTreeNode) winTree.getLastSelectedPathComponent();
                double id = (selectNode != null) ? selectNode.com5t().id : -1;

                //Перегрузим winTree
                loadingTree2(winc);

                //Установим курсор
                UTree.selectionPathWin(id, winTree);

                //Перерисуем конструкцию
                canvas.init(winc);
                canvas.draw();

                //Обновим поля форм
                selectionTree2();

            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.undoChanges() " + e);
        }
    }

    //Получить текущий Wincalc
    private Wincalc wincalc() {
        int index = UGui.getIndexRec(tab5);
        if (index != -1) {
            Record sysprodRec = qSysprod.get(index);
            Object winc = sysprodRec.get(eSysprod.values().length);
            if (winc instanceof Wincalc) {
                return (Wincalc) winc;
            }
        }
        return null;
    }

    @Override
    public Query reload(boolean b) {
        changeAndRedraw();
        return qSysprod;
    }

    @Override
    public void action() {
        undoChanges();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ppmCrud = new javax.swing.JPopupMenu();
        mInsert = new javax.swing.JMenuItem();
        mDelit = new javax.swing.JMenuItem();
        ppmTree = new javax.swing.JPopupMenu();
        addImpost = new javax.swing.JMenu();
        addImpostHor = new javax.swing.JMenuItem();
        addImpostVer = new javax.swing.JMenuItem();
        removeImpost = new javax.swing.JMenuItem();
        addStvorka = new javax.swing.JMenuItem();
        removeStvorka = new javax.swing.JMenuItem();
        removeMosquit = new javax.swing.JMenuItem();
        elements = new javax.swing.JMenuItem();
        panT = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tool = new javax.swing.JPanel();
        btnIns = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        btnReport1 = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnTest = new javax.swing.JButton();
        btnFind1 = new javax.swing.JButton();
        btnMoveU = new javax.swing.JButton();
        btnTree = new javax.swing.JButton();
        btnMoveD = new javax.swing.JButton();
        btnFind2 = new javax.swing.JButton();
        centr = new javax.swing.JPanel();
        scr1 = new javax.swing.JScrollPane();
        sysTree = new javax.swing.JTree();
        split1 = new javax.swing.JSplitPane();
        pan2 = new javax.swing.JPanel();
        pan7 = new javax.swing.JPanel();
        pan11 = new javax.swing.JPanel();
        scr7 = new javax.swing.JScrollPane();
        tab7 = new javax.swing.JTable();
        pan12 = new javax.swing.JPanel();
        panDesign = new javax.swing.JPanel();
        tabb1 = new javax.swing.JTabbedPane();
        pan6 = new javax.swing.JPanel();
        lab13 = new javax.swing.JLabel();
        lab14 = new javax.swing.JLabel();
        lab15 = new javax.swing.JLabel();
        lab16 = new javax.swing.JLabel();
        lab17 = new javax.swing.JLabel();
        lab19 = new javax.swing.JLabel();
        lab24 = new javax.swing.JLabel();
        txt1 = new javax.swing.JTextField();
        txt2 = new javax.swing.JTextField();
        txt3 = new javax.swing.JTextField();
        txt4 = new javax.swing.JTextField();
        txt5 = new javax.swing.JTextField();
        txt7 = new javax.swing.JTextField();
        txt11 = new javax.swing.JTextField();
        btn4 = new javax.swing.JButton();
        btn7 = new javax.swing.JButton();
        btn11 = new javax.swing.JButton();
        lab47 = new javax.swing.JLabel();
        txt35 = new javax.swing.JTextField();
        lab69 = new javax.swing.JLabel();
        txt12 = new javax.swing.JTextField();
        pan3 = new javax.swing.JPanel();
        scr2 = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JTable();
        pan4 = new javax.swing.JPanel();
        scr3 = new javax.swing.JScrollPane();
        tab3 = new javax.swing.JTable();
        pan5 = new javax.swing.JPanel();
        scr4 = new javax.swing.JScrollPane();
        tab4 = new javax.swing.JTable();
        pan10 = new javax.swing.JPanel();
        scr5 = new javax.swing.JScrollPane();
        tab5 = new javax.swing.JTable();
        scr6 = new javax.swing.JScrollPane();
        winTree = new javax.swing.JTree();
        south = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        lab1 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        lab2 = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));

        mInsert.setFont(frames.UGui.getFont(1,0));
        mInsert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c033.gif"))); // NOI18N
        mInsert.setText("Добавить");
        mInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmActionItems(evt);
            }
        });
        ppmCrud.add(mInsert);

        mDelit.setFont(frames.UGui.getFont(1,0));
        mDelit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c034.gif"))); // NOI18N
        mDelit.setText("Удалить");
        mDelit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmActionItems(evt);
            }
        });
        ppmCrud.add(mDelit);

        addImpost.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        addImpost.setText("Добавить импост");

        addImpostHor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        addImpostHor.setText("горизонтальный");
        addImpostHor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addImpostHorAction(evt);
            }
        });
        addImpost.add(addImpostHor);

        addImpostVer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        addImpostVer.setText("вертикальный");
        addImpostVer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addImpostVerAction(evt);
            }
        });
        addImpost.add(addImpostVer);

        ppmTree.add(addImpost);

        removeImpost.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        removeImpost.setText("Удалить импост");
        removeImpost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeImpostAction(evt);
            }
        });
        ppmTree.add(removeImpost);

        addStvorka.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        addStvorka.setText("Добавить сворку");
        addStvorka.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStvorkaAction(evt);
            }
        });
        ppmTree.add(addStvorka);

        removeStvorka.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        removeStvorka.setText("Удалить створку");
        removeStvorka.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeStvorkaAction(evt);
            }
        });
        ppmTree.add(removeStvorka);

        removeMosquit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        removeMosquit.setText("Удалить москитку");
        removeMosquit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeMosquitAction(evt);
            }
        });
        ppmTree.add(removeMosquit);

        elements.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b057.gif"))); // NOI18N
        elements.setText("Составы элементов");
        elements.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                elementsView(evt);
            }
        });
        ppmTree.add(elements);

        panT.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "TESTOV PANEL**", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jLabel1.setText("AKSENO");

        jLabel2.setText("SERGEY");

        javax.swing.GroupLayout panTLayout = new javax.swing.GroupLayout(panT);
        panT.setLayout(panTLayout);
        panTLayout.setHorizontalGroup(
            panTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panTLayout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(panTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addContainerGap(363, Short.MAX_VALUE))
        );
        panTLayout.setVerticalGroup(
            panTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panTLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addContainerGap(322, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Модели системных профилей");
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage()));
        setMinimumSize(new java.awt.Dimension(800, 580));
        setPreferredSize(new java.awt.Dimension(1164, 700));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                Systree.this.windowClosed(evt);
            }
        });

        tool.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        tool.setPreferredSize(new java.awt.Dimension(800, 29));

        btnIns.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c033.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.eProp.locale); // NOI18N
        btnIns.setToolTipText(bundle.getString("Добавить")); // NOI18N
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

        btnDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c034.gif"))); // NOI18N
        btnDel.setToolTipText(bundle.getString("Удалить")); // NOI18N
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

        btnReport1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c053.gif"))); // NOI18N
        btnReport1.setToolTipText(bundle.getString("Печать")); // NOI18N
        btnReport1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnReport1.setFocusable(false);
        btnReport1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnReport1.setMaximumSize(new java.awt.Dimension(25, 25));
        btnReport1.setMinimumSize(new java.awt.Dimension(25, 25));
        btnReport1.setPreferredSize(new java.awt.Dimension(25, 25));
        btnReport1.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnReport1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnReport1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReport(evt);
            }
        });

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c009.gif"))); // NOI18N
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

        btnTest.setText("Test");
        btnTest.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnTest.setMaximumSize(new java.awt.Dimension(25, 25));
        btnTest.setMinimumSize(new java.awt.Dimension(25, 25));
        btnTest.setPreferredSize(new java.awt.Dimension(25, 25));
        btnTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTestActionPerformed(evt);
            }
        });

        btnFind1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c088.gif"))); // NOI18N
        btnFind1.setToolTipText(bundle.getString("Поиск записи")); // NOI18N
        btnFind1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnFind1.setMaximumSize(new java.awt.Dimension(25, 25));
        btnFind1.setMinimumSize(new java.awt.Dimension(25, 25));
        btnFind1.setPreferredSize(new java.awt.Dimension(25, 25));
        btnFind1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findFromArtikl(evt);
            }
        });

        btnMoveU.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c051.gif"))); // NOI18N
        btnMoveU.setToolTipText(bundle.getString("Переместить вверх")); // NOI18N
        btnMoveU.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnMoveU.setFocusable(false);
        btnMoveU.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMoveU.setMaximumSize(new java.awt.Dimension(25, 25));
        btnMoveU.setMinimumSize(new java.awt.Dimension(25, 25));
        btnMoveU.setPreferredSize(new java.awt.Dimension(25, 25));
        btnMoveU.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnMoveU.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMoveU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMove(evt);
            }
        });

        btnTree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c086.gif"))); // NOI18N
        btnTree.setToolTipText(bundle.getString("Изменить вид")); // NOI18N
        btnTree.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnTree.setFocusable(false);
        btnTree.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTree.setMaximumSize(new java.awt.Dimension(25, 25));
        btnTree.setMinimumSize(new java.awt.Dimension(25, 25));
        btnTree.setPreferredSize(new java.awt.Dimension(25, 25));
        btnTree.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnTree.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTreebtnMove(evt);
            }
        });

        btnMoveD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c052.gif"))); // NOI18N
        btnMoveD.setToolTipText(bundle.getString("Переместить вниз")); // NOI18N
        btnMoveD.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnMoveD.setFocusable(false);
        btnMoveD.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMoveD.setMaximumSize(new java.awt.Dimension(25, 25));
        btnMoveD.setMinimumSize(new java.awt.Dimension(25, 25));
        btnMoveD.setPreferredSize(new java.awt.Dimension(25, 25));
        btnMoveD.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnMoveD.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMoveD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMove(evt);
            }
        });

        btnFind2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c089.gif"))); // NOI18N
        btnFind2.setToolTipText(bundle.getString("Поиск записи")); // NOI18N
        btnFind2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnFind2.setFocusable(false);
        btnFind2.setMaximumSize(new java.awt.Dimension(25, 25));
        btnFind2.setMinimumSize(new java.awt.Dimension(25, 25));
        btnFind2.setPreferredSize(new java.awt.Dimension(25, 25));
        btnFind2.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnFind2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFind2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFind2(evt);
            }
        });

        javax.swing.GroupLayout toolLayout = new javax.swing.GroupLayout(tool);
        tool.setLayout(toolLayout);
        toolLayout.setHorizontalGroup(
            toolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnIns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnFind1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFind2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTree, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMoveU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMoveD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 539, Short.MAX_VALUE)
                .addComponent(btnTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnReport1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        toolLayout.setVerticalGroup(
            toolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnIns, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnDel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(toolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(btnReport1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnTest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnFind1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnMoveU, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnMoveD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnTree, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(btnFind2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        getContentPane().add(tool, java.awt.BorderLayout.PAGE_START);

        centr.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        centr.setMinimumSize(new java.awt.Dimension(105, 700));
        centr.setPreferredSize(new java.awt.Dimension(1164, 700));
        centr.setLayout(new java.awt.BorderLayout());

        scr1.setBorder(null);
        scr1.setPreferredSize(new java.awt.Dimension(260, 550));

        sysTree.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        sysTree.setFont(frames.UGui.getFont(0,0));
        sysTree.setEditable(true);
        sysTree.setName("sysTree"); // NOI18N
        sysTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });
        scr1.setViewportView(sysTree);

        centr.add(scr1, java.awt.BorderLayout.WEST);

        split1.setDividerLocation(400);
        split1.setDividerSize(2);
        split1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        split1.setResizeWeight(0.7);
        split1.setName("split1"); // NOI18N
        split1.setPreferredSize(new java.awt.Dimension(900, 550));

        pan2.setLayout(new java.awt.BorderLayout());

        pan7.setPreferredSize(new java.awt.Dimension(360, 506));
        pan7.setLayout(new java.awt.CardLayout());

        pan11.setLayout(new java.awt.BorderLayout());

        scr7.setBorder(null);
        scr7.setPreferredSize(new java.awt.Dimension(450, 300));

        tab7.setFont(frames.UGui.getFont(0,0));
        tab7.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Параметр конструкции", "Значение по умолчанию", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab7.setFillsViewportHeight(true);
        tab7.setName("tab7"); // NOI18N
        tab7.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });
        scr7.setViewportView(tab7);
        if (tab7.getColumnModel().getColumnCount() > 0) {
            tab7.getColumnModel().getColumn(0).setPreferredWidth(300);
            tab7.getColumnModel().getColumn(1).setPreferredWidth(140);
            tab7.getColumnModel().getColumn(2).setPreferredWidth(40);
            tab7.getColumnModel().getColumn(2).setMaxWidth(60);
        }

        pan11.add(scr7, java.awt.BorderLayout.CENTER);

        pan7.add(pan11, "card11");

        pan12.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Основные", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 1)));
        pan12.setToolTipText("");

        javax.swing.GroupLayout pan12Layout = new javax.swing.GroupLayout(pan12);
        pan12.setLayout(pan12Layout);
        pan12Layout.setHorizontalGroup(
            pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 350, Short.MAX_VALUE)
        );
        pan12Layout.setVerticalGroup(
            pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 383, Short.MAX_VALUE)
        );

        pan7.add(pan12, "card12");

        pan2.add(pan7, java.awt.BorderLayout.EAST);

        panDesign.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        panDesign.setLayout(new java.awt.BorderLayout());
        pan2.add(panDesign, java.awt.BorderLayout.CENTER);

        split1.setTopComponent(pan2);

        tabb1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 2, 0, 0));
        tabb1.setFont(frames.UGui.getFont(0,0));

        pan6.setPreferredSize(new java.awt.Dimension(623, 400));

        lab13.setFont(frames.UGui.getFont(0,0));
        lab13.setText("Заполн. по умолчанию");
        lab13.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab13.setPreferredSize(new java.awt.Dimension(112, 18));

        lab14.setFont(frames.UGui.getFont(0,0));
        lab14.setText("Доступные толщины");
        lab14.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab14.setPreferredSize(new java.awt.Dimension(120, 18));

        lab15.setFont(frames.UGui.getFont(0,0));
        lab15.setText("Основная текстура");
        lab15.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab15.setPreferredSize(new java.awt.Dimension(112, 18));

        lab16.setFont(frames.UGui.getFont(0,0));
        lab16.setText("Внутр. текстура");
        lab16.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab16.setPreferredSize(new java.awt.Dimension(112, 18));

        lab17.setFont(frames.UGui.getFont(0,0));
        lab17.setText("Внешняя текстура");
        lab17.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab17.setPreferredSize(new java.awt.Dimension(112, 18));

        lab19.setFont(frames.UGui.getFont(0,0));
        lab19.setText("Признак системы");
        lab19.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab19.setMaximumSize(new java.awt.Dimension(112, 18));
        lab19.setMinimumSize(new java.awt.Dimension(112, 18));
        lab19.setPreferredSize(new java.awt.Dimension(112, 18));

        lab24.setFont(frames.UGui.getFont(0,0));
        lab24.setText("Вид изделия по умолчанию");
        lab24.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab24.setPreferredSize(new java.awt.Dimension(120, 18));

        txt1.setEditable(false);
        txt1.setFont(frames.UGui.getFont(0,0));
        txt1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt1.setPreferredSize(new java.awt.Dimension(70, 18));

        txt2.setFont(frames.UGui.getFont(0,0));
        txt2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt2.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt2.setName("{4}"); // NOI18N
        txt2.setPreferredSize(new java.awt.Dimension(80, 18));

        txt3.setFont(frames.UGui.getFont(0,0));
        txt3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt3.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt3.setName("{5}"); // NOI18N
        txt3.setPreferredSize(new java.awt.Dimension(80, 18));

        txt4.setFont(frames.UGui.getFont(0,0));
        txt4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt4.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt4.setName("{5}"); // NOI18N
        txt4.setPreferredSize(new java.awt.Dimension(72, 18));

        txt5.setFont(frames.UGui.getFont(0,0));
        txt5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt5.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt5.setName("{5}"); // NOI18N
        txt5.setPreferredSize(new java.awt.Dimension(72, 18));

        txt7.setEditable(false);
        txt7.setFont(frames.UGui.getFont(0,0));
        txt7.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt7.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt7.setPreferredSize(new java.awt.Dimension(62, 18));

        txt11.setEditable(false);
        txt11.setFont(frames.UGui.getFont(0,0));
        txt11.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt11.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt11.setPreferredSize(new java.awt.Dimension(70, 18));

        btn4.setText("...");
        btn4.setToolTipText(bundle.getString("Закрыть")); // NOI18N
        btn4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn4.setMaximumSize(new java.awt.Dimension(21, 20));
        btn4.setMinimumSize(new java.awt.Dimension(21, 20));
        btn4.setPreferredSize(new java.awt.Dimension(21, 20));
        btn4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                glasdefToSystree(evt);
            }
        });

        btn7.setText("...");
        btn7.setToolTipText(bundle.getString("Закрыть")); // NOI18N
        btn7.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn7.setMaximumSize(new java.awt.Dimension(21, 20));
        btn7.setMinimumSize(new java.awt.Dimension(21, 20));
        btn7.setPreferredSize(new java.awt.Dimension(21, 20));
        btn7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeToSystree(evt);
            }
        });

        btn11.setText("...");
        btn11.setToolTipText(bundle.getString("Закрыть")); // NOI18N
        btn11.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn11.setMaximumSize(new java.awt.Dimension(21, 20));
        btn11.setMinimumSize(new java.awt.Dimension(21, 20));
        btn11.setPreferredSize(new java.awt.Dimension(21, 20));
        btn11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imageviewToSystree(evt);
            }
        });

        lab47.setFont(frames.UGui.getFont(0,0));
        lab47.setText("Коэф. рентабельности");
        lab47.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        txt35.setFont(frames.UGui.getFont(0,0));
        txt35.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt35.setName("{3}"); // NOI18N
        txt35.setPreferredSize(new java.awt.Dimension(80, 18));

        lab69.setFont(frames.UGui.getFont(0,0));
        lab69.setText("Код системы");
        lab69.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab69.setMaximumSize(new java.awt.Dimension(112, 18));
        lab69.setMinimumSize(new java.awt.Dimension(112, 18));
        lab69.setPreferredSize(new java.awt.Dimension(112, 18));

        txt12.setFont(frames.UGui.getFont(0,0));
        txt12.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt12.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt12.setPreferredSize(new java.awt.Dimension(80, 18));

        javax.swing.GroupLayout pan6Layout = new javax.swing.GroupLayout(pan6);
        pan6.setLayout(pan6Layout);
        pan6Layout.setHorizontalGroup(
            pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan6Layout.createSequentialGroup()
                        .addComponent(lab15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan6Layout.createSequentialGroup()
                        .addComponent(lab16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan6Layout.createSequentialGroup()
                        .addComponent(lab17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan6Layout.createSequentialGroup()
                        .addComponent(lab19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt7, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan6Layout.createSequentialGroup()
                        .addComponent(lab69, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan6Layout.createSequentialGroup()
                        .addComponent(lab14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan6Layout.createSequentialGroup()
                        .addComponent(lab24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt11, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan6Layout.createSequentialGroup()
                        .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pan6Layout.createSequentialGroup()
                                .addComponent(lab47, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(pan6Layout.createSequentialGroup()
                                .addComponent(lab13, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt1, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(2, 2, 2)))
                .addGap(19, 19, 19))
        );
        pan6Layout.setVerticalGroup(
            pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lab69, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lab13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan6Layout.createSequentialGroup()
                        .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lab19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btn7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lab15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lab16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lab17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pan6Layout.createSequentialGroup()
                        .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lab47)
                            .addComponent(txt35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lab24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txt11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btn11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lab14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(129, Short.MAX_VALUE))
        );

        tabb1.addTab("   Основные   ", pan6);

        pan3.setLayout(new java.awt.BorderLayout());

        scr2.setBorder(null);

        tab2.setFont(frames.UGui.getFont(0,0));
        tab2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Приоритет", "Применение", "Сторона", "Артикул", "Название", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, true, true, false
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
            tab2.getColumnModel().getColumn(0).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(0).setMaxWidth(120);
            tab2.getColumnModel().getColumn(1).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(2).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(3).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(4).setPreferredWidth(200);
            tab2.getColumnModel().getColumn(5).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(5).setMaxWidth(60);
        }

        pan3.add(scr2, java.awt.BorderLayout.CENTER);

        tabb1.addTab("   Профили   ", pan3);

        pan4.setLayout(new java.awt.BorderLayout());

        scr3.setBorder(null);

        tab3.setFont(frames.UGui.getFont(0,0));
        tab3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Приоритет", "Название  фурнитуры", "Тип открывания", "Замена", "Установка ручки", "Артикул ручки", "Артикул подвеса", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab3.setFillsViewportHeight(true);
        tab3.setName("tab3"); // NOI18N
        tab3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });
        scr3.setViewportView(tab3);
        if (tab3.getColumnModel().getColumnCount() > 0) {
            tab3.getColumnModel().getColumn(0).setPreferredWidth(40);
            tab3.getColumnModel().getColumn(0).setMaxWidth(80);
            tab3.getColumnModel().getColumn(1).setPreferredWidth(200);
            tab3.getColumnModel().getColumn(3).setPreferredWidth(40);
            tab3.getColumnModel().getColumn(3).setMaxWidth(80);
            tab3.getColumnModel().getColumn(7).setPreferredWidth(40);
            tab3.getColumnModel().getColumn(7).setMaxWidth(60);
        }

        pan4.add(scr3, java.awt.BorderLayout.CENTER);

        tabb1.addTab("   Фурнитура   ", pan4);

        pan5.setLayout(new java.awt.BorderLayout());

        scr4.setBorder(null);

        tab4.setFont(frames.UGui.getFont(0,0));
        tab4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Параметр системы", "Значение по умолчанию", "Закреплено", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tab4.setFillsViewportHeight(true);
        tab4.setName("tab4"); // NOI18N
        tab4.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });
        scr4.setViewportView(tab4);
        if (tab4.getColumnModel().getColumnCount() > 0) {
            tab4.getColumnModel().getColumn(2).setPreferredWidth(60);
            tab4.getColumnModel().getColumn(2).setMaxWidth(80);
            tab4.getColumnModel().getColumn(3).setPreferredWidth(40);
            tab4.getColumnModel().getColumn(3).setMaxWidth(60);
        }

        pan5.add(scr4, java.awt.BorderLayout.CENTER);

        tabb1.addTab("   Параметры   ", pan5);

        pan10.setLayout(new java.awt.BorderLayout());

        scr5.setBorder(null);
        scr5.setPreferredSize(new java.awt.Dimension(350, 400));

        tab5.setFont(frames.UGui.getFont(0,0));
        tab5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null}
            },
            new String [] {
                "Наименование", "Рисунок"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab5.setFillsViewportHeight(true);
        tab5.setName("tab5"); // NOI18N
        tab5.setRowHeight(68);
        tab5.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });
        scr5.setViewportView(tab5);
        if (tab5.getColumnModel().getColumnCount() > 0) {
            tab5.getColumnModel().getColumn(0).setPreferredWidth(80);
            tab5.getColumnModel().getColumn(1).setMinWidth(68);
            tab5.getColumnModel().getColumn(1).setPreferredWidth(68);
            tab5.getColumnModel().getColumn(1).setMaxWidth(68);
        }

        pan10.add(scr5, java.awt.BorderLayout.CENTER);

        scr6.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        scr6.setPreferredSize(new java.awt.Dimension(250, 400));

        winTree.setFont(frames.UGui.getFont(0,0));
        winTree.setName("winTree"); // NOI18N
        winTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });
        scr6.setViewportView(winTree);

        pan10.add(scr6, java.awt.BorderLayout.EAST);

        tabb1.addTab("   Модели   ", pan10);

        split1.setBottomComponent(tabb1);
        tabb1.getAccessibleContext().setAccessibleName("<html><font size=\"3\">&nbsp;&nbsp;&nbsp\nОсновные\n&nbsp;&nbsp;&nbsp");

        centr.add(split1, java.awt.BorderLayout.CENTER);

        getContentPane().add(centr, java.awt.BorderLayout.CENTER);

        south.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.setMaximumSize(new java.awt.Dimension(32771, 32771));
        south.setMinimumSize(new java.awt.Dimension(100, 20));
        south.setPreferredSize(new java.awt.Dimension(219, 20));
        south.setLayout(new javax.swing.BoxLayout(south, javax.swing.BoxLayout.LINE_AXIS));

        filler1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.add(filler1);

        lab1.setFont(frames.UGui.getFont(0,0));
        lab1.setText("___");
        south.add(lab1);

        filler2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.add(filler2);

        lab2.setFont(frames.UGui.getFont(0,0));
        lab2.setText("___");
        south.add(lab2);

        filler3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.add(filler3);

        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosed
        UGui.stopCellEditingAndExecSql(getRootPane());
        if (models != null) {
            models.dispose();
        }
    }//GEN-LAST:event_windowClosed

    private void glasdefToSystree(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_glasdefToSystree

        new DicArtikl(this, (record) -> {
            UGui.stopCellEditing(tab2, tab3, tab4, tab5);
            sysNode.rec().set(eSystree.glas, record.getStr(eArtikl.code));
            rsvSystree.load();
        }, false, 5);
    }//GEN-LAST:event_glasdefToSystree

    private void imageviewToSystree(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imageviewToSystree

        new DicEnums(this, (record) -> {
            UGui.stopCellEditing(tab2, tab3, tab4, tab5);
            sysNode.rec().set(eSystree.imgview, record.getInt(0));
            rsvSystree.load();

        }, LayoutProd.values());
    }//GEN-LAST:event_imageviewToSystree

    private void typeToSystree(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeToSystree

        new DicEnums(this, (record) -> {
            UGui.stopCellEditing(tab2, tab3, tab4, tab5);
            sysNode.rec().set(eSystree.types, record.getInt(0));
            rsvSystree.load();
        }, TypeUse.values());
    }//GEN-LAST:event_typeToSystree

    private void btnInsert(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsert
        if (sysNode != null) {
            if (sysTree.getBorder() != null) {
                if (JOptionPane.showConfirmDialog(this, "Вы действительно хотите добавить ветку в систему?", "Подтверждение",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
                    Record record = eSystree.up.newRecord(Query.INS);
                    int id = Connect.genId(eSystree.id);
                    record.setNo(eSystree.id, id);
                    int parent_id = (sysNode.rec().getInt(eSystree.id) == -1) ? id : sysNode.rec().getInt(eSystree.id);
                    record.setNo(eSystree.parent_id, parent_id);
                    record.setNo(eSystree.name, "P" + id + "." + parent_id);
                    record.setNo(eSystree.coef, 1);
                    qSystree.insert(record); //record сохраним в базе
                    record.set(eSystree.up, Query.SEL);
                    qSystree.add(record); //добавим record в список
                    DefMutableTreeNode newNode = new DefMutableTreeNode(record);
                    ((DefaultTreeModel) sysTree.getModel()).insertNodeInto(newNode, sysNode, sysNode.getChildCount()); //добавим node в tree
                    TreeNode[] nodes = ((DefaultTreeModel) sysTree.getModel()).getPathToRoot(newNode);
                    sysTree.scrollPathToVisible(new TreePath(nodes));
                    sysTree.setSelectionPath(new TreePath(nodes));
                }

            } else if (tab2.getBorder() != null) {
                UGui.insertRecordCur(tab2, eSysprof.up, (record) -> {
                    record.set(eSysprof.systree_id, systreeID);
                    int max = qSysprof.stream().mapToInt(rec -> rec.getInt(eSysprof.npp)).max().orElse(0); //.getAsInt();
                    record.set(eSysprof.npp, ++max);
                    int index = UGui.getIndexFind(tab2, eSysprof.id, record.get(eSysprof.id));
                    qSysprof.table(eArtikl.up).add(index, eArtikl.up.newRecord(Query.SEL));
                });

            } else if (tab3.getBorder() != null) {
                UGui.insertRecordCur(tab3, eSysfurn.up, (record) -> {
                    record.set(eSysfurn.systree_id, systreeID);
                    record.setNo(eSysfurn.npp, record.get(1));
                    record.setNo(eSysfurn.replac, 0);
                    record.setNo(eSysfurn.side_open, TypeOpen2.REQ.id);
                    record.setNo(eSysfurn.hand_pos, LayoutHand.MIDL.id);
                    int index = UGui.getIndexFind(tab3, eSysfurn.id, record.get(eSysfurn.id));
                    qSysfurn.table(eFurniture.up).add(index, eFurniture.up.newRecord(Query.SEL));
                });
            } else if (tab4.getBorder() != null) {
                UGui.insertRecordCur(tab4, eSyspar1.up, (record) -> {
                    record.set(eSyspar1.systree_id, systreeID);
                });

            } else if (tab5.getBorder() != null) {
                if (sysNode != null && sysNode.isLeaf()) {
                    if (evt.getSource() instanceof JMenuItem && eProp.typuse.equals("99") == true) {
                        loadLocalScript();
                    } else {
                        ProgressBar.create(Systree.this, new ListenerFrame() {
                            public void actionRequest(Object obj) {
                                models = new Models(listenerModel);
                                models.setVisible(true);
                            }
                        });
                    }
                }
            }
        }
    }//GEN-LAST:event_btnInsert

    private void btnDelete(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete
        if (sysTree.getBorder() != null) {
            if (sysTree.isSelectionEmpty() == false) {
                ImageIcon img = new ImageIcon(this.getClass().getResource("/resource/img24/c014.gif"));

                if (sysNode.getChildCount() != 0) {
                    JOptionPane.showMessageDialog(this, "Нельзя удалить текущий узел т. к. у него есть подчинённые записи", "Предупреждение", JOptionPane.NO_OPTION, img);
                    return;
                }
                for (JTable tab : List.of(tab2, tab3, tab4, tab5)) {
                    if (tab.getRowCount() != 0) {
                        JOptionPane.showMessageDialog(this, "Перед удалением записи, удалите данные в зависимых таблицах", "Предупреждение", JOptionPane.NO_OPTION, img);
                        return;
                    }
                }
                DefMutableTreeNode parentNode = (DefMutableTreeNode) sysNode.getParent();
                if (JOptionPane.showConfirmDialog(this, "Хотите удалить узел <" + sysNode + ">?", "Подтвердите удаление",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null) == 0) {
                    UGui.stopCellEditing(sysTree);
                    if (qSystree.delete(sysNode.rec())) {

                        qSystree.remove(sysNode.rec());
                        ((DefaultTreeModel) sysTree.getModel()).removeNodeFromParent(sysNode);
                        if (parentNode != null) {
                            TreeNode[] nodes = ((DefaultTreeModel) sysTree.getModel()).getPathToRoot(parentNode);
                            sysTree.scrollPathToVisible(new TreePath(nodes));
                            sysTree.setSelectionPath(new TreePath(nodes));
                        }
                    }
                }
            }
        } else if (tab2.getBorder() != null) {
            if (UGui.isDeleteRecord(tab2, this) == 0) {
                UGui.deleteRecord(tab2);
            }
        } else if (tab3.getBorder() != null) {
            if (UGui.isDeleteRecord(tab3, this) == 0) {
                UGui.deleteRecord(tab3);
            }
        } else if (tab4.getBorder() != null) {
            if (UGui.isDeleteRecord(tab4, this) == 0) {
                UGui.deleteRecord(tab4);
            }
        } else if (tab5.getBorder() != null) {
            if (UGui.isDeleteRecord(tab5, this) == 0) {
                UGui.deleteRecord(tab5);
            }
        }
    }//GEN-LAST:event_btnDelete

    private void findFromArtikl(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findFromArtikl
        if (tab2.getBorder() != null) {
            Record record = qSysprof.get(UGui.getIndexRec(tab2));
            Record record2 = eArtikl.find(record.getInt(eSysprof.artikl_id), false);
            ProgressBar.create(this, new ListenerFrame() {
                public void actionRequest(Object obj) {
                    App.Artikles.createFrame(Systree.this, record2);
                }
            });
        }
    }//GEN-LAST:event_findFromArtikl

    private void btnReport(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReport
        changeAndRedraw();
//            Object o1 = winTree.getLastSelectedPathComponent();
//            System.out.println(o1);        
    }//GEN-LAST:event_btnReport

    private void btnClose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose
        this.dispose();
    }//GEN-LAST:event_btnClose

    private void btnTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTestActionPerformed
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(JsonParser.parseString(wincalc().gson.toJson())));
    }//GEN-LAST:event_btnTestActionPerformed

    private void btnMove(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMove
        JButton btn = (JButton) evt.getSource();
        if (sysTree.getBorder() != null) {
            int index = 0;
            DefMutableTreeNode node = (DefMutableTreeNode) sysTree.getLastSelectedPathComponent();
            selectedPath = node.getPath();
            int ID2 = ((DefMutableTreeNode) node).rec().getInt(eSystree.id);
            for (index = 0; index < qSystree.size(); index++) {
                int ID1 = qSystree.getAs(index, eSystree.id);
                if (ID1 == ID2) {
                    break;
                }
            }
            int index2 = index;
            if (btn == btnMoveD) {
                Collections.swap(qSystree, index, ++index2);

            } else if (btn == btnMoveU) {
                Collections.swap(qSystree, index, --index2);
            }
            IntStream.range(0, qSystree.size()).forEach(i -> qSystree.set(i + 1, i, eSysprof.npp));
            loadingTree1();
            sysTree.setSelectionPath(new TreePath(selectedPath));

        } else {
            int index = UGui.getIndexRec(UGui.tableFromBorder(tab2, tab3, tab5));
            if (index != -1) {
                int index2 = index;
                if (tab2.getBorder() != null) {
                    if (btn == btnMoveD && tab2.getSelectedRow() < tab2.getRowCount() - 1) {
                        Collections.swap(qSysprof, index, ++index2);
                        Collections.swap(qSysprof.table(eArtikl.up), index, index2);

                    } else if (btn == btnMoveU && tab2.getSelectedRow() > 0) {
                        Collections.swap(qSysprof, index, --index2);
                        Collections.swap(qSysprof.table(eArtikl.up), index, index2);
                    }
                    IntStream.range(0, qSysprof.size()).forEach(i -> qSysprof.set(i + 1, i, eSysprof.npp));
                    ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
                    UGui.setSelectedIndex(tab2, index2);

                } else if (tab3.getBorder() != null) {
                    if (btn == btnMoveD && tab3.getSelectedRow() < tab3.getRowCount() - 1) {
                        Collections.swap(qSysfurn, index, ++index2);
                        Collections.swap(qSysfurn.table(eFurniture.up), index, index2);

                    } else if (btn == btnMoveU && tab3.getSelectedRow() > 0) {
                        Collections.swap(qSysfurn, index, --index2);
                        Collections.swap(qSysfurn.table(eFurniture.up), index, index2);
                    }
                    IntStream.range(0, qSysfurn.size()).forEach(i -> qSysfurn.set(i + 1, i, eSysfurn.npp));
                    ((DefaultTableModel) tab3.getModel()).fireTableDataChanged();
                    UGui.setSelectedIndex(tab3, index2);

                } else if (tab5.getBorder() != null) {
                    if (btn == btnMoveD && tab5.getSelectedRow() < tab5.getRowCount() - 1) {
                        Collections.swap(qSysprod, index, ++index2);

                    } else if (btn == btnMoveU && tab5.getSelectedRow() > 0) {
                        Collections.swap(qSysprod, index, --index2);
                    }
                    IntStream.range(0, qSysprod.size()).forEach(i -> qSysprod.set(i + 1, i, eSysprod.npp));
                    ((DefaultTableModel) tab5.getModel()).fireTableDataChanged();
                    UGui.setSelectedIndex(tab5, index2);
                }
            }
        }
    }//GEN-LAST:event_btnMove

    private void btnFind2(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFind2
        if (tab2.getBorder() != null) {
            JOptionPane.showMessageDialog(Systree.this, "В разработке...");
//            Record sysprofRec = qSysprof.get(UGui.getIndexRec(tab2));
//            ProgressBar.create(this, new ListenerFrame() {
//                public void actionRequest(Object obj) {
//                    App.Element.createFrame(Systree.this, sysprofRec.get(eSysprof.artikl_id));
//                }
//            });
        } else if (tab3.getBorder() != null) {
            Record sysfurnRec = qSysfurn.get(UGui.getIndexRec(tab3));
            int furnitureID = sysfurnRec.getInt(eSysfurn.furniture_id);
            ProgressBar.create(this, new ListenerFrame() {
                public void actionRequest(Object obj) {
                    App.Furniture.createFrame(Systree.this, -1 * furnitureID);
                }
            });
        }
    }//GEN-LAST:event_btnFind2

    private void ppmActionItems(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppmActionItems
        if (evt.getSource() == mInsert) {
            btnInsert(new java.awt.event.ActionEvent(mInsert, -1, ""));
        } else if (evt.getSource() == mDelit) {
            btnDelete(new java.awt.event.ActionEvent(mDelit, -1, ""));
        }
    }//GEN-LAST:event_ppmActionItems

    private void tabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabMouseClicked
        if (evt.getSource() instanceof JTable) {
            JTable table = (JTable) evt.getSource();
            UGui.updateBorderAndSql(table, List.of(tab2, tab3, tab4, tab5, tab7));
            List.of(btnFind1, btnFind2, btnMoveU, btnMoveD).forEach(btn -> btn.setEnabled(false));
            List.of(sysTree, sysTree).forEach(tree -> tree.setBorder(null));
            if (sysTree.isEditing()) {
                sysTree.getCellEditor().stopCellEditing();
            }
            if (winTree.isEditing()) {
                winTree.getCellEditor().stopCellEditing();
            }
            if (evt.getButton() == MouseEvent.BUTTON3) {
                ppmCrud.show(table, evt.getX(), evt.getY());
            }
            if ("tab2".equals(table.getName())) {
                List.of(btnMoveU, btnMoveD).forEach(btn -> btn.setEnabled(true));
                btnFind1.setEnabled(true);

            } else if ("tab3".equals(table.getName())) {
                List.of(btnFind2, btnMoveU, btnMoveD).forEach(btn -> btn.setEnabled(true));

            } else if ("tab4".equals(table.getName())) {
                //

            } else if ("tab5".equals(table.getName())) {
                List.of(btnMoveU, btnMoveD).forEach(btn -> btn.setEnabled(true));
            }
        } else if (evt.getSource() instanceof JTree) {
            JTree tree = (JTree) evt.getSource();
            UGui.stopCellEditing(tab2, tab3, tab4, tab5, tab7);
            List.of(tab2, tab3, tab4, tab5, tab7).forEach(tab -> tab.setBorder(null));
            if ("sysTree".equals(tree.getName())) {
                sysTree.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255)));
            } else if ("winTree".equals(tree.getName())) {
                winTree.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255)));
            }
        }
    }//GEN-LAST:event_tabMouseClicked

    private void btnTreebtnMove(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTreebtnMove
        if (scr1.isVisible() == true) {
            scr1.setVisible(false);
        } else {
            scr1.setVisible(true);
        }
        this.pack();
    }//GEN-LAST:event_btnTreebtnMove

    private void removeImpostAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeImpostAction
        Com5t owner = ((DefMutableTreeNode) winTree.getLastSelectedPathComponent()).com5t().owner;
        owner.gson.childs = owner.gson.childs.stream().filter(e -> e.type == enums.Type.BOX_SIDE).collect(toList());
        owner.gson.addElem(new GsonElem(enums.Type.GLASS));
        changeAndRedraw();
    }//GEN-LAST:event_removeImpostAction

    private void addStvorkaAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStvorkaAction
        try {
            GsonElem gsonGlass = ((DefMutableTreeNode) winTree.getLastSelectedPathComponent()).com5t().gson;
            GsonElem gsonStvorka = new GsonElem(enums.Type.STVORKA);
            gsonStvorka.addArea(new GsonElem(enums.Type.GLASS));
            gsonStvorka.owner = gsonGlass.owner;

            if (gsonGlass.owner instanceof GsonRoot) {
                for (int i = 0; i < gsonGlass.owner.childs.size(); ++i) {
                    if (gsonGlass.owner.childs.get(i).id == gsonGlass.id) {
                        gsonGlass.owner.childs.set(i, gsonStvorka);
                    }
                }
            } else {
                for (int i = 0; i < gsonGlass.owner.owner.childs.size(); ++i) {
                    if (gsonGlass.owner.owner.childs.get(i).id == gsonGlass.owner.id) {
                        gsonGlass.owner.owner.childs.set(i, gsonStvorka);
                    }
                }
            }
            changeAndRedraw();

        } catch (Exception e) {
            System.err.println("Ошибка: Systree.addStvorkaAction()");
        }
    }//GEN-LAST:event_addStvorkaAction

    private void removeStvorkaAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeStvorkaAction
        Com5t stv = ((DefMutableTreeNode) winTree.getLastSelectedPathComponent()).com5t();
        for (int i = 0; i < stv.owner.gson.childs.size(); ++i) {
            if (stv.owner.gson.childs.get(i).id == stv.id) {
                if (stv.owner.gson instanceof GsonRoot) { //первый уровень

                    if (wincalc().listElem.stream().anyMatch(e -> e.type == enums.Type.IMPOST)) {
                        GsonElem glass = new GsonElem(enums.Type.AREA).addElem(new GsonElem(enums.Type.GLASS));
                        stv.owner.gson.childs.set(i, glass);
                    } else {
                        GsonElem glass = new GsonElem(enums.Type.GLASS);
                        stv.owner.gson.childs.set(i, glass);
                    }
                } else { //второй, третий... уровни
                    GsonElem glass = new GsonElem(enums.Type.AREA).addElem(new GsonElem(enums.Type.GLASS));
                    stv.owner.gson.childs.set(i, glass);
                }
            }
        }
        changeAndRedraw();
    }//GEN-LAST:event_removeStvorkaAction

    private void addImpostHorAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addImpostHorAction
        Com5t glass = ((DefMutableTreeNode) winTree.getLastSelectedPathComponent()).com5t();
        for (int i = 0; i < glass.owner.gson.childs.size(); ++i) {
            if (glass.owner.gson.childs.get(i).id == glass.id) {
                Envelope env = glass.owner.area.getEnvelopeInternal();
                double x1 = env.getMinX(), x2 = env.getMaxX(), y = env.getMinY() + env.getHeight() / 2;
                glass.owner.gson.childs.set(i, new GsonElem(enums.Type.AREA).addElem(new GsonElem(enums.Type.GLASS)));
                glass.owner.gson.childs.add(i, new GsonElem(enums.Type.IMPOST, x1, y, x2, y));
                glass.owner.gson.childs.add(i, new GsonElem(enums.Type.AREA).addElem(new GsonElem(enums.Type.GLASS)));
            }
        }
        changeAndRedraw();
    }//GEN-LAST:event_addImpostHorAction

    private void addImpostVerAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addImpostVerAction
        Com5t glass = ((DefMutableTreeNode) winTree.getLastSelectedPathComponent()).com5t();
        for (int i = 0; i < glass.owner.gson.childs.size(); ++i) {
            if (glass.owner.gson.childs.get(i).id == glass.id) {
                Envelope env = glass.owner.area.getEnvelopeInternal();
                double y1 = env.getMinY(), y2 = env.getMaxY(), x = env.getMinX() + env.getWidth() / 2;
                glass.owner.gson.childs.set(i, new GsonElem(enums.Type.AREA).addElem(new GsonElem(enums.Type.GLASS)));
                glass.owner.gson.childs.add(i, new GsonElem(enums.Type.IMPOST, x, y1, x, y2));
                glass.owner.gson.childs.add(i, new GsonElem(enums.Type.AREA).addElem(new GsonElem(enums.Type.GLASS)));
            }
        }
        changeAndRedraw();
    }//GEN-LAST:event_addImpostVerAction

    private void removeMosquitAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeMosquitAction
        Com5t mosq = ((DefMutableTreeNode) winTree.getLastSelectedPathComponent()).com5t();
        for (int i = 0; i < mosq.owner.gson.childs.size(); ++i) {
            if (mosq.owner.gson.childs.get(i).id == mosq.id) {
                mosq.owner.gson.childs.remove(i);
            }
        }
        changeAndRedraw();
    }//GEN-LAST:event_removeMosquitAction

    private void elementsView(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_elementsView
        ProgressBar.create(Systree.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                Com5t com5t = ((DefMutableTreeNode) winTree.getLastSelectedPathComponent()).com5t();
                int sysprodID = qSysprod.getAs(UGui.getIndexRec(tab5), eSysprod.id);
                App.Element.createFrame(Systree.this, sysprodID, com5t);
            }
        });
    }//GEN-LAST:event_elementsView

// <editor-fold defaultstate="collapsed" desc="Generated Code"> 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu addImpost;
    private javax.swing.JMenuItem addImpostHor;
    private javax.swing.JMenuItem addImpostVer;
    private javax.swing.JMenuItem addStvorka;
    private javax.swing.JButton btn11;
    private javax.swing.JButton btn4;
    private javax.swing.JButton btn7;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnFind1;
    private javax.swing.JButton btnFind2;
    private javax.swing.JButton btnIns;
    private javax.swing.JButton btnMoveD;
    private javax.swing.JButton btnMoveU;
    private javax.swing.JButton btnReport1;
    private javax.swing.JButton btnTest;
    private javax.swing.JButton btnTree;
    private javax.swing.JPanel centr;
    private javax.swing.JMenuItem elements;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lab1;
    private javax.swing.JLabel lab13;
    private javax.swing.JLabel lab14;
    private javax.swing.JLabel lab15;
    private javax.swing.JLabel lab16;
    private javax.swing.JLabel lab17;
    private javax.swing.JLabel lab19;
    private javax.swing.JLabel lab2;
    private javax.swing.JLabel lab24;
    private javax.swing.JLabel lab47;
    private javax.swing.JLabel lab69;
    private javax.swing.JMenuItem mDelit;
    private javax.swing.JMenuItem mInsert;
    private javax.swing.JPanel pan10;
    private javax.swing.JPanel pan11;
    private javax.swing.JPanel pan12;
    private javax.swing.JPanel pan2;
    private javax.swing.JPanel pan3;
    private javax.swing.JPanel pan4;
    private javax.swing.JPanel pan5;
    private javax.swing.JPanel pan6;
    private javax.swing.JPanel pan7;
    private javax.swing.JPanel panDesign;
    private javax.swing.JPanel panT;
    private javax.swing.JPopupMenu ppmCrud;
    private javax.swing.JPopupMenu ppmTree;
    private javax.swing.JMenuItem removeImpost;
    private javax.swing.JMenuItem removeMosquit;
    private javax.swing.JMenuItem removeStvorka;
    private javax.swing.JScrollPane scr1;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JScrollPane scr3;
    private javax.swing.JScrollPane scr4;
    private javax.swing.JScrollPane scr5;
    private javax.swing.JScrollPane scr6;
    private javax.swing.JScrollPane scr7;
    private javax.swing.JPanel south;
    private javax.swing.JSplitPane split1;
    private javax.swing.JTree sysTree;
    private javax.swing.JTable tab2;
    private javax.swing.JTable tab3;
    private javax.swing.JTable tab4;
    private javax.swing.JTable tab5;
    private javax.swing.JTable tab7;
    private javax.swing.JTabbedPane tabb1;
    private javax.swing.JPanel tool;
    private javax.swing.JTextField txt1;
    private javax.swing.JTextField txt11;
    private javax.swing.JTextField txt12;
    private javax.swing.JTextField txt2;
    private javax.swing.JTextField txt3;
    private javax.swing.JTextField txt35;
    private javax.swing.JTextField txt4;
    private javax.swing.JTextField txt5;
    private javax.swing.JTextField txt7;
    private javax.swing.JTree winTree;
    // End of variables declaration//GEN-END:variables
// </editor-fold> 

    public final void initElements() {

        btnTest.setVisible(eProp.typuse.equals("99"));
        App.loadLocationWin(this, btnClose, (e) -> {
            App.saveLocationWin(this, btnClose, split1);
        }, split1);

        panDesign.add(scene, java.awt.BorderLayout.CENTER);
        new UColor();

        List.of(btnFind1, btnFind2, btnMoveU, btnMoveD).forEach(btn -> btn.setEnabled(false));
        //UGui.setDocumentFilter(3, txt17, txt22, txt24, txt26);
        List.of(btnIns, btnDel).forEach(b -> b.addActionListener(l -> UGui.stopCellEditing(tab2, tab3, tab4, tab5)));
        DefaultTreeCellRenderer rnd = (DefaultTreeCellRenderer) sysTree.getCellRenderer();
        rnd.setLeafIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b038.gif")));
        rnd.setOpenIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b007.gif")));
        rnd.setClosedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b006.gif")));
        DefaultTreeCellRenderer rnd2 = (DefaultTreeCellRenderer) winTree.getCellRenderer();
        rnd2.setLeafIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b038.gif")));
        rnd2.setOpenIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b007.gif")));
        rnd2.setClosedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b006.gif")));
        sysTree.setBorder(null);
        sysTree.getSelectionModel().addTreeSelectionListener(tse -> selectionTree1());
        winTree.getSelectionModel().addTreeSelectionListener(tse -> selectionTree2());
        tab4.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    UGui.setSelectedIndex(tab7, UGui.getIndexRec(tab4));
                }
            }
        });
        tab5.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    selectionTab5();
                }
            }
        });
        tab7.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    UGui.setSelectedIndex(tab4, UGui.getIndexRec(tab7));
                }
            }
        });
        DefaultTreeModel model = (DefaultTreeModel) winTree.getModel();
        ((DefaultMutableTreeNode) model.getRoot()).removeAllChildren();
        model.reload();

        TableFieldFilter filterTable = new TableFieldFilter(0, tab2, tab5, tab3, tab4, tab5, tab7);
        south.add(filterTable, 0);
        filterTable.getTxt().grabFocus();

        listenerWincalc = () -> {
            return wincalc();
        };

        listenerCangeAndRedraw = () -> {
            changeAndRedraw();
        };
    }

    //Грузим тестовые скрипты
    private void loadLocalScript() {
        try {
            Object prj = JOptionPane.showInputDialog(Systree.this, "Номер проекта", "Проект", JOptionPane.QUESTION_MESSAGE);
            if (prj != null) {
                Record record = eSysprod.up.newRecord(Query.INS);
                record.set(eSysprod.id, Connect.genId(eSysprod.up));
                record.set(eSysprod.npp, record.get(eSysprod.id));
                String json = GsonScript.scriptPath(Integer.valueOf(prj.toString()));
                GsonRoot gsonRoot = new Gson().fromJson(json, GsonRoot.class);
                record.set(eSysprod.name, "TEST-Kod:" + prj + gsonRoot.name);
                record.set(eSysprod.script, json);
                record.set(eSysprod.systree_id, systreeID);
                eSysprod.data().add(record);

                loadingTab5();
                UGui.setSelectedIndex(tab5, qSysprod.size() - 1);
                UGui.scrollRectToIndex(qSysprod.size() - 1, tab5);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.loadLocalScript()");
        }
    }
}
