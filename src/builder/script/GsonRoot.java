package builder.script;

import com.google.gson.GsonBuilder;
import enums.Type;

public class GsonRoot extends GsonElem {

    public static transient double genId = 0;  //идентификатор   
    public String version = null; //версия
    public Integer prj = null; //PNUMB - номер тестируемого проекта, поле пока нужно только для тестов при сравнении с PS4
    public Integer ord = null; //ONUMB - номер тестируемого заказа, поле пока нужно только для тестов при сравнении с PS4 
    public Integer nuni = null;  //nuni профиля (PRO4_SYSPROF.NUNI)    
    public String name = null;  //название пректа
    public Integer color1 = -3;  //основная текстура
    public Integer color2 = -3;  //внутренняя текстура
    public Integer color3 = -3;  //внешняя текстура       

    public GsonRoot(String version, Integer prj, Integer ord, Integer nuni, Type type, String name) {
      this(version, prj, ord, nuni, type, name, -3, -3, -3);
    }
    
    public GsonRoot(String version, Integer prj, Integer ord, Integer nuni, Type type, String name, Integer color1, Integer color2, Integer color3) {
        this.version = version;
        this.prj = prj;
        this.ord = ord;
        this.nuni = nuni;
        this.name = name;
        this.type = type;
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;        
    }

    public String toJson() {
//        this.notSerialize(this);
        return new GsonBuilder().create().toJson(this);
    }    
}
