package builder;

import builder.model.AreaRectangl;
import builder.model.AreaSimple;
import builder.model.AreaStvorka;
import builder.model.Com5t;
import builder.model.ElemCross;
import builder.model.ElemFrame;
import builder.model.ElemGlass;
import builder.model.ElemSimple;
import builder.making.TRecord;
import builder.making.UColor;
import builder.model.AreaArch;
import builder.model.AreaDoor;
import builder.model.AreaTrapeze;
import builder.model.ElemBlinds;
import builder.model.ElemJoining;
import builder.model.ElemMosquit;
import builder.script.GsonElem;
import builder.script.GsonRoot;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import common.UCom;
import common.eProp;
import common.listener.ListenerAction;
import common.listener.ListenerKey;
import common.listener.ListenerMouse;
import dataset.Record;
import domain.eArtikl;
import domain.ePrjprod;
import domain.eProject;
import domain.eSyspar1;
import domain.eSysprof;
import domain.eSyssize;
import enums.Type;
import enums.UseType;
import frames.swing.comp.Canvas;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import static startup.App.Top;

// см. алгоритм git -> 804d27409d
public class Wincalc {

    public Integer nuni = 0; //код системы  
    public double nppID = 0; //для генерации ключа в спецификации
    public int colorID1 = -1, colorID2 = 1, colorID3 = -1; //базовый,внутр,внещний 
    public Record syssizRec = null; //система константт
    public double cost1 = 0; //стоимость без скидки
    public double cost2 = 0; //стоимость со скидкой
    public double weight = 0; //масса конструкции  
    public BufferedImage bufferImg = null;  //образ рисунка
    public ImageIcon imageIcon = null; //рисунок конструкции
    public Graphics2D gc2d = null; //графический контекст рисунка  
    public double scale = 1; //коэффициент сжатия
    public Canvas canvas = null;
    public boolean sceleton = false;
    public GsonRoot gson = null; //объектная модель конструкции 1-го уровня
    public AreaSimple root = null; //объектная модель конструкции 2-го уровня    

    public ListenerAction actionEvent = () -> {
    };
    public ArrayList<ListenerKey> keyboardPressed = new ArrayList<ListenerKey>();
    public ArrayList<ListenerMouse> mousePressed = new ArrayList<ListenerMouse>();
    public ArrayList<ListenerMouse> mouseDragged = new ArrayList<ListenerMouse>();

    public HashMap<Integer, Record> mapPardef = new HashMap<>(); //пар. по умолчанию + наложенные пар. клиента
    public ArrayList<AreaSimple> listArea = new ArrayList<AreaSimple>(); //список ареа.
    public ArrayList<ElemSimple> listElem = new ArrayList<ElemSimple>(); //список элем.
    public ArrayList<ElemJoining> listJoin = new ArrayList<ElemJoining>(); //список соед.
    public ArrayList<Com5t> listAll = new ArrayList<Com5t>(); //список всех компонентов (area + elem)
    public ArrayList<TRecord> listSpec = new ArrayList<TRecord>(); //спецификация
    public ArrayList<TRecord> listKit = new ArrayList<TRecord>(); //комплектация

    public Wincalc() {
    }

    public Wincalc(String script) {
        build(script);
    }

    public void build(String script) {
        try {
            //System.out.println(new GsonBuilder().create().toJson(JsonParser.parseString(script))); //для тестирования
            //System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(JsonParser.parseString(script)));

            //Инит свойств
            nppID = 0;
            mapPardef.clear();
            List.of((List) listArea, (List) listElem, (List) listSpec, (List) listAll, (List) listJoin).forEach(el -> el.clear());

            //Создание Gson класса
            gson = new GsonBuilder().create().fromJson(script, GsonRoot.class);

            JsonParser parser = new JsonParser();
            JsonElement rootNode = parser.parse(script);

            gson.setOwner(this);

            //Инит конструктива
            nuni = (gson.nuni == null) ? -3 : gson.nuni;
            Record sysprofRec = eSysprof.find2(nuni, UseType.FRAME); //первая.запись коробки
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

        } catch (JsonSyntaxException e) {
            System.out.println("Ошибка: Wincalc.build()");
        }
    }

