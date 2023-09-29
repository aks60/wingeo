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
import common.ArrayJoin;
import common.ArraySpc;
import common.LinkedCom;
import common.listener.ListenerMouse;
import dataset.Record;
import domain.eSyspar1;
import domain.eSysprof;
import enums.Form;
import enums.Type;
import enums.UseArtiklTo;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import org.locationtech.jts.geom.GeometryFactory;

public class Wincalc {

    public Integer nuni = 0; //код системы
    public Record syssizeRec = null; //системные константы     
    public double genId = 0; //для генерации ключа в спецификации
    public String script = null;
    public double width1 = 0; //ширина окна нижняя
    public double width2 = 0; //ширина окна верхняя    
    public double height1 = 0; //высота окна левая
    public double height2 = 0; //высота окна правая    
    public int colorID1 = -1, colorID2 = 1, colorID3 = -1; //базовый,внутр,внещний 
    public double costpric1 = 0; //себест. за ед. без отхода     
    public double costpric2 = 0; //себест. за ед. с отходом
    public double price = 0; //стоимость без скидки
    public double cost2 = 0; //стоимость с технологической скидкой
    public double weight = 0; //масса конструкции  
    public Form form = null; //форма контура (параметр в развитии)
    public BufferedImage bufferImg = null;  //образ рисунка
    public ImageIcon imageIcon = null; //рисунок конструкции
    public Graphics2D gc2d = null; //графический котекст рисунка  
    public double scale = 1; //коэффициент сжатия
    public Canvas2D canvas = null;
    public ArrayList<ListenerMouse> mousePressed = new ArrayList();
    public ArrayList<ListenerMouse> mouseReleased = new ArrayList();
    public ArrayList<ListenerMouse> mouseDragged = new ArrayList();

    public HashMap<Integer, Record> mapPardef = new HashMap(); //пар. по умолчанию + наложенные пар. клиента
    public GeometryFactory geomFact = new GeometryFactory();
    public LinkedCom<AreaSimple> listArea = new LinkedCom(); //список ареа.
    public LinkedCom<ElemSimple> listElem = new LinkedCom(); //список элем.
    public LinkedCom<ElemFrame> listFrame = new LinkedCom(); //список рам
    public List<ElemCross> listCross = new ArrayList(); //список имп.
    public LinkedCom<Com5t> listAll = new LinkedCom(); //список всех компонентов (area + elem)
    public ArraySpc<Specific> listSpec = new ArraySpc(); //спецификация
    public ArrayJoin listJoin = new ArrayJoin(); //список соединений рам и створок 

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
        //System.out.println(new GsonBuilder().create().toJson(new com.google.gson.JsonParser().parse(script)));
        //System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(new com.google.gson.JsonParser().parse(script)));

        gson = new GsonBuilder().create().fromJson(script, GsonRoot.class);
        gson.setOwner(this);

        //Инит конструктива
        this.nuni = gson.nuni;
        Record sysprofRec = eSysprof.find2(nuni, UseArtiklTo.FRAME);
        eSyspar1.find(nuni).forEach(syspar1Rec -> mapPardef.put(syspar1Rec.getInt(eSyspar1.groups_id), syspar1Rec)); //загрузим параметры по умолчанию

        rootArea = new AreaPolygon(this, gson);

        elements(rootArea, gson);
    }

    private void elements(AreaSimple owner, GsonElem gson) {
        try {
            LinkedHashMap<AreaSimple, GsonElem> hm = new LinkedHashMap();
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
            for (Map.Entry<AreaSimple, GsonElem> entry : hm.entrySet()) {
                elements(entry.getKey(), entry.getValue());
            }

        } catch (Exception e) {
            System.err.println("Ошибка:Wincalc.elements(*) " + e);
        }
    }

    //Конструктив и тарификация 
    public void constructiv(boolean norm_otx) {
//        weight = 0;
//        price = 0;
//        cost2 = 0;
//        //cost3 = 0;
//        try {
//            //Детали элемента через конструктив попадают в спецификацию через функцию addSpecific();
//            calcJoining = new Joining(this); //соединения
//            calcJoining.calc();
//            Cal5e calcElements = (eProp.old.read().equals("0")) //составы
//                    ? new builder.making.Elements(this)
//                    : new builder.making.Elements(this);
//            calcElements.calc();
//            calcFilling = (eProp.old.read().equals("0")) //заполнения
//                    ? new builder.making.Filling(this)
//                    : new builder.making.Filling(this);
//            calcFilling.calc();
//            calcFurniture = (eProp.old.read().equals("0")) //фурнитура 
//                    ? new builder.making.Furniture(this)
//                    : new builder.making.Furniture(this);
//            calcFurniture.calc();
//            calcTariffication = (eProp.old.read().equals("0")) //тарификация 
//                    ? new builder.making.Tariffic(this, norm_otx)
//                    : new builder.making.Tariffic(this, norm_otx);
//            calcTariffication.calc();
//
//            //Построим список спецификации
//            for (IElem5e elem5e : listElem) {
//                if (elem5e.spcRec().artikl.isEmpty() || elem5e.spcRec().artikl.trim().charAt(0) != '@') {
//                    listSpec.add(elem5e.spcRec());
//                }
//                for (Specific spc : elem5e.spcRec().spcList) {
//                    if (spc.artikl.isEmpty() || spc.artikl.trim().charAt(0) != '@') {
//                        listSpec.add(spc);
//                    }
//                }
//            }
//
//            //Итоговая стоимость
//            for (Specific spc : listSpec) {
//                this.price(this.price() + spc.price); //общая стоимость без скидки
//                this.cost2(this.cost2() + spc.cost2); //общая стоимость со скидкой             
//            }
//
//            //Вес изделия
//            LinkedList<IElem5e> glassList = listElem.filter(Type.GLASS);
//            for (IElem5e el : glassList) {
//                this.weight += el.artiklRecAn().getDbl(eArtikl.density) * el.width() * el.height() / 1000000; //уд.вес * площадь = вес
//            }
//
//            Collections.sort(listSpec, (o1, o2) -> (o1.place.subSequence(0, 3) + o1.name + o1.width).compareTo(o2.place.subSequence(0, 3) + o2.name + o2.width));
//
//        } catch (Exception e) {
//            System.err.println("Ошибка:Wincalc.constructiv() " + e);
//        }
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

    public double square() {
        return width() * height() / 1000000;
    }
    // </editor-fold>  
}
