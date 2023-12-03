package builder;

import builder.making.Cal5e;
import builder.making.Joining;
import builder.model.AreaRectangl;
import builder.model.AreaSimple;
import builder.model.AreaStvorka;
import builder.model.Com5t;
import builder.model.ElemCross;
import builder.model.ElemFrame;
import builder.model.ElemGlass;
import builder.model.ElemSimple;
import builder.making.Specific;
import builder.making.UColor;
import builder.model.ElemMosquit;
import builder.script.GsonElem;
import builder.script.GsonRoot;
import com.google.gson.GsonBuilder;
import common.ArrayJoin;
import common.ArraySpc;
import common.LinkedCom;
import common.listener.ListenerKey;
import common.listener.ListenerMouse;
import dataset.Record;
import domain.eArtikl;
import domain.eSyspar1;
import domain.eSysprof;
import enums.Type;
import enums.UseArtiklTo;
import frames.swing.draw.Canvas;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;

public class Wincalc {

    public Integer nuni = 0; //код системы
    public Record syssizeRec = null; //системные константы     
    public double genId = 0; //для генерации ключа в спецификации
    public String script = null;
    public int colorID1 = -1, colorID2 = 1, colorID3 = -1; //базовый,внутр,внещний 
    public double costpric1 = 0; //себест. за ед. без отхода     
    public double costpric2 = 0; //себест. за ед. с отходом
    public double price = 0; //стоимость без скидки
    public double cost2 = 0; //стоимость с технологической скидкой
    public double weight = 0; //масса конструкции  
    public BufferedImage bufferImg = null;  //образ рисунка
    public ImageIcon imageIcon = null; //рисунок конструкции
    public Graphics2D gc2d = null; //графический котекст рисунка  
    public double scale = 1; //коэффициент сжатия
    public Canvas canvas = null;
    public ArrayList<ListenerKey> keyboardPressed = new ArrayList();
    public ArrayList<ListenerMouse> mousePressed = new ArrayList();
    public ArrayList<ListenerMouse> mouseReleased = new ArrayList();
    public ArrayList<ListenerMouse> mouseDragged = new ArrayList();
    public HashMap<Integer, Record> mapPardef = new HashMap(); //пар. по умолчанию + наложенные пар. клиента
    public LinkedCom<AreaSimple> listArea = new LinkedCom(); //список ареа.
    public LinkedCom<ElemSimple> listElem = new LinkedCom(); //список элем.
    public LinkedCom<Com5t> listAll = new LinkedCom(); //список всех компонентов (area + elem)
    public ArraySpc<Specific> listSpec = new ArraySpc(); //спецификация
    public ArrayJoin listJoin = new ArrayJoin(); //список соединений рам и створок 
    public Cal5e calcJoining, calcElements, calcFilling, calcFurniture, calcTariffication; //объекты калькуляции конструктива

    public GsonRoot gson = null; //объектная модель конструкции 1-го уровня
    public AreaRectangl root = null; //объектная модель конструкции 2-го уровня

    public Wincalc() {
    }

    public Wincalc(String script) {
        parsing(script);
    }

    public void parsing(String script) {
        //Для тестирования
        //System.out.println(new GsonBuilder().create().toJson(new com.google.gson.JsonParser().parse(script)));
        //System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(new com.google.gson.JsonParser().parse(script)));

        //Инит свойств окна
        this.script = script;
        genId = 0;
        syssizeRec = null;
        mapPardef.clear();
        List.of((List) listArea, (List) listElem, (List) listSpec, (List) listAll, (List) listJoin).forEach(el -> el.clear());

        //Создание Gson класса
        gson = new GsonBuilder().create().fromJson(script, GsonRoot.class);
        gson.setOwner(this);

        //Инит конструктива
        this.nuni = gson.nuni;
        Record sysprofRec = eSysprof.find2(nuni, UseArtiklTo.FRAME);
        this.colorID1 = (gson.color1 == -3) ? UColor.colorFromArtikl(sysprofRec.getInt(eSysprof.artikl_id)) : gson.color1;
        this.colorID2 = (gson.color2 == -3) ? UColor.colorFromArtikl(sysprofRec.getInt(eSysprof.artikl_id)) : gson.color2;
        this.colorID3 = (gson.color3 == -3) ? UColor.colorFromArtikl(sysprofRec.getInt(eSysprof.artikl_id)) : gson.color3;
        eSyspar1.find(nuni).forEach(syspar1Rec -> mapPardef.put(syspar1Rec.getInt(eSyspar1.groups_id), syspar1Rec)); //загрузим параметры по умолчанию

        //if (Type.RECTANGL == gson.type) {
        root = new AreaRectangl(this, gson);
        //}

        passElements(root, gson);

        //Создание полигона рамы
        this.calcLocation();
    }

