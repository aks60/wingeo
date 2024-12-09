package frames;

import builder.Kitcalc;
import frames.swing.ProgressBar;
import frames.swing.FrameToFile;
import frames.dialog.DicCurrenc;
import builder.model.Com5t;
import builder.Wincalc;
import builder.making.TFurniture;
import builder.making.TJoining;
import builder.making.TRecord;
import builder.making.TTariffic;
import builder.making.UColor;
import builder.model.AreaSimple;
import builder.model.AreaStvorka;
import builder.model.ElemJoining;
import builder.model.ElemMosquit;
import builder.model.ElemSimple;
import builder.script.GsonElem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import common.UCom;
import dataset.Field;
import dataset.Query;
import dataset.Record;
import domain.eProject;
import domain.ePrjpart;
import frames.dialog.DicDate;
import javax.swing.JTable;
import frames.swing.DefTableModel;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashSet;
import javax.swing.table.DefaultTableModel;
import common.eProfile;
import common.eProp;
import common.listener.ListenerAction;
import common.listener.ListenerFrame;
import common.listener.ListenerRecord;
import dataset.Conn;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eCurrenc;
import domain.eElement;
import domain.eFurniture;
import domain.eGroups;
import domain.eJoining;
import domain.eJoinvar;
import domain.eParams;
import domain.ePrjkit;
import domain.ePrjprod;
import domain.eSysfurn;
import domain.eSyspar1;
import domain.eSysprod;
import domain.eSysprof;
import domain.eSystree;
import enums.Layout;
import enums.LayoutKnob;
import enums.PKjson;
import enums.TypeGrup;
import enums.TypeOpen1;
import enums.UseSideTo;
import frames.dialog.DicArtikl;
import frames.dialog.DicArtikl2;
import frames.dialog.DicColor;
import frames.dialog.DicEnums;
import frames.dialog.DicHandl;
import frames.dialog.DicKits;
import frames.dialog.DicName;
import frames.dialog.DicPrjprod;
import frames.dialog.DicSyspod;
import frames.dialog.DicSysprof;
import frames.dialog.ParDefVal;
import frames.swing.Canvas;
import frames.swing.DefMutableTreeNode;
import frames.swing.Scene;
import frames.swing.TableFieldFilter;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import report.RCheck;
import report.RMaterial;
import report.ROffer;
import report.RSmeta;
import report.RSpecific;
import startup.App;
import common.listener.ListenerReload;
import static dataset.Query.INS;
import java.util.ArrayList;
import java.util.stream.Collectors;
import report.RTarget;

public class Orders extends javax.swing.JFrame implements ListenerReload, ListenerAction {

