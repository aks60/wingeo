package builder.param.check;

import builder.Wincalc;
import builder.model.AreaSimple;
import builder.model.Com5t;
import builder.model.ElemSimple;
import builder.param.ElementDet;
import builder.param.ElementVar;
import builder.param.FillingDet;
import builder.param.FillingVar;
import builder.param.FurnitureDet;
import builder.param.FurnitureVar;
import builder.param.JoiningDet;
import builder.param.JoiningVar;
import builder.script.GsonScript;
import dataset.Record;
import java.util.HashMap;

public class WincalcTest {

    public static Wincalc iwin2 = null; //601004
    public static Wincalc iwin3 = null; //604005
    public static Wincalc iwin4 = null; //700027

    public static HashMap<Integer, String> hmParam = new HashMap();
    public static int grup = -1;
    public static Record record = null;

    public static ElemSimple frame2_1 = null;
    public static ElemSimple frame2_3 = null;
    public static ElemSimple stv2_left_3 = null;
    public static ElemSimple imp2_horiz = null;
    public static ElemSimple imp2_vert = null;
    public static ElemSimple glass2_top = null;
    public static ElemSimple glass2_left = null;
    public static ElemSimple glass2_right = null;

    public static ElemSimple frame3_left = null;
    public static ElemSimple frame3_right = null;
    public static ElemSimple stv3_right_3 = null;
    public static ElemSimple imp3_vert = null;
    public static ElemSimple glass3_top = null;
    public static ElemSimple glass3_left = null;

    public static ElemSimple frame4_left = null;
    public static ElemSimple frame4_right = null;
    public static ElemSimple stv4_left_1 = null;
    public static ElemSimple stv4_right_3 = null;
    public static ElemSimple glass4_right = null;
    public static ElemSimple glass4_left = null;

    public static ElementVar elementVar2 = null;
    public static ElementDet elementDet2 = null;
    public static JoiningVar joiningVar2 = null;
    public static JoiningDet joiningDet2 = null;
    public static FillingVar fillingVar2 = null;
    public static FillingDet fillingDet2 = null;
    public static FurnitureVar furnitureVar2 = null;
    public static FurnitureDet furnitureDet2 = null;

    public static ElementVar elementVar3 = null;
    public static ElementDet elementDet3 = null;
    public static JoiningVar joiningVar3 = null;
    public static JoiningDet joiningDet3 = null;
    public static FillingVar fillingVar3 = null;
    public static FillingDet fillingDet3 = null;
    public static FurnitureVar furnitureVar3 = null;
    public static FurnitureDet furnitureDet3 = null;

    public static ElementVar elementVar4 = null;
    public static ElementDet elementDet4 = null;
    public static JoiningVar joiningVar4 = null;
    public static JoiningDet joiningDet4 = null;
    public static FillingVar fillingVar4 = null;
    public static FillingDet fillingDet4 = null;
    public static FurnitureVar furnitureVar4 = null;
    public static FurnitureDet furnitureDet4 = null;

    public static void init() {
        try {
            iwin2(); //601004
           // iwin3(); //604005
           // iwin4(); //700027

        } catch (Exception e) {
            System.err.println("ОШИБКА:param.test.WinacalcTest() " + e);
        }        
    }
    
    //601004 "KBE 58\\1 ОКНА\\Открывание внутрь (ств. Z77)"
    private static void iwin2() {
        iwin2 = new builder.Wincalc(); //601004
        iwin2.build(GsonScript.scriptPath(601004));
        iwin2.specific(true);
        elementVar2 = new ElementVar(iwin2);
        elementDet2 = new ElementDet(iwin2);
        joiningVar2 = new JoiningVar(iwin2);
        fillingVar2 = new FillingVar(iwin2);
        fillingDet2 = new FillingDet(iwin2);
        joiningVar2 = new JoiningVar(iwin2);
        joiningDet2 = new JoiningDet(iwin2);
        furnitureVar2 = new FurnitureVar(iwin2);
        furnitureDet2 = new FurnitureDet(iwin2);
        frame2_1 = getElem(iwin2.root, 1);

        frame2_3 = getElem(iwin2.root, 3);
        stv2_left_3 = getElem(iwin2.root, 9.3);
        imp2_vert = getElem(iwin2.root, 11);
        imp2_horiz = getElem(iwin2.root, 7);
        glass2_top = (ElemSimple) getElem(iwin2.root, 6);
        glass2_left = (ElemSimple) getElem(iwin2.root, 10);
        glass2_right = (ElemSimple) getElem(iwin2.root, 13);
    }