    private void passElements(AreaSimple owner, GsonElem gson) {
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
                        ElemFrame elem5e = new ElemFrame(this, js, owner);
                        root.frames.add(elem5e);

                    } else if (Type.IMPOST == js.type || Type.SHTULP == js.type || Type.STOIKA == js.type) {
                        ElemCross elem5e = new ElemCross(this, js, owner);
                        owner.childs.add(elem5e); //добавим ребёнка родителю

                    } else if (Type.GLASS == js.type) {
                        ElemGlass elem5e = new ElemGlass(this, js, owner);
                        owner.childs.add(elem5e); //добавим ребёнка родителю

                    } else if (Type.MOSKITKA == js.type) {
                        ElemMosquit elem5e = new ElemMosquit(this, js, owner);
                        owner.childs.add(elem5e); //добавим ребёнка родителю
                    }
                }
                //Теперь вложенные элементы
                for (Map.Entry<AreaSimple, GsonElem> entry : hm.entrySet()) {
                    passElements(entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Wincalc.elements(*) " + e);
        }
    }

    //Кальк.коорд. элементов конструкции
    public void calcLocation() {
        try {
            //Главное окно ограниченное сторонами рамы
            this.root.calcLocation();

            //Пилим полигоны на ареа справа и слева
            this.listElem.filter(Type.IMPOST, Type.SHTULP, Type.STOIKA).forEach(e -> e.calcLocation());

            //Создание и коррекция сторон створки
            this.listArea.filter(Type.STVORKA).forEach(e -> e.calcLocation());

            //Рассчёт полигонов сторон рамы
            this.listElem.filter(Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.GLASS).forEach(e -> e.calcLocation());
        } catch (Exception s) {
            System.err.println("Ошибка:Wincalc.location() " + s);
        }
    }

    //Спецификация и тарификация 
    public void calcTarification(boolean norm_otx) {
        weight = 0;
        price = 0;
        cost2 = 0;
        try {
            //Каждый элемент конструкции попадает в спецификацию через функцию setSpecific()   
            listElem.filter4(Type.FRAME_SIDE).forEach(elem -> elem.setSpecific()); //спецификация ведущих элементов конструкции

            //Детали элемента через конструктив попадают в спецификацию через функцию addSpecific();
            //calcJoining = new Joining(this); //соединения
            //calcJoining.calc();
//            calcElements = new builder.making.Elements(this);
//            calcElements.calc();
//            calcFilling = new builder.making.Filling(this); //заполнения
//            calcFilling.calc();
//            calcFurniture = new builder.making.Furniture(this); //фурнитура 
//            calcFurniture.calc();
            calcTariffication = new builder.making.Tariffic(this, norm_otx); //тарификация 
            calcTariffication.calc();

            //Построим список спецификации
            for (ElemSimple elem5e : listElem) {
                if (elem5e.spcRec.artikl.isEmpty() || elem5e.spcRec.artikl.trim().charAt(0) != '@') {
                    listSpec.add(elem5e.spcRec);
                }
                for (Specific spc : elem5e.spcRec.spcList) {
                    if (spc.artikl.isEmpty() || spc.artikl.trim().charAt(0) != '@') {
                        listSpec.add(spc);
                    }
                }
            }

            //Итоговая стоимость
            for (Specific spc : listSpec) {
                this.price = (this.price + spc.price); //общая стоимость без скидки
                this.cost2 = (this.cost2 + spc.cost2); //общая стоимость со скидкой             
            }

            //Вес изделия
            LinkedList<ElemSimple> glassList = listElem.filter(Type.GLASS);
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
            LinkedList<ElemSimple> elemGlassList = this.listElem.filter(Type.GLASS);
            elemGlassList.stream().forEach(el -> el.paint());

            //Прорисовка импостов
            LinkedList<ElemSimple> elemImpostList = this.listElem.filter(Type.IMPOST);
            elemImpostList.stream().forEach(el -> el.paint());

            //Прорисовка штульпов
            LinkedList<ElemSimple> elemShtulpList = this.listElem.filter(Type.SHTULP);
            elemShtulpList.stream().forEach(el -> el.paint());

            //Прорисовка стоек
            LinkedList<ElemSimple> elemStoikaList = this.listElem.filter(Type.STOIKA);
            elemStoikaList.stream().forEach(el -> el.paint());

            //Прорисовка рам
            LinkedList<ElemSimple> elemFrameList = this.listElem.filter(Type.FRAME_SIDE);
            elemFrameList.stream().forEach(el -> el.paint());

            //Прорисовка створок
            LinkedList<ElemSimple> elemStvorkaList = this.listElem.filter(Type.STVORKA_SIDE);
            elemStvorkaList.stream().forEach(el -> el.paint());

            //Прорисовка раскладок
            //LinkedList<ElemSimple> glassList = winc.listElem.filter(Type.GLASS);
            //glassList.stream().forEach(el -> el.rascladkaPaint());
            //Прорисовка москиток
            //LinkedList<ElemSimple> mosqList = this.listElem.filter(Type.MOSKITKA);
            //mosqList.stream().forEach(el -> el.paint());
            //Рисунок в память
//            if (winc.bufferImg != null) {
//                ByteArrayOutputStream byteArrOutStream = new ByteArrayOutputStream();
//                ImageIO.write(winc.bufferImg, "png", byteArrOutStream);
//                if (eProp.dev == true) {
//                    File outputfile = new File("CanvasImage.png");
//                    ImageIO.write(winc.bufferImg, "png", outputfile);
//                }
//            }
        } catch (Exception s) {
            System.err.println("Ошибка:Wincalc.draw() " + s);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="GET AND SET"> 
    public double width() {
        return root.geom.getEnvelopeInternal().getWidth();
    }

    public double height() {
        return root.geom.getEnvelopeInternal().getHeight();
    }
    // </editor-fold>  
}
