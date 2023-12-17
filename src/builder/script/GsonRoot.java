package builder.script;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import enums.Type;

public class GsonRoot extends GsonElem {

    public String version = null; //версия
    public Integer prj = null; //PNUMB - номер тестируемого проекта, поле пока нужно только для тестов при сравнении с PS4
    public Integer pid = null; //PNUMB - номер тестируемого проекта, поле пока нужно только для тестов при сравнении с PS4    
    public Integer ord = null; //ONUMB - номер тестируемого заказа, поле пока нужно только для тестов при сравнении с PS4 
    public Integer nuni = null;  //nuni профиля (PRO4_SYSPROF.NUNI)    
    public String name = null;  //название пректа
    public Integer color1 = -3;  //основная текстура
    public Integer color2 = -3;  //внутренняя текстура
    public Integer color3 = -3;  //внешняя текстура       

    public GsonRoot(String version, Type type, String name) {
        this(version, null, null, null, null, type, name, -3, -3, -3);
    }

    public GsonRoot(String version, Integer punic, Integer pnumb, Integer ord, Integer nuni, Type type, String name) {
        this(version, punic, pnumb, ord, nuni, type, name, -3, -3, -3, null);
    }

    public GsonRoot(String version, Integer punic, Integer pnumb, Integer ord, Integer nuni, Type type,
            String name, Integer color1, Integer color2, Integer color3) {
        this(version, punic, pnumb, ord, nuni, type, name, color1, color2, color3, null);
    }

    public GsonRoot(String version, Integer punic, Integer pnumb, Integer ord, Integer nuni, Type type,
            String name, Integer color1, Integer color2, Integer color3, String paramJson) {
        genId = 0;
        this.id = 0;
        this.version = version;
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

    public String toJson() {
        return new GsonBuilder().create().toJson(this);
    }
}
