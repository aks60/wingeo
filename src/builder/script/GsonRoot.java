package builder.script;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import enums.Type;
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
        this.name = name;
        this.type = type;
        this.genId = 0;
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
        genId = 0;
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
            dx = (dx == 0) ? 0 : dx / scale;
            dy = (dy == 0) ? 0 : dy / scale;            
            for (GsonElem el : gson.childs) {
                if (List.of(Type.IMPOST, Type.STOIKA, Type.SHTULP).contains(el.type)) {
                    if (dx != 0) {
                        el.x1 += dx;
                        el.x2 += dx;
                    }
                    if (dy != 0) {
                        el.y1 += dy;
                        el.y2 += dy;
                    }
                } else if (List.of(Type.FRAME_SIDE, Type.STVORKA_SIDE).contains(el.type)) {
                    if (dx != 0) {
                        el.x1 = el.x1 + dx;
                    }
                    if (dy != 0) {
                        el.y1 += dy;
                    }
                } if (List.of(Type.AREA, Type.STVORKA).contains(el.type)) {
                    translate(el, dx, dy, scale);
                }
            }
        }
    }

    public String toJson() {
        this.serialize(this);
        return new GsonBuilder().create().toJson(this);
    }
}
