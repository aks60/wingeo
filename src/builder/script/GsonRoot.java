package builder.script;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.awt.geom.Point2D;
import java.util.List;

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

    public GsonRoot(String version, Integer prj, Integer ord, Integer nuni, String name) {

        this.version = version;
        this.prj = prj;
        this.ord = ord;
        this.nuni = nuni;
        this.name = name;       
    }
    
    public GsonRoot(String version, Integer prj, Integer ord, Integer nuni, String name, Integer color1, Integer color2, Integer color3) {

        this.version = version;
        this.prj = prj;
        this.ord = ord;
        this.nuni = nuni;
        this.name = name;
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;            
    }

    public String toJson() {
//        this.notSerialize(this);
        return new GsonBuilder().create().toJson(this);
    }    
}