    //604005  "Wintech\\Termotech 742\\1 ОКНА"
    private static void iwin3() {
        iwin3 = new builder.Wincalc(); //604005
        iwin3.build(GsonScript.scriptPath(604005));
        iwin3.specific(true);
        elementVar3 = new ElementVar(iwin3);
        elementDet3 = new ElementDet(iwin3);
        joiningVar3 = new JoiningVar(iwin3);
        joiningDet3 = new JoiningDet(iwin3);
        fillingVar3 = new FillingVar(iwin3);
        fillingDet3 = new FillingDet(iwin3);
        joiningVar3 = new JoiningVar(iwin3);
        joiningDet3 = new JoiningDet(iwin3);
        furnitureVar3 = new FurnitureVar(iwin3);
        furnitureDet3 = new FurnitureDet(iwin3);

        frame3_left = getElem(iwin3.root, 1);
        frame3_right = getElem(iwin3.root, 3);
        stv3_right_3 = getElem(iwin3.root, 12.3);
        imp3_vert = getElem(iwin3.root, 11);
        glass3_top = (ElemSimple) getElem(iwin3.root, 6);
        glass3_left = (ElemSimple) getElem(iwin3.root, 10);
    }

    //700027  "Montblanc / Eco / 1 ОКНА (штульп)"
    private static void iwin4() {
        iwin4 = new builder.Wincalc(); //700027
        iwin4.build(GsonScript.scriptPath(700027));
        iwin4.specific(true);
        elementVar4 = new ElementVar(iwin4);
        elementDet4 = new ElementDet(iwin4);
        joiningVar4 = new JoiningVar(iwin4);
        joiningDet4 = new JoiningDet(iwin4);
        fillingVar4 = new FillingVar(iwin4);
        fillingDet4 = new FillingDet(iwin4);
        joiningVar4 = new JoiningVar(iwin4);
        joiningDet4 = new JoiningDet(iwin4);
        furnitureVar4 = new FurnitureVar(iwin4);
        furnitureDet4 = new FurnitureDet(iwin4);

        frame4_left = getElem(iwin4.root, 1);
        frame4_right = getElem(iwin4.root, 3);
        stv4_left_1 = getElem(iwin4.root, 5.1);
        stv4_right_3 = getElem(iwin4.root, 8.3);
        glass4_right = (ElemSimple) getElem(iwin4.root, 6);
        glass4_left = (ElemSimple) getElem(iwin4.root, 9);
    }

    //Получить элемент по ключу
    public static ElemSimple getElem(AreaSimple root, double id) {
        for (ElemSimple frm : root.frames) {
            if (frm.id == id) {
                return (ElemSimple) frm;
            }
        }
        for (Com5t it1 : root.childs) {
            if (it1.id == id) {
                return (ElemSimple) it1;
            }
            if (it1 instanceof AreaSimple) {
                for (ElemSimple frm : ((AreaSimple) it1).frames) {
                    if (frm.id == id) {
                        return (ElemSimple) frm;
                    }
                }
            }
            if (it1 instanceof AreaSimple) {
                for (Com5t it2 : ((AreaSimple) it1).childs) {
                    if (it2.id == id) {
                        return (ElemSimple) it2;
                    }
                    if (it2 instanceof AreaSimple) {
                        for (ElemSimple frm : ((AreaSimple) it2).frames) {
                            if (frm.id == id) {
                                return frm;
                            }
                        }
                    }
                    if (it2 instanceof AreaSimple) {
                        for (Com5t it3 : ((AreaSimple) it2).childs) {
                            if (it3.id == id) {
                                return (ElemSimple) it3;
                            }
                            if (it3 instanceof AreaSimple) {
                                for (ElemSimple frm : ((AreaSimple) it3).frames) {
                                    if (frm.id == id) {
                                        return frm;
                                    }
                                }
                            }
                            if (it3 instanceof AreaSimple) {
                                for (Com5t it4 : ((AreaSimple) it3).childs) {
                                    if (it4.id == id) {
                                        return (ElemSimple) it4;
                                    }
                                    if (it4 instanceof AreaSimple) {
                                        for (ElemSimple frm : ((AreaSimple) it4).frames) {
                                            if (frm.id == id) {
                                                return frm;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static Record param(String txt, int grup) {
        Record record = new Record();
        record.add("SEL");
        record.add(-3);
        record.add(txt);
        record.add(grup);
        record.add(-3);
        return record;
    }
}
