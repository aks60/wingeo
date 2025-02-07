package builder.script;

import builder.Wincalc;
import builder.model.Com5t;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import enums.Type;
import java.util.ArrayList;
import java.util.List;

public class GsonRoot extends GsonElem {

    public String version = "2.0"; //версия 
    public Integer prj = null; //PNUMB - номер тестируемого проекта, поле пока нужно только для тестов при сравнении с PS4
    public Integer pid = null; //PNUMB - номер тестируемого проекта, поле пока нужно только для тестов при сравнении с PS4    
    public Integer ord = null; //ONUMB - номер тестируемого заказа, поле пока нужно только для тестов при сравнении с PS4 
    public Integer nuni = null;  //nuni профиля (PRO4_SYSPROF.NUNI)    
    public String name = null;  //название пректа
    public Integer color1 = -3;  //основная текстура
    public Integer color2 = -3;  //внутренняя текстура
    public Integer color3 = -3;  //внешняя текстура       

    public GsonRoot(Type type, String name) {
        super();
        GsonElem.gsonId = 0;
        this.id = 0;
        this.name = name;
        this.type = type;
    }

    public GsonRoot(Integer punic, Integer pnumb, Integer ord, Integer nuni, Type type, String name) {
        this(punic, pnumb, ord, nuni, type, name, -3, -3, -3, null);
    }

    public GsonRoot(Integer punic, Integer pnumb, Integer ord, Integer nuni, Type type,
            String name, Integer color1, Integer color2, Integer color3) {
        this(punic, pnumb, ord, nuni, type, name, color1, color2, color3, null);
    }

    public GsonRoot(Integer punic, Integer pnumb, Integer ord, Integer nuni, Type type,
            String name, Integer color1, Integer color2, Integer color3, String paramJson) {
        super();
        GsonElem.gsonId = 0;
        this.id = 0;
        this.prj = pnumb;
        this.pid = punic;
        this.ord = ord;
        this.nuni = nuni;
        this.name = name;
        this.type = type;
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
        if (paramJson != null) {
            this.param = new Gson().fromJson(paramJson, JsonObject.class);
        }
    }

    //Перемещение на канве
    public void translate(GsonElem gson, Double dx, Double dy, Double scale) {
        if (gson.childs != null) {
            Double dX = (dx == 0) ? 0 : dx / scale;
            Double dY = (dy == 0) ? 0 : dy / scale;
            for (GsonElem gs : gson.childs) {
                if (List.of(Type.IMPOST, Type.STOIKA, Type.SHTULP).contains(gs.type)) {
                    if (dX != 0) {
                        gs.x1 += dX;
                        gs.x2 += dX;
                    }
                    if (dY != 0) {
                        gs.y1 += dY;
                        gs.y2 += dY;
                    }
                } else if (List.of(Type.BOX_SIDE, Type.STV_SIDE).contains(gs.type)) {
                    if (dX != 0) {
                        gs.x1 += +dX;
                    }
                    if (dY != 0) {
                        gs.y1 += dY;
                    }
                }
                if (List.of(Type.AREA, Type.STVORKA).contains(gs.type)) {
                    translate(gs, dx, dy, scale);
                }
            }
        }
    }

    public String toJson() {
        this.serialize(this);
        return new GsonBuilder().create().toJson(this);
    }
}
