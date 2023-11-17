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
    public Double width1 = null; //ширина area мм. нижняя
    public Double width2 = null;  //ширина area мм. верхняя
    public Double height1 = null; //высота area мм левая 
    public Double height2 = null;  //высота area мм. правая
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
    
    public Double height() {
        if (height1 == null) {
            return height2;
        } else if (height2 == null) {
            return height1;
        } else if (height1 > height2) {
            return height1;
        } else {
            return height2;
        }
    }

    public Double width() {
        if (width2 == null) {
            return width1;
        } else if (width1 == null) {
            return width2;
        } else if (width2 > width1) {
            return width2;
        } else {
            return width1;
        }
    }   
    
    public String toJson() {
//        this.notSerialize(this);
        return new GsonBuilder().create().toJson(this);
    }    
}
