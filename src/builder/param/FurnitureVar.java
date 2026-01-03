package builder.param;

import dataset.Record;
import domain.eArtikl;
import domain.eFurnpar1;
import domain.eFurnside1;
import java.util.List;
import builder.Wincalc;
import builder.model.AreaStvorka;
import builder.model.Com5t;
import builder.model.ElemSimple;
import builder.model.UGeo;
import common.UCom;
import domain.eSystree;
import enums.LayoutHand;
import enums.Type;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;

//Фурнитура
public class FurnitureVar extends Par5s {

    public FurnitureVar(Wincalc winc) {
        super(winc);
    }

    public boolean filter(ElemSimple elem5e, Record furnside1Rec) {

        List<Record> paramList = eFurnpar1.filter(furnside1Rec.getInt(eFurnside1.id));
        if (filterParamDef(paramList) == false) {
            return false;
        }
        //Цикл по параметрам фурнитуры
        for (Record rec : paramList) {
            if (check(elem5e, rec) == false) {
                return false;
            }
        }
        return true;
    }

    public boolean check(ElemSimple elem5e, Record rec) {
        int grup = rec.getInt(GRUP);
        try {
            switch (grup) {

                case 21001:  //Форма контура - "Прямоугольное", "Не прямоугольное", "Не арочное", "Арочное" (Type.AREA - глухарь) 
                    //Прямоугольником называется параллелограмм, у которого все углы прямые.
                    //Трапецией называется четырёхугольник, у которого две стороны параллельны, а две другие — не параллельны
                    //Арка это колич. рёбер > Com5t.MAXSIDE
                    Geometry geom = elem5e.owner.area.getGeometryN(0);
                    Coordinate[] coo = geom.getCoordinates();
                    if ("прямоугольная".equals(rec.getStr(TEXT)) && geom.isRectangle() == false) {
                        return false;
                    } else if ("трапециевидная".equals(rec.getStr(TEXT))) {
                        if (coo.length == 5) {
                            Object b1 = new LineSegment(coo[0], coo[1]).lineIntersection(new LineSegment(coo[2], coo[3]));
                            Object b2 = new LineSegment(coo[1], coo[2]).lineIntersection(new LineSegment(coo[3], coo[4]));
                            if (!((b1 == null && b2 != null) || (b1 != null && b2 == null))) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else if ("арочная".equals(rec.getStr(TEXT)) && coo.length < Com5t.MAXSIDE) {
                        return false;
                    } else if ("не арочная".equals(rec.getStr(TEXT)) && coo.length > Com5t.MAXSIDE) {
                        return false;
                    }
                    break;
                case 21004: //Артикул створки 
                {
                    ElemSimple stv = UCom.filter(winc.listElem, Type.STV_SIDE).get(0);
                    if (stv.artiklRecAn.getStr(eArtikl.code).equals(rec.getStr(TEXT)) == false) {
                        return false;
                    }
                }
                break;
                case 21005: //Артикул заполнения по умолчанию 
                {
                    Record sysreeRec = eSystree.find(winc.nuni); //по умолчанию стеклопакет
                    if (rec.getStr(TEXT).equals(sysreeRec.getStr(eSystree.glas)) == false) {
                        return false;
                    }
                }
                break;
                case 21010: //Ограничение длины стороны, мм 
                    if (UPar.is_21010_21011_21012_21013(rec.getStr(TEXT), elem5e) == false) {
                        return false;
                    }
                    break;
                case 21011: //Ограничение длины ручка константа, мм 
                {
                    AreaStvorka stv = (AreaStvorka) elem5e.owner;
                    if (stv.handLayout == LayoutHand.CONST) {
                        if (UPar.is_21010_21011_21012_21013(rec.getStr(TEXT), elem5e) == false) {
                            return false;
                        }
                    }
                }
                break;
                case 21012: //Ограничение длины ручка вариацион, мм 
                {
                    AreaStvorka stv = (AreaStvorka) elem5e.owner;
                    if (stv.handLayout == LayoutHand.VAR) {
                        if (UPar.is_21010_21011_21012_21013(rec.getStr(TEXT), elem5e) == false) {
                            return false;
                        }
                    }
                }
                break;
                case 21013: //Ограничение длины ручка по середине, мм 
                {
                    AreaStvorka stv = (AreaStvorka) elem5e.owner;
                    if (stv.handLayout == LayoutHand.MIDL) {
                        if (UPar.is_21010_21011_21012_21013(rec.getStr(TEXT), elem5e) == false) {
                            return false;
                        }
                    }
                }
                break;
                case 21016:  //Допустимое соотношение габаритов б/м) 
                    Envelope env = elem5e.owner.area.getGeometryN(0).getEnvelopeInternal();
                    double max = (env.getWidth() > env.getHeight()) ? env.getWidth() : env.getHeight();
                    double min = (env.getWidth() < env.getHeight()) ? env.getWidth() : env.getHeight();
                    if (UCom.containsNumbJust(rec.getStr(TEXT), max / min) == false) {
                        return false;
                    }
                    break;
                case 21037: //Диапазон высоты вариационной ручки, мм 
                {
                    AreaStvorka stv = (AreaStvorka) elem5e.owner;
                    if (stv.handLayout == LayoutHand.VAR) {
                        String[] arr = rec.getStr(TEXT).split("-");
                        if (UCom.getInt(arr[0]) > stv.handHeight || UCom.getInt(arr[1]) < stv.handHeight) {
                            return false;
                        }
                    }
                }
                break;
                case 21040:  //Ограничение угла
                    if (UCom.containsNumbJust(rec.getStr(TEXT), UGeo.anglHor(elem5e.x1(), elem5e.y1(), elem5e.x2(), elem5e.y2())) == false) {
                        return false;
                    }
                    break;
                case 21050:  //Ориентация стороны, ° 
                    if (UCom.containsNumbJust(rec.getStr(TEXT), UGeo.anglHor(elem5e.x1(), elem5e.y1(), elem5e.x2(), elem5e.y2())) == false) {
                        return false;
                    }
                    break;
                case 21085:  //Надпись на эскизе 
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 21088:  //Уравнивание складных створок 
                    message(rec.getInt(GRUP));
                    break;
                default:
                    assert !(grup > 0 && grup < 50000) : "Код " + grup + "  не обработан!!!";
            }
        } catch (Exception e) {
            System.err.println("Ошибка: param.FurnitureVar.check()  parametr=" + grup + "    " + e);
            return false;
        }
        return true;
    }
}
