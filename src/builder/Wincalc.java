package builder;

import builder.model.AreaPolygon;
import builder.model.AreaSimple;
import builder.model.AreaStvorka;
import builder.model.Com5t;
import builder.model.ElemCross;
import builder.model.ElemFrame;
import builder.model.ElemGlass;
import builder.model.ElemSimple;
import builder.making.Specific;
import builder.model.Canvas2D;
import builder.script.GsonElem;
import builder.script.GsonRoot;
import com.google.gson.GsonBuilder;
import common.ArraySpc;
import common.LinkedCom;
import common.listener.ListenerMouse;
import dataset.Record;
import domain.eSyspar1;
import domain.eSysprof;
import enums.Type;
import enums.UseArtiklTo;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.GeometryFactory;

public class Wincalc {

    public Integer nuni = 0; //код системы
    public Record syssizeRec = null; //системные константы     
    public double genId = 0; //для генерации ключа в спецификации
    public int colorID1 = -1, colorID2 = 1, colorID3 = -1; //базовый,внутр,внещний 

    public Graphics2D gc2D = null; //графический котекст рисунка 
    public Canvas2D canvas = null;
    public double scale = 1;
    public ArrayList<ListenerMouse> mousePressed = new ArrayList();
    public ArrayList<ListenerMouse> mouseReleased = new ArrayList();
    public ArrayList<ListenerMouse> mouseDragged = new ArrayList();

    public HashMap<Integer, Record> mapPardef = new HashMap(); //пар. по умолчанию + наложенные пар. клиента
    public GeometryFactory geomFact = new GeometryFactory();			
    public LinkedCom<AreaSimple> listArea = new LinkedCom(); //список ареа.
    public LinkedCom<ElemSimple> listElem = new LinkedCom(); //список элем.
    public LinkedCom<ElemFrame> listFrame = new LinkedCom(); //список рам
    public List<ElemCross> listCross = new ArrayList(); //список имп.
    public ArraySpc<Specific> listSpec = new ArraySpc(); //спецификация

    public GsonRoot gson = null; //объектная модель конструкции 1-го уровня
    public AreaPolygon rootArea = null; //объектная модель конструкции 2-го уровня

    public Wincalc() {
    }

    public Wincalc(String script) {
        build(script);
    }

    /**
     * Построение окна из json скрипта
     *
     * @param script - json скрипт построения окна
     * @return rootArea - главное окно
     */
    public void build(String script) {
        try {
            //Инит свойств окна
            init();

            //Парсинг входного скрипта
            //Создание элементов конструкции
            parsing(script);

            //построение полигонов
            rootArea.setLocation();

            //Каждый элемент конструкции попадает в спецификацию через функцию setSpecific()            
            //listLine.forEach(elem -> elem.setSpecific()); //спецификация ведущих элементов конструкции
        } catch (Exception e) {
            System.err.println("Ошибка:Wincalc.build() " + e);
        }
    }

    private void parsing(String script) {
        //Для тестирования
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(new com.google.gson.JsonParser().parse(script)));

        gson = new GsonBuilder().create().fromJson(script, GsonRoot.class);
        gson.setOwner(this);

        //Инит конструктива
        this.nuni = gson.nuni;
        Record sysprofRec = eSysprof.find2(nuni, UseArtiklTo.FRAME);
        eSyspar1.find(nuni).forEach(syspar1Rec -> mapPardef.put(syspar1Rec.getInt(eSyspar1.groups_id), syspar1Rec)); //загрузим параметры по умолчанию

        rootArea = new AreaPolygon(this, gson);

        elements(rootArea, gson);
    }

    private void elements(Com5t owner, GsonElem gson) {
        try {
            LinkedHashMap<Com5t, GsonElem> hm = new LinkedHashMap();
            for (GsonElem js : gson.childs) {

                if (Type.STVORKA == js.type) {
                    AreaSimple area5e = new AreaStvorka(this, gson, owner);
                    owner.childs().add(area5e); //добавим ребёнка родителю
                    hm.put(area5e, js);

                } else if (Type.AREA == js.type) {
                    AreaSimple area5e = new AreaSimple(this, js, owner);
                    owner.childs().add(area5e); //добавим ребёнка родителю
                    listArea.add(area5e);
                    hm.put(area5e, js);

                } else if (Type.FRAME_SIDE == js.type) {
                    ElemFrame elem5e = new ElemFrame(this, js, owner);
                    listElem.add(elem5e);
                    listFrame.add(elem5e);

                } else if (Type.IMPOST == js.type || Type.SHTULP == js.type || Type.STOIKA == js.type) {
                    ElemCross elem5e = new ElemCross(this, js, owner);
                    owner.childs().add(elem5e); //добавим ребёнка родителю
                    listElem.add(elem5e);
                    listCross.add(elem5e);

                } else if (Type.GLASS == js.type) {
                    ElemGlass elem5e = new ElemGlass(this, js, owner);
                    owner.childs().add(elem5e); //добавим ребёнка родителю

                } else if (Type.MOSKITKA == js.type) {
                    //Elem2Mosquit elem5e = new Elem2Mosquit(this, js, owner);
                    //owner.childs().add(elem5e); //добавим ребёнка родителю

                }
            }

            //Теперь вложенные элементы
            for (Map.Entry<Com5t, GsonElem> entry : hm.entrySet()) {
                elements(entry.getKey(), entry.getValue());
            }

        } catch (Exception e) {
            System.err.println("Ошибка:Wincalc.elements(*) " + e);
        }
    }

    public void draw() {
        try {
            rootArea.setLocation();
            rootArea.paint();
            listFrame.forEach(e -> e.setLocation());
            listFrame.forEach(e -> e.paint());
            listCross.forEach(e -> e.setLocation());
            listCross.forEach(e -> e.paint());

        } catch (Exception e) {
            System.err.println("Ошибка:Wingeo.draw() " + e);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="GET AND SET"> 
    private void init() {
        genId = 0;
        syssizeRec = null;
        mapPardef.clear();
        List.of((List) listArea, (List) listFrame, (List) listCross, (List) listSpec).forEach(el -> el.clear());
    }

    public double width() {
        return rootArea.area.getBounds2D().getWidth();
    }

    public double height() {
        return rootArea.area.getBounds2D().getHeight();
    }
    // </editor-fold>  
}
