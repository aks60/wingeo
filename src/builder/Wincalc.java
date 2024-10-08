package builder;

import builder.making.SpcJoining;
import builder.model.AreaRectangl;
import builder.model.AreaSimple;
import builder.model.AreaStvorka;
import builder.model.Com5t;
import builder.model.ElemCross;
import builder.model.ElemFrame;
import builder.model.ElemGlass;
import builder.model.ElemSimple;
import builder.making.SpcRecord;
import builder.making.UColor;
import builder.model.AreaArch;
import builder.model.AreaDoor;
import builder.model.AreaTrapeze;
import builder.model.ElemBlinds;
import builder.model.ElemMosquit;
import builder.model.UGeo;
import builder.script.GsonElem;
import builder.script.GsonRoot;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import common.ArrayJoin;
import common.ArraySpc;
import common.ArrayCom;
import common.listener.ListenerKey;
import common.listener.ListenerMouse;
import dataset.Record;
import domain.eArtikl;
import domain.eSyspar1;
import domain.eSysprof;
import domain.eSyssize;
import enums.Type;
import enums.UseArtiklTo;
import frames.swing.Canvas;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;

public class Wincalc {

    public Integer nuni = 0; //код системы  
    public double spcId = 0; //для генерации ключа в спецификации
    public int colorID1 = -1, colorID2 = 1, colorID3 = -1; //базовый,внутр,внещний 
    public Record syssizRec = null; //система констант
    public double costpric1 = 0; //себест. за ед. без отхода     
    public double costpric2 = 0; //себест. за ед. с отходом
    public double price2 = 0; //стоимость без скидки
    public double cost2 = 0; //стоимость с технологической скидкой
    public double weight = 0; //масса конструкции  
    //public double square = 0; //площадь конструкции  
    public double count = 0; //колич. единиц
    public BufferedImage bufferImg = null;  //образ рисунка
    public ImageIcon imageIcon = null; //рисунок конструкции
    public Graphics2D gc2d = null; //графический котекст рисунка  
    public double scale = 1; //коэффициент сжатия
    public Canvas canvas = null;
    public GsonRoot gson = null; //объектная модель конструкции 1-го уровня
    public AreaSimple root = null; //объектная модель конструкции 2-го уровня    

    public ArrayList<ListenerKey> keyboardPressed = new ArrayList<ListenerKey>();
    public ArrayList<ListenerMouse> mousePressed = new ArrayList<ListenerMouse>();
    public ArrayList<ListenerMouse> mouseDragged = new ArrayList<ListenerMouse>();

    public HashMap<Integer, Record> mapPardef = new HashMap<>(); //пар. по умолчанию + наложенные пар. клиента
    public ArrayCom<AreaSimple> listArea = new ArrayCom<AreaSimple>(root); //список ареа.
    public ArrayCom<ElemSimple> listElem = new ArrayCom<ElemSimple>(root); //список элем.
    public ArrayCom<Com5t> listAll = new ArrayCom<Com5t>(root); //список всех компонентов (area + elem)
    public ArraySpc<SpcRecord> listSpec = new ArraySpc<SpcRecord>(); //спецификация
    public ArrayJoin listJoin = new ArrayJoin(); //список соединений рам и створок 

    public Wincalc() {
    }

    public Wincalc(String script) {
        build(script);
    }

    public void build(String script) {
        //System.out.println(new GsonBuilder().create().toJson(new com.google.gson.JsonParser().parse(script))); //для тестирования
        //System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(new com.google.gson.JsonParser().parse(script)));

        //Инит свойств
        spcId = 0;
        mapPardef.clear();
        List.of((List) listArea, (List) listElem, (List) listSpec, (List) listAll, (List) listJoin).forEach(el -> el.clear());

        //Создание Gson класса
        gson = new GsonBuilder().create().fromJson(script, GsonRoot.class);

        JsonParser parser = new JsonParser();
        JsonElement rootNode = parser.parse(script);

        gson.setOwner(this);

        //Инит конструктива
        nuni = (gson.nuni == null) ? -3 : gson.nuni;
        Record sysprofRec = eSysprof.find2(nuni, UseArtiklTo.FRAME); //первая.запись коробки
        Record artiklRec = eArtikl.find(sysprofRec.getInt(eSysprof.artikl_id), false); //артикул
        syssizRec = eSyssize.find(artiklRec); //системные константы
        colorID1 = (gson.color1 == -3) ? UColor.colorFromArtikl(sysprofRec.getInt(eSysprof.artikl_id)) : gson.color1;
        colorID2 = (gson.color2 == -3) ? UColor.colorFromArtikl(sysprofRec.getInt(eSysprof.artikl_id)) : gson.color2;
        colorID3 = (gson.color3 == -3) ? UColor.colorFromArtikl(sysprofRec.getInt(eSysprof.artikl_id)) : gson.color3;

        //Параметры по умолчанию
        eSyspar1.filter(nuni).forEach(syspar1Rec -> mapPardef.put(syspar1Rec.getInt(eSyspar1.groups_id), syspar1Rec));

        //Главное окно
        if (Type.RECTANGL == gson.type) {
            root = new AreaRectangl(this, gson);

        } else if (Type.TRAPEZE == gson.type) {
            root = new AreaTrapeze(this, gson);

        } else if (Type.ARCH == gson.type) {
            root = new AreaArch(this, gson);

        } else if (Type.DOOR == gson.type) {
            root = new AreaDoor(this, gson);
        }

        //Элементы конструкции
        creator(root, gson);

        //Обновление конструкции
        location();
    }

