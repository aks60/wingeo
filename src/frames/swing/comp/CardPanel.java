package frames.swing.comp;

import builder.Wincalc;
import builder.making.TFurniture;
import builder.making.TJoining;
import builder.making.UColor;
import builder.model.AreaSimple;
import builder.model.AreaStvorka;
import builder.model.Com5t;
import builder.model.ElemGlass;
import builder.model.ElemJoining;
import builder.model.ElemSimple;
import builder.model.UPar;
import builder.script.GsonElem;
import common.UCom;
import common.listener.ListenerAction;
import dataset.Field;
import dataset.Query;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eElement;
import domain.eFurniture;
import domain.eJoining;
import domain.eJoinvar;
import domain.eSysfurn;
import domain.eSysprof;
import domain.eSystree;
import dataset.Record;
import enums.Layout;
import enums.LayoutHand;
import enums.PKjson;
import enums.TypeJoin;
import enums.TypeOpen1;
import enums.UseSide;
import frames.UGui;
import frames.dialog.DicArtikl;
import frames.dialog.DicColor;
import frames.dialog.DicEnums;
import frames.dialog.DicHandl;
import frames.dialog.DicName;
import frames.dialog.DicSysprof;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import org.locationtech.jts.geom.Envelope;
import startup.App;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import common.listener.ListenerGet;
import common.listener.ListenerRecord;
import common.listener.ListenerSet;
import javax.swing.JPanel;

public class CardPanel extends javax.swing.JPanel {

    private JPanel panCont;
    private ListenerGet<Wincalc> listenerWincalc;
    private ListenerAction listenerChangeAndRedraw;
    private ImageIcon icon = new ImageIcon(getClass().getResource("/resource/img16/b031.gif"));
    private Query qGroups, qSysprof;
    private Record record;

    javax.swing.JFrame thiz = null;
    javax.swing.JTree winTree = new javax.swing.JTree();
    javax.swing.JPopupMenu ppmTree = new javax.swing.JPopupMenu();
    private DefMutableTreeNode winNode = null;

    public CardPanel(ListenerGet listenerWincalc, ListenerAction listenerChangeAndRedraw,
            JTree winTree, JPopupMenu ppmTree, Query qGroups, Query qSysprof, JPanel panCont) {
        initComponents();
        this.panCont = panCont;
        initElements();
        this.listenerWincalc = listenerWincalc;
        this.listenerChangeAndRedraw = listenerChangeAndRedraw;
        this.winTree = winTree;
        this.ppmTree = ppmTree;
        this.qGroups = qGroups;
        this.qSysprof = qSysprof;
    }

    //При выборе элемента конструкции
    public void selectionTree() {
        try {
            //Пересчёт фурнитуры с учётом настроек 
            //конструктива ручки, петли, замка см. форму "Фурнитура"                    
            new TFurniture(wincalc(), true).furn();

            //Выделенный элемент
            Object selectNode = winTree.getLastSelectedPathComponent();
            if (selectNode instanceof DefMutableTreeNode) {
                winNode = (DefMutableTreeNode) selectNode;
                Com5t com5t = winNode.com5t();
                Wincalc winc = wincalc();

                UGui.changePpmTree(winTree, ppmTree, com5t);

                //Таймер цвета
                if (enums.Type.contains(com5t, enums.Type.PARAM, enums.Type.FRAME, enums.Type.JOINING) == false) {
                    if (winc.canvas != null) {
                        com5t.timer.start();
                        winc.canvas.repaint();
                    }
                }
                //Коробка
                if (List.of(enums.Type.RECTANGL, enums.Type.TRAPEZE,
                        enums.Type.ARCH, enums.Type.DOOR).contains(com5t.type)) {
                    ((CardLayout) panCont.getLayout()).show(panCont, "card18");
                    ((TitledBorder) pan12.getBorder()).setTitle(winc.root.type.name);
                    setText(txt9, eColor.find(winc.root.colorID1).getStr(eColor.name));
                    setIcon(btn9, UPar.isFinite(winc.root.gson.param, PKjson.colorID1));
                    setText(txt13, eColor.find(winc.root.colorID2).getStr(eColor.name));
                    setIcon(btn13, UPar.isFinite(winc.root.gson.param, PKjson.colorID2));
                    setText(txt14, eColor.find(winc.root.colorID3).getStr(eColor.name));
                    setIcon(btn2, UPar.isFinite(winc.root.gson.param, PKjson.colorID3));
                    setText(txt17, UCom.format(winc.width(), 1));
                    setText(txt22, UCom.format(winc.height(), 1));
                    setText(txt53, winc.root.artiklRec.getStr(eArtikl.code));
                    setIcon(btn36, UPar.isFinite(winc.root.gson.param, PKjson.sysprofID));
                    setText(txt66, winc.root.artiklRec.get(eArtikl.name));
                    setText(txt67, eArtikl.find2(com5t.artiklRec.getInt(eArtikl.analog_id)).getStr(eArtikl.code));

                    //Рама, импост...
                } else if (List.of(enums.Type.BOX_SIDE, enums.Type.STV_SIDE, enums.Type.IMPOST,
                        enums.Type.STOIKA, enums.Type.SHTULP).contains(com5t.type)) {
                    ((CardLayout) panCont.getLayout()).show(panCont, "card13");
                    ((TitledBorder) pan13.getBorder()).setTitle(winNode.toString());
                    setText(txt32, com5t.artiklRec.getStr(eArtikl.code));
                    setIcon(btn22, UPar.isFinite(com5t.gson.param, PKjson.sysprofID));
                    setText(txt33, com5t.artiklRec.getStr(eArtikl.name));
                    setText(txt52, eArtikl.find2(com5t.artiklRec.getInt(eArtikl.analog_id)).getStr(eArtikl.code));

                    setText(txt27, eColor.find(com5t.colorID1).getStr(eColor.name));
                    setIcon(btn18, UPar.isFinite(com5t.gson.param, PKjson.colorID1));
                    setText(txt28, eColor.find(com5t.colorID2).getStr(eColor.name));
                    setIcon(btn19, UPar.isFinite(com5t.gson.param, PKjson.colorID2));
                    setText(txt29, eColor.find(com5t.colorID3).getStr(eColor.name));
                    setIcon(btn20, UPar.isFinite(com5t.gson.param, PKjson.colorID3));

                    //Стеклопакет
                } else if (com5t.type == enums.Type.GLASS) {
                    ElemGlass elem = (ElemGlass) com5t;
                    ((CardLayout) panCont.getLayout()).show(panCont, "card15");
                    dataset.Record artiklRec = com5t.artiklRec;
                    setText(txt19, artiklRec.getStr(eArtikl.code));
                    setIcon(btn3, UPar.isFinite(com5t.gson.param, PKjson.artglasID));
                    setText(txt18, artiklRec.getStr(eArtikl.name));
                    dataset.Record colorRec = eColor.find(com5t.colorID1);
                    setText(txt34, colorRec.getStr(eColor.name));
                    setIcon(btn25, UPar.isFinite(com5t.gson.param, PKjson.colorGlass));
                    dataset.Record rasclRec = ((ElemGlass) com5t).rascRec;
                    setText(txt49, rasclRec.getStr(eArtikl.code));
                    setText(txt50, rasclRec.getStr(eArtikl.name));
                    dataset.Record colorRascl = eColor.find(((ElemGlass) com5t).rascColor);
                    setText(txt51, colorRascl.getStr(eColor.name));
                    spinHor.setValue(((ElemGlass) com5t).rascNumber[0]);
                    spinVert.setValue(((ElemGlass) com5t).rascNumber[1]);
                    //Помощь менеджеру для коррекция импоста
//                    if (elem.deltaDY != null) {  
//                        ElemSimple el = winc.listElem.stream().filter(e -> e.type == enums.Type.IMPOST).findFirst().orElse(null);
//                        double s1 = el.artiklRec.getDbl(eArtikl.height), 
//                                s2 = el.artiklRec.getDbl(eArtikl.size_centr), 
//                                s3 = el.artiklRec.getDbl(eArtikl.size_falz);                       
//                        //имп.шир - имп.серед - имп.фальц + смещю.стор.точк.перес.арк.и.коробки
//                        lab4.setText("DY: " + s1 + " - " + s2 + " - " + s3 + " + "
//                                + UCom.format(elem.deltaDY, 2) + " = " 
//                                + UCom.format(s1 - s2 - s3 + elem.deltaDY, 2) + " мм.");
//                        System.out.println("frames.Systree.selectionTree()");
//                    }

                    //Створка
                } else if (com5t.type == enums.Type.STVORKA) {
                    //через сокр. тарификацию
                    ((CardLayout) panCont.getLayout()).show(panCont, "card16");
                    AreaStvorka stv = (AreaStvorka) com5t;
                    Envelope env = stv.area.getGeometryN(0).getEnvelopeInternal();
                    int sysfurnID = stv.sysfurnRec.getInt(eSysfurn.furniture_id);
                    setText(txt68, stv.artiklRec.get(eArtikl.code));
                    setIcon(btn37, UPar.isFinite(stv.gson.param, PKjson.sysprofID));
                    setText(txt69, stv.artiklRec.get(eArtikl.name));
                    setText(txt70, eArtikl.find2(com5t.artiklRec.getInt(eArtikl.analog_id)).getStr(eArtikl.code));

                    setText(txt10, eColor.find(stv.colorID1).getStr(eColor.name));
                    setIcon(btn38, UPar.isFinite(stv.gson.param, PKjson.colorID1));
                    setText(txt15, eColor.find(stv.colorID2).getStr(eColor.name));
                    setIcon(btn39, UPar.isFinite(stv.gson.param, PKjson.colorID2));
                    setText(txt23, eColor.find(stv.colorID3).getStr(eColor.name));
                    setIcon(btn8, UPar.isFinite(stv.gson.param, PKjson.colorID3));

                    setText(txt24, UCom.format(env.getWidth(), 1));
                    setText(txt26, UCom.format(env.getHeight(), 1));
                    setText(txt20, eFurniture.find(sysfurnID).getStr(eFurniture.name));
                    setIcon(btn10, UPar.isFinite(stv.gson.param, PKjson.sysfurnID));
                    setText(txt30, stv.typeOpen.name2);
                    setIcon(btn12, UPar.isFinite(stv.gson.param, PKjson.artiklHand));
                    setText(txt16, stv.handLayout.name);
                    setText(txt31, (stv.handLayout == LayoutHand.VAR) ? UCom.format(stv.handHeight, 1) : "");
                    setText(txt21, stv.handRec[0].getStr(eArtikl.code));
                    setText(txt59, stv.handRec[0].getStr(eArtikl.name));
                    setIcon(btn21, UPar.isFinite(stv.gson.param, PKjson.typeOpen));
                    setText(txt25, eColor.find(stv.handColor[0]).getStr(eColor.name));
                    setIcon(btn14, UPar.isFinite(stv.gson.param, PKjson.colorHand));
                    setText(txt45, stv.loopRec[0].getStr(eArtikl.code));
                    setText(txt57, stv.loopRec[0].getStr(eArtikl.name));
                    setIcon(btn15, UPar.isFinite(stv.gson.param, PKjson.artiklLoop));
                    setText(txt47, eColor.find(stv.loopColor[0]).getStr(eColor.name));
                    setIcon(btn17, UPar.isFinite(stv.gson.param, PKjson.colorLoop));
                    setText(txt46, stv.lockRec[0].getStr(eArtikl.code));
                    setText(txt58, stv.lockRec[0].getStr(eArtikl.name));
                    setIcon(btn23, UPar.isFinite(stv.gson.param, PKjson.artiklLock));
                    setText(txt48, eColor.find(stv.lockColor[0]).getStr(eColor.name));
                    setIcon(btn24, UPar.isFinite(stv.gson.param, PKjson.colorLock));
                    //Москитка
                    Com5t mosq = stv.childs.stream().filter(e -> e.type == enums.Type.MOSQUIT).findFirst().orElse(null);
                    List.of(txt54, txt55, txt60, txt56).forEach(e -> e.setText(null));
                    List.of(btn16, btn32).forEach(btn -> setIcon(btn, false));
                    if (mosq != null) {

                        setText(txt54, mosq.artiklRec.getStr(eArtikl.code));
                        setIcon(btn16, UPar.isFinite(mosq.gson.param, PKjson.artiklID));
                        setText(txt55, mosq.artiklRec.getStr(eArtikl.name));
                        setText(txt60, eColor.find(mosq.colorID1).getStr(eColor.name));
                        setIcon(btn32, UPar.isFinite(mosq.gson.param, PKjson.colorID1));
                        setText(txt56, mosq.sysprofRec.getStr(eElement.name));
                    }

                    //Соединения
                } else if (com5t.type == enums.Type.JOINING) {
                    ((CardLayout) panCont.getLayout()).show(panCont, "card17");
                    DefMutableTreeNode nodeParent = (DefMutableTreeNode) winNode.getParent();
                    ElemSimple elem5e = (ElemSimple) nodeParent.com5t();
                    new TJoining(winc, true).join();//заполним соединения из конструктива                                        
                    ElemJoining ej1 = UCom.join(winc.listJoin, elem5e, 0);
                    ElemJoining ej2 = UCom.join(winc.listJoin, elem5e, 1);
                    ElemJoining ej3 = UCom.join(winc.listJoin, elem5e, 2);
                    List.of(lab55, lab56, lab57).forEach(it -> it.setIcon(null));
                    List.of(txt36, txt37, txt38, txt39, txt40, txt41, txt42, txt43, txt44).forEach(it -> it.setText(""));
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
                    if (ej3 != null && ej3.type() == TypeJoin.FLAT) {
                        setText(txt40, ej3.joiningRec.getStr(eJoining.name));
                        setText(txt44, ej3.name());
                        setText(txt41, ej3.joinvarRec.getStr(eJoinvar.name));
                        lab57.setIcon(UColor.iconFromTypeJoin2(ej3.type().id));
                    }
                } else {
                    ((CardLayout) panCont.getLayout()).show(panCont, "card12");
                }
                List.of(pan12, pan13, pan15, pan16).forEach(it -> it.repaint());
            }
        } catch (Exception e) {
            System.err.println("Ошибка:CardPanel.selectionTree " + e);
        }
    }

