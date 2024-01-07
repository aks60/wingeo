package builder.param.check;

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
import dataset.Record;
import java.util.HashMap;

public class ParamTest {

    protected HashMap<Integer, String> hmParam = new HashMap();
    protected int grup = -1;
    protected Record record = null;

    protected ElemSimple frame_left_2 = null;
    protected ElemSimple frame_right_2 = null;
    protected ElemSimple stv_right_2 = null;
    protected ElemSimple imp_horiz_2 = null;
    protected ElemSimple imp_vert_2 = null;
    protected ElemSimple glass_top_2 = null;
    protected ElemSimple glass_left_2 = null;
    protected ElemSimple glass_right_2 = null;

    protected ElemSimple frame_left_3 = null;
    protected ElemSimple frame_right_3 = null;
    protected ElemSimple stv_right_3 = null;
    protected ElemSimple imp_vert_3 = null;
    protected ElemSimple glass_top_3 = null;
    protected ElemSimple glass_left_3 = null;

    protected ElemSimple frame_left_4 = null;
    protected ElemSimple frame_right_4 = null;
    protected ElemSimple stv_left_4 = null;
    protected ElemSimple stv_right_4 = null;
    protected ElemSimple glass_right_4 = null;
    protected ElemSimple glass_left_4 = null;

    builder.Wincalc iwin_2 = null; //601004
    protected ElementVar elementVar2 = null;
    protected ElementDet elementDet2 = null;
    protected JoiningVar joiningVar2 = null;
    protected JoiningDet joiningDet2 = null;
    protected FillingVar fillingVar2 = null;
    protected FillingDet fillingDet2 = null;
    protected FurnitureVar furnitureVar2 = null;
    protected FurnitureDet furnitureDet2 = null;

    builder.Wincalc iwin_3 = null; //604005
    protected ElementVar elementVar3 = null;
    protected ElementDet elementDet3 = null;
    protected JoiningVar joiningVar3 = null;
    protected JoiningDet joiningDet3 = null;
    protected FillingVar fillingVar3 = null;
    protected FillingDet fillingDet3 = null;
    protected FurnitureVar furnitureVar3 = null;
    protected FurnitureDet furnitureDet3 = null;

    builder.Wincalc iwin_4 = null; //700027
    protected ElementVar elementVar4 = null;
    protected ElementDet elementDet4 = null;
    protected JoiningVar joiningVar4 = null;
    protected JoiningDet joiningDet4 = null;
    protected FillingVar fillingVar4 = null;
    protected FillingDet fillingDet4 = null;
    protected FurnitureVar furnitureVar4 = null;
    protected FurnitureDet furnitureDet4 = null;

    public ParamTest() {
        try {
            iwin2();
            iwin3();
            iwin4();
        } catch (Exception e) {
            System.err.println("ОШИБКА:param.test.ParamTest() " + e);
        }
    }

    //601004 "KBE 58\\1 ОКНА\\Открывание внутрь (ств. Z77)"
    private void iwin2() {
        iwin_2 = new builder.Wincalc(); //601004
        iwin_2.build(builder.script.GsonScript.scriptPath(601004));
        iwin_2.specific(true);
        elementVar2 = new ElementVar(iwin_2);
        elementDet2 = new ElementDet(iwin_2);
        joiningVar2 = new JoiningVar(iwin_2);
        fillingVar2 = new FillingVar(iwin_2);
        fillingDet2 = new FillingDet(iwin_2);
        joiningVar2 = new JoiningVar(iwin_2);
        joiningDet2 = new JoiningDet(iwin_2);
        furnitureVar2 = new FurnitureVar(iwin_2);
        furnitureDet2 = new FurnitureDet(iwin_2);
        frame_left_2 = getElem(iwin_2.root, 1.0f);
        frame_right_2 = getElem(iwin_2.root, 2.0f);
        stv_right_2 = getElem(iwin_2.root, 10.2f);
        imp_vert_2 = getElem(iwin_2.root, 12.0f);
        imp_horiz_2 = getElem(iwin_2.root, 7.0f);
        glass_top_2 = (ElemSimple) getElem(iwin_2.root, 6.0f);
        glass_left_2 = (ElemSimple) getElem(iwin_2.root, 11.0f);
        glass_right_2 = (ElemSimple) getElem(iwin_2.root, 15.0f);
    }

    //604005  "Wintech\\Termotech 742\\1 ОКНА"
    private void iwin3() {
        iwin_3 = new builder.Wincalc(); //604005
        iwin_3.build(builder.script.GsonScript.scriptPath(604005));
        iwin_3.specific(true);
        elementVar3 = new ElementVar(iwin_3);
        elementDet3 = new ElementDet(iwin_3);
        joiningVar3 = new JoiningVar(iwin_3);
        joiningDet3 = new JoiningDet(iwin_3);
        fillingVar3 = new FillingVar(iwin_3);
        fillingDet3 = new FillingDet(iwin_3);
        joiningVar3 = new JoiningVar(iwin_3);
        joiningDet3 = new JoiningDet(iwin_3);
        furnitureVar3 = new FurnitureVar(iwin_3);
        furnitureDet3 = new FurnitureDet(iwin_3);
        frame_left_3 = getElem(iwin_3.root, 1.0f);
        frame_right_3 = getElem(iwin_3.root, 2.0f);
        stv_right_3 = getElem(iwin_3.root, 10.2f);
        imp_vert_3 = getElem(iwin_3.root, 12.0f);
        glass_top_3 = (ElemSimple) getElem(iwin_3.root, 6.0f);
        glass_left_3 = (ElemSimple) getElem(iwin_3.root, 11.0f);
        glass_left_3.anglHoriz();
    }

    //700027  "Montblanc / Eco / 1 ОКНА (штульп)"
    private void iwin4() {
        iwin_4 = new builder.Wincalc(); //700027
        iwin_4.build(builder.script.GsonScript.scriptPath(700027));
        iwin_4.specific(true);
        elementVar4 = new ElementVar(iwin_4);
        elementDet4 = new ElementDet(iwin_4);
        joiningVar4 = new JoiningVar(iwin_4);
        joiningDet4 = new JoiningDet(iwin_4);
        fillingVar4 = new FillingVar(iwin_4);
        fillingDet4 = new FillingDet(iwin_4);
        joiningVar4 = new JoiningVar(iwin_4);
        joiningDet4 = new JoiningDet(iwin_4);
        furnitureVar4 = new FurnitureVar(iwin_4);
        furnitureDet4 = new FurnitureDet(iwin_4);
        frame_left_4 = getElem(iwin_4.root, 1.0f);
        frame_right_4 = getElem(iwin_4.root, 2.0f);
        stv_left_4 = getElem(iwin_4.root, 6.4f);
        stv_right_4 = getElem(iwin_4.root, 6.2f);
        glass_right_4 = (ElemSimple) getElem(iwin_4.root, 11.0f);
        glass_left_4 = (ElemSimple) getElem(iwin_4.root, 7.0f);
    }

    //Получить элемент по ключу
    public ElemSimple getElem(AreaSimple root, double id) {
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