    private void creator(AreaSimple owner, GsonElem gson) {
        try {
            if (gson.childs != null) {
                LinkedHashMap<AreaSimple, GsonElem> hm = new LinkedHashMap();
                for (GsonElem js : gson.childs) {

                    if (Type.STVORKA == js.type) {
                        AreaSimple area5e = new AreaStvorka(this, js, owner);
                        owner.childs.add(area5e); //добавим ребёнка родителю
                        hm.put(area5e, js); //погружение ареа

                    } else if (Type.AREA == js.type) {
                        AreaSimple area5e = new AreaSimple(this, js, owner);
                        owner.childs.add(area5e); //добавим ребёнка родителю
                        hm.put(area5e, js); //погружение ареа

                    } else if (Type.FRAME_SIDE == js.type) {
                        ElemFrame elem5e = new ElemFrame(this, js.id, js, owner);
                        root.frames.add(elem5e);

                    } else if (List.of(Type.IMPOST, Type.STOIKA, Type.ERKER, Type.SHTULP).contains(js.type)) {
                        ElemCross elem5e = new ElemCross(this, js, owner);                        
                        owner.childs.add(elem5e); //добавим ребёнка родителю
                        //UGeo.normalizeElem(elem5e);

                    } else if (Type.GLASS == js.type) {
                        ElemGlass elem5e = new ElemGlass(this, js, owner);
                        owner.childs.add(elem5e); //добавим ребёнка родителю

                    } else if (Type.MOSQUIT == js.type) {
                        ElemMosquit elem5e = new ElemMosquit(this, js, owner);
                        owner.childs.add(elem5e); //добавим ребёнка родителю

                    } else if (Type.BLINDS == js.type) {
                        ElemBlinds elem5e = new ElemBlinds(this, js, owner);
                        owner.childs.add(elem5e); //добавим ребёнка родителю
                    }
                }
                //Теперь вложенные элементы
                for (Map.Entry<AreaSimple, GsonElem> entry : hm.entrySet()) {
                    creator(entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Wincalc.elements(*) " + e);
        }
    }

    //Кальк.коорд. элементов конструкции
    public void location() {
        try {
            //Главное окно ограниченное сторонами рамы
            root.setLocation();

            //Инит. артикулов элементов конструкции
            listElem.forEach(e -> e.initArtikle());

            //Создание и коррекция сторон створки
            if (root.type == Type.DOOR) {
                listArea.filter(Type.STVORKA).forEach(e -> e.setLocation());
            }

            //Пилим полигоны на ареа и рассчёт полигона импостов
            listElem.filter(Type.IMPOST, Type.STOIKA, Type.ERKER, Type.SHTULP).forEach(e -> e.setLocation());

            //Создание и коррекция сторон створки
            if (root.type != Type.DOOR) {
                listArea.filter(Type.STVORKA).forEach(e -> e.setLocation());
            }

            //Инит. артикулов створки
            listArea.filter(Type.STVORKA).forEach(a -> a.frames.forEach(e -> e.initArtikle()));

            //Рассчёт полигонов сторон рамы
            if (root.type == Type.DOOR) {
                for (ElemSimple elemSimple : listElem.filter(Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.GLASS)) {
                    elemSimple.setLocation();
                }
            } else {
                listElem.filter(Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.GLASS).forEach(e -> e.setLocation());
            }

            //Соединения рам, импостов и створок             
            root.addJoining();  //L и T соединения
            listArea.filter(Type.STVORKA).forEach(e -> e.addJoining()); //прил. соед.

        } catch (Exception s) {
            System.err.println("Ошибка:Wincalc.location() " + s);
        }
    }

    //Спецификация и тарификация 
    public void specification(boolean norm_otx) {
        weight = 0;
        price2 = 0;
        cost2 = 0;
        try {
            //Спецификация ведущих элементов конструкции
            listElem.forEach(elem -> elem.setSpecific());

            //Детали элемента через конструктив попадают в спецификацию через функцию addSpecific();
            new SpcJoining(this).calc(); //соединения
            new builder.making.SpcElement(this).calc(); //вставки
            new builder.making.SpcFilling(this).calc(); //заполнения
            new builder.making.SpcFurniture(this).calc(); //фурнитура 
            new builder.making.SpcTariffic(this, norm_otx).calc(); //рассчитаем тарификацию

            //Заполним список спецификации
            for (ElemSimple elem5e : listElem) {
                if (elem5e.spcRec.artikl.isEmpty() || elem5e.spcRec.artikl.trim().charAt(0) != '@') {
                    listSpec.add(elem5e.spcRec);
                }
                for (SpcRecord spc : elem5e.spcRec.spcList) {
                    if (spc.artikl.isEmpty() || spc.artikl.trim().charAt(0) != '@') {
                        listSpec.add(spc);
                    }
                }
            }

            //Итоговая стоимость
            for (SpcRecord spc : listSpec) {
                this.price2 = (this.price2 + spc.price1); //общая стоимость без скидки
                this.cost2 = (this.cost2 + spc.price2); //общая стоимость со скидкой             
                this.count = (this.count + spc.count); //колич. единиц                       
            }

            //Вес изделия
            ArrayList<ElemSimple> glassList = listElem.filter(Type.GLASS);
            for (ElemSimple el : glassList) {
                this.weight += el.artiklRecAn.getDbl(eArtikl.density) * el.width() * el.height() / 1000000; //уд.вес * площадь = вес
            }

            Collections.sort(listSpec, (o1, o2) -> (o1.place.subSequence(0, 3) + o1.name + o1.width).compareTo(o2.place.subSequence(0, 3) + o2.name + o2.width));

        } catch (Exception e) {
            System.err.println("Ошибка:Wincalc.constructiv() " + e);
        } 
    }

    //Рисуем конструкцию
    public void draw() {
        try {

            //Прорисовка стеклопакетов
            this.listElem.filter(Type.GLASS).stream().forEach(el -> el.paint());

            //Прорисовка раскладок
            this.listElem.filter(Type.GLASS).stream().forEach(el -> ((ElemGlass) el).rascladkaPaint());

            //Прорисовка москиток
            this.listElem.filter(Type.MOSQUIT).stream().forEach(el -> ((ElemMosquit) el).paint());

            //Прорисовка импостов
            this.listElem.filter(Type.IMPOST, Type.SHTULP, Type.STOIKA).stream().forEach(el -> el.paint());

            //Прорисовка рам
            this.listElem.filter(Type.FRAME_SIDE).stream().forEach(el -> el.paint());

            //Прорисовка профилей створок
            this.listElem.filter(Type.STVORKA_SIDE).stream().forEach(el -> el.paint());

            //Прорисока фурнитуры створок
            this.listArea.filter(Type.STVORKA).stream().forEach(el -> el.paint());

            //Размерные линии
            if (this.scale > .1) {
                this.root.paint();
            }

// <editor-fold defaultstate="collapsed" desc="Раскладка"> 
/*            
            //Прорисовка раскладок
            winc.listElem.filter(Type.GLASS).stream().forEach(el -> el.rascladkaPaint());
            Прорисовка москиток
            this.listElem.filter(Type.MOSKITKA).stream().forEach(el -> el.paint());
            Рисунок в память
            if (winc.bufferImg != null) {
                ByteArrayOutputStream byteArrOutStream = new ByteArrayOutputStream();
                ImageIO.write(winc.bufferImg, "png", byteArrOutStream);
                if (eProp.dev == true) {
                    File outputfile = new File("CanvasImage.png");
                    ImageIO.write(winc.bufferImg, "png", outputfile);
                }
            }
             */
// </editor-fold> 
        } catch (Exception s) {
            System.err.println("Ошибка:Wincalc.draw() " + s);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="GET AND SET"> 
    public double width() {
        return root.area.getGeometryN(0).getEnvelopeInternal().getWidth();
    }

    public double height() {
        return root.area.getGeometryN(0).getEnvelopeInternal().getHeight();
    }
    // </editor-fold>  
}