    private ImageIcon icon = new ImageIcon(getClass().getResource("/resource/img16/b031.gif"));
    private Query qGroups = new Query(eGroups.values());
    private Query qParams = new Query(eParams.values());
    private Query qCurrenc = new Query(eCurrenc.values());
    private Query qProjectAll = new Query(eProject.values());
    private Query qProject = new Query(eProject.values());
    private Query qPrjpart = new Query(ePrjpart.values());
    private Query qPrjprod = new Query(ePrjprod.values());
    private Query qPrjkit = new Query(ePrjkit.values(), eArtikl.values());
    private Query qSyspar1 = new Query(eSyspar1.values());
    private DefMutableTreeNode winNode = null;
    private Canvas canvas = new Canvas();
    private Scene scene = null;
    Object[] column = new String[]{"", "Скидка (%)", "Без скидок", "Со скидкой"};
    private Gson gson = new GsonBuilder().create();
    DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            value = (value instanceof Double) ? UCom.format(value, -9) : value;
            JLabel lab = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            lab.setBackground(new java.awt.Color(212, 208, 200));
            return lab;
        }
    };

    public Orders(boolean menureport) {
        initComponents();
        scene = new Scene(canvas, this, this);
        btnReport.setVisible(menureport);
        initElements();
        loadingData();
        loadingModel();
        listenerAdd();
        loadingTab1();
    }

    private void loadingData() {
        qGroups.sq2(eGroups.data(), eGroups.grup, TypeGrup.PARAM_USER.id, eGroups.grup, TypeGrup.COLOR_MAP.id);
        qParams.sql(eParams.data(), eParams.up);
        qCurrenc.sql(eCurrenc.data(), eCurrenc.up).sort(eCurrenc.name);
        qProjectAll.sql(eProject.data(), eProject.up).sort(eProject.date4);
        qPrjpart.sql(ePrjpart.data(), ePrjpart.up);
    }

    public void loadingModel() {
        new DefTableModel(tab1, qProject, eProject.num_ord, eProject.num_acc, eProject.date4, eProject.date5, eProject.date6, eProject.prjpart_id, eProject.manager) {
            @Override
            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (field == eProject.prjpart_id) {
                    Record record = qPrjpart.stream().filter(rec -> rec.get(ePrjpart.id).equals(val)).findFirst().orElse(ePrjpart.up.newRecord(Query.SEL));
                    return record.get(ePrjpart.partner);
                }
                return val;
            }
        };
        new DefTableModel(tab2, qPrjprod, ePrjprod.name, ePrjprod.num, ePrjprod.id);
        new DefTableModel(tab3, qSyspar1, eSyspar1.groups_id, eSyspar1.text) {
            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (val != null && field == eSyspar1.groups_id) {
                    return qGroups.find(val, eGroups.id).getDev(eGroups.name, val);
                }
                return val;
            }
        };
        new DefTableModel(tab4, qPrjkit, ePrjkit.prjprod_id, eArtikl.code, eArtikl.name, ePrjkit.color1_id, ePrjkit.color2_id,
                ePrjkit.color3_id, ePrjkit.width, ePrjkit.height, ePrjkit.numb, ePrjkit.angl1, ePrjkit.angl2) {

            public Object getValueAt(int col, int row, Object val) {

                if (columns[col] == ePrjkit.prjprod_id) {
                    if (val != null) {
                        return qPrjprod.stream().filter(rec -> val.equals(rec.get(ePrjprod.id)))
                                .findFirst().orElse(ePrjprod.up.newRecord(Query.SEL)).getStr(ePrjprod.name);
                    } else {
                        return "Заказ № " + qProject.get(UGui.getIndexRec(tab1)).get(eProject.num_ord);
                    }
                } else if (val != null && columns[col] == ePrjkit.color1_id) {
                    return eColor.find((int) val).getStr(eColor.name);

                } else if (val != null && columns[col] == ePrjkit.color2_id) {
                    return eColor.find((int) val).getStr(eColor.name);

                } else if (val != null && columns[col] == ePrjkit.color3_id) {
                    return eColor.find((int) val).getStr(eColor.name);

                }
                return val;
            }
        };

        DefaultTableCellRenderer defaultTableDateRenderer = new DefaultTableCellRenderer() {

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                Field field = ((DefTableModel) table.getModel()).columns[column];
                if (field.meta().type() == Field.TYPE.DATE) {
                    value = UGui.DateToStr(value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        tab1.getColumnModel().getColumn(2).setCellRenderer(defaultTableDateRenderer);
        tab1.getColumnModel().getColumn(3).setCellRenderer(defaultTableDateRenderer);
        tab1.getColumnModel().getColumn(4).setCellRenderer(defaultTableDateRenderer);
        DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer() {

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 2) {
                    Record rec = qPrjprod.get(row);
                    if (rec.size() > ePrjprod.values().length) {
                        Object v = rec.get(ePrjprod.values().length);
                        if (v instanceof Wincalc) {
                            label.setIcon(((Wincalc) v).imageIcon);
                        }
                    }
                } else {
                    label.setVerticalAlignment(javax.swing.SwingConstants.TOP);
                    label.setIcon(null);
                }
                return label;
            }
        };
        tab2.setDefaultRenderer(Object.class, defaultTableCellRenderer);
    }

    public void loadingTab1() {
        qProject.clear();
        int first = (btnF1.isSelected()) ? qProjectAll.size() - 10 : (btnF2.isSelected()) ? qProjectAll.size() - 30 : 0;
        first = (first < 0) ? 0 : first;
        for (int i = first; i < qProjectAll.size(); ++i) {
            qProject.add(qProjectAll.get(i));
        }
        //Выделяем заказ если сохранён в Property
        int orderID = Integer.valueOf(eProp.orderID.read());
        ((DefTableModel) tab1.getModel()).fireTableDataChanged();
        int index = -1;
        for (int index2 = 0; index2 < qProject.size(); ++index2) {
            if (qProject.get(index2).getInt(ePrjprod.id) == orderID) {
                index = index2;
            }
        }
        if (index != -1) {
            UGui.setSelectedIndex(tab1, index);
            UGui.scrollRectToIndex(index, tab1);
        } else {
            UGui.setSelectedRow(tab1);
        }
    }

    public void loadingTab2() {
        int index = -1;
        UGui.stopCellEditing(tab1, tab2, tab3, tab4);
        List.of(qProject, qPrjprod, qPrjkit).forEach(q -> q.execsql());
        if (tab1.getSelectedRow() != -1) {

            Record projectRec = qProject.get(UGui.getIndexRec(tab1));
            int id = projectRec.getInt(eProject.id);
            qPrjprod.sql(ePrjprod.data(), ePrjprod.project_id, id);

            for (Record record : qPrjprod) {
                try {
                    String script = record.getStr(ePrjprod.script);
                    Wincalc iwin2 = new Wincalc(script);
                    iwin2.imageIcon = Canvas.createIcon(iwin2, 68);
                    record.add(iwin2);

                } catch (Exception e) {
                    System.err.println("Ошибка:Order.loadingTab2() " + e);
                }
            }
            ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();

            //Выделяем конструкцию если сохранена в Property
            int prjprodID = Integer.valueOf(eProp.prjprodID.read());
            for (int i = 0; i < qPrjprod.size(); ++i) {
                if (qPrjprod.get(i).getInt(ePrjprod.id) == prjprodID) {
                    index = i;
                }
            }
            if (index != -1) {
                UGui.setSelectedIndex(tab2, index);
            } else {
                UGui.setSelectedRow(tab2);
            }
        }
    }

    public void loadingTab4() {
        UGui.stopCellEditing(tab1, tab2, tab3, tab4);
        UGui.updateBorderAndSql(tab4, List.of(tab1, tab2, tab3, tab4));
        Record projectRec = qProject.get(UGui.getIndexRec(tab1));

        int id = projectRec.getInt(eProject.id);
        qPrjkit.sql(ePrjkit.data(), ePrjkit.project_id, id);
        qPrjkit.table(eArtikl.up).join2(qPrjkit, eArtikl.data(), ePrjkit.artikl_id, eArtikl.id);
        ((DefaultTableModel) tab4.getModel()).fireTableDataChanged();
        UGui.setSelectedRow(tab4);
    }

    public void loadingTab5() {
        Record projectRec = qProject.get(UGui.getIndexRec(tab1));

        Object data[][] = {
            {" Конструкции", projectRec.getDbl(eProject.disc2, 0), projectRec.getDbl(eProject.price1a, 0), projectRec.getDbl(eProject.price2a, 0)},
            {" Комплектации", projectRec.getDbl(eProject.disc3, 0), projectRec.getDbl(eProject.price1b, 0), projectRec.getDbl(eProject.price2b, 0)},
            {" Итого за заказ", projectRec.getDbl(eProject.disc4, 0), projectRec.getDbl(eProject.price1c, 0), projectRec.getDbl(eProject.price2c, 0)}};

        ((DefaultTableModel) tab5.getModel()).setDataVector(data, column);
        tab5.getColumnModel().getColumn(2).setCellRenderer(defaultTableCellRenderer);
        tab5.getColumnModel().getColumn(3).setCellRenderer(defaultTableCellRenderer);
    }

    public void loadingTree(Wincalc winc) {
        try {
            DefMutableTreeNode root = UTree.loadWinTree(winc);
            winTree.setModel(new DefaultTreeModel(root));

        } catch (Exception e) {
            System.err.println("Ошибка:Order.loadingWinTree() " + e);
        }
    }

    public void selectionTab1() {

        UGui.clearTable(tab2, tab4);
        List.of(tab1, tab2, tab3, tab4).forEach(tab -> ((DefTableModel) tab.getModel()).getQuery().execsql());
        if (tab1.getSelectedRow() != -1) {

            Record projectRec = qProject.get(UGui.getIndexRec(tab1));
            //lab2.setText("Заказ № " + projectRec.getStr(eProject.num_ord));
            int orderID = qProject.getAs(UGui.getIndexRec(tab1), eProject.id);
            eProp.orderID.write(String.valueOf(orderID));

            Record currencRec = qCurrenc.stream().filter(rec -> rec.get(eCurrenc.id).equals(projectRec.get(eProject.currenc_id))).findFirst().orElse(eCurrenc.up.newRecord(Query.SEL));
            txt3.setText(currencRec.getStr(eCurrenc.name));
            txt7.setText(UCom.format(projectRec.getDbl(eProject.weight), 1));
            txt8.setText(UCom.format(projectRec.getDbl(eProject.square) / 1000000, 2));

            loadingTab2();
            loadingTab4();
            loadingTab5();
        }
    }

    public void selectionTab2() {
        Arrays.asList(tab1, tab2, tab3, tab4).forEach(tab -> ((DefTableModel) tab.getModel()).getQuery().execsql());
        int index = UGui.getIndexRec(tab2);
        if (index != -1) {
            Record prjprodRec = qPrjprod.get(index);
            eProp.prjprodID.write(prjprodRec.getStr(ePrjprod.id)); //запишем текущий prjprodID в файл
            App.Top.frame.setTitle(UGui.designTitle());
            Object w = prjprodRec.get(ePrjprod.values().length);
            if (w instanceof Wincalc) { //прорисовка окна               
                Wincalc win = (Wincalc) w;

                GsonElem.setMaxID(win); //установим генератор идентификаторов  

                scene.init(win);
                canvas.draw();

                loadingTree(win);

                winTree.setSelectionInterval(0, 0);
            }
        } else {
            canvas.draw();
            winTree.setModel(new DefaultTreeModel(new DefMutableTreeNode("")));
        }
    }

    public void selectionTree() {
        try {
            Object selNode = winTree.getLastSelectedPathComponent();
            if (selNode instanceof DefMutableTreeNode) {
                winNode = (DefMutableTreeNode) winTree.getLastSelectedPathComponent();
                Wincalc winc = wincalc();

                //Конструкции
                if (winNode.com5t().type == enums.Type.RECTANGL || winNode.com5t().type == enums.Type.DOOR || winNode.com5t().type == enums.Type.TRAPEZE || winNode.com5t().type == enums.Type.ARCH) {
                    ((CardLayout) pan8.getLayout()).show(pan8, "card12");
                    ((TitledBorder) pan12.getBorder()).setTitle(winc.root.type.name);
                    setText(txt9, eColor.find(winc.colorID1).getStr(eColor.name));
                    setText(txt13, eColor.find(winc.colorID2).getStr(eColor.name));
                    setText(txt14, eColor.find(winc.colorID3).getStr(eColor.name));
                    setText(txt17, UCom.format(winc.width(), 1));
                    setText(txt22, UCom.format(winc.height(), 1));
                    Record record = qPrjprod.get(UGui.getIndexRec(tab2));
                    Integer systreeID = record.getInt(ePrjprod.systree_id);
                    setText(txt12, eSystree.find(systreeID).getStr(eSystree.note));

                    //Параметры
                } else if (winNode.com5t().type == enums.Type.PARAM) {
                    ((CardLayout) pan8.getLayout()).show(pan8, "card14");
                    qSyspar1.clear();
                    winc.mapPardef.forEach((pk, syspar1Rec) -> qSyspar1.add(syspar1Rec));
                    Collections.sort(qSyspar1, (o1, o2) -> qGroups.find(o1.getInt(eSyspar1.groups_id), eGroups.id).getStr(eGroups.name)
                            .compareTo(qGroups.find(o2.getInt(eSyspar1.groups_id), eGroups.id).getStr(eGroups.name)));
                    ((DefTableModel) tab3.getModel()).fireTableDataChanged();

                    //Рама, импост...
                } else if (List.of(enums.Type.FRAME_SIDE, enums.Type.STVORKA_SIDE, enums.Type.IMPOST,
                        enums.Type.STOIKA, enums.Type.SHTULP).contains(winNode.com5t().type)) {
                    ((CardLayout) pan8.getLayout()).show(pan8, "card13");
                    ((TitledBorder) pan13.getBorder()).setTitle(winNode.toString());
                    txt32.setText(winNode.com5t().artiklRecAn.getStr(eArtikl.code));
                    txt33.setText(winNode.com5t().artiklRecAn.getStr(eArtikl.name));
                    txt27.setText(eColor.find(winNode.com5t().colorID1).getStr(eColor.name));
                    txt28.setText(eColor.find(winNode.com5t().colorID2).getStr(eColor.name));
                    txt29.setText(eColor.find(winNode.com5t().colorID3).getStr(eColor.name));

                    //Стеклопакет
                } else if (winNode.com5t().type == enums.Type.GLASS) {
                    ((CardLayout) pan8.getLayout()).show(pan8, "card15");
                    Record artiklRec = winNode.com5t().artiklRec;
                    txt19.setText(artiklRec.getStr(eArtikl.code));
                    txt18.setText(artiklRec.getStr(eArtikl.name));
                    Record colorRec = eColor.find(winNode.com5t().colorID1);
                    setText(txt34, colorRec.getStr(eColor.name));

                    //Створка
                } else if (winNode.com5t().type == enums.Type.STVORKA) {
                    //расчёт ручки, 
                    new TFurniture(wincalc(), true).furn();   //подвеса, замка
                    //через сокр. тарификацию
                    ((CardLayout) pan8.getLayout()).show(pan8, "card16");
                    AreaStvorka stv = (AreaStvorka) winNode.com5t();
                    AreaSimple sta = (AreaSimple) winNode.com5t();
                    int id = stv.sysfurnRec.getInt(eSysfurn.furniture_id);
                    setText(txt24, UCom.format(UCom.layout(sta.frames, Layout.BOTT).width(), 1));
                    double h = (UCom.layout(sta.frames, Layout.RIGHT).height() > UCom.layout(sta.frames, Layout.LEFT).height()) ? UCom.layout(sta.frames, Layout.RIGHT).height() : UCom.layout(sta.frames, Layout.LEFT).height();
                    setText(txt26, UCom.format(h, 1));
                    setText(txt20, eFurniture.find(id).getStr(eFurniture.name));
                    setIcon(btn10, stv.isJson(stv.gson.param, PKjson.sysfurnID));
                    setText(txt30, stv.typeOpen.name2);
                    setText(txt16, stv.knobLayout.name);
                    if (stv.knobLayout == LayoutKnob.VAR) {
                        txt31.setEditable(true);
                        setText(txt31, UCom.format(stv.knobHeight, 1));
                    } else {
                        txt31.setEditable(false);
                        setText(txt31, "");
                    }
                    setText(txt21, stv.knobRec.getStr(eArtikl.code) + " ? " + stv.knobRec.getStr(eArtikl.name));
                    setIcon(btn12, stv.isJson(stv.gson.param, PKjson.artiklKnob));
                    setText(txt25, eColor.find(stv.knobColor).getStr(eColor.name));
                    setIcon(btn14, stv.isJson(stv.gson.param, PKjson.colorKnob));
                    setText(txt45, stv.loopRec.getStr(eArtikl.code));
                    setText(txt58, stv.loopRec.getStr(eArtikl.name));
                    setIcon(btn15, stv.isJson(stv.gson.param, PKjson.artiklLoop));
                    setText(txt47, eColor.find(stv.loopColor).getStr(eColor.name));
                    setIcon(btn17, stv.isJson(stv.gson.param, PKjson.colorLoop));
                    setText(txt46, stv.lockRec.getStr(eArtikl.code));
                    setText(txt57, stv.lockRec.getStr(eArtikl.name));
                    setIcon(btn23, stv.isJson(stv.gson.param, PKjson.artiklLock));
                    setText(txt48, eColor.find(stv.lockColor).getStr(eColor.name));
                    setIcon(btn24, stv.isJson(stv.gson.param, PKjson.colorLock));
                    //Москитка
                    ArrayList<Com5t> mosqList = UCom.filter(((AreaSimple) stv).childs, enums.Type.MOSQUIT);
                    if (mosqList.isEmpty() == false) {
                        ElemSimple mosq = (ElemSimple) mosqList.get(0);
                        setText(txt54, mosq.artiklRec.getStr(eArtikl.code));
                        setText(txt55, mosq.artiklRec.getStr(eArtikl.name));
                        setText(txt60, eColor.find(mosq.colorID1).getStr(eColor.name));
                        setText(txt56, mosq.sysprofRec.getStr(eElement.name));
                    }

                    //Соединения
                } else if (winNode.com5t().type == enums.Type.JOINING) {
                    ((CardLayout) pan8.getLayout()).show(pan8, "card17");
                    DefMutableTreeNode nodeParent = (DefMutableTreeNode) winNode.getParent();
                    ElemSimple elem5e = (ElemSimple) nodeParent.com5t();
                    List.of(txt36, txt37, txt38, txt39, txt40, txt41, txt42, txt43, txt44).forEach(it -> it.setText(""));
                    new TJoining(winc, true).join();//заполним соединения из конструктива 
                    ElemJoining ej1 = UCom.join(winc.listJoin, elem5e, 0);
                    ElemJoining ej2 = UCom.join(winc.listJoin, elem5e, 1);
                    ElemJoining ej3 = UCom.join(winc.listJoin, elem5e, 2);

                    if (ej1 != null) {
                        setText(txt36, ej1.joiningRec.getStr(eJoining.name));
                        setText(txt42, ej1.name());
                        setText(txt38, ej1.joinvarRec.getStr(eJoinvar.name));
                        lab55.setIcon(UColor.iconFromTypeJoin2(ej1.type().id));
                    }
                    if (ej2 != null) {
                        setText(txt37, ej2.joiningRec.getStr(eJoining.name));
                        setText(txt43, ej2.name());
                        setText(txt39, ej2.joinvarRec.getStr(eJoinvar.name));
                        lab56.setIcon(UColor.iconFromTypeJoin2(ej2.type().id));
                    }
                    if (ej3 != null) {
                        setText(txt40, ej3.joiningRec.getStr(eJoining.name));
                        setText(txt44, ej3.name());
                        setText(txt41, ej3.joinvarRec.getStr(eJoinvar.name));
                        lab57.setIcon(UColor.iconFromTypeJoin2(ej3.type().id));
                    }
                } else {
                    ((CardLayout) pan8.getLayout()).show(pan8, "card18");
                }
                List.of(pan12, pan13, pan15, pan16).forEach(it -> it.repaint());
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Orders.selectionTree() " + e);
        }
    }

    public void listenerAdd() {

        UGui.buttonCellEditor(tab1, 2).addActionListener(event -> {
            new DicDate(this, (obj) -> {
                GregorianCalendar calendar = (GregorianCalendar) obj;
                UGui.stopCellEditing(tab1, tab2, tab3, tab4);
                Record record2 = qProject.get(UGui.getIndexRec(tab1));
                record2.set(eProject.date4, calendar.getTime());
                ((DefaultTableModel) tab1.getModel()).fireTableRowsUpdated(tab1.getSelectedRow(), tab1.getSelectedRow());
                return true;
            }, 0);
        });

        UGui.buttonCellEditor(tab1, 3).addActionListener(event -> {
            new DicDate(this, (obj) -> {
                GregorianCalendar calendar = (GregorianCalendar) obj;
                UGui.stopCellEditing(tab1);
                Record record2 = qProject.get(UGui.getIndexRec(tab1));
                record2.set(eProject.date5, calendar.getTime());
                ((DefaultTableModel) tab1.getModel()).fireTableRowsUpdated(tab1.getSelectedRow(), tab1.getSelectedRow());
                return true;
            }, 0);
        });

        UGui.buttonCellEditor(tab1, 4).addActionListener(event -> {
            new DicDate(this, (obj) -> {
                GregorianCalendar calendar = (GregorianCalendar) obj;
                UGui.stopCellEditing(tab1);
                Record record2 = qProject.get(UGui.getIndexRec(tab1));
                record2.set(eProject.date6, calendar.getTime());
                ((DefaultTableModel) tab1.getModel()).fireTableRowsUpdated(tab1.getSelectedRow(), tab1.getSelectedRow());
                return true;
            }, 0);
        });

        UGui.buttonCellEditor(tab1, 5).addActionListener(event -> {
            new Partner(Orders.this, (record) -> {
                UGui.stopCellEditing(tab1, tab2, tab3, tab4);
                Record record2 = qProject.get(UGui.getIndexRec(tab1));
                record2.set(eProject.prjpart_id, record.getInt(ePrjpart.id));
                ((DefaultTableModel) tab1.getModel()).fireTableRowsUpdated(tab1.getSelectedRow(), tab1.getSelectedRow());
            });
        });

        UGui.buttonCellEditor(tab3, 1).addActionListener(event -> {
            UGui.stopCellEditing(tab2, tab3, tab4, tab5);
            int id = qSyspar1.getAs(UGui.getIndexRec(tab3), eSyspar1.id);
            int fixed = eSyspar1.find2(id).getInt(eSyspar1.fixed);
            if (fixed != 1) {
                Integer grup = qSyspar1.getAs(UGui.getIndexRec(tab3), eSyspar1.groups_id);
                new ParDefVal(this, record -> {

                    int index = UGui.getIndexRec(tab2);
                    int index2 = UGui.getIndexRec(tab3);
                    if (index != -1) {
                        Record prjprodRec = qPrjprod.get(index);
                        String script = prjprodRec.getStr(ePrjprod.script);
                        String script2 = UGui.ioknaParamUpdate(script, record.getInt(0));
                        prjprodRec.set(ePrjprod.script, script2);
                        qPrjprod.execsql();
                        wincalc().build(script2);
                        selectionTree();
                        UGui.setSelectedIndex(tab3, index2);
                    }
                }, grup);
            } else {
                JOptionPane.showMessageDialog(Orders.this, "Неизменяемый параметр в системе", "ВНИМАНИЕ!", 1);
            }
        });

        UGui.buttonCellEditor(tab4, 0).addActionListener(event -> {
            int index = UGui.getIndexRec(tab1);
            if (index != -1) {
                Record projectRec = qProject.get(index);

                new DicPrjprod(Orders.this, (record) -> {
                    UGui.stopCellEditing(tab1, tab2, tab3, tab4);
                    Record record2 = qPrjkit.get(UGui.getIndexRec(tab4));
                    if (record.size() == 2) {
                        record2.set(ePrjkit.prjprod_id, null);
                    } else {
                        record2.set(ePrjkit.prjprod_id, record.getInt(ePrjprod.id));
                    }
                    ((DefaultTableModel) tab4.getModel()).fireTableRowsUpdated(tab4.getSelectedRow(), tab4.getSelectedRow());
                }, projectRec.getInt(eProject.id));
            }
        });

        UGui.buttonCellEditor(tab4, 1).addActionListener(event -> {
            Record prjkitRec = qPrjkit.get(UGui.getIndexRec(tab4));
            int id = prjkitRec.getInt(ePrjkit.artikl_id, -1);
            DicArtikl2 frame = new DicArtikl2(this, id, (record) -> {
                UGui.stopCellEditing(tab1, tab2, tab3, tab4);
                if (record.size() == 2) {
                    JOptionPane.showMessageDialog(this, "Поле артикул должно иметь значеение");
                } else {
                    qPrjkit.set(record.getInt(eArtikl.id), UGui.getIndexRec(tab4), ePrjkit.artikl_id);
                    qPrjkit.table(eArtikl.up).set(record.get(eArtikl.code), UGui.getIndexRec(tab4), eArtikl.code);
                    qPrjkit.table(eArtikl.up).set(record.get(eArtikl.name), UGui.getIndexRec(tab4), eArtikl.name);
                    UGui.fireTableRowUpdated(tab4);
                }
            }, 1, 2, 3, 4, 5);
        });

        UGui.buttonCellEditor(tab4, 2).addActionListener(event -> {
            Record prjkitRec = qPrjkit.get(UGui.getIndexRec(tab4));
            int id = prjkitRec.getInt(ePrjkit.artikl_id, -1);
            DicArtikl2 frame = new DicArtikl2(this, id, (record) -> {
                UGui.stopCellEditing(tab1, tab2, tab3, tab4);
                if (record.size() == 2) {
                    JOptionPane.showMessageDialog(this, "Поле артикул должно иметь значеение");
                } else {
                    qPrjkit.set(record.getInt(eArtikl.id), UGui.getIndexRec(tab4), ePrjkit.artikl_id);
                    qPrjkit.table(eArtikl.up).set(record.get(eArtikl.code), UGui.getIndexRec(tab4), eArtikl.code);
                    qPrjkit.table(eArtikl.up).set(record.get(eArtikl.name), UGui.getIndexRec(tab4), eArtikl.name);
                    UGui.fireTableRowUpdated(tab4);
                }
            }, 1, 2, 3, 4, 5);
        });

        UGui.buttonCellEditor(tab4, 3).addActionListener(event -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4);
            int index = UGui.getIndexRec(tab4);
            Record record = qPrjkit.get(index);
            HashSet<Record> colorSet = UGui.artiklToColorSet(record.getInt(ePrjkit.artikl_id), 1);

            DicColor frame = new DicColor(this, (record2) -> {
                record.set(ePrjkit.color1_id, record2.get(eColor.id));
                UGui.fireTableRowUpdated(tab4);

            }, colorSet, true, false);
        });

        UGui.buttonCellEditor(tab4, 4).addActionListener(event -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4);
            int index = UGui.getIndexRec(tab4);
            Record record = qPrjkit.get(index);
            HashSet<Record> colorSet = UGui.artiklToColorSet(record.getInt(ePrjkit.artikl_id), 2);

            DicColor frame = new DicColor(this, (record2) -> {
                record.set(ePrjkit.color2_id, record2.get(eColor.id));
                UGui.fireTableRowUpdated(tab4);

            }, colorSet, true, false);
        });

        UGui.buttonCellEditor(tab4, 5).addActionListener(event -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4);
            int index = UGui.getIndexRec(tab4);
            Record record = qPrjkit.get(index);
            HashSet<Record> colorSet = UGui.artiklToColorSet(record.getInt(ePrjkit.artikl_id), 3);

            DicColor frame = new DicColor(this, (record2) -> {
                record.set(ePrjkit.color3_id, record2.get(eColor.id));
                UGui.fireTableRowUpdated(tab4);

            }, colorSet, true, false);
        });

    }

    private Wincalc wincalc() {
        int index = UGui.getIndexRec(tab2);
        if (index != -1) {
            Record sysprodRec = qPrjprod.table(ePrjprod.up).get(index);
            Object v = sysprodRec.get(ePrjprod.values().length);
            if (v instanceof Wincalc) { //прорисовка окна               
                return (Wincalc) v;
            }
        }
        return null;
    }

    public void updateScript(double selectID) {
        try {
            //Сохраним скрипт в базе
            String script = wincalc().gson.toJson();
            Record prjprodRec = qPrjprod.get(UGui.getIndexRec(tab2));
            prjprodRec.set(eSysprod.script, script);
            qPrjprod.update(prjprodRec);

            //Экземпляр нового скрипта
            Wincalc iwin2 = new Wincalc(script);
            iwin2.imageIcon = Canvas.createIcon(iwin2, 68);
            prjprodRec.setNo(ePrjprod.values().length, iwin2);

            //Запомним курсор
            DefMutableTreeNode selectNode = (DefMutableTreeNode) winTree.getLastSelectedPathComponent();
            double id = (selectNode != null) ? selectNode.com5t().id : -1;

            //Перегрузим winTree
            loadingTree(iwin2);

            //Перерисуем конструкцию
            canvas.draw();

            //Обновим поля форм
            selectionTree();

            //Установим курсор
            UGui.selectionPathWin(id, winTree);

        } catch (Exception e) {
            System.err.println("Ошибка:Order.updateScript() " + e);
        }
    }

    @Override
    public Query reload(boolean b) {
        Wincalc win = wincalc();
        int index = UGui.getIndexRec(tab2);
        if (index != -1) {
            String script = win.gson.toJson();
            win.build(script);
            win.imageIcon = Canvas.createIcon(win, 68);
            if (b == true) {
                Record sysprodRec = qPrjprod.get(index);
                sysprodRec.set(ePrjprod.script, script);
                sysprodRec.set(ePrjprod.values().length, win);
            }
            canvas.draw();
            selectionTree();
        }
        return qPrjprod;
    }

    @Override
    public void action() {
        //int index = UGui.getIndexRec(tab1);
        //if (index != -1) {
        loadingTab2();
        // UGui.setSelectedIndex(tab1, index);
        //}
    }

    public void calculate() {
        try {
            int index = UGui.getIndexRec(tab1);
            UGui.stopCellEditingAndExecSql();
            Record projectRec = qProject.get(UGui.getIndexRec(tab1));
            Record currencRec = qCurrenc.stream().filter(rec -> rec.get(eCurrenc.id).equals(projectRec.get(eProject.currenc_id))).findFirst().orElse(eCurrenc.up.newRecord(Query.SEL));
            double square = 0, weight = 0,
                    price1a = 0, price1b = 0, price1c = 0, //без скидки менеджера
                    price2a = 0, price2b = 0, price2c = 0; //со скидкой менеджера
            //Пересчёт заказа
            if (UGui.getIndexRec(tab1) != -1) {

                //Цикл по конструкциям
                for (Record prjprodRec : qPrjprod) {
                    Object w = prjprodRec.get(ePrjprod.values().length);
                    if (w instanceof Wincalc) {

                        Wincalc win = (Wincalc) w;
                        String script = prjprodRec.getStr(ePrjprod.script);
                        //JsonElement jsonElem = new Gson().fromJson(script, JsonElement.class);
                        //win.build(jsonElem.toString()); //калкуляция                              
                        win.build(script); //калкуляция                              
                        win.specific(true); //конструктив  

                        double countWin = prjprodRec.getDbl(ePrjprod.num);
                        square = square + countWin * win.root.area.getGeometryN(0).getArea(); //площадь изделий  
                        weight = weight + countWin * win.weight; //вес изделий
                        price1a = price1a + countWin * win.price(1); //стоимость конструкций без скидки менеджера
                        price2a = price2a + countWin * win.price(2); //стоимость конструкций со скидкой менеджера
                    }
                }
                //Комплектация
                ArrayList<TRecord> kitList = Kitcalc.tarifficProj(projectRec, new Wincalc(), true); //комплекты 
                price1b = Kitcalc.price(1); //стоимость без скидки
                price2b = Kitcalc.price(2); //стоимость со скидкой               

                //Сохраним новые кальк.данные в проекте
                if (price1a != projectRec.getDbl(eProject.price1a)) {
                    projectRec.set(eProject.price1a, price1a); //стоимость конструкции без скидки менеджера
                }
                price2a = price2a - price2a * projectRec.getDbl(eProject.disc2) / 100;
                if (price2a != projectRec.getDbl(eProject.price2a)) {
                    projectRec.set(eProject.price2a, price2a); //стоимость конструкции со скидкой менеджера
                }
                if (price1b != projectRec.getDbl(eProject.price1b)) {
                    projectRec.set(eProject.price1b, price1b); //стоимость комплектации без скидки менеджера
                }
                price2b = price2b - price2b * projectRec.getDbl(eProject.disc3) / 100;
                if (price2b != projectRec.getDbl(eProject.price2b)) {
                    projectRec.set(eProject.price2b, price2b); //стоимость комплектации со скидкой менеджера
                }
                price1c = price1a + price1b;
                if (price1a + price1b != projectRec.getDbl(eProject.price1c)) {
                    projectRec.set(eProject.price1c, price1c); //стоимость проекта без скидок
                }
                price2c = (price2a + price2b) - ((price2a + price2b) * projectRec.getDbl(eProject.disc4) / 100);
                if (price2c != projectRec.getDbl(eProject.price2c)) {
                    projectRec.set(eProject.price2c, price2c); //стоимость проекта со скидками менеджера
                }
                projectRec.set(eProject.date5, new GregorianCalendar().getTime());

                qProject.execsql();

                //Заполним вес, площадь
                txt8.setText(UCom.format(projectRec.getDbl(eProject.square) / 1000000, 2)); //площадь
                txt7.setText(UCom.format(projectRec.getDbl(eProject.weight), 1)); //вес 

                //Заполним таблицу
                tab5.setValueAt(projectRec.getDbl(eProject.price1a), 0, 2); //стоимость конструкций без скидки
                tab5.setValueAt(projectRec.getDbl(eProject.price2a), 0, 3); //стоимость конструкций со скидкой
                tab5.setValueAt(projectRec.getDbl(eProject.price1b), 1, 2); //стоимость комплектации без скидки
                tab5.setValueAt(projectRec.getDbl(eProject.price2b), 1, 3); //стоимость комплектации со скидкой
                tab5.setValueAt(projectRec.getDbl(eProject.price1c), 2, 2); //итого стоимость без скидки
                tab5.setValueAt(projectRec.getDbl(eProject.price2c), 2, 3); //итого стоимость со скидкой

                if (index != -1) {
                    ((DefaultTableModel) tab1.getModel()).fireTableDataChanged();
                    UGui.setSelectedIndex(tab1, index);
                } else {
                    UGui.setSelectedRow(tab1);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Orders.btnCalc() " + e);
        }
    }

    private void setText(JTextComponent comp, String txt) {
        comp.setText(txt);
        comp.setCaretPosition(0);
    }

    private void setIcon(JButton btn, boolean b) {
        if (b == true) {
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

        buttonGroup = new javax.swing.ButtonGroup();
        ppReport = new javax.swing.JPopupMenu();
        jmenu01 = new javax.swing.JMenu();
        menuItem19 = new javax.swing.JMenuItem();
        menuItem20 = new javax.swing.JMenuItem();
        menuItem21 = new javax.swing.JMenuItem();
        sep4 = new javax.swing.JPopupMenu.Separator();
        menuItem22 = new javax.swing.JMenuItem();
        menuItem23 = new javax.swing.JMenuItem();
        sep5 = new javax.swing.JPopupMenu.Separator();
        menuItem24 = new javax.swing.JMenuItem();
        menuItem25 = new javax.swing.JMenuItem();
        sep6 = new javax.swing.JPopupMenu.Separator();
        menuItem26 = new javax.swing.JMenuItem();
        jmenu02 = new javax.swing.JMenu();
        menuItem12 = new javax.swing.JMenuItem();
        menuItem11 = new javax.swing.JMenuItem();
        menuItem18 = new javax.swing.JMenuItem();
        sep1 = new javax.swing.JPopupMenu.Separator();
        menuItem14 = new javax.swing.JMenuItem();
        menuItem13 = new javax.swing.JMenuItem();
        sep2 = new javax.swing.JPopupMenu.Separator();
        menuItem15 = new javax.swing.JMenuItem();
        menuItem16 = new javax.swing.JMenuItem();
        sep3 = new javax.swing.JPopupMenu.Separator();
        menuItem17 = new javax.swing.JMenuItem();
        ppmCrud = new javax.swing.JPopupMenu();
        mInsert = new javax.swing.JMenuItem();
        mDelit = new javax.swing.JMenuItem();
        north = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnSet = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        btnIns = new javax.swing.JButton();
        btnCalc = new javax.swing.JButton();
        btnF1 = new javax.swing.JToggleButton();
        btnF2 = new javax.swing.JToggleButton();
        btnF3 = new javax.swing.JToggleButton();
        btnTest = new javax.swing.JButton();
        btnFind = new javax.swing.JButton();
        btnReport = new javax.swing.JToggleButton();
        centr = new javax.swing.JPanel();
        tabb1 = new javax.swing.JTabbedPane();
        pan1 = new javax.swing.JPanel();
        scr1 = new javax.swing.JScrollPane();
        tab1 = new javax.swing.JTable();
        pan11 = new javax.swing.JPanel();
        pan9 = new javax.swing.JPanel();
        pan19 = new javax.swing.JPanel();
        lab1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        lab3 = new javax.swing.JLabel();
        txt3 = new javax.swing.JTextField();
        btn1 = new javax.swing.JButton();
        lab7 = new javax.swing.JLabel();
        txt7 = new javax.swing.JTextField();
        txt8 = new javax.swing.JTextField();
        lab8 = new javax.swing.JLabel();
        scr5 = new javax.swing.JScrollPane();
        tab5 = new javax.swing.JTable();
        pan7 = new javax.swing.JPanel();
        scr2 = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JTable();
        pan3 = new javax.swing.JPanel();
        pan5 = new javax.swing.JPanel();
        pan8 = new javax.swing.JPanel();
        pan14 = new javax.swing.JPanel();
        scr3 = new javax.swing.JScrollPane();
        tab3 = new javax.swing.JTable();
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
        txt17 = new javax.swing.JTextField();
        lab38 = new javax.swing.JLabel();
        txt22 = new javax.swing.JTextField();
        lab69 = new javax.swing.JLabel();
        txt12 = new javax.swing.JTextArea();
        pan13 = new javax.swing.JPanel();
        pan20 = new javax.swing.JPanel();
        lab28 = new javax.swing.JLabel();
        lab43 = new javax.swing.JLabel();
        lab44 = new javax.swing.JLabel();
        btn18 = new javax.swing.JButton();
        btn19 = new javax.swing.JButton();
        btn20 = new javax.swing.JButton();
        txt27 = new javax.swing.JTextField();
        txt28 = new javax.swing.JTextField();
        txt29 = new javax.swing.JTextField();
        lab33 = new javax.swing.JLabel();
        lab34 = new javax.swing.JLabel();
        txt32 = new javax.swing.JTextField();
        txt33 = new javax.swing.JTextField();
        btn22 = new javax.swing.JButton();
        pan15 = new javax.swing.JPanel();
        lab29 = new javax.swing.JLabel();
        lab36 = new javax.swing.JLabel();
        btn3 = new javax.swing.JButton();
        txt19 = new javax.swing.JTextField();
        txt18 = new javax.swing.JTextField();
        lab61 = new javax.swing.JLabel();
        txt34 = new javax.swing.JTextField();
        btn25 = new javax.swing.JButton();
        pan16 = new javax.swing.JPanel();
        tabb2 = new javax.swing.JTabbedPane();
        pan22 = new javax.swing.JPanel();
        lab41 = new javax.swing.JLabel();
        txt24 = new javax.swing.JTextField();
        lab42 = new javax.swing.JLabel();
        txt26 = new javax.swing.JTextField();
        lab30 = new javax.swing.JLabel();
        txt20 = new javax.swing.JTextField();
        btn10 = new javax.swing.JButton();
        lab45 = new javax.swing.JLabel();
        txt30 = new javax.swing.JTextField();
        btn21 = new javax.swing.JButton();
        lab37 = new javax.swing.JLabel();
        txt21 = new javax.swing.JTextField();
        btn12 = new javax.swing.JButton();
        lab39 = new javax.swing.JLabel();
        txt25 = new javax.swing.JTextField();
        btn14 = new javax.swing.JButton();
        lab46 = new javax.swing.JLabel();
        txt16 = new javax.swing.JTextField();
        txt31 = new javax.swing.JTextField();
        btn6 = new javax.swing.JButton();
        pan23 = new javax.swing.JPanel();
        lab26 = new javax.swing.JLabel();
        txt45 = new javax.swing.JTextField();
        btn15 = new javax.swing.JButton();
        lab48 = new javax.swing.JLabel();
        txt47 = new javax.swing.JTextField();
        btn17 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txt46 = new javax.swing.JTextField();
        btn23 = new javax.swing.JButton();
        lab63 = new javax.swing.JLabel();
        txt48 = new javax.swing.JTextField();
        btn24 = new javax.swing.JButton();
        txt57 = new javax.swing.JTextField();
        lab51 = new javax.swing.JLabel();
        lab52 = new javax.swing.JLabel();
        txt58 = new javax.swing.JTextField();
        pan24 = new javax.swing.JPanel();
        lab47 = new javax.swing.JLabel();
        txt54 = new javax.swing.JTextField();
        btn16 = new javax.swing.JButton();
        lab53 = new javax.swing.JLabel();
        txt55 = new javax.swing.JTextField();
        lab66 = new javax.swing.JLabel();
        txt60 = new javax.swing.JTextField();
        btn32 = new javax.swing.JButton();
        lab62 = new javax.swing.JLabel();
        txt56 = new javax.swing.JTextField();
        btn31 = new javax.swing.JButton();
        pan17 = new javax.swing.JPanel();
        lab49 = new javax.swing.JLabel();
        lab50 = new javax.swing.JLabel();
        txt36 = new javax.swing.JTextField();
        txt37 = new javax.swing.JTextField();
        lab55 = new javax.swing.JLabel();
        txt38 = new javax.swing.JTextField();
        lab56 = new javax.swing.JLabel();
        txt39 = new javax.swing.JTextField();
        lab54 = new javax.swing.JLabel();
        txt40 = new javax.swing.JTextField();
        lab57 = new javax.swing.JLabel();
        txt41 = new javax.swing.JTextField();
        lab58 = new javax.swing.JLabel();
        txt42 = new javax.swing.JTextField();
        lab59 = new javax.swing.JLabel();
        txt43 = new javax.swing.JTextField();
        lab60 = new javax.swing.JLabel();
        txt44 = new javax.swing.JTextField();
        pan18 = new javax.swing.JPanel();
        scr6 = new javax.swing.JScrollPane();
        winTree = new javax.swing.JTree();
        panDesign = new javax.swing.JPanel();
        pan6 = new javax.swing.JPanel();
        scr4 = new javax.swing.JScrollPane();
        tab4 = new javax.swing.JTable();
        south = new javax.swing.JPanel();

        jmenu01.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b058.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.eProp.locale); // NOI18N
        jmenu01.setText(bundle.getString("Меню.Изделие")); // NOI18N
        jmenu01.setFont(frames.UGui.getFont(0,1));

        menuItem19.setFont(frames.UGui.getFont(0,1));
        menuItem19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b058.gif"))); // NOI18N
        menuItem19.setText(bundle.getString("Меню.Спецификация")); // NOI18N
        menuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem19(evt);
            }
        });
        jmenu01.add(menuItem19);

        menuItem20.setFont(frames.UGui.getFont(0,1));
        menuItem20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b058.gif"))); // NOI18N
        menuItem20.setText(bundle.getString("Меню.Расход материалов")); // NOI18N
        menuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem20(evt);
            }
        });
        jmenu01.add(menuItem20);

        menuItem21.setFont(frames.UGui.getFont(0,1));
        menuItem21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b058.gif"))); // NOI18N
        menuItem21.setText(bundle.getString("Меню.Задание в цех")); // NOI18N
        menuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem21(evt);
            }
        });
        jmenu01.add(menuItem21);
        jmenu01.add(sep4);

        menuItem22.setFont(frames.UGui.getFont(0,1));
        menuItem22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b058.gif"))); // NOI18N
        menuItem22.setText(bundle.getString("Меню.Смета")); // NOI18N
        menuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem22(evt);
            }
        });
        jmenu01.add(menuItem22);

        menuItem23.setFont(frames.UGui.getFont(0,1));
        menuItem23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b058.gif"))); // NOI18N
        menuItem23.setText(bundle.getString("Меню.Смета подробная")); // NOI18N
        menuItem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem23(evt);
            }
        });
        jmenu01.add(menuItem23);
        jmenu01.add(sep5);

        menuItem24.setFont(frames.UGui.getFont(0,1));
        menuItem24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b058.gif"))); // NOI18N
        menuItem24.setText(bundle.getString("Меню.Счёт")); // NOI18N
        menuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem24(evt);
            }
        });
        jmenu01.add(menuItem24);

        menuItem25.setFont(frames.UGui.getFont(0,1));
        menuItem25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b058.gif"))); // NOI18N
        menuItem25.setText(bundle.getString("Меню.Счёт-фактура")); // NOI18N
        menuItem25.setToolTipText("");
        menuItem25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem25(evt);
            }
        });
        jmenu01.add(menuItem25);
        jmenu01.add(sep6);

        menuItem26.setFont(frames.UGui.getFont(0,1));
        menuItem26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b058.gif"))); // NOI18N
        menuItem26.setText(bundle.getString("Меню.Ком-ое предл...")); // NOI18N
        menuItem26.setToolTipText("");
        menuItem26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem26(evt);
            }
        });
        jmenu01.add(menuItem26);

        ppReport.add(jmenu01);

        jmenu02.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b058.gif"))); // NOI18N
        jmenu02.setText(bundle.getString("Меню.Заказ")); // NOI18N
        jmenu02.setFont(frames.UGui.getFont(0,1));

        menuItem12.setFont(frames.UGui.getFont(0,1));
        menuItem12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b058.gif"))); // NOI18N
        menuItem12.setText(bundle.getString("Меню.Спецификация")); // NOI18N
        menuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem12(evt);
            }
        });
        jmenu02.add(menuItem12);

        menuItem11.setFont(frames.UGui.getFont(0,1));
        menuItem11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b058.gif"))); // NOI18N
        menuItem11.setText(bundle.getString("Меню.Расход материалов")); // NOI18N
        menuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem11(evt);
            }
        });
        jmenu02.add(menuItem11);

        menuItem18.setFont(frames.UGui.getFont(0,1));
        menuItem18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b058.gif"))); // NOI18N
        menuItem18.setText(bundle.getString("Меню.Задание в цех")); // NOI18N
        menuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem18(evt);
            }
        });
        jmenu02.add(menuItem18);
        jmenu02.add(sep1);

        menuItem14.setFont(frames.UGui.getFont(0,1));
        menuItem14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b058.gif"))); // NOI18N
        menuItem14.setText(bundle.getString("Меню.Смета")); // NOI18N
        menuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem14(evt);
            }
        });
        jmenu02.add(menuItem14);

        menuItem13.setFont(frames.UGui.getFont(0,1));
        menuItem13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b058.gif"))); // NOI18N
        menuItem13.setText(bundle.getString("Меню.Смета подробная")); // NOI18N
        menuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem13(evt);
            }
        });
        jmenu02.add(menuItem13);
        jmenu02.add(sep2);

        menuItem15.setFont(frames.UGui.getFont(0,1));
        menuItem15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b058.gif"))); // NOI18N
        menuItem15.setText(bundle.getString("Меню.Счёт")); // NOI18N
        menuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem15(evt);
            }
        });
        jmenu02.add(menuItem15);

        menuItem16.setFont(frames.UGui.getFont(0,1));
        menuItem16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b058.gif"))); // NOI18N
        menuItem16.setText(bundle.getString("Меню.Счёт-фактура")); // NOI18N
        menuItem16.setToolTipText("");
        menuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem16(evt);
            }
        });
        jmenu02.add(menuItem16);
        jmenu02.add(sep3);

        menuItem17.setFont(frames.UGui.getFont(0,1));
        menuItem17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b058.gif"))); // NOI18N
        menuItem17.setText(bundle.getString("Меню.Ком-ое предл...")); // NOI18N
        menuItem17.setToolTipText("");
        menuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem17(evt);
            }
        });
        jmenu02.add(menuItem17);

        ppReport.add(jmenu02);

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
        setTitle("Заказы");
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage()));
        setMinimumSize(new java.awt.Dimension(800, 600));
        setPreferredSize(new java.awt.Dimension(900, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                Orders.this.windowClosed(evt);
            }
        });

        north.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        north.setMaximumSize(new java.awt.Dimension(32767, 31));
        north.setPreferredSize(new java.awt.Dimension(800, 29));

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

        btnSet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c031.gif"))); // NOI18N
        btnSet.setToolTipText(bundle.getString("Выбрать список")); // NOI18N
        btnSet.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnSet.setEnabled(false);
        btnSet.setMaximumSize(new java.awt.Dimension(25, 25));
        btnSet.setMinimumSize(new java.awt.Dimension(25, 25));
        btnSet.setPreferredSize(new java.awt.Dimension(25, 25));
        btnSet.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnSet.addActionListener(new java.awt.event.ActionListener() {
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

        btnIns.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c033.gif"))); // NOI18N
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

        btnCalc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c041.gif"))); // NOI18N
        btnCalc.setToolTipText(bundle.getString("Пересчитать")); // NOI18N
        btnCalc.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnCalc.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        btnCalc.setFocusable(false);
        btnCalc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCalc.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnCalc.setMaximumSize(new java.awt.Dimension(25, 25));
        btnCalc.setMinimumSize(new java.awt.Dimension(25, 25));
        btnCalc.setPreferredSize(new java.awt.Dimension(25, 25));
        btnCalc.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnCalc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalc(evt);
            }
        });

        buttonGroup.add(btnF1);
        btnF1.setFont(frames.UGui.getFont(1,0));
        btnF1.setSelected(true);
        btnF1.setText("10");
        btnF1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnF1.setFocusable(false);
        btnF1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnF1.setMaximumSize(new java.awt.Dimension(28, 25));
        btnF1.setMinimumSize(new java.awt.Dimension(28, 25));
        btnF1.setPreferredSize(new java.awt.Dimension(32, 25));
        btnF1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnF1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilter(evt);
            }
        });

        buttonGroup.add(btnF2);
        btnF2.setFont(frames.UGui.getFont(1,0));
        btnF2.setText("30");
        btnF2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnF2.setFocusable(false);
        btnF2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnF2.setMaximumSize(new java.awt.Dimension(28, 25));
        btnF2.setMinimumSize(new java.awt.Dimension(28, 25));
        btnF2.setPreferredSize(new java.awt.Dimension(32, 25));
        btnF2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnF2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilter(evt);
            }
        });

        buttonGroup.add(btnF3);
        btnF3.setFont(frames.UGui.getFont(1,0));
        btnF3.setText(">");
        btnF3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnF3.setFocusable(false);
        btnF3.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnF3.setMaximumSize(new java.awt.Dimension(28, 25));
        btnF3.setMinimumSize(new java.awt.Dimension(28, 25));
        btnF3.setPreferredSize(new java.awt.Dimension(32, 25));
        btnF3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnF3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilter(evt);
            }
        });

        btnTest.setText("Test");
        btnTest.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnTest.setMaximumSize(new java.awt.Dimension(25, 25));
        btnTest.setMinimumSize(new java.awt.Dimension(25, 25));
        btnTest.setPreferredSize(new java.awt.Dimension(25, 25));
        btnTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTest(evt);
            }
        });

        btnFind.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c090.gif"))); // NOI18N
        btnFind.setToolTipText(bundle.getString("Поиск записи")); // NOI18N
        btnFind.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnFind.setFocusable(false);
        btnFind.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFind.setMaximumSize(new java.awt.Dimension(25, 25));
        btnFind.setMinimumSize(new java.awt.Dimension(25, 25));
        btnFind.setPreferredSize(new java.awt.Dimension(25, 25));
        btnFind.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnFind.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFind(evt);
            }
        });

        btnReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c093.gif"))); // NOI18N
        btnReport.setText(bundle.getString("Меню.Отчёты")); // NOI18N
        btnReport.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnReport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnReport.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnReport.setMaximumSize(new java.awt.Dimension(80, 250));
        btnReport.setMinimumSize(new java.awt.Dimension(25, 25));
        btnReport.setPreferredSize(new java.awt.Dimension(80, 25));
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRepor(evt);
            }
        });

        javax.swing.GroupLayout northLayout = new javax.swing.GroupLayout(north);
        north.setLayout(northLayout);
        northLayout.setHorizontalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnIns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCalc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnFind, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnF1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnF2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnF3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 450, Short.MAX_VALUE)
                .addComponent(btnTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        northLayout.setVerticalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCalc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnFind, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(northLayout.createSequentialGroup()
                        .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(btnDel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnIns, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(btnF2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnF3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnF1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        centr.setPreferredSize(new java.awt.Dimension(800, 550));
        centr.setLayout(new java.awt.BorderLayout());

        tabb1.setFont(frames.UGui.getFont(1,0));
        tabb1.setPreferredSize(new java.awt.Dimension(800, 500));
        tabb1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                Orders.this.stateChanged(evt);
            }
        });

        pan1.setPreferredSize(new java.awt.Dimension(800, 500));
        pan1.setLayout(new java.awt.BorderLayout());

        scr1.setBorder(null);
        scr1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        tab1.setFont(frames.UGui.getFont(0,0));
        tab1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Номер заказа", "Номер счёта", "Дата регистрации", "Дата расчёта", "Дата в произволство", "Контрагент", "User", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, true, true, true, true, false
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
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Orders.this.mousePressed(evt);
            }
        });
        scr1.setViewportView(tab1);
        if (tab1.getColumnModel().getColumnCount() > 0) {
            tab1.getColumnModel().getColumn(0).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(1).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(2).setPreferredWidth(80);
            tab1.getColumnModel().getColumn(3).setPreferredWidth(80);
            tab1.getColumnModel().getColumn(4).setPreferredWidth(80);
            tab1.getColumnModel().getColumn(5).setPreferredWidth(200);
            tab1.getColumnModel().getColumn(6).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(7).setPreferredWidth(40);
            tab1.getColumnModel().getColumn(7).setMaxWidth(60);
        }

        pan1.add(scr1, java.awt.BorderLayout.CENTER);

        pan11.setPreferredSize(new java.awt.Dimension(400, 500));
        pan11.setLayout(new java.awt.BorderLayout());

        pan9.setPreferredSize(new java.awt.Dimension(450, 145));
        pan9.setLayout(new java.awt.BorderLayout());

        pan19.setPreferredSize(new java.awt.Dimension(450, 70));

        lab1.setFont(frames.UGui.getFont(0,0));
        lab1.setText("Тип расчтета");
        lab1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab1.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab1.setMinimumSize(new java.awt.Dimension(34, 14));
        lab1.setPreferredSize(new java.awt.Dimension(86, 20));

        jComboBox1.setFont(frames.UGui.getFont(0,0));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "По спецификации" }));
        jComboBox1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jComboBox1.setPreferredSize(new java.awt.Dimension(180, 20));

        lab3.setFont(frames.UGui.getFont(0,0));
        lab3.setText("Валюта");
        lab3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab3.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab3.setPreferredSize(new java.awt.Dimension(60, 18));

        txt3.setFont(frames.UGui.getFont(0,0));
        txt3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt3.setFocusable(false);
        txt3.setPreferredSize(new java.awt.Dimension(62, 20));

        btn1.setText("...");
        btn1.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn1.setMaximumSize(new java.awt.Dimension(21, 20));
        btn1.setMinimumSize(new java.awt.Dimension(21, 20));
        btn1.setName("btn1"); // NOI18N
        btn1.setPreferredSize(new java.awt.Dimension(21, 20));
        btn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn1ActionPerformed(evt);
            }
        });

        lab7.setFont(frames.UGui.getFont(0,0));
        lab7.setText("Вес");
        lab7.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab7.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab7.setMinimumSize(new java.awt.Dimension(4, 14));
        lab7.setPreferredSize(new java.awt.Dimension(36, 18));

        txt7.setFont(frames.UGui.getFont(0,0));
        txt7.setText("0");
        txt7.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt7.setFocusable(false);
        txt7.setPreferredSize(new java.awt.Dimension(40, 20));

        txt8.setFont(frames.UGui.getFont(0,0));
        txt8.setText("0");
        txt8.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt8.setFocusable(false);
        txt8.setPreferredSize(new java.awt.Dimension(40, 20));

        lab8.setFont(frames.UGui.getFont(0,0));
        lab8.setText("Площадь");
        lab8.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab8.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab8.setMinimumSize(new java.awt.Dimension(34, 14));
        lab8.setPreferredSize(new java.awt.Dimension(60, 18));

        javax.swing.GroupLayout pan19Layout = new javax.swing.GroupLayout(pan19);
        pan19.setLayout(pan19Layout);
        pan19Layout.setHorizontalGroup(
            pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan19Layout.createSequentialGroup()
                        .addComponent(lab3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt3, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lab8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lab7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan19Layout.createSequentialGroup()
                        .addComponent(lab1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(122, 122, 122))))
        );
        pan19Layout.setVerticalGroup(
            pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan19Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pan9.add(pan19, java.awt.BorderLayout.NORTH);

        scr5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        scr5.setPreferredSize(new java.awt.Dimension(450, 80));

        tab5.setFont(frames.UGui.getFont(0,0));
        tab5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"   Конструкции", Double.valueOf(0.0), Double.valueOf(0.0), Double.valueOf(0.0)},
                {"   Комплектации",  Double.valueOf(0.0), Double.valueOf(0.0), Double.valueOf(0.0)},
                {"   Итого за заказ",  Double.valueOf(0.0), Double.valueOf(0.0), Double.valueOf(0.0)}
            },
            new String [] {
                "", "Скидка (%)", "Без скидок", "Со скидкой"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class

                , java.lang.Double.class

                , java.lang.Double.class

                , java.lang.Double.class

            };
            boolean[] canEdit = new boolean [] {
                false, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }

            public void setValueAt(Object aValue, int row, int column) {
                try {
                    super.setValueAt(aValue, row, column);
                    Record projectRec = qProject.get(UGui.getIndexRec(tab1));

                    if (row == 0 && column == 1 && aValue.equals(projectRec.get(eProject.disc2)) == false) {
                        projectRec.set(eProject.disc2, aValue);
                    } else if (row == 1 && column == 1 && aValue.equals(projectRec.get(eProject.disc3)) == false) {
                        projectRec.set(eProject.disc3, aValue);
                    } else if (row == 2 && column == 1 && aValue.equals(projectRec.get(eProject.disc4)) == false) {
                        projectRec.set(eProject.disc4, aValue);
                    }
                    loadingTab5();
                } catch (Exception e) {
                    System.out.println("Ошибка: tab5.setValueAt() " + e);
                }
            }
        });
        tab5.getTableHeader().setReorderingAllowed(false);
        scr5.setViewportView(tab5);

        pan9.add(scr5, java.awt.BorderLayout.CENTER);

        pan11.add(pan9, java.awt.BorderLayout.NORTH);

        pan7.setPreferredSize(new java.awt.Dimension(450, 350));
        pan7.setLayout(new java.awt.BorderLayout());

        scr2.setPreferredSize(new java.awt.Dimension(204, 404));

        tab2.setFont(frames.UGui.getFont(0,0));
        tab2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Наименование", "Кол-во", "Рисунок", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab2.setFillsViewportHeight(true);
        tab2.setName("tab2x"); // NOI18N
        tab2.setRowHeight(68);
        tab2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Orders.this.mousePressed(evt);
            }
        });
        scr2.setViewportView(tab2);
        if (tab2.getColumnModel().getColumnCount() > 0) {
            tab2.getColumnModel().getColumn(0).setPreferredWidth(180);
            tab2.getColumnModel().getColumn(1).setPreferredWidth(30);
            tab2.getColumnModel().getColumn(1).setMaxWidth(80);
            tab2.getColumnModel().getColumn(2).setMinWidth(68);
            tab2.getColumnModel().getColumn(2).setPreferredWidth(68);
            tab2.getColumnModel().getColumn(2).setMaxWidth(68);
            tab2.getColumnModel().getColumn(3).setPreferredWidth(30);
            tab2.getColumnModel().getColumn(3).setMaxWidth(60);
        }

        pan7.add(scr2, java.awt.BorderLayout.CENTER);

        pan11.add(pan7, java.awt.BorderLayout.CENTER);

        pan1.add(pan11, java.awt.BorderLayout.EAST);

        tabb1.addTab("       Заказы       ", pan1);

        pan3.setPreferredSize(new java.awt.Dimension(800, 500));
        pan3.setLayout(new java.awt.BorderLayout());

        pan5.setPreferredSize(new java.awt.Dimension(400, 450));
        pan5.setLayout(new java.awt.BorderLayout());

        pan8.setPreferredSize(new java.awt.Dimension(10, 292));
        pan8.setLayout(new java.awt.CardLayout());

        pan14.setLayout(new java.awt.BorderLayout());

        scr3.setBorder(null);
        scr3.setPreferredSize(new java.awt.Dimension(450, 300));

        tab3.setFont(frames.UGui.getFont(0,0));
        tab3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Параметры конструкции", "Значение по умолчанию", "ID"
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
        tab3.setFillsViewportHeight(true);
        tab3.setName("tab3"); // NOI18N
        tab3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scr3.setViewportView(tab3);
        if (tab3.getColumnModel().getColumnCount() > 0) {
            tab3.getColumnModel().getColumn(0).setPreferredWidth(400);
            tab3.getColumnModel().getColumn(1).setPreferredWidth(200);
            tab3.getColumnModel().getColumn(2).setPreferredWidth(40);
            tab3.getColumnModel().getColumn(2).setMaxWidth(60);
        }

        pan14.add(scr3, java.awt.BorderLayout.CENTER);

        pan8.add(pan14, "card14");

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
        btn9.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn9.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn9.setMaximumSize(new java.awt.Dimension(21, 20));
        btn9.setMinimumSize(new java.awt.Dimension(21, 20));
        btn9.setName("btn9"); // NOI18N
        btn9.setPreferredSize(new java.awt.Dimension(21, 20));
        btn9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToKorobka(evt);
            }
        });

        btn13.setText("...");
        btn13.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn13.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn13.setMaximumSize(new java.awt.Dimension(21, 20));
        btn13.setMinimumSize(new java.awt.Dimension(21, 20));
        btn13.setName("btn13"); // NOI18N
        btn13.setPreferredSize(new java.awt.Dimension(21, 20));
        btn13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToKorobka(evt);
            }
        });

        btn2.setText("...");
        btn2.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn2.setMaximumSize(new java.awt.Dimension(21, 20));
        btn2.setMinimumSize(new java.awt.Dimension(21, 20));
        btn2.setName("btn2"); // NOI18N
        btn2.setPreferredSize(new java.awt.Dimension(21, 20));
        btn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToKorobka(evt);
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
                        .addComponent(txt14, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE))
                    .addGroup(pan21Layout.createSequentialGroup()
                        .addComponent(lab27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan21Layout.createSequentialGroup()
                        .addComponent(lab31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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

        txt17.setFont(frames.UGui.getFont(0,0));
        txt17.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt17.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt17.setPreferredSize(new java.awt.Dimension(60, 18));
        txt17.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtKeyEnter(evt);
            }
        });

        lab38.setFont(frames.UGui.getFont(0,0));
        lab38.setText("Высота1");
        lab38.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab38.setPreferredSize(new java.awt.Dimension(80, 18));

        txt22.setFont(frames.UGui.getFont(0,0));
        txt22.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt22.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt22.setPreferredSize(new java.awt.Dimension(60, 18));
        txt22.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKeyEnter(evt);
            }
        });

        lab69.setFont(frames.UGui.getFont(0,0));
        lab69.setText("Примечание");
        lab69.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab69.setMaximumSize(new java.awt.Dimension(600, 36));
        lab69.setMinimumSize(new java.awt.Dimension(80, 36));
        lab69.setPreferredSize(new java.awt.Dimension(80, 36));

        txt12.setEditable(false);
        txt12.setFont(frames.UGui.getFont(0,0));
        txt12.setLineWrap(true);
        txt12.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt12.setMinimumSize(new java.awt.Dimension(5, 36));
        txt12.setPreferredSize(new java.awt.Dimension(164, 36));

        javax.swing.GroupLayout pan12Layout = new javax.swing.GroupLayout(pan12);
        pan12.setLayout(pan12Layout);
        pan12Layout.setHorizontalGroup(
            pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pan21, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
            .addGroup(pan12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan12Layout.createSequentialGroup()
                        .addComponent(lab69, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan12Layout.createSequentialGroup()
                        .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pan12Layout.createSequentialGroup()
                                .addComponent(lab35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pan12Layout.createSequentialGroup()
                                .addComponent(lab38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pan12Layout.setVerticalGroup(
            pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan12Layout.createSequentialGroup()
                .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lab69, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt12, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pan21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56))
        );

        pan8.add(pan12, "card12");

        pan13.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Рама, импост..", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 1)));
        pan13.setPreferredSize(new java.awt.Dimension(300, 200));

        pan20.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Текстура элемента", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 0)));
        pan20.setPreferredSize(new java.awt.Dimension(308, 104));

        lab28.setFont(frames.UGui.getFont(0,0));
        lab28.setText("Основная");
        lab28.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab28.setPreferredSize(new java.awt.Dimension(80, 18));

        lab43.setFont(frames.UGui.getFont(0,0));
        lab43.setText("Внутренняя");
        lab43.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab43.setPreferredSize(new java.awt.Dimension(80, 18));

        lab44.setFont(frames.UGui.getFont(0,0));
        lab44.setText("Внешняя");
        lab44.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab44.setPreferredSize(new java.awt.Dimension(80, 18));

        btn18.setText("...");
        btn18.setToolTipText(bundle.getString("Выбрать")); // NOI18N
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

        btn19.setText("...");
        btn19.setToolTipText(bundle.getString("Выбрать")); // NOI18N
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

        btn20.setText("...");
        btn20.setToolTipText(bundle.getString("Выбрать")); // NOI18N
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

        txt27.setEditable(false);
        txt27.setFont(frames.UGui.getFont(0,0));
        txt27.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt27.setPreferredSize(new java.awt.Dimension(180, 18));

        txt28.setEditable(false);
        txt28.setFont(frames.UGui.getFont(0,0));
        txt28.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt28.setPreferredSize(new java.awt.Dimension(180, 18));

        txt29.setEditable(false);
        txt29.setFont(frames.UGui.getFont(0,0));
        txt29.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt29.setPreferredSize(new java.awt.Dimension(180, 18));

        javax.swing.GroupLayout pan20Layout = new javax.swing.GroupLayout(pan20);
        pan20.setLayout(pan20Layout);
        pan20Layout.setHorizontalGroup(
            pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan20Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan20Layout.createSequentialGroup()
                        .addComponent(lab44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt29, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                    .addGroup(pan20Layout.createSequentialGroup()
                        .addComponent(lab28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt27, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                    .addGroup(pan20Layout.createSequentialGroup()
                        .addComponent(lab43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt28, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );
        pan20Layout.setVerticalGroup(
            pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan20Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        lab33.setFont(frames.UGui.getFont(0,0));
        lab33.setText("  Артикул");
        lab33.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab33.setPreferredSize(new java.awt.Dimension(80, 18));

        lab34.setFont(frames.UGui.getFont(0,0));
        lab34.setText("  Название");
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
        btn22.setToolTipText(bundle.getString("Выбрать")); // NOI18N
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

        javax.swing.GroupLayout pan13Layout = new javax.swing.GroupLayout(pan13);
        pan13.setLayout(pan13Layout);
        pan13Layout.setHorizontalGroup(
            pan13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pan20, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                    .addGroup(pan13Layout.createSequentialGroup()
                        .addComponent(lab33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt32, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan13Layout.createSequentialGroup()
                        .addComponent(lab34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt33, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pan13Layout.setVerticalGroup(
            pan13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pan20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(107, Short.MAX_VALUE))
        );

        pan8.add(pan13, "card13");

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
        btn3.setToolTipText(bundle.getString("Выбрать")); // NOI18N
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
        btn25.setToolTipText(bundle.getString("Выбрать")); // NOI18N
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
                        .addComponent(txt19, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan15Layout.createSequentialGroup()
                        .addComponent(lab36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt18, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE))
                    .addGroup(pan15Layout.createSequentialGroup()
                        .addComponent(lab61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt34, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pan15Layout.setVerticalGroup(
            pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan15Layout.createSequentialGroup()
                .addContainerGap()
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
                .addContainerGap(191, Short.MAX_VALUE))
        );

        pan8.add(pan15, "card15");

        pan16.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Створка", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 1)));
        pan16.setPreferredSize(new java.awt.Dimension(3100, 220));
        pan16.setLayout(new java.awt.BorderLayout());

        tabb2.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        tabb2.setFont(frames.UGui.getFont(-1,0));

        lab41.setFont(frames.UGui.getFont(0,0));
        lab41.setText("Ширина");
        lab41.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab41.setPreferredSize(new java.awt.Dimension(80, 18));

        txt24.setFont(frames.UGui.getFont(0,0));
        txt24.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt24.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt24.setPreferredSize(new java.awt.Dimension(60, 18));

        lab42.setFont(frames.UGui.getFont(0,0));
        lab42.setText("Высота");
        lab42.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab42.setPreferredSize(new java.awt.Dimension(80, 18));

        txt26.setFont(frames.UGui.getFont(0,0));
        txt26.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt26.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt26.setPreferredSize(new java.awt.Dimension(60, 18));
        txt26.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtKeyEnter(evt);
            }
        });

        lab30.setFont(frames.UGui.getFont(0,0));
        lab30.setText("Фурнитура");
        lab30.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab30.setPreferredSize(new java.awt.Dimension(80, 18));

        txt20.setEditable(false);
        txt20.setFont(frames.UGui.getFont(0,0));
        txt20.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt20.setPreferredSize(new java.awt.Dimension(178, 18));

        btn10.setText("...");
        btn10.setToolTipText(bundle.getString("Выбрать")); // NOI18N
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

        lab45.setFont(frames.UGui.getFont(0,0));
        lab45.setText("Напр. откр.");
        lab45.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab45.setPreferredSize(new java.awt.Dimension(80, 18));

        txt30.setEditable(false);
        txt30.setFont(frames.UGui.getFont(0,0));
        txt30.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt30.setPreferredSize(new java.awt.Dimension(178, 18));

        btn21.setText("...");
        btn21.setToolTipText(bundle.getString("Выбрать")); // NOI18N
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

        lab37.setFont(frames.UGui.getFont(0,0));
        lab37.setText("Ручка");
        lab37.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab37.setPreferredSize(new java.awt.Dimension(80, 18));

        txt21.setEditable(false);
        txt21.setFont(frames.UGui.getFont(0,0));
        txt21.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt21.setPreferredSize(new java.awt.Dimension(178, 18));

        btn12.setText("...");
        btn12.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn12.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn12.setMaximumSize(new java.awt.Dimension(21, 20));
        btn12.setMinimumSize(new java.awt.Dimension(21, 20));
        btn12.setName("btnField17"); // NOI18N
        btn12.setPreferredSize(new java.awt.Dimension(21, 20));
        btn12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handkToStvorka(evt);
            }
        });

        lab39.setFont(frames.UGui.getFont(0,0));
        lab39.setText("Текстура");
        lab39.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab39.setPreferredSize(new java.awt.Dimension(80, 18));

        txt25.setEditable(false);
        txt25.setFont(frames.UGui.getFont(0,0));
        txt25.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt25.setPreferredSize(new java.awt.Dimension(178, 18));

        btn14.setText("...");
        btn14.setToolTipText(bundle.getString("Выбрать")); // NOI18N
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

        lab46.setFont(frames.UGui.getFont(0,0));
        lab46.setText("Высота ручки");
        lab46.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab46.setPreferredSize(new java.awt.Dimension(80, 18));

        txt16.setEditable(false);
        txt16.setFont(frames.UGui.getFont(0,0));
        txt16.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt16.setPreferredSize(new java.awt.Dimension(208, 18));

        txt31.setFont(frames.UGui.getFont(0,0));
        txt31.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt31.setPreferredSize(new java.awt.Dimension(56, 18));

        btn6.setText("...");
        btn6.setToolTipText(bundle.getString("Выбрать")); // NOI18N
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

        javax.swing.GroupLayout pan22Layout = new javax.swing.GroupLayout(pan22);
        pan22.setLayout(pan22Layout);
        pan22Layout.setHorizontalGroup(
            pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan22Layout.createSequentialGroup()
                        .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lab45, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lab30, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt30, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pan22Layout.createSequentialGroup()
                        .addComponent(lab41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lab42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan22Layout.createSequentialGroup()
                        .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lab39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lab37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt25, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pan22Layout.createSequentialGroup()
                        .addComponent(lab46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt16, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pan22Layout.setVerticalGroup(
            pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lab42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lab41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(79, Short.MAX_VALUE))
        );

        tabb2.addTab("Основн...", pan22);

        lab26.setFont(frames.UGui.getFont(0,0));
        lab26.setText("Петля");
        lab26.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab26.setMaximumSize(new java.awt.Dimension(80, 18));
        lab26.setMinimumSize(new java.awt.Dimension(80, 18));
        lab26.setPreferredSize(new java.awt.Dimension(80, 18));

        txt45.setEditable(false);
        txt45.setFont(frames.UGui.getFont(0,0));
        txt45.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt45.setPreferredSize(new java.awt.Dimension(178, 18));

        btn15.setText("...");
        btn15.setToolTipText(bundle.getString("Выбрать")); // NOI18N
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

        lab48.setFont(frames.UGui.getFont(0,0));
        lab48.setText("Текстура");
        lab48.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab48.setMaximumSize(new java.awt.Dimension(80, 18));
        lab48.setMinimumSize(new java.awt.Dimension(80, 18));
        lab48.setPreferredSize(new java.awt.Dimension(80, 18));

        txt47.setEditable(false);
        txt47.setFont(frames.UGui.getFont(0,0));
        txt47.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt47.setPreferredSize(new java.awt.Dimension(178, 18));

        btn17.setText("...");
        btn17.setToolTipText(bundle.getString("Выбрать")); // NOI18N
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

        jLabel1.setFont(frames.UGui.getFont(0,0));
        jLabel1.setText("Замок");
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel1.setMaximumSize(new java.awt.Dimension(80, 18));
        jLabel1.setMinimumSize(new java.awt.Dimension(80, 18));
        jLabel1.setPreferredSize(new java.awt.Dimension(80, 18));

        txt46.setEditable(false);
        txt46.setFont(frames.UGui.getFont(0,0));
        txt46.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt46.setPreferredSize(new java.awt.Dimension(178, 18));

        btn23.setText("...");
        btn23.setToolTipText(bundle.getString("Выбрать")); // NOI18N
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

        lab63.setFont(frames.UGui.getFont(0,0));
        lab63.setText("Текстура");
        lab63.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab63.setMaximumSize(new java.awt.Dimension(80, 18));
        lab63.setMinimumSize(new java.awt.Dimension(80, 18));
        lab63.setPreferredSize(new java.awt.Dimension(80, 18));

        txt48.setEditable(false);
        txt48.setFont(frames.UGui.getFont(0,0));
        txt48.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt48.setPreferredSize(new java.awt.Dimension(178, 18));

        btn24.setText("...");
        btn24.setToolTipText(bundle.getString("Выбрать")); // NOI18N
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

        txt57.setEditable(false);
        txt57.setFont(frames.UGui.getFont(0,0));
        txt57.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt57.setPreferredSize(new java.awt.Dimension(180, 18));

        lab51.setFont(frames.UGui.getFont(0,0));
        lab51.setText("Название");
        lab51.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab51.setMaximumSize(new java.awt.Dimension(80, 18));
        lab51.setMinimumSize(new java.awt.Dimension(80, 18));
        lab51.setPreferredSize(new java.awt.Dimension(80, 18));

        lab52.setFont(frames.UGui.getFont(0,0));
        lab52.setText("Название");
        lab52.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab52.setMaximumSize(new java.awt.Dimension(80, 18));
        lab52.setMinimumSize(new java.awt.Dimension(80, 18));
        lab52.setPreferredSize(new java.awt.Dimension(80, 18));

        txt58.setEditable(false);
        txt58.setFont(frames.UGui.getFont(0,0));
        txt58.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt58.setPreferredSize(new java.awt.Dimension(180, 18));

        javax.swing.GroupLayout pan23Layout = new javax.swing.GroupLayout(pan23);
        pan23.setLayout(pan23Layout);
        pan23Layout.setHorizontalGroup(
            pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(lab26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(txt45, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(lab48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(txt47, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(txt46, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(lab63, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(txt48, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(lab51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt57, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(lab52, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(lab52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab63, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(83, Short.MAX_VALUE))
        );

        tabb2.addTab("Дополн...", pan23);

        lab47.setFont(frames.UGui.getFont(0,0));
        lab47.setText("Моск. сетка");
        lab47.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab47.setMaximumSize(new java.awt.Dimension(80, 18));
        lab47.setMinimumSize(new java.awt.Dimension(80, 18));
        lab47.setPreferredSize(new java.awt.Dimension(80, 18));

        txt54.setEditable(false);
        txt54.setFont(frames.UGui.getFont(0,0));
        txt54.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt54.setPreferredSize(new java.awt.Dimension(180, 18));

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

        lab53.setFont(frames.UGui.getFont(0,0));
        lab53.setText("Название");
        lab53.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab53.setMaximumSize(new java.awt.Dimension(80, 18));
        lab53.setMinimumSize(new java.awt.Dimension(80, 18));
        lab53.setPreferredSize(new java.awt.Dimension(80, 18));

        txt55.setEditable(false);
        txt55.setFont(frames.UGui.getFont(0,0));
        txt55.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt55.setPreferredSize(new java.awt.Dimension(212, 18));

        lab66.setFont(frames.UGui.getFont(0,0));
        lab66.setText("Текстура");
        lab66.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab66.setMaximumSize(new java.awt.Dimension(80, 18));
        lab66.setMinimumSize(new java.awt.Dimension(80, 18));
        lab66.setPreferredSize(new java.awt.Dimension(80, 18));

        txt60.setEditable(false);
        txt60.setFont(frames.UGui.getFont(0,0));
        txt60.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt60.setPreferredSize(new java.awt.Dimension(180, 18));

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

        lab62.setFont(frames.UGui.getFont(0,0));
        lab62.setText("Состав");
        lab62.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab62.setMaximumSize(new java.awt.Dimension(80, 18));
        lab62.setMinimumSize(new java.awt.Dimension(80, 18));
        lab62.setPreferredSize(new java.awt.Dimension(80, 18));

        txt56.setEditable(false);
        txt56.setFont(frames.UGui.getFont(0,0));
        txt56.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt56.setPreferredSize(new java.awt.Dimension(180, 18));

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

        javax.swing.GroupLayout pan24Layout = new javax.swing.GroupLayout(pan24);
        pan24.setLayout(pan24Layout);
        pan24Layout.setHorizontalGroup(
            pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 390, Short.MAX_VALUE)
            .addGroup(pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pan24Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pan24Layout.createSequentialGroup()
                            .addComponent(lab47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt54, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btn16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(pan24Layout.createSequentialGroup()
                            .addComponent(lab62, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt56, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btn31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(pan24Layout.createSequentialGroup()
                            .addComponent(lab53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt55, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan24Layout.createSequentialGroup()
                            .addComponent(lab66, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt60, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btn32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap()))
        );
        pan24Layout.setVerticalGroup(
            pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 232, Short.MAX_VALUE)
            .addGroup(pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pan24Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(lab47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lab53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txt55, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lab66, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txt56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lab62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(126, Short.MAX_VALUE)))
        );

        tabb2.addTab("Маскитка", pan24);

        pan16.add(tabb2, java.awt.BorderLayout.CENTER);

        pan8.add(pan16, "card16");

        pan17.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Соединения", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 1)));
        pan17.setPreferredSize(new java.awt.Dimension(300, 200));

        lab49.setFont(frames.UGui.getFont(0,0));
        lab49.setText("1  соединение");
        lab49.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab49.setPreferredSize(new java.awt.Dimension(80, 18));

        lab50.setFont(frames.UGui.getFont(0,0));
        lab50.setText("2  соединение");
        lab50.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab50.setPreferredSize(new java.awt.Dimension(80, 18));

        txt36.setEditable(false);
        txt36.setFont(frames.UGui.getFont(0,0));
        txt36.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt36.setPreferredSize(new java.awt.Dimension(180, 18));

        txt37.setEditable(false);
        txt37.setFont(frames.UGui.getFont(0,0));
        txt37.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt37.setPreferredSize(new java.awt.Dimension(180, 18));

        lab55.setFont(frames.UGui.getFont(0,0));
        lab55.setText("Вариант");
        lab55.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab55.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lab55.setIconTextGap(6);
        lab55.setMaximumSize(new java.awt.Dimension(80, 19));
        lab55.setMinimumSize(new java.awt.Dimension(80, 19));
        lab55.setPreferredSize(new java.awt.Dimension(80, 19));

        txt38.setEditable(false);
        txt38.setFont(frames.UGui.getFont(0,0));
        txt38.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt38.setPreferredSize(new java.awt.Dimension(180, 18));

        lab56.setFont(frames.UGui.getFont(0,0));
        lab56.setText("Вариант");
        lab56.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab56.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lab56.setIconTextGap(6);
        lab56.setMaximumSize(new java.awt.Dimension(80, 19));
        lab56.setMinimumSize(new java.awt.Dimension(80, 19));
        lab56.setPreferredSize(new java.awt.Dimension(80, 19));

        txt39.setEditable(false);
        txt39.setFont(frames.UGui.getFont(0,0));
        txt39.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt39.setPreferredSize(new java.awt.Dimension(180, 18));

        lab54.setFont(frames.UGui.getFont(0,0));
        lab54.setText("3  прилегающее");
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
        lab57.setMaximumSize(new java.awt.Dimension(80, 19));
        lab57.setMinimumSize(new java.awt.Dimension(80, 19));
        lab57.setPreferredSize(new java.awt.Dimension(80, 19));

        txt41.setEditable(false);
        txt41.setFont(frames.UGui.getFont(0,0));
        txt41.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt41.setPreferredSize(new java.awt.Dimension(180, 18));

        lab58.setFont(frames.UGui.getFont(0,0));
        lab58.setText("Артикул 1,2");
        lab58.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab58.setIconTextGap(1);
        lab58.setPreferredSize(new java.awt.Dimension(80, 18));

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
                        .addComponent(txt36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt42, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab59, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt43, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt44, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan17Layout.createSequentialGroup()
                        .addComponent(lab50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pan17Layout.setVerticalGroup(
            pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(txt37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(txt40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        pan8.add(pan17, "card17");

        javax.swing.GroupLayout pan18Layout = new javax.swing.GroupLayout(pan18);
        pan18.setLayout(pan18Layout);
        pan18Layout.setHorizontalGroup(
            pan18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        pan18Layout.setVerticalGroup(
            pan18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 292, Short.MAX_VALUE)
        );

        pan8.add(pan18, "card18");

        pan5.add(pan8, java.awt.BorderLayout.NORTH);

        scr6.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        scr6.setPreferredSize(new java.awt.Dimension(4, 260));

        winTree.setFont(frames.UGui.getFont(0,0));
        scr6.setViewportView(winTree);

        pan5.add(scr6, java.awt.BorderLayout.CENTER);

        pan3.add(pan5, java.awt.BorderLayout.EAST);

        panDesign.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        panDesign.setLayout(new java.awt.BorderLayout());
        pan3.add(panDesign, java.awt.BorderLayout.CENTER);

        tabb1.addTab("        Изделия        ", pan3);

        pan6.setPreferredSize(new java.awt.Dimension(800, 500));
        pan6.setLayout(new java.awt.BorderLayout());

        scr4.setPreferredSize(new java.awt.Dimension(700, 240));

        tab4.setFont(frames.UGui.getFont(0,0));
        tab4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Изделие", "Артикул", "Название", "Текстура", "Внутренняя", "Внешняя", "Длина", "Ширина", "Кол-во", "Угол 1", "Угол 2", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, true, true, true, true, true, true, true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab4.setFillsViewportHeight(true);
        tab4.setPreferredSize(new java.awt.Dimension(700, 32));
        tab4.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });
        scr4.setViewportView(tab4);
        if (tab4.getColumnModel().getColumnCount() > 0) {
            tab4.getColumnModel().getColumn(0).setPreferredWidth(240);
            tab4.getColumnModel().getColumn(1).setPreferredWidth(100);
            tab4.getColumnModel().getColumn(2).setPreferredWidth(240);
            tab4.getColumnModel().getColumn(3).setPreferredWidth(80);
            tab4.getColumnModel().getColumn(4).setPreferredWidth(80);
            tab4.getColumnModel().getColumn(5).setPreferredWidth(80);
            tab4.getColumnModel().getColumn(6).setPreferredWidth(50);
            tab4.getColumnModel().getColumn(7).setPreferredWidth(50);
            tab4.getColumnModel().getColumn(8).setPreferredWidth(50);
            tab4.getColumnModel().getColumn(9).setPreferredWidth(50);
            tab4.getColumnModel().getColumn(10).setPreferredWidth(50);
            tab4.getColumnModel().getColumn(11).setPreferredWidth(40);
            tab4.getColumnModel().getColumn(11).setMaxWidth(60);
        }

        pan6.add(scr4, java.awt.BorderLayout.CENTER);

        tabb1.addTab("   Комплектация   ", pan6);

        centr.add(tabb1, java.awt.BorderLayout.CENTER);

        getContentPane().add(centr, java.awt.BorderLayout.CENTER);

        south.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.setMinimumSize(new java.awt.Dimension(100, 20));
        south.setPreferredSize(new java.awt.Dimension(900, 20));
        south.setLayout(new javax.swing.BoxLayout(south, javax.swing.BoxLayout.LINE_AXIS));
        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose
        this.dispose();
    }//GEN-LAST:event_btnClose

    private void btnDelete(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete
        UGui.stopCellEditing(tab1, tab2, tab3, tab4);
        List.of(tab1, tab2, tab3, tab4).forEach(tab -> ((DefTableModel) tab.getModel()).getQuery().execsql());
        if (tab1.getBorder() != null) {
            if (UGui.isDeleteRecord(tab1, this, tab2) == 0) {
                UGui.deleteRecord(tab1);
            }
        } else if (tab2.getBorder() != null) {
            if (UGui.isDeleteRecord(tab2, this, tab4) == 0) {
                UGui.deleteRecord(tab2);
            }
        } else if (tab4.getBorder() != null) {
            //TODO Если вставить комплект, а потом сразу удалить возникает ошибка
            if (UGui.isDeleteRecord(tab4, this) == 0) {
                UGui.deleteRecord(tab4);
            }
        }
    }//GEN-LAST:event_btnDelete

    private void btnInsert(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsert

        UGui.stopCellEditingAndExecSql(getRootPane());

        if (tab1.getBorder() != null) {
            UGui.insertRecordCur(tab1, eProject.up, (projectRec) -> {
                projectRec.set(eProject.manager, eProfile.user);
                projectRec.set(eProject.date4, UGui.getDateCur());
            });

        } else if (tab2.getBorder() != null) {
            //Вставка без UGui.insertRecordCur() т.к. рисунок добавляется в доп. поле
            new DicSyspod(this, (record) -> {
                Record prjprodRec = ePrjprod.up.newRecord(INS);
                prjprodRec.set(ePrjprod.id, Conn.genId(ePrjprod.up));
                prjprodRec.set(ePrjprod.name, record.getStr(eSysprod.name));
                prjprodRec.set(ePrjprod.num, 1);
                prjprodRec.set(ePrjprod.script, record.getStr(eSysprod.script));
                prjprodRec.set(ePrjprod.systree_id, record.getStr(eSysprod.systree_id));
                prjprodRec.set(ePrjprod.project_id, qProject.getAs(UGui.getIndexRec(tab1), eProject.id));
                ePrjprod.data().add(prjprodRec); //добавим в кэш новую запись
                qPrjprod.insert(prjprodRec);
            });

            loadingTab2();

            for (int index = 0; index < qPrjprod.size(); ++index) {
                if (qPrjprod.get(index, ePrjprod.id) == qPrjprod.get(index, ePrjprod.id)) {
                    UGui.setSelectedIndex(tab2, index);
                    UGui.scrollRectToRow(index, tab2);
                    winTree.setSelectionRow(0);
                }
            }
        } else if (tab4.getBorder() != null) {
            int index1 = UGui.getIndexRec(tab1);
            int index2 = UGui.getIndexRec(tab2);
            if (index1 != -1) {
                if (((JButton) evt.getSource()) == btnIns) {
                    if (index2 != -1) { //вставляем запись в продукт
                        UGui.insertRecordCur(tab4, ePrjkit.up, (record) -> {
                            record.set(ePrjkit.prjprod_id, qPrjprod.get(index2, ePrjprod.id));
                            record.set(ePrjkit.project_id, qProject.get(index1, eProject.id));
                            record.set(ePrjkit.numb, 0);
                            int index3 = UGui.getIndexFind(tab4, ePrjkit.id, record.get(ePrjkit.id));
                            qPrjkit.table(eArtikl.up).add(index3, eArtikl.up.newRecord(Query.SEL));
                        });
                    } else { //вставляем запись в проект
                        UGui.insertRecordCur(tab4, ePrjkit.up, (record) -> {
                            record.set(ePrjkit.project_id, qProject.get(index1, eProject.id));
                            record.set(ePrjkit.numb, 0);
                            int index3 = UGui.getIndexFind(tab4, ePrjkit.id, record.get(ePrjkit.id));
                            qPrjkit.table(eArtikl.up).add(index3, eArtikl.up.newRecord(Query.SEL));
                        });
                    }
                } else if (((JButton) evt.getSource()) == btnSet) { //вставляем сразу несколько записей
                    DicKits frame = new DicKits(Orders.this, (q) -> {
                        loadingTab4();
                        return true;
                    }, qProject.getAs(index1, eProject.id), qPrjprod.getAs(index2, ePrjprod.id));
                }
            } else {
                JOptionPane.showMessageDialog(this, "Заказ не выбран.", "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnInsert

    private void windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosed
        UGui.stopCellEditingAndExecSql(getRootPane());
        eProp.save(); //запишем текущий ordersId в файл  
    }//GEN-LAST:event_windowClosed

    private void mousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mousePressed
        JTable table = (JTable) evt.getSource();
        UGui.updateBorderAndSql(table, List.of(tab1, tab2, tab3, tab4));
    }//GEN-LAST:event_mousePressed

    private void stateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_stateChanged
        UGui.stopCellEditing(tab1, tab2, tab4, tab3);
        if (tabb1.getSelectedIndex() == 0) {
            UGui.updateBorderAndSql(tab1, List.of(tab1, tab2, tab3, tab4));
        } else if (tabb1.getSelectedIndex() == 1) {
            UGui.updateBorderAndSql(tab2, List.of(tab1, tab2, tab3, tab4));
        } else if (tabb1.getSelectedIndex() == 2) {
            UGui.updateBorderAndSql(tab4, List.of(tab1, tab2, tab3, tab4));
        }
    }//GEN-LAST:event_stateChanged

    private void btn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn1ActionPerformed
        DicCurrenc frame = new DicCurrenc(this, (currencRec) -> {

            int index = UGui.getIndexRec(tab1);
            if (index != -1) {
                Record record2 = qProject.get(index);
                record2.set(eProject.currenc_id, currencRec.get(eCurrenc.id));
                txt3.setText(currencRec.getStr(eCurrenc.name));
            }
            UGui.stopCellEditing(tab1, tab2, tab4, tab3);
        });
    }//GEN-LAST:event_btn1ActionPerformed

    private void colorToKorobka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorToKorobka
        try {
            if (winNode != null) {
                double selectID = winNode.com5t().id;
                int systreeID = qPrjprod.getAs(UGui.getIndexRec(tab2), ePrjprod.systree_id);
                Record systreeRec = eSystree.find(systreeID);
                eSystree field = (evt.getSource() == btn9) ? eSystree.col1 : (evt.getSource() == btn13) ? eSystree.col2 : eSystree.col3;
                HashSet<Record> colorSet = DicColor.filterTxt(eColor.data(), systreeRec.getStr(field));
                Integer[] arr2 = UCom.parserInt(systreeRec.getStr(field));

                ListenerRecord listenerColor = (colorRec) -> {

                    if (colorRec.get(1) != null) {
                        GsonElem jsonElem = UCom.gson(wincalc().listAll, selectID);
                        if (jsonElem != null) {
                            if (evt.getSource() == btn9) {
                                wincalc().gson.color1 = colorRec.getInt(eColor.id);
                            } else if (evt.getSource() == btn13) {
                                wincalc().gson.color2 = colorRec.getInt(eColor.id);
                            } else {
                                wincalc().gson.color3 = colorRec.getInt(eColor.id);
                            }
                            updateScript(selectID);
                        }
                    }
                };
                if (arr2.length == 0) {
                    new DicColor(this, listenerColor, false, false);
                } else {
                    new DicColor(this, listenerColor, colorSet, false, false);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка°: " + e);
        }
    }//GEN-LAST:event_colorToKorobka

    private void sysprofToFrame(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sysprofToFrame
        try {
            if (winNode != null) {
                Layout layout = winNode.com5t().layout();
                double selectID = winNode.com5t().id;
                int systreeID = qPrjprod.getAs(UGui.getIndexRec(tab2), ePrjprod.systree_id);
                Query qSysprof = new Query(eSysprof.values(), eArtikl.values()).sql(eSysprof.data(), eSysprof.systree_id, systreeID);
                qSysprof.table(eArtikl.up).join2(qSysprof, eArtikl.data(), eSysprof.artikl_id, eArtikl.id);
                Query qSysprof2 = new Query(eSysprof.values(), eArtikl.values());

                //Отфильтруем подходящие по параметрам
                for (int index = 0; index < qSysprof.size(); ++index) {
                    Record sysprofRec = qSysprof.get(index);
                    if (winNode.com5t().type.id2 == sysprofRec.getInt(eSysprof.use_type)) {
                        if (sysprofRec.getInt(eSysprof.use_side) == winNode.com5t().layout().id
                                || sysprofRec.getInt(eSysprof.use_side) == UseSideTo.ANY.id
                                || sysprofRec.getInt(eSysprof.use_side) == UseSideTo.MANUAL.id) {
                            qSysprof2.add(sysprofRec);
                            qSysprof2.table(eArtikl.up).add(qSysprof.table(eArtikl.up).get(index));
                        }
                    }
                }
                new DicSysprof(this, (sysprofRec) -> {
                    Wincalc winc = wincalc();
                    if (winNode.com5t().type == enums.Type.FRAME_SIDE) { //рама окна
                        final double gsonId = winNode.com5t().id;
                        GsonElem gsonRama = UCom.gson(wincalc().listAll, gsonId);
                        if (sysprofRec.get(1) == null) {
                            gsonRama.param.remove(PKjson.sysprofID);
                        } else {
                            gsonRama.param.addProperty(PKjson.sysprofID, sysprofRec.getInt(eSysprof.id));
                        }
                        updateScript(selectID);

                    } else if (winNode.com5t().type == enums.Type.STVORKA_SIDE) { //рама створки
                        double stvId = winNode.com5t().owner.id;
                        GsonElem stvArea = UCom.gson(wincalc().listAll, stvId);
                        JsonObject paramObj = stvArea.param;
                        String stvKey = null;
                        if (layout == Layout.BOTT) {
                            stvKey = PKjson.stvorkaBot;
                        } else if (layout == Layout.RIGHT) {
                            stvKey = PKjson.stvorkaRig;
                        } else if (layout == Layout.TOP) {
                            stvKey = PKjson.stvorkaTop;
                        } else if (layout == Layout.LEFT) {
                            stvKey = PKjson.stvorkaLef;
                        }
                        JsonObject jso = UGui.getAsJsonObject(paramObj, stvKey);
                        if (sysprofRec.get(1) == null) {
                            jso.remove(PKjson.sysprofID);
                        } else {
                            jso.addProperty(PKjson.sysprofID, sysprofRec.getStr(eSysprof.id));
                        }
                        updateScript(selectID);

                    } else {  //импост
                        double elemId = winNode.com5t().id;
                        GsonElem gsonElem = UCom.gson(wincalc().listAll, elemId);
                        if (sysprofRec.get(1) == null) {
                            gsonElem.param.remove(PKjson.sysprofID);
                        } else {
                            gsonElem.param.addProperty(PKjson.sysprofID, sysprofRec.getInt(eSysprof.id));
                        }
                        updateScript(selectID);
                    }

                }, qSysprof2);
            }
        } catch (Exception e) {
            System.err.println("Ошибка: " + e);
        }
    }//GEN-LAST:event_sysprofToFrame

    private void colorToFrame(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorToFrame
        try {
            double selectID = winNode.com5t().id;
            int systreeID = qPrjprod.getAs(UGui.getIndexRec(tab2), ePrjprod.systree_id);
            Record systreeRec = eSystree.find(systreeID);
            eSystree col = (evt.getSource() == btn18) ? eSystree.col1 : (evt.getSource() == btn19) ? eSystree.col2 : eSystree.col3;
            Field field = (evt.getSource() == btn18) ? eArtdet.mark_c1 : (evt.getSource() == btn19) ? eArtdet.mark_c2 : eArtdet.mark_c3;
            Query artdetList = new Query(eArtdet.values()).sql(eArtdet.data(), eArtdet.artikl_id, winNode.com5t().artiklRec.getInt(eArtikl.id));

            HashSet<Record> colorSrc = DicColor.filterTxt(eColor.data(), systreeRec.getStr(col));
            HashSet<Record> colorSet = DicColor.filterDet(colorSrc, artdetList, field);

            DicColor frame = new DicColor(this, (colorRec) -> {

                String colorID = (evt.getSource() == btn18) ? PKjson.colorID1 : (evt.getSource() == btn19) ? PKjson.colorID2 : PKjson.colorID3;
                double parentId = winNode.com5t().owner.id;
                GsonElem parentArea = UCom.gson(wincalc().listAll, parentId);

                if (winNode.com5t().type == enums.Type.STVORKA_SIDE) {
                    JsonObject paramObj = parentArea.param;
                    String stvKey = null;
                    if (winNode.com5t().layout() == Layout.BOTT) {
                        stvKey = PKjson.stvorkaBot;
                    } else if (winNode.com5t().layout() == Layout.RIGHT) {
                        stvKey = PKjson.stvorkaRig;
                    } else if (winNode.com5t().layout() == Layout.TOP) {
                        stvKey = PKjson.stvorkaTop;
                    } else if (winNode.com5t().layout() == Layout.LEFT) {
                        stvKey = PKjson.stvorkaLef;
                    }

                    JsonObject jso = UGui.getAsJsonObject(paramObj, stvKey);
                    if (colorRec.get(1) == null) {
                        jso.remove(colorID);
                    } else {
                        jso.addProperty(colorID, colorRec.getStr(eColor.id));
                    }
                    updateScript(selectID);

                } else if (winNode.com5t().type == enums.Type.FRAME_SIDE) {
                    for (GsonElem elem : parentArea.childs) {
                        if (elem.id == ((DefMutableTreeNode) winNode).com5t().id) {
                            if (colorRec.get(1) == null) {
                                elem.param.remove(colorID);
                            } else {
                                elem.param.addProperty(colorID, colorRec.getStr(eColor.id));
                            }
                            updateScript(selectID);
                        }
                    }
                } else if (winNode.com5t().type == enums.Type.IMPOST
                        || winNode.com5t().type == enums.Type.STOIKA
                        || winNode.com5t().type == enums.Type.SHTULP) {
                    for (GsonElem elem : parentArea.childs) {
                        if (elem.id == ((DefMutableTreeNode) winNode).com5t().id) {
                            if (colorRec.get(1) == null) {
                                elem.param.remove(colorID);
                            } else {
                                elem.param.addProperty(colorID, colorRec.getStr(eColor.id));
                            }
                            updateScript(selectID);
                        }
                    }
                }

            }, colorSet, true, false);
        } catch (Exception e) {
            System.err.println("Ошибка: " + e);
        }
    }//GEN-LAST:event_colorToFrame

    private void sysfurnToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sysfurnToStvorka
        try {
            double windowsID = winNode.com5t().id;
            int systreeID = qPrjprod.getAs(UGui.getIndexRec(tab2), ePrjprod.systree_id);
            Query qSysfurn = new Query(eSysfurn.values(), eFurniture.values()).sql(eSysfurn.data(), eSysfurn.systree_id, systreeID);
            qSysfurn.table(eFurniture.up).join2(qSysfurn, eFurniture.data(), eSysfurn.furniture_id, eFurniture.id);
            new DicName(this, (sysfurnRec) -> {

                GsonElem stvArea = UCom.gson(wincalc().listAll, windowsID);
                if (sysfurnRec.get(1) == null) {
                    stvArea.param.remove(PKjson.sysfurnID);
                } else {
                    stvArea.param.addProperty(PKjson.sysfurnID, sysfurnRec.getStr(eSysfurn.id));
                }
                updateScript(windowsID);

            }, qSysfurn, eFurniture.name);

        } catch (Exception e) {
            System.err.println("Ошибка: " + e);
        }
    }//GEN-LAST:event_sysfurnToStvorka

    private void typeOpenToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeOpenToStvorka
        try {
            new DicEnums(this, (typeopenRec) -> {

                double elemID = winNode.com5t().id;
                GsonElem stvArea = UCom.gson(wincalc().listAll, elemID);
                if (typeopenRec.get(1) == null) {
                    stvArea.param.remove(PKjson.typeOpen);
                } else {
                    stvArea.param.addProperty(PKjson.typeOpen, typeopenRec.getInt(0));
                }
                updateScript(elemID);

            }, TypeOpen1.LEFT, TypeOpen1.LEFTUP, TypeOpen1.LEFMOV,
                    TypeOpen1.RIGH, TypeOpen1.RIGHUP, TypeOpen1.RIGMOV, TypeOpen1.UPPER, TypeOpen1.EMPTY);
        } catch (Exception e) {
            System.err.println("Ошибка: " + e);
        }
    }//GEN-LAST:event_typeOpenToStvorka

    private void colorToHandl(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorToHandl
        try {
            double selectID = winNode.com5t().id;
            AreaStvorka stv = (AreaStvorka) winNode.com5t();
            HashSet<Record> colorSet = UGui.artiklToColorSet(stv.knobRec.getInt(eArtikl.id));
            DicColor frame = new DicColor(this, (colorRec) -> {

                GsonElem stvArea = UCom.gson(wincalc().listAll, selectID);
                if (colorRec.get(1) == null) {
                    stvArea.param.remove(PKjson.colorKnob);
                } else {
                    stvArea.param.addProperty(PKjson.colorKnob, colorRec.getStr(eColor.id));
                }
                updateScript(selectID);

            }, colorSet, true, false);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorToHandl() " + e);
        }
    }//GEN-LAST:event_colorToHandl

    private void handkToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_handkToStvorka
        try {
            double stvorkaID = winNode.com5t().id;
            int furnitureID = ((AreaStvorka) winNode.com5t()).sysfurnRec.getInt(eSysfurn.furniture_id);
            Query qArtikl = new Query(eArtikl.values()).sql(eArtikl.data(), eArtikl.level1, 2, eArtikl.level2, 11);
            Query qResult = UGui.artTypeToFurndetList(furnitureID, qArtikl);
            new DicArtikl(this, (artiklRec) -> {

                GsonElem stvArea = UCom.gson(wincalc().listAll, stvorkaID);
                stvArea.param.remove(PKjson.colorKnob);
                if (artiklRec.get(1) == null) {
                    stvArea.param.remove(PKjson.artiklKnob);
                } else {
                    stvArea.param.addProperty(PKjson.artiklKnob, artiklRec.getStr(eArtikl.id));
                }
                updateScript(stvorkaID);

            }, qResult);

        } catch (Exception e) {
            System.err.println("Ошибка: " + e);
        }
    }//GEN-LAST:event_handkToStvorka

    private void heightHandlToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_heightHandlToStvorka

        AreaSimple areaStv = (AreaSimple) winNode.com5t();
        int indexLayoutHandl = 0;
        if (LayoutKnob.CONST.name.equals(txt16.getText())) {
            indexLayoutHandl = 1;
        } else if (LayoutKnob.VAR.name.equals(txt16.getText())) {
            indexLayoutHandl = 2;
        }
        new DicHandl(this, (record) -> {
            try {
                double selectID = areaStv.id;
                GsonElem jsonStv = UCom.gson(wincalc().listAll, selectID);

                if (record.getInt(0) == 0) {
                    jsonStv.param.addProperty(PKjson.positionKnob, LayoutKnob.MIDL.id);
                    txt31.setEditable(false);

                } else if (record.getInt(0) == 1) {
                    jsonStv.param.addProperty(PKjson.positionKnob, LayoutKnob.CONST.id);
                    txt31.setEditable(false);

                } else if (record.getInt(0) == 2) {
                    jsonStv.param.addProperty(PKjson.positionKnob, LayoutKnob.VAR.id);
                    jsonStv.param.addProperty(PKjson.heightKnob, record.getInt(1));
                    txt31.setEditable(true);
                }
                updateScript(selectID);

            } catch (Exception e) {
                System.err.println("Ошибка: " + e);
            }

        }, indexLayoutHandl);
    }//GEN-LAST:event_heightHandlToStvorka

    private void artiklToGlass(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_artiklToGlass
        try {
            double selectID = winNode.com5t().id;
            int systreeID = qPrjprod.getAs(UGui.getIndexRec(tab2), ePrjprod.systree_id);
            Record systreeRec = eSystree.find(systreeID);
            String depth = systreeRec.getStr(eSystree.depth);
            if (depth != null && depth.isEmpty() == false) {
                depth = depth.replace(";;;", ";").replace(";;", ";");
                depth = depth.replace(";", ",");
                if (depth.charAt(depth.length() - 1) == ',') {
                    depth = depth.substring(0, depth.length() - 1);
                }
            }
            //Список стеклопакетов
            Query qData = new Query(eArtikl.values()), qArtikl = new Query(eArtikl.values());
            List<Double> listID = (depth != null && depth.isEmpty() == false)
                    ? List.of(depth.split(",")).stream().map(m -> Double.valueOf(m)).collect(Collectors.toList()) : new ArrayList();
            qData.sql(eArtikl.data(), eArtikl.level1, 5, eArtikl.level2, 1, 2, 3).sort(eArtikl.name);
            qArtikl.addAll(qData.stream().filter(rec -> listID.contains(rec.getDbl(eArtikl.depth))).collect(Collectors.toList()));
            int artiklID = (winNode.com5t().artiklRec != null) ? winNode.com5t().artiklRec.getInt(eArtikl.id) : -3;

            new DicArtikl(this, artiklID, (artiklRec) -> {

                GsonElem glassElem = UCom.gson(wincalc().listAll, selectID);
                if (artiklRec.get(1) == null) {
                    glassElem.param.remove(PKjson.artglasID);
                } else {
                    glassElem.param.addProperty(PKjson.artglasID, artiklRec.getStr(eArtikl.id));
                }
                updateScript(selectID);

            }, qArtikl);

        } catch (Exception e) {
            System.err.println("Ошибка: " + e);
        }
    }//GEN-LAST:event_artiklToGlass

    private void btnCalc(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalc
        ProgressBar.create(Orders.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                calculate();
            }
        });
    }//GEN-LAST:event_btnCalc

    private void btnFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilter
        loadingTab1();
    }//GEN-LAST:event_btnFilter

    private void btnTest(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTest
        System.out.println(eProp.orderID.read());
    }//GEN-LAST:event_btnTest

    private void loopToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loopToStvorka
        try {
            double selectID = winNode.com5t().id;
            int furnitureID = ((AreaStvorka) winNode.com5t()).sysfurnRec.getInt(eSysfurn.furniture_id);
            Query qArtikl = new Query(eArtikl.values()).sql(eArtikl.data(), eArtikl.level1, 2, eArtikl.level2, 12);
            Query qResult = UGui.artTypeToFurndetList(furnitureID, qArtikl);
            new DicArtikl(this, (artiklRec) -> {

                GsonElem stvArea = UCom.gson(wincalc().listAll, selectID);
                stvArea.param.remove(PKjson.colorLoop);
                if (artiklRec.get(1) == null) {
                    stvArea.param.remove(PKjson.artiklLoop);
                } else {
                    stvArea.param.addProperty(PKjson.artiklLoop, artiklRec.getStr(eArtikl.id));
                }
                updateScript(selectID);

            }, qResult);

        } catch (Exception e) {
            System.err.println("Ошибка: " + e);
        }
    }//GEN-LAST:event_loopToStvorka

    private void colorFromLoop(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorFromLoop
        try {
            double selectID = winNode.com5t().id;
            AreaStvorka stv = (AreaStvorka) winNode.com5t();
            HashSet<Record> colorSet = UGui.artiklToColorSet(stv.loopRec.getInt(eArtikl.id));
            DicColor frame = new DicColor(this, (colorRec) -> {

                GsonElem stvArea = UCom.gson(wincalc().listAll, selectID);
                if (colorRec.get(1) == null) {
                    stvArea.param.remove(PKjson.colorLoop);
                } else {
                    stvArea.param.addProperty(PKjson.colorLoop, colorRec.getStr(eColor.id));
                }
                updateScript(selectID);

            }, colorSet, true, false);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorToHandl() " + e);
        }
    }//GEN-LAST:event_colorFromLoop

    private void lockToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lockToStvorka
        try {
            double selectID = winNode.com5t().id;
            int furnitureID = ((AreaStvorka) winNode.com5t()).sysfurnRec.getInt(eSysfurn.furniture_id);
            Query qArtikl = new Query(eArtikl.values()).sql(eArtikl.data(), eArtikl.level1, 2, eArtikl.level2, 9);
            Query qResult = UGui.artTypeToFurndetList(furnitureID, qArtikl);
            new DicArtikl(this, (artiklRec) -> {

                GsonElem stvArea = UCom.gson(wincalc().listAll, selectID);
                stvArea.param.remove(PKjson.colorLock);
                if (artiklRec.get(1) == null) {
                    stvArea.param.remove(PKjson.artiklLock);
                } else {
                    stvArea.param.addProperty(PKjson.artiklLock, artiklRec.getStr(eArtikl.id));
                }
                updateScript(selectID);

            }, qResult);

        } catch (Exception e) {
            System.err.println("Ошибка: " + e);
        }
    }//GEN-LAST:event_lockToStvorka

    private void colorFromLock(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorFromLock
        try {
            double selectID = winNode.com5t().id;
            AreaStvorka stv = (AreaStvorka) winNode.com5t();
            HashSet<Record> colorSet = UGui.artiklToColorSet(stv.lockRec.getInt(eArtikl.id));
            DicColor frame = new DicColor(this, (colorRec) -> {

                GsonElem stvArea = UCom.gson(wincalc().listAll, selectID);
                if (colorRec.get(1) == null) {
                    stvArea.param.remove(PKjson.colorLock);
                } else {
                    stvArea.param.addProperty(PKjson.colorLock, colorRec.getStr(eColor.id));
                }
                updateScript(selectID);

            }, colorSet, true, false);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorToHandl() " + e);
        }
    }//GEN-LAST:event_colorFromLock

    private void txtKeyEnter(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKeyEnter

    }//GEN-LAST:event_txtKeyEnter

    private void btnFind(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFind
        if (tab1.getBorder() != null) {
            Record record = ((DefTableModel) tab1.getModel()).getQuery().get(UGui.getIndexRec(tab1));
            if (record != null) {
                ProgressBar.create(this, new ListenerFrame() {
                    public void actionRequest(Object obj) {
                        App.Partner.createFrame(Orders.this, record.getInt(eProject.prjpart_id));
                    }
                });
            }
        } else if (tab4.getBorder() != null) {
            Record record = ((DefTableModel) tab4.getModel()).getQuery().get(UGui.getIndexRec(tab4));
            if (record != null) {
                Record record2 = eArtikl.find(record.getInt(ePrjkit.artikl_id), false);
                ProgressBar.create(this, new ListenerFrame() {
                    public void actionRequest(Object obj) {
                        App.Artikles.createFrame(Orders.this, record2);
                    }
                });
            }
        }
    }//GEN-LAST:event_btnFind

    private void colorFromGlass(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorFromGlass
        try {
            double selectID = winNode.com5t().id;
            ElemSimple glas = (ElemSimple) winNode.com5t();
            HashSet<Record> colorSet = UGui.artiklToColorSet(glas.artiklRec.getInt(eArtikl.id));
            DicColor frame = new DicColor(this, (colorRec) -> {

                GsonElem stvArea = UCom.gson(wincalc().listAll, selectID);
                if (colorRec.get(1) == null) {
                    stvArea.param.remove(PKjson.colorGlass);
                } else {
                    stvArea.param.addProperty(PKjson.colorGlass, colorRec.getStr(eColor.id));
                }
                updateScript(selectID);

            }, colorSet, false, false);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorFromGlass() " + e);
        }
    }//GEN-LAST:event_colorFromGlass

    private void menuItem11(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem11
        ProgressBar.create(Orders.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                //Расход мат.
                Record projectRec = qProject.get(UGui.getIndexRec(tab1));
                List<Record> prjprodList = ePrjprod.filter(projectRec.getInt(eProject.id));
                new RMaterial().parseDoc(prjprodList);
            }
        });
    }//GEN-LAST:event_menuItem11

    private void menuItem12(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem12
        ProgressBar.create(Orders.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                //Спецификация
                Record projectRec = qProject.get(UGui.getIndexRec(tab1, 1));
                List<Record> prjprodList = ePrjprod.filter(projectRec.getInt(eProject.id));
                new RSpecific().parseDoc(prjprodList);
            }
        });
    }//GEN-LAST:event_menuItem12

    private void menuItem13(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem13
        ProgressBar.create(Orders.this, new ListenerFrame() {
            @Override
            public void actionRequest(Object obj) {
                //Смета
                Record projectRec = qProject.get(UGui.getIndexRec(tab1));
                List<Record> prjprodList = ePrjprod.filter(projectRec.getInt(eProject.id));
                new RSmeta().parseDoc2(prjprodList);
            }
        });
    }//GEN-LAST:event_menuItem13

    private void menuItem14(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem14
        ProgressBar.create(Orders.this, new ListenerFrame() {
            @Override
            public void actionRequest(Object obj) {
                //Отчёт
                Record projectRec = qProject.get(UGui.getIndexRec(tab1));
                List<Record> prjprodList = ePrjprod.filter(projectRec.getInt(eProject.id));
                new RSmeta().parseDoc1(prjprodList);
            }
        });
    }//GEN-LAST:event_menuItem14

    private void menuItem15(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem15
        ProgressBar.create(Orders.this, new ListenerFrame() {
            @Override
            public void actionRequest(Object obj) {
                //Отчёт
                new RCheck().parseDoc1(qProject.get(UGui.getIndexRec(tab1)));
            }
        });
    }//GEN-LAST:event_menuItem15

    private void menuItem16(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem16
        ProgressBar.create(Orders.this, new ListenerFrame() {
            @Override
            public void actionRequest(Object obj) {
                //Отчёт
                new RCheck().parseDoc2(qProject.get(UGui.getIndexRec(tab1)));
            }
        });
    }//GEN-LAST:event_menuItem16

    private void menuItem17(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem17
        ProgressBar.create(Orders.this, new ListenerFrame() {
            @Override
            public void actionRequest(Object obj) {
                //Отчёт
                new ROffer().parseDoc(qProject.get(UGui.getIndexRec(tab1)));
            }
        });
    }//GEN-LAST:event_menuItem17

    private void mosquitToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mosquitToStvorka
        try {
            AreaSimple stvElem = (AreaSimple) winNode.com5t();
            double selectID = winNode.com5t().id;
            Query qArtikl = new Query(eArtikl.values()).sql(eArtikl.data(), eArtikl.level1, 5, eArtikl.level2, 20);

            new DicArtikl(this, (artiklRec) -> {

                GsonElem gsonElem = null;
                ArrayList<Com5t> mosqList = UCom.filter(((AreaSimple) stvElem).childs, enums.Type.MOSQUIT);

                if (mosqList.isEmpty() == false) {
                    ElemSimple mosqElem = (ElemSimple) mosqList.get(0);
                    gsonElem = mosqElem.gson;
                } else {
                    gsonElem = new GsonElem(enums.Type.MOSQUIT);
                    GsonElem stvArea = stvElem.gson;
                    stvArea.childs.add(gsonElem);
                }
                if (artiklRec.get(1) == null) {
                    gsonElem.param.remove(PKjson.artiklID);
                    gsonElem.param.remove(PKjson.elementID);
                } else {
                    gsonElem.param.addProperty(PKjson.artiklID, artiklRec.getStr(eArtikl.id));
                }
                updateScript(selectID);

            }, qArtikl);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.mosquitToStvorka() " + e);
        }
    }//GEN-LAST:event_mosquitToStvorka

    private void mosqToColor(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mosqToColor
        try {
            double selectID = winNode.com5t().id;
            AreaStvorka stvElem = (AreaStvorka) winNode.com5t();
            ArrayList<Com5t> mosqList = UCom.filter(((AreaSimple) stvElem).childs, enums.Type.MOSQUIT);
            if (mosqList.isEmpty() == false) {
                ElemMosquit mosqElem = (ElemMosquit) mosqList.get(0);
                HashSet<Record> colorSet = UGui.artiklToColorSet(mosqElem.artiklRec.getInt(eArtikl.id));
                DicColor frame = new DicColor(this, (colorRec) -> {

                    if (colorRec.get(1) == null) {
                        mosqElem.gson.param.remove(PKjson.colorID1);
                    } else {
                        mosqElem.gson.param.addProperty(PKjson.colorID1, colorRec.getStr(eColor.id));
                    }
                    updateScript(selectID);

                }, colorSet, true, false);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorToHandl() " + e);
        }
    }//GEN-LAST:event_mosqToColor

    private void mosqToElements(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mosqToElements
        try {
            double selectID = winNode.com5t().id;
            AreaSimple stvElem = (AreaSimple) winNode.com5t();
            ArrayList<Com5t> mosqList = UCom.filter(stvElem.childs, enums.Type.MOSQUIT);
            if (mosqList.isEmpty() == false) {
                ElemSimple mosqElem = (ElemSimple) mosqList.get(0);
                Record artiklRec = mosqElem.artiklRec;
                Query qElements = new Query(eElement.values()).sql(eElement.data(), eElement.artikl_id, artiklRec.getInt(eArtikl.id));

                new DicName(this, (elementRec) -> {

                    if (elementRec.get(1) == null) {
                        mosqElem.gson.param.remove(PKjson.elementID);
                    } else {
                        mosqElem.gson.param.addProperty(PKjson.elementID, elementRec.getStr(eElement.id));
                    }
                    updateScript(selectID);

                }, qElements, eElement.name);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.mosqToElements() " + e);
        }
    }//GEN-LAST:event_mosqToElements

    private void ppmActionItems(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppmActionItems
        if (evt.getSource() == mInsert) {
            btnInsert(new java.awt.event.ActionEvent(btnIns, -1, ""));
        } else if (evt.getSource() == mDelit) {
            btnDelete(new java.awt.event.ActionEvent(btnDel, -1, ""));
        }
    }//GEN-LAST:event_ppmActionItems

    private void tabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {
            JTable table = List.of(tab1, tab2, tab4).stream().filter(it -> it == evt.getSource()).findFirst().get();
            List.of(tab1, tab2, tab4).forEach(tab -> tab.setBorder(null));
            table.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255)));
            ppmCrud.show(table, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tabMouseClicked

    private void menuItem18(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem18
        ProgressBar.create(Orders.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                //Задание в цех
                Record projectRec = qProject.get(UGui.getIndexRec(tab1));
                List<Record> prjprodList = ePrjprod.filter(projectRec.getInt(eProject.id));
                new RTarget().parseDoc(prjprodList);
            }
        });
    }//GEN-LAST:event_menuItem18

    private void menuItem19(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem19
        ProgressBar.create(Orders.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                //Спецификация
                List<Record> prjprodList = List.of(qPrjprod.get(UGui.getIndexRec(tab2)));
                new RSpecific().parseDoc(prjprodList);
            }
        });
    }//GEN-LAST:event_menuItem19

    private void menuItem20(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem20
        ProgressBar.create(Orders.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                //Расход мат.
                List<Record> prjprodList = List.of(qPrjprod.get(UGui.getIndexRec(tab2)));
                new RMaterial().parseDoc(prjprodList);
            }
        });
    }//GEN-LAST:event_menuItem20

    private void menuItem21(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem21
        ProgressBar.create(Orders.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                //Расход мат.
                List<Record> prjprodList = List.of(qPrjprod.get(UGui.getIndexRec(tab2)));
                new RTarget().parseDoc(prjprodList);
            }
        });
    }//GEN-LAST:event_menuItem21

    private void menuItem22(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem22
        ProgressBar.create(Orders.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                //Смета
                List<Record> prjprodList = List.of(qPrjprod.get(UGui.getIndexRec(tab2)));
                new RSmeta().parseDoc1(prjprodList);
            }
        });
    }//GEN-LAST:event_menuItem22

    private void menuItem23(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem23
        //Смета
        List<Record> prjprodList = List.of(qPrjprod.get(UGui.getIndexRec(tab2)));
        new RSmeta().parseDoc2(prjprodList);
    }//GEN-LAST:event_menuItem23

    private void menuItem24(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem24

    }//GEN-LAST:event_menuItem24

    private void menuItem25(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem25
        // TODO add your handling code here:
    }//GEN-LAST:event_menuItem25

    private void menuItem26(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem26
        // TODO add your handling code here:
    }//GEN-LAST:event_menuItem26

    private void btnRepor(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRepor
        ppReport.show(north, btnReport.getX(), btnReport.getY() + 18);
    }//GEN-LAST:event_btnRepor

// <editor-fold defaultstate="collapsed" desc="Generated Code"> 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn1;
    private javax.swing.JButton btn10;
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
    private javax.swing.JButton btn3;
    private javax.swing.JButton btn31;
    private javax.swing.JButton btn32;
    private javax.swing.JButton btn6;
    private javax.swing.JButton btn9;
    private javax.swing.JButton btnCalc;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDel;
    private javax.swing.JToggleButton btnF1;
    private javax.swing.JToggleButton btnF2;
    private javax.swing.JToggleButton btnF3;
    private javax.swing.JButton btnFind;
    private javax.swing.JButton btnIns;
    private javax.swing.JToggleButton btnReport;
    private javax.swing.JButton btnSet;
    private javax.swing.JButton btnTest;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JPanel centr;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jmenu01;
    private javax.swing.JMenu jmenu02;
    private javax.swing.JLabel lab1;
    private javax.swing.JLabel lab26;
    private javax.swing.JLabel lab27;
    private javax.swing.JLabel lab28;
    private javax.swing.JLabel lab29;
    private javax.swing.JLabel lab3;
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
    private javax.swing.JLabel lab66;
    private javax.swing.JLabel lab69;
    private javax.swing.JLabel lab7;
    private javax.swing.JLabel lab8;
    private javax.swing.JMenuItem mDelit;
    private javax.swing.JMenuItem mInsert;
    private javax.swing.JMenuItem menuItem11;
    private javax.swing.JMenuItem menuItem12;
    private javax.swing.JMenuItem menuItem13;
    private javax.swing.JMenuItem menuItem14;
    private javax.swing.JMenuItem menuItem15;
    private javax.swing.JMenuItem menuItem16;
    private javax.swing.JMenuItem menuItem17;
    private javax.swing.JMenuItem menuItem18;
    private javax.swing.JMenuItem menuItem19;
    private javax.swing.JMenuItem menuItem20;
    private javax.swing.JMenuItem menuItem21;
    private javax.swing.JMenuItem menuItem22;
    private javax.swing.JMenuItem menuItem23;
    private javax.swing.JMenuItem menuItem24;
    private javax.swing.JMenuItem menuItem25;
    private javax.swing.JMenuItem menuItem26;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan1;
    private javax.swing.JPanel pan11;
    private javax.swing.JPanel pan12;
    private javax.swing.JPanel pan13;
    private javax.swing.JPanel pan14;
    private javax.swing.JPanel pan15;
    private javax.swing.JPanel pan16;
    private javax.swing.JPanel pan17;
    private javax.swing.JPanel pan18;
    private javax.swing.JPanel pan19;
    private javax.swing.JPanel pan20;
    private javax.swing.JPanel pan21;
    private javax.swing.JPanel pan22;
    private javax.swing.JPanel pan23;
    private javax.swing.JPanel pan24;
    private javax.swing.JPanel pan3;
    private javax.swing.JPanel pan5;
    private javax.swing.JPanel pan6;
    private javax.swing.JPanel pan7;
    private javax.swing.JPanel pan8;
    private javax.swing.JPanel pan9;
    private javax.swing.JPanel panDesign;
    private javax.swing.JPopupMenu ppReport;
    private javax.swing.JPopupMenu ppmCrud;
    private javax.swing.JScrollPane scr1;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JScrollPane scr3;
    private javax.swing.JScrollPane scr4;
    private javax.swing.JScrollPane scr5;
    private javax.swing.JScrollPane scr6;
    private javax.swing.JPopupMenu.Separator sep1;
    private javax.swing.JPopupMenu.Separator sep2;
    private javax.swing.JPopupMenu.Separator sep3;
    private javax.swing.JPopupMenu.Separator sep4;
    private javax.swing.JPopupMenu.Separator sep5;
    private javax.swing.JPopupMenu.Separator sep6;
    private javax.swing.JPanel south;
    private javax.swing.JTable tab1;
    private javax.swing.JTable tab2;
    private javax.swing.JTable tab3;
    private javax.swing.JTable tab4;
    private javax.swing.JTable tab5;
    private javax.swing.JTabbedPane tabb1;
    private javax.swing.JTabbedPane tabb2;
    private javax.swing.JTextArea txt12;
    private javax.swing.JTextField txt13;
    private javax.swing.JTextField txt14;
    private javax.swing.JTextField txt16;
    private javax.swing.JTextField txt17;
    private javax.swing.JTextField txt18;
    private javax.swing.JTextField txt19;
    private javax.swing.JTextField txt20;
    private javax.swing.JTextField txt21;
    private javax.swing.JTextField txt22;
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
    private javax.swing.JTextField txt36;
    private javax.swing.JTextField txt37;
    private javax.swing.JTextField txt38;
    private javax.swing.JTextField txt39;
    private javax.swing.JTextField txt40;
    private javax.swing.JTextField txt41;
    private javax.swing.JTextField txt42;
    private javax.swing.JTextField txt43;
    private javax.swing.JTextField txt44;
    private javax.swing.JTextField txt45;
    private javax.swing.JTextField txt46;
    private javax.swing.JTextField txt47;
    private javax.swing.JTextField txt48;
    private javax.swing.JTextField txt54;
    private javax.swing.JTextField txt55;
    private javax.swing.JTextField txt56;
    private javax.swing.JTextField txt57;
    private javax.swing.JTextField txt58;
    private javax.swing.JTextField txt60;
    private javax.swing.JTextField txt7;
    private javax.swing.JTextField txt8;
    private javax.swing.JTextField txt9;
    private javax.swing.JTree winTree;
    // End of variables declaration//GEN-END:variables
// </editor-fold> 

    private void initElements() {
        new FrameToFile(this, btnClose);
        new UColor();

        TableFieldFilter filterTable = new TableFieldFilter(0, tab1);
        south.add(filterTable, 0);
        filterTable.getTxt().grabFocus();

        panDesign.add(scene, java.awt.BorderLayout.CENTER);
        //UGui.documentFilter(3, txt7);
        List.of(btnIns, btnDel).forEach(b -> b.addActionListener(l -> UGui.stopCellEditing(tab1)));
        winTree.getSelectionModel().addTreeSelectionListener(tse -> selectionTree());
        DefaultTreeCellRenderer rnd2 = (DefaultTreeCellRenderer) winTree.getCellRenderer();
        rnd2.setLeafIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b038.gif")));
        rnd2.setOpenIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b007.gif")));
        rnd2.setClosedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b006.gif")));
        tab1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    selectionTab1();
                }
            }
        });
        tab2.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false && tab2.getSelectedRow() != -1) {
                    tab2.setRowSelectionInterval(tab2.getSelectedRow(), tab2.getSelectedRow());
                    selectionTab2();
                }
            }
        });
        winTree.getSelectionModel().addTreeSelectionListener(tse -> selectionTree());
        DefaultTreeModel model = (DefaultTreeModel) winTree.getModel();
        ((DefaultMutableTreeNode) model.getRoot()).removeAllChildren();
        model.reload();
        tabb1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {

                if (tabb1.getSelectedIndex() == 1) {
                    canvas.init(wincalc());  //т.к. при смене вклвдки терятся keyPressed(KeyEvent event)

                } else if (tabb1.getSelectedIndex() == 2) {
                    btnSet.setEnabled(true);
                    btnFind.setEnabled(true);

                } else {
                    btnSet.setEnabled(false);
                    btnFind.setEnabled(false);
                }
            }
        });
    }
}