    //Получить текущий Wincalc
    private Wincalc wincalc() {
        return listenerWincalc.get();
    }

    public void setText(JTextField comp, Object txt) {
        if (txt == null) {
            comp.setText("");
        }
        comp.setText(txt.toString());
        comp.setCaretPosition(0);
    }

    public void setIcon(JButton btn, boolean b) {
        if (b == true) {
            btn.setText("");
            btn.setIcon(icon);
        } else {
            btn.setText("...");
            btn.setIcon(null);
        }
    }

    //Изменить скрипт в базе и перерисовать
    public void changeAndRedraw() {
        listenerChangeAndRedraw.action();
    }

    private void dicArtiklToFurniture(String PKjsonColor, int level2) {
        try {
            double stvorkaID = winNode.com5t().id;
            int furnitureID = ((AreaStvorka) winNode.com5t()).sysfurnRec.getInt(eSysfurn.furniture_id);
            Query qArtikl = new Query(eArtikl.values()).sql(eArtikl.data(), eArtikl.level1, 2, eArtikl.level2, level2);
            Query qResult = UGui.artTypeToFurndetList(furnitureID, qArtikl);
            new DicArtikl(thiz, (artiklRec) -> {

                GsonElem stvArea = UCom.gson(wincalc().listAll, stvorkaID);
                if (artiklRec.get(1) == null) {
                    UPar.remove(stvArea.param, List.of(PKjsonColor));
                } else {
                    UPar.addProperty(stvArea.param, List.of(PKjsonColor), artiklRec.getInt(eArtikl.id));
                }
                changeAndRedraw();

            }, qResult);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.artiklToFurniture() " + e);
        }
    }

    private void dicColorToProfile(java.awt.event.ActionEvent evt, JButton btn1, JButton btn2) {
        try {
            List<String> keys = new ArrayList();
            Com5t comElem = winNode.com5t();
            dataset.Record systreeRec = eSystree.find(comElem.winc.nuni);
            Field colorFilterMark = (evt.getSource() == btn1) ? eArtdet.mark_c1 : (evt.getSource() == btn2) ? eArtdet.mark_c2 : eArtdet.mark_c3;
            String colorFilterTxt = (evt.getSource() == btn1) ? systreeRec.getStr(eSystree.col1) : (evt.getSource() == btn2)
                    ? systreeRec.getStr(eSystree.col2) : systreeRec.getStr(eSystree.col3);
            Query artdetList = new Query(eArtdet.values()).sql(eArtdet.data(), eArtdet.artikl_id, comElem.artiklRec.getInt(eArtikl.id));

            HashSet<dataset.Record> colorFilterSet = DicColor.filterTxt(eColor.data(), colorFilterTxt);
            HashSet<dataset.Record> colorSet = DicColor.filterDet(colorFilterSet, artdetList, colorFilterMark);
            if (comElem.type == enums.Type.STV_SIDE) {
                if (comElem.layout() == Layout.BOT) {
                    keys.add(PKjson.stvorkaBot);
                } else if (comElem.layout() == Layout.RIG) {
                    keys.add(PKjson.stvorkaRig);
                } else if (comElem.layout() == Layout.TOP) {
                    keys.add(PKjson.stvorkaTop);
                } else if (comElem.layout() == Layout.LEF) {
                    keys.add(PKjson.stvorkaLef);
                }
            }
            if (evt.getSource() == btn1) {
                keys.add(PKjson.colorID1);
            } else if (evt.getSource() == btn2) {
                keys.add(PKjson.colorID2);
            } else {
                keys.add(PKjson.colorID3);
            }

            new DicColor(thiz, (colorRec) -> {
                final Com5t com5t = (comElem.type == enums.Type.STV_SIDE) ? comElem.owner : comElem;

                if (colorRec.get(1) == null) {
                    UPar.remove(com5t.gson.param, keys);
                } else {
                    UPar.addProperty(com5t.gson.param, keys, colorRec.getInt(eColor.id));
                }
                changeAndRedraw(); //обновим конструкцию
            }, colorSet, true, false);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorToProfile() " + e);
        }
    }

    private void dicColorToElement(String PKjsonColor, dataset.Record artiklElem) {
        try {
            double elemID = winNode.com5t().id;
            HashSet<dataset.Record> colorSet = UGui.artiklToColorSet(artiklElem.getInt(eArtikl.id));
            new DicColor(thiz, (colorRec) -> {

                GsonElem gsonElem = UCom.gson(wincalc().listAll, elemID);
                if (colorRec.get(1) == null) {
                    UPar.remove(gsonElem.param, List.of(PKjsonColor));
                } else {
                    UPar.addProperty(gsonElem.param, List.of(PKjsonColor), colorRec.getInt(eColor.id));
                }
                changeAndRedraw();

            }, colorSet, true, false);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorToElement() " + e);
        }
    }

