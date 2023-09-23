package frames;

import frames.swing.ProgressBar;
import frames.swing.FrameToFile;
import builder.IArea5e;
import builder.ICom5t;
import common.eProp;
import dataset.Conn;
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
import enums.LayoutProduct;
import enums.LayoutHandle;
import enums.TypeArtikl;
import enums.UseSide;
import enums.TypeOpen2;
import enums.UseArtiklTo;
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
import frames.swing.DefCellRendererBool;
import frames.swing.TableFieldFormat;
import frames.swing.DefTableModel;
import builder.Wincalc;
import builder.making.Furniture;
import builder.model1.ElemJoining;
import builder.script.GsonElem;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import common.UCom;
import domain.eArtdet;
import domain.eColor;
import enums.Layout;
import enums.PKjson;
import enums.TypeOpen1;
import frames.dialog.DicColor;
import frames.dialog.DicHandl;
import frames.dialog.DicSysprof;
import frames.swing.draw.Canvas;
import frames.swing.DefMutableTreeNode;
import java.awt.CardLayout;
import java.awt.Component;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import startup.App;
import common.listener.ListenerRecord;
import common.listener.ListenerFrame;
import common.eProfile;
import domain.eJoining;
import builder.making.Joining;
import builder.making.UColor;
import builder.IElem5e;
import builder.IStvorka;
import builder.making.Cal5e;
import builder.model1.ElemMosquit;
import builder.script.GsonRoot;
import builder.script.GsonScript;
import com.google.gson.Gson;
import common.LinkedCom;
import domain.eJoinvar;
import enums.TypeJoin;
import frames.swing.draw.Scene;
import common.listener.ListenerReload;
import domain.eElement;
import domain.eGroups;
import enums.TypeGrup;
import frames.dialog.ParDefVal;
import frames.dialog.ParDefault;
import frames.swing.TableFieldFilter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Systree extends javax.swing.JFrame implements ListenerReload {

    private ImageIcon icon = new ImageIcon(getClass().getResource("/resource/img16/b031.gif"));
    private ListenerRecord listenerArtikl, listenerModel, listenerFurn,
            listenerParam1, listenerParam2, listenerArt211, listenerArt212;
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
    private TableFieldFormat rsvSystree;
    private java.awt.Frame models = null;
    private DefMutableTreeNode sysNode = null;
    private DefMutableTreeNode winNode = null;
    private TreeNode[] selectedPath = null;

    public Systree() {
        initComponents();
        scene = new Scene(canvas, spinner, this);
        initElements();
        loadingData();
        loadingModel();
        listenerAdd();
        listenerSet();
        tabb1.setSelectedIndex(4);
    }

    public Systree(int nuni, int mode) {
        initComponents();
        scene = new Scene(canvas, spinner, this);
        this.systreeID = nuni;
        this.writeNuni = false;
        initElements();
        loadingData();
        loadingModel();
        listenerAdd();
        listenerSet();
        tabb1.setSelectedIndex(mode);
    }

    public final void loadingData() {
        //Получим сохранённую systreeID при выходе из программы
        Record sysprodRec = null; //при открытии указывает на конструкцию
        if (this.systreeID == -1 && "-1".equals(eProp.sysprodID.read()) != true) {
            sysprodRec = eSysprod.find(Integer.valueOf(eProp.sysprodID.read()));
        }
        if (sysprodRec != null) {
            this.systreeID = sysprodRec.getInt(eSysprod.systree_id);

        } else if (eSysprod.query().isEmpty() == false) {
            if (this.systreeID == -1) {
                sysprodRec = eSysprod.query().get(0);
                this.systreeID = sysprodRec.getInt(eSysprod.systree_id);
            }
        }
        qGroups.select(eGroups.up, "where", eGroups.grup, "=", TypeGrup.PARAM_USER.id, "or", eGroups.grup, "=", TypeGrup.COLOR_MAP.id);
        qSystree.select(eSystree.up, "order by id");
        qParams.select(eParams.up);
        qArtikl.select(eArtikl.up, "where", eArtikl.level1, "= 2 and", eArtikl.level2, "in (11,12)");

        loadingTree1();
    }

    public final void loadingModel() {
        ((DefaultTreeCellEditor) sysTree.getCellEditor()).addCellEditorListener(new CellEditorListener() {

            public void editingStopped(ChangeEvent e) {
                String str = ((DefaultTreeCellEditor) sysTree.getCellEditor()).getCellEditorValue().toString();
                sysNode.rec().set(eSystree.name, str);
                sysNode.setUserObject(str);
                setText(txt8, str);
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
                    UseArtiklTo en = UseArtiklTo.get(Integer.valueOf(val.toString()));
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
                        return List.of(TypeOpen2.values()).stream().filter(el -> el.id == id).findFirst().orElse(TypeOpen2.QUE).name;
                    } else if (field == eSysfurn.hand_pos) {
                        int id = Integer.valueOf(val.toString());
                        return List.of(LayoutHandle.values()).stream().filter(el -> el.id == id).findFirst().orElse(LayoutHandle.MIDL).name;
                    } else if (field == eSysfurn.artikl_id1) {
                        int id = Integer.valueOf(val.toString());
                        return qArtikl.stream().filter(rec -> rec.getInt(eArtikl.id) == id).findFirst().orElse(eArtikl.up.newRecord()).get(eArtikl.code);
                    } else if (field == eSysfurn.artikl_id2) {
                        int id = Integer.valueOf(val.toString());
                        return qArtikl.stream().filter(rec -> rec.getInt(eArtikl.id) == id).findFirst().orElse(eArtikl.up.newRecord()).get(eArtikl.code);
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
                    return qGroups.find(val, eGroups.id).getDev(eGroups.name, val);
                }
                return val;
            }
        };
        new DefTableModel(tab5, qSysprod, eSysprod.name, eSysprod.id);
        new DefTableModel(tab7, qSyspar1b, eSyspar1.groups_id, eSyspar1.text) {
            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (val != null && field == eSyspar1.groups_id) {
                    return qGroups.find(val, eGroups.id).getDev(eGroups.name, val);
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

            public Set<JTextField> set = new HashSet();

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
                LayoutProduct layoutProduct = Stream.of(LayoutProduct.values()).filter(en -> en.numb() == imgviewID).findFirst().orElse(LayoutProduct.P1);
                setTxt(txt7, typeUse.name);
                setTxt(txt11, layoutProduct.name);
            }

            @Override
            public void clear() {
                super.clear();
                set.forEach(s -> s.setText(null));
            }
        };
        rsvSystree.add(eSystree.name, txt8);
        rsvSystree.add(eSystree.glas, txt1);
        rsvSystree.add(eSystree.depth, txt2);
        rsvSystree.add(eSystree.col1, txt3);
        rsvSystree.add(eSystree.col2, txt4);
        rsvSystree.add(eSystree.col3, txt5);
        rsvSystree.add(eSystree.note, txt12);
        rsvSystree.add(eSystree.coef, txt35);

        canvas.setVisible(true);
        if (selectedPath != null) {
            sysTree.setSelectionPath(new TreePath(selectedPath));
        } else {
            sysTree.setSelectionRow(0);
        }
    }

    public void loadingTree1() {
        Record recordRoot = eSystree.up.newRecord(Query.SEL);
        recordRoot.set(eSystree.id, -1);
        recordRoot.set(eSystree.parent_id, -1);
        recordRoot.set(eSystree.name, "Дерево системы профилей");
        DefMutableTreeNode rootTree = new DefMutableTreeNode(recordRoot);
        ArrayList<DefMutableTreeNode> nodeList = new ArrayList();

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
            DefMutableTreeNode root = UGui.loadWinTree(winc);
            winTree.setModel(new DefaultTreeModel(root));

        } catch (Exception e) {
            System.err.println("Ошибка: Systree.loadingWinTree() " + e);
        }
    }

    public void loadingTab5() {
        qSysprod.select(eSysprod.up, "where", eSysprod.systree_id, "=", systreeID, "order by", eSysprod.npp, ",", eSysprod.id);
        DefaultTableModel dm = (DefaultTableModel) tab5.getModel();
        dm.getDataVector().removeAllElements();
        for (Record record : qSysprod.table(eSysprod.up)) {
            try {
                String script = record.getStr(eSysprod.script);
                Wincalc iwin2 = new Wincalc(script);
                Cal5e joining = new Joining(iwin2, true);//заполним соединения из конструктива
                joining.calc();
                iwin2.imageIcon = Canvas.createIcon(iwin2, 68);
                record.add(iwin2);

            } catch (Exception e) {
                System.err.println("Ошибка:Systree.loadingTab5() " + e);
            }
        }
        ((DefaultTableModel) tab5.getModel()).fireTableDataChanged();
    }

    public void selectionTree1() {
        UGui.stopCellEditing(tab2, tab3, tab4, tab5);
        List.of(tab2, tab3, tab4).forEach(table -> ((DefTableModel) table.getModel()).getQuery().execsql());

        sysNode = (DefMutableTreeNode) sysTree.getLastSelectedPathComponent();
        if (sysNode != null) {
            systreeID = sysNode.rec().getInt(eSystree.id);
            rsvSystree.load();
            qSysprof.select(eSysprof.up, "left join", eArtikl.up, "on", eArtikl.id, "=",
                    eSysprof.artikl_id, "where", eSysprof.systree_id, "=", sysNode.rec().getInt(eSystree.id), "order by", eSysprof.npp);
            qSysfurn.select(eSysfurn.up, "left join", eFurniture.up, "on", eFurniture.id, "=",
                    eSysfurn.furniture_id, "where", eSysfurn.systree_id, "=", sysNode.rec().getInt(eSystree.id), "order by", eSysfurn.npp);
            qSyspar1a.select(eSyspar1.up, "where", eSyspar1.systree_id, "=", sysNode.rec().getInt(eSystree.id));
            lab1.setText("ID = " + systreeID);
            lab2.setText("ID = -1");
            Collections.sort(qSyspar1a, (o1, o2) -> qGroups.find(o1.getInt(eSyspar1.groups_id), eGroups.id).getStr(eGroups.name)
                    .compareTo(qGroups.find(o2.getInt(eSyspar1.groups_id), eGroups.id).getStr(eGroups.name)));

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
                int sysprodID = Integer.valueOf(eProp.sysprodID.read());
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
    }

    public void selectionTree2() {
        try {
            Object selNode = winTree.getLastSelectedPathComponent();
            if (selNode instanceof DefMutableTreeNode) {
                winNode = (DefMutableTreeNode) winTree.getLastSelectedPathComponent();
                Wincalc winc = wincalc();

                //Конструкции
                if (winNode.com5t().type() == enums.Type.RECTANGL || winNode.com5t().type() == enums.Type.DOOR || winNode.com5t().type() == enums.Type.TRAPEZE || winNode.com5t().type() == enums.Type.ARCH) {
                    ((CardLayout) pan7.getLayout()).show(pan7, "card12");
                    ((TitledBorder) pan12.getBorder()).setTitle(winc.rootArea.type().name);
                    setText(txt9, eColor.find(winc.colorID1).getStr(eColor.name));
                    setText(txt13, eColor.find(winc.colorID2).getStr(eColor.name));
                    setText(txt14, eColor.find(winc.colorID3).getStr(eColor.name));
                    setText(txt17, UCom.format(winc.rootGson.width(), 1));
                    setText(txt22, UCom.format(winc.rootGson.height1(), 1));
                    setText(txt23, UCom.format(winc.rootGson.height2(), 1));
                    txt23.setEditable(List.of(enums.Type.ARCH, enums.Type.TRIANGL, enums.Type.TRAPEZE).contains(winNode.com5t().type()));

                    //Параметры
                } else if (winNode.com5t().type() == enums.Type.PARAM) {
                    ((CardLayout) pan7.getLayout()).show(pan7, "card11");
                    qSyspar1b.clear();
                    winc.mapPardef().forEach((pk, syspar1Rec) -> qSyspar1b.add(syspar1Rec));
                    Collections.sort(qSyspar1b, (o1, o2) -> qGroups.find(o1.getInt(eSyspar1.groups_id), eGroups.id).getStr(eGroups.name)
                            .compareTo(qGroups.find(o2.getInt(eSyspar1.groups_id), eGroups.id).getStr(eGroups.name)));
                    ((DefTableModel) tab7.getModel()).fireTableDataChanged();

                    //Рама, импост...
                } else if (winNode.com5t().type() == enums.Type.FRAME_SIDE
                        || winNode.com5t().type() == enums.Type.STVORKA_SIDE
                        || winNode.com5t().type() == enums.Type.IMPOST
                        || winNode.com5t().type() == enums.Type.STOIKA
                        || winNode.com5t().type() == enums.Type.SHTULP) {
                    ((CardLayout) pan7.getLayout()).show(pan7, "card13");
                    ((TitledBorder) pan13.getBorder()).setTitle(winNode.toString());
                    setText(txt32, winNode.com5t().artiklRec().getStr(eArtikl.code));
                    setText(txt33, winNode.com5t().artiklRec().getStr(eArtikl.name));
                    setText(txt27, eColor.find(winNode.com5t().colorID1()).getStr(eColor.name));
                    setText(txt28, eColor.find(winNode.com5t().colorID2()).getStr(eColor.name));
                    setText(txt29, eColor.find(winNode.com5t().colorID3()).getStr(eColor.name));

                    //Стеклопакет
                } else if (winNode.com5t().type() == enums.Type.GLASS) {
                    ((CardLayout) pan7.getLayout()).show(pan7, "card15");
                    Record artiklRec = winNode.com5t().artiklRec();
                    setText(txt19, artiklRec.getStr(eArtikl.code));
                    setText(txt18, artiklRec.getStr(eArtikl.name));
                    Record colorRec = eColor.find(winNode.com5t().colorID1());
                    setText(txt34, colorRec.getStr(eColor.name));
                    Record rasclRec = ((IElem5e) winNode.com5t()).rasclRec();
                    setText(txt49, rasclRec.getStr(eArtikl.code));
                    setText(txt50, rasclRec.getStr(eArtikl.name));
                    Record colorRascl = eColor.find(((IElem5e) winNode.com5t()).rasclColor());
                    setText(txt51, colorRascl.getStr(eColor.name));
                    spinHor.setValue(((IElem5e) winNode.com5t()).rasclNumber(0));
                    spinVert.setValue(((IElem5e) winNode.com5t()).rasclNumber(1));

                    //Створка
                } else if (winNode.com5t().type() == enums.Type.STVORKA) {
                    new Furniture(wincalc(), true); //найдём ручку створки
                    ((CardLayout) pan7.getLayout()).show(pan7, "card16");
                    IStvorka stv = (IStvorka) winNode.com5t();
                    IArea5e sta = (IArea5e) winNode.com5t();
                    int id = stv.sysfurnRec().getInt(eSysfurn.furniture_id);
                    IArea5e own = winNode.com5t().owner();
                    setText(txt24, own.width());
                    setText(txt26, own.height());
                    setText(txt20, eFurniture.find(id).getStr(eFurniture.name));
                    setIcon(btn10, stv.paramCheck()[0]);
                    setText(txt30, stv.typeOpen().name2);
                    setIcon(btn12, stv.paramCheck()[1]);
                    setText(txt16, stv.handleLayout().name);
                    txt31.setEditable((stv.handleLayout() == LayoutHandle.VARIAT) ? true : false);
                    setText(txt31, (stv.handleLayout() == LayoutHandle.VARIAT) ? UCom.format(stv.handleHeight(), 1) : "");
                    setText(txt21, stv.handleRec().getStr(eArtikl.code));
                    setText(txt59, stv.handleRec().getStr(eArtikl.name));
                    setIcon(btn21, stv.paramCheck()[7]);
                    setText(txt25, eColor.find(stv.handleColor()).getStr(eColor.name));
                    setIcon(btn14, stv.paramCheck()[2]);
                    setText(txt45, stv.loopRec().getStr(eArtikl.code));
                    setText(txt57, stv.loopRec().getStr(eArtikl.name));
                    setIcon(btn15, stv.paramCheck()[3]);
                    setText(txt47, eColor.find(stv.loopColor()).getStr(eColor.name));
                    setIcon(btn17, stv.paramCheck()[4]);
                    setText(txt46, stv.lockRec().getStr(eArtikl.code));
                    setText(txt58, stv.lockRec().getStr(eArtikl.name));
                    setIcon(btn23, stv.paramCheck()[5]);
                    setText(txt48, eColor.find(stv.lockColor()).getStr(eColor.name));
                    setIcon(btn24, stv.paramCheck()[6]);
                    //Москитка
                    LinkedCom<ICom5t> mosqList = ((IArea5e) stv).childs().filter(enums.Type.MOSKITKA);
                    if (mosqList.isEmpty() == false) {
                        IElem5e mosq = (IElem5e) mosqList.get(0);
                        setText(txt54, mosq.artiklRec().getStr(eArtikl.code));
                        setText(txt55, mosq.artiklRec().getStr(eArtikl.name));
                        setText(txt60, eColor.find(mosq.colorID1()).getStr(eColor.name));
                        setText(txt56, mosq.sysprofRec().getStr(eElement.name));
                    }

                    //Соединения
                } else if (winNode.com5t().type() == enums.Type.JOINING) {
                    //new Joining(winc(), true); //заполним соединения данными из конструктива
                    ((CardLayout) pan7.getLayout()).show(pan7, "card17");
                    DefMutableTreeNode nodeParent = (DefMutableTreeNode) winNode.getParent();
                    IElem5e elem5e = (IElem5e) nodeParent.com5t();
                    ElemJoining ej1 = winc.listJoin.get(elem5e, 0);
                    ElemJoining ej2 = winc.listJoin.get(elem5e, 1);
                    ElemJoining ej3 = winc.listJoin.get(elem5e, 2);
                    List.of(lab55, lab56, lab57).forEach(it -> it.setIcon(null));
                    if (ej1 != null) {
                        setText(txt36, ej1.joiningRec.getStr(eJoining.name));
                        setText(txt42, ej1.name());
                        setText(txt38, ej1.joinvarRec.getStr(eJoinvar.name));
                        lab55.setIcon(UColor.iconFromTypeJoin2(ej1.type.id));
                    }
                    if (ej2 != null) {
                        setText(txt37, ej2.joiningRec.getStr(eJoining.name));
                        setText(txt43, ej2.name());
                        setText(txt39, ej2.joinvarRec.getStr(eJoinvar.name));
                        lab56.setIcon(UColor.iconFromTypeJoin2(ej2.type.id));
                    }
                    if (ej3 != null && ej3.type == TypeJoin.VAR10) {
                        setText(txt40, ej3.joiningRec.getStr(eJoining.name));
                        setText(txt44, ej3.name());
                        setText(txt41, ej3.joinvarRec.getStr(eJoinvar.name));
                        lab57.setIcon(UColor.iconFromTypeJoin2(ej3.type.id));
                    }
                } else {
                    ((CardLayout) pan7.getLayout()).show(pan7, "card18");
                }
                lab2.setText("ID = " + winNode.com5t().id());
                List.of(pan12, pan13, pan15, pan16).forEach(it -> it.repaint());
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.selectionTree() " + e);
        }
    }

    public void selectionTab5() {
        int index = UGui.getIndexRec(tab5);
        if (index != -1) {
            Record sysprodRec = qSysprod.table(eSysprod.up).get(index);
            if (writeNuni == true) {
                eProp.sysprodID.write(sysprodRec.getStr(eSysprod.id)); //запишем текущий sysprodID в файл
            }
            App.Top.frame.setTitle(eProfile.profile.title + UGui.designTitle());

            Object w = sysprodRec.get(eSysprod.values().length);
            if (w instanceof Wincalc) { //прорисовка окна               
                Wincalc win = (Wincalc) w;
                scene.init(win);
                canvas.draw();
                scene.draw();

                loadingTree2(win);

                winTree.setSelectionInterval(0, 0);
            }
        } else {
            winTree.setModel(new DefaultTreeModel(new DefMutableTreeNode("")));
        }
    }

    public void listenerAdd() {
        UGui.buttonCellEditor(tab2, 1).addActionListener(event -> {
            new DicEnums(this, (record) -> {
                UGui.cellParamEnum(record, tab2, eSysprof.use_type, tab2, tab3, tab4);
            }, UseArtiklTo.values());
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
            DicName frame = new DicName(this, listenerFurn, new Query(eFurniture.values()).select(eFurniture.up, "where", eFurniture.types, "= 0", "order by", eFurniture.name), eFurniture.name);
        });

        UGui.buttonCellEditor(tab3, 2).addActionListener(event -> {
            DicEnums frame = new DicEnums(this, (record) -> {
                UGui.cellParamEnum(record, tab3, eSysfurn.side_open, tab2, tab3, tab4, tab5);
            }, TypeOpen2.values());
        });

        UGui.buttonCellEditor(tab3, 4).addActionListener(event -> {
            DicEnums frame = new DicEnums(this, (record) -> {
                UGui.cellParamEnum(record, tab3, eSysfurn.hand_pos, tab2, tab3, tab4, tab5);
            }, LayoutHandle.values());
        });

        UGui.buttonCellEditor(tab3, 5).addActionListener(event -> {
            int furnityreId = qSysfurn.getAs(UGui.getIndexRec(tab3), eSysfurn.furniture_id);
            new DicArtikl(this, listenerArt211, false, furnityreId, TypeArtikl.X211.id1, TypeArtikl.X211.id2);
        });

        UGui.buttonCellEditor(tab3, 6).addActionListener(event -> {
            int furnityreId = qSysfurn.getAs(UGui.getIndexRec(tab3), eSysfurn.furniture_id);
            new DicArtikl(this, listenerArt212, false, furnityreId, TypeArtikl.X212.id1, TypeArtikl.X212.id2);
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

        listenerModel = (record) -> {
            UGui.stopCellEditing(tab2, tab3, tab4, tab5);

            //Запишем в скрипт ветку из которого будет создаваться окно  
            String script = record.get(2).toString();
            JsonObject je = new GsonBuilder().create().fromJson(script, JsonObject.class);
            je.addProperty("nuni", systreeID);
            String script2 = new GsonBuilder().create().toJson(je);

            //Сохраним скрипт в базе
            Record sysprodRec = eSysprod.up.newRecord(Query.INS);
            sysprodRec.setNo(eSysprod.id, Conn.genId(eSysprod.id));
            sysprodRec.setNo(eSysprod.npp, sysprodRec.get(eSysprod.id));
            sysprodRec.setNo(eSysprod.systree_id, systreeID);
            sysprodRec.setNo(eSysprod.name, record.get(1));
            sysprodRec.setNo(eSysprod.script, script2);
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

    public void updateScript(double selectID) {
        try {
            //Сохраним скрипт в базе
            String script = wincalc().rootGson.toJson();
            Record sysprodRec = qSysprod.get(UGui.getIndexRec(tab5));
            sysprodRec.set(eSysprod.script, script);
            qSysprod.update(sysprodRec);

            //Экземпляр нового скрипта
            Wincalc iwin = new Wincalc(script);
            Cal5e joining = new Joining(iwin, true);//заполним соединения из конструктива
            joining.calc();
            iwin.imageIcon = Canvas.createIcon(iwin, 68);
            sysprodRec.setNo(eSysprod.values().length, iwin);

            //Запомним курсор
            DefMutableTreeNode selectNode = (DefMutableTreeNode) winTree.getLastSelectedPathComponent();
            double id = (selectNode != null) ? selectNode.com5t().id() : -1;

            //Перегрузим winTree
            loadingTree2(iwin);

            //Установим курсор
            UGui.selectionPathWin(id, winTree);

            //Перерисуем конструкцию
            scene.init(iwin);
            canvas.draw();
            scene.draw();

            //Обновим поля форм
            selectionTree2();

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.updateScript() " + e);
        }
    }

    private Wincalc wincalc() {
        int index = UGui.getIndexRec(tab5);
        if (index != -1) {
            Record sysprodRec = qSysprod.table(eSysprod.up).get(index);
            Object v = sysprodRec.get(eSysprod.values().length);
            if (v instanceof Wincalc) {
                return (Wincalc) v;
            }
        }
        return null;
    }

    @Override
    public void reload() {
        try {
            Wincalc win = wincalc();
            int index = UGui.getIndexRec(tab5);
            if (index != -1) {
                String script = win.rootGson.toJson();
                win.build(script);
                win.imageIcon = Canvas.createIcon(win, 68);
                Record sysprodRec = qSysprod.get(index);
                sysprodRec.set(eSysprod.script, script);
                sysprodRec.set(eSysprod.values().length, win);
                canvas.draw();
                scene.lineHoriz.forEach(e -> e.init());
                scene.lineVert.forEach(e -> e.init());
                scene.draw();
                selectionTree2();
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.reload() " + e);
        }
    }

    private void setText(JTextField comp, Object txt) {
        if (txt == null) {
            comp.setText("");
        }
        comp.setText(txt.toString());
        comp.setCaretPosition(0);
    }

    private void setIcon(JButton btn, boolean b) {
        if (b == false) {
            btn.setText("");
            btn.setIcon(icon);
        } else {
            btn.setText("...");
            btn.setIcon(null);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ppmCrud = new javax.swing.JPopupMenu();
        mInsert = new javax.swing.JMenuItem();
        mDelit = new javax.swing.JMenuItem();
        tool = new javax.swing.JPanel();
        btnIns = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        btnRef = new javax.swing.JButton();
        btnReport1 = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnTest = new javax.swing.JButton();
        btnFind1 = new javax.swing.JButton();
        panSspinner = new javax.swing.JPanel();
        spinner = new javax.swing.JSpinner();
        btnMoveU = new javax.swing.JButton();
        btnMoveD = new javax.swing.JButton();
        btnFind2 = new javax.swing.JButton();
        centr = new javax.swing.JPanel();
        scr1 = new javax.swing.JScrollPane();
        sysTree = new javax.swing.JTree();
        pan1 = new javax.swing.JPanel();
        pan2 = new javax.swing.JPanel();
        panDesign = new javax.swing.JPanel();
        pan7 = new javax.swing.JPanel();
        pan11 = new javax.swing.JPanel();
        scr7 = new javax.swing.JScrollPane();
        tab7 = new javax.swing.JTable();
        pan12 = new javax.swing.JPanel();
        pan21 = new javax.swing.JPanel();
        lab27 = new javax.swing.JLabel();
        lab31 = new javax.swing.JLabel();
        lab32 = new javax.swing.JLabel();
        btn9 = new javax.swing.JButton();
        btn13 = new javax.swing.JButton();
        btn2 = new javax.swing.JButton();
        txt9 = new javax.swing.JTextField();
        txt13 = new javax.swing.JTextField();
        txt14 = new javax.swing.JTextField();
        lab35 = new javax.swing.JLabel();
        lab38 = new javax.swing.JLabel();
        lab40 = new javax.swing.JLabel();
        txt17 = new javax.swing.JTextField();
        txt22 = new javax.swing.JTextField();
        txt23 = new javax.swing.JTextField();
        pan13 = new javax.swing.JPanel();
        lab33 = new javax.swing.JLabel();
        lab34 = new javax.swing.JLabel();
        txt32 = new javax.swing.JTextField();
        txt33 = new javax.swing.JTextField();
        btn22 = new javax.swing.JButton();
        pan22 = new javax.swing.JPanel();
        lab51 = new javax.swing.JLabel();
        lab52 = new javax.swing.JLabel();
        lab53 = new javax.swing.JLabel();
        txt27 = new javax.swing.JTextField();
        btn18 = new javax.swing.JButton();
        txt28 = new javax.swing.JTextField();
        btn19 = new javax.swing.JButton();
        txt29 = new javax.swing.JTextField();
        btn20 = new javax.swing.JButton();
        pan15 = new javax.swing.JPanel();
        lab29 = new javax.swing.JLabel();
        lab36 = new javax.swing.JLabel();
        btn3 = new javax.swing.JButton();
        txt19 = new javax.swing.JTextField();
        txt18 = new javax.swing.JTextField();
        lab61 = new javax.swing.JLabel();
        txt34 = new javax.swing.JTextField();
        btn25 = new javax.swing.JButton();
        lab62 = new javax.swing.JLabel();
        lab64 = new javax.swing.JLabel();
        lab65 = new javax.swing.JLabel();
        txt49 = new javax.swing.JTextField();
        txt50 = new javax.swing.JTextField();
        txt51 = new javax.swing.JTextField();
        btn5 = new javax.swing.JButton();
        btn29 = new javax.swing.JButton();
        lab67 = new javax.swing.JLabel();
        lab68 = new javax.swing.JLabel();
        spinHor = new javax.swing.JSpinner();
        spinVert = new javax.swing.JSpinner();
        pan16 = new javax.swing.JPanel();
        tabb2 = new javax.swing.JTabbedPane();
        pan20 = new javax.swing.JPanel();
        lab46 = new javax.swing.JLabel();
        lab45 = new javax.swing.JLabel();
        btn10 = new javax.swing.JButton();
        btn12 = new javax.swing.JButton();
        btn14 = new javax.swing.JButton();
        btn21 = new javax.swing.JButton();
        btn6 = new javax.swing.JButton();
        txt20 = new javax.swing.JTextField();
        txt30 = new javax.swing.JTextField();
        txt25 = new javax.swing.JTextField();
        txt21 = new javax.swing.JTextField();
        txt31 = new javax.swing.JTextField();
        txt16 = new javax.swing.JTextField();
        lab41 = new javax.swing.JLabel();
        txt24 = new javax.swing.JTextField();
        lab42 = new javax.swing.JLabel();
        txt26 = new javax.swing.JTextField();
        lab30 = new javax.swing.JLabel();
        lab25 = new javax.swing.JLabel();
        lab39 = new javax.swing.JLabel();
        txt59 = new javax.swing.JTextField();
        lab70 = new javax.swing.JLabel();
        pan23 = new javax.swing.JPanel();
        txt47 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btn17 = new javax.swing.JButton();
        btn24 = new javax.swing.JButton();
        txt46 = new javax.swing.JTextField();
        btn15 = new javax.swing.JButton();
        lab26 = new javax.swing.JLabel();
        btn23 = new javax.swing.JButton();
        txt45 = new javax.swing.JTextField();
        lab44 = new javax.swing.JLabel();
        lab63 = new javax.swing.JLabel();
        txt48 = new javax.swing.JTextField();
        txt57 = new javax.swing.JTextField();
        lab48 = new javax.swing.JLabel();
        txt58 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        pan19 = new javax.swing.JPanel();
        lab37 = new javax.swing.JLabel();
        lab43 = new javax.swing.JLabel();
        btn16 = new javax.swing.JButton();
        txt56 = new javax.swing.JTextField();
        txt54 = new javax.swing.JTextField();
        btn31 = new javax.swing.JButton();
        btn32 = new javax.swing.JButton();
        txt60 = new javax.swing.JTextField();
        lab66 = new javax.swing.JLabel();
        lab28 = new javax.swing.JLabel();
        txt55 = new javax.swing.JTextField();
        pan17 = new javax.swing.JPanel();
        lab49 = new javax.swing.JLabel();
        lab50 = new javax.swing.JLabel();
        txt36 = new javax.swing.JTextField();
        lab58 = new javax.swing.JLabel();
        txt37 = new javax.swing.JTextField();
        lab55 = new javax.swing.JLabel();
        txt38 = new javax.swing.JTextField();
        btn26 = new javax.swing.JButton();
        btn27 = new javax.swing.JButton();
        lab56 = new javax.swing.JLabel();
        txt39 = new javax.swing.JTextField();
        lab54 = new javax.swing.JLabel();
        txt40 = new javax.swing.JTextField();
        lab57 = new javax.swing.JLabel();
        txt41 = new javax.swing.JTextField();
        btn28 = new javax.swing.JButton();
        txt42 = new javax.swing.JTextField();
        lab59 = new javax.swing.JLabel();
        txt43 = new javax.swing.JTextField();
        lab60 = new javax.swing.JLabel();
        txt44 = new javax.swing.JTextField();
        pan18 = new javax.swing.JPanel();
        tabb1 = new javax.swing.JTabbedPane();
        pan6 = new javax.swing.JPanel();
        lab13 = new javax.swing.JLabel();
        lab14 = new javax.swing.JLabel();
        lab15 = new javax.swing.JLabel();
        lab16 = new javax.swing.JLabel();
        lab17 = new javax.swing.JLabel();
        lab19 = new javax.swing.JLabel();
        lab20 = new javax.swing.JLabel();
        lab24 = new javax.swing.JLabel();
        txt1 = new javax.swing.JTextField();
        txt2 = new javax.swing.JTextField();
        txt3 = new javax.swing.JTextField();
        txt4 = new javax.swing.JTextField();
        txt5 = new javax.swing.JTextField();
        txt7 = new javax.swing.JTextField();
        txt8 = new javax.swing.JTextField();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Системы профилей");
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage()));
        setMinimumSize(new java.awt.Dimension(800, 500));
        setPreferredSize(new java.awt.Dimension(900, 600));
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

        btnRef.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c038.gif"))); // NOI18N
        btnRef.setToolTipText(bundle.getString("Обновить")); // NOI18N
        btnRef.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnRef.setFocusable(false);
        btnRef.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRef.setMaximumSize(new java.awt.Dimension(25, 25));
        btnRef.setMinimumSize(new java.awt.Dimension(25, 25));
        btnRef.setPreferredSize(new java.awt.Dimension(25, 25));
        btnRef.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnRef.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefresh(evt);
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

        panSspinner.setMinimumSize(new java.awt.Dimension(50, 20));
        panSspinner.setPreferredSize(new java.awt.Dimension(100, 24));
        panSspinner.setLayout(new java.awt.BorderLayout());

        spinner.setFont(frames.UGui.getFont(1,0));
        spinner.setModel(new javax.swing.SpinnerNumberModel(0.0f, null, null, 1.0f));
        spinner.setBorder(null);
        spinner.setPreferredSize(new java.awt.Dimension(50, 24));
        panSspinner.add(spinner, java.awt.BorderLayout.CENTER);

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnFind1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFind2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMoveU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMoveD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 185, Short.MAX_VALUE)
                .addComponent(panSspinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 262, Short.MAX_VALUE)
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
            .addComponent(btnRef, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(toolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(btnReport1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnTest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnFind1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnMoveU, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnMoveD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(btnFind2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panSspinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        getContentPane().add(tool, java.awt.BorderLayout.PAGE_START);

        centr.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        centr.setPreferredSize(new java.awt.Dimension(800, 600));
        centr.setLayout(new java.awt.BorderLayout());

        scr1.setBorder(null);
        scr1.setPreferredSize(new java.awt.Dimension(260, 550));

        sysTree.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        sysTree.setFont(frames.UGui.getFont(0,0));
        sysTree.setEditable(true);
        sysTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                sysTreeMousePressed(evt);
            }
        });
        scr1.setViewportView(sysTree);

        centr.add(scr1, java.awt.BorderLayout.WEST);

        pan1.setPreferredSize(new java.awt.Dimension(440, 550));
        pan1.setLayout(new java.awt.GridLayout(2, 1));

        pan2.setPreferredSize(new java.awt.Dimension(540, 200));
        pan2.setLayout(new java.awt.GridLayout(1, 2));

        panDesign.setLayout(new java.awt.BorderLayout());
        pan2.add(panDesign);

        pan7.setPreferredSize(new java.awt.Dimension(300, 200));
        pan7.setLayout(new java.awt.CardLayout());

        pan11.setLayout(new java.awt.BorderLayout());

        scr7.setBorder(null);
        scr7.setPreferredSize(new java.awt.Dimension(450, 300));

        tab7.setFont(frames.UGui.getFont(0,0));
        tab7.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"111", "1", null},
                {"222", "2", null}
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
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Systree.this.mousePressed(evt);
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
        pan12.setPreferredSize(new java.awt.Dimension(300, 200));

        pan21.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Текстура изделия", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 0)));
        pan21.setPreferredSize(new java.awt.Dimension(308, 104));

        lab27.setFont(frames.UGui.getFont(0,0));
        lab27.setText("Основная");
        lab27.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab27.setPreferredSize(new java.awt.Dimension(80, 18));

        lab31.setFont(frames.UGui.getFont(0,0));
        lab31.setText("Внутренняя");
        lab31.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab31.setPreferredSize(new java.awt.Dimension(80, 18));

        lab32.setFont(frames.UGui.getFont(0,0));
        lab32.setText("Внешняя");
        lab32.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab32.setPreferredSize(new java.awt.Dimension(80, 18));

        btn9.setText("...");
        btn9.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn9.setMaximumSize(new java.awt.Dimension(21, 20));
        btn9.setMinimumSize(new java.awt.Dimension(21, 20));
        btn9.setName("btn9"); // NOI18N
        btn9.setPreferredSize(new java.awt.Dimension(21, 20));
        btn9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToWindows(evt);
            }
        });

        btn13.setText("...");
        btn13.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn13.setMaximumSize(new java.awt.Dimension(21, 20));
        btn13.setMinimumSize(new java.awt.Dimension(21, 20));
        btn13.setName("btn13"); // NOI18N
        btn13.setPreferredSize(new java.awt.Dimension(21, 20));
        btn13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToWindows(evt);
            }
        });

        btn2.setText("...");
        btn2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn2.setMaximumSize(new java.awt.Dimension(21, 20));
        btn2.setMinimumSize(new java.awt.Dimension(21, 20));
        btn2.setName("btn2"); // NOI18N
        btn2.setPreferredSize(new java.awt.Dimension(21, 20));
        btn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToWindows(evt);
            }
        });

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

        javax.swing.GroupLayout pan21Layout = new javax.swing.GroupLayout(pan21);
        pan21.setLayout(pan21Layout);
        pan21Layout.setHorizontalGroup(
            pan21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan21Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(pan21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan21Layout.createSequentialGroup()
                        .addComponent(lab32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt14, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))
                    .addGroup(pan21Layout.createSequentialGroup()
                        .addComponent(lab27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt9, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                    .addGroup(pan21Layout.createSequentialGroup()
                        .addComponent(lab31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt13, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );
        pan21Layout.setVerticalGroup(
            pan21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan21Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(pan21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        lab35.setFont(frames.UGui.getFont(0,0));
        lab35.setText("Ширина");
        lab35.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab35.setPreferredSize(new java.awt.Dimension(80, 18));

        lab38.setFont(frames.UGui.getFont(0,0));
        lab38.setText("Высота1");
        lab38.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab38.setPreferredSize(new java.awt.Dimension(80, 18));

        lab40.setFont(frames.UGui.getFont(0,0));
        lab40.setText("Высота2");
        lab40.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab40.setPreferredSize(new java.awt.Dimension(80, 18));

        txt17.setEditable(false);
        txt17.setFont(frames.UGui.getFont(0,0));
        txt17.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt17.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt17.setPreferredSize(new java.awt.Dimension(60, 18));

        txt22.setEditable(false);
        txt22.setFont(frames.UGui.getFont(0,0));
        txt22.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt22.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt22.setPreferredSize(new java.awt.Dimension(60, 18));

        txt23.setEditable(false);
        txt23.setFont(frames.UGui.getFont(0,0));
        txt23.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt23.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt23.setPreferredSize(new java.awt.Dimension(60, 18));

        javax.swing.GroupLayout pan12Layout = new javax.swing.GroupLayout(pan12);
        pan12.setLayout(pan12Layout);
        pan12Layout.setHorizontalGroup(
            pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pan21, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
            .addGroup(pan12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan12Layout.createSequentialGroup()
                        .addComponent(lab38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan12Layout.createSequentialGroup()
                        .addComponent(lab35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan12Layout.createSequentialGroup()
                        .addComponent(lab40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pan12Layout.setVerticalGroup(
            pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan12Layout.createSequentialGroup()
                .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addComponent(pan21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(71, Short.MAX_VALUE))
        );

        pan7.add(pan12, "card12");

        pan13.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Рама, импост..", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 1)));
        pan13.setPreferredSize(new java.awt.Dimension(300, 200));

        lab33.setFont(frames.UGui.getFont(0,0));
        lab33.setText("Артикул");
        lab33.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab33.setPreferredSize(new java.awt.Dimension(80, 18));

        lab34.setFont(frames.UGui.getFont(0,0));
        lab34.setText("Название");
        lab34.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab34.setPreferredSize(new java.awt.Dimension(80, 18));

        txt32.setEditable(false);
        txt32.setFont(frames.UGui.getFont(0,0));
        txt32.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt32.setPreferredSize(new java.awt.Dimension(180, 18));

        txt33.setEditable(false);
        txt33.setFont(frames.UGui.getFont(0,0));
        txt33.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt33.setPreferredSize(new java.awt.Dimension(180, 18));

        btn22.setText("...");
        btn22.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn22.setMaximumSize(new java.awt.Dimension(21, 20));
        btn22.setMinimumSize(new java.awt.Dimension(21, 20));
        btn22.setName("btnField17"); // NOI18N
        btn22.setPreferredSize(new java.awt.Dimension(21, 20));
        btn22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sysprofToFrame(evt);
            }
        });

        pan22.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Текстура изделия", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 0)));
        pan22.setPreferredSize(new java.awt.Dimension(308, 104));

        lab51.setFont(frames.UGui.getFont(0,0));
        lab51.setText("Основная");
        lab51.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab51.setPreferredSize(new java.awt.Dimension(80, 18));

        lab52.setFont(frames.UGui.getFont(0,0));
        lab52.setText("Внутренняя");
        lab52.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab52.setPreferredSize(new java.awt.Dimension(80, 18));

        lab53.setFont(frames.UGui.getFont(0,0));
        lab53.setText("Внешняя");
        lab53.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab53.setPreferredSize(new java.awt.Dimension(80, 18));

        txt27.setEditable(false);
        txt27.setFont(frames.UGui.getFont(0,0));
        txt27.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt27.setPreferredSize(new java.awt.Dimension(180, 18));

        btn18.setText("...");
        btn18.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn18.setMaximumSize(new java.awt.Dimension(21, 20));
        btn18.setMinimumSize(new java.awt.Dimension(21, 20));
        btn18.setName("btnField17"); // NOI18N
        btn18.setPreferredSize(new java.awt.Dimension(21, 20));
        btn18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToFrame(evt);
            }
        });

        txt28.setEditable(false);
        txt28.setFont(frames.UGui.getFont(0,0));
        txt28.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt28.setPreferredSize(new java.awt.Dimension(180, 18));

        btn19.setText("...");
        btn19.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn19.setMaximumSize(new java.awt.Dimension(21, 20));
        btn19.setMinimumSize(new java.awt.Dimension(21, 20));
        btn19.setName("btnField17"); // NOI18N
        btn19.setPreferredSize(new java.awt.Dimension(21, 20));
        btn19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToFrame(evt);
            }
        });

        txt29.setEditable(false);
        txt29.setFont(frames.UGui.getFont(0,0));
        txt29.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt29.setPreferredSize(new java.awt.Dimension(180, 18));

        btn20.setText("...");
        btn20.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn20.setMaximumSize(new java.awt.Dimension(21, 20));
        btn20.setMinimumSize(new java.awt.Dimension(21, 20));
        btn20.setName("btnField17"); // NOI18N
        btn20.setPreferredSize(new java.awt.Dimension(21, 20));
        btn20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToFrame(evt);
            }
        });

        javax.swing.GroupLayout pan22Layout = new javax.swing.GroupLayout(pan22);
        pan22.setLayout(pan22Layout);
        pan22Layout.setHorizontalGroup(
            pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan22Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lab53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt29, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                    .addComponent(txt27, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                    .addComponent(txt28, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        pan22Layout.setVerticalGroup(
            pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan22Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pan22Layout.createSequentialGroup()
                        .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pan22Layout.createSequentialGroup()
                        .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lab51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lab52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lab53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pan13Layout = new javax.swing.GroupLayout(pan13);
        pan13.setLayout(pan13Layout);
        pan13Layout.setHorizontalGroup(
            pan13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pan13Layout.createSequentialGroup()
                        .addComponent(lab33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt32, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pan13Layout.createSequentialGroup()
                        .addComponent(lab34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt33, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(pan22, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
        );
        pan13Layout.setVerticalGroup(
            pan13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan13Layout.createSequentialGroup()
                .addGroup(pan13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pan22, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 90, Short.MAX_VALUE))
        );

        pan7.add(pan13, "card13");

        pan15.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Стеклопакет", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 1)));
        pan15.setPreferredSize(new java.awt.Dimension(300, 200));

        lab29.setFont(frames.UGui.getFont(0,0));
        lab29.setText("Артикул");
        lab29.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab29.setPreferredSize(new java.awt.Dimension(80, 18));

        lab36.setFont(frames.UGui.getFont(0,0));
        lab36.setText("Название");
        lab36.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab36.setPreferredSize(new java.awt.Dimension(80, 18));

        btn3.setText("...");
        btn3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn3.setMaximumSize(new java.awt.Dimension(21, 20));
        btn3.setMinimumSize(new java.awt.Dimension(21, 20));
        btn3.setName("btnField17"); // NOI18N
        btn3.setPreferredSize(new java.awt.Dimension(21, 20));
        btn3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                artiklToGlass(evt);
            }
        });

        txt19.setEditable(false);
        txt19.setFont(frames.UGui.getFont(0,0));
        txt19.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt19.setPreferredSize(new java.awt.Dimension(180, 18));

        txt18.setEditable(false);
        txt18.setFont(frames.UGui.getFont(0,0));
        txt18.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt18.setPreferredSize(new java.awt.Dimension(180, 18));

        lab61.setFont(frames.UGui.getFont(0,0));
        lab61.setText("Цвет");
        lab61.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab61.setPreferredSize(new java.awt.Dimension(80, 18));

        txt34.setEditable(false);
        txt34.setFont(frames.UGui.getFont(0,0));
        txt34.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt34.setPreferredSize(new java.awt.Dimension(180, 18));

        btn25.setText("...");
        btn25.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn25.setMaximumSize(new java.awt.Dimension(21, 20));
        btn25.setMinimumSize(new java.awt.Dimension(21, 20));
        btn25.setName("btnField17"); // NOI18N
        btn25.setPreferredSize(new java.awt.Dimension(21, 20));
        btn25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorFromGlass(evt);
            }
        });

        lab62.setFont(frames.UGui.getFont(0,0));
        lab62.setText("Раскладка");
        lab62.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab62.setPreferredSize(new java.awt.Dimension(80, 18));

        lab64.setFont(frames.UGui.getFont(0,0));
        lab64.setText("Название");
        lab64.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab64.setPreferredSize(new java.awt.Dimension(80, 18));

        lab65.setFont(frames.UGui.getFont(0,0));
        lab65.setText("Текстура");
        lab65.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab65.setPreferredSize(new java.awt.Dimension(80, 18));

        txt49.setEditable(false);
        txt49.setFont(frames.UGui.getFont(0,0));
        txt49.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt49.setPreferredSize(new java.awt.Dimension(180, 18));

        txt50.setEditable(false);
        txt50.setFont(frames.UGui.getFont(0,0));
        txt50.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt50.setPreferredSize(new java.awt.Dimension(180, 18));

        txt51.setEditable(false);
        txt51.setFont(frames.UGui.getFont(0,0));
        txt51.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt51.setPreferredSize(new java.awt.Dimension(180, 18));

        btn5.setText("...");
        btn5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn5.setMaximumSize(new java.awt.Dimension(21, 20));
        btn5.setMinimumSize(new java.awt.Dimension(21, 20));
        btn5.setName("btnField17"); // NOI18N
        btn5.setPreferredSize(new java.awt.Dimension(21, 20));
        btn5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rascladkaToGlass(evt);
            }
        });

        btn29.setText("...");
        btn29.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn29.setMaximumSize(new java.awt.Dimension(21, 20));
        btn29.setMinimumSize(new java.awt.Dimension(21, 20));
        btn29.setName("btnField17"); // NOI18N
        btn29.setPreferredSize(new java.awt.Dimension(21, 20));
        btn29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToRascladka(evt);
            }
        });

        lab67.setFont(frames.UGui.getFont(0,0));
        lab67.setText("Кол. ячеек горизонтальных");
        lab67.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab67.setMaximumSize(new java.awt.Dimension(147, 19));
        lab67.setMinimumSize(new java.awt.Dimension(64, 19));
        lab67.setPreferredSize(new java.awt.Dimension(128, 19));

        lab68.setFont(frames.UGui.getFont(0,0));
        lab68.setText("Кол. ячеек вертикальных");
        lab68.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab68.setMaximumSize(new java.awt.Dimension(274, 19));
        lab68.setMinimumSize(new java.awt.Dimension(64, 19));
        lab68.setPreferredSize(new java.awt.Dimension(128, 19));

        spinHor.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        spinHor.setAlignmentX(0.0F);
        spinHor.setAlignmentY(0.0F);
        spinHor.setBorder(null);
        spinHor.setMinimumSize(new java.awt.Dimension(64, 19));
        spinHor.setPreferredSize(new java.awt.Dimension(40, 19));
        spinHor.setValue(2);
        spinHor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinHorStateChanged(evt);
            }
        });

        spinVert.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        spinVert.setBorder(null);
        spinVert.setMinimumSize(new java.awt.Dimension(64, 19));
        spinVert.setPreferredSize(new java.awt.Dimension(40, 19));
        spinVert.setValue(2);
        spinVert.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinVertStateChanged(evt);
            }
        });

        javax.swing.GroupLayout pan15Layout = new javax.swing.GroupLayout(pan15);
        pan15.setLayout(pan15Layout);
        pan15Layout.setHorizontalGroup(
            pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan15Layout.createSequentialGroup()
                        .addComponent(lab29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt19, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan15Layout.createSequentialGroup()
                        .addComponent(lab36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt18, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE))
                    .addGroup(pan15Layout.createSequentialGroup()
                        .addComponent(lab64, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt50, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan15Layout.createSequentialGroup()
                        .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pan15Layout.createSequentialGroup()
                                .addComponent(lab62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt49, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                            .addGroup(pan15Layout.createSequentialGroup()
                                .addComponent(lab61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt34, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan15Layout.createSequentialGroup()
                        .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pan15Layout.createSequentialGroup()
                                .addComponent(lab65, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt51, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pan15Layout.createSequentialGroup()
                                .addComponent(lab68, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinVert, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan15Layout.createSequentialGroup()
                        .addComponent(lab67, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinHor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pan15Layout.setVerticalGroup(
            pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan15Layout.createSequentialGroup()
                .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab64, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab65, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab67, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinHor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab68, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinVert, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 53, Short.MAX_VALUE))
        );

        txt19.getAccessibleContext().setAccessibleName("");

        pan7.add(pan15, "card15");

        pan16.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Створка", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 1)));
        pan16.setPreferredSize(new java.awt.Dimension(3100, 200));
        pan16.setLayout(new java.awt.BorderLayout());

        tabb2.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        tabb2.setFont(frames.UGui.getFont(-1,0));

        pan20.setPreferredSize(new java.awt.Dimension(308, 98));

        lab46.setFont(frames.UGui.getFont(0,0));
        lab46.setText("Высота ручки");
        lab46.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab46.setPreferredSize(new java.awt.Dimension(80, 18));

        lab45.setFont(frames.UGui.getFont(0,0));
        lab45.setText("Напр. откр.");
        lab45.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab45.setPreferredSize(new java.awt.Dimension(80, 18));

        btn10.setText("...");
        btn10.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn10.setMaximumSize(new java.awt.Dimension(21, 20));
        btn10.setMinimumSize(new java.awt.Dimension(21, 20));
        btn10.setName("btnField17"); // NOI18N
        btn10.setPreferredSize(new java.awt.Dimension(21, 20));
        btn10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sysfurnToStvorka(evt);
            }
        });

        btn12.setText("...");
        btn12.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn12.setMaximumSize(new java.awt.Dimension(21, 20));
        btn12.setMinimumSize(new java.awt.Dimension(21, 20));
        btn12.setName("btnField17"); // NOI18N
        btn12.setPreferredSize(new java.awt.Dimension(21, 20));
        btn12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handlToStvorka(evt);
            }
        });

        btn14.setText("...");
        btn14.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn14.setMaximumSize(new java.awt.Dimension(21, 20));
        btn14.setMinimumSize(new java.awt.Dimension(21, 20));
        btn14.setName("btnField17"); // NOI18N
        btn14.setPreferredSize(new java.awt.Dimension(21, 20));
        btn14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToHandl(evt);
            }
        });

        btn21.setText("...");
        btn21.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn21.setMaximumSize(new java.awt.Dimension(21, 20));
        btn21.setMinimumSize(new java.awt.Dimension(21, 20));
        btn21.setName("btnField17"); // NOI18N
        btn21.setPreferredSize(new java.awt.Dimension(21, 20));
        btn21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeOpenToStvorka(evt);
            }
        });

        btn6.setText("...");
        btn6.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn6.setMaximumSize(new java.awt.Dimension(21, 20));
        btn6.setMinimumSize(new java.awt.Dimension(21, 20));
        btn6.setName("btnField17"); // NOI18N
        btn6.setPreferredSize(new java.awt.Dimension(21, 20));
        btn6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                heightHandlToStvorka(evt);
            }
        });

        txt20.setEditable(false);
        txt20.setFont(frames.UGui.getFont(0,0));
        txt20.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt20.setPreferredSize(new java.awt.Dimension(180, 18));

        txt30.setEditable(false);
        txt30.setFont(frames.UGui.getFont(0,0));
        txt30.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt30.setPreferredSize(new java.awt.Dimension(180, 18));

        txt25.setEditable(false);
        txt25.setFont(frames.UGui.getFont(0,0));
        txt25.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt25.setPreferredSize(new java.awt.Dimension(180, 18));

        txt21.setEditable(false);
        txt21.setFont(frames.UGui.getFont(0,0));
        txt21.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt21.setPreferredSize(new java.awt.Dimension(180, 18));

        txt31.setFont(frames.UGui.getFont(0,0));
        txt31.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt31.setPreferredSize(new java.awt.Dimension(56, 18));

        txt16.setEditable(false);
        txt16.setFont(frames.UGui.getFont(0,0));
        txt16.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt16.setPreferredSize(new java.awt.Dimension(180, 18));

        lab41.setFont(frames.UGui.getFont(0,0));
        lab41.setText("Ширина");
        lab41.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab41.setPreferredSize(new java.awt.Dimension(80, 18));

        txt24.setFont(frames.UGui.getFont(0,0));
        txt24.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt24.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt24.setMinimumSize(new java.awt.Dimension(40, 18));
        txt24.setPreferredSize(new java.awt.Dimension(48, 18));

        lab42.setFont(frames.UGui.getFont(0,0));
        lab42.setText("Высота");
        lab42.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab42.setPreferredSize(new java.awt.Dimension(60, 18));

        txt26.setEditable(false);
        txt26.setFont(frames.UGui.getFont(0,0));
        txt26.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt26.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt26.setEnabled(false);
        txt26.setMinimumSize(new java.awt.Dimension(40, 18));
        txt26.setPreferredSize(new java.awt.Dimension(48, 18));

        lab30.setFont(frames.UGui.getFont(0,0));
        lab30.setText("Фурнитура");
        lab30.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab30.setPreferredSize(new java.awt.Dimension(80, 18));

        lab25.setFont(frames.UGui.getFont(0,0));
        lab25.setText("Ручка");
        lab25.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab25.setMaximumSize(new java.awt.Dimension(80, 18));
        lab25.setMinimumSize(new java.awt.Dimension(80, 18));
        lab25.setPreferredSize(new java.awt.Dimension(80, 18));

        lab39.setFont(frames.UGui.getFont(0,0));
        lab39.setText("Текстура");
        lab39.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab39.setMaximumSize(new java.awt.Dimension(80, 18));
        lab39.setMinimumSize(new java.awt.Dimension(80, 18));
        lab39.setPreferredSize(new java.awt.Dimension(80, 18));

        txt59.setEditable(false);
        txt59.setFont(frames.UGui.getFont(0,0));
        txt59.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt59.setPreferredSize(new java.awt.Dimension(180, 18));

        lab70.setFont(frames.UGui.getFont(0,0));
        lab70.setText("Название");
        lab70.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab70.setMaximumSize(new java.awt.Dimension(80, 18));
        lab70.setMinimumSize(new java.awt.Dimension(80, 18));
        lab70.setPreferredSize(new java.awt.Dimension(80, 18));

        javax.swing.GroupLayout pan20Layout = new javax.swing.GroupLayout(pan20);
        pan20.setLayout(pan20Layout);
        pan20Layout.setHorizontalGroup(
            pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan20Layout.createSequentialGroup()
                        .addComponent(lab45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt30, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan20Layout.createSequentialGroup()
                        .addComponent(lab25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt21, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan20Layout.createSequentialGroup()
                        .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lab46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lab39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pan20Layout.createSequentialGroup()
                                .addComponent(txt16, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt31, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txt25, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pan20Layout.createSequentialGroup()
                        .addComponent(lab70, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt59, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE))
                    .addGroup(pan20Layout.createSequentialGroup()
                        .addComponent(lab30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt20, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan20Layout.createSequentialGroup()
                        .addComponent(lab41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lab42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pan20Layout.setVerticalGroup(
            pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt59, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab70, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        tabb2.addTab("Основн...", pan20);

        txt47.setEditable(false);
        txt47.setFont(frames.UGui.getFont(0,0));
        txt47.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt47.setPreferredSize(new java.awt.Dimension(180, 18));

        jLabel1.setFont(frames.UGui.getFont(0,0));
        jLabel1.setText("Замок");
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel1.setMaximumSize(new java.awt.Dimension(80, 18));
        jLabel1.setMinimumSize(new java.awt.Dimension(80, 18));
        jLabel1.setPreferredSize(new java.awt.Dimension(80, 18));

        btn17.setText("...");
        btn17.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn17.setMaximumSize(new java.awt.Dimension(21, 20));
        btn17.setMinimumSize(new java.awt.Dimension(21, 20));
        btn17.setName("btnField17"); // NOI18N
        btn17.setPreferredSize(new java.awt.Dimension(21, 20));
        btn17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorFromLoop(evt);
            }
        });

        btn24.setText("...");
        btn24.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn24.setMaximumSize(new java.awt.Dimension(21, 20));
        btn24.setMinimumSize(new java.awt.Dimension(21, 20));
        btn24.setName("btnField17"); // NOI18N
        btn24.setPreferredSize(new java.awt.Dimension(21, 20));
        btn24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorFromLock(evt);
            }
        });

        txt46.setEditable(false);
        txt46.setFont(frames.UGui.getFont(0,0));
        txt46.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt46.setPreferredSize(new java.awt.Dimension(180, 18));

        btn15.setText("...");
        btn15.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn15.setMaximumSize(new java.awt.Dimension(21, 20));
        btn15.setMinimumSize(new java.awt.Dimension(21, 20));
        btn15.setName("btnField17"); // NOI18N
        btn15.setPreferredSize(new java.awt.Dimension(21, 20));
        btn15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loopToStvorka(evt);
            }
        });

        lab26.setFont(frames.UGui.getFont(0,0));
        lab26.setText("Подвес");
        lab26.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab26.setMaximumSize(new java.awt.Dimension(80, 18));
        lab26.setMinimumSize(new java.awt.Dimension(80, 18));
        lab26.setPreferredSize(new java.awt.Dimension(80, 18));

        btn23.setText("...");
        btn23.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn23.setMaximumSize(new java.awt.Dimension(21, 20));
        btn23.setMinimumSize(new java.awt.Dimension(21, 20));
        btn23.setName("btnField17"); // NOI18N
        btn23.setPreferredSize(new java.awt.Dimension(21, 20));
        btn23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lockToStvorka(evt);
            }
        });

        txt45.setEditable(false);
        txt45.setFont(frames.UGui.getFont(0,0));
        txt45.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt45.setPreferredSize(new java.awt.Dimension(180, 18));

        lab44.setFont(frames.UGui.getFont(0,0));
        lab44.setText("Текстура");
        lab44.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab44.setMaximumSize(new java.awt.Dimension(80, 18));
        lab44.setMinimumSize(new java.awt.Dimension(80, 18));
        lab44.setPreferredSize(new java.awt.Dimension(80, 18));

        lab63.setFont(frames.UGui.getFont(0,0));
        lab63.setText("Текстура");
        lab63.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab63.setMaximumSize(new java.awt.Dimension(80, 18));
        lab63.setMinimumSize(new java.awt.Dimension(80, 18));
        lab63.setPreferredSize(new java.awt.Dimension(80, 18));

        txt48.setEditable(false);
        txt48.setFont(frames.UGui.getFont(0,0));
        txt48.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt48.setPreferredSize(new java.awt.Dimension(180, 18));

        txt57.setEditable(false);
        txt57.setFont(frames.UGui.getFont(0,0));
        txt57.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt57.setPreferredSize(new java.awt.Dimension(180, 18));

        lab48.setFont(frames.UGui.getFont(0,0));
        lab48.setText("Название");
        lab48.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab48.setMaximumSize(new java.awt.Dimension(80, 18));
        lab48.setMinimumSize(new java.awt.Dimension(80, 18));
        lab48.setPreferredSize(new java.awt.Dimension(80, 18));

        txt58.setEditable(false);
        txt58.setFont(frames.UGui.getFont(0,0));
        txt58.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt58.setPreferredSize(new java.awt.Dimension(180, 18));

        jLabel2.setFont(frames.UGui.getFont(0,0));
        jLabel2.setText("Название");
        jLabel2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel2.setMaximumSize(new java.awt.Dimension(80, 18));
        jLabel2.setMinimumSize(new java.awt.Dimension(80, 18));
        jLabel2.setPreferredSize(new java.awt.Dimension(80, 18));

        javax.swing.GroupLayout pan23Layout = new javax.swing.GroupLayout(pan23);
        pan23.setLayout(pan23Layout);
        pan23Layout.setHorizontalGroup(
            pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(lab26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt45, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt46, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(lab48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt57, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan23Layout.createSequentialGroup()
                        .addComponent(lab44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt47, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(lab63, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt48, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt58, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pan23Layout.setVerticalGroup(
            pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab63, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(66, Short.MAX_VALUE))
        );

        tabb2.addTab("Дополн...", pan23);

        lab37.setFont(frames.UGui.getFont(0,0));
        lab37.setText("Название");
        lab37.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab37.setMaximumSize(new java.awt.Dimension(80, 18));
        lab37.setMinimumSize(new java.awt.Dimension(80, 18));
        lab37.setPreferredSize(new java.awt.Dimension(80, 18));

        lab43.setFont(frames.UGui.getFont(0,0));
        lab43.setText("Состав");
        lab43.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab43.setMaximumSize(new java.awt.Dimension(80, 18));
        lab43.setMinimumSize(new java.awt.Dimension(80, 18));
        lab43.setPreferredSize(new java.awt.Dimension(80, 18));

        btn16.setText("...");
        btn16.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn16.setMaximumSize(new java.awt.Dimension(21, 20));
        btn16.setMinimumSize(new java.awt.Dimension(21, 20));
        btn16.setName("btnField17"); // NOI18N
        btn16.setPreferredSize(new java.awt.Dimension(21, 20));
        btn16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mosquitToStvorka(evt);
            }
        });

        txt56.setEditable(false);
        txt56.setFont(frames.UGui.getFont(0,0));
        txt56.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt56.setPreferredSize(new java.awt.Dimension(180, 18));

        txt54.setEditable(false);
        txt54.setFont(frames.UGui.getFont(0,0));
        txt54.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt54.setPreferredSize(new java.awt.Dimension(180, 18));

        btn31.setText("...");
        btn31.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn31.setMaximumSize(new java.awt.Dimension(21, 20));
        btn31.setMinimumSize(new java.awt.Dimension(21, 20));
        btn31.setName("btnField17"); // NOI18N
        btn31.setPreferredSize(new java.awt.Dimension(21, 20));
        btn31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mosqToElements(evt);
            }
        });

        btn32.setText("...");
        btn32.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn32.setMaximumSize(new java.awt.Dimension(21, 20));
        btn32.setMinimumSize(new java.awt.Dimension(21, 20));
        btn32.setName("btnField17"); // NOI18N
        btn32.setPreferredSize(new java.awt.Dimension(21, 20));
        btn32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mosqToColor(evt);
            }
        });

        txt60.setEditable(false);
        txt60.setFont(frames.UGui.getFont(0,0));
        txt60.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt60.setPreferredSize(new java.awt.Dimension(180, 18));

        lab66.setFont(frames.UGui.getFont(0,0));
        lab66.setText("Текстура");
        lab66.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab66.setMaximumSize(new java.awt.Dimension(80, 18));
        lab66.setMinimumSize(new java.awt.Dimension(80, 18));
        lab66.setPreferredSize(new java.awt.Dimension(80, 18));

        lab28.setFont(frames.UGui.getFont(0,0));
        lab28.setText("Моск. сетка");
        lab28.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab28.setMaximumSize(new java.awt.Dimension(80, 18));
        lab28.setMinimumSize(new java.awt.Dimension(80, 18));
        lab28.setPreferredSize(new java.awt.Dimension(80, 18));

        txt55.setEditable(false);
        txt55.setFont(frames.UGui.getFont(0,0));
        txt55.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt55.setPreferredSize(new java.awt.Dimension(212, 18));

        javax.swing.GroupLayout pan19Layout = new javax.swing.GroupLayout(pan19);
        pan19.setLayout(pan19Layout);
        pan19Layout.setHorizontalGroup(
            pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 306, Short.MAX_VALUE)
            .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pan19Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pan19Layout.createSequentialGroup()
                            .addComponent(lab28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt54, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btn16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(pan19Layout.createSequentialGroup()
                            .addComponent(lab43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt56, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btn31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(pan19Layout.createSequentialGroup()
                            .addComponent(lab37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt55, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan19Layout.createSequentialGroup()
                            .addComponent(lab66, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt60, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btn32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap()))
        );
        pan19Layout.setVerticalGroup(
            pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 218, Short.MAX_VALUE)
            .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pan19Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(lab28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lab37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txt55, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lab66, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txt56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lab43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(116, Short.MAX_VALUE)))
        );

        tabb2.addTab("Москитка", pan19);

        pan16.add(tabb2, java.awt.BorderLayout.CENTER);

        pan7.add(pan16, "card16");

        pan17.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Соединения", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 1)));
        pan17.setPreferredSize(new java.awt.Dimension(300, 200));

        lab49.setFont(frames.UGui.getFont(0,0));
        lab49.setText("1  соединение");
        lab49.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab49.setIconTextGap(1);
        lab49.setPreferredSize(new java.awt.Dimension(80, 18));

        lab50.setFont(frames.UGui.getFont(0,0));
        lab50.setText("2  соединение");
        lab50.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab50.setPreferredSize(new java.awt.Dimension(80, 18));

        txt36.setEditable(false);
        txt36.setFont(frames.UGui.getFont(0,0));
        txt36.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt36.setPreferredSize(new java.awt.Dimension(180, 18));

        lab58.setFont(frames.UGui.getFont(0,0));
        lab58.setText("Артикул 1,2");
        lab58.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab58.setIconTextGap(1);
        lab58.setPreferredSize(new java.awt.Dimension(80, 18));

        txt37.setEditable(false);
        txt37.setFont(frames.UGui.getFont(0,0));
        txt37.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt37.setPreferredSize(new java.awt.Dimension(180, 18));

        lab55.setFont(frames.UGui.getFont(0,0));
        lab55.setText("Вариант");
        lab55.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab55.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lab55.setIconTextGap(6);
        lab55.setPreferredSize(new java.awt.Dimension(80, 19));

        txt38.setEditable(false);
        txt38.setFont(frames.UGui.getFont(0,0));
        txt38.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt38.setPreferredSize(new java.awt.Dimension(180, 18));

        btn26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b018.gif"))); // NOI18N
        btn26.setToolTipText(bundle.getString("Поиск записи")); // NOI18N
        btn26.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn26.setMaximumSize(new java.awt.Dimension(18, 18));
        btn26.setMinimumSize(new java.awt.Dimension(18, 18));
        btn26.setName("btn26"); // NOI18N
        btn26.setPreferredSize(new java.awt.Dimension(18, 18));
        btn26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                joinToFrame(evt);
            }
        });

        btn27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b018.gif"))); // NOI18N
        btn27.setToolTipText(bundle.getString("Поиск записи")); // NOI18N
        btn27.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn27.setMaximumSize(new java.awt.Dimension(18, 18));
        btn27.setMinimumSize(new java.awt.Dimension(18, 18));
        btn27.setName("btn27"); // NOI18N
        btn27.setPreferredSize(new java.awt.Dimension(18, 18));
        btn27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                joinToFrame(evt);
            }
        });

        lab56.setFont(frames.UGui.getFont(0,0));
        lab56.setText("Вариант");
        lab56.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab56.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lab56.setIconTextGap(6);
        lab56.setPreferredSize(new java.awt.Dimension(80, 19));

        txt39.setEditable(false);
        txt39.setFont(frames.UGui.getFont(0,0));
        txt39.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt39.setPreferredSize(new java.awt.Dimension(180, 18));

        lab54.setFont(frames.UGui.getFont(0,0));
        lab54.setText("3  соединение");
        lab54.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab54.setPreferredSize(new java.awt.Dimension(80, 18));

        txt40.setEditable(false);
        txt40.setFont(frames.UGui.getFont(0,0));
        txt40.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt40.setPreferredSize(new java.awt.Dimension(180, 18));

        lab57.setFont(frames.UGui.getFont(0,0));
        lab57.setText("Вариант");
        lab57.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab57.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lab57.setIconTextGap(6);
        lab57.setPreferredSize(new java.awt.Dimension(80, 19));

        txt41.setEditable(false);
        txt41.setFont(frames.UGui.getFont(0,0));
        txt41.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt41.setPreferredSize(new java.awt.Dimension(180, 18));

        btn28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b018.gif"))); // NOI18N
        btn28.setToolTipText(bundle.getString("Поиск записи")); // NOI18N
        btn28.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn28.setMaximumSize(new java.awt.Dimension(18, 18));
        btn28.setMinimumSize(new java.awt.Dimension(18, 18));
        btn28.setName("btn28"); // NOI18N
        btn28.setPreferredSize(new java.awt.Dimension(18, 18));
        btn28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                joinToFrame(evt);
            }
        });

        txt42.setEditable(false);
        txt42.setFont(frames.UGui.getFont(0,0));
        txt42.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt42.setPreferredSize(new java.awt.Dimension(180, 18));

        lab59.setFont(frames.UGui.getFont(0,0));
        lab59.setText("Артикул 1,2");
        lab59.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab59.setIconTextGap(1);
        lab59.setPreferredSize(new java.awt.Dimension(80, 18));

        txt43.setEditable(false);
        txt43.setFont(frames.UGui.getFont(0,0));
        txt43.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt43.setPreferredSize(new java.awt.Dimension(180, 18));

        lab60.setFont(frames.UGui.getFont(0,0));
        lab60.setText("Артикул 1,2");
        lab60.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab60.setIconTextGap(1);
        lab60.setPreferredSize(new java.awt.Dimension(80, 18));

        txt44.setEditable(false);
        txt44.setFont(frames.UGui.getFont(0,0));
        txt44.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt44.setPreferredSize(new java.awt.Dimension(180, 18));

        javax.swing.GroupLayout pan17Layout = new javax.swing.GroupLayout(pan17);
        pan17.setLayout(pan17Layout);
        pan17Layout.setHorizontalGroup(
            pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab55, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt36, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan17Layout.createSequentialGroup()
                        .addComponent(lab50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt37, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab59, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pan17Layout.setVerticalGroup(
            pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan17Layout.createSequentialGroup()
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab55, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab59, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 36, Short.MAX_VALUE))
        );

        pan7.add(pan17, "card17");

        javax.swing.GroupLayout pan18Layout = new javax.swing.GroupLayout(pan18);
        pan18.setLayout(pan18Layout);
        pan18Layout.setHorizontalGroup(
            pan18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 316, Short.MAX_VALUE)
        );
        pan18Layout.setVerticalGroup(
            pan18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 270, Short.MAX_VALUE)
        );

        pan7.add(pan18, "card18");

        pan2.add(pan7);

        pan1.add(pan2);

        tabb1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 2, 0, 0));
        tabb1.setFont(frames.UGui.getFont(0,0));
        tabb1.setPreferredSize(new java.awt.Dimension(540, 250));
        tabb1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                Systree.this.stateChanged(evt);
            }
        });

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

        lab20.setFont(frames.UGui.getFont(0,0));
        lab20.setText("Система");
        lab20.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab20.setPreferredSize(new java.awt.Dimension(112, 18));

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
        txt7.setPreferredSize(new java.awt.Dimension(70, 18));

        txt8.setFont(frames.UGui.getFont(0,0));
        txt8.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt8.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt8.setEnabled(false);
        txt8.setPreferredSize(new java.awt.Dimension(450, 18));

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
        lab69.setText("Примечание");
        lab69.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab69.setPreferredSize(new java.awt.Dimension(120, 18));

        txt12.setFont(frames.UGui.getFont(0,0));
        txt12.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt12.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt12.setPreferredSize(new java.awt.Dimension(200, 18));

        javax.swing.GroupLayout pan6Layout = new javax.swing.GroupLayout(pan6);
        pan6.setLayout(pan6Layout);
        pan6Layout.setHorizontalGroup(
            pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan6Layout.createSequentialGroup()
                        .addComponent(lab69, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan6Layout.createSequentialGroup()
                        .addComponent(lab20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt8, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE))
                    .addGroup(pan6Layout.createSequentialGroup()
                        .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pan6Layout.createSequentialGroup()
                                .addComponent(lab19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt7, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan6Layout.createSequentialGroup()
                                .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pan6Layout.createSequentialGroup()
                                        .addComponent(lab15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt3, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pan6Layout.createSequentialGroup()
                                        .addComponent(lab16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt4, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pan6Layout.createSequentialGroup()
                                        .addComponent(lab17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt5, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)))
                                .addGap(18, 18, 18)))
                        .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pan6Layout.createSequentialGroup()
                                .addComponent(lab14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt2, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE))
                            .addGroup(pan6Layout.createSequentialGroup()
                                .addComponent(lab24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt11, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan6Layout.createSequentialGroup()
                                .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pan6Layout.createSequentialGroup()
                                        .addComponent(lab13, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt1, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btn4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(pan6Layout.createSequentialGroup()
                                        .addComponent(lab47, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt35, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)))
                                .addGap(2, 2, 2)))))
                .addGap(19, 19, 19))
        );
        pan6Layout.setVerticalGroup(
            pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lab19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lab13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lab47)
                        .addComponent(txt35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lab15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan6Layout.createSequentialGroup()
                        .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lab24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txt11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btn11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lab14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan6Layout.createSequentialGroup()
                        .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lab16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lab17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab69, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(89, Short.MAX_VALUE))
        );

        tabb1.addTab("   Основные   ", pan6);

        pan3.setLayout(new java.awt.BorderLayout());

        scr2.setBorder(null);
        scr2.setPreferredSize(new java.awt.Dimension(450, 300));

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
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Systree.this.mousePressed(evt);
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
        scr3.setPreferredSize(new java.awt.Dimension(450, 300));

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
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Systree.this.mousePressed(evt);
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
        scr4.setPreferredSize(new java.awt.Dimension(450, 300));

        tab4.setFont(frames.UGui.getFont(0,0));
        tab4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"111", "1",  new Boolean(true), null},
                {"222", "2",  new Boolean(true), null}
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
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Systree.this.mousePressed(evt);
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
        tab5.setRowHeight(68);
        tab5.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Systree.this.mousePressed(evt);
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
        scr6.setPreferredSize(new java.awt.Dimension(240, 324));

        winTree.setFont(frames.UGui.getFont(0,0));
        scr6.setViewportView(winTree);

        pan10.add(scr6, java.awt.BorderLayout.EAST);

        tabb1.addTab("   Модели   ", pan10);

        pan1.add(tabb1);
        tabb1.getAccessibleContext().setAccessibleName("<html><font size=\"3\">&nbsp;&nbsp;&nbsp\nОсновные\n&nbsp;&nbsp;&nbsp");

        centr.add(pan1, java.awt.BorderLayout.CENTER);

        getContentPane().add(centr, java.awt.BorderLayout.CENTER);

        south.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.setMinimumSize(new java.awt.Dimension(100, 20));
        south.setPreferredSize(new java.awt.Dimension(800, 20));
        south.setLayout(new javax.swing.BoxLayout(south, javax.swing.BoxLayout.LINE_AXIS));

        filler1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.add(filler1);

        lab1.setText("___");
        south.add(lab1);

        filler2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.add(filler2);

        lab2.setText("___");
        south.add(lab2);

        filler3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.add(filler3);

        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mousePressed