    //Цыклическое заполнение root по содержимому gson 
    private void creator(AreaSimple owner, GsonElem gson) {
        try {
            if (gson.childs != null) {
                LinkedHashMap<AreaSimple, GsonElem> hmDip = new LinkedHashMap();
                for (GsonElem js : gson.childs) {

                    if (Type.STVORKA == js.type) {
                        AreaStvorka area5e = new AreaStvorka(this, js, owner);
                        owner.childs.add(area5e); //добавим ребёнка родителю
                        hmDip.put(area5e, js); //погружение ареа

                    } else if (Type.AREA == js.type) {
                        AreaSimple area5e = new AreaSimple(this, js, owner);
                        owner.childs.add(area5e); //добавим ребёнка родителю
                        hmDip.put(area5e, js); //погружение ареа

                    } else if (Type.BOX_SIDE == js.type) {
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
                for (Map.Entry<AreaSimple, GsonElem> entry : hmDip.entrySet()) {
                    creator(entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Wincalc.creator() " + e);
        }
    }

    //Кальк.коорд. элементов конструкции
    public void location() {
        try {
            listElem.forEach(e -> e.initArtikle());
            root.setLocation();

            //Исключая импост створки т.к. ств. ещё не создана
            for (ElemSimple elem : listElem) {
                if (elem instanceof ElemFrame) {
                    elem.setLocation();
                } else if (elem instanceof ElemCross && elem.owner instanceof AreaStvorka == false) {
                    elem.setLocation();
                }
            }
            //Исключая створку т.к. она не создана
            for (AreaSimple area : listArea) {
                if (area.id != 0.0) {
                    if (area instanceof AreaStvorka == false && area.owner instanceof AreaStvorka == false) {
                        area.setLocation();
                    }
                }
            }

            //Создание створки
            UCom.filter(listArea, Type.STVORKA).forEach(e -> ((AreaStvorka) e).initStvorka());
            UCom.filter(listArea, Type.STVORKA).forEach(a -> a.frames.forEach(e -> e.initArtikle()));
            UCom.filter(listArea, Type.STVORKA).forEach(e -> e.setLocation());
            UCom.filter(listElem, Type.STV_SIDE).forEach(e -> e.setLocation());

            for (ElemSimple elem : listElem) {
                if (elem instanceof ElemGlass) {
                    elem.setLocation();
                } else if (elem instanceof ElemCross && elem.owner instanceof AreaStvorka) {
                    elem.setLocation();
                }
            }
            root.addJoining();  //L и T соединения
            UCom.filter(listArea, Type.STVORKA).forEach(e -> e.addJoining()); //прил. соед.

        } catch (Exception s) {
            System.err.println("Ошибка:Wincalc.location() " + s);
        }
    }

    //Спецификация и тарификация 
    public void specific(boolean norm_otx) {
        specific(norm_otx, false);
    }

    public void specific(boolean norm_otx, boolean man) {
        this.weight = 0;
        this.cost1 = 0;
        this.cost2 = 0;
        this.listSpec.clear();
        try {
            //Спецификация ведущих элементов конструкции
            listElem.forEach(elem -> elem.setSpecific());

            //Детали элемента через конструктив попадают в спецификацию через функцию addSpecific();
            new builder.making.TJoining(this).join(); //соединения
            new builder.making.TElement(this).elem(); //составы
            new builder.making.TFilling(this).fill(); //заполнения
            new builder.making.TFurniture(this).furn(); //фурнитура 
            new builder.making.TTariffic(this, norm_otx).calculate(); //тарификация

            //Заполним список спецификации
            for (ElemSimple elem5e : listElem) {
                if (elem5e.spcRec.artikl.isEmpty() || elem5e.spcRec.artikl.trim().charAt(0) != '@') {
                    this.listSpec.add(elem5e.spcRec);
                }
                for (TRecord spc : elem5e.spcRec.spcList) {
                    if (spc.artikl.isEmpty() || spc.artikl.trim().charAt(0) != '@') {
                        this.listSpec.add(spc);
                    }
                }
            }
            //Если спецификация на продукт менеджера
            if (man == true) {
                int prjprodID = Integer.valueOf(eProp.prjprodID.getProp());
                Record prjprodRec = ePrjprod.find(prjprodID);
                Record projectRec = eProject.find(prjprodRec.getInt(ePrjprod.project_id));
                if (prjprodRec != null) {
                    //Скидка менеджера
                    double disc = projectRec.getDbl(eProject.disc_all, 0) + projectRec.getDbl(eProject.disc_win, 0);
                    for (TRecord tRecord : this.listSpec) {
                        tRecord.cost2 = tRecord.cost2 - disc * tRecord.cost2 / 100; //скидка менеджера
                    }
                } else {
                    JOptionPane.showMessageDialog(Top.frame, "Выберите конструкцию в списке заказов", "Предупреждение", JOptionPane.OK_OPTION);
                }
            }

            //Итоговая стоимость
            for (TRecord spc : this.listSpec) {
                this.cost1 += spc.cost1; //общая стоимость без скидки
                this.cost2 += spc.cost2; //общая стоимость со скидкой                                   
            }

            //Вес изделия
            ArrayList<ElemSimple> glassList = UCom.filter(listElem, Type.GLASS);
            for (ElemSimple el : glassList) {
                this.weight += el.artiklRecAn.getDbl(eArtikl.density) * el.width() * el.height() / 1000000; //уд.вес * площадь = вес
            }

            Collections.sort(this.listSpec, (o1, o2) -> (o1.place.subSequence(0, 3) + o1.name + o1.width).compareTo(o2.place.subSequence(0, 3) + o2.name + o2.width));

        } catch (Exception e) {
            System.err.println("Ошибка:Wincalc.constructiv() " + e);
        }
    }

    //Рисуем конструкцию
    public void draw() {
        try {

            if (this.sceleton == false) {
                //Прорисовка стеклопакетов
                UCom.filter(this.listElem, Type.GLASS).stream().forEach(el -> el.paint());

                //Прорисовка раскладок
                UCom.filter(this.listElem, Type.GLASS).stream().forEach(el -> ((ElemGlass) el).rascladkaPaint());

                //Прорисовка москиток
                UCom.filter(this.listElem, Type.MOSQUIT).stream().forEach(el -> ((ElemMosquit) el).paint());

                //Прорисовка импостов
                UCom.filter(this.listElem, Type.IMPOST, Type.SHTULP, Type.STOIKA).stream().forEach(el -> el.paint());

                //Прорисовка рам
                UCom.filter(this.listElem, Type.BOX_SIDE).stream().forEach(el -> el.paint());

                //Прорисовка профилей створок
                UCom.filter(this.listElem, Type.STV_SIDE).stream().forEach(el -> el.paint());

                //Прорисока фурнитуры створок
                UCom.filter(this.listArea, Type.STVORKA).stream().forEach(el -> el.paint());

                //Размерные линии
                if (this.scale > .1) {
                    this.root.paint();
                }
            } else {
                //this.listArea.stream().forEach(el -> System.out.println("id=" + el.id));
                this.listArea.stream().forEach(el -> el.paint());
                this.listElem.stream().forEach(el -> el.paint());
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