    private void dicColorToElement(GsonElem gsonElem, String PKjsonColor, dataset.Record artiklElem) {
        try {
            if (gsonElem != null) {
                HashSet<dataset.Record> colorSet = UGui.artiklToColorSet(artiklElem.getInt(eArtikl.id));
                new DicColor(thiz, (colorRec) -> {

                    if (colorRec.get(1) == null) {
                        UPar.remove(gsonElem.param, List.of(PKjsonColor));
                    } else {
                        UPar.addProperty(gsonElem.param, List.of(PKjsonColor), colorRec.getInt(eColor.id));
                    }
                    changeAndRedraw();

                }, colorSet, true, false);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorToElement() " + e);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panContainer = new javax.swing.JPanel();
        pan12 = new javax.swing.JPanel();
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
        lab76 = new javax.swing.JLabel();
        txt52 = new javax.swing.JTextField();
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
        lab4 = new javax.swing.JLabel();
        lab40 = new javax.swing.JLabel();
        txt61 = new javax.swing.JTextField();
        btn30 = new javax.swing.JButton();
        lab72 = new javax.swing.JLabel();
        txt62 = new javax.swing.JTextField();
        lab73 = new javax.swing.JLabel();
        txt63 = new javax.swing.JTextField();
        btn33 = new javax.swing.JButton();
        lab74 = new javax.swing.JLabel();
        txt64 = new javax.swing.JTextField();
        btn34 = new javax.swing.JButton();
        lab75 = new javax.swing.JLabel();
        txt65 = new javax.swing.JTextField();
        btn35 = new javax.swing.JButton();
        pan16 = new javax.swing.JPanel();
        tabb2 = new javax.swing.JTabbedPane();
        pan20 = new javax.swing.JPanel();
        lab41 = new javax.swing.JLabel();
        txt24 = new javax.swing.JTextField();
        lab42 = new javax.swing.JLabel();
        txt26 = new javax.swing.JTextField();
        lab80 = new javax.swing.JLabel();
        txt68 = new javax.swing.JTextField();
        btn37 = new javax.swing.JButton();
        txt69 = new javax.swing.JTextField();
        lab81 = new javax.swing.JLabel();
        lab82 = new javax.swing.JLabel();
        txt70 = new javax.swing.JTextField();
        pan24 = new javax.swing.JPanel();
        lab83 = new javax.swing.JLabel();
        lab84 = new javax.swing.JLabel();
        lab85 = new javax.swing.JLabel();
        btn38 = new javax.swing.JButton();
        btn39 = new javax.swing.JButton();
        btn8 = new javax.swing.JButton();
        txt10 = new javax.swing.JTextField();
        txt15 = new javax.swing.JTextField();
        txt23 = new javax.swing.JTextField();
        pan19 = new javax.swing.JPanel();
        lab30 = new javax.swing.JLabel();
        txt20 = new javax.swing.JTextField();
        btn10 = new javax.swing.JButton();
        btn21 = new javax.swing.JButton();
        txt30 = new javax.swing.JTextField();
        lab45 = new javax.swing.JLabel();
        lab25 = new javax.swing.JLabel();
        txt21 = new javax.swing.JTextField();
        btn12 = new javax.swing.JButton();
        lab70 = new javax.swing.JLabel();
        txt59 = new javax.swing.JTextField();
        lab39 = new javax.swing.JLabel();
        txt25 = new javax.swing.JTextField();
        btn14 = new javax.swing.JButton();
        lab46 = new javax.swing.JLabel();
        txt16 = new javax.swing.JTextField();
        txt31 = new javax.swing.JTextField();
        btn6 = new javax.swing.JButton();
        lab26 = new javax.swing.JLabel();
        txt45 = new javax.swing.JTextField();
        btn15 = new javax.swing.JButton();
        lab48 = new javax.swing.JLabel();
        txt57 = new javax.swing.JTextField();
        lab44 = new javax.swing.JLabel();
        txt47 = new javax.swing.JTextField();
        btn17 = new javax.swing.JButton();
        pan23 = new javax.swing.JPanel();
        lab71 = new javax.swing.JLabel();
        btn24 = new javax.swing.JButton();
        txt46 = new javax.swing.JTextField();
        btn23 = new javax.swing.JButton();
        lab63 = new javax.swing.JLabel();
        txt48 = new javax.swing.JTextField();
        txt58 = new javax.swing.JTextField();
        lab3 = new javax.swing.JLabel();
        lab28 = new javax.swing.JLabel();
        txt54 = new javax.swing.JTextField();
        btn16 = new javax.swing.JButton();
        lab37 = new javax.swing.JLabel();
        txt55 = new javax.swing.JTextField();
        lab66 = new javax.swing.JLabel();
        txt60 = new javax.swing.JTextField();
        btn32 = new javax.swing.JButton();
        lab43 = new javax.swing.JLabel();
        txt56 = new javax.swing.JTextField();
        btn31 = new javax.swing.JButton();
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
        lab35 = new javax.swing.JLabel();
        txt17 = new javax.swing.JTextField();
        lab38 = new javax.swing.JLabel();
        txt22 = new javax.swing.JTextField();
        lab77 = new javax.swing.JLabel();
        txt53 = new javax.swing.JTextField();
        btn36 = new javax.swing.JButton();
        lab78 = new javax.swing.JLabel();
        txt66 = new javax.swing.JTextField();
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
        lab79 = new javax.swing.JLabel();
        txt67 = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(450, 524));
        setLayout(new java.awt.CardLayout());

        panContainer.setPreferredSize(new java.awt.Dimension(450, 524));
        panContainer.setLayout(new java.awt.CardLayout());

        pan12.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Основные", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 1)));
        pan12.setToolTipText("");

        javax.swing.GroupLayout pan12Layout = new javax.swing.GroupLayout(pan12);
        pan12.setLayout(pan12Layout);
        pan12Layout.setHorizontalGroup(
            pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 452, Short.MAX_VALUE)
        );
        pan12Layout.setVerticalGroup(
            pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );

        panContainer.add(pan12, "card12");

        pan13.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Рама, импост...", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 1)));

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
                btn22sysprofToFrame(evt);
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
                btn18colorToFrame(evt);
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
                btn19colorToFrame(evt);
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
                btn20colorToFrame(evt);
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
                    .addComponent(txt29, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
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
                    .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan22Layout.createSequentialGroup()
                        .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lab51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lab52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lab53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lab76.setFont(frames.UGui.getFont(0,0));
        lab76.setText("Аналог");
        lab76.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab76.setPreferredSize(new java.awt.Dimension(80, 18));

        txt52.setEditable(false);
        txt52.setFont(frames.UGui.getFont(0,0));
        txt52.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt52.setPreferredSize(new java.awt.Dimension(180, 18));

        javax.swing.GroupLayout pan13Layout = new javax.swing.GroupLayout(pan13);
        pan13.setLayout(pan13Layout);
        pan13Layout.setHorizontalGroup(
            pan13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pan22, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
            .addGroup(pan13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan13Layout.createSequentialGroup()
                        .addComponent(lab33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt32, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan13Layout.createSequentialGroup()
                        .addComponent(lab34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt33, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan13Layout.createSequentialGroup()
                        .addComponent(lab76, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt52, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)))
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
                .addGroup(pan13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab76, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pan22, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(209, Short.MAX_VALUE))
        );

        panContainer.add(pan13, "card13");

        pan15.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Стеклопакет", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 1)));

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
                btn3artiklToGlass(evt);
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
                btn25colorToGlass(evt);
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
                btn5artiklToRascladka(evt);
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
                btn29colorToRascladka(evt);
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

        lab4.setMaximumSize(new java.awt.Dimension(260, 18));
        lab4.setPreferredSize(new java.awt.Dimension(260, 18));

        lab40.setFont(frames.UGui.getFont(0,0));
        lab40.setText("Жалюзи");
        lab40.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab40.setMaximumSize(new java.awt.Dimension(80, 18));
        lab40.setMinimumSize(new java.awt.Dimension(80, 18));
        lab40.setPreferredSize(new java.awt.Dimension(80, 18));

        txt61.setEditable(false);
        txt61.setFont(frames.UGui.getFont(0,0));
        txt61.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt61.setPreferredSize(new java.awt.Dimension(180, 18));

        btn30.setText("...");
        btn30.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn30.setEnabled(false);
        btn30.setMaximumSize(new java.awt.Dimension(21, 20));
        btn30.setMinimumSize(new java.awt.Dimension(21, 20));
        btn30.setName("btnField17"); // NOI18N
        btn30.setPreferredSize(new java.awt.Dimension(21, 20));
        btn30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn30blindsToStvorka(evt);
            }
        });

        lab72.setFont(frames.UGui.getFont(0,0));
        lab72.setText("Название");
        lab72.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab72.setMaximumSize(new java.awt.Dimension(80, 18));
        lab72.setMinimumSize(new java.awt.Dimension(80, 18));
        lab72.setPreferredSize(new java.awt.Dimension(80, 18));

        txt62.setEditable(false);
        txt62.setFont(frames.UGui.getFont(0,0));
        txt62.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt62.setPreferredSize(new java.awt.Dimension(212, 18));

        lab73.setFont(frames.UGui.getFont(0,0));
        lab73.setText("Текстура");
        lab73.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab73.setMaximumSize(new java.awt.Dimension(80, 18));
        lab73.setMinimumSize(new java.awt.Dimension(80, 18));
        lab73.setPreferredSize(new java.awt.Dimension(80, 18));

        txt63.setEditable(false);
        txt63.setFont(frames.UGui.getFont(0,0));
        txt63.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt63.setPreferredSize(new java.awt.Dimension(180, 18));