//        JTable table = (JTable) evt.getSource();
//        UGui.updateBorderAndSql(table, List.of(tab2, tab3, tab4, tab5));
//        if (sysTree.isEditing()) {
//            sysTree.getCellEditor().stopCellEditing();
//        }
//        sysTree.setBorder(null);
    }//GEN-LAST:event_mousePressed

    private void windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosed
        UGui.stopCellEditing(sysTree, tab2, tab3, tab4, tab5);
        qSystree.execsql();
        List.of(tab2, tab3, tab4, tab5).forEach(tab -> ((DefTableModel) tab.getModel()).getQuery().execsql());
        if (models != null) {
            models.dispose();
        }
    }//GEN-LAST:event_windowClosed

    private void stateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_stateChanged

        UGui.stopCellEditing(sysTree);
        sysTree.setBorder(null);
        List.of(btnFind1, btnFind2, btnMoveU, btnMoveD).forEach(btn -> btn.setEnabled(false));

        if (tabb1.getSelectedIndex() == 1) {
            UGui.updateBorderAndSql(tab2, List.of(tab2, tab3, tab4, tab5));
            List.of(btnMoveU, btnMoveD).forEach(btn -> btn.setEnabled(true));
            btnFind1.setEnabled(true);
            btnFind2.setEnabled(true);

        } else if (tabb1.getSelectedIndex() == 2) {
            UGui.updateBorderAndSql(tab3, List.of(tab2, tab3, tab4, tab5));
            List.of(btnMoveU, btnMoveD).forEach(btn -> btn.setEnabled(true));
            btnFind2.setEnabled(true);

        } else if (tabb1.getSelectedIndex() == 3) {
            UGui.updateBorderAndSql(tab4, List.of(tab2, tab3, tab4, tab5));

        } else if (tabb1.getSelectedIndex() == 4) {
            UGui.updateBorderAndSql(tab5, List.of(tab2, tab3, tab4, tab5));
            List.of(btnMoveU, btnMoveD).forEach(btn -> btn.setEnabled(true));
        }
    }//GEN-LAST:event_stateChanged

    private void sysTreeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sysTreeMousePressed
        List.of(tab2, tab3, tab4, tab5).forEach(tab -> tab.setBorder(null));
        sysTree.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255)));
        UGui.stopCellEditing(tab2, tab3, tab4, tab5);
    }//GEN-LAST:event_sysTreeMousePressed

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

        }, LayoutProduct.values());
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
                    int id = Conn.genId(eSystree.id);
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
                    int index = UGui.getIndexKeyValue(tab2, record, eSysprof.id);
                    qSysprof.table(eArtikl.up).add(index, eArtikl.up.newRecord());
                });

            } else if (tab3.getBorder() != null) {
                UGui.insertRecordCur(tab3, eSysfurn.up, (record) -> {
                    record.set(eSysfurn.systree_id, systreeID);
                    record.setNo(eSysfurn.npp, record.get(1));
                    record.setNo(eSysfurn.replac, 0);
                    record.setNo(eSysfurn.side_open, TypeOpen2.QUE.id);
                    record.setNo(eSysfurn.hand_pos, LayoutHandle.MIDL.id);
                    int index = UGui.getIndexKeyValue(tab3, record, eSysfurn.id);
                    qSysfurn.table(eFurniture.up).add(index, eFurniture.up.newRecord());
                });
            } else if (tab4.getBorder() != null) {
                UGui.insertRecordCur(tab4, eSyspar1.up, (record) -> {
                    record.set(eSyspar1.systree_id, systreeID);
                });

            } else if (tab5.getBorder() != null) {
                if (sysNode != null && sysNode.isLeaf()) {
                    if (evt.getSource() instanceof JMenuItem && eProp.dev == true) {
                        testBimax();
                    } else {
                        ProgressBar.create(Systree.this, new ListenerFrame() {
                            public void actionRequest(Object obj) {
                                models = new Models(listenerModel);
                                FrameToFile.setFrameSize(models);
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
                if (sysNode.getChildCount() != 0) {
                    JOptionPane.showMessageDialog(this, "Нельзя удалить текущий узел т. к. у него есть подчинённые записи", "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                DefMutableTreeNode parentNode = (DefMutableTreeNode) sysNode.getParent();
                if (JOptionPane.showConfirmDialog(this, "Хотите удалить " + sysNode + " узел?", "Подтвердите удаление",
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

    private void btnRefresh(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefresh
        DefMutableTreeNode selectNodeSys = (DefMutableTreeNode) sysTree.getLastSelectedPathComponent();
        double id1 = selectNodeSys.rec().getDbl(eSystree.id);
        DefMutableTreeNode selectNodeWin = (DefMutableTreeNode) winTree.getLastSelectedPathComponent();
        double id2 = (selectNodeWin != null) ? selectNodeWin.com5t().id() : -1;
        UGui.stopCellEditing(sysTree, tab2, tab3, tab4, tab5);
        qSystree.execsql();
        List.of(tab2, tab3, tab4, tab5).forEach(tab -> ((DefTableModel) tab.getModel()).getQuery().execsql());
        Query.listOpenTable.forEach(q -> q.clear());
        loadingData();
        selectionTree1();
        UGui.selectionPathSys(id1, sysTree); //установим курсор выделения          
        UGui.selectionPathWin(id2, winTree); //установим курсор выделения          
    }//GEN-LAST:event_btnRefresh

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
        ((DefaultTableModel) tab4.getModel()).fireTableDataChanged();
        UGui.setSelectedIndex(tab4, 5);
    }//GEN-LAST:event_btnReport

    private void btnClose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose
        this.dispose();
    }//GEN-LAST:event_btnClose

    private void sysprofToFrame(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sysprofToFrame
        try {
            if (winNode != null) {
                Layout layout = winNode.com5t().layout();
                double selectID = winNode.com5t().id(); //id элемента который уже есть в конструкции, это либо виртуал. либо найденный по приоритету при построении модели
                Query qSysprofFilter = new Query(eSysprof.values(), eArtikl.values()); //тут будет список допустимых профилей из ветки системы
                //Цикл по профилям ветки 
                for (int index = 0; index < qSysprof.size(); ++index) {
                    Record sysprofRec = qSysprof.get(index);

                    //Отфильтруем подходящие по параметрам
                    if (winNode.com5t().type().id2 == sysprofRec.getInt(eSysprof.use_type)) {
                        int useSideId = sysprofRec.getInt(eSysprof.use_side);
                        if (useSideId == layout.id
                                || ((layout == Layout.BOTT || layout == Layout.TOP) && useSideId == UseSide.HORIZ.id)
                                || ((layout == Layout.RIGHT || layout == Layout.LEFT) && useSideId == UseSide.VERT.id)
                                || useSideId == UseSide.ANY.id || useSideId == UseSide.MANUAL.id) {

                            qSysprofFilter.add(sysprofRec);
                            qSysprofFilter.table(eArtikl.up).add(qSysprof.table(eArtikl.up).get(index));
                        }
                    }
                }
                new DicSysprof(this, (sysprofRec) -> {
                    Wincalc winc = wincalc();
                    if (winNode.com5t().type() == enums.Type.FRAME_SIDE) { //рама окна
                        double elemId = winNode.com5t().id();
                        GsonElem gsonRama = winc.listAll.gson(elemId);
                        if (sysprofRec.get(1) == null) {
                            gsonRama.param().remove(PKjson.sysprofID);
                        } else {
                            gsonRama.param().addProperty(PKjson.sysprofID, sysprofRec.getInt(eSysprof.id));
                        }
                        updateScript(selectID);

                    } else if (winNode.com5t().type() == enums.Type.STVORKA_SIDE) { //рама створки
                        double stvId = winNode.com5t().owner().id();
                        GsonElem stvArea = (GsonElem) winc.listAll.gson(stvId);
                        JsonObject paramObj = stvArea.param();
                        String stvKey = null;
                        if (layout == Layout.BOTT) {
                            stvKey = PKjson.stvorkaBottom;
                        } else if (layout == Layout.RIGHT) {
                            stvKey = PKjson.stvorkaRight;
                        } else if (layout == Layout.TOP) {
                            stvKey = PKjson.stvorkaTop;
                        } else if (layout == Layout.LEFT) {
                            stvKey = PKjson.stvorkaLeft;
                        }
                        JsonObject jso = UGui.getAsJsonObject(paramObj, stvKey);
                        if (sysprofRec.get(1) == null) {
                            jso.remove(PKjson.sysprofID);
                        } else {
                            jso.addProperty(PKjson.sysprofID, sysprofRec.getStr(eSysprof.id));
                        }
                        updateScript(selectID);

                    } else {  //импост
                        double elemId = winNode.com5t().id();
                        GsonElem gsonElem = winc.listAll.gson(elemId);
                        if (sysprofRec.get(1) == null) {
                            gsonElem.param().remove(PKjson.sysprofID);
                        } else {
                            gsonElem.param().addProperty(PKjson.sysprofID, sysprofRec.getInt(eSysprof.id));
                        }
                        updateScript(selectID);
                    }
                }, qSysprofFilter);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.sysprofToFrame() " + e);
        }
    }//GEN-LAST:event_sysprofToFrame

    private void colorToFrame(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorToFrame
        try {
            double selectID = winNode.com5t().id();
            HashSet<Record> colorSet = new HashSet();
            //Все текстуры артикула элемента конструкции
            Field field = (evt.getSource() == btn18) ? eArtdet.mark_c1 : (evt.getSource() == btn19) ? eArtdet.mark_c2 : eArtdet.mark_c3;
            Query artdetList = new Query(eArtdet.values()).select(eArtdet.up, "where", eArtdet.artikl_id, "=", winNode.com5t().artiklRec().getInt(eArtikl.id));
            artdetList.forEach(rec -> {
                if (rec.getInt(field) == 1) {

                    if (rec.getInt(eArtdet.color_fk) < 0) { //все текстуры групы color_fk                       
                        eColor.query().forEach(rec2 -> {
                            if (rec2.getInt(eColor.groups_id) == rec.getInt(eArtdet.color_fk)) {
                                colorSet.add(rec2);
                            }
                        });
                    } else { //текстура color_fk 
                        colorSet.add(eColor.find(rec.getInt(eArtdet.color_fk)));
                    }
                }
            });
            DicColor frame = new DicColor(this, (colorRec) -> {

                String colorKey = (evt.getSource() == btn18) ? PKjson.colorID1 : (evt.getSource() == btn19) ? PKjson.colorID2 : PKjson.colorID3;
                double parentId = winNode.com5t().owner().id();
                GsonElem parentArea = (GsonElem) wincalc().listAll.gson(parentId);

                if (winNode.com5t().type() == enums.Type.STVORKA_SIDE) {
                    JsonObject paramObj = parentArea.param();
                    String stvKey = null;
                    if (winNode.com5t().layout() == Layout.BOTT) {
                        stvKey = PKjson.stvorkaBottom;
                    } else if (winNode.com5t().layout() == Layout.RIGHT) {
                        stvKey = PKjson.stvorkaRight;
                    } else if (winNode.com5t().layout() == Layout.TOP) {
                        stvKey = PKjson.stvorkaTop;
                    } else if (winNode.com5t().layout() == Layout.LEFT) {
                        stvKey = PKjson.stvorkaLeft;
                    }

                    JsonObject jso = UGui.getAsJsonObject(paramObj, stvKey);
                    if (colorRec.get(1) == null) {
                        jso.remove(colorKey);
                    } else {
                        jso.addProperty(colorKey, colorRec.getStr(eColor.id));
                    }
                    updateScript(selectID);

                } else if (winNode.com5t().type() == enums.Type.FRAME_SIDE) {
                    for (GsonElem elem : parentArea.childs()) {
                        if (elem.id() == ((DefMutableTreeNode) winNode).com5t().id()) {
                            if (colorRec.get(1) == null) {
                                elem.param().remove(colorKey);
                            } else {
                                elem.param().addProperty(colorKey, colorRec.getStr(eColor.id));
                            }
                            updateScript(selectID);
                        }
                    }
                } else if (winNode.com5t().type() == enums.Type.IMPOST
                        || winNode.com5t().type() == enums.Type.STOIKA
                        || winNode.com5t().type() == enums.Type.SHTULP) {
                    for (GsonElem elem : parentArea.childs()) {
                        if (elem.id() == ((DefMutableTreeNode) winNode).com5t().id()) {
                            if (colorRec.get(1) == null) {
                                elem.param().remove(colorKey);
                            } else {
                                elem.param().addProperty(colorKey, colorRec.getStr(eColor.id));
                            }
                            updateScript(selectID);
                        }
                    }
                }
            }, colorSet, true, false);
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorToFrame() " + e);
        }
    }//GEN-LAST:event_colorToFrame

    private void colorToWindows(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorToWindows
        try {
            double selectID = winNode.com5t().id();
            HashSet<Record> colorSet = new HashSet();

            String colorTxt = (evt.getSource() == btn9) ? txt3.getText() : (evt.getSource() == btn13) ? txt4.getText() : txt5.getText();
            Integer[] colorArr = UCom.parserInt(colorTxt);

            //Поле текстур заполнено
            if (colorArr.length != 0) {
                for (Record rec : eColor.query()) {
                    for (int i = 0; i < colorArr.length; i = i + 2) { //тестуры
                        if (rec.getInt(eColor.code) >= colorArr[i] && rec.getInt(eColor.code) <= colorArr[i + 1]) {
                            colorSet.add(rec);
                        }
                    }
                }
            }

            ListenerRecord listenerColor = (colorRec) -> {

                Wincalc winc = wincalc();
                builder.script.GsonElem rootArea = winc.listAll.gson(selectID);
                if (rootArea != null) {
                    if (evt.getSource() == btn9) {
                        winc.rootGson.color1(colorRec.getInt(eColor.id));
                    } else if (evt.getSource() == btn13) {
                        winc.rootGson.color2(colorRec.getInt(eColor.id));
                    } else {
                        winc.rootGson.color3(colorRec.getInt(eColor.id));
                    }
                    updateScript(selectID);
                }
            };
            if (colorArr.length == 0) {
                new DicColor(this, listenerColor, false, false);
            } else {
                new DicColor(this, listenerColor, colorSet, false, false);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorToWindows() " + e);
        }
    }//GEN-LAST:event_colorToWindows

    private void artiklToGlass(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_artiklToGlass
        try {
            double selectID = winNode.com5t().id();
            //Список доступных толщин в ветке системы например 4;5;8
            String depth = sysNode.rec().getStr(eSystree.depth);
            if (depth != null && depth.isEmpty() == false) {
                depth = depth.replace(";;;", ";").replace(";;", ";");
                depth = depth.replace(";", ",");
                if (depth.charAt(depth.length() - 1) == ',') {
                    depth = depth.substring(0, depth.length() - 1);
                }
            }
            //Список стеклопакетов
            depth = (depth != null && depth.isEmpty() == false) ? " and " + eArtikl.depth.name() + " in (" + depth + ")" : "";
            Query qArtikl = new Query(eArtikl.values()).select(eArtikl.up,
                    "where", eArtikl.level1, "= 5 and", eArtikl.level2, "in (1,2,3)", depth);

            new DicArtikl(this, (artiklRec) -> {

                GsonElem glassElem = (GsonElem) wincalc().listAll.gson(selectID);
                if (artiklRec.get(1) == null) {
                    glassElem.param().remove(PKjson.artglasID);
                } else {
                    glassElem.param().addProperty(PKjson.artglasID, artiklRec.getStr(eArtikl.id));
                }
                updateScript(selectID);

            }, qArtikl);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.artiklToGlass() " + e);
        }
    }//GEN-LAST:event_artiklToGlass

    private void sysfurnToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sysfurnToStvorka
        try {
            double windowsID = winNode.com5t().id();
            String systreeID = sysNode.rec().getStr(eSystree.id);
            Query qSysfurn = new Query(eSysfurn.values(), eFurniture.values()).select(eSysfurn.up, "left join", eFurniture.up, "on",
                    eSysfurn.furniture_id, "=", eFurniture.id, "where", eSysfurn.systree_id, "=", systreeID);

            new DicName(this, (sysfurnRec) -> {

                GsonElem stvArea = (GsonElem) wincalc().listAll.gson(windowsID);
                if (sysfurnRec.get(1) == null) {
                    stvArea.param().remove(PKjson.sysfurnID);
                } else {
                    stvArea.param().addProperty(PKjson.sysfurnID, sysfurnRec.getStr(eSysfurn.id));
                }
                updateScript(windowsID);

            }, qSysfurn, eFurniture.name);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.sysfurnToStvorka() " + e);
        }
    }//GEN-LAST:event_sysfurnToStvorka

    private void typeOpenToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeOpenToStvorka
        try {
            new DicEnums(this, (typeopenRec) -> {

                double elemID = winNode.com5t().id();
                GsonElem stvArea = (GsonElem) wincalc().listAll.gson(elemID);
                if (typeopenRec.get(1) == null) {
                    stvArea.param().remove(PKjson.typeOpen);
                } else {
                    stvArea.param().addProperty(PKjson.typeOpen, typeopenRec.getInt(0));
                }
                updateScript(elemID);

            }, TypeOpen1.INVALID, TypeOpen1.LEFT, TypeOpen1.LEFTUP, TypeOpen1.LEFTMOV,
                    TypeOpen1.RIGHT, TypeOpen1.RIGHTUP, TypeOpen1.RIGHTMOV, TypeOpen1.UPPER, TypeOpen1.FIXED);
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.typeOpenToStvorka() " + e);
        }
    }//GEN-LAST:event_typeOpenToStvorka

    private void handlToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_handlToStvorka
        try {
            double stvorkaID = winNode.com5t().id();
            int furnitureID = ((IStvorka) winNode.com5t()).sysfurnRec().getInt(eSysfurn.furniture_id);
            Query qArtikl = new Query(eArtikl.values()).select(eArtikl.up, "where", eArtikl.level1, "= 2 and", eArtikl.level2, " = 11");
            Query qResult = UGui.artTypeToFurndetList(furnitureID, qArtikl);
            new DicArtikl(this, (artiklRec) -> {

                GsonElem stvArea = (GsonElem) wincalc().listAll.gson(stvorkaID);
                stvArea.param().remove(PKjson.colorHandl);
                if (artiklRec.get(1) == null) {
                    stvArea.param().remove(PKjson.artiklHandl);
                    stvArea.param().remove(PKjson.colorHandl);
                } else {
                    stvArea.param().addProperty(PKjson.artiklHandl, artiklRec.getStr(eArtikl.id));
                }
                updateScript(stvorkaID);

            }, qResult);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.handlToStvorka() " + e);
        }
    }//GEN-LAST:event_handlToStvorka

    private void heightHandlToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_heightHandlToStvorka

        IArea5e areaStv = (IArea5e) winNode.com5t();
        int indexLayoutHandl = 0;
        if (LayoutHandle.CONST.name.equals(txt16.getText())) {
            indexLayoutHandl = 1;
        } else if (LayoutHandle.VARIAT.name.equals(txt16.getText())) {
            indexLayoutHandl = 2;
        }
        new DicHandl(this, (record) -> {
            try {
                double selectID = areaStv.id();
                GsonElem stvArea = (GsonElem) wincalc().listAll.gson(selectID);
                if (record.get(1) != null) {
                    if (record.getInt(0) == 0) {
                        stvArea.param().addProperty(PKjson.positionHandl, LayoutHandle.MIDL.id);
                        txt31.setEditable(false);

                    } else if (record.getInt(0) == 1) {
                        stvArea.param().addProperty(PKjson.positionHandl, LayoutHandle.CONST.id);
                        txt31.setEditable(false);

                    } else if (record.getInt(0) == 2) {
                        stvArea.param().addProperty(PKjson.positionHandl, LayoutHandle.VARIAT.id);
                        stvArea.param().addProperty(PKjson.heightHandl, record.getInt(1));
                        txt31.setEditable(true);
                    }
                    updateScript(selectID);
                }

            } catch (Exception e) {
                System.err.println("Ошибка:Systree.heightHandlToStvorka() " + e);
            }

        }, indexLayoutHandl);
    }//GEN-LAST:event_heightHandlToStvorka

    private void colorToHandl(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorToHandl
        try {
            double selectID = winNode.com5t().id();
            IStvorka stv = (IStvorka) winNode.com5t();
            HashSet<Record> colorSet = UGui.artiklToColorSet(stv.handleRec().getInt(eArtikl.id));
            DicColor frame = new DicColor(this, (colorRec) -> {

                GsonElem stvArea = (GsonElem) wincalc().listAll.gson(selectID);
                if (colorRec.get(1) == null) {
                    stvArea.param().remove(PKjson.colorHandl);
                } else {
                    stvArea.param().addProperty(PKjson.colorHandl, colorRec.getStr(eColor.id));
                }
                updateScript(selectID);

            }, colorSet, true, false);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorToHandl() " + e);
        }
    }//GEN-LAST:event_colorToHandl

    private void joinToFrame(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_joinToFrame
        try {
            Wincalc winc = wincalc();
            if (winNode != null) {
                DefMutableTreeNode nodeParent = (DefMutableTreeNode) winNode.getParent();
                IElem5e elem5e = (IElem5e) nodeParent.com5t();
                JButton btn = (JButton) evt.getSource();
                int point = (btn.getName().equals("btn26")) ? 0 : (btn.getName().equals("btn27")) ? 1 : 2;
                ElemJoining elemJoin = winc.listJoin.get(elem5e, point);
                App.Joining.createFrame(Systree.this, elemJoin);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.joinToFrame() " + e);
        }
    }//GEN-LAST:event_joinToFrame

    private void loopToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loopToStvorka
        try {
            double selectID = winNode.com5t().id();
            int furnitureID = ((IStvorka) winNode.com5t()).sysfurnRec().getInt(eSysfurn.furniture_id);
            Query qArtikl = new Query(eArtikl.values()).select(eArtikl.up, "where", eArtikl.level1, "= 2 and", eArtikl.level2, " = 12");
            Query qResult = UGui.artTypeToFurndetList(furnitureID, qArtikl);
            new DicArtikl(this, (artiklRec) -> {

                GsonElem stvArea = (GsonElem) wincalc().listAll.gson(selectID);
                stvArea.param().remove(PKjson.colorLoop);
                if (artiklRec.get(1) == null) {
                    stvArea.param().remove(PKjson.artiklLoop);
                    stvArea.param().remove(PKjson.colorLoop);
                } else {
                    stvArea.param().addProperty(PKjson.artiklLoop, artiklRec.getStr(eArtikl.id));
                }
                updateScript(selectID);

            }, qResult);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.loopToStvorka() " + e);
        }
    }//GEN-LAST:event_loopToStvorka

    private void lockToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lockToStvorka
        try {
            double selectID = winNode.com5t().id();
            int furnitureID = ((IStvorka) winNode.com5t()).sysfurnRec().getInt(eSysfurn.furniture_id);
            Query qArtikl = new Query(eArtikl.values()).select(eArtikl.up, "where", eArtikl.level1, "= 2 and", eArtikl.level2, " = 9");
            Query qResult = UGui.artTypeToFurndetList(furnitureID, qArtikl);
            new DicArtikl(this, (artiklRec) -> {

                GsonElem stvArea = (GsonElem) wincalc().listAll.gson(selectID);
                stvArea.param().remove(PKjson.colorLock);
                if (artiklRec.get(1) == null) {
                    stvArea.param().remove(PKjson.artiklLock);
                    stvArea.param().remove(PKjson.colorLock);
                } else {
                    stvArea.param().addProperty(PKjson.artiklLock, artiklRec.getStr(eArtikl.id));
                }
                updateScript(selectID);

            }, qResult);

        } catch (Exception e) {
            System.err.println("Ошибка:frames.lockToStvorka " + e);
        }
    }//GEN-LAST:event_lockToStvorka

    private void colorFromLoop(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorFromLoop
        try {
            double selectID = winNode.com5t().id();
            IStvorka stv = (IStvorka) winNode.com5t();
            HashSet<Record> colorSet = UGui.artiklToColorSet(stv.loopRec().getInt(eArtikl.id));
            DicColor frame = new DicColor(this, (colorRec) -> {

                GsonElem stvArea = (GsonElem) wincalc().listAll.gson(selectID);
                if (colorRec.get(1) == null) {
                    stvArea.param().remove(PKjson.colorLoop);
                } else {
                    stvArea.param().addProperty(PKjson.colorLoop, colorRec.getStr(eColor.id));
                }
                updateScript(selectID);

            }, colorSet, true, false);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorToHandl() " + e);
        }
    }//GEN-LAST:event_colorFromLoop

    private void colorFromLock(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorFromLock
        try {
            double selectID = winNode.com5t().id();
            IStvorka stv = (IStvorka) winNode.com5t();
            HashSet<Record> colorSet = UGui.artiklToColorSet(stv.lockRec().getInt(eArtikl.id));
            DicColor frame = new DicColor(this, (colorRec) -> {

                GsonElem stvArea = (GsonElem) wincalc().listAll.gson(selectID);
                if (colorRec.get(1) == null) {
                    stvArea.param().remove(PKjson.colorLock);
                } else {
                    stvArea.param().addProperty(PKjson.colorLock, colorRec.getStr(eColor.id));
                }
                updateScript(selectID);

            }, colorSet, true, false);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorToHandl() " + e);
        }
    }//GEN-LAST:event_colorFromLock

    private void btnTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTestActionPerformed
        Wincalc win = wincalc();
        String json = win.rootGson.toJson();
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(new com.google.gson.JsonParser().parse(json))); //для тестирования
    }//GEN-LAST:event_btnTestActionPerformed

    private void colorFromGlass(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorFromGlass
        try {
            double selectID = winNode.com5t().id();
            IElem5e glas = (IElem5e) winNode.com5t();
            HashSet<Record> colorSet = UGui.artiklToColorSet(glas.artiklRec().getInt(eArtikl.id));
            DicColor frame = new DicColor(this, (colorRec) -> {

                GsonElem stvArea = (GsonElem) wincalc().listAll.gson(selectID);
                if (colorRec.get(1) == null) {
                    stvArea.param().remove(PKjson.colorGlass);
                } else {
                    stvArea.param().addProperty(PKjson.colorGlass, colorRec.getStr(eColor.id));
                }
                updateScript(selectID);

            }, colorSet, false, false);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorFromGlass() " + e);
        }
    }//GEN-LAST:event_colorFromGlass

    private void btnMove(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMove
        JButton btn = (JButton) evt.getSource();
        int index = UGui.getIndexRec(UGui.tableFromBorder(tab2, tab3, tab5)), index2 = index;
        if (index != -1) {
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
                qSysprof.execsql();

            } else if (tab3.getBorder() != null) {
                if (tab3.getBorder() != null) {
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
                }
                qSysprof.execsql();

            } else if (tab5.getBorder() != null) {
                if (tab5.getBorder() != null) {
                    if (btn == btnMoveD && tab5.getSelectedRow() < tab5.getRowCount() - 1) {
                        Collections.swap(qSysprod, index, ++index2);

                    } else if (btn == btnMoveU && tab5.getSelectedRow() > 0) {
                        Collections.swap(qSysprod, index, --index2);
                    }
                    IntStream.range(0, qSysprod.size()).forEach(i -> qSysprod.set(i + 1, i, eSysprod.npp));
                    ((DefaultTableModel) tab5.getModel()).fireTableDataChanged();
                    UGui.setSelectedIndex(tab5, index2);
                }
                qSysprod.execsql();
            }
        }
    }//GEN-LAST:event_btnMove

    private void mosquitToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mosquitToStvorka
        try {
            IArea5e stvElem = (IArea5e) winNode.com5t();
            double selectID = winNode.com5t().id();
            Query qArtikl = new Query(eArtikl.values()).select(eArtikl.up,
                    "where", eArtikl.level1, "= 5 and", eArtikl.level2, "= 20");

            new DicArtikl(this, (artiklRec) -> {

                GsonElem gsonElem = null;
                LinkedCom<ICom5t> mosqList = ((IArea5e) stvElem).childs().filter(enums.Type.MOSKITKA);

                if (mosqList.isEmpty() == false) {
                    IElem5e mosqElem = (IElem5e) mosqList.get(0);
                    gsonElem = mosqElem.gson();
                } else {
                    gsonElem = new GsonElem(enums.Type.MOSKITKA);
                    GsonElem stvArea = stvElem.gson();
                    stvArea.childs().add(gsonElem);
                }
                if (artiklRec.get(1) == null) {
                    gsonElem.param().remove(PKjson.artiklID);
                    gsonElem.param().remove(PKjson.elementID);
                } else {
                    gsonElem.param().addProperty(PKjson.artiklID, artiklRec.getStr(eArtikl.id));
                }
                updateScript(selectID);

            }, qArtikl);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.mosquitToStvorka() " + e);
        }
    }//GEN-LAST:event_mosquitToStvorka

    private void mosqToElements(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mosqToElements
        try {
            double selectID = winNode.com5t().id();
            IArea5e stvElem = (IArea5e) winNode.com5t();
            LinkedCom<ICom5t> mosqList = ((IArea5e) stvElem).childs().filter(enums.Type.MOSKITKA);
            if (mosqList.isEmpty() == false) {
                IElem5e mosqElem = (IElem5e) mosqList.get(0);
                Record artiklRec = mosqElem.artiklRec();
                Query qElements = new Query(eElement.values()).select(eElement.up,
                        "where", eElement.artikl_id, "=", artiklRec.getInt(eArtikl.id));

                new DicName(this, (elementRec) -> {

                    if (elementRec.get(1) == null) {
                        mosqElem.gson().param().remove(PKjson.elementID);
                    } else {
                        mosqElem.gson().param().addProperty(PKjson.elementID, elementRec.getStr(eElement.id));
                    }
                    updateScript(selectID);

                }, qElements, eElement.name);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.mosqToElements() " + e);
        }
    }//GEN-LAST:event_mosqToElements

    private void btnFind2(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFind2
        if (tab2.getBorder() != null) {
            JOptionPane.showMessageDialog(Systree.this, "В разработке...");
//            Record sysprofRec = qSysprof.get(UGui.getIndexRec(tab2));
//            FrameProgress.create(this, new ListenerFrame() {
//                public void actionRequest(Object obj) {
//                    App.Element.createFrame(Systree.this, Set.of(sysprofRec.get(eSysprof.artikl_id)));
//                }
//            });
        } else if (tab3.getBorder() != null) {
            Record sysfurnRec = qSysfurn.get(UGui.getIndexRec(tab3));
            ProgressBar.create(this, new ListenerFrame() {
                public void actionRequest(Object obj) {
                    App.Furniture.createFrame(Systree.this, Set.of(sysfurnRec.get(eSysfurn.furniture_id)));
                }
            });
        }
    }//GEN-LAST:event_btnFind2

    private void rascladkaToGlass(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rascladkaToGlass
        try {
            IElem5e classElem = (IElem5e) winNode.com5t();
            double selectID = winNode.com5t().id();
            Query qArtikl = new Query(eArtikl.values()).select(eArtikl.up,
                    "where", eArtikl.level1, "= 1 and", eArtikl.level2, "= 12");

            new DicArtikl(this, (artiklRec) -> {

                classElem.gson().param().addProperty(PKjson.artiklRascl, artiklRec.getStr(eArtikl.id));
                updateScript(selectID);

            }, qArtikl);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.rascladkaToGlass() " + e);
        }
    }//GEN-LAST:event_rascladkaToGlass

    private void colorToRascladka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorToRascladka
        try {
            double selectID = winNode.com5t().id();
            IElem5e glas = (IElem5e) winNode.com5t();
            HashSet<Record> colorSet = UGui.artiklToColorSet(glas.rasclRec().getInt(eArtikl.id));
            DicColor frame = new DicColor(this, (colorRec) -> {

                GsonElem glassElem = (GsonElem) wincalc().listAll.gson(selectID);
                if (colorRec.get(1) == null) {
                    glassElem.param().remove(PKjson.colorRascl);
                } else {
                    glassElem.param().addProperty(PKjson.colorRascl, colorRec.getStr(eColor.id));
                }
                updateScript(selectID);

            }, colorSet, false, false);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorToRascladka() " + e);
        }
    }//GEN-LAST:event_colorToRascladka

    private void spinHorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinHorStateChanged
        double selectID = winNode.com5t().id();
        GsonElem glassElem = (GsonElem) wincalc().listAll.gson(selectID);
        glassElem.param().addProperty(PKjson.rasclHor, spinHor.getValue().toString());
        updateScript(selectID);
    }//GEN-LAST:event_spinHorStateChanged

    private void spinVertStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinVertStateChanged
        double selectID = winNode.com5t().id();
        GsonElem glassElem = (GsonElem) wincalc().listAll.gson(selectID);
        glassElem.param().addProperty(PKjson.rasclVert, spinVert.getValue().toString());
        updateScript(selectID);
    }//GEN-LAST:event_spinVertStateChanged

    private void mosqToColor(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mosqToColor
        try {
            double selectID = winNode.com5t().id();
            IStvorka stvElem = (IStvorka) winNode.com5t();
            LinkedCom<ICom5t> mosqList = ((IArea5e) stvElem).childs().filter(enums.Type.MOSKITKA);
            if (mosqList.isEmpty() == false) {
                ElemMosquit mosqElem = (ElemMosquit) mosqList.get(0);
                HashSet<Record> colorSet = UGui.artiklToColorSet(mosqElem.artiklRec().getInt(eArtikl.id));
                DicColor frame = new DicColor(this, (colorRec) -> {

                    if (colorRec.get(1) == null) {
                        mosqElem.gson().param().remove(PKjson.colorID1);
                    } else {
                        mosqElem.gson().param().addProperty(PKjson.colorID1, colorRec.getStr(eColor.id));
                    }
                    updateScript(selectID);

                }, colorSet, true, false);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorToHandl() " + e);
        }
    }//GEN-LAST:event_mosqToColor

    private void ppmActionItems(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppmActionItems
        if (evt.getSource() == mInsert) {
            btnInsert(new java.awt.event.ActionEvent(mInsert, -1, ""));
        } else if (evt.getSource() == mDelit) {
            btnDelete(new java.awt.event.ActionEvent(btnDel, -1, ""));
        }
    }//GEN-LAST:event_ppmActionItems

    private void tabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabMouseClicked
        JTable table = (JTable) evt.getSource();
        UGui.updateBorderAndSql(table, List.of(tab2, tab3, tab4, tab5));
        sysTree.setBorder(null);
        if (sysTree.isEditing()) {
            sysTree.getCellEditor().stopCellEditing();
        }
        if (evt.getButton() == MouseEvent.BUTTON3) {
            ppmCrud.show(table, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tabMouseClicked

// <editor-fold defaultstate="collapsed" desc="Generated Code"> 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn10;
    private javax.swing.JButton btn11;
    private javax.swing.JButton btn12;
    private javax.swing.JButton btn13;
    private javax.swing.JButton btn14;
    private javax.swing.JButton btn15;
    private javax.swing.JButton btn16;
    private javax.swing.JButton btn17;
    private javax.swing.JButton btn18;
    private javax.swing.JButton btn19;
    private javax.swing.JButton btn2;
    private javax.swing.JButton btn20;
    private javax.swing.JButton btn21;
    private javax.swing.JButton btn22;
    private javax.swing.JButton btn23;
    private javax.swing.JButton btn24;
    private javax.swing.JButton btn25;
    private javax.swing.JButton btn26;
    private javax.swing.JButton btn27;
    private javax.swing.JButton btn28;
    private javax.swing.JButton btn29;
    private javax.swing.JButton btn3;
    private javax.swing.JButton btn31;
    private javax.swing.JButton btn32;
    private javax.swing.JButton btn4;
    private javax.swing.JButton btn5;
    private javax.swing.JButton btn6;
    private javax.swing.JButton btn7;
    private javax.swing.JButton btn9;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnFind1;
    private javax.swing.JButton btnFind2;
    private javax.swing.JButton btnIns;
    private javax.swing.JButton btnMoveD;
    private javax.swing.JButton btnMoveU;
    private javax.swing.JButton btnRef;
    private javax.swing.JButton btnReport1;
    private javax.swing.JButton btnTest;
    private javax.swing.JPanel centr;
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
    private javax.swing.JLabel lab20;
    private javax.swing.JLabel lab24;
    private javax.swing.JLabel lab25;
    private javax.swing.JLabel lab26;
    private javax.swing.JLabel lab27;
    private javax.swing.JLabel lab28;
    private javax.swing.JLabel lab29;
    private javax.swing.JLabel lab30;
    private javax.swing.JLabel lab31;
    private javax.swing.JLabel lab32;
    private javax.swing.JLabel lab33;
    private javax.swing.JLabel lab34;
    private javax.swing.JLabel lab35;
    private javax.swing.JLabel lab36;
    private javax.swing.JLabel lab37;
    private javax.swing.JLabel lab38;
    private javax.swing.JLabel lab39;
    private javax.swing.JLabel lab40;
    private javax.swing.JLabel lab41;
    private javax.swing.JLabel lab42;
    private javax.swing.JLabel lab43;
    private javax.swing.JLabel lab44;
    private javax.swing.JLabel lab45;
    private javax.swing.JLabel lab46;
    private javax.swing.JLabel lab47;
    private javax.swing.JLabel lab48;
    private javax.swing.JLabel lab49;
    private javax.swing.JLabel lab50;
    private javax.swing.JLabel lab51;
    private javax.swing.JLabel lab52;
    private javax.swing.JLabel lab53;
    private javax.swing.JLabel lab54;
    private javax.swing.JLabel lab55;
    private javax.swing.JLabel lab56;
    private javax.swing.JLabel lab57;
    private javax.swing.JLabel lab58;
    private javax.swing.JLabel lab59;
    private javax.swing.JLabel lab60;
    private javax.swing.JLabel lab61;
    private javax.swing.JLabel lab62;
    private javax.swing.JLabel lab63;
    private javax.swing.JLabel lab64;
    private javax.swing.JLabel lab65;
    private javax.swing.JLabel lab66;
    private javax.swing.JLabel lab67;
    private javax.swing.JLabel lab68;
    private javax.swing.JLabel lab69;
    private javax.swing.JLabel lab70;
    private javax.swing.JMenuItem mDelit;
    private javax.swing.JMenuItem mInsert;
    private javax.swing.JPanel pan1;
    private javax.swing.JPanel pan10;
    private javax.swing.JPanel pan11;
    private javax.swing.JPanel pan12;
    private javax.swing.JPanel pan13;
    private javax.swing.JPanel pan15;
    private javax.swing.JPanel pan16;
    private javax.swing.JPanel pan17;
    private javax.swing.JPanel pan18;
    private javax.swing.JPanel pan19;
    private javax.swing.JPanel pan2;
    private javax.swing.JPanel pan20;
    private javax.swing.JPanel pan21;
    private javax.swing.JPanel pan22;
    private javax.swing.JPanel pan23;
    private javax.swing.JPanel pan3;
    private javax.swing.JPanel pan4;
    private javax.swing.JPanel pan5;
    private javax.swing.JPanel pan6;
    private javax.swing.JPanel pan7;
    private javax.swing.JPanel panDesign;
    private javax.swing.JPanel panSspinner;
    private javax.swing.JPopupMenu ppmCrud;
    private javax.swing.JScrollPane scr1;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JScrollPane scr3;
    private javax.swing.JScrollPane scr4;
    private javax.swing.JScrollPane scr5;
    private javax.swing.JScrollPane scr6;
    private javax.swing.JScrollPane scr7;
    private javax.swing.JPanel south;
    private javax.swing.JSpinner spinHor;
    private javax.swing.JSpinner spinVert;
    private javax.swing.JSpinner spinner;
    private javax.swing.JTree sysTree;
    private javax.swing.JTable tab2;
    private javax.swing.JTable tab3;
    private javax.swing.JTable tab4;
    private javax.swing.JTable tab5;
    private javax.swing.JTable tab7;
    private javax.swing.JTabbedPane tabb1;
    private javax.swing.JTabbedPane tabb2;
    private javax.swing.JPanel tool;
    private javax.swing.JTextField txt1;
    private javax.swing.JTextField txt11;
    private javax.swing.JTextField txt12;
    private javax.swing.JTextField txt13;
    private javax.swing.JTextField txt14;
    private javax.swing.JTextField txt16;
    private javax.swing.JTextField txt17;
    private javax.swing.JTextField txt18;
    private javax.swing.JTextField txt19;
    private javax.swing.JTextField txt2;
    private javax.swing.JTextField txt20;
    private javax.swing.JTextField txt21;
    private javax.swing.JTextField txt22;
    private javax.swing.JTextField txt23;
    private javax.swing.JTextField txt24;
    private javax.swing.JTextField txt25;
    private javax.swing.JTextField txt26;
    private javax.swing.JTextField txt27;
    private javax.swing.JTextField txt28;
    private javax.swing.JTextField txt29;
    private javax.swing.JTextField txt3;
    private javax.swing.JTextField txt30;
    private javax.swing.JTextField txt31;
    private javax.swing.JTextField txt32;
    private javax.swing.JTextField txt33;
    private javax.swing.JTextField txt34;
    private javax.swing.JTextField txt35;
    private javax.swing.JTextField txt36;
    private javax.swing.JTextField txt37;
    private javax.swing.JTextField txt38;
    private javax.swing.JTextField txt39;
    private javax.swing.JTextField txt4;
    private javax.swing.JTextField txt40;
    private javax.swing.JTextField txt41;
    private javax.swing.JTextField txt42;
    private javax.swing.JTextField txt43;
    private javax.swing.JTextField txt44;
    private javax.swing.JTextField txt45;
    private javax.swing.JTextField txt46;
    private javax.swing.JTextField txt47;
    private javax.swing.JTextField txt48;
    private javax.swing.JTextField txt49;
    private javax.swing.JTextField txt5;
    private javax.swing.JTextField txt50;
    private javax.swing.JTextField txt51;
    private javax.swing.JTextField txt54;
    private javax.swing.JTextField txt55;
    private javax.swing.JTextField txt56;
    private javax.swing.JTextField txt57;
    private javax.swing.JTextField txt58;
    private javax.swing.JTextField txt59;
    private javax.swing.JTextField txt60;
    private javax.swing.JTextField txt7;
    private javax.swing.JTextField txt8;
    private javax.swing.JTextField txt9;
    private javax.swing.JTree winTree;
    // End of variables declaration//GEN-END:variables
// </editor-fold> 

    public final void initElements() {

        new FrameToFile(this, btnClose);
        new UColor();

        TableFieldFilter filterTable = new TableFieldFilter(0, tab2, tab3, tab4, tab5, tab7);
        south.add(filterTable, 0);
        filterTable.getTxt().grabFocus();

        panDesign.add(scene, java.awt.BorderLayout.CENTER);
        UGui.setDocumentFilter(3, txt17, txt22, txt23, txt24, txt26);
        List.of(btnIns, btnDel, btnRef).forEach(b -> b.addActionListener(l -> UGui.stopCellEditing(tab2, tab3, tab4, tab5)));
        DefaultTreeCellRenderer rnd = (DefaultTreeCellRenderer) sysTree.getCellRenderer();
        rnd.setLeafIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b038.gif")));
        rnd.setOpenIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b007.gif")));
        rnd.setClosedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b006.gif")));
        DefaultTreeCellRenderer rnd2 = (DefaultTreeCellRenderer) winTree.getCellRenderer();
        rnd2.setLeafIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b038.gif")));
        rnd2.setOpenIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b007.gif")));
        rnd2.setClosedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b006.gif")));
        sysTree.getSelectionModel().addTreeSelectionListener(tse -> selectionTree1());
        winTree.getSelectionModel().addTreeSelectionListener(tse -> selectionTree2());
        tab4.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    UGui.setSelectedIndex(tab7, UGui.getIndexRec(tab4));
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
        tab5.getSelectionModel().addListSelectionListener(it -> selectionTab5());
        DefaultTreeModel model = (DefaultTreeModel) winTree.getModel();
        ((DefaultMutableTreeNode) model.getRoot()).removeAllChildren();
        model.reload();
    }

    private void testBimax() {
        try {
            Object prj = JOptionPane.showInputDialog(Systree.this, "Номер проекта", "Проект", JOptionPane.QUESTION_MESSAGE);
            if (prj != null) {
                Record record = eSysprod.up.newRecord(Query.INS);
                record.set(eSysprod.id, Conn.genId(eSysprod.up));
                record.set(eSysprod.npp, record.get(eSysprod.id));
                String json = GsonScript.productJson(Integer.valueOf(prj.toString()));
                GsonRoot gsonRoot = new Gson().fromJson(json, GsonRoot.class);
                record.set(eSysprod.name, "Kod:" + prj + "* " + gsonRoot.name());
                record.set(eSysprod.script, json);
                record.set(eSysprod.systree_id, systreeID);
                qSysprod.insert(record);

                loadingTab5();
                UGui.setSelectedIndex(tab5, qSysprod.size() - 1);
                UGui.scrollRectToIndex(qSysprod.size() - 1, tab5);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.testBimax()");
        }
    }
}