        btn33.setText("...");
        btn33.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn33.setEnabled(false);
        btn33.setMaximumSize(new java.awt.Dimension(21, 20));
        btn33.setMinimumSize(new java.awt.Dimension(21, 20));
        btn33.setName("btnField17"); // NOI18N
        btn33.setPreferredSize(new java.awt.Dimension(21, 20));
        btn33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn33colorToBlinds(evt);
            }
        });

        lab74.setFont(frames.UGui.getFont(0,0));
        lab74.setText("Состав");
        lab74.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab74.setMaximumSize(new java.awt.Dimension(80, 18));
        lab74.setMinimumSize(new java.awt.Dimension(80, 18));
        lab74.setPreferredSize(new java.awt.Dimension(80, 18));

        txt64.setEditable(false);
        txt64.setFont(frames.UGui.getFont(0,0));
        txt64.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt64.setPreferredSize(new java.awt.Dimension(180, 18));

        btn34.setText("...");
        btn34.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn34.setEnabled(false);
        btn34.setMaximumSize(new java.awt.Dimension(21, 20));
        btn34.setMinimumSize(new java.awt.Dimension(21, 20));
        btn34.setName("btnField17"); // NOI18N
        btn34.setPreferredSize(new java.awt.Dimension(21, 20));
        btn34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn34elemsToBlinds(evt);
            }
        });

        lab75.setFont(frames.UGui.getFont(0,0));
        lab75.setText("Расположение");
        lab75.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab75.setMaximumSize(new java.awt.Dimension(80, 18));
        lab75.setMinimumSize(new java.awt.Dimension(80, 18));
        lab75.setPreferredSize(new java.awt.Dimension(80, 18));

        txt65.setEditable(false);
        txt65.setFont(frames.UGui.getFont(0,0));
        txt65.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt65.setPreferredSize(new java.awt.Dimension(180, 18));

        btn35.setText("...");
        btn35.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn35.setEnabled(false);
        btn35.setMaximumSize(new java.awt.Dimension(21, 20));
        btn35.setMinimumSize(new java.awt.Dimension(21, 20));
        btn35.setName("btnField17"); // NOI18N
        btn35.setPreferredSize(new java.awt.Dimension(21, 20));
        btn35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn35artiklToBlinds(evt);
            }
        });

        javax.swing.GroupLayout pan15Layout = new javax.swing.GroupLayout(pan15);
        pan15.setLayout(pan15Layout);
        pan15Layout.setHorizontalGroup(
            pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lab4, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pan15Layout.createSequentialGroup()
                        .addComponent(lab29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt19, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pan15Layout.createSequentialGroup()
                        .addComponent(lab36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt18, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pan15Layout.createSequentialGroup()
                        .addComponent(lab64, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt50, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                    .addGroup(pan15Layout.createSequentialGroup()
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
                    .addGroup(pan15Layout.createSequentialGroup()
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
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pan15Layout.createSequentialGroup()
                        .addComponent(lab74, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt64, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan15Layout.createSequentialGroup()
                        .addComponent(lab40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt61, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                        .addGap(27, 27, 27))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pan15Layout.createSequentialGroup()
                        .addComponent(lab72, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt62, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
                    .addGroup(pan15Layout.createSequentialGroup()
                        .addComponent(lab73, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt63, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pan15Layout.createSequentialGroup()
                        .addComponent(lab67, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinHor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pan15Layout.createSequentialGroup()
                        .addComponent(lab75, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan15Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btn35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan15Layout.createSequentialGroup()
                                .addComponent(txt65, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan15Layout.createSequentialGroup()
                        .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lab40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lab72, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt62, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lab73, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt63, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txt64, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btn34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lab74, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lab75, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt65, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btn35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lab4, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 45, Short.MAX_VALUE))
        );

        panContainer.add(pan15, "card15");

        pan16.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Створка", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 1)));
        pan16.setName(""); // NOI18N
        pan16.setLayout(new java.awt.BorderLayout());

        tabb2.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        tabb2.setFont(frames.UGui.getFont(-1,0));

        pan20.setPreferredSize(new java.awt.Dimension(308, 98));

        lab41.setFont(frames.UGui.getFont(0,0));
        lab41.setText("Ширина");
        lab41.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab41.setPreferredSize(new java.awt.Dimension(80, 18));

        txt24.setEditable(false);
        txt24.setFont(frames.UGui.getFont(0,0));
        txt24.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt24.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt24.setMinimumSize(new java.awt.Dimension(40, 18));
        txt24.setPreferredSize(new java.awt.Dimension(48, 18));

        lab42.setFont(frames.UGui.getFont(0,0));
        lab42.setText("Высота");
        lab42.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab42.setPreferredSize(new java.awt.Dimension(80, 18));

        txt26.setEditable(false);
        txt26.setFont(frames.UGui.getFont(0,0));
        txt26.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt26.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt26.setEnabled(false);
        txt26.setMinimumSize(new java.awt.Dimension(40, 18));
        txt26.setPreferredSize(new java.awt.Dimension(48, 18));

        lab80.setFont(frames.UGui.getFont(0,0));
        lab80.setText("Артикул");
        lab80.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab80.setPreferredSize(new java.awt.Dimension(80, 18));

        txt68.setEditable(false);
        txt68.setFont(frames.UGui.getFont(0,0));
        txt68.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt68.setPreferredSize(new java.awt.Dimension(180, 18));

        btn37.setText("...");
        btn37.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn37.setMaximumSize(new java.awt.Dimension(21, 20));
        btn37.setMinimumSize(new java.awt.Dimension(21, 20));
        btn37.setName("btnField17"); // NOI18N
        btn37.setPreferredSize(new java.awt.Dimension(21, 20));
        btn37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn37sysprofToStvorka(evt);
            }
        });

        txt69.setEditable(false);
        txt69.setFont(frames.UGui.getFont(0,0));
        txt69.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt69.setPreferredSize(new java.awt.Dimension(180, 18));

        lab81.setFont(frames.UGui.getFont(0,0));
        lab81.setText("Название");
        lab81.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab81.setPreferredSize(new java.awt.Dimension(80, 18));

        lab82.setFont(frames.UGui.getFont(0,0));
        lab82.setText("Аналог");
        lab82.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab82.setPreferredSize(new java.awt.Dimension(80, 18));

        txt70.setEditable(false);
        txt70.setFont(frames.UGui.getFont(0,0));
        txt70.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt70.setPreferredSize(new java.awt.Dimension(180, 18));

        pan24.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Текстура створки", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 0)));
        pan24.setPreferredSize(new java.awt.Dimension(320, 104));

        lab83.setFont(frames.UGui.getFont(0,0));
        lab83.setText("Основная");
        lab83.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab83.setPreferredSize(new java.awt.Dimension(80, 18));

        lab84.setFont(frames.UGui.getFont(0,0));
        lab84.setText("Внутренняя");
        lab84.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab84.setPreferredSize(new java.awt.Dimension(80, 18));

        lab85.setFont(frames.UGui.getFont(0,0));
        lab85.setText("Внешняя");
        lab85.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab85.setPreferredSize(new java.awt.Dimension(80, 18));

        btn38.setText("...");
        btn38.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn38.setMaximumSize(new java.awt.Dimension(21, 20));
        btn38.setMinimumSize(new java.awt.Dimension(21, 20));
        btn38.setName("btn9"); // NOI18N
        btn38.setPreferredSize(new java.awt.Dimension(21, 20));
        btn38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn38colorToStvorka(evt);
            }
        });

        btn39.setText("...");
        btn39.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn39.setMaximumSize(new java.awt.Dimension(21, 20));
        btn39.setMinimumSize(new java.awt.Dimension(21, 20));
        btn39.setName("btn13"); // NOI18N
        btn39.setPreferredSize(new java.awt.Dimension(21, 20));
        btn39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn39colorToStvorka(evt);
            }
        });

        btn8.setText("...");
        btn8.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn8.setMaximumSize(new java.awt.Dimension(21, 20));
        btn8.setMinimumSize(new java.awt.Dimension(21, 20));
        btn8.setName("btn2"); // NOI18N
        btn8.setPreferredSize(new java.awt.Dimension(21, 20));
        btn8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn8colorToStvorka(evt);
            }
        });

        txt10.setEditable(false);
        txt10.setFont(frames.UGui.getFont(0,0));
        txt10.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt10.setPreferredSize(new java.awt.Dimension(180, 18));

        txt15.setEditable(false);
        txt15.setFont(frames.UGui.getFont(0,0));
        txt15.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt15.setPreferredSize(new java.awt.Dimension(180, 18));

        txt23.setEditable(false);
        txt23.setFont(frames.UGui.getFont(0,0));
        txt23.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt23.setPreferredSize(new java.awt.Dimension(180, 18));

        javax.swing.GroupLayout pan24Layout = new javax.swing.GroupLayout(pan24);
        pan24.setLayout(pan24Layout);
        pan24Layout.setHorizontalGroup(
            pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan24Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan24Layout.createSequentialGroup()
                        .addComponent(lab85, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt23, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE))
                    .addGroup(pan24Layout.createSequentialGroup()
                        .addComponent(lab83, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt10, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE))
                    .addGroup(pan24Layout.createSequentialGroup()
                        .addComponent(lab84, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt15, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );
        pan24Layout.setVerticalGroup(
            pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan24Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab83, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab84, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab85, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout pan20Layout = new javax.swing.GroupLayout(pan20);
        pan20.setLayout(pan20Layout);
        pan20Layout.setHorizontalGroup(
            pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan20Layout.createSequentialGroup()
                        .addComponent(lab80, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt68, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan20Layout.createSequentialGroup()
                        .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lab81, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lab82, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt69, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt70, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(pan20Layout.createSequentialGroup()
                        .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pan20Layout.createSequentialGroup()
                                .addComponent(lab41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pan20Layout.createSequentialGroup()
                                .addComponent(lab42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(pan24, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
        );
        pan20Layout.setVerticalGroup(
            pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab80, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt68, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab81, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt69, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt70, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab82, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pan24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(131, Short.MAX_VALUE))
        );

        tabb2.addTab("Створка", pan20);

        lab30.setFont(frames.UGui.getFont(0,0));
        lab30.setText("Фурнитура");
        lab30.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab30.setPreferredSize(new java.awt.Dimension(80, 18));

        txt20.setEditable(false);
        txt20.setFont(frames.UGui.getFont(0,0));
        txt20.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt20.setPreferredSize(new java.awt.Dimension(180, 18));

        btn10.setText("...");
        btn10.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn10.setMaximumSize(new java.awt.Dimension(21, 20));
        btn10.setMinimumSize(new java.awt.Dimension(21, 20));
        btn10.setName("btnField17"); // NOI18N
        btn10.setPreferredSize(new java.awt.Dimension(21, 20));
        btn10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn10sysfurnToStvorka(evt);
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
                btn21typeOpenToStvorka(evt);
            }
        });

        txt30.setEditable(false);
        txt30.setFont(frames.UGui.getFont(0,0));
        txt30.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt30.setPreferredSize(new java.awt.Dimension(180, 18));

        lab45.setFont(frames.UGui.getFont(0,0));
        lab45.setText("Напр. откр.");
        lab45.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab45.setPreferredSize(new java.awt.Dimension(80, 18));

        lab25.setFont(frames.UGui.getFont(0,0));
        lab25.setText("Ручка");
        lab25.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab25.setMaximumSize(new java.awt.Dimension(80, 18));
        lab25.setMinimumSize(new java.awt.Dimension(80, 18));
        lab25.setPreferredSize(new java.awt.Dimension(80, 18));

        txt21.setEditable(false);
        txt21.setFont(frames.UGui.getFont(0,0));
        txt21.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt21.setPreferredSize(new java.awt.Dimension(180, 18));

        btn12.setText("...");
        btn12.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn12.setMaximumSize(new java.awt.Dimension(21, 20));
        btn12.setMinimumSize(new java.awt.Dimension(21, 20));
        btn12.setName("btnField17"); // NOI18N
        btn12.setPreferredSize(new java.awt.Dimension(21, 20));
        btn12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn12artiklToHand(evt);
            }
        });

        lab70.setFont(frames.UGui.getFont(0,0));
        lab70.setText("Название");
        lab70.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab70.setMaximumSize(new java.awt.Dimension(80, 18));
        lab70.setMinimumSize(new java.awt.Dimension(80, 18));
        lab70.setPreferredSize(new java.awt.Dimension(80, 18));

        txt59.setEditable(false);
        txt59.setFont(frames.UGui.getFont(0,0));
        txt59.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt59.setPreferredSize(new java.awt.Dimension(180, 18));

        lab39.setFont(frames.UGui.getFont(0,0));
        lab39.setText("Текстура");
        lab39.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab39.setMaximumSize(new java.awt.Dimension(80, 18));
        lab39.setMinimumSize(new java.awt.Dimension(80, 18));
        lab39.setPreferredSize(new java.awt.Dimension(80, 18));

        txt25.setEditable(false);
        txt25.setFont(frames.UGui.getFont(0,0));
        txt25.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt25.setPreferredSize(new java.awt.Dimension(180, 18));

        btn14.setText("...");
        btn14.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn14.setMaximumSize(new java.awt.Dimension(21, 20));
        btn14.setMinimumSize(new java.awt.Dimension(21, 20));
        btn14.setName("btnField17"); // NOI18N
        btn14.setPreferredSize(new java.awt.Dimension(21, 20));
        btn14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn14colorToHandl(evt);
            }
        });

        lab46.setFont(frames.UGui.getFont(0,0));
        lab46.setText("Высота ручки");
        lab46.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab46.setPreferredSize(new java.awt.Dimension(80, 18));

        txt16.setEditable(false);
        txt16.setFont(frames.UGui.getFont(0,0));
        txt16.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt16.setPreferredSize(new java.awt.Dimension(180, 18));

        txt31.setEditable(false);
        txt31.setFont(frames.UGui.getFont(0,0));
        txt31.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt31.setPreferredSize(new java.awt.Dimension(56, 18));

        btn6.setText("...");
        btn6.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn6.setMaximumSize(new java.awt.Dimension(21, 20));
        btn6.setMinimumSize(new java.awt.Dimension(21, 20));
        btn6.setName("btnField17"); // NOI18N
        btn6.setPreferredSize(new java.awt.Dimension(21, 20));
        btn6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn6heightHandlToStvorka(evt);
            }
        });

        lab26.setFont(frames.UGui.getFont(0,0));
        lab26.setText("Петля");
        lab26.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab26.setMaximumSize(new java.awt.Dimension(80, 18));
        lab26.setMinimumSize(new java.awt.Dimension(80, 18));
        lab26.setPreferredSize(new java.awt.Dimension(80, 18));

        txt45.setEditable(false);
        txt45.setFont(frames.UGui.getFont(0,0));
        txt45.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt45.setPreferredSize(new java.awt.Dimension(180, 18));

        btn15.setText("...");
        btn15.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn15.setMaximumSize(new java.awt.Dimension(21, 20));
        btn15.setMinimumSize(new java.awt.Dimension(21, 20));
        btn15.setName("btnField17"); // NOI18N
        btn15.setPreferredSize(new java.awt.Dimension(21, 20));
        btn15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn15artiklToLoop(evt);
            }
        });

        lab48.setFont(frames.UGui.getFont(0,0));
        lab48.setText("Название");
        lab48.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab48.setMaximumSize(new java.awt.Dimension(80, 18));
        lab48.setMinimumSize(new java.awt.Dimension(80, 18));
        lab48.setPreferredSize(new java.awt.Dimension(80, 18));

        txt57.setEditable(false);
        txt57.setFont(frames.UGui.getFont(0,0));
        txt57.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt57.setPreferredSize(new java.awt.Dimension(180, 18));

        lab44.setFont(frames.UGui.getFont(0,0));
        lab44.setText("Текстура");
        lab44.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab44.setMaximumSize(new java.awt.Dimension(80, 18));
        lab44.setMinimumSize(new java.awt.Dimension(80, 18));
        lab44.setPreferredSize(new java.awt.Dimension(80, 18));

        txt47.setEditable(false);
        txt47.setFont(frames.UGui.getFont(0,0));
        txt47.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt47.setPreferredSize(new java.awt.Dimension(180, 18));

        btn17.setText("...");
        btn17.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn17.setMaximumSize(new java.awt.Dimension(21, 20));
        btn17.setMinimumSize(new java.awt.Dimension(21, 20));
        btn17.setName("btnField17"); // NOI18N
        btn17.setPreferredSize(new java.awt.Dimension(21, 20));
        btn17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn17colorToLoop(evt);
            }
        });

        javax.swing.GroupLayout pan19Layout = new javax.swing.GroupLayout(pan19);
        pan19.setLayout(pan19Layout);
        pan19Layout.setHorizontalGroup(
            pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan19Layout.createSequentialGroup()
                        .addComponent(lab45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt30, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan19Layout.createSequentialGroup()
                        .addComponent(lab25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt21, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan19Layout.createSequentialGroup()
                        .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lab46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lab39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pan19Layout.createSequentialGroup()
                                .addComponent(txt16, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt31, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txt25, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pan19Layout.createSequentialGroup()
                        .addComponent(lab70, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt59, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
                    .addGroup(pan19Layout.createSequentialGroup()
                        .addComponent(lab30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt20, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan19Layout.createSequentialGroup()
                        .addComponent(lab26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt45, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan19Layout.createSequentialGroup()
                        .addComponent(lab48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt57, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                    .addGroup(pan19Layout.createSequentialGroup()
                        .addComponent(lab44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt47, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pan19Layout.setVerticalGroup(
            pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt59, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab70, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(133, Short.MAX_VALUE))
        );

        tabb2.addTab("Фурнитура", pan19);

        lab71.setFont(frames.UGui.getFont(0,0));
        lab71.setText("Замок");
        lab71.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab71.setMaximumSize(new java.awt.Dimension(80, 18));
        lab71.setMinimumSize(new java.awt.Dimension(80, 18));
        lab71.setPreferredSize(new java.awt.Dimension(80, 18));

        btn24.setText("...");
        btn24.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn24.setMaximumSize(new java.awt.Dimension(21, 20));
        btn24.setMinimumSize(new java.awt.Dimension(21, 20));
        btn24.setName("btnField17"); // NOI18N
        btn24.setPreferredSize(new java.awt.Dimension(21, 20));
        btn24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn24colorToLock(evt);
            }
        });

        txt46.setEditable(false);
        txt46.setFont(frames.UGui.getFont(0,0));
        txt46.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt46.setPreferredSize(new java.awt.Dimension(180, 18));

        btn23.setText("...");
        btn23.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn23.setMaximumSize(new java.awt.Dimension(21, 20));
        btn23.setMinimumSize(new java.awt.Dimension(21, 20));
        btn23.setName("btnField17"); // NOI18N
        btn23.setPreferredSize(new java.awt.Dimension(21, 20));
        btn23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn23artiklToLock(evt);
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
        txt48.setPreferredSize(new java.awt.Dimension(180, 18));

        txt58.setEditable(false);
        txt58.setFont(frames.UGui.getFont(0,0));
        txt58.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt58.setPreferredSize(new java.awt.Dimension(180, 18));

        lab3.setFont(frames.UGui.getFont(0,0));
        lab3.setText("Название");
        lab3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab3.setMaximumSize(new java.awt.Dimension(80, 18));
        lab3.setMinimumSize(new java.awt.Dimension(80, 18));
        lab3.setPreferredSize(new java.awt.Dimension(80, 18));

        lab28.setFont(frames.UGui.getFont(0,0));
        lab28.setText("Моск. сетка");
        lab28.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab28.setMaximumSize(new java.awt.Dimension(80, 18));
        lab28.setMinimumSize(new java.awt.Dimension(80, 18));
        lab28.setPreferredSize(new java.awt.Dimension(80, 18));

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
                btn16artiklToMosq(evt);
            }
        });

        lab37.setFont(frames.UGui.getFont(0,0));
        lab37.setText("Название");
        lab37.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab37.setMaximumSize(new java.awt.Dimension(80, 18));
        lab37.setMinimumSize(new java.awt.Dimension(80, 18));
        lab37.setPreferredSize(new java.awt.Dimension(80, 18));

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
                btn32colorToMosk(evt);
            }
        });

        lab43.setFont(frames.UGui.getFont(0,0));
        lab43.setText("Состав");
        lab43.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab43.setMaximumSize(new java.awt.Dimension(80, 18));
        lab43.setMinimumSize(new java.awt.Dimension(80, 18));
        lab43.setPreferredSize(new java.awt.Dimension(80, 18));

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
                btn31elemsToMosq(evt);
            }
        });

        javax.swing.GroupLayout pan23Layout = new javax.swing.GroupLayout(pan23);
        pan23.setLayout(pan23Layout);
        pan23Layout.setHorizontalGroup(
            pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(lab71, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt46, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(lab63, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt48, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(lab3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt58, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(lab28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt54, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(lab43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt56, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(lab37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt55, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan23Layout.createSequentialGroup()
                        .addComponent(lab66, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt60, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pan23Layout.setVerticalGroup(
            pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lab71, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab63, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lab28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lab37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt55, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab66, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(155, 155, 155))
        );

        tabb2.addTab("Дополн...", pan23);

        pan16.add(tabb2, java.awt.BorderLayout.CENTER);

        panContainer.add(pan16, "card16");

        pan17.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Соединения", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 1)));

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
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.eProp.locale); // NOI18N
        btn26.setToolTipText(bundle.getString("Поиск записи")); // NOI18N
        btn26.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn26.setMaximumSize(new java.awt.Dimension(18, 18));
        btn26.setMinimumSize(new java.awt.Dimension(18, 18));
        btn26.setName("btn26"); // NOI18N
        btn26.setPreferredSize(new java.awt.Dimension(18, 18));
        btn26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn26joinToFrame(evt);
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
                btn27joinToFrame(evt);
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
                btn28joinToFrame(evt);
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
                        .addComponent(txt38, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt36, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan17Layout.createSequentialGroup()
                        .addComponent(lab50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt37, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt42, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt41, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt40, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt39, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab59, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt43, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt44, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pan17Layout.setVerticalGroup(
            pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan17Layout.createSequentialGroup()
                .addContainerGap()
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
                .addContainerGap(179, Short.MAX_VALUE))
        );

        panContainer.add(pan17, "card17");

        pan18.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Коробка", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 1)));

        lab35.setFont(frames.UGui.getFont(0,0));
        lab35.setText("Ширина");
        lab35.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab35.setPreferredSize(new java.awt.Dimension(80, 18));

        txt17.setEditable(false);
        txt17.setFont(frames.UGui.getFont(0,0));
        txt17.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt17.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt17.setPreferredSize(new java.awt.Dimension(60, 18));

        lab38.setFont(frames.UGui.getFont(0,0));
        lab38.setText("Высота");
        lab38.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab38.setPreferredSize(new java.awt.Dimension(80, 18));

        txt22.setEditable(false);
        txt22.setFont(frames.UGui.getFont(0,0));
        txt22.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt22.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt22.setPreferredSize(new java.awt.Dimension(60, 18));

        lab77.setFont(frames.UGui.getFont(0,0));
        lab77.setText("Артикул");
        lab77.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab77.setPreferredSize(new java.awt.Dimension(80, 18));

        txt53.setEditable(false);
        txt53.setFont(frames.UGui.getFont(0,0));
        txt53.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt53.setPreferredSize(new java.awt.Dimension(180, 18));

        btn36.setText("...");
        btn36.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn36.setMaximumSize(new java.awt.Dimension(21, 20));
        btn36.setMinimumSize(new java.awt.Dimension(21, 20));
        btn36.setName("btnField17"); // NOI18N
        btn36.setPreferredSize(new java.awt.Dimension(21, 20));
        btn36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn36sysprofToKorobka(evt);
            }
        });

        lab78.setFont(frames.UGui.getFont(0,0));
        lab78.setText("Название");
        lab78.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab78.setPreferredSize(new java.awt.Dimension(80, 18));

        txt66.setEditable(false);
        txt66.setFont(frames.UGui.getFont(0,0));
        txt66.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt66.setPreferredSize(new java.awt.Dimension(180, 18));

        pan21.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Текстура изделия", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 0)));
        pan21.setPreferredSize(new java.awt.Dimension(320, 104));

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
                colorToKorobka(evt);
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
                colorToKorobka(evt);
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
                        .addComponent(txt14, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE))
                    .addGroup(pan21Layout.createSequentialGroup()
                        .addComponent(lab27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt9, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE))
                    .addGroup(pan21Layout.createSequentialGroup()
                        .addComponent(lab31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt13, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)))
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

        lab79.setFont(frames.UGui.getFont(0,0));
        lab79.setText("Аналог");
        lab79.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab79.setPreferredSize(new java.awt.Dimension(80, 18));

        txt67.setEditable(false);
        txt67.setFont(frames.UGui.getFont(0,0));
        txt67.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt67.setPreferredSize(new java.awt.Dimension(180, 18));

        javax.swing.GroupLayout pan18Layout = new javax.swing.GroupLayout(pan18);
        pan18.setLayout(pan18Layout);
        pan18Layout.setHorizontalGroup(
            pan18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan18Layout.createSequentialGroup()
                .addGroup(pan18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan18Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pan18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pan18Layout.createSequentialGroup()
                                .addGroup(pan18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pan18Layout.createSequentialGroup()
                                        .addComponent(lab38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(pan18Layout.createSequentialGroup()
                                        .addComponent(lab35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(pan18Layout.createSequentialGroup()
                                .addComponent(lab77, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt53, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pan18Layout.createSequentialGroup()
                                .addGroup(pan18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lab78, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lab79, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pan18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt66, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txt67, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addComponent(pan21, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE))
                .addContainerGap())
        );
        pan18Layout.setVerticalGroup(
            pan18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab77, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab78, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt66, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt67, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab79, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pan21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(166, Short.MAX_VALUE))
        );

        panContainer.add(pan18, "card18");

        add(panContainer, "card2");
    }// </editor-fold>//GEN-END:initComponents

    private void btn22sysprofToFrame(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn22sysprofToFrame
        try {
            if (winNode != null) {
                Layout layout = winNode.com5t().layout();
                double selectID = winNode.com5t().id; //id элемента который уже есть в конструкции, это либо виртуал. либо найденный по приоритету при построении модели
                Query qSysprofFilter = new Query(eSysprof.values(), eArtikl.values()); //тут будет список допустимых профилей из ветки системы
                //Цикл по профилям ветки
                for (int index = 0; index < qSysprof.size(); ++index) {
                    Record sysprofRec = qSysprof.get(index);

                    //Отфильтруем подходящие по параметрам
                    if (winNode.com5t().type.id2 == sysprofRec.getInt(eSysprof.use_type)) {
                        int useSideId = sysprofRec.getInt(eSysprof.use_side);
                        if (useSideId == layout.id
                                || ((layout == Layout.BOT || layout == Layout.TOP) && useSideId == UseSide.HORIZ.id)
                                || ((layout == Layout.RIG || layout == Layout.LEF) && useSideId == UseSide.VERT.id)
                                || useSideId == UseSide.ANY.id || useSideId == UseSide.MANUAL.id) {

                            qSysprofFilter.add(sysprofRec);
                            qSysprofFilter.table(eArtikl.up).add(qSysprof.table(eArtikl.up).get(index));
                        }
                    }
                }
                int paramID = winNode.com5t().artiklRec.getInt(eArtikl.id);
                Record paramRec = qSysprofFilter.stream().filter(rec -> rec.getInt(eSysprof.artikl_id) == paramID).findFirst().orElse(eSysprof.virtualRec(0));

                new DicSysprof(thiz, paramRec.getInt(eSysprof.id), (sysprofRec) -> {
                    Wincalc winc = wincalc();
                    if (winNode.com5t().type == enums.Type.BOX_SIDE) { //рама окна
                        double elemId = winNode.com5t().id;
                        GsonElem gsonRama = UCom.gson(winc.listAll, elemId);
                        if (sysprofRec.get(1) == null) {
                            UPar.remove(gsonRama.param, List.of(PKjson.sysprofID));
                        } else {
                            UPar.addProperty(gsonRama.param, List.of(PKjson.sysprofID), sysprofRec.getInt(eSysprof.id));
                        }
                        changeAndRedraw();

                    } else if (winNode.com5t().type == enums.Type.STV_SIDE) { //рама створки
                        double stvId = winNode.com5t().owner.id;
                        Com5t com5tStv = winc.listArea.stream().filter(el -> el.id == stvId).findFirst().get();
                        String stvSide = null;
                        if (layout == Layout.BOT) {
                            stvSide = PKjson.stvorkaBot;
                        } else if (layout == Layout.RIG) {
                            stvSide = PKjson.stvorkaRig;
                        } else if (layout == Layout.TOP) {
                            stvSide = PKjson.stvorkaTop;
                        } else if (layout == Layout.LEF) {
                            stvSide = PKjson.stvorkaLef;
                        }
                        //JsonObject jso = UGui.getAsJsonObject(paramObj, stvKey);
                        if (sysprofRec.get(1) == null) {
                            UPar.remove(com5tStv.gson.param, List.of(stvSide, PKjson.sysprofID));
                        } else {
                            UPar.addProperty(com5tStv.gson.param, List.of(stvSide, PKjson.sysprofID), sysprofRec.getInt(eSysprof.id));
                        }
                        changeAndRedraw();

                    } else {  //импост
                        double elemId = winNode.com5t().id;
                        GsonElem gsonElem = UCom.gson(winc.listAll, elemId);
                        if (sysprofRec.get(1) == null) {
                            UPar.remove(gsonElem.param, List.of(PKjson.sysprofID));
                        } else {
                            UPar.addProperty(gsonElem.param, List.of(PKjson.sysprofID), sysprofRec.getInt(eSysprof.id));
                        }
                        changeAndRedraw();
                    }
                }, qSysprofFilter);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.sysprofToFrame() " + e);
        }
    }//GEN-LAST:event_btn22sysprofToFrame

    private void btn18colorToFrame(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn18colorToFrame
        dicColorToProfile(evt, btn18, btn19);
    }//GEN-LAST:event_btn18colorToFrame

    private void btn19colorToFrame(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn19colorToFrame
        dicColorToProfile(evt, btn18, btn19);
    }//GEN-LAST:event_btn19colorToFrame

    private void btn20colorToFrame(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn20colorToFrame
        dicColorToProfile(evt, btn18, btn19);
    }//GEN-LAST:event_btn20colorToFrame

    private void btn3artiklToGlass(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn3artiklToGlass
        try {
            double selectID = winNode.com5t().id;
            Record systreeRec = eSystree.find(wincalc().nuni);
            
            //Список доступных толщин в ветке системы например 4;5;8
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

            new DicArtikl(thiz, artiklID, (artiklRec) -> {

                GsonElem glassElem = UCom.gson(wincalc().listAll, selectID);
                if (artiklRec.get(1) == null) {
                    UPar.remove(glassElem.param, List.of(PKjson.artglasID));
                } else {
                    UPar.addProperty(glassElem.param, List.of(PKjson.artglasID), artiklRec.getInt(eArtikl.id));
                }
                changeAndRedraw();

            }, qArtikl);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.artiklToGlass() " + e);
        }
    }//GEN-LAST:event_btn3artiklToGlass

    private void btn25colorToGlass(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn25colorToGlass
        ElemSimple glas = (ElemSimple) winNode.com5t();
        dicColorToElement(PKjson.colorGlass, glas.artiklRec);
    }//GEN-LAST:event_btn25colorToGlass

    private void btn5artiklToRascladka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn5artiklToRascladka
        try {
            ElemGlass classElem = (ElemGlass) winNode.com5t();
            double selectID = winNode.com5t().id;
            Query qArtikl = new Query(eArtikl.values()).sql(eArtikl.data(), eArtikl.level1, 1, eArtikl.level2, 12);

            new DicArtikl(thiz, (artiklRec) -> {

                if (artiklRec.get(eArtikl.id) != null) {
                    //classElem.gson.param.addProperty(PKjson.artiklRasc, artiklRec.getStr(eArtikl.id));
                    UPar.addProperty(classElem.gson.param, List.of(PKjson.artiklRasc), artiklRec.getInt(eArtikl.id));
                } else {
                    UPar.remove(classElem.gson.param, List.of(PKjson.artiklRasc));
                    UPar.remove(classElem.gson.param, List.of(PKjson.colorRasc));
                    UPar.remove(classElem.gson.param, List.of(PKjson.horRasc));
                    UPar.remove(classElem.gson.param, List.of(PKjson.verRasc));
                }
                changeAndRedraw();

            }, qArtikl);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.rascladkaToGlass() " + e);
        }
    }//GEN-LAST:event_btn5artiklToRascladka

    private void btn29colorToRascladka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn29colorToRascladka
        ElemSimple glas = (ElemSimple) winNode.com5t();
        dicColorToElement(PKjson.colorRasc, ((ElemGlass) glas).rascRec);
    }//GEN-LAST:event_btn29colorToRascladka

    private void spinHorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinHorStateChanged
        double selectID = winNode.com5t().id;
        GsonElem glassElem = UCom.gson(wincalc().listAll, selectID);
        glassElem.param.addProperty(PKjson.horRasc, spinHor.getValue().toString());
        changeAndRedraw();
    }//GEN-LAST:event_spinHorStateChanged

    private void spinVertStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinVertStateChanged
        double selectID = winNode.com5t().id;
        GsonElem glassElem = UCom.gson(wincalc().listAll, selectID);
        glassElem.param.addProperty(PKjson.verRasc, spinVert.getValue().toString());
        changeAndRedraw();
    }//GEN-LAST:event_spinVertStateChanged

    private void btn30blindsToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn30blindsToStvorka
        try {
            double stvorkaID = winNode.com5t().id;
            int furnitureID = ((AreaStvorka) winNode.com5t()).sysfurnRec.getInt(eSysfurn.furniture_id);
            Query qArtikl = new Query(eArtikl.values()).sql(eArtikl.data(), eArtikl.level1, 2, eArtikl.level2, 11);
            Query qResult = UGui.artTypeToFurndetList(furnitureID, qArtikl);
            new DicArtikl(thiz, (artiklRec) -> {

                GsonElem stvArea = UCom.gson(wincalc().listAll, stvorkaID);
                //stvArea.param.remove(PKjson.colorHand);
                if (artiklRec.get(1) == null) {
                    UPar.remove(stvArea.param, List.of(PKjson.artiklHand));
                    UPar.remove(stvArea.param, List.of(PKjson.colorHand));
                } else {
                    UPar.addProperty(stvArea.param, List.of(PKjson.artiklHand), artiklRec.getInt(eArtikl.id));
                }
                changeAndRedraw();

            }, qResult);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.handlToStvorka() " + e);
        }
    }//GEN-LAST:event_btn30blindsToStvorka

    private void btn33colorToBlinds(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn33colorToBlinds
        //
    }//GEN-LAST:event_btn33colorToBlinds

    private void btn34elemsToBlinds(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn34elemsToBlinds
        //
    }//GEN-LAST:event_btn34elemsToBlinds

    private void btn35artiklToBlinds(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn35artiklToBlinds
        //        try {
        //            double glassID = winNode.com5t().id;
        //            int artiklID = ((ElemGlass) winNode.com5t()).artiklRec.getInt(eArtikl.id);
        //            Query qBlinds = new Query(eArtikl.values()).sql(eArtikl.data(), eArtikl.level1, 5, eArtikl.level2, 50);
        //            //Query qResult = UGui.artTypeToFurndetList(furnitureID, qArtikl);
        //            new DicArtikl(this, (artiklRec) -> {
        //
        //                GsonElem glass = UCom.gson(wincalc().listAll, glassID);
        //                //stvArea.param.remove(PKjson.colorHand);
        //                if (artiklRec.get(1) == null) {
        //                    //stvArea.param.remove(PKjson.artiklB);
        //                } else {
        //                    glass.param.addProperty(PKjson.artiklHand, artiklRec.getStr(eArtikl.id));
        //                }
        //                //updateScript(stvorkaID);
        //
        //            }, qBlinds);
        //
        //        } catch (Exception e) {
        //            System.err.println("Ошибка:Systree.handlToStvorka() " + e);
        //        }
    }//GEN-LAST:event_btn35artiklToBlinds

    private void btn37sysprofToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn37sysprofToStvorka
        try {
            if (winNode != null) {
                Layout layout = winNode.com5t().layout();
                //double selectID = winNode.com5t().id; //id элемента который уже есть в конструкции, это либо виртуал. либо найденный по приоритету при построении модели
                Query qSysprofFilter = new Query(eSysprof.values(), eArtikl.values()); //тут будет список допустимых профилей из ветки системы
                //Цикл по профилям ветки
                for (int index = 0; index < qSysprof.size(); ++index) {
                    Record sysprofRec = qSysprof.get(index);

                    //Отфильтруем подходящие по параметрам
                    if (winNode.com5t().type.id2 == sysprofRec.getInt(eSysprof.use_type)) {
                        qSysprofFilter.add(sysprofRec);
                        qSysprofFilter.table(eArtikl.up).add(qSysprof.table(eArtikl.up).get(index));
                    }
                }
                int paramID = winNode.com5t().artiklRec.getInt(eArtikl.id);
                Record paramRec = qSysprofFilter.stream().filter(rec -> rec.getInt(eSysprof.artikl_id) == paramID).findFirst().orElse(eSysprof.virtualRec(0));

                new DicSysprof(thiz, paramRec.getInt(eSysprof.id), (sysprofRec) -> {
                    Wincalc winc = wincalc();
                    double elemId = winNode.com5t().id;
                    GsonElem gsonStv = UCom.gson(winc.listAll, elemId);
                    if (sysprofRec.get(1) == null) {
                        UPar.remove(gsonStv.param, List.of(PKjson.sysprofID));
                    } else {
                        UPar.addProperty(gsonStv.param, List.of(PKjson.sysprofID), sysprofRec.getInt(eSysprof.id));
                    }
                    changeAndRedraw();
                }, qSysprofFilter);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.sysprofToStvorka() " + e);
        }
    }//GEN-LAST:event_btn37sysprofToStvorka

    private void btn38colorToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn38colorToStvorka
        dicColorToProfile(evt, btn38, btn39);
    }//GEN-LAST:event_btn38colorToStvorka

    private void btn39colorToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn39colorToStvorka
        dicColorToProfile(evt, btn38, btn39);
    }//GEN-LAST:event_btn39colorToStvorka

    private void btn8colorToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn8colorToStvorka
        dicColorToProfile(evt, btn38, btn39);
    }//GEN-LAST:event_btn8colorToStvorka

    private void btn10sysfurnToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn10sysfurnToStvorka
        try {
            double windowsID = winNode.com5t().id;
            int systreeID = eSystree.find(wincalc().nuni).getInt(eSystree.id);
            Query qSysfurn = new Query(eSysfurn.values(), eFurniture.values()).sql(eSysfurn.data(), eSysfurn.systree_id, systreeID);
            qSysfurn.table(eFurniture.up).join(qSysfurn, eFurniture.data(), eSysfurn.furniture_id, eFurniture.id);
            new DicName(thiz, (sysfurnRec) -> {

                GsonElem stvArea = UCom.gson(wincalc().listAll, windowsID);
                if (sysfurnRec.get(1) == null) {
                    UPar.remove(stvArea.param, List.of(PKjson.sysfurnID));
                } else {
                    UPar.addProperty(stvArea.param, List.of(PKjson.sysfurnID), sysfurnRec.getInt(eSysfurn.id));
                }
                changeAndRedraw();

            }, qSysfurn, eFurniture.name);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.sysfurnToStvorka() " + e);
        }
    }//GEN-LAST:event_btn10sysfurnToStvorka

    private void btn21typeOpenToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn21typeOpenToStvorka
        try {
            new DicEnums(thiz, (typeopenRec) -> {

                double elemID = winNode.com5t().id;
                GsonElem stvArea = UCom.gson(wincalc().listAll, elemID);
                if (typeopenRec.get(1) == null) {
                    UPar.remove(stvArea.param, List.of(PKjson.typeOpen));
                } else {
                    UPar.addProperty(stvArea.param, List.of(PKjson.typeOpen), typeopenRec.getInt(0));
                }
                changeAndRedraw();

            }, TypeOpen1.REQUEST, TypeOpen1.LEFT, TypeOpen1.LEFTUP, TypeOpen1.LEFMOV,
                    TypeOpen1.RIGH, TypeOpen1.RIGHUP, TypeOpen1.RIGMOV, TypeOpen1.UPPER, TypeOpen1.EMPTY);
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.typeOpenToStvorka() " + e);
        }
    }//GEN-LAST:event_btn21typeOpenToStvorka

    private void btn12artiklToHand(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn12artiklToHand
        dicArtiklToFurniture(PKjson.artiklHand, 11);
    }//GEN-LAST:event_btn12artiklToHand

    private void btn14colorToHandl(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn14colorToHandl
        AreaStvorka stv = (AreaStvorka) winNode.com5t();
        dicColorToElement(PKjson.colorHand, stv.handRec[0]);
    }//GEN-LAST:event_btn14colorToHandl

    private void btn6heightHandlToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn6heightHandlToStvorka
        AreaSimple areaStv = (AreaSimple) winNode.com5t();
        int indexLayoutHandl = 0;
        if (LayoutHand.CONST.name.equals(txt16.getText())) {
            indexLayoutHandl = 1;
        } else if (LayoutHand.VAR.name.equals(txt16.getText())) {
            indexLayoutHandl = 2;
        }
        new DicHandl(thiz, (record) -> {
            try {
                double selectID = areaStv.id;
                GsonElem stvArea = UCom.gson(wincalc().listAll, selectID);
                if (record.get(1) != null) {
                    if (record.getInt(0) == 0) {
                        stvArea.param.addProperty(PKjson.positionHand, LayoutHand.MIDL.id);

                    } else if (record.getInt(0) == 1) {
                        stvArea.param.addProperty(PKjson.positionHand, LayoutHand.CONST.id);

                    } else if (record.getInt(0) == 2) {
                        stvArea.param.addProperty(PKjson.positionHand, LayoutHand.VAR.id);
                        stvArea.param.addProperty(PKjson.heightHand, record.getInt(1));
                    }
                    changeAndRedraw();
                }

            } catch (Exception e) {
                System.err.println("Ошибка:Systree.heightHandlToStvorka() " + e);
            }

        }, indexLayoutHandl);
    }//GEN-LAST:event_btn6heightHandlToStvorka

    private void btn15artiklToLoop(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn15artiklToLoop
        dicArtiklToFurniture(PKjson.artiklLoop, 12);
    }//GEN-LAST:event_btn15artiklToLoop

    private void btn17colorToLoop(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn17colorToLoop
        AreaStvorka stv = (AreaStvorka) winNode.com5t();
        dicColorToElement(PKjson.colorLoop, stv.loopRec[0]);
    }//GEN-LAST:event_btn17colorToLoop

    private void btn24colorToLock(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn24colorToLock
        AreaStvorka stv = (AreaStvorka) winNode.com5t();
        dicColorToElement(PKjson.colorLock, stv.lockRec[0]);
    }//GEN-LAST:event_btn24colorToLock

    private void btn23artiklToLock(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn23artiklToLock
        dicArtiklToFurniture(PKjson.artiklLock, 9);
    }//GEN-LAST:event_btn23artiklToLock

    private void btn16artiklToMosq(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn16artiklToMosq
        try {
            AreaStvorka areaStv = (AreaStvorka) winNode.com5t();
            Query qArtikl = new Query(eArtikl.values()).sql(eArtikl.data(), eArtikl.level1, 5, eArtikl.level2, 20);
            Com5t com5tMosq = wincalc().listAll.stream().filter(e -> e.type == enums.Type.MOSQUIT).findFirst().orElse(null);
            int artiklID = (com5tMosq != null && com5tMosq.artiklRec != null) ? com5tMosq.artiklRec.getInt(eArtikl.id) : -1; //id курсора в справочнике
            new DicArtikl(thiz, artiklID, (artiklRec) -> {
                //Удаление
                if (artiklRec.get(1) == null) { //если удаляем артикул то и удаляем москитку
                    areaStv.gson.childs.removeIf(e -> e.id == com5tMosq.id);
                    areaStv.winc.listElem.removeIf(e -> e.id == com5tMosq.id);
                    areaStv.winc.listAll.removeIf(e -> e.id == com5tMosq.id);

                    //Добаление
                } else {
                    if (com5tMosq != null) { //если уже москитка есть
                        UPar.addProperty(com5tMosq.gson.param, List.of(PKjson.artiklID), artiklRec.getInt(eArtikl.id));

                    } else {  //если москидки нет
                        GsonElem mosqNew = new GsonElem(enums.Type.MOSQUIT);
                        areaStv.gson.childs.add(mosqNew); //добавим ребёнка родителю
                        UPar.addProperty(mosqNew.param, List.of(PKjson.artiklID), artiklRec.getInt(eArtikl.id));
                    }
                }
                changeAndRedraw();

            }, qArtikl);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.artiklToMosq() " + e);
        }
    }//GEN-LAST:event_btn16artiklToMosq

    private void btn32colorToMosk(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn32colorToMosk
        Com5t mosq = wincalc().listAll.stream().filter(e -> e.type == enums.Type.MOSQUIT).findFirst().orElse(null);
        if (mosq != null) {
            dicColorToElement(mosq.gson, PKjson.colorID1, mosq.artiklRec);
        }
    }//GEN-LAST:event_btn32colorToMosk

    private void btn31elemsToMosq(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn31elemsToMosq
        try {
            AreaStvorka areaStv = (AreaStvorka) winNode.com5t();
            Com5t mosq = areaStv.childs.stream().filter(e -> e.type == enums.Type.MOSQUIT).findFirst().orElse(null);
            if (mosq != null) {
                int recordID = (mosq.sysprofRec == null) ? -1 : mosq.sysprofRec.getInt(eSysprof.id);
                Query qElements = new Query(eElement.values()).sql(eElement.data(), eElement.artikl_id, mosq.artiklRec.getInt(eArtikl.id));

                Object o = new DicName(thiz, recordID, (elementRec) -> {

                    if (elementRec.get(1) == null) {
                        UPar.remove(mosq.gson.param, List.of(PKjson.elementID));
                    } else {
                        UPar.addProperty(mosq.gson.param, List.of(PKjson.elementID), elementRec.getInt(eColor.id));
                    }
                    changeAndRedraw();

                }, qElements, eElement.name);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.mosqToElements() " + e);
        }
    }//GEN-LAST:event_btn31elemsToMosq

    private void btn26joinToFrame(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn26joinToFrame
        try {
            Wincalc winc = wincalc();
            if (winNode != null) {
                DefMutableTreeNode nodeParent = (DefMutableTreeNode) winNode.getParent();
                ElemSimple elem5e = (ElemSimple) nodeParent.com5t();
                JButton btn = (JButton) evt.getSource();
                int point = (btn.getName().equals("btn26")) ? 0 : (btn.getName().equals("btn27")) ? 1 : 2;
                ElemJoining elemJoin = UCom.join(winc.listJoin, elem5e, point);
                App.Joining.createFrame(thiz, elemJoin);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.joinToFrame() " + e);
        }
    }//GEN-LAST:event_btn26joinToFrame

    private void btn27joinToFrame(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn27joinToFrame
        try {
            Wincalc winc = wincalc();
            if (winNode != null) {
                DefMutableTreeNode nodeParent = (DefMutableTreeNode) winNode.getParent();
                ElemSimple elem5e = (ElemSimple) nodeParent.com5t();
                JButton btn = (JButton) evt.getSource();
                int point = (btn.getName().equals("btn26")) ? 0 : (btn.getName().equals("btn27")) ? 1 : 2;
                ElemJoining elemJoin = UCom.join(winc.listJoin, elem5e, point);
                App.Joining.createFrame(thiz, elemJoin);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.joinToFrame() " + e);
        }
    }//GEN-LAST:event_btn27joinToFrame

    private void btn28joinToFrame(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn28joinToFrame
        try {
            Wincalc winc = wincalc();
            if (winNode != null) {
                DefMutableTreeNode nodeParent = (DefMutableTreeNode) winNode.getParent();
                ElemSimple elem5e = (ElemSimple) nodeParent.com5t();
                JButton btn = (JButton) evt.getSource();
                int point = (btn.getName().equals("btn26")) ? 0 : (btn.getName().equals("btn27")) ? 1 : 2;
                ElemJoining elemJoin = UCom.join(winc.listJoin, elem5e, point);
                App.Joining.createFrame(thiz, elemJoin);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.joinToFrame() " + e);
        }
    }//GEN-LAST:event_btn28joinToFrame

    private void btn36sysprofToKorobka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn36sysprofToKorobka
        try {
            if (winNode != null) {
                Layout layout = winNode.com5t().layout();
                //double selectID = winNode.com5t().id; //id элемента который уже есть в конструкции, это либо виртуал. либо найденный по приоритету при построении модели
                Query qSysprofFilter = new Query(eSysprof.values(), eArtikl.values()); //тут будет список допустимых профилей из ветки системы
                //Цикл по профилям ветки
                for (int index = 0; index < qSysprof.size(); ++index) {
                    Record sysprofRec = qSysprof.get(index);

                    //Отфильтруем подходящие по параметрам
                    if (winNode.com5t().type.id2 == sysprofRec.getInt(eSysprof.use_type)) {
                        qSysprofFilter.add(sysprofRec);
                        qSysprofFilter.table(eArtikl.up).add(qSysprof.table(eArtikl.up).get(index));
                    }
                }
                int paramID = winNode.com5t().artiklRec.getInt(eArtikl.id);
                Record paramRec = qSysprofFilter.stream().filter(rec -> rec.getInt(eSysprof.artikl_id) == paramID).findFirst().orElse(eSysprof.virtualRec(0));

                new DicSysprof(thiz, paramRec.getInt(eSysprof.id), (sysprofRec) -> {
                    Wincalc winc = wincalc();
                    double elemId = winNode.com5t().id;
                    GsonElem gsonRama = UCom.gson(winc.listAll, elemId);
                    if (sysprofRec.get(1) == null) {
                        UPar.remove(gsonRama.param, List.of(PKjson.sysprofID));
                    } else {
                        UPar.addProperty(gsonRama.param, List.of(PKjson.sysprofID), sysprofRec.getInt(eSysprof.id));
                    }
                    changeAndRedraw();
                }, qSysprofFilter);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.sysprofToKorobka() " + e);
        }
    }//GEN-LAST:event_btn36sysprofToKorobka

    private void colorToKorobka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorToKorobka
        dicColorToProfile(evt, btn9, btn13);
    }//GEN-LAST:event_colorToKorobka
// <editor-fold defaultstate="collapsed" desc="Generated Code"> 
    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JButton btn26;
    private javax.swing.JButton btn27;
    private javax.swing.JButton btn28;
    private javax.swing.JButton btn29;
    private javax.swing.JButton btn3;
    private javax.swing.JButton btn30;
    private javax.swing.JButton btn31;
    private javax.swing.JButton btn32;
    private javax.swing.JButton btn33;
    private javax.swing.JButton btn34;
    private javax.swing.JButton btn35;
    private javax.swing.JButton btn36;
    private javax.swing.JButton btn37;
    private javax.swing.JButton btn38;
    private javax.swing.JButton btn39;
    private javax.swing.JButton btn5;
    private javax.swing.JButton btn6;
    private javax.swing.JButton btn8;
    private javax.swing.JButton btn9;
    private javax.swing.JLabel lab25;
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
    private javax.swing.JLabel lab4;
    private javax.swing.JLabel lab40;
    private javax.swing.JLabel lab41;
    private javax.swing.JLabel lab42;
    private javax.swing.JLabel lab43;
    private javax.swing.JLabel lab44;
    private javax.swing.JLabel lab45;
    private javax.swing.JLabel lab46;
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
    private javax.swing.JLabel lab70;
    private javax.swing.JLabel lab71;
    private javax.swing.JLabel lab72;
    private javax.swing.JLabel lab73;
    private javax.swing.JLabel lab74;
    private javax.swing.JLabel lab75;
    private javax.swing.JLabel lab76;
    private javax.swing.JLabel lab77;
    private javax.swing.JLabel lab78;
    private javax.swing.JLabel lab79;
    private javax.swing.JLabel lab80;
    private javax.swing.JLabel lab81;
    private javax.swing.JLabel lab82;
    private javax.swing.JLabel lab83;
    private javax.swing.JLabel lab84;
    private javax.swing.JLabel lab85;
    private javax.swing.JPanel pan12;
    private javax.swing.JPanel pan13;
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
    private javax.swing.JPanel panContainer;
    private javax.swing.JSpinner spinHor;
    private javax.swing.JSpinner spinVert;
    private javax.swing.JTabbedPane tabb2;
    private javax.swing.JTextField txt10;
    private javax.swing.JTextField txt13;
    private javax.swing.JTextField txt14;
    private javax.swing.JTextField txt15;
    private javax.swing.JTextField txt16;
    private javax.swing.JTextField txt17;
    private javax.swing.JTextField txt18;
    private javax.swing.JTextField txt19;
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
    private javax.swing.JTextField txt49;
    private javax.swing.JTextField txt50;
    private javax.swing.JTextField txt51;
    private javax.swing.JTextField txt52;
    private javax.swing.JTextField txt53;
    private javax.swing.JTextField txt54;
    private javax.swing.JTextField txt55;
    private javax.swing.JTextField txt56;
    private javax.swing.JTextField txt57;
    private javax.swing.JTextField txt58;
    private javax.swing.JTextField txt59;
    private javax.swing.JTextField txt60;
    private javax.swing.JTextField txt61;
    private javax.swing.JTextField txt62;
    private javax.swing.JTextField txt63;
    private javax.swing.JTextField txt64;
    private javax.swing.JTextField txt65;
    private javax.swing.JTextField txt66;
    private javax.swing.JTextField txt67;
    private javax.swing.JTextField txt68;
    private javax.swing.JTextField txt69;
    private javax.swing.JTextField txt70;
    private javax.swing.JTextField txt9;
    // End of variables declaration//GEN-END:variables
// </editor-fold> 

    public ListenerAction sizeEvent = () -> {
        Wincalc w = wincalc();
        if (w != null) {
            txt17.setText(UCom.format(w.width(), 1));
            txt22.setText(UCom.format(w.height(), 1));
        }
    };

    public final void initElements() {
        panCont.add(pan13, "card13");
        panCont.add(pan15, "card15");
        panCont.add(pan16, "card16");
        panCont.add(pan17, "card17");
        panCont.add(pan18, "card18");
        ((CardLayout) panCont.getLayout()).show(panCont, "card18");
        UGui.setDocumentFilter(3, txt17, txt22, txt24, txt26);
    }
}
